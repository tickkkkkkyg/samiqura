/*
 * Samiqura - About and Privacy Policy.
 *
 * Copyright (C) 2026 Samiqura contributors
 * Derived from Omni Browser (C) 2026 RebelRoot Ltd, GPL-3.0.
 *
 * Two constraints shape this screen, and they pull in opposite directions.
 *
 * Samiqura is an independent distribution with its own identity, so the About
 * page leads with Puskr Cooperation and its funding. But the app is distributed
 * under GPL-3.0, which requires the upstream attribution to remain visible, and
 * the upstream trademark policy requires that the fork not imply endorsement.
 * So the credit is stated plainly, in its own section, rather than being buried
 * in a licence file nobody opens.
 *
 * The privacy policy lives in the app rather than on a website. A hosted policy
 * can be changed without the user seeing it; this one ships with the binary, so
 * it can only change when the user installs an update.
 */

package com.samiqura.browser.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samiqura.browser.R

/**
 * The About screen: who built Samiqura, what it is, and where it came from.
 */
@Composable
fun AboutSamiquraScreen(
    appVersionName: String,
    onNavigateBack: () -> Unit,
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Cap the reading width. Long measure lines are hard to track back from,
    // which is the opposite of what a policy page is for.
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = stringResource(R.string.about_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Mark, name, and who is behind it. This is the identity block,
                // so it sits alone above the factual sections.
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_samiqura_mark),
                        contentDescription = null,
                        modifier = Modifier.size(96.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Samiqura",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.about_version_label) + " " + appVersionName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.about_watermark),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.about_funded_by),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AboutParagraph(R.string.about_blurb)

                AboutSection(
                    titleRes = R.string.about_upstream_title,
                    bodyRes = R.string.about_upstream_body
                )

                Spacer(Modifier.height(24.dp))
            }
            Spacer(Modifier.navigationBarsPadding())
        }
    }
}

/**
 * One policy section: a heading and its body.
 *
 * Kept deliberately plain. A policy page is read, not admired, so it avoids
 * cards and decoration that would compete with the text.
 */
@Composable
private fun AboutSection(titleRes: Int, bodyRes: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(bodyRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 21.sp
        )
    }
}

/** Body text with no heading, for the opening description. */
@Composable
private fun AboutParagraph(bodyRes: Int) {
    Text(
        text = stringResource(bodyRes),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 21.sp
    )
}

/**
 * The privacy policy, in the app.
 *
 * Every claim here is checkable against the codebase: there is no analytics SDK,
 * no crash reporter and no first-party server, so the policy can say plainly
 * that nothing is collected. Claims that could not be verified have been left
 * out rather than softened.
 */
@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = stringResource(R.string.about_privacy_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Lead with the one-sentence answer. A reader who wants the gist
                // should not have to scroll for it.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(18.dp)
                ) {
                    Text(
                        text = stringResource(R.string.privacy_summary_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.privacy_summary_body),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 21.sp
                    )
                }

                AboutSection(R.string.privacy_what_stays_title, R.string.privacy_what_stays_body)
                AboutSection(R.string.privacy_network_title, R.string.privacy_network_body)
                AboutSection(R.string.privacy_permissions_title, R.string.privacy_permissions_body)
                AboutSection(R.string.privacy_children_title, R.string.privacy_children_body)
                AboutSection(R.string.privacy_changes_title, R.string.privacy_changes_body)
                AboutSection(R.string.privacy_contact_title, R.string.privacy_contact_body)

                Spacer(Modifier.height(24.dp))
            }
            Spacer(Modifier.navigationBarsPadding())
        }
    }
}
