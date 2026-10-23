package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.view.View
import android.webkit.WebChromeClient
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Bookmark
import com.example.data.model.BrowserSettings
import com.example.data.model.HistoryItem
import com.example.data.model.SearchEngine
import com.example.data.repository.BrowserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ActiveSheet {
    NONE,
    SEARCH,
    BOOKMARKS_HISTORY,
    VIDEO_TOOLS,
    SETTINGS
}

data class CustomViewData(
    val view: View,
    val callback: WebChromeClient.CustomViewCallback
)

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BrowserRepository(application.applicationContext)

    val bookmarks: StateFlow<List<Bookmark>> = repository.bookmarks
    val history: StateFlow<List<HistoryItem>> = repository.history
    val settings: StateFlow<BrowserSettings> = repository.settings
    val totalBlockedAds: StateFlow<Int> = repository.totalBlockedAds

    private val _currentUrl = MutableStateFlow("")
    val currentUrl: StateFlow<String> = _currentUrl.asStateFlow()

    private val _pageTitle = MutableStateFlow("Serchwebview")
    val pageTitle: StateFlow<String> = _pageTitle.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadingProgress = MutableStateFlow(0)
    val loadingProgress: StateFlow<Int> = _loadingProgress.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _canGoForward = MutableStateFlow(false)
    val canGoForward: StateFlow<Boolean> = _canGoForward.asStateFlow()

    private val _blockedAdsOnPage = MutableStateFlow(0)
    val blockedAdsOnPage: StateFlow<Int> = _blockedAdsOnPage.asStateFlow()

    private val _activeSheet = MutableStateFlow(ActiveSheet.NONE)
    val activeSheet: StateFlow<ActiveSheet> = _activeSheet.asStateFlow()

    private val _customViewData = MutableStateFlow<CustomViewData?>(null)
    val customViewData: StateFlow<CustomViewData?> = _customViewData.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _clipboardUrl = MutableStateFlow<String?>(null)
    val clipboardUrl: StateFlow<String?> = _clipboardUrl.asStateFlow()

    private val _isPipMode = MutableStateFlow(false)
    val isPipMode: StateFlow<Boolean> = _isPipMode.asStateFlow()

    // Navigation trigger event (to be executed by WebView)
    private val _navigationAction = MutableStateFlow<NavigationAction?>(null)
    val navigationAction: StateFlow<NavigationAction?> = _navigationAction.asStateFlow()

    sealed class NavigationAction {
        data class LoadUrl(val url: String) : NavigationAction()
        data object GoBack : NavigationAction()
        data object GoForward : NavigationAction()
        data object Reload : NavigationAction()
        data object Stop : NavigationAction()
        data class SetSpeed(val speed: Float) : NavigationAction()
        data object ToggleCinema : NavigationAction()
    }

    fun loadUrl(url: String) {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return
        val target = if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            if (trimmed.contains(".") && !trimmed.contains(" ")) {
                "https://$trimmed"
            } else {
                settings.value.defaultSearchEngine.buildSearchUrl(trimmed)
            }
        } else {
            trimmed
        }
        _currentUrl.value = target
        _blockedAdsOnPage.value = 0
        _navigationAction.value = NavigationAction.LoadUrl(target)
        _activeSheet.value = ActiveSheet.NONE
        _clipboardUrl.value = null
    }

    fun onSearchSubmit(query: String, searchEngine: SearchEngine? = null) {
        val engine = searchEngine ?: settings.value.defaultSearchEngine
        val target = engine.buildSearchUrl(query)
        loadUrl(target)
    }

    fun goHome() {
        _currentUrl.value = ""
        _pageTitle.value = "Serchwebview"
        _blockedAdsOnPage.value = 0
        _activeSheet.value = ActiveSheet.NONE
    }

    fun goBack() {
        if (_currentUrl.value.isNotEmpty()) {
            if (_canGoBack.value) {
                _navigationAction.value = NavigationAction.GoBack
            } else {
                goHome()
            }
        }
    }

    fun goForward() {
        if (_canGoForward.value) {
            _navigationAction.value = NavigationAction.GoForward
        }
    }

    fun reload() {
        _navigationAction.value = NavigationAction.Reload
    }

    fun stopLoading() {
        _navigationAction.value = NavigationAction.Stop
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        _navigationAction.value = NavigationAction.SetSpeed(speed)
    }

    fun toggleCinemaMode() {
        _navigationAction.value = NavigationAction.ToggleCinema
    }

    fun clearNavigationAction() {
        _navigationAction.value = null
    }

    fun onPageStarted(url: String) {
        _currentUrl.value = url
        _isLoading.value = true
        _loadingProgress.value = 10
        _blockedAdsOnPage.value = 0
    }

    fun onPageFinished(url: String, title: String?) {
        _currentUrl.value = url
        _pageTitle.value = if (!title.isNullOrBlank()) title else url
        _isLoading.value = false
        _loadingProgress.value = 100
        if (url.isNotBlank() && !url.startsWith("about:")) {
            repository.addHistoryItem(_pageTitle.value, url)
        }
    }

    fun onProgressChanged(progress: Int) {
        _loadingProgress.value = progress
        if (progress >= 100) {
            _isLoading.value = false
        }
    }

    fun updateNavigationState(canBack: Boolean, canForward: Boolean) {
        _canGoBack.value = canBack
        _canGoForward.value = canForward
    }

    fun onAdBlocked() {
        _blockedAdsOnPage.value += 1
        repository.incrementBlockedAds(1)
    }

    fun setActiveSheet(sheet: ActiveSheet) {
        _activeSheet.value = sheet
    }

    fun closeSheet() {
        _activeSheet.value = ActiveSheet.NONE
    }

    fun setCustomView(view: View, callback: WebChromeClient.CustomViewCallback) {
        _customViewData.value = CustomViewData(view, callback)
    }

    fun hideCustomView() {
        _customViewData.value?.callback?.onCustomViewHidden()
        _customViewData.value = null
    }

    fun toggleBookmark() {
        val url = _currentUrl.value
        if (url.isBlank()) return
        if (repository.isBookmarked(url)) {
            val existing = bookmarks.value.find { it.url == url }
            if (existing != null) {
                repository.removeBookmark(existing.id)
            }
        } else {
            repository.addBookmark(_pageTitle.value, url)
        }
    }

    fun removeBookmark(id: String) {
        repository.removeBookmark(id)
    }

    fun removeHistoryItem(id: String) {
        repository.removeHistoryItem(id)
    }

    fun clearHistory() {
        repository.clearHistory()
    }

    fun toggleDesktopMode() {
        val current = settings.value
        val updated = current.copy(desktopMode = !current.desktopMode)
        repository.updateSettings(updated)
        reload()
    }

    fun toggleAdBlock() {
        val current = settings.value
        val updated = current.copy(adBlockEnabled = !current.adBlockEnabled)
        repository.updateSettings(updated)
        reload()
    }

    fun togglePrivateBrowsing() {
        val current = settings.value
        val updated = current.copy(privateBrowsing = !current.privateBrowsing)
        repository.updateSettings(updated)
    }

    fun updateSearchEngine(engine: SearchEngine) {
        val current = settings.value
        repository.updateSettings(current.copy(defaultSearchEngine = engine))
    }

    fun setPipMode(enabled: Boolean) {
        _isPipMode.value = enabled
    }

    fun checkClipboardUrl(text: String?) {
        if (text.isNullOrBlank()) {
            _clipboardUrl.value = null
            return
        }
        val trimmed = text.trim()
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            // Check if it's a new URL and not currently playing
            if (trimmed != _currentUrl.value) {
                _clipboardUrl.value = trimmed
            }
        } else {
            _clipboardUrl.value = null
        }
    }

    fun dismissClipboardUrl() {
        _clipboardUrl.value = null
    }
}
