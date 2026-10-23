# Serchwebview

Ultra-lightweight, compact Android web browser built with Kotlin and Jetpack Compose, designed specifically for opening and streaming video links without ads.

## Features

- **Ad & Tracker Blocker**: Built-in multi-stage ad-blocking engine that intercepts ad delivery requests (DoubleClick, Google AdServices, Taboola, popups, and tracker scripts) and injects element-hiding CSS.
- **Video Pre-roll Auto-Skipper**: Automatically fast-forwards and clicks "Skip Ad" buttons on video platforms.
- **Picture-in-Picture (PiP)**: Watch videos in a floating mini-window while multitasking across other apps.
- **HTML5 Fullscreen Video**: Seamless native Android fullscreen video playback support via custom WebChromeClient.
- **Video Controls & Playback Speed**: Integrated video controller to adjust playback rates (0.5x, 0.75x, 1.0x, 1.25x, 1.5x, 2.0x) and activate Cinema Mode.
- **Clipboard Video Link Sniffer**: Automatically detects video URLs from your clipboard on app launch and provides an instant 1-tap "Play Ad-Free" launcher.
- **System Share & URL Intent Integration**: Set Serchwebview as your default video link handler or share video links directly from YouTube, Twitter/X, Reddit, or other apps via Android's Share menu.
- **Desktop & Mobile Site Toggle**: Switch user-agents on the fly to bypass mobile video limitations.
- **Search Engine Selector**: Instant search switching between DuckDuckGo, Google, YouTube, Brave, and Bing.
- **Bookmarks & History**: Fast, lightweight management of favorite video portals and browsing history with one-tap clearing.
- **Private Browsing Mode**: Browse without saving local cache or history records.

## Architecture

- **UI Framework**: Modern Jetpack Compose with Material Design 3 (M3) edge-to-edge support.
- **Architecture Pattern**: MVVM (Model-View-ViewModel) with Kotlin Coroutines and StateFlow.
- **Web Engine**: Android WebView with custom `WebViewClient` request interceptor and `WebChromeClient` custom view handlers.
