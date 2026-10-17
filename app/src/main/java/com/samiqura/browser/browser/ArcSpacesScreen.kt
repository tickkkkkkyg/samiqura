/*
 * Samiqura - Arc-inspired Spaces surface.
 *
 * Copyright (C) 2026 Samiqura contributors
 * Derived from Omni Browser (C) 2026 RebelRoot Ltd, GPL-3.0.
 *
 * Arc's central idea is that a tab group is a *place* you move into, not a
 * section you scroll past. This screen is that idea on a phone: one card per
 * Space, each showing its own tabs, with the whole Space opening as a unit.
 *
 * All colour, radius and motion values come from ArcTokens; nothing is
 * hard-coded here.
 */

package com.samiqura.browser.browser

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samiqura.browser.ui.theme.ArcMotion
import com.samiqura.browser.ui.theme.ArcRadius
import com.samiqura.browser.ui.theme.ArcSpaceColors

/**
 * A single Space: its colour, its name, and the tabs inside it.
 *
 * [isActive] drives the border and elevation so the current Space is obvious
 * at a glance without relying on colour alone.
 */
@Composable
private fun SpaceCard(
    group: TabGroup,
    tabs: List<TabState>,
    isActive: Boolean,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = Color(group.color)
    val borderColor by animateColorAsState(
        targetValue = if (isActive) accent else MaterialTheme.colorScheme.outline,
        animationSpec = tween(ArcMotion.BASE_MILLIS),
        label = "spaceBorder",
    )
    val borderWidth by animateFloatAsState(
        targetValue = if (isActive) 2f else 1f,
        animationSpec = tween(ArcMotion.BASE_MILLIS),
        label = "spaceBorderWidth",
    )
    val surface = if (isActive) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surface
    }

    Surface(
        onClick = onOpen,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "${group.title}, ${tabs.size} tabs"
            },
        shape = RoundedCornerShape(ArcRadius.card),
        color = surface,
        border = BorderStroke(
            borderWidth.dp,
            borderColor,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(ArcRadius.dot * 2)
                        .clip(CircleShape)
                        .background(accent)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = group.title.ifBlank { "Space" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = tabs.size.toString(),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Delete ${group.title}",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (tabs.isEmpty()) {
                Text(
                    text = "No tabs yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                // Show only the first few so a Space with dozens of tabs still
                // reads as a single card rather than a list.
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    tabs.take(4).forEach { tab ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(accent.copy(alpha = 0.7f))
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = tab.title.ifBlank { tab.url },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    if (tabs.size > 4) {
                        Text(
                            text = "+${tabs.size - 4} more",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

/**
 * The Spaces list. Falls back to a prompt when the user has no Spaces yet,
 * because an empty screen with no instruction is a dead end.
 */
@Composable
fun ArcSpacesScreen(
    tabGroups: List<TabGroup>,
    tabs: List<TabState>,
    activeTabId: String?,
    onOpenSpace: (TabGroup) -> Unit,
    onDeleteSpace: (TabGroup) -> Unit,
    modifier: Modifier = Modifier,
) {
    // A Space is active when it holds the tab currently in front.
    val activeGroupId = tabGroups.firstOrNull { group ->
        group.tabIds.any { it == activeTabId }
    }?.id

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "header") {
            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = "Spaces",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Groups of tabs you move between",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (tabGroups.isEmpty()) {
            item(key = "empty") {
                Surface(
                    shape = RoundedCornerShape(ArcRadius.card),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            tint = ArcSpaceColors.first(),
                            modifier = Modifier.size(28.dp),
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "No Spaces yet",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Group related tabs together to keep them tidy.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        } else {
            items(tabGroups, key = { it.id }) { group ->
                val groupTabs = tabs.filter { it.id in group.tabIds }
                SpaceCard(
                    group = group,
                    tabs = groupTabs,
                    isActive = group.id == activeGroupId,
                    onOpen = { onOpenSpace(group) },
                    onDelete = { onDeleteSpace(group) },
                )
            }
        }
    }
}
