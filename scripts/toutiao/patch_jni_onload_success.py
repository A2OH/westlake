#!/usr/bin/env python3
"""Diagnostic: replace an ELF64 JNI_OnLoad entry with JNI_VERSION_1_6.

This is deliberately symbol based and size preserving.  It is intended only
for a copied/extracted compatibility-stage DSO, never for the APK itself.
"""

from __future__ import annotations

import argparse
import pathlib
import struct
import sys


ELF_HEADER = struct.Struct("<16sHHIQQQIHHHHHH")
PROGRAM_HEADER = struct.Struct("<IIQQQQQQ")
SECTION_HEADER = struct.Struct("<IIQQQQIIQQ")
SYMBOL = struct.Struct("<IBBHQQ")

# mov w0, #6; movk w0, #1, lsl #16; ret
JNI_1_6_RETURN = bytes.fromhex("c00080522000a072c0035fd6")


def c_string(data: bytes, offset: int) -> str:
    end = data.find(b"\0", offset)
    if end < 0:
        raise ValueError("unterminated ELF string")
    return data[offset:end].decode("ascii")


def symbol_file_offset(image: bytes, wanted: str) -> tuple[int, int]:
    fields = ELF_HEADER.unpack_from(image)
    if fields[0][:6] != b"\x7fELF\x02\x01":
        raise ValueError("input is not a little-endian ELF64 file")

    phoff, shoff = fields[5], fields[6]
    phentsize, phnum = fields[9], fields[10]
    shentsize, shnum = fields[11], fields[12]
    if phentsize != PROGRAM_HEADER.size or shentsize != SECTION_HEADER.size:
        raise ValueError("unexpected ELF table layout")

    loads: list[tuple[int, int, int]] = []
    for index in range(phnum):
        ph = PROGRAM_HEADER.unpack_from(image, phoff + index * phentsize)
        if ph[0] == 1:  # PT_LOAD
            loads.append((ph[3], ph[2], ph[5]))

    sections = [
        SECTION_HEADER.unpack_from(image, shoff + index * shentsize)
        for index in range(shnum)
    ]
    for section in sections:
        _, sh_type, _, _, sh_offset, sh_size, sh_link, _, _, sh_entsize = section
        if sh_type != 11 or sh_link >= len(sections):  # SHT_DYNSYM
            continue
        strings_section = sections[sh_link]
        strings = image[
            strings_section[4] : strings_section[4] + strings_section[5]
        ]
        for offset in range(sh_offset, sh_offset + sh_size, sh_entsize):
            st_name, _, _, _, st_value, st_size = SYMBOL.unpack_from(image, offset)
            if c_string(strings, st_name) != wanted:
                continue
            for vaddr, file_offset, file_size in loads:
                if vaddr <= st_value and st_value + len(JNI_1_6_RETURN) <= vaddr + file_size:
                    return file_offset + st_value - vaddr, st_size
            raise ValueError("JNI_OnLoad is not backed by a PT_LOAD range")
    raise ValueError("dynamic symbol not found: JNI_OnLoad")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("input", type=pathlib.Path)
    parser.add_argument("output", type=pathlib.Path)
    args = parser.parse_args()

    image = bytearray(args.input.read_bytes())
    offset, size = symbol_file_offset(image, "JNI_OnLoad")
    if size < len(JNI_1_6_RETURN):
        raise ValueError("JNI_OnLoad is too small to patch")
    state = "already patched" if image[offset : offset + len(JNI_1_6_RETURN)] == JNI_1_6_RETURN else "patched"
    image[offset : offset + len(JNI_1_6_RETURN)] = JNI_1_6_RETURN
    args.output.write_bytes(image)
    print(f"{state}: JNI_OnLoad file offset 0x{offset:x}, size {size}")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, ValueError, struct.error) as exc:
        print(f"error: {exc}", file=sys.stderr)
        raise SystemExit(1)
