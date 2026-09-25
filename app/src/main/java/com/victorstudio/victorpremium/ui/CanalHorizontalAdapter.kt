package com.victorstudio.victorpremium.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.victorstudio.victorpremium.databinding.ItemCanalHorizontalBinding
import com.victorstudio.victorpremium.model.Canal

class CanalHorizontalAdapter(
    private val canales: List<Canal>,
    private val onCanalSeleccionado: (Canal) -> Unit
) : RecyclerView.Adapter<CanalHorizontalAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCanalHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val binding = ItemCanalHorizontalBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val canal = canales[position]
        holder.binding.textNombreCanal.text = canal.nombre
        Glide.with(holder.binding.root)
            .load(canal.logoUrl)
            .into(holder.binding.imagenLogoCanal)

        holder.binding.root.setOnClickListener { onCanalSeleccionado(canal) }
        // isFocusable/setOnFocusChangeListener para D-pad de Android TV
        // se configuran en el layout (focusable="true", nextFocus*).
    }

    override fun getItemCount(): Int = canales.size
}
