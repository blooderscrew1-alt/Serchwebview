package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.net.URI

@Composable
fun BrowserTopBar(
    currentUrl: String,
    pageTitle: String,
    isLoading: Boolean,
    progress: Int,
    blockedAdsCount: Int,
    isAdBlockEnabled: Boolean,
    onUrlBarClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onStopClick: () -> Unit,
    onShieldClick: () -> Unit,
    onToolsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // URL pill
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .clickable { onUrlBarClick() }
                    .testTag("url_bar_button"),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                shape = RoundedCornerShape(22.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentUrl.isEmpty()) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Search or enter video URL...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        val isHttps = currentUrl.startsWith("https://")
                        Icon(
                            imageVector = if (isHttps) Icons.Filled.Lock else Icons.Filled.Search,
                            contentDescription = if (isHttps) "Secure connection" else "Connection",
                            tint = if (isHttps) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        val displayHost = try {
                            val uri = URI(currentUrl)
                            uri.host?.removePrefix("www.") ?: currentUrl
                        } catch (_: Exception) {
                            currentUrl
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = displayHost,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (pageTitle.isNotBlank() && pageTitle != displayHost) {
                                Text(
                                    text = pageTitle,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Reload / Stop icon inside URL bar
                    if (currentUrl.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                if (isLoading) onStopClick() else onRefreshClick()
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("refresh_stop_button")
                        ) {
                            Icon(
                                imageVector = if (isLoading) Icons.Filled.Close else Icons.Filled.Refresh,
                                contentDescription = if (isLoading) "Stop loading" else "Reload page",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Shield / AdBlocker status badge
            IconButton(
                onClick = onShieldClick,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("adblock_shield_button")
            ) {
                BadgedBox(
                    badge = {
                        if (isAdBlockEnabled && blockedAdsCount > 0) {
                            Badge(
                                containerColor = Color(0xFF10B981),
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = if (blockedAdsCount > 99) "99+" else blockedAdsCount.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = "AdBlock Shield",
                        tint = if (isAdBlockEnabled) Color(0xFF0EA5E9) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Video Tools & Options menu button
            IconButton(
                onClick = onToolsClick,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("tools_menu_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = "Video tools and settings",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Web progress indicator
        AnimatedVisibility(visible = isLoading && progress < 100) {
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent
            )
        }
    }
}
