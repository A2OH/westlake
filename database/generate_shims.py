#!/usr/bin/env python3
"""Generate Java shim stubs from the api_compat.db database.

Only generates files for classes that don't already have a .java file.
Skips java.*, javax.*, org.* (provided by JDK).
"""

import sqlite3
import os
import re
import sys

DB_PATH = os.path.join(os.path.dirname(__file__), "api_compat.db")
SHIM_ROOT = os.path.join(os.path.dirname(__file__), "..", "shim", "java")

# Packages to skip (provided by JDK or not needed)
SKIP_PREFIXES = ("java.", "javax.", "org.", "sun.", "com.sun.")


def simple_name(fqn):
    """Extract simple class name from fully qualified name."""
    if not fqn:
        return None
    # Handle generics like Foo<Bar>
    fqn = re.sub(r'<.*>', '', fqn)
    # Handle arrays
    fqn = fqn.replace("[]", "")
    return fqn.rsplit(".", 1)[-1] if "." in fqn else fqn


def is_android_type(fqn):
    """Check if a type is an android/dalvik type (needs import)."""
    if not fqn:
        return False
    fqn = re.sub(r'<.*>', '', fqn).replace("[]", "").strip()
    if fqn in ("void", "int", "long", "float", "double", "boolean", "byte",
               "short", "char", "Object", "String", "Class"):
        return False
    return fqn.startswith("android.") or fqn.startswith("dalvik.")


def parse_signature(sig):
    """Parse a method signature like 'void foo(int, String)' into components."""
    if not sig:
        return None, None, []

    # Remove annotations like @NonNull, @Nullable, @IdRes, etc.
    sig = re.sub(r'@\w+(\.\w+)*\s*', '', sig)
    # Remove 'final', 'synchronized', 'native', 'strictfp'
    sig = re.sub(r'\b(final|synchronized|native|strictfp|static|abstract|default|public|protected|private)\b\s*', '', sig)

    m = re.match(r'(.*?)\s+(\w+)\s*\((.*)\)', sig.strip())
    if not m:
        return None, None, []

    ret_type = m.group(1).strip()
    method_name = m.group(2).strip()
    params_str = m.group(3).strip()

    params = []
    if params_str:
        # Split params carefully (handle generics with commas)
        depth = 0
        current = []
        for ch in params_str:
            if ch in '<':
                depth += 1
            elif ch in '>':
                depth -= 1
            elif ch == ',' and depth == 0:
                params.append(''.join(current).strip())
                current = []
                continue
            current.append(ch)
        if current:
            params.append(''.join(current).strip())

    return ret_type, method_name, params


def default_return(ret_type):
    """Return a default value expression for the given return type."""
    if not ret_type or ret_type == "void":
        return None
    rt = ret_type.strip()
    # Handle generics
    base = re.sub(r'<.*>', '', rt).replace("[]", "").strip()
    if base in ("int", "byte", "short"):
        return "0"
    if base == "long":
        return "0L"
    if base == "float":
        return "0f"
    if base == "double":
        return "0.0"
    if base == "boolean":
        return "false"
    if base == "char":
        return "'\\0'"
    return "null"


def simplify_type(fqn):
    """Convert a fully-qualified type to simple name for use in method bodies."""
    if not fqn:
        return fqn
    # Don't simplify primitives
    if fqn in ("void", "int", "long", "float", "double", "boolean", "byte",
               "short", "char"):
        return fqn

    # Handle arrays
    arr_suffix = ""
    while fqn.endswith("[]"):
        arr_suffix += "[]"
        fqn = fqn[:-2]

    # Handle generics: simplify outer and inner
    generic_match = re.match(r'(.*?)<(.*)>', fqn)
    if generic_match:
        outer = simplify_type(generic_match.group(1))
        # For inner, just use ? to avoid complex resolution
        return outer + "<?>" + arr_suffix

    # Simple name
    name = fqn.rsplit(".", 1)[-1] if "." in fqn else fqn
    return name + arr_suffix


def format_param(param_str, idx):
    """Format a parameter declaration. param_str is like 'android.content.Intent' or 'int'."""
    # Remove annotations
    param_str = re.sub(r'@\w+(\.\w+)*\s*', '', param_str).strip()
    # Handle varargs
    varargs = param_str.endswith("...")
    if varargs:
        param_str = param_str[:-3].strip()

    type_name = simplify_type(param_str)
    suffix = "..." if varargs else ""
    return f"{type_name}{suffix} p{idx}"


def collect_imports(pkg, extends_type, implements_list, apis):
    """Collect needed imports for a class."""
    imports = set()

    def maybe_import(fqn):
        if not fqn:
            return
        fqn = re.sub(r'<.*>', '', fqn).replace("[]", "").strip()
        if "." not in fqn:
            return
        type_pkg = fqn.rsplit(".", 1)[0]
        if type_pkg != pkg and not fqn.startswith("java.lang."):
            imports.add(fqn)

    if extends_type:
        maybe_import(extends_type)
    for iface in implements_list:
        maybe_import(iface)

    # We use Object params in stubs, so we don't need to import parameter types
    # This keeps it simple and avoids circular dependency issues

    return sorted(imports)


def generate_enum(pkg, class_name, apis):
    """Generate an enum stub."""
    lines = [f"package {pkg};\n"]

    constants = [a for a in apis if a['kind'] == 'enum_constant']
    methods = [a for a in apis if a['kind'] == 'method']
    fields = [a for a in apis if a['kind'] == 'field']

    lines.append(f"public enum {class_name} {{")

    if constants:
        const_names = [a['name'] for a in constants]
        lines.append("    " + ",\n    ".join(const_names) + ";")
    else:
        lines.append("    ;")

    # Static fields
    for f in fields:
        if f['is_static']:
            ret = simplify_type(f['return_type']) if f['return_type'] else 'Object'
            defval = default_return(ret)
            if defval:
                lines.append(f"    public static final {ret} {f['name']} = {defval};")
            else:
                lines.append(f"    public static {ret} {f['name']};")

    # Methods
    for m in methods:
        sig = m['signature']
        ret_type, mname, params = parse_signature(sig)
        if not mname:
            continue
        sret = simplify_type(ret_type) if ret_type else "void"
        sparams = ", ".join(format_param(p, i) for i, p in enumerate(params))
        static = "static " if m['is_static'] else ""
        defval = default_return(sret)
        if defval:
            lines.append(f"    public {static}{sret} {mname}({sparams}) {{ return {defval}; }}")
        else:
            lines.append(f"    public {static}{sret} {mname}({sparams}) {{}}")

    lines.append("}")
    return "\n".join(lines) + "\n"


def generate_interface(pkg, class_name, extends_type, apis):
    """Generate an interface stub."""
    lines = [f"package {pkg};\n"]

    ext = ""
    if extends_type and extends_type not in ("java.lang.Object", "Object"):
        ext = f" extends {simplify_type(extends_type)}"

    lines.append(f"public interface {class_name}{ext} {{")

    fields = [a for a in apis if a['kind'] == 'field']
    methods = [a for a in apis if a['kind'] == 'method']

    for f in fields:
        ret = simplify_type(f['return_type']) if f['return_type'] else 'Object'
        defval = default_return(ret)
        if defval and ret in ('int', 'long', 'float', 'double', 'boolean', 'byte', 'short', 'char',
                               'String'):
            lines.append(f"    {ret} {f['name']} = {defval};")
        elif ret == 'String':
            lines.append(f"    String {f['name']} = \"\";")
        else:
            # Can't have non-constant fields in interface, skip or use int
            pass

    for m in methods:
        sig = m['signature']
        ret_type, mname, params = parse_signature(sig)
        if not mname:
            continue
        if m['is_static']:
            continue  # skip static methods in interfaces for simplicity
        sret = simplify_type(ret_type) if ret_type else "void"
        sparams = ", ".join(format_param(p, i) for i, p in enumerate(params))
        lines.append(f"    {sret} {mname}({sparams});")

    lines.append("}")
    return "\n".join(lines) + "\n"


def generate_class(pkg, class_name, type_info, apis):
    """Generate a class stub."""
    is_abstract = type_info['is_abstract']
    is_final = type_info['is_final']
    extends_type = type_info['extends_type']
    implements_list = type_info['implements_list']

    lines = [f"package {pkg};\n"]

    # Class declaration
    mods = "public "
    if is_abstract:
        mods += "abstract "
    if is_final:
        mods += "final "

    ext = ""
    if extends_type and extends_type not in ("java.lang.Object", "Object", None, ""):
        ext = f" extends {simplify_type(extends_type)}"

    impl = ""
    if implements_list:
        impl_names = [simplify_type(i) for i in implements_list]
        impl = " implements " + ", ".join(impl_names)

    lines.append(f"{mods}class {class_name}{ext}{impl} {{")

    constructors = [a for a in apis if a['kind'] == 'constructor']
    fields = [a for a in apis if a['kind'] == 'field']
    methods = [a for a in apis if a['kind'] == 'method']

    # Fields
    for f in fields:
        ret = simplify_type(f['return_type']) if f['return_type'] else 'int'
        defval = default_return(ret)
        static = "static " if f['is_static'] else ""
        final_mod = ""
        if f['is_static']:
            final_mod = "final "
        if defval:
            lines.append(f"    public {static}{final_mod}{ret} {f['name']} = {defval};")
        else:
            lines.append(f"    public {static}{ret} {f['name']};")

    if fields:
        lines.append("")

    # Constructors - just add a default no-arg if none exist
    if constructors:
        for c in constructors:
            sig = c['signature']
            # Parse constructor params from signature like "Foo(int, String)"
            m = re.match(r'\w+\s*\((.*)\)', sig.strip())
            if m:
                params_str = m.group(1).strip()
                if params_str:
                    # Remove annotations
                    params_str = re.sub(r'@\w+(\.\w+)*\s*', '', params_str)
                    params = []
                    depth = 0
                    current = []
                    for ch in params_str:
                        if ch == '<':
                            depth += 1
                        elif ch == '>':
                            depth -= 1
                        elif ch == ',' and depth == 0:
                            params.append(''.join(current).strip())
                            current = []
                            continue
                        current.append(ch)
                    if current:
                        params.append(''.join(current).strip())
                    sparams = ", ".join(format_param(p, i) for i, p in enumerate(params))
                    lines.append(f"    public {class_name}({sparams}) {{}}")
                else:
                    lines.append(f"    public {class_name}() {{}}")
    else:
        lines.append(f"    public {class_name}() {{}}")

    lines.append("")

    # Methods
    seen_methods = set()
    for m in methods:
        sig = m['signature']
        ret_type, mname, params = parse_signature(sig)
        if not mname:
            continue

        # Deduplicate
        method_key = f"{mname}_{len(params)}"
        if method_key in seen_methods:
            continue
        seen_methods.add(method_key)

        sret = simplify_type(ret_type) if ret_type else "void"
        sparams = ", ".join(format_param(p, i) for i, p in enumerate(params))
        static = "static " if m['is_static'] else ""
        defval = default_return(sret)

        if is_abstract and not m['is_static']:
            # Could make abstract, but safer to provide default impl
            pass

        if defval:
            lines.append(f"    public {static}{sret} {mname}({sparams}) {{ return {defval}; }}")
        else:
            lines.append(f"    public {static}void {mname}({sparams}) {{}}")

    lines.append("}")
    return "\n".join(lines) + "\n"


def main():
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    cur = conn.cursor()

    # Get all types with their packages
    cur.execute("""
        SELECT t.id, t.name, t.kind, t.is_abstract, t.is_final, t.is_static,
               t.extends_type, t.implements, p.name as pkg
        FROM android_types t
        JOIN android_packages p ON t.package_id = p.id
        ORDER BY p.name, t.name
    """)

    types = cur.fetchall()
    generated = 0
    skipped = 0
    already_exists = 0

    for t in types:
        pkg = t['pkg']
        class_name = t['name']

        # Skip non-android packages
        if any(pkg.startswith(prefix) for prefix in SKIP_PREFIXES):
            skipped += 1
            continue

        # Skip inner classes (contain $ or .)
        if '$' in class_name or '.' in class_name:
            skipped += 1
            continue

        # Check if file already exists
        java_path = os.path.join(SHIM_ROOT, pkg.replace(".", "/"), f"{class_name}.java")
        if os.path.exists(java_path):
            already_exists += 1
            continue

        # Get APIs for this type
        cur.execute("""
            SELECT kind, name, signature, return_type, params, is_static, is_deprecated
            FROM android_apis
            WHERE type_id = ?
        """, (t['id'],))
        apis = [dict(row) for row in cur.fetchall()]

        # Parse implements
        implements_str = t['implements'] or ""
        implements_list = [i.strip() for i in implements_str.split() if i.strip()] if implements_str else []

        type_info = {
            'is_abstract': t['is_abstract'],
            'is_final': t['is_final'],
            'extends_type': t['extends_type'],
            'implements_list': implements_list,
        }

        try:
            if t['kind'] == 'enum':
                content = generate_enum(pkg, class_name, apis)
            elif t['kind'] == 'interface':
                content = generate_interface(pkg, class_name, t['extends_type'], apis)
            else:
                content = generate_class(pkg, class_name, type_info, apis)

            # Write file
            os.makedirs(os.path.dirname(java_path), exist_ok=True)
            with open(java_path, 'w') as f:
                f.write(content)
            generated += 1

        except Exception as e:
            print(f"ERROR generating {pkg}.{class_name}: {e}", file=sys.stderr)

    conn.close()
    print(f"Generated: {generated}")
    print(f"Already existed: {already_exists}")
    print(f"Skipped (java/inner): {skipped}")


if __name__ == "__main__":
    main()
