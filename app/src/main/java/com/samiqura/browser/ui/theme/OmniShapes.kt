/*
 * Omni Browser - A premium, private, and secure web browser.
 * Copyright (C) 2026 RebelRoot Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.samiqura.browser.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val OmniShapes = Shapes(
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp)
)

/**
 * Arc-inspired shape scale.
 *
 * Larger than [OmniShapes] because the Arc language is pill-like: controls are
 * fully rounded and cards are generously curved, which is what separates the
 * intended look from a conventional Material surface. Values live in
 * [ArcRadius] so components and the theme agree.
 */
val ArcShapes = Shapes(
    extraSmall = RoundedCornerShape(ArcRadius.chip),
    small = RoundedCornerShape(ArcRadius.panel),
    medium = RoundedCornerShape(ArcRadius.card),
    large = RoundedCornerShape(ArcRadius.card),
    extraLarge = RoundedCornerShape(ArcRadius.sheet),
)
