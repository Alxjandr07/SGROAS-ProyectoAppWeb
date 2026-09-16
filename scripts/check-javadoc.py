#!/usr/bin/env python3
"""Equivalent to scripts/check-javadoc.ps1.

Checks that at least 90% of public/protected methods in src/main/java have a
Javadoc block immediately above (skipping annotation-only lines). Ports the
PowerShell logic exactly (reads files as latin-1). Exit 0 = OK, 1 = below 90%.
"""
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, "src", "main", "java")

PATTERN = re.compile(
    r"^\s*(public|protected)\s+((static|final|synchronized|abstract)\s+)*[\w<>, \.\?\[\]]+\s+(\w+)\s*\(",
    re.IGNORECASE,
)


def main():
    total = 0
    doc = 0
    missing = []

    for dirpath, _dirs, files in os.walk(SRC):
        for fname in files:
            if not fname.endswith(".java"):
                continue
            path = os.path.join(dirpath, fname)
            with open(path, "r", encoding="latin-1") as fh:
                txt = fh.read()
            lines = txt.splitlines()
            for i, line in enumerate(lines):
                m = PATTERN.match(line)
                if not m:
                    continue
                total += 1
                j = i - 1
                while j >= 0 and lines[j].lstrip().startswith("@"):
                    j -= 1
                if j >= 0 and re.search(r"\*/\s*$", lines[j].rstrip()):
                    doc += 1
                else:
                    rel = os.path.relpath(path, ROOT)
                    missing.append(f"{rel}:{i + 1}: {m.group(3)}")

    pct = 100.0 * doc / total if total > 0 else 0.0
    print(f"Javadoc coverage: {doc}/{total} ({pct:.1f}%)")
    if pct < 90:
        print(f"Missing Javadoc ({len(missing)}):")
        for item in missing:
            print(f"  {item}")
        print("FAIL: Javadoc below 90%")
        return 1
    print("OK: Javadoc >= 90%")
    return 0


if __name__ == "__main__":
    sys.exit(main())