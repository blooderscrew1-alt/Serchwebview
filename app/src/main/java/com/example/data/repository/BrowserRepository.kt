package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Bookmark
import com.example.data.model.BrowserSettings
import com.example.data.model.HistoryItem
import com.example.data.model.SearchEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class BrowserRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("serchwebview_prefs", Context.MODE_PRIVATE)

    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    private val _settings = MutableStateFlow(BrowserSettings())
    val settings: StateFlow<BrowserSettings> = _settings.asStateFlow()

    private val _totalBlockedAds = MutableStateFlow(0)
    val totalBlockedAds: StateFlow<Int> = _totalBlockedAds.asStateFlow()

    init {
        loadSettings()
        loadBookmarks()
        loadHistory()
        _totalBlockedAds.value = prefs.getInt("key_total_blocked_ads", 0)
    }

    private fun loadSettings() {
        val adBlock = prefs.getBoolean("key_adblock", true)
        val autoSkip = prefs.getBoolean("key_auto_skip", true)
        val desktop = prefs.getBoolean("key_desktop", false)
        val privateBrowsing = prefs.getBoolean("key_private", false)
        val js = prefs.getBoolean("key_js", true)
        val engineStr = prefs.getString("key_search_engine", SearchEngine.DUCKDUCKGO.name)
        val engine = try {
            SearchEngine.valueOf(engineStr ?: SearchEngine.DUCKDUCKGO.name)
        } catch (_: Exception) {
            SearchEngine.DUCKDUCKGO
        }
        _settings.value = BrowserSettings(
            adBlockEnabled = adBlock,
            autoSkipVideoAds = autoSkip,
            desktopMode = desktop,
            privateBrowsing = privateBrowsing,
            javaScriptEnabled = js,
            defaultSearchEngine = engine
        )
    }

    fun updateSettings(newSettings: BrowserSettings) {
        _settings.value = newSettings
        prefs.edit()
            .putBoolean("key_adblock", newSettings.adBlockEnabled)
            .putBoolean("key_auto_skip", newSettings.autoSkipVideoAds)
            .putBoolean("key_desktop", newSettings.desktopMode)
            .putBoolean("key_private", newSettings.privateBrowsing)
            .putBoolean("key_js", newSettings.javaScriptEnabled)
            .putString("key_search_engine", newSettings.defaultSearchEngine.name)
            .apply()
    }

    fun incrementBlockedAds(count: Int = 1) {
        val updated = _totalBlockedAds.value + count
        _totalBlockedAds.value = updated
        prefs.edit().putInt("key_total_blocked_ads", updated).apply()
    }

    private fun loadBookmarks() {
        val raw = prefs.getString("key_bookmarks", null)
        if (raw.isNullOrBlank()) {
            val defaults = listOf(
                Bookmark(title = "YouTube", url = "https://m.youtube.com", iconEmoji = "▶️", category = "Video"),
                Bookmark(title = "Twitch", url = "https://m.twitch.tv", iconEmoji = "🟣", category = "Streaming"),
                Bookmark(title = "Vimeo", url = "https://vimeo.com/watch", iconEmoji = "🎬", category = "Video"),
                Bookmark(title = "Dailymotion", url = "https://www.dailymotion.com", iconEmoji = "🎥", category = "Video"),
                Bookmark(title = "Internet Archive", url = "https://archive.org/details/movies", iconEmoji = "🏛️", category = "Movies"),
                Bookmark(title = "TED Talks", url = "https://www.ted.com/talks", iconEmoji = "💡", category = "Education"),
                Bookmark(title = "Bilibili", url = "https://m.bilibili.com", iconEmoji = "📺", category = "Video")
            )
            _bookmarks.value = defaults
            saveBookmarks(defaults)
        } else {
            try {
                val array = JSONArray(raw)
                val list = mutableListOf<Bookmark>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        Bookmark(
                            id = obj.optString("id"),
                            title = obj.optString("title"),
                            url = obj.optString("url"),
                            iconEmoji = obj.optString("iconEmoji", "🌐"),
                            category = obj.optString("category", "General"),
                            timestamp = obj.optLong("timestamp")
                        )
                    )
                }
                _bookmarks.value = list
            } catch (_: Exception) {
                _bookmarks.value = emptyList()
            }
        }
    }

    private fun saveBookmarks(list: List<Bookmark>) {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("title", item.title)
            obj.put("url", item.url)
            obj.put("iconEmoji", item.iconEmoji)
            obj.put("category", item.category)
            obj.put("timestamp", item.timestamp)
            array.put(obj)
        }
        prefs.edit().putString("key_bookmarks", array.toString()).apply()
    }

    fun addBookmark(title: String, url: String, emoji: String = "🔖", category: String = "Saved") {
        val current = _bookmarks.value.toMutableList()
        current.removeAll { it.url == url }
        current.add(0, Bookmark(title = title, url = url, iconEmoji = emoji, category = category))
        _bookmarks.value = current
        saveBookmarks(current)
    }

    fun removeBookmark(id: String) {
        val current = _bookmarks.value.filterNot { it.id == id }
        _bookmarks.value = current
        saveBookmarks(current)
    }

    fun isBookmarked(url: String): Boolean {
        return _bookmarks.value.any { it.url == url }
    }

    private fun loadHistory() {
        val raw = prefs.getString("key_history", null)
        if (!raw.isNullOrBlank()) {
            try {
                val array = JSONArray(raw)
                val list = mutableListOf<HistoryItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        HistoryItem(
                            id = obj.optString("id"),
                            title = obj.optString("title"),
                            url = obj.optString("url"),
                            timestamp = obj.optLong("timestamp")
                        )
                    )
                }
                _history.value = list
            } catch (_: Exception) {
                _history.value = emptyList()
            }
        }
    }

    fun addHistoryItem(title: String, url: String) {
        if (_settings.value.privateBrowsing) return // Do not record history in private mode
        if (url.isBlank() || url.startsWith("about:") || url.startsWith("chrome:")) return

        val current = _history.value.toMutableList()
        current.removeAll { it.url == url }
        current.add(0, HistoryItem(title = if (title.isBlank()) url else title, url = url))
        // Cap history at 100 items for lightweight storage
        val trimmed = if (current.size > 100) current.subList(0, 100) else current
        _history.value = trimmed

        val array = JSONArray()
        for (item in trimmed) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("title", item.title)
            obj.put("url", item.url)
            obj.put("timestamp", item.timestamp)
            array.put(obj)
        }
        prefs.edit().putString("key_history", array.toString()).apply()
    }

    fun clearHistory() {
        _history.value = emptyList()
        prefs.edit().remove("key_history").apply()
    }

    fun removeHistoryItem(id: String) {
        val current = _history.value.filterNot { it.id == id }
        _history.value = current
        val array = JSONArray()
        for (item in current) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("title", item.title)
            obj.put("url", item.url)
            obj.put("timestamp", item.timestamp)
            array.put(obj)
        }
        prefs.edit().putString("key_history", array.toString()).apply()
    }
}
