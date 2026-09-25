package com.victorstudio.victorpremium.player

import android.app.Activity
import android.provider.Settings
import android.view.WindowManager
import androidx.core.content.ContextCompat
import kotlin.math.max
import kotlin.math.min

/**
 * Reconstrucción de com.brouken.player.BrightnessControl (Just Player):
 * ajusta el brillo de la pantalla de la Activity con un gesto de swipe
 * vertical en la mitad izquierda del reproductor.
 *
 * Solo aplica en celular/tablet con touchscreen; en Android TV este
 * control no se usa (no hay gesto táctil, el brillo lo maneja el TV).
 */
class BrightnessControl(private val activity: Activity) {

    private var brilloActual: Float = obtenerBrilloInicial()

    private fun obtenerBrilloInicial(): Float {
        val brilloVentana = activity.window.attributes.screenBrightness
        if (brilloVentana in 0f..1f) return brilloVentana
        return try {
            Settings.System.getInt(activity.contentResolver, Settings.System.SCREEN_BRIGHTNESS) / 255f
        } catch (e: Settings.SettingNotFoundException) {
            0.5f
        }
    }

    /** delta en [-1f, 1f]: proporción de la altura de pantalla que se deslizó. */
    fun ajustarPorGesto(delta: Float) {
        brilloActual = min(1f, max(0.02f, brilloActual - delta))
        val params = activity.window.attributes
        params.screenBrightness = brilloActual
        activity.window.attributes = params
    }

    fun brilloActualPorcentaje(): Int = (brilloActual * 100).toInt()
}
