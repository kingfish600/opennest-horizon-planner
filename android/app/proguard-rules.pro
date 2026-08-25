# Default ProGuard/R8 rules (minification is disabled for this app).
# Keep the WebView JS bridge surface intact if one is ever added.
-keepclassmembers class com.opennest.horizon.** {
    @android.webkit.JavascriptInterface <methods>;
}
