#!/usr/bin/env python3
"""Iteratively fix compile errors by replacing unknown types with Object."""

import os
import re
import subprocess
import sys

SHIM_ROOT = os.path.join(os.path.dirname(__file__), "..", "shim", "java")


def get_java_files():
    files = []
    for root, dirs, fnames in os.walk(SHIM_ROOT):
        for f in fnames:
            if f.endswith(".java"):
                files.append(os.path.join(root, f))
    return files


def compile_and_get_errors():
    """Run javac and return (error_files, missing_symbols)."""
    files = get_java_files()
    result = subprocess.run(
        ["javac", "-sourcepath", SHIM_ROOT, "-d", "/tmp/shim-fix"] + files,
        capture_output=True, text=True, timeout=300
    )
    stderr = result.stderr

    # Parse error file -> line -> error
    error_files = set()
    missing_symbols = set()
    repeated_mod_files = set()
    other_error_files = set()

    for line in stderr.split('\n'):
        if ': error:' in line:
            filepath = line.split(':')[0]
            error_files.add(filepath)

            if 'cannot find symbol' in line:
                pass  # symbol on next lines
            elif 'repeated modifier' in line:
                repeated_mod_files.add(filepath)
            elif "illegal start of type" in line or "';' expected" in line:
                other_error_files.add(filepath)

        m = re.match(r'\s*symbol:\s*class\s+(\w+)', line)
        if m:
            missing_symbols.add(m.group(1))

    return error_files, missing_symbols, repeated_mod_files, other_error_files


def fix_repeated_modifiers(filepath):
    """Fix 'static static' and similar in a file."""
    with open(filepath) as f:
        content = f.read()
    content = content.replace('static static', 'static')
    content = content.replace('public public', 'public')
    content = content.replace('final final', 'final')
    with open(filepath, 'w') as f:
        f.write(content)


def replace_types_with_object(filepath, types_to_replace):
    """Replace unknown type names with Object in a file."""
    with open(filepath) as f:
        content = f.read()

    changed = False
    for tname in types_to_replace:
        pattern = re.compile(r'(?<![.\w])' + re.escape(tname) + r'(?!\w)')
        lines = content.split('\n')
        new_lines = []
        for line in lines:
            if line.strip().startswith('package ') or line.strip().startswith('import '):
                new_lines.append(line)
            else:
                new_line = pattern.sub('Object', line)
                if new_line != line:
                    changed = True
                new_lines.append(new_line)
        content = '\n'.join(new_lines)

    if changed:
        with open(filepath, 'w') as f:
            f.write(content)
    return changed


def fix_syntax_errors(filepath):
    """Try to fix common syntax errors like annotation params."""
    with open(filepath) as f:
        content = f.read()

    orig = content
    # Fix annotation-value params: (from=0) int -> int, (to=255) int -> int
    content = re.sub(r'\([^)]*=[^)]*\)\s*', '', content)
    # Fix <?> return types
    content = re.sub(r'public <\?> ', 'public Object ', content)
    # Fix return null for primitives
    for prim, val in [('int', '0'), ('long', '0L'), ('float', '0f'), ('double', '0.0'),
                       ('boolean', 'false'), ('byte', '0'), ('short', '0'), ('char', "'\\0'")]:
        content = re.sub(
            rf'(public\s+(?:static\s+)?{prim}\s+\w+\([^)]*\))\s*\{{\s*return null;\s*\}}',
            rf'\1 {{ return {val}; }}',
            content
        )

    if content != orig:
        with open(filepath, 'w') as f:
            f.write(content)
        return True
    return False


def main():
    os.makedirs("/tmp/shim-fix", exist_ok=True)

    for iteration in range(20):
        error_files, missing_symbols, repeated_files, syntax_files = compile_and_get_errors()

        if not error_files:
            print(f"Clean compile after {iteration} iterations!")
            break

        print(f"Iteration {iteration}: {len(error_files)} error files, "
              f"{len(missing_symbols)} missing symbols")

        fixed = 0

        # Fix repeated modifiers
        for f in repeated_files:
            fix_repeated_modifiers(f)
            fixed += 1

        # Fix syntax errors
        for f in syntax_files:
            if fix_syntax_errors(f):
                fixed += 1

        # Replace missing symbols with Object in all files that reference them
        if missing_symbols:
            for f in get_java_files():
                if replace_types_with_object(f, missing_symbols):
                    fixed += 1

        if fixed == 0:
            print("No fixes applied, stopping")
            # Print remaining errors
            result = subprocess.run(
                ["javac", "-sourcepath", SHIM_ROOT, "-d", "/tmp/shim-fix"] + get_java_files(),
                capture_output=True, text=True, timeout=300
            )
            for line in result.stderr.split('\n'):
                if 'error:' in line:
                    print(line)
            break

    total = len(get_java_files())
    print(f"Total Java files: {total}")


if __name__ == "__main__":
    main()
