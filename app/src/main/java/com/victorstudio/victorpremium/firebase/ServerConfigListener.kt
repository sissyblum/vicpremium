package com.victorstudio.victorpremium.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.victorstudio.victorpremium.model.ServidorIPTV

/**
 * Equivalente a escucharServidoresFirebase() de Víctor Play: escucha en
 * tiempo real el nodo de "servidores" para poder activar/desactivar o
 * reordenar fuentes sin tener que actualizar la app.
 */
class ServerConfigListener(
    private val nodo: String = "servidores",
    private val onServidoresActualizados: (List<ServidorIPTV>) -> Unit
) {
    private val ref = FirebaseDatabase.getInstance().getReference(nodo)
    private var listener: ValueEventListener? = null

    fun iniciar() {
        val l = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val servidores = snapshot.children.mapNotNull {
                    it.getValue(ServidorIPTV::class.java)
                }
                onServidoresActualizados(servidores.sortedByDescending { it.prioridad })
            }

            override fun onCancelled(error: DatabaseError) {
                // Silencioso: si Firebase falla, se sigue con la última
                // lista de servidores conocida (no se rompe la app).
            }
        }
        ref.addValueEventListener(l)
        listener = l
    }

    fun detener() {
        listener?.let { ref.removeEventListener(it) }
        listener = null
    }
}
