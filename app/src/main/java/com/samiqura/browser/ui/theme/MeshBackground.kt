/*
 * Samiqura - signature background and mark.
 *
 * Copyright (C) 2026 Samiqura contributors
 * Derived from Omni Browser (C) 2026 RebelRoot Ltd, GPL-3.0.
 *
 * The visual language here is deliberately minimal: one soft mesh gradient, one
 * mark, and nothing competing for attention. Two rules follow from that and are
 * enforced by keeping colour in this file only.
 *
 * 1. The gradient is rasterised once and cached. Compositing a few large radial
 *    gradients is cheap, but doing it on every frame costs real battery on a
 *    screen that is on all day. Nothing here draws per-frame after the first
 *    composition.
 * 2. Light and dark are the same composition with different blob colours and
 *    weights, so the two modes cannot drift apart structurally.
 */

package com.samiqura.browser.ui.theme

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * One soft colour blob in the mesh.
 *
 * Position and radius are fractions of the canvas so the same definition scales
 * from a phone to a tablet without looking different.
 */
private data class MeshBlob(
    val centerX: Float,
    val centerY: Float,
    val radius: Float,
    /** Inner colour at full opacity. */
    val core: Int,
    /** Colour at the blob's edge, transparent, so blobs blend rather than band. */
    val edge: Int,
)

/**
 * Light-mode mesh. Coral and peach sit high and to the right, lavender carries
 * the middle, and a periwinkle blue anchors the lower left. The warm-to-cool
 * diagonal is what makes the background read as lit rather than merely tinted.
 *
 * These values were previewed as an image before being committed, by
 * tools/preview_mesh.py, which reimplements this composition. The first attempt
 * was visibly washed out and had to be pushed much further than expected.
 */
private val LightBlobs = listOf(
    MeshBlob(0.86f, 0.07f, 0.52f, 0xFFFF9EA8.toInt(), 0x00FF9EA8), // coral
    MeshBlob(0.97f, 0.26f, 0.42f, 0xFFFFC186.toInt(), 0x00FFC186), // peach
    MeshBlob(0.44f, 0.46f, 0.62f, 0xFFD6BCF2.toInt(), 0x00D6BCF2), // lavender
    MeshBlob(0.07f, 0.70f, 0.58f, 0xFFA8C6F2.toInt(), 0x00A8C6F2), // periwinkle
    MeshBlob(0.56f, 0.90f, 0.55f, 0xFFC9DDFA.toInt(), 0x00C9DDFA), // pale blue foot
)

/**
 * Dark-mode mesh: the same composition, pushed deep and desaturated. Rose and
 * violet keep just enough chroma to stay recognisable as the same brand, and the
 * blues drop almost to black so text remains comfortable at night.
 */
private val DarkBlobs = listOf(
    MeshBlob(0.78f, 0.06f, 0.70f, 0xFF6E3350.toInt(), 0x006E3350),
    MeshBlob(0.92f, 0.20f, 0.46f, 0xFF7A4A2E.toInt(), 0x007A4A2E),
    MeshBlob(0.42f, 0.42f, 0.72f, 0xFF3E2E63.toInt(), 0x003E2E63),
    MeshBlob(0.10f, 0.74f, 0.62f, 0xFF16203D.toInt(), 0x0016203D),
    MeshBlob(0.60f, 0.86f, 0.58f, 0xFF1A1A2E.toInt(), 0x001A1A2E),
)

/**
 * Ink colours for the minimal shell, tuned against each ground.
 *
 * Exposed as Compose [androidx.compose.ui.graphics.Color] rather than Int: the
 * Canvas path in this file works in Int ARGB, but the composables consume these
 * as tint and background colours, and an Int there is a type error waiting to
 * happen.
 */
object MeshInk {
    /** Primary text and icons on the light mesh. */
    val onLight = androidx.compose.ui.graphics.Color(0xFF1A1620)
    /** Secondary text on the light mesh. */
    val onLightSecondary = androidx.compose.ui.graphics.Color(0xFF5C5566)
    /** Primary text and icons on the dark mesh. */
    val onDark = androidx.compose.ui.graphics.Color(0xFFF4F1F8)
    /** Secondary text on the dark mesh. */
    val onDarkSecondary = androidx.compose.ui.graphics.Color(0xFFA8A0B8)
    /**
     * The mark's outline and the pill surface. Opaque white rather than a tint,
     * because these sit over every part of the mesh and a translucent value
     * would disappear against the pale lower-left region.
     */
    val markOutline = androidx.compose.ui.graphics.Color(0xFFFFFFFF)
    /** The sheet surface sits above the mesh, so it needs a real colour. */
    val sheetLight = androidx.compose.ui.graphics.Color(0xFFF3F1F7)
    val sheetDark = androidx.compose.ui.graphics.Color(0xFF1B1922)
}

/**
 * Rasterise the mesh once at [width] x [height] and return it as a Compose
 * bitmap.
 *
 * Drawn with the platform Canvas rather than Compose brushes because this runs
 * exactly once: the native side composites the blobs in a single pass with
 * additive-leaning blending, which is faster than issuing one draw call per blob
 * and keeps the result identical across API levels.
 *
 * Caching is the caller's job, via [rememberMeshBackground]. The function is
 * pure so a caller can key it on size and theme alone.
 */
fun createMeshBackground(
    width: Int,
    height: Int,
    isDark: Boolean,
): ImageBitmap {
    // Guard against a zero-sized first layout pass; a 1x1 bitmap keeps callers
    // free of null checks while being discarded on the next composition.
    val w = width.coerceAtLeast(1)
    val h = height.coerceAtLeast(1)

    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val blobs = if (isDark) DarkBlobs else LightBlobs
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Base coat first so any gap between blobs still shows a designed colour
    // rather than transparent black.
    paint.shader = LinearGradient(
        0f, 0f, w.toFloat(), h.toFloat(),
        if (isDark) 0xFF14121A.toInt() else 0xFFF6F2F8.toInt(),
        if (isDark) 0xFF0D0C12.toInt() else 0xFFEFF3FB.toInt(),
        Shader.TileMode.CLAMP,
    )
    canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

    // The largest radius that still covers its blob from the far corner. Using a
    // single radius keeps every blob's falloff identical, which is what stops
    // the mesh from looking blotchy.
    val maxRadius = kotlin.math.hypot(w.toDouble(), h.toDouble()).toFloat()

    blobs.forEach { blob ->
        val cx = blob.centerX * w
        val cy = blob.centerY * h
        val r = blob.radius * maxRadius
        paint.shader = RadialGradient(
            cx, cy, r,
            intArrayOf(blob.core, blob.core, blob.edge),
            floatArrayOf(0f, 0.45f, 1f),
            Shader.TileMode.CLAMP,
        )
        canvas.drawCircle(cx, cy, r, paint)
    }

    return bitmap.asImageBitmap()
}
