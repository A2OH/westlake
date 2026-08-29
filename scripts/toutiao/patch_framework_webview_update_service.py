#!/usr/bin/env python3
"""Patch WebViewFactory's OHOS update-service handoff without rebuilding dex.

Android's WebViewFactory normally obtains an IBinder from SystemServer and
passes it through IWebViewUpdateService.Stub.asInterface().  The standalone
OHOS runtime instead seeds ServiceManager.sCache with the local adapter object
itself.  On the mixed framework/ART stack the additional asInterface call can
return null even though the cache readback is the correct live adapter.

This patch retains the existing ServiceManager.getService() call, then casts
its result directly to IWebViewUpdateService.  It deliberately changes only
the 22-byte instruction array of getUpdateServiceUnchecked(), preserving all
dex IDs, offsets, and file size.  The dex SHA-1 signature and Adler-32 checksum
are regenerated after the edit.
"""

from __future__ import annotations

import argparse
import hashlib
import struct
import sys
import zlib
from pathlib import Path


TARGET_DESCRIPTOR = b"Landroid/webkit/IWebViewUpdateService;"

# Exact 11-code-unit body from the deployed Android 13 framework classes4.dex:
#   sget-object v0, WEBVIEW_UPDATE_SERVICE_NAME
#   invoke-static {v0}, ServiceManager.getService
#   move-result-object v0
#   invoke-static {v0}, IWebViewUpdateService$Stub.asInterface
#   move-result-object v0
#   return-object v0
OLD_BODY = bytes.fromhex(
    "62007d27"
    "7110dd090000"
    "0c00"
    "7110f5500000"
    "0c00"
    "1100"
)


def read_uleb128(data: bytes, offset: int) -> tuple[int, int]:
    value = 0
    shift = 0
    for _ in range(5):
        byte = data[offset]
        offset += 1
        value |= (byte & 0x7F) << shift
        if not byte & 0x80:
            return value, offset
        shift += 7
    raise ValueError("invalid ULEB128 value")


def dex_string(data: bytes, string_ids_off: int, index: int) -> bytes:
    data_off = struct.unpack_from("<I", data, string_ids_off + index * 4)[0]
    _, payload_off = read_uleb128(data, data_off)
    end = data.index(0, payload_off)
    return data[payload_off:end]


def find_type_index(data: bytes, descriptor: bytes) -> int:
    string_ids_size, string_ids_off = struct.unpack_from("<II", data, 0x38)
    type_ids_size, type_ids_off = struct.unpack_from("<II", data, 0x40)
    for type_index in range(type_ids_size):
        string_index = struct.unpack_from("<I", data, type_ids_off + type_index * 4)[0]
        if string_index >= string_ids_size:
            raise ValueError(f"invalid descriptor string index {string_index}")
        if dex_string(data, string_ids_off, string_index) == descriptor:
            return type_index
    raise ValueError(f"type descriptor not found: {descriptor.decode()}")


def patch_dex(data: bytes) -> tuple[bytes, int, int]:
    if data[:8] != b"dex\n039\0":
        raise ValueError(f"expected dex 039, got {data[:8]!r}")

    body_offset = data.find(OLD_BODY)
    if body_offset < 0:
        raise ValueError("target WebViewFactory instruction body not found")
    if data.find(OLD_BODY, body_offset + 1) >= 0:
        raise ValueError("target instruction body is not unique")

    interface_type = find_type_index(data, TARGET_DESCRIPTOR)
    if interface_type > 0xFFFF:
        raise ValueError(f"type index does not fit check-cast: {interface_type}")

    # Preserve the existing sget/invoke/move-result (12 bytes), replace the
    # Stub.asInterface invoke with check-cast, and pad the fixed-size body with
    # two Dalvik nop instructions.
    new_body = (
        OLD_BODY[:12]
        + b"\x1f\x00"
        + struct.pack("<H", interface_type)
        + b"\x11\x00"
        + b"\x00\x00\x00\x00"
    )
    if len(new_body) != len(OLD_BODY):
        raise AssertionError("replacement changed the code-item size")

    patched = bytearray(data)
    patched[body_offset : body_offset + len(OLD_BODY)] = new_body
    patched[12:32] = hashlib.sha1(patched[32:]).digest()
    struct.pack_into("<I", patched, 8, zlib.adler32(patched[12:]) & 0xFFFFFFFF)
    return bytes(patched), body_offset, interface_type


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("input", type=Path, help="original classes4.dex")
    parser.add_argument("output", type=Path, help="patched classes4.dex")
    args = parser.parse_args()

    original = args.input.read_bytes()
    patched, body_offset, type_index = patch_dex(original)
    args.output.write_bytes(patched)
    print(
        f"patched offset=0x{body_offset:x} type_idx=0x{type_index:04x} "
        f"size={len(patched)} sha256={hashlib.sha256(patched).hexdigest()}"
    )
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, ValueError) as error:
        print(f"error: {error}", file=sys.stderr)
        raise SystemExit(1)
