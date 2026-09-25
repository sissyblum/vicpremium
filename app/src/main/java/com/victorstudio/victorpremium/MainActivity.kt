package com.victorstudio.victorpremium

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.victorstudio.victorpremium.databinding.ActivityMainBinding
import com.victorstudio.victorpremium.firebase.ServerConfigListener
import com.victorstudio.victorpremium.model.Canal
import com.victorstudio.victorpremium.model.CategoriaConCanales
import com.victorstudio.victorpremium.network.IptvRepository
import com.victorstudio.victorpremium.player.PlayerFactory
import com.victorstudio.victorpremium.player.PlayerGestureHelper
import com.victorstudio.victorpremium.ui.MainVerticalAdapter
import com.victorstudio.victorpremium.webview.StreamUrlExtractor
import kotlinx.coroutines.launch

@UnstableApi
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val repository = IptvRepository()
    private lateinit var adapterCategorias: MainVerticalAdapter
    private var categoriasActuales: List<CategoriaConCanales> = emptyList()

    private var player: androidx.media3.exoplayer.ExoPlayer? = null
    private var gestureHelper: PlayerGestureHelper? = null
    private lateinit var serverConfigListener: ServerConfigListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapterCategorias = MainVerticalAdapter(emptyList()) { canal -> reproducirCanal(canal) }
        binding.recyclerCategorias.layoutManager = LinearLayoutManager(this)
        binding.recyclerCategorias.adapter = adapterCategorias

        serverConfigListener = ServerConfigListener { servidores ->
            // TODO: refrescar layout_selector_servidores con la lista nueva
        }
        serverConfigListener.iniciar()

        cargarCanales()
    }

    private fun cargarCanales() {
        lifecycleScope.launch {
            val categorias = repository.obtenerCanalesAmbos()
            categoriasActuales = categorias
            adapterCategorias.actualizar(categorias)
        }
    }

    private fun reproducirCanal(canal: Canal) {
        lifecycleScope.launch {
            val canalResuelto = if (canal.requiereWebView) {
                val urlReal = StreamUrlExtractor(this@MainActivity)
                    .resolverUrlStream(canal.urlFuente)
                if (urlReal != null) canal.copy(urlFuente = urlReal) else canal
            } else canal

            iniciarPlayer(canalResuelto)
        }
    }

    private fun iniciarPlayer(canal: Canal) {
        liberarPlayer()

        val nuevoPlayer = PlayerFactory.crearPlayer(this)
        binding.playerView.player = nuevoPlayer
        nuevoPlayer.setMediaItem(PlayerFactory.crearMediaItem(canal))
        nuevoPlayer.prepare()
        nuevoPlayer.playWhenReady = true

        gestureHelper = PlayerGestureHelper(this, binding.playerView, nuevoPlayer)
        binding.playerView.setOnTouchListener { view, event ->
            gestureHelper?.onTouchEvent(view, event) ?: false
        }

        player = nuevoPlayer
    }

    private fun liberarPlayer() {
        player?.release()
        player = null
    }

    /**
     * Navegación por control remoto pedida en Víctor Play:
     * subir/bajar canal en los extremos de una categoría pasa a la
     * categoría siguiente/anterior (en vez de quedarse en el borde),
     * y al volver de la reproducción el foco queda en el canal que
     * se estaba viendo.
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // TODO: portar aquí la lógica exacta de foco entre
        // MainVerticalAdapter / CanalHorizontalAdapter usando
        // FocusFinder + View.requestFocus(), guardando la posición
        // del último canal reproducido para restaurar el foco al volver.
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        liberarPlayer()
        serverConfigListener.detener()
    }
}
