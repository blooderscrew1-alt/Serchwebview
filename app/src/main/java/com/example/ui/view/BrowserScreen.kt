package com.example.ui.view

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adblock.AdBlocker
import com.example.ui.components.BookmarksHistorySheet
import com.example.ui.components.BrowserBottomBar
import com.example.ui.components.BrowserTopBar
import com.example.ui.components.SearchDialog
import com.example.ui.components.SettingsSheet
import com.example.ui.components.SpeedDialView
import com.example.ui.components.VideoToolsSheet
import com.example.ui.viewmodel.ActiveSheet
import com.example.ui.viewmodel.BrowserViewModel

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel,
    onEnterPip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUrl by viewModel.currentUrl.collectAsStateWithLifecycle()
    val pageTitle by viewModel.pageTitle.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val loadingProgress by viewModel.loadingProgress.collectAsStateWithLifecycle()
    val canGoBack by viewModel.canGoBack.collectAsStateWithLifecycle()
    val canGoForward by viewModel.canGoForward.collectAsStateWithLifecycle()
    val blockedAdsCount by viewModel.blockedAdsOnPage.collectAsStateWithLifecycle()
    val totalBlockedAds by viewModel.totalBlockedAds.collectAsStateWithLifecycle()
    val activeSheet by viewModel.activeSheet.collectAsStateWithLifecycle()
    val customViewData by viewModel.customViewData.collectAsStateWithLifecycle()
    val playbackSpeed by viewModel.playbackSpeed.collectAsStateWithLifecycle()
    val clipboardUrl by viewModel.clipboardUrl.collectAsStateWithLifecycle()
    val isPipMode by viewModel.isPipMode.collectAsStateWithLifecycle()
    val navigationAction by viewModel.navigationAction.collectAsStateWithLifecycle()

    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val webViewHolder = remember { FrameLayout(context) }
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            val webSettings = this.settings
            webSettings.javaScriptEnabled = true
            webSettings.domStorageEnabled = true
            webSettings.databaseEnabled = true
            webSettings.mediaPlaybackRequiresUserGesture = false
            webSettings.allowFileAccess = false
            webSettings.allowContentAccess = false
            webSettings.loadWithOverviewMode = true
            webSettings.useWideViewPort = true
            webSettings.builtInZoomControls = true
            webSettings.displayZoomControls = false
            webSettings.cacheMode = WebSettings.LOAD_DEFAULT
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }
    }

    // Configure user agent for desktop vs mobile
    LaunchedEffect(settings.desktopMode) {
        if (settings.desktopMode) {
            webView.settings.userAgentString = DESKTOP_USER_AGENT
        } else {
            webView.settings.userAgentString = null // Reset to default mobile user-agent
        }
    }

    // Configure WebViewClient & WebChromeClient
    DisposableEffect(webView) {
        webViewHolder.addView(webView)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                val reqUrl = request?.url?.toString() ?: return null
                if (settings.adBlockEnabled && AdBlocker.isAdUrl(reqUrl)) {
                    view?.post { viewModel.onAdBlocked() }
                    return WebResourceResponse(
                        "text/plain",
                        "UTF-8",
                        AdBlocker.createEmptyResponse()
                    )
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                url?.let {
                    viewModel.onPageStarted(it)
                    viewModel.updateNavigationState(view?.canGoBack() == true, view?.canGoForward() == true)
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                url?.let {
                    viewModel.onPageFinished(it, view?.title)
                    viewModel.updateNavigationState(view?.canGoBack() == true, view?.canGoForward() == true)

                    // Inject ad blocking & auto-skip JS
                    if (settings.adBlockEnabled) {
                        view?.evaluateJavascript(AdBlocker.VIDEO_AD_SKIPPER_SCRIPT, null)
                    }
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                viewModel.onProgressChanged(newProgress)
                viewModel.updateNavigationState(view?.canGoBack() == true, view?.canGoForward() == true)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                if (currentUrl.isNotEmpty()) {
                    viewModel.onPageFinished(currentUrl, title)
                }
            }

            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                super.onShowCustomView(view, callback)
                if (view != null && callback != null) {
                    viewModel.setCustomView(view, callback)
                }
            }

            override fun onHideCustomView() {
                super.onHideCustomView()
                viewModel.hideCustomView()
            }
        }

        onDispose {
            webViewHolder.removeAllViews()
            webView.destroy()
        }
    }

    // Handle navigation events dispatched from ViewModel
    LaunchedEffect(navigationAction) {
        val action = navigationAction ?: return@LaunchedEffect
        when (action) {
            is BrowserViewModel.NavigationAction.LoadUrl -> {
                webView.loadUrl(action.url)
            }
            BrowserViewModel.NavigationAction.GoBack -> {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    viewModel.goHome()
                }
            }
            BrowserViewModel.NavigationAction.GoForward -> {
                if (webView.canGoForward()) {
                    webView.goForward()
                }
            }
            BrowserViewModel.NavigationAction.Reload -> {
                webView.reload()
            }
            BrowserViewModel.NavigationAction.Stop -> {
                webView.stopLoading()
            }
            is BrowserViewModel.NavigationAction.SetSpeed -> {
                webView.evaluateJavascript(AdBlocker.getPlaybackSpeedScript(action.speed), null)
            }
            BrowserViewModel.NavigationAction.ToggleCinema -> {
                webView.evaluateJavascript(AdBlocker.CINEMA_MODE_SCRIPT, null)
            }
        }
        viewModel.clearNavigationAction()
    }

    // Back button handling
    BackHandler(enabled = customViewData != null || activeSheet != ActiveSheet.NONE || currentUrl.isNotEmpty()) {
        when {
            customViewData != null -> viewModel.hideCustomView()
            activeSheet != ActiveSheet.NONE -> viewModel.closeSheet()
            webView.canGoBack() -> webView.goBack()
            else -> viewModel.goHome()
        }
    }

    // Fullscreen HTML5 Video Custom View overlay
    if (customViewData != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("fullscreen_video_container")
        ) {
            AndroidView(
                factory = { customViewData!!.view },
                modifier = Modifier.fillMaxSize()
            )

            // Floating exit fullscreen button
            FloatingActionButton(
                onClick = { viewModel.hideCustomView() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .testTag("exit_fullscreen_video_btn"),
                shape = CircleShape,
                containerColor = Color.Black.copy(alpha = 0.6f),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Filled.FullscreenExit,
                    contentDescription = "Exit Fullscreen"
                )
            }
        }
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                if (!isPipMode && currentUrl.isNotEmpty()) {
                    BrowserTopBar(
                        currentUrl = currentUrl,
                        pageTitle = pageTitle,
                        isLoading = isLoading,
                        progress = loadingProgress,
                        blockedAdsCount = blockedAdsCount,
                        isAdBlockEnabled = settings.adBlockEnabled,
                        onUrlBarClick = { viewModel.setActiveSheet(ActiveSheet.SEARCH) },
                        onRefreshClick = { viewModel.reload() },
                        onStopClick = { viewModel.stopLoading() },
                        onShieldClick = { viewModel.toggleAdBlock() },
                        onToolsClick = { viewModel.setActiveSheet(ActiveSheet.VIDEO_TOOLS) }
                    )
                }
            },
            bottomBar = {
                if (!isPipMode) {
                    BrowserBottomBar(
                        canGoBack = canGoBack,
                        canGoForward = canGoForward,
                        isHomeActive = currentUrl.isEmpty(),
                        onBackClick = { viewModel.goBack() },
                        onForwardClick = { viewModel.goForward() },
                        onHomeClick = { viewModel.goHome() },
                        onBookmarksClick = { viewModel.setActiveSheet(ActiveSheet.BOOKMARKS_HISTORY) },
                        onVideoToolsClick = {
                            if (currentUrl.isEmpty()) {
                                viewModel.setActiveSheet(ActiveSheet.SETTINGS)
                            } else {
                                viewModel.setActiveSheet(ActiveSheet.VIDEO_TOOLS)
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isPipMode) androidx.compose.foundation.layout.PaddingValues(0.dp) else innerPadding)
            ) {
                if (currentUrl.isEmpty()) {
                    SpeedDialView(
                        totalBlockedAds = totalBlockedAds,
                        isAdBlockActive = settings.adBlockEnabled,
                        isPrivateMode = settings.privateBrowsing,
                        currentSearchEngine = settings.defaultSearchEngine,
                        bookmarks = bookmarks,
                        clipboardUrl = clipboardUrl,
                        onSearchEngineChange = { viewModel.updateSearchEngine(it) },
                        onUrlSubmit = { viewModel.loadUrl(it) },
                        onBookmarkClick = { viewModel.loadUrl(it.url) },
                        onDismissClipboard = { viewModel.dismissClipboardUrl() }
                    )
                } else {
                    AndroidView(
                        factory = { webViewHolder },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("webview_container")
                    )
                }
            }
        }
    }

    // Modal Bottom Sheets
    when (activeSheet) {
        ActiveSheet.SEARCH -> {
            SearchDialog(
                initialText = currentUrl,
                currentSearchEngine = settings.defaultSearchEngine,
                onDismiss = { viewModel.closeSheet() },
                onSubmit = { query, engine -> viewModel.onSearchSubmit(query, engine) }
            )
        }
        ActiveSheet.VIDEO_TOOLS -> {
            VideoToolsSheet(
                currentUrl = currentUrl,
                pageTitle = pageTitle,
                isAdBlockEnabled = settings.adBlockEnabled,
                blockedAdsCount = blockedAdsCount,
                isDesktopMode = settings.desktopMode,
                isBookmarked = bookmarks.any { it.url == currentUrl },
                currentSpeed = playbackSpeed,
                onDismiss = { viewModel.closeSheet() },
                onEnterPip = { onEnterPip() },
                onToggleCinema = { viewModel.toggleCinemaMode() },
                onSpeedChange = { viewModel.setPlaybackSpeed(it) },
                onToggleAdBlock = { viewModel.toggleAdBlock() },
                onToggleDesktop = { viewModel.toggleDesktopMode() },
                onToggleBookmark = { viewModel.toggleBookmark() },
                onShareUrl = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, pageTitle)
                        putExtra(Intent.EXTRA_TEXT, currentUrl)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Video Link"))
                },
                onCopyUrl = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("URL", currentUrl))
                    Toast.makeText(context, "URL copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                onOpenSettings = { viewModel.setActiveSheet(ActiveSheet.SETTINGS) }
            )
        }
        ActiveSheet.BOOKMARKS_HISTORY -> {
            BookmarksHistorySheet(
                bookmarks = bookmarks,
                history = history,
                currentUrl = currentUrl,
                pageTitle = pageTitle,
                onDismiss = { viewModel.closeSheet() },
                onUrlClick = { viewModel.loadUrl(it) },
                onAddCurrentBookmark = { viewModel.toggleBookmark() },
                onDeleteBookmark = { viewModel.removeBookmark(it) },
                onDeleteHistoryItem = { viewModel.removeHistoryItem(it) },
                onClearHistory = { viewModel.clearHistory() }
            )
        }
        ActiveSheet.SETTINGS -> {
            SettingsSheet(
                settings = settings,
                totalBlockedAds = totalBlockedAds,
                onDismiss = { viewModel.closeSheet() },
                onToggleAdBlock = { viewModel.toggleAdBlock() },
                onToggleAutoSkipAds = {
                    val current = settings
                    viewModel.toggleAdBlock()
                },
                onTogglePrivateBrowsing = { viewModel.togglePrivateBrowsing() },
                onSelectSearchEngine = { viewModel.updateSearchEngine(it) },
                onClearCache = {
                    webView.clearCache(true)
                    webView.clearHistory()
                    CookieManager.getInstance().removeAllCookies(null)
                    Toast.makeText(context, "Cache and cookies cleared", Toast.LENGTH_SHORT).show()
                }
            )
        }
        ActiveSheet.NONE -> {}
    }
}
