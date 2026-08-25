package com.opennest.horizon

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File

/**
 * Hosts the fully-offline OpenNest Horizon Planner single-page app from assets.
 *
 * All content is served from file:///android_asset/, so the app needs no
 * network permission. localStorage/DOM storage persist planner state between
 * launches, and hardware acceleration keeps Chart.js canvas rendering fluid.
 *
 * Two native bridges make the Data tab work like a real app:
 *  - window.OpenNestNative.saveFile(name, base64) → writes exports (JSON plan
 *    snapshots, ledger CSVs) into the device's Downloads folder.
 *  - onShowFileChooser → powers the "Import snapshot" file picker.
 */
class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private lateinit var fileChooserLauncher: ActivityResultLauncher<Intent>

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hardware acceleration at the window level (belt-and-braces with the
        // manifest flag) for smooth Canvas 2D / Chart.js animation.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )

        setContentView(R.layout.activity_main)

        fileChooserLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val callback = filePathCallback
                filePathCallback = null
                val picked = if (result.resultCode == RESULT_OK) {
                    WebChromeClient.FileChooserParams.parseResult(result.resultCode, result.data)
                } else null
                callback?.onReceiveValue(picked ?: arrayOf())
            }

        webView = findViewById(R.id.webview)
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                callback: ValueCallback<Array<Uri>>,
                params: FileChooserParams
            ): Boolean {
                // A picker is already open — cancel it before starting another.
                filePathCallback?.onReceiveValue(arrayOf())
                filePathCallback = callback
                return try {
                    fileChooserLauncher.launch(params.createIntent())
                    true
                } catch (e: Exception) {
                    filePathCallback = null
                    toast("No file picker available on this device.")
                    false
                }
            }
        }

        configureSettings()
        webView.addJavascriptInterface(NativeBridge(), "OpenNestNative")

        // Android back button walks the WebView history (in-app tabs/history),
        // and only leaves the app when there is nothing left to go back to.
        val backCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) webView.goBack()
            }
        }
        onBackPressedDispatcher.addCallback(this, backCallback)
        webView.webViewClient = object : WebViewClient() {
            override fun doUpdateVisitedHistory(
                view: WebView,
                url: String?,
                isReload: Boolean
            ) {
                backCallback.isEnabled = !isReload && view.canGoBack()
                super.doUpdateVisitedHistory(view, url, isReload)
            }
        }

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState)
        } else {
            webView.loadUrl(BASE_URL)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureSettings() {
        webView.settings.apply {
            // Required for the SPA itself.
            javaScriptEnabled = true

            // localStorage / IndexedDB-style persistence so the planner saves
            // scenarios offline. databaseEnabled covers the older WebSQL-backed
            // storage path some OEM WebViews still fall back to.
            domStorageEnabled = true
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT

            // Content is a local asset bundle; keep viewport/zoom behavior
            // driven by the HTML meta viewport tag.
            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = false
            displayZoomControls = false

            // Never leak traffic: everything must resolve locally.
            allowFileAccess = true
            allowContentAccess = false
            mediaPlaybackRequiresUserGesture = false
        }
    }

    /**
     * Exposed to JS as window.OpenNestNative.saveFile(fileName, base64).
     * Writes into the shared Downloads collection (no storage permission
     * needed on API 29+). On API 26–28 it prefers the public Downloads
     * directory when legacy write permission was granted, and otherwise
     * falls back to this app's own external files directory.
     */
    private inner class NativeBridge {
        /**
         * Exposed to JS as window.OpenNestNative.printPage().
         * Opens the Android system print dialog over the current page;
         * its built-in "Save as PDF" destination exports a real PDF file.
         */
        @JavascriptInterface
        fun printPage() {
            runOnUiThread {
                try {
                    val printManager = applicationContext.getSystemService(PRINT_SERVICE) as android.print.PrintManager
                    val adapter = webView.createPrintDocumentAdapter("OpenNest-Horizon-Report")
                    printManager.print("OpenNest Horizon Report", adapter, android.print.PrintAttributes.Builder().build())
                } catch (e: Exception) {
                    toast("Printing unavailable: ${e.message}")
                }
            }
        }

        @JavascriptInterface
        fun saveFile(fileName: String, base64Data: String): Boolean {
            val safeName = fileName.replace(Regex("[^A-Za-z0-9._ ()+-]"), "_").ifBlank { "export.bin" }
            val bytes = try {
                Base64.decode(base64Data, Base64.DEFAULT)
            } catch (e: IllegalArgumentException) {
                return false
            }
            return try {
                val location = writeToDownloads(safeName, bytes)
                toast("Saved to $location")
                true
            } catch (e: Exception) {
                toast("Export failed: ${e.message}")
                false
            }
        }

        private fun writeToDownloads(name: String, bytes: ByteArray): String {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = applicationContext.contentResolver
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeFor(name))
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: throw IllegalStateException("Download provider rejected the file")
                resolver.openOutputStream(uri)?.use { it.write(bytes) }
                    ?: throw IllegalStateException("Could not open output stream")
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                return "Downloads/$name"
            }

            // Legacy path (API 26–28).
            val legacyPublic = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val granted = checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
            val dir = if (granted && legacyPublic != null) {
                legacyPublic.apply { mkdirs() }
            } else {
                // App-specific dir needs no permission on any API level.
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    ?: getExternalFilesDir(null)
                    ?: filesDir
            }
            val out = File(dir, name)
            out.writeBytes(bytes)
            return out.absolutePath
        }

        private fun mimeFor(name: String): String = when {
            name.endsWith(".json", ignoreCase = true) -> "application/json"
            name.endsWith(".csv", ignoreCase = true) -> "text/csv"
            else -> "application/octet-stream"
        }
    }

    private fun toast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }

    companion object {
        private const val BASE_URL = "file:///android_asset/index.html"
    }
}
