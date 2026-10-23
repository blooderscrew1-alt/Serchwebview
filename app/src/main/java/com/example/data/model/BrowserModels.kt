package com.example.data.model

data class Bookmark(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val iconEmoji: String = "🌐",
    val category: String = "General",
    val timestamp: Long = System.currentTimeMillis()
)

data class HistoryItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class BrowserSettings(
    val adBlockEnabled: Boolean = true,
    val autoSkipVideoAds: Boolean = true,
    val desktopMode: Boolean = false,
    val privateBrowsing: Boolean = false,
    val javaScriptEnabled: Boolean = true,
    val defaultSearchEngine: SearchEngine = SearchEngine.DUCKDUCKGO,
    val autoPiPEnabled: Boolean = true
)
