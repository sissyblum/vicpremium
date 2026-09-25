package com.victorstudio.victorpremium.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victorstudio.victorpremium.databinding.ItemServidorBinding
import com.victorstudio.victorpremium.model.ServidorIPTV

class ServidoresAdapter(
    private val servidores: List<ServidorIPTV>,
    private val onServidorSeleccionado: (ServidorIPTV) -> Unit
) : RecyclerView.Adapter<ServidoresAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemServidorBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val binding = ItemServidorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val servidor = servidores[position]
        holder.binding.textNombreServidor.text = servidor.nombre
        holder.binding.root.isEnabled = servidor.activo
        holder.binding.root.alpha = if (servidor.activo) 1f else 0.4f
        holder.binding.root.setOnClickListener {
            if (servidor.activo) onServidorSeleccionado(servidor)
        }
    }

    override fun getItemCount(): Int = servidores.size
}
