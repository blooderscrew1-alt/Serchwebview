package com.example.data.model

enum class SearchEngine(
    val displayName: String,
    val searchUrlPrefix: String,
    val iconName: String
) {
    DUCKDUCKGO(
        displayName = "DuckDuckGo",
        searchUrlPrefix = "https://duckduckgo.com/?q=",
        iconName = "🦆"
    ),
    GOOGLE(
        displayName = "Google",
        searchUrlPrefix = "https://www.google.com/search?q=",
        iconName = "🔍"
    ),
    YOUTUBE(
        displayName = "YouTube",
        searchUrlPrefix = "https://m.youtube.com/results?search_query=",
        iconName = "▶️"
    ),
    BRAVE(
        displayName = "Brave",
        searchUrlPrefix = "https://search.brave.com/search?q=",
        iconName = "🦁"
    ),
    BING(
        displayName = "Bing",
        searchUrlPrefix = "https://www.bing.com/search?q=",
        iconName = "🌐"
    );

    fun buildSearchUrl(query: String): String {
        val trimmed = query.trim()
        return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else if (trimmed.contains(".") && !trimmed.contains(" ") && trimmed.length > 3) {
            "https://$trimmed"
        } else {
            "$searchUrlPrefix${java.net.URLEncoder.encode(trimmed, "UTF-8")}"
        }
    }
}
