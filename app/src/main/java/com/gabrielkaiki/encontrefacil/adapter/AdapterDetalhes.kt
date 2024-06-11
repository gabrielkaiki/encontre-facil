package com.gabrielkaiki.encontrefacil.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.gabrielkaiki.encontrefacil.R
import com.gabrielkaiki.encontrefacil.models.Local
import com.gabrielkaiki.encontrefacil.utils.calcularDistancia
import com.gabrielkaiki.encontrefacil.utils.formatarDistancia
import com.gabrielkaiki.encontrefacil.utils.localUsuarioAtual
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class AdapterDetalhes(var listaLocais: ArrayList<Local>, var context: Context) :
    RecyclerView.Adapter<AdapterDetalhes.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nome: TextView = itemView.findViewById(R.id.nome_adapter)
        var endereco: TextView = itemView.findViewById(R.id.endereco_adapter)
        var distancia: TextView = itemView.findViewById(R.id.distancia_adapter)
        var disponibilidade: TextView = itemView.findViewById(R.id.abertoFechado)
        var img_local: ImageView = itemView.findViewById(R.id.imagem_local_adapter)
        var progress_bar: ProgressBar = itemView.findViewById(R.id.progressBar_adapter)
        var fab_route: FloatingActionButton = itemView.findViewById(R.id.fabRoute)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_detalhes, parent, false)
        return ViewHolder(item)
    }

    override fun getItemCount(): Int {
        return listaLocais.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        resetValues(holder)

        val local = listaLocais[position]

        //Nome do local
        holder.nome.text = local.name
        holder.nome.typeface = ResourcesCompat.getFont(context, R.font.fonte)
        holder.endereco.text = local.vicinity

        //Coordenadas
        val coordenadasLocal = LatLng(
            local.geometry!!.location!!.lat!!.toDouble(),
            local.geometry!!.location!!.lng!!.toDouble()
        )
        val latLng = "${coordenadasLocal.latitude},${coordenadasLocal.longitude}"

        //Rota
        holder.fab_route.setOnClickListener {
            val uri = Uri.parse("google.navigation:q=<$latLng>&mode=d")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            context.startActivity(intent)
        }

        //Calculo de distância
        val distanciaUsuarioDestino = calcularDistancia(localUsuarioAtual!!, coordenadasLocal)
        holder.distancia.text = formatarDistancia(distanciaUsuarioDestino)

        carregaFoto(holder, local)
        verificaSeAberto(holder, local)
    }

    private fun resetValues(holder: ViewHolder) {
        holder.progress_bar.visibility = View.GONE
        holder.img_local.setImageResource(R.drawable.construcao)
        holder.distancia.text = "? M"
        holder.endereco.text = "Endereço não informado"
        holder.nome.text = "Nome não informado"
    }

    private fun carregaFoto(holder: ViewHolder, local: Local) {
        val semFotos = local.photos.size == 0
        if (!semFotos) {
            holder.progress_bar.visibility = View.VISIBLE
            val url =
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&key=AIzaSyDBaSfV2vGuPPLLkIFK67eZQjvUsmivBnw&photo_reference="
            val urlCompleta = url + local.photos[0].photo_reference
            Picasso.get().load(Uri.parse(urlCompleta)).into(holder.img_local, object : Callback {
                override fun onSuccess() {
                    holder.progress_bar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    holder.progress_bar.visibility = View.GONE
                }
            })
        }
    }

    private fun verificaSeAberto(holder: ViewHolder, local: Local) {
        val estaAberto = local.opening_hours?.open_now
        if (estaAberto != null) {
            if (estaAberto) {
                holder.disponibilidade.text = "Aberto"
                holder.disponibilidade.setTextColor(Color.rgb(14, 209, 69))
                holder.disponibilidade.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.door_open,
                    0,
                    0,
                    0
                )
            } else {
                holder.disponibilidade.text = "Fechado"
                holder.disponibilidade.setTextColor(Color.RED)
                holder.disponibilidade.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.door_closed,
                    0,
                    0,
                    0
                )
            }
        } else {
            holder.disponibilidade.text = "Horário de funcionamento não informado"
            holder.disponibilidade.setTextColor(Color.rgb(14, 209, 69))
            holder.disponibilidade.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.door_open,
                0,
                0,
                0
            )
        }
    }
}