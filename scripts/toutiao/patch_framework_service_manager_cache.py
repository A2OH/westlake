#!/usr/bin/env python3
"""Use ArrayMap's concrete virtual get in ServiceManager's cache fast path.

The deployed ServiceManager.sCache object is android.util.ArrayMap.  On this
mixed ART/framework runtime the invoke-interface Map.get call can return null
even though a concrete reflected ArrayMap.get readback returns the live value.
Change only that invoke instruction; the existing null check and raw service
manager fallback remain byte-for-byte unchanged.
"""

from __future__ import annotations

import argparse
import hashlib
import struct
import sys
import zlib
from pathlib import Path


# The longer context uniquely identifies ServiceManager.getService rather than
# its similar checkService cache lookup.
OLD_CONTEXT = bytes.fromhex(
    "62002310"          # sget-object v0, ServiceManager.sCache
    "722006fc3000"      # invoke-interface {v0,v3}, Map.get
    "0c00"              # move-result-object v0
    "1f000903"          # check-cast v0, IBinder
    "38000300"          # if-eqz v0, fallback
    "1100"              # return-object v0
    "7110a1210300"      # invoke-static {v3}, rawGetService
)
NEW_INVOKE = bytes.fromhex(
    "6e2010ce3000"      # invoke-virtual {v0,v3}, ArrayMap.get
)


def patch_service_manager_dex(data: bytes) -> tuple[bytes, int]:
    if data[:8] != b"dex\n039\0":
        raise ValueError(f"expected dex 039, got {data[:8]!r}")
    context_offset = data.find(OLD_CONTEXT)
    if context_offset < 0:
        raise ValueError("target ServiceManager.getService body not found")
    if data.find(OLD_CONTEXT, context_offset + 1) >= 0:
        raise ValueError("target ServiceManager.getService body is not unique")

    patched = bytearray(data)
    invoke_offset = context_offset + 4
    patched[invoke_offset : invoke_offset + len(NEW_INVOKE)] = NEW_INVOKE
    patched[12:32] = hashlib.sha1(patched[32:]).digest()
    struct.pack_into("<I", patched, 8, zlib.adler32(patched[12:]) & 0xFFFFFFFF)
    return bytes(patched), invoke_offset


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("input", type=Path)
    parser.add_argument("output", type=Path)
    args = parser.parse_args()
    patched, offset = patch_service_manager_dex(args.input.read_bytes())
    args.output.write_bytes(patched)
    print(
        f"patched offset=0x{offset:x} size={len(patched)} "
        f"sha256={hashlib.sha256(patched).hexdigest()}"
    )
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, ValueError) as error:
        print(f"error: {error}", file=sys.stderr)
        raise SystemExit(1)
