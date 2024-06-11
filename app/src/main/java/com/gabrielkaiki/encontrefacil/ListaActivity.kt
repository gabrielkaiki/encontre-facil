package com.gabrielkaiki.encontrefacil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gabrielkaiki.encontrefacil.adapter.AdapterDetalhes
import com.gabrielkaiki.encontrefacil.databinding.ActivityListaBinding
import com.gabrielkaiki.encontrefacil.utils.listaDeLocaisAtual

class ListaActivity : AppCompatActivity() {
    lateinit var _binding: ActivityListaBinding
    private val binding get() = _binding
    lateinit var recyclerView: RecyclerView
    lateinit var adapterLista: AdapterDetalhes
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityListaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        recyclerView = binding.recyclerView
        configurarRecyclerView()
    }

    private fun configurarRecyclerView() {
        adapterLista = AdapterDetalhes(listaDeLocaisAtual, this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapterLista
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}