/*
 * Samiqura - minimal home screen.
 *
 * Copyright (C) 2026 Samiqura contributors
 * Derived from Omni Browser (C) 2026 RebelRoot Ltd, GPL-3.0.
 *
 * The design intent is an empty room. Nothing competes with the mark, and every
 * affordance is three taps of reach at most.
 *
 * Two decisions are load-bearing:
 *
 * 1. The background is a rasterised mesh that is built once per size and theme,
 *    then reused. Compositing it per frame would cost real battery on a screen
 *    that is on all day.
 * 2. Search lives in a sheet rather than on the screen. A visible search field
 *    would break the empty composition, and tapping the mark is a more natural
 *    target on a screen with nothing else in it.
 */

package com.samiqura.browser.browser

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samiqura.browser.R
import com.samiqura.browser.ui.theme.ArcRadius
import com.samiqura.browser.ui.theme.MeshInk
import com.samiqura.browser.ui.theme.createMeshBackground
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.res.stringResource

/** Sheets need a real surface colour; the mesh cannot show through them. */
private val SheetShape = RoundedCornerShape(
    topStart = ArcRadius.sheet,
    topEnd = ArcRadius.sheet,
)

/**
 * The mesh, rasterised once per viewport size and theme.
 *
 * Composition does not cache, so the size is held in state and the bitmap is
 * rebuilt only when the size or theme actually changes. Building it blends a
 * few hundred thousand pixels, so it happens off the composition thread to
 * avoid dropping a frame at first paint.
 */
@Composable
private fun rememberMesh(
    isDark: Boolean,
    size: IntSize,
): ImageBitmap? {
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(size.width, size.height, isDark) {
        if (size.width > 0 && size.height > 0) {
            val w = size.width
            val h = size.height
            bitmap = withContext(Dispatchers.Default) {
                createMeshBackground(w, h, isDark)
            }
        } else {
            bitmap = null
        }
    }
    return bitmap
}

/**
 * The minimal home screen.
 *
 * [isDark] selects the deep variant of the same mesh rather than a flat black,
 * so the two modes stay recognisably the same product.
 */
@Composable
fun MeshHomeScreen(
    isDark: Boolean,
    onOpenSearch: () -> Unit,
    onOpenCast: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {    var viewport by remember { mutableStateOf(IntSize.Zero) }
    val mesh = rememberMesh(isDark, viewport)
    val ink = if (isDark) MeshInk.onDark else MeshInk.onLight
    val inkSecondary = if (isDark) MeshInk.onDarkSecondary else MeshInk.onLightSecondary

    Box(
        modifier = modifier
            .fillMaxSize()
            // Measured so the mesh is built at exactly the viewport size. The
            // background sits under the system bars, so this is the full screen
            // rather than the inset content area.
            .onGloballyPositioned { viewport = it.size }
    ) {
        if (mesh != null) {
            Image(
                bitmap = mesh,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
        }

        // The mark, centred and alone. Tapping it opens search, which is the
        // only gesture the empty screen needs.
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(40.dp))
                .clickable(onClick = onOpenSearch)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_samiqura_mark),
                contentDescription = null,
                modifier = Modifier
                    .size(148.dp)
                    .shadow(
                        elevation = 22.dp,
                        shape = RoundedCornerShape(40.dp),
                        ambientColor = Color(0x33000000),
                        spotColor = Color(0x33000000)
                    )
            )
        }

        // Watermark. Sits under the control row and stays legible over the mesh
        // without competing with the mark: the mesh's pale lower-left region is
        // the lightest area, and this text is placed where that gradient has
        // settled rather than over its brightest point.
        Text(
            text = stringResource(R.string.about_watermark),
            color = ink.copy(alpha = 0.55f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 84.dp)
        )

        // Three controls, and nothing else. Centre is a raised pill so it reads
        // as the primary action without needing colour.
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 28.dp, end = 28.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onOpenCast) {
                Icon(
                    imageVector = Icons.Rounded.Cast,
                    contentDescription = "Cast",
                    tint = ink,
                    modifier = Modifier.size(25.dp)
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MeshInk.markOutline)
                    .clickable(onClick = onOpenSearch)
                    .padding(horizontal = 26.dp, vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Search",
                    tint = if (isDark) MeshInk.onDark else MeshInk.onLight,
                    modifier = Modifier.size(26.dp)
                )
            }

            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Settings",
                    tint = ink,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}

/**
 * The search sheet that rises over the home screen.
 *
 * Takes the query rather than owning it, so the caller keeps the single source
 * of truth for the text and the suggestions stay in step with it.
 */
@Composable
fun MeshSearchSheet(
    query: String,
    onQueryChange: (String) -> Unit,
    suggestions: List<String>,
    onSubmit: (String) -> Unit,
    isDark: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ink = if (isDark) MeshInk.onDark else MeshInk.onLight
    val inkSecondary = if (isDark) MeshInk.onDarkSecondary else MeshInk.onLightSecondary
    val sheetColor = if (isDark) MeshInk.sheetDark else MeshInk.sheetLight

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SheetShape)
            .background(sheetColor)
            .navigationBarsPadding()
            .padding(top = 10.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Grab handle. Decorative, so no semantics.
        Box(
            modifier = Modifier
                .size(width = 38.dp, height = 4.dp)
                .clip(CircleShape)
                .background(ink.copy(alpha = 0.22f))
        )

        Spacer(Modifier.height(16.dp))

        // The pill. The text field is transparent and sits on top of the pill so
        // the field itself never draws a border or its own background.
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(color = ink, fontSize = 16.sp),
            cursorBrush = SolidColor(ink),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (query.isNotBlank()) onSubmit(query.trim())
                }
            ),
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .clip(CircleShape)
                .background(MeshInk.markOutline)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Search…",
                            color = inkSecondary,
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    inner()
                }
            }
        )

        Spacer(Modifier.height(6.dp))

        // Suggestions, capped so the sheet never grows past the fold.
        if (suggestions.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                suggestions.take(12).forEach { suggestion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSubmit(suggestion) }
                            .padding(horizontal = 22.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = suggestion,
                            color = ink,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
