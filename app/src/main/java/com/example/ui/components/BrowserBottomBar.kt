package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun BrowserBottomBar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    isHomeActive: Boolean,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    onHomeClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onVideoToolsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back
            IconButton(
                onClick = onBackClick,
                enabled = canGoBack || !isHomeActive,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (canGoBack || !isHomeActive) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }
                )
            }

            // Forward
            IconButton(
                onClick = onForwardClick,
                enabled = canGoForward,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_forward_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Forward",
                    tint = if (canGoForward) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }
                )
            }

            // Home
            IconButton(
                onClick = onHomeClick,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_home_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = if (isHomeActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }

            // Bookmarks & History
            IconButton(
                onClick = onBookmarksClick,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_bookmarks_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Bookmarks,
                    contentDescription = "Bookmarks and History",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Video Controls / Tools
            IconButton(
                onClick = onVideoToolsClick,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_video_tools_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.VideoSettings,
                    contentDescription = "Video tools",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
