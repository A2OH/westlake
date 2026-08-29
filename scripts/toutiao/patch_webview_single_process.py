#!/usr/bin/env python3
"""Pin Chromium WebView to the OH adapter's single-process mode.

The bridge's IWebViewUpdateService implementation reports that multiprocess
WebView is disabled.  Chromium asks the final framework WebViewDelegate for
the same value during provider construction.  On the standalone OH process,
that delegate can lose the locally cached service at the Binder interface
boundary and dereference null before Chromium finishes initializing.

Patch Chromium's one-instruction delegate thunk (class ``LOB;`` in this
provider build) to return false directly.  The replacement is the same size
as the original instruction array, so dex offsets and APK resources remain
unchanged.  Dex signature and checksum fields are regenerated.
"""

from __future__ import annotations

import argparse
import hashlib
import struct
import sys
import zlib
from pathlib import Path


TARGET_CLASS = b"LOB;"
TARGET_NAME = b"a"
TARGET_RETURN = b"Z"
TARGET_PARAMETER = b"Landroid/webkit/WebViewDelegate;"


def read_uleb128(data: bytes, offset: int) -> tuple[int, int]:
    value = 0
    shift = 0
    for _ in range(5):
        byte = data[offset]
        offset += 1
        value |= (byte & 0x7f) << shift
        if not byte & 0x80:
            return value, offset
        shift += 7
    raise ValueError("invalid ULEB128 value")


def dex_string(data: bytes, index: int) -> bytes:
    string_count, string_ids_off = struct.unpack_from("<II", data, 0x38)
    if index >= string_count:
        raise ValueError(f"string index out of range: {index}")
    data_off = struct.unpack_from("<I", data, string_ids_off + index * 4)[0]
    _, payload_off = read_uleb128(data, data_off)
    return data[payload_off : data.index(0, payload_off)]


def type_descriptor(data: bytes, type_index: int) -> bytes:
    type_count, type_ids_off = struct.unpack_from("<II", data, 0x40)
    if type_index >= type_count:
        raise ValueError(f"type index out of range: {type_index}")
    string_index = struct.unpack_from("<I", data, type_ids_off + type_index * 4)[0]
    return dex_string(data, string_index)


def proto_matches(data: bytes, proto_index: int) -> bool:
    proto_count, proto_ids_off = struct.unpack_from("<II", data, 0x48)
    if proto_index >= proto_count:
        raise ValueError(f"proto index out of range: {proto_index}")
    _, return_type, parameters_off = struct.unpack_from(
        "<III", data, proto_ids_off + proto_index * 12
    )
    if type_descriptor(data, return_type) != TARGET_RETURN or not parameters_off:
        return False
    parameter_count = struct.unpack_from("<I", data, parameters_off)[0]
    if parameter_count != 1:
        return False
    parameter_type = struct.unpack_from("<H", data, parameters_off + 4)[0]
    return type_descriptor(data, parameter_type) == TARGET_PARAMETER


def target_method_index(data: bytes) -> tuple[int, int]:
    method_count, method_ids_off = struct.unpack_from("<II", data, 0x58)
    matches: list[tuple[int, int]] = []
    for method_index in range(method_count):
        class_index, proto_index, name_index = struct.unpack_from(
            "<HHI", data, method_ids_off + method_index * 8
        )
        if (
            type_descriptor(data, class_index) == TARGET_CLASS
            and dex_string(data, name_index) == TARGET_NAME
            and proto_matches(data, proto_index)
        ):
            matches.append((method_index, class_index))
    if len(matches) != 1:
        raise ValueError(f"expected one OB delegate thunk, found {matches}")
    return matches[0]


def class_data_offset(data: bytes, target_class_index: int) -> int:
    class_count, class_defs_off = struct.unpack_from("<II", data, 0x60)
    matches = []
    for class_def_index in range(class_count):
        class_index = struct.unpack_from(
            "<I", data, class_defs_off + class_def_index * 32
        )[0]
        if class_index == target_class_index:
            matches.append(
                struct.unpack_from(
                    "<I", data, class_defs_off + class_def_index * 32 + 24
                )[0]
            )
    if len(matches) != 1 or not matches[0]:
        raise ValueError(f"expected one concrete OB class, found {matches}")
    return matches[0]


def target_code_offset(data: bytes, class_data_off: int, target_method: int) -> int:
    cursor = class_data_off
    sizes = []
    for _ in range(4):
        value, cursor = read_uleb128(data, cursor)
        sizes.append(value)
    static_fields, instance_fields, direct_methods, virtual_methods = sizes
    for _ in range(static_fields + instance_fields):
        _, cursor = read_uleb128(data, cursor)
        _, cursor = read_uleb128(data, cursor)

    matches = []
    for method_count in (direct_methods, virtual_methods):
        method_index = 0
        for _ in range(method_count):
            delta, cursor = read_uleb128(data, cursor)
            method_index += delta
            _, cursor = read_uleb128(data, cursor)
            code_off, cursor = read_uleb128(data, cursor)
            if method_index == target_method:
                matches.append(code_off)
    if len(matches) != 1 or not matches[0]:
        raise ValueError(f"expected one code item for target method, found {matches}")
    return matches[0]


def patch_dex(data: bytes) -> tuple[bytes, int]:
    if data[:8] not in (b"dex\n035\0", b"dex\n039\0"):
        raise ValueError(f"expected dex 035 or 039, got {data[:8]!r}")
    method_index, class_index = target_method_index(data)
    code_off = target_code_offset(data, class_data_offset(data, class_index), method_index)
    registers, ins, outs, tries = struct.unpack_from("<HHHH", data, code_off)
    instruction_count = struct.unpack_from("<I", data, code_off + 12)[0]
    instructions_off = code_off + 16
    body = data[instructions_off : instructions_off + instruction_count * 2]
    if (registers, ins, outs, tries, instruction_count) != (1, 1, 1, 0, 5):
        raise ValueError(
            "unexpected OB code item shape: "
            f"registers={registers} ins={ins} outs={outs} tries={tries} "
            f"instructions={instruction_count}"
        )
    if not (body[0] == 0x6e and body[6:8] == b"\x0a\x00" and body[8:10] == b"\x0f\x00"):
        raise ValueError(f"unexpected OB instruction body: {body.hex()}")

    # const/4 v0, #0; return v0; nop; nop; nop
    replacement = bytes.fromhex("12000f00000000000000")
    patched = bytearray(data)
    patched[instructions_off : instructions_off + len(replacement)] = replacement
    patched[12:32] = hashlib.sha1(patched[32:]).digest()
    struct.pack_into("<I", patched, 8, zlib.adler32(patched[12:]) & 0xffffffff)
    return bytes(patched), instructions_off


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("input", type=Path)
    parser.add_argument("output", type=Path)
    args = parser.parse_args()
    patched, offset = patch_dex(args.input.read_bytes())
    args.output.write_bytes(patched)
    print(
        f"patched offset=0x{offset:x} size={len(patched)} "
        f"sha256={hashlib.sha256(patched).hexdigest()}"
    )
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, ValueError, IndexError, struct.error) as error:
        print(f"error: {error}", file=sys.stderr)
        raise SystemExit(1)
