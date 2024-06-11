package com.gabrielkaiki.encontrefacil.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gabrielkaiki.encontrefacil.R
import com.gabrielkaiki.encontrefacil.models.Item
import java.util.ArrayList

class AdapterHistorico(var listaHistorico: ArrayList<Item>) :
    RecyclerView.Adapter<AdapterHistorico.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pesquisa: TextView? = itemView.findViewById(R.id.textPesquisa)
        var raio: TextView? = itemView.findViewById(R.id.textRaio)
        var checkBox: CheckBox? = itemView.findViewById(R.id.checkBoxItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_historico, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listaHistorico.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.checkBox!!.visibility = View.GONE
        holder.checkBox!!.isChecked = false

        val item = listaHistorico[position]
        holder.pesquisa!!.text = item.pesquisa
        holder.raio!!.text = item.raio

        if(item.boxVisible) holder.checkBox!!.visibility = View.VISIBLE
        if(item.boxChecked) holder.checkBox!!.isChecked = true
    }
}