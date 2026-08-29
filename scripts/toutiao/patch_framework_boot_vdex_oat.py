#!/usr/bin/env python3
"""Apply the WebView update-service dex fix to a framework VDEX/OAT pair.

This is safe only for a verify-only framework OAT whose ELF .text section is
empty.  In that layout ART interprets the dex embedded in boot-framework.vdex;
the OAT stores dex locations and jar-entry location checksums but no compiled
method bodies.  Those location checksums deliberately remain unchanged: the
deployed hybrid image already pairs this VDEX identity with a newer framework
jar, while the patched embedded dex regenerates its own SHA-1 and Adler fields.
"""

from __future__ import annotations

import argparse
import struct
import sys
from pathlib import Path

from patch_framework_service_manager_cache import patch_service_manager_dex
from patch_framework_webview_update_service import patch_dex


def require_empty_elf_text(data: bytes) -> None:
    if data[:4] != b"\x7fELF" or data[4] != 2 or data[5] != 1:
        raise ValueError("expected little-endian ELF64 OAT")
    section_off = struct.unpack_from("<Q", data, 0x28)[0]
    section_size = struct.unpack_from("<H", data, 0x3A)[0]
    section_count = struct.unpack_from("<H", data, 0x3C)[0]
    names_index = struct.unpack_from("<H", data, 0x3E)[0]
    if not (section_off and section_size >= 64 and names_index < section_count):
        raise ValueError("invalid ELF section table")

    names_header = section_off + names_index * section_size
    names_off = struct.unpack_from("<Q", data, names_header + 0x18)[0]
    names_size = struct.unpack_from("<Q", data, names_header + 0x20)[0]
    names = data[names_off : names_off + names_size]
    for index in range(section_count):
        header = section_off + index * section_size
        name_off = struct.unpack_from("<I", data, header)[0]
        end = names.find(b"\0", name_off)
        name = names[name_off:end] if end >= 0 else b""
        if name == b".text":
            text_size = struct.unpack_from("<Q", data, header + 0x20)[0]
            if text_size != 0:
                raise ValueError(
                    f"refusing to patch OAT with compiled code (.text={text_size})"
                )
            return
    raise ValueError("ELF .text section not found")


def patch_pair(vdex_data: bytes, oat_data: bytes) -> tuple[bytes, bytes, list[str]]:
    require_empty_elf_text(oat_data)
    if vdex_data[:12] != b"vdex027\0\x04\0\0\0":
        raise ValueError("expected four-section VDEX 027")

    sections = [struct.unpack_from("<III", vdex_data, 12 + index * 12)
                for index in range(4)]
    checksum_kind, checksum_off, checksum_size = sections[0]
    dex_kind, dex_off, dex_size = sections[1]
    if checksum_kind != 0 or dex_kind != 1 or checksum_size % 4:
        raise ValueError("invalid VDEX checksum/dex sections")
    dex_count = checksum_size // 4

    dex_cursor = dex_off
    dex_end = dex_off + dex_size
    patched_ranges: list[tuple[int, bytes, int]] = []
    details: list[tuple[str, int, int, int]] = []
    for dex_index in range(dex_count):
        if vdex_data[dex_cursor : dex_cursor + 8] != b"dex\n039\0":
            raise ValueError(f"dex {dex_index} missing at 0x{dex_cursor:x}")
        file_size = struct.unpack_from("<I", vdex_data, dex_cursor + 0x20)[0]
        original = vdex_data[dex_cursor : dex_cursor + file_size]
        patched = original
        try:
            patched, body_offset, _ = patch_dex(patched)
        except ValueError as error:
            if "not found" not in str(error):
                raise
        else:
            details.append(("webview", dex_index, dex_cursor + body_offset, 0))
        try:
            patched, invoke_offset = patch_service_manager_dex(patched)
        except ValueError as error:
            if "not found" not in str(error):
                raise
        else:
            details.append(("service-manager", dex_index, dex_cursor + invoke_offset, 0))
        if patched != original:
            patched_ranges.append((dex_cursor, patched, dex_index))
        dex_cursor = (dex_cursor + file_size + 3) & ~3
    if dex_cursor != dex_end:
        raise ValueError(f"VDEX dex section ends at 0x{dex_end:x}, parsed 0x{dex_cursor:x}")
    labels = {detail[0] for detail in details}
    if labels != {"webview", "service-manager"} or len(details) != 2:
        raise ValueError(f"expected both target methods once, found {details}")

    patched_vdex = bytearray(vdex_data)
    printable: list[str] = []
    for target_off, patched_dex, dex_index in patched_ranges:
        patched_vdex[target_off : target_off + len(patched_dex)] = patched_dex
        location_checksum = struct.unpack_from(
            "<I", vdex_data, checksum_off + dex_index * 4
        )[0]
        location_bytes = struct.pack("<I", location_checksum)
        oat_checksum_off = oat_data.find(location_bytes)
        if oat_checksum_off < 0 or oat_data.find(location_bytes, oat_checksum_off + 1) >= 0:
            raise ValueError("expected exactly one VDEX location checksum in OAT")
        printable.append(
            f"dex_index={dex_index} oat_checksum=0x{oat_checksum_off:x}"
        )
    for label, dex_index, absolute_offset, _ in details:
        printable.append(f"{label}=dex{dex_index}@0x{absolute_offset:x}")
    return bytes(patched_vdex), oat_data, printable


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("input_vdex", type=Path)
    parser.add_argument("input_oat", type=Path)
    parser.add_argument("output_vdex", type=Path)
    parser.add_argument("output_oat", type=Path)
    args = parser.parse_args()

    vdex, oat, details = patch_pair(
        args.input_vdex.read_bytes(), args.input_oat.read_bytes()
    )
    args.output_vdex.write_bytes(vdex)
    args.output_oat.write_bytes(oat)
    print("patched " + " ".join(details))
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, ValueError) as error:
        print(f"error: {error}", file=sys.stderr)
        raise SystemExit(1)
