package com.victorstudio.victorpremium.network

import com.victorstudio.victorpremium.model.CategoriaConCanales
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Equivalente a IptvRepository de Víctor Play: trae ambos JSON de canales
 * en paralelo (cargarCanalesAmbos) y los combina en una sola lista de
 * categorías. Si un servidor falla, no tira abajo al otro.
 */
class IptvRepository(private val api: ApiService = RetrofitClient.apiService) {

    suspend fun obtenerCanalesAmbos(): List<CategoriaConCanales> = coroutineScope {
        val fuente1 = async { runCatching { api.obtenerCanales(ApiService.URL_CANALES_1) } }
        val fuente2 = async { runCatching { api.obtenerCanales(ApiService.URL_CANALES_2) } }

        val listas = listOf(fuente1.await(), fuente2.await())
            .mapNotNull { it.getOrNull() }

        combinarPorCategoria(listas.flatten())
    }

    /** Si dos fuentes traen la misma categoría, se fusionan los canales. */
    private fun combinarPorCategoria(
        categorias: List<CategoriaConCanales>
    ): List<CategoriaConCanales> {
        return categorias
            .groupBy { it.idCategoria }
            .map { (id, grupo) ->
                CategoriaConCanales(
                    idCategoria = id,
                    nombreCategoria = grupo.first().nombreCategoria,
                    canales = grupo.flatMap { it.canales }.distinctBy { it.id }
                )
            }
    }
}
