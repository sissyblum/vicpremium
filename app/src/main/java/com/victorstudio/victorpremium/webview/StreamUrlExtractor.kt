package com.victorstudio.victorpremium.webview

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.regex.Pattern
import kotlin.coroutines.resume

/**
 * Reconstrucción de MainActivity.extraerUrlDeWebView() de Víctor Play:
 * carga la página del canal en un WebView invisible, inyecta JS para
 * forzar el play() de los <video>/<audio> (evita el bloqueo de autoplay)
 * e intercepta las requests de red hasta encontrar un .m3u8 o .mpd.
 *
 * NOTA: el JS de "forzar play" es una reconstrucción funcional a partir
 * del string encontrado en Víctor Play; conviene comparar 1:1 contra la
 * app original antes de darlo por definitivo canal por canal.
 */
class StreamUrlExtractor(private val context: Context) {

    companion object {
        private val PATRON_STREAM = Pattern.compile(".*\\.(m3u8|mpd)(\\?.*)?$", Pattern.CASE_INSENSITIVE)

        // JS inyectado: fuerza reproducción muteada y hace click en botones
        // de "play" típicos de reproductores embebidos de terceros.
        private const val JS_FORZAR_PLAY = """
            (function(){
              try {
                var m = document.querySelectorAll('video,audio');
                for (var i=0;i<m.length;i++){
                  try { m[i].muted = true; m[i].volume = 0;
                        m[i].setAttribute('muted','muted');
                        m[i].setAttribute('playsinline','playsinline'); } catch(e){}
                }
                var b = document.querySelectorAll(".vjs-big-play-button,button[aria-label='Play'],.play-button,.player-poster");
                for (var j=0;j<b.length;j++){ try { b[j].click(); } catch(e){} }
                var v = document.querySelectorAll('video');
                for (var k=0;k<v.length;k++){
                  try { v[k].muted = true; v[k].volume = 0;
                        var q = v[k].play(); if (q && q.catch) { q.catch(function(e){}); } } catch(e){}
                }
              } catch(e) {}
            })();
        """
    }

    @SuppressLint("SetJavaScriptEnabled")
    suspend fun resolverUrlStream(paginaCanal: String, timeoutMs: Long = 12_000): String? =
        suspendCancellableCoroutine { cont ->
            val webView = WebView(context)
            webView.settings.javaScriptEnabled = true
            webView.settings.mediaPlaybackRequiresUserGesture = false

            var resuelto = false

            fun finalizar(resultado: String?) {
                if (resuelto) return
                resuelto = true
                webView.stopLoading()
                webView.destroy()
                if (cont.isActive) cont.resume(resultado)
            }

            webView.webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView,
                    request: WebResourceRequest
                ): android.webkit.WebResourceResponse? {
                    val url = request.url.toString()
                    if (PATRON_STREAM.matcher(url).matches()) {
                        finalizar(url)
                    }
                    return super.shouldInterceptRequest(view, request)
                }

                override fun onPageFinished(view: WebView, url: String) {
                    view.evaluateJavascript(JS_FORZAR_PLAY, null)
                }
            }

            cont.invokeOnCancellation { finalizar(null) }

            webView.postDelayed({ finalizar(null) }, timeoutMs)
            webView.loadUrl(paginaCanal)
        }
}
