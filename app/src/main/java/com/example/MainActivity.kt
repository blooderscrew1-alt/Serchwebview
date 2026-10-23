package com.example

import android.app.PictureInPictureParams
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.ui.theme.SerchwebviewTheme
import com.example.ui.view.BrowserScreen
import com.example.ui.viewmodel.BrowserViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)

        setContent {
            SerchwebviewTheme {
                BrowserScreen(
                    viewModel = viewModel,
                    onEnterPip = { enterPipMode() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkClipboardForVideoLink()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return

        when (intent.action) {
            Intent.ACTION_VIEW -> {
                val data = intent.dataString
                if (!data.isNullOrBlank()) {
                    viewModel.loadUrl(data)
                }
            }
            Intent.ACTION_SEND -> {
                if (intent.type == "text/plain") {
                    val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                    if (!sharedText.isNullOrBlank()) {
                        // Extract URL from shared text if surrounded by other words
                        val url = extractUrl(sharedText)
                        viewModel.loadUrl(url)
                    }
                }
            }
        }
    }

    private fun extractUrl(text: String): String {
        val trimmed = text.trim()
        val words = trimmed.split("\\s+".toRegex())
        for (word in words) {
            if (word.startsWith("http://") || word.startsWith("https://")) {
                return word
            }
        }
        return trimmed
    }

    private fun checkClipboardForVideoLink() {
        try {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            if (clipboard?.hasPrimaryClip() == true) {
                val item = clipboard.primaryClip?.getItemAt(0)
                val text = item?.text?.toString()
                viewModel.checkClipboardUrl(text)
            }
        } catch (_: Exception) {}
    }

    private fun enterPipMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val params = PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(16, 9))
                    .build()
                enterPictureInPictureMode(params)
            } catch (_: Exception) {
                // Fallback if device does not support PiP
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        viewModel.setPipMode(isInPictureInPictureMode)
    }
}
