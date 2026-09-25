package com.victorstudio.victorpremium.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victorstudio.victorpremium.databinding.ItemFilaCategoriaBinding
import com.victorstudio.victorpremium.model.Canal
import com.victorstudio.victorpremium.model.CategoriaConCanales

/**
 * Lista vertical de categorías; cada item es una fila horizontal de
 * canales (ver CanalHorizontalAdapter). Estructura 1:1 con Víctor Play.
 */
class MainVerticalAdapter(
    private var categorias: List<CategoriaConCanales>,
    private val onCanalSeleccionado: (Canal) -> Unit
) : RecyclerView.Adapter<MainVerticalAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFilaCategoriaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val binding = ItemFilaCategoriaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoria = categorias[position]
        holder.binding.textCategoriaNombre.text = categoria.nombreCategoria
        holder.binding.recyclerCanalesHorizontal.adapter =
            CanalHorizontalAdapter(categoria.canales, onCanalSeleccionado)
    }

    override fun getItemCount(): Int = categorias.size

    fun actualizar(nuevas: List<CategoriaConCanales>) {
        categorias = nuevas
        notifyDataSetChanged()
    }
}
