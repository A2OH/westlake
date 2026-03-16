#!/usr/bin/env python3
"""Second pass: add java.* imports for common JDK types."""

import os
import re

SHIM_ROOT = os.path.join(os.path.dirname(__file__), "..", "shim", "java")

JAVA_TYPES = {
    "List": "java.util.List",
    "ArrayList": "java.util.ArrayList",
    "Map": "java.util.Map",
    "HashMap": "java.util.HashMap",
    "Set": "java.util.Set",
    "HashSet": "java.util.HashSet",
    "Collection": "java.util.Collection",
    "Collections": "java.util.Collections",
    "Iterator": "java.util.Iterator",
    "Locale": "java.util.Locale",
    "Date": "java.util.Date",
    "UUID": "java.util.UUID",
    "Consumer": "java.util.function.Consumer",
    "Executor": "java.util.concurrent.Executor",
    "ByteBuffer": "java.nio.ByteBuffer",
    "Closeable": "java.io.Closeable",
    "InputStream": "java.io.InputStream",
    "OutputStream": "java.io.OutputStream",
    "File": "java.io.File",
    "FileDescriptor": "java.io.FileDescriptor",
    "PrintWriter": "java.io.PrintWriter",
    "IOException": "java.io.IOException",
    "Serializable": "java.io.Serializable",
    "Charset": "java.nio.charset.Charset",
    "Pattern": "java.util.regex.Pattern",
    "CharBuffer": "java.nio.CharBuffer",
    "InetAddress": "java.net.InetAddress",
    "InetSocketAddress": "java.net.InetSocketAddress",
    "Socket": "java.net.Socket",
    "URI": "java.net.URI",
    "URL": "java.net.URL",
    "Observable": "java.util.Observable",
    "Observer": "java.util.Observer",
    "Comparable": "java.lang.Comparable",
}

def get_package(filepath):
    with open(filepath) as f:
        for line in f:
            m = re.match(r'package\s+([\w.]+)\s*;', line)
            if m:
                return m.group(1)
    return ""

def fix_file(filepath):
    with open(filepath) as f:
        content = f.read()

    pkg = get_package(filepath)

    # Find existing imports
    existing_imports = set(re.findall(r'import\s+([\w.]+)\s*;', content))

    needed = set()
    for simple, fqn in JAVA_TYPES.items():
        if fqn in existing_imports:
            continue
        # Check if the simple name is used in a type position
        if re.search(r'\b' + simple + r'\b', content):
            needed.add(fqn)

    if not needed:
        return False

    import_block = "\n".join(f"import {imp};" for imp in sorted(needed))

    # Add after existing imports or after package
    if 'import ' in content:
        # Add before first non-import line after imports
        content = re.sub(
            r'(import\s+[\w.]+\s*;\n)(?!import)',
            r'\1' + import_block + '\n',
            content,
            count=1
        )
    else:
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
    fixed = 0
    for root, dirs, files in os.walk(SHIM_ROOT):
        for f in files:
            if f.endswith(".java"):
                if fix_file(os.path.join(root, f)):
                    fixed += 1
    print(f"Fixed {fixed} files with java.* imports")

if __name__ == "__main__":
    main()
