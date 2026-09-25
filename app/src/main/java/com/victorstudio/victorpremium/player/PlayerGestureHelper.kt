package com.victorstudio.victorpremium.player

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

/**
 * Junta los gestos que trae Just Player y que Víctor Play no tiene:
 *  - swipe vertical mitad izquierda -> brillo
 *  - swipe vertical mitad derecha   -> volumen
 *  - doble tap izquierda/derecha    -> retroceder/avanzar 10s
 *  - pinch                          -> zoom (aspect ratio ZOOM vs FIT)
 *
 * Se engancha sobre el PlayerView existente sin tocar su UI de controles.
 */
@UnstableApi
class PlayerGestureHelper(
    private val activity: Activity,
    private val playerView: PlayerView,
    private val player: ExoPlayer
) {
    private val brightnessControl = BrightnessControl(activity)
    private val audioManager =
        activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var zoomActivo = false

    private val scaleDetector = ScaleGestureDetector(
        activity,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScaleEnd(detector: ScaleGestureDetector) {
                zoomActivo = detector.scaleFactor > 1f
                playerView.resizeMode = if (zoomActivo) {
                    AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                } else {
                    AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            }
        }
    )

    private val gestureDetector = GestureDetector(
        activity,
        object : GestureDetector.SimpleOnGestureListener() {

            override fun onDoubleTap(e: MotionEvent): Boolean {
                val mitad = playerView.width / 2
                if (e.x < mitad) {
                    player.seekBack()
                } else {
                    player.seekForward()
                }
                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (e1 == null) return false
                val alto = playerView.height.coerceAtLeast(1)
                val delta = distanceY / alto // positivo = deslizó hacia arriba

                val mitad = playerView.width / 2
                if (e1.x < mitad) {
                    brightnessControl.ajustarPorGesto(delta)
                } else {
                    ajustarVolumenPorGesto(delta)
                }
                return true
            }
        }
    )

    private fun ajustarVolumenPorGesto(delta: Float) {
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val actual = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val nuevo = (actual - (delta * max)).toInt().coerceIn(0, max)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, nuevo, 0)
    }

    /** Llamar desde el onTouchListener del contenedor del reproductor. */
    fun onTouchEvent(view: View, event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        return gestureDetector.onTouchEvent(event)
    }
}
