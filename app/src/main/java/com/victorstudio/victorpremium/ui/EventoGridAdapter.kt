package com.victorstudio.victorpremium.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.victorstudio.victorpremium.databinding.ItemEventoGridBinding
import com.victorstudio.victorpremium.model.EventoPlano

class EventoGridAdapter(
    private val eventos: List<EventoPlano>,
    private val onEventoSeleccionado: (EventoPlano) -> Unit
) : RecyclerView.Adapter<EventoGridAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemEventoGridBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val binding = ItemEventoGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evento = eventos[position]
        holder.binding.textTituloEvento.text = evento.titulo
        Glide.with(holder.binding.root).load(evento.imagenUrl).into(holder.binding.imagenEvento)
        holder.binding.root.setOnClickListener { onEventoSeleccionado(evento) }
    }

    override fun getItemCount(): Int = eventos.size
}
