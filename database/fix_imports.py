#!/usr/bin/env python3
"""Fix generated shim files by replacing unresolvable simple type names with Object."""

import os
import re
import subprocess
import sys

SHIM_ROOT = os.path.join(os.path.dirname(__file__), "..", "shim", "java")

# Common Android types that exist as shims - map simple name to FQN
KNOWN_TYPES = {}

def build_known_types():
    """Scan shim/java for all .java files and build a map of simple name -> package."""
    for root, dirs, files in os.walk(SHIM_ROOT):
        for f in files:
            if f.endswith(".java"):
                name = f[:-5]  # strip .java
                rel = os.path.relpath(root, SHIM_ROOT)
                pkg = rel.replace(os.sep, ".")
                KNOWN_TYPES[name] = pkg

def get_package(filepath):
    """Extract package from a Java file."""
    with open(filepath) as f:
        for line in f:
            m = re.match(r'package\s+([\w.]+)\s*;', line)
            if m:
                return m.group(1)
    return ""

def fix_file(filepath):
    """Add missing imports to a generated file."""
    with open(filepath) as f:
        content = f.read()

    pkg = get_package(filepath)
    if not pkg:
        return False

    # Find all simple type names used (word followed by space or < or [])
    # that could be unresolved types
    # Look for types in extends, implements, method params, return types, field types

    # Collect types that need importing
    needed_imports = set()

    # Find words that look like type references (capitalized, used in type positions)
    # This is a rough heuristic
    type_pattern = re.compile(r'\b([A-Z]\w+)\b')

    for match in type_pattern.finditer(content):
        name = match.group(1)
        if name in KNOWN_TYPES:
            type_pkg = KNOWN_TYPES[name]
            if type_pkg != pkg and type_pkg != "java.lang":
                # Check if it's a java.* type
                if type_pkg.startswith("java.") or type_pkg.startswith("javax."):
                    needed_imports.add(f"{type_pkg}.{name}")
                elif type_pkg.startswith("android.") or type_pkg.startswith("dalvik."):
                    needed_imports.add(f"{type_pkg}.{name}")

    if not needed_imports:
        return False

    # Add imports after package line
    import_block = "\n".join(f"import {imp};" for imp in sorted(needed_imports))
    content = re.sub(
        r'(package\s+[\w.]+\s*;)\n',
        r'\1\n' + import_block + '\n',
        content,
        count=1
    )

    with open(filepath, 'w') as f:
        f.write(content)

    return True


def main():
    build_known_types()
    print(f"Known types: {len(KNOWN_TYPES)}")

    fixed = 0
    for root, dirs, files in os.walk(SHIM_ROOT):
        for f in files:
            if f.endswith(".java"):
                filepath = os.path.join(root, f)
                if fix_file(filepath):
                    fixed += 1

    print(f"Fixed {fixed} files")


if __name__ == "__main__":
    main()
