#!/usr/bin/env python3
"""Replace unresolvable type references with Object in generated shim files."""

import os
import re
import subprocess

SHIM_ROOT = os.path.join(os.path.dirname(__file__), "..", "shim", "java")

# Types that should be replaced with Object
UNKNOWN_TYPES = [
    "AudioRecordingCallback", "Callback", "Coordinate3F", "CryptoInfo",
    "DeviceCallback", "DownloadRequest", "EffectFactory", "Eq", "EqBand",
    "EventCallback", "FocusObserver", "Limiter", "Mbc", "MbcBand",
    "MidiConnection", "OnDeviceOpenedListener", "OnErrorListener",
    "OnFrameAvailableListener", "OnParameterChangeListener",
    "OnRoutingChangedListener", "PortInfo", "RecordingCallback",
    "SeekPoint", "SeekableInputReader", "TimeListener",
    "WindowInsetsAnimationController", "AudioMetadataReadMap",
    "TransitionListener",
]

def fix_files():
    """Run javac, parse errors, and fix them."""
    # Get all java files
    java_files = []
    for root, dirs, files in os.walk(SHIM_ROOT):
        for f in files:
            if f.endswith(".java"):
                java_files.append(os.path.join(root, f))

    # For each file with an unknown type, replace the type with Object
    fixed = 0
    for filepath in java_files:
        with open(filepath) as f:
            content = f.read()

        changed = False
        for utype in UNKNOWN_TYPES:
            # Replace in parameter types, return types, field types
            # But not in package/import statements or class names
            # Simple approach: replace word boundary matches
            pattern = re.compile(r'(?<![.\w])' + utype + r'(?!\w)')
            if pattern.search(content):
                # Don't replace in import/package lines
                lines = content.split('\n')
                new_lines = []
                for line in lines:
                    if line.strip().startswith('package ') or line.strip().startswith('import '):
                        new_lines.append(line)
                    else:
                        new_lines.append(pattern.sub('Object', line))
                content = '\n'.join(new_lines)
                changed = True

        # Also fix generic type params T, V that aren't declared
        # Replace "T p0" or "V p0" with "Object p0" in method params
        # But be careful not to replace T in class declarations
        for gtype in ['T', 'V']:
            pattern = re.compile(r'\b' + gtype + r'\b(?=\s+p\d)')
            if pattern.search(content):
                content = pattern.sub('Object', content)
                changed = True

        if changed:
            with open(filepath, 'w') as f:
                f.write(content)
            fixed += 1

    print(f"Fixed {fixed} files")

if __name__ == "__main__":
    fix_files()
