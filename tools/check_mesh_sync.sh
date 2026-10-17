#!/usr/bin/env bash
# Fails if the Kotlin mesh palette and the preview renderer disagree.
#
# tools/preview_mesh.py reimplements the blob composition from
# MeshBackground.kt so the gradient can be looked at as an image rather than
# guessed at. That only helps if the two stay in step: a palette tuned in the
# preview and never copied across would make the preview actively misleading.
#
# Run after changing any blob in either file.
set -euo pipefail

cd "$(dirname "$0")/.."

python3 - <<'PY'
import re, sys

kt_path = "app/src/main/java/com/samiqura/browser/ui/theme/MeshBackground.kt"
py_path = "tools/preview_mesh.py"

kt = open(kt_path, encoding="utf-8").read()
py = open(py_path, encoding="utf-8").read()

# Markers are literal substrings, because the two files declare the same things
# in different shapes: Kotlin writes "private val DarkBlobs" and
# "object MeshInk", Python writes "DARK_BLOBS = [" and "def render".
def slice_between(src, start, end, what):
    ia = src.find(start)
    ib = src.find(end)
    if ia < 0 or ib < 0 or ia >= ib:
        raise SystemExit(
            f"::error::could not locate {what}: start={start!r} ({ia}), "
            f"end={end!r} ({ib}). The marker moved; update this script."
        )
    return src[ia:ib]

def kotlin_blobs(start, end, what):
    block = slice_between(kt, start, end, what)
    return re.findall(
        r"MeshBlob\((\d+\.\d+)f, (\d+\.\d+)f, (\d+\.\d+)f, 0xFF([0-9A-F]{6})\.toInt\(\)",
        block,
    )

def python_blobs(start, end, what):
    block = slice_between(py, start, end, what)
    return [
        (m[0], m[1], m[2], m[3] + m[4] + m[5])
        for m in re.findall(
            r"\((\d+\.\d+), (\d+\.\d+), (\d+\.\d+), "
            r"\(0x([0-9A-F]{2}), 0x([0-9A-F]{2}), 0x([0-9A-F]{2})\)\)",
            block,
        )
    ]

bad = False
for label, k, p in [
    ("Light",
     kotlin_blobs("private val LightBlobs", "private val DarkBlobs", "light blobs"),
     python_blobs("LIGHT_BLOBS = [", "DARK_BLOBS = [", "light blobs")),
    ("Dark",
     kotlin_blobs("private val DarkBlobs", "object MeshInk", "dark blobs"),
     python_blobs("DARK_BLOBS = [", "def render", "dark blobs")),
]:
    if k != p:
        bad = True
        print(f"::error::{label} blobs differ between Kotlin and the preview")
        for i, (a, b) in enumerate(zip(k, p)):
            if a != b:
                print(f"  blob {i}: kotlin={a} preview={b}")
        if len(k) != len(p):
            print(f"  count: kotlin={len(k)} preview={len(p)}")
    else:
        print(f"{label}: {len(k)} blobs match")

sys.exit(1 if bad else 0)
PY

echo "Mesh palette in sync with the preview renderer."
