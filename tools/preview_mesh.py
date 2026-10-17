"""
Preview renderer for the Samiqura mesh background.

Mirrors the blob composition in MeshBackground.kt exactly, so what is rendered
here is what the Kotlin code produces. The point is to see the design before
shipping it: the composition is arithmetic, and arithmetic is checkable.

Rendering is additive rather than a normal alpha composite, because that is what
a sequence of RadialGradient draw calls over an opaque base actually looks like.
"""

from PIL import Image
import math

# ── Blob definitions, copied from MeshBackground.kt ───────────────────────────
LIGHT_BLOBS = [
    # cx, cy, radius, core
    (0.86, 0.07, 0.52, (0xFF, 0x9E, 0xA8)),   # coral, top right
    (0.97, 0.26, 0.42, (0xFF, 0xC1, 0x86)),   # peach, under it
    (0.44, 0.46, 0.62, (0xD6, 0xBC, 0xF2)),   # lavender through the middle
    (0.07, 0.70, 0.58, (0xA8, 0xC6, 0xF2)),   # periwinkle, lower left
    (0.56, 0.90, 0.55, (0xC9, 0xDD, 0xFA)),   # pale blue foot
]

DARK_BLOBS = [
    (0.78, 0.06, 0.70, (0x6E, 0x33, 0x50)),
    (0.92, 0.20, 0.46, (0x7A, 0x4A, 0x2E)),
    (0.42, 0.42, 0.72, (0x3E, 0x2E, 0x63)),
    (0.10, 0.74, 0.62, (0x16, 0x20, 0x3D)),
    (0.60, 0.86, 0.58, (0x1A, 0x1A, 0x2E)),
]

LIGHT_BASE_A = (0xF6, 0xF2, 0xF8)
LIGHT_BASE_B = (0xEF, 0xF3, 0xFB)
DARK_BASE_A = (0x14, 0x12, 0x1A)
DARK_BASE_B = (0x0D, 0x0C, 0x12)


def render(blobs, base_a, base_b, w=360, h=800):
    img = Image.new("RGB", (w, h))
    px = img.load()

    max_radius = math.hypot(w, h)

    # Base: linear gradient top-left to bottom-right.
    for y in range(h):
        t = (x_t := (y / max(h - 1, 1)))  # vertical drive for the base wash
        for x in range(w):
            u = x / max(w - 1, 1)
            m = (u + t) / 2.0
            px[x, y] = tuple(
                int(base_a[i] + (base_b[i] - base_a[i]) * m) for i in range(3)
            )

    # Blobs: normal SRC_OVER, which is what Canvas.drawCircle with a
    # RadialGradient whose outer stop is transparent actually does. The centre is
    # an opaque colour and the edge is alpha 0, so each blob covers the ground at
    # its middle and dissolves into it at its edge.
    for cx_f, cy_f, r_f, core in blobs:
        cx = cx_f * w
        cy = cy_f * h
        r = r_f * max_radius
        x0 = max(0, int(cx - r))
        x1 = min(w, int(cx + r) + 1)
        y0 = max(0, int(cy - r))
        y1 = min(h, int(cy + r) + 1)
        for y in range(y0, y1):
            for x in range(x0, x1):
                d = math.hypot(x - cx, y - cy) / r
                if d >= 1.0:
                    continue
                # Stops: opaque through 0.45, then linear fade to alpha 0.
                if d <= 0.45:
                    a = 1.0
                else:
                    a = 1.0 - ((d - 0.45) / 0.55)
                br, bg, bb = px[x, y]
                px[x, y] = (
                    int(br + (core[0] - br) * a),
                    int(bg + (core[1] - bg) * a),
                    int(bb + (core[2] - bb) * a),
                )
    return img


light = render(LIGHT_BLOBS, LIGHT_BASE_A, LIGHT_BASE_B)
dark = render(DARK_BLOBS, DARK_BASE_A, DARK_BASE_B)

side = side_by_side = Image.new("RGB", (light.width + dark.width + 24, light.height), (255, 255, 255))
side_by_side.paste(light, (0, 0))
side_by_side.paste(dark, (light.width + 24, 0))
side_by_side.save("/workspace/repos/omni-browser/out/mesh-preview.png")

# Sample a few points so the palette can be checked numerically as well.
def sample(img, fx, fy):
    return img.getpixel((int(fx * img.width), int(fy * img.height)))

print("LIGHT  top-right(rose):", sample(light, 0.80, 0.10))
print("LIGHT  centre(violet):", sample(light, 0.45, 0.45))
print("LIGHT  bottom-left(blue):", sample(light, 0.12, 0.78))
print("DARK   top-right:", sample(dark, 0.80, 0.10))
print("DARK   centre:", sample(dark, 0.45, 0.45))
print("DARK   bottom-left:", sample(dark, 0.12, 0.78))
print("saved out/mesh-preview.png")
