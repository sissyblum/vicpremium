package com.victorstudio.victorpremium.network

import com.victorstudio.victorpremium.model.CategoriaConCanales
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Igual que en Víctor Play: los canales no vienen de un backend propio,
 * sino de archivos JSON estáticos (hoy alojados en Dropbox). Se dejan
 * las URLs como constantes fáciles de cambiar si mudás el hosting.
 */
interface ApiService {

    @GET
    suspend fun obtenerCanales(@Url url: String): List<CategoriaConCanales>

    companion object {
        // TODO: confirmar si seguís usando los mismos links de Dropbox
        // o si para "Víctor Premium" vas a mover esto a tu propio hosting
        // (por ejemplo, el mismo Firebase Storage que ya usás en tu
        // app-store de Android TV).
        const val URL_CANALES_1 =
            "https://www.dropbox.com/scl/fi/zvi0u0yh7rp0lavnn5jic/canales.json?rlkey=lhigl3l6elznet6rvfm7nwz8y&st=2vd749mp&dl=1"
        const val URL_CANALES_2 =
            "https://www.dropbox.com/scl/fi/c2nkbg90267ri8gxltrg9/canales2.json?rlkey=qnop2hcswbnmvtd6r267j2q0d&st=ogo85e4r&dl=1"
    }
}
