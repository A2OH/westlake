#!/usr/bin/env python3
"""Route WebView's libjpeg longjmp through the OH boundary diagnostic.

The replacement has exactly the same length as the original dynamic-string
entry, so no ELF offsets move.  setjmp remains bound directly to OH musl; the
diagnostic only observes and forwards libjpeg's error transfer.
"""

from __future__ import annotations

import argparse
import pathlib
import struct


ELF64_HEADER = struct.Struct("<16sHHIQQQIHHHHHH")
ELF64_SECTION = struct.Struct("<IIQQQQIIQQ")
ORIGINAL = b"longjmp\0"
REPLACEMENT = b"wl_ljmp\0"


def find_dynstr(data: bytes) -> tuple[int, int]:
    if data[:4] != b"\x7fELF" or data[4] != 2 or data[5] != 1:
        raise ValueError("expected a little-endian ELF64 file")
    header = ELF64_HEADER.unpack_from(data)
    section_offset = header[6]
    section_size = header[11]
    section_count = header[12]
    names_index = header[13]
    if section_size != ELF64_SECTION.size:
        raise ValueError(f"unexpected section-header size {section_size}")
    sections = [
        ELF64_SECTION.unpack_from(data, section_offset + index * section_size)
        for index in range(section_count)
    ]
    names = sections[names_index]
    names_blob = data[names[4] : names[4] + names[5]]
    for section in sections:
        name_offset = section[0]
        name_end = names_blob.find(b"\0", name_offset)
        if names_blob[name_offset:name_end] == b".dynstr":
            return section[4], section[5]
    raise ValueError("ELF has no .dynstr section")


def patch(source: pathlib.Path, output: pathlib.Path) -> None:
    if len(ORIGINAL) != len(REPLACEMENT):
        raise AssertionError("replacement changes dynamic-string size")
    data = bytearray(source.read_bytes())
    dynstr_offset, dynstr_size = find_dynstr(data)
    dynstr = bytes(data[dynstr_offset : dynstr_offset + dynstr_size])
    count = dynstr.count(ORIGINAL)
    if count != 1:
        raise ValueError(f"expected one longjmp import, found {count}")
    absolute = dynstr_offset + dynstr.index(ORIGINAL)
    data[absolute : absolute + len(ORIGINAL)] = REPLACEMENT
    output.write_bytes(data)


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("source", type=pathlib.Path)
    parser.add_argument("output", type=pathlib.Path)
    args = parser.parse_args()
    patch(args.source, args.output)


if __name__ == "__main__":
    main()
