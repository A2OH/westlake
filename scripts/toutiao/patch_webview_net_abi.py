#!/usr/bin/env python3
"""Redirect Android WebView libc-ABI imports to the boundary shim.

The replacements are deliberately equal length, so this updates only dynamic
string-table entries and does not move any ELF offsets.
"""

from __future__ import annotations

import argparse
import pathlib
import struct


ELF64_HEADER = struct.Struct("<16sHHIQQQIHHHHHH")
ELF64_SECTION = struct.Struct("<IIQQQQIIQQ")
REPLACEMENTS = {
    b"getaddrinfo\0": b"wl_getai_oh\0",
    b"freeaddrinfo\0": b"wl_freeai_oh\0",
    b"connect\0": b"wl_conn\0",
    b"sigaction\0": b"wl_sigact\0",
}


def section_headers(data: bytes) -> tuple[list[tuple[int, ...]], int]:
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
    return sections, names_index


def find_dynstr(data: bytes) -> tuple[int, int]:
    sections, names_index = section_headers(data)
    names = sections[names_index]
    names_blob = data[names[4] : names[4] + names[5]]
    for section in sections:
        name_offset = section[0]
        name_end = names_blob.find(b"\0", name_offset)
        name = names_blob[name_offset:name_end]
        if name == b".dynstr":
            return section[4], section[5]
    raise ValueError("ELF has no .dynstr section")


def patch(source: pathlib.Path, output: pathlib.Path) -> None:
    data = bytearray(source.read_bytes())
    dynstr_offset, dynstr_size = find_dynstr(data)
    dynstr = bytes(data[dynstr_offset : dynstr_offset + dynstr_size])
    for old, new in REPLACEMENTS.items():
        if len(old) != len(new):
            raise AssertionError(f"replacement changes size: {old!r} -> {new!r}")
        count = dynstr.count(old)
        if count != 1:
            raise ValueError(f"expected one {old[:-1].decode()} import, found {count}")
        relative = dynstr.index(old)
        absolute = dynstr_offset + relative
        data[absolute : absolute + len(old)] = new
        dynstr = dynstr.replace(old, new, 1)
    output.write_bytes(data)


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("source", type=pathlib.Path)
    parser.add_argument("output", type=pathlib.Path)
    args = parser.parse_args()
    patch(args.source, args.output)


if __name__ == "__main__":
    main()
