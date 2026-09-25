package com.victorstudio.victorpremium.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.victorstudio.victorpremium.model.Canal

/**
 * Crea el ExoPlayer y el MediaItem con la misma configuración de DRM
 * ClearKey que usa Víctor Play (MainActivity.setupPlayer / <LA_URL>).
 */
@UnstableApi
object PlayerFactory {

    fun crearPlayer(context: Context): ExoPlayer =
        ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(10_000)
            .setSeekForwardIncrementMs(10_000)
            .build()

    fun crearMediaItem(canal: Canal): MediaItem {
        val builder = MediaItem.Builder()
            .setMediaId(canal.id)
            .setUri(canal.urlFuente)

        if (!canal.clearKeyId.isNullOrBlank() && !canal.clearKeyValue.isNullOrBlank()) {
            // ClearKey vía licenseUrl (equivalente al <LA_URL> de Víctor Play),
            // o embebido si el manifest DASH no lo resuelve solo.
            val drmConfig = MediaItem.DrmConfiguration
                .Builder(androidx.media3.common.C.CLEARKEY_UUID)
                .setLicenseUri(canal.licenseUrl)
                .setMultiSession(false)
                .build()
            builder.setDrmConfiguration(drmConfig)
        }

        return builder.build()
    }
}
