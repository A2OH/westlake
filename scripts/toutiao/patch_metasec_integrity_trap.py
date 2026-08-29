#!/usr/bin/env python3
"""Disable the known metasec 6.5 integrity-failure trap in an extracted DSO.

The target routine intentionally corrupts FP/SP and branches to an unmapped
address.  This diagnostic keeps JNI_OnLoad and all RegisterNatives work intact,
but turns only that failure routine into a return.  Refuse unknown binaries.
"""

from __future__ import annotations

import argparse
import hashlib
import pathlib
import sys


SUPPORTED_MD5 = "915094ffd7a95ceb82265ff99c8a5c12"
TRAP_OFFSET = 0x1B780C
EXPECTED = bytes.fromhex("bf0300f9610580d2a10700f9")
RETURN = bytes.fromhex("c0035fd6")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("input", type=pathlib.Path)
    parser.add_argument("output", type=pathlib.Path)
    args = parser.parse_args()

    image = bytearray(args.input.read_bytes())
    digest = hashlib.md5(image).hexdigest()
    if digest != SUPPORTED_MD5:
        raise ValueError(f"unsupported input md5: {digest}")
    if image[TRAP_OFFSET : TRAP_OFFSET + len(EXPECTED)] != EXPECTED:
        raise ValueError("integrity trap bytes do not match the supported image")
    image[TRAP_OFFSET : TRAP_OFFSET + len(RETURN)] = RETURN
    args.output.write_bytes(image)
    print(f"patched integrity trap at file offset 0x{TRAP_OFFSET:x}")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, ValueError) as exc:
        print(f"error: {exc}", file=sys.stderr)
        raise SystemExit(1)
