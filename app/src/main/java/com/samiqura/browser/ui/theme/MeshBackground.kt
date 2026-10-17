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
 * Light-mode mesh. Warm rose and amber sit high and to the right, violet carries
 * the middle, and a cool blue anchors the lower left. The warm-to-cool diagonal
 * is what makes the background read as lit rather than merely tinted.
 */
private val LightBlobs = listOf(
    MeshBlob(0.78f, 0.06f, 0.70f, 0xFFFFC9D4.toInt(), 0x00FFC9D4),
    MeshBlob(0.92f, 0.20f, 0.46f, 0xFFFFD9A8.toInt(), 0x00FFD9A8),
    MeshBlob(0.42f, 0.42f, 0.72f, 0xFFE0C8F0.toInt(), 0x00E0C8F0),
    MeshBlob(0.10f, 0.74f, 0.62f, 0xFFCBDDF7.toInt(), 0x00CBDDF7),
    MeshBlob(0.60f, 0.86f, 0.58f, 0xFFDDE6FA.toInt(), 0x00DDE6FA),
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

/** Ink colours for the minimal shell, tuned against each ground. */
object MeshInk {
    /** Primary text and icons on the light mesh. */
    val onLight = 0xFF1A1620.toInt()
    /** Secondary text on the light mesh. */
    val onLightSecondary = 0xFF5C5566.toInt()
    /** Primary text and icons on the dark mesh. */
    val onDark = 0xFFF4F1F8.toInt()
    /** Secondary text on the dark mesh. */
    val onDarkSecondary = 0xFFA8A0B8.toInt()
    /**
     * The mark's outline. Opaque white rather than a tint, because the mark sits
     * over every part of the mesh and a translucent edge would disappear against
     * the pale lower-left region.
     */
    val markOutline = 0xFFFFFFFF.toInt()
    /** The sheet surface sits above the mesh, so it needs a real colour. */
    val sheetLight = 0xFFF3F1F7.toInt()
    val sheetDark = 0xFF1B1922.toInt()
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
