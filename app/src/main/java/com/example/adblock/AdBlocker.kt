package com.example.adblock

import android.net.Uri
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.Locale

object AdBlocker {

    private val BLOCKED_HOST_SUFFIXES = hashSetOf(
        "doubleclick.net",
        "googleadservices.com",
        "googlesyndication.com",
        "adservice.google.com",
        "adcolony.com",
        "applovin.com",
        "unityads.unity3d.com",
        "vungle.com",
        "taboola.com",
        "outbrain.com",
        "popads.net",
        "popcash.net",
        "propellerads.com",
        "scorecardresearch.com",
        "quantserve.com",
        "adroll.com",
        "criteo.com",
        "rubiconproject.com",
        "pubmatic.com",
        "openx.net",
        "advertising.com",
        "adnxs.com",
        "admob.com",
        "exponential.com",
        "chartbeat.com",
        "clicksor.com",
        "infolinks.com",
        "buysellads.com",
        "revcontent.com",
        "zergnet.com",
        "mgid.com",
        "yieldmo.com",
        "smartadserver.com",
        "teads.tv",
        "casale-media.com",
        "bidswitch.net",
        "spotxchange.com",
        "lijit.com",
        "sovrn.com",
        "adblade.com",
        "content.ad",
        "disqusads.com",
        "amazon-adsystem.com",
        "flashtalking.com",
        "exponential.com",
        "advertising.amazon.com",
        "adsafeprotected.com",
        "moatads.com",
        "serving-sys.com",
        "adform.net",
        "fastclick.net",
        "trafficjunky.com",
        "exoclick.com",
        "juicyads.com",
        "adxcore.ch",
        "adtrue.com"
    )

    private val BLOCKED_KEYWORDS = listOf(
        "/googleads/",
        "/pagead/",
        "/ad_status.js",
        "/ads/api/",
        "api/stats/ads",
        "/doubleclick/",
        "/adserver/",
        "/ads.js",
        "/ad-banner",
        "/banner_ad",
        "/popunder",
        "video-ads",
        "/pre-roll-ad"
    )

    fun isAdUrl(url: String): Boolean {
        if (url.isBlank()) return false
        val uri = try {
            Uri.parse(url)
        } catch (_: Exception) {
            return false
        }

        val host = uri.host?.lowercase(Locale.ROOT) ?: return false

        // Check if host matches any blocked domain suffix
        for (suffix in BLOCKED_HOST_SUFFIXES) {
            if (host == suffix || host.endsWith(".$suffix")) {
                return true
            }
        }

        // Check if url path contains known ad endpoints
        val lowerUrl = url.lowercase(Locale.ROOT)
        for (keyword in BLOCKED_KEYWORDS) {
            if (lowerUrl.contains(keyword)) {
                return true
            }
        }

        return false
    }

    fun createEmptyResponse(): InputStream {
        return ByteArrayInputStream(ByteArray(0))
    }

    // CSS to hide banners, overlays, popup blockers, and promotional video overlays
    val AD_HIDING_CSS: String = """
        [id*="ad-"], [id*="ad_"], [id*="ads-"], [id*="ads_"],
        [class*="ad-"], [class*="ad_"], [class*="ads-"], [class*="ads_"],
        .ytp-ad-module, .ytp-ad-overlay-container, .ytp-ad-player-overlay,
        .video-ads, .ad-container, .ad-wrapper, .ad-unit, .adsbygoogle,
        iframe[src*="doubleclick"], iframe[src*="googleadservices"], iframe[src*="ads"],
        .taboola, .outbrain, .sponsor-box, .promoted-content,
        #cookie-notice, #cookie-banner, .cookie-consent-banner,
        .videoAdUi, .videoAdUiSkipButton {
            display: none !important;
            visibility: hidden !important;
            height: 0 !important;
            width: 0 !important;
            opacity: 0 !important;
            pointer-events: none !important;
        }
    """.trimIndent().replace("\n", " ")

    // JavaScript to inject CSS & automatically skip video ads
    val VIDEO_AD_SKIPPER_SCRIPT: String = """
        (function() {
            // 1. Inject ad-hiding CSS
            try {
                if (!document.getElementById('serch-adblock-style')) {
                    var style = document.createElement('style');
                    style.id = 'serch-adblock-style';
                    style.textContent = '$AD_HIDING_CSS';
                    (document.head || document.documentElement).appendChild(style);
                }
            } catch(e) {}

            // 2. Video ad auto-skip & fast forward loop
            function cleanVideoAds() {
                try {
                    // Click YouTube / video skip buttons
                    var skipButtons = document.querySelectorAll(
                        '.ytp-ad-skip-button, .ytp-ad-skip-button-modern, .ytp-skip-ad-button, .videoAdUiSkipButton, [class*="skip-button"], button[id*="skip"]'
                    );
                    for (var i = 0; i < skipButtons.length; i++) {
                        skipButtons[i].click();
                    }

                    // Check if video is an ad and skip to end
                    var isAdShowing = document.querySelector('.ad-showing, .ytp-ad-player-overlay, .video-ads .ad-container');
                    if (isAdShowing) {
                        var videos = document.querySelectorAll('video');
                        for (var v = 0; v < videos.length; v++) {
                            var video = videos[v];
                            if (video && !video.paused && video.duration > 0) {
                                video.currentTime = video.duration;
                            }
                        }
                    }

                    // Close ad overlay popups
                    var overlayCloses = document.querySelectorAll('.ytp-ad-overlay-close-button, [class*="ad-close"]');
                    for (var c = 0; c < overlayCloses.length; c++) {
                        overlayCloses[c].click();
                    }
                } catch(e) {}
            }

            // Run immediately and every 500ms
            cleanVideoAds();
            if (!window.__serchAdBlockInterval) {
                window.__serchAdBlockInterval = setInterval(cleanVideoAds, 500);
            }
        })();
    """.trimIndent()

    fun getPlaybackSpeedScript(speed: Float): String {
        return """
            (function() {
                var videos = document.querySelectorAll('video');
                for (var i = 0; i < videos.length; i++) {
                    videos[i].playbackRate = $speed;
                }
            })();
        """.trimIndent()
    }

    val CINEMA_MODE_SCRIPT: String = """
        (function() {
            try {
                var video = document.querySelector('video');
                if (video) {
                    if (document.fullscreenElement) {
                        document.exitFullscreen();
                    } else if (video.requestFullscreen) {
                        video.requestFullscreen();
                    } else if (video.webkitRequestFullscreen) {
                        video.webkitRequestFullscreen();
                    }
                }
            } catch(e) {}
        })();
    """.trimIndent()
}
