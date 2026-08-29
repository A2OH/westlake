#!/usr/bin/env python3
"""Disable ShadowHook's bionic-linker initialization without repacking an APK.

ShadowHook 1.1.1 and newer inline-hooks Android's linker from shadowhook_init().
That implementation cannot inspect OpenHarmony's musl loader and crashes before
it can return an ordinary initialization error.  Keep the library and its JNI
registration intact, but make shadowhook_init() report SHADOWHOOK_ERRNO_DISABLED
(45).  Callers already use this documented error contract to skip optional
instrumentation.

The patch is intentionally ELF-symbol based and size preserving.  The original
APK remains unchanged; apply it only to the extracted compatibility-stage DSO.
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

# mov w0, #45; ret
DISABLED_RETURN = bytes.fromhex("a0058052c0035fd6")


def c_string(data: bytes, offset: int) -> str:
    end = data.find(b"\0", offset)
    if end < 0:
        raise ValueError("unterminated ELF string")
    return data[offset:end].decode("ascii")


def symbol_file_offset(image: bytes, wanted: str) -> tuple[int, int]:
    fields = ELF_HEADER.unpack_from(image)
    ident = fields[0]
    if ident[:6] != b"\x7fELF\x02\x01":
        raise ValueError("input is not a little-endian ELF64 file")

    phoff, shoff = fields[5], fields[6]
    phentsize, phnum = fields[9], fields[10]
    shentsize, shnum = fields[11], fields[12]
    if phentsize != PROGRAM_HEADER.size or shentsize != SECTION_HEADER.size:
        raise ValueError("unexpected ELF header layout")

    loads: list[tuple[int, int, int]] = []
    for index in range(phnum):
        ph = PROGRAM_HEADER.unpack_from(image, phoff + index * phentsize)
        p_type, _, p_offset, p_vaddr, _, p_filesz, _, _ = ph
        if p_type == 1:  # PT_LOAD
            loads.append((p_vaddr, p_offset, p_filesz))

    sections = [
        SECTION_HEADER.unpack_from(image, shoff + index * shentsize)
        for index in range(shnum)
    ]
    for section in sections:
        _, sh_type, _, _, sh_offset, sh_size, sh_link, _, _, sh_entsize = section
        if sh_type != 11:  # SHT_DYNSYM
            continue
        if sh_entsize != SYMBOL.size or sh_link >= len(sections):
            raise ValueError("unexpected dynamic symbol table layout")
        strtab = sections[sh_link]
        strings = image[strtab[4] : strtab[4] + strtab[5]]
        for offset in range(sh_offset, sh_offset + sh_size, sh_entsize):
            st_name, _, _, _, st_value, st_size = SYMBOL.unpack_from(image, offset)
            if c_string(strings, st_name) != wanted:
                continue
            for vaddr, file_offset, file_size in loads:
                if vaddr <= st_value and st_value + len(DISABLED_RETURN) <= vaddr + file_size:
                    return file_offset + st_value - vaddr, st_size
            raise ValueError(f"{wanted} is not backed by a PT_LOAD file range")
    raise ValueError(f"dynamic symbol not found: {wanted}")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("input", type=pathlib.Path)
    parser.add_argument("output", type=pathlib.Path)
    args = parser.parse_args()

    image = bytearray(args.input.read_bytes())
    offset, size = symbol_file_offset(image, "shadowhook_init")
    if size < len(DISABLED_RETURN):
        raise ValueError("shadowhook_init is too small to patch safely")
    if image[offset : offset + len(DISABLED_RETURN)] == DISABLED_RETURN:
        state = "already patched"
    else:
        image[offset : offset + len(DISABLED_RETURN)] = DISABLED_RETURN
        state = "patched"
    args.output.write_bytes(image)
    print(f"{state}: shadowhook_init file offset 0x{offset:x}, size {size}")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, ValueError, struct.error) as exc:
        print(f"error: {exc}", file=sys.stderr)
        raise SystemExit(1)
