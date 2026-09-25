package com.victorstudio.victorpremium.model

/**
 * Un canal individual dentro de una categoría.
 * Refleja la estructura que usa Víctor Play (canales.json / canales2.json).
 */
data class Canal(
    val id: String,
    val nombre: String,
    val logoUrl: String? = null,
    // URL "cruda": puede ser un enlace directo a .m3u8/.mpd, o una página
    // que hay que resolver con WebView (ver StreamUrlExtractor).
    val urlFuente: String,
    val requiereWebView: Boolean = false,
    // Datos de ClearKey si el stream viene protegido (DASH + ClearKey).
    val clearKeyId: String? = null,
    val clearKeyValue: String? = null,
    val licenseUrl: String? = null
)

/** Categoría con su lista de canales (fila horizontal en la UI). */
data class CategoriaConCanales(
    val idCategoria: String,
    val nombreCategoria: String,
    val canales: List<Canal>
)

/** Servidor alternativo (Víctor Play permite elegir servidor/fuente). */
data class ServidorIPTV(
    val id: String,
    val nombre: String,
    val baseUrl: String,
    val activo: Boolean = true,
    val prioridad: Int = 0
)

/** Evento en vivo (grilla de eventos deportivos, "CanchaTv" en Víctor Play). */
data class EventoPlano(
    val id: String,
    val titulo: String,
    val horaInicio: Long,
    val imagenUrl: String? = null,
    val canalAsociado: String
)
