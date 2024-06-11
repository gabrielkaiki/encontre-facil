package com.gabrielkaiki.encontrefacil.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gabrielkaiki.encontrefacil.DAO.ItemDAO
import com.gabrielkaiki.encontrefacil.R
import com.gabrielkaiki.encontrefacil.adapter.AdapterHistorico
import com.gabrielkaiki.encontrefacil.databinding.FragmentNotificationsBinding
import com.gabrielkaiki.encontrefacil.models.Item
import com.gabrielkaiki.encontrefacil.utils.RecyclerItemClickListener
import com.gabrielkaiki.encontrefacil.utils.pesquisaAtual
import com.gabrielkaiki.encontrefacil.utils.raioAtual
import com.google.android.gms.ads.AdRequest

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private var listener: RecyclerItemClickListener? = null
    private var recyclerHistorico: RecyclerView? = null
    private var adapterHistorico: AdapterHistorico? = null
    private var listaHistorico = arrayListOf<Item>()
    private var allChecked = false
    private lateinit var optionsMenu: Menu
    lateinit var textoVazio: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setHasOptionsMenu(true)

        recyclerHistorico = binding.recyclerHistorico
        textoVazio = binding.textEmpty

        recuperarHistorico()
        return root
    }

    private fun recuperarHistorico() {
        listaHistorico.clear()
        listaHistorico = ItemDAO(requireContext()).listar()
        if (listaHistorico.size == 0) textoVazio.visibility =
            View.VISIBLE else textoVazio.visibility = View.GONE
        configuraRecyclerView()
        configuraRecyclerClickListener()
    }

    private fun configuraRecyclerClickListener() {
        listener = RecyclerItemClickListener(
            requireContext(),
            recyclerHistorico,
            object : RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    if (listaHistorico[position].boxVisible) {
                        val item = listaHistorico[position]
                        listaHistorico[position].boxChecked = !item.boxChecked
                        adapterHistorico!!.notifyDataSetChanged()
                    } else {
                        fazerPesquisa(position)
                    }
                }

                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                }

                override fun onLongItemClick(view: View?, position: Int) {
                    definirTodosCheckBoxComoVisible()
                    exibirItensDoMenu()
                }

            })
        recyclerHistorico!!.addOnItemTouchListener(listener!!)
    }

    private fun fazerPesquisa(position: Int) {
        val item = listaHistorico[position]
        raioAtual = item.raio
        pesquisaAtual = item.pesquisa
        val navController =
            requireActivity().findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.navigation_home)
    }

    private fun exibirItensDoMenu() {
        for (i in 0..2) {
            optionsMenu.getItem(i).isVisible = true
        }
    }

    private fun ocultarItensDoMenu() {
        for (i in 0..2) {
            optionsMenu.getItem(i).isVisible = false
        }
    }

    private fun definirTodosCheckBoxComoVisible() {
        for (item in listaHistorico) {
            item.boxVisible = true
            adapterHistorico!!.notifyDataSetChanged()
        }
    }

    private fun configuraRecyclerView() {
        adapterHistorico = AdapterHistorico(listaHistorico)
        recyclerHistorico!!.setHasFixedSize(true)
        recyclerHistorico!!.layoutManager = LinearLayoutManager(requireContext())
        recyclerHistorico!!.adapter = adapterHistorico
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_hostorico, menu)
        optionsMenu = menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_fechar -> {
                ocultarItensDoMenu()
                uncheckAllBox()
                ocultarCheckBox()
                optionsMenu.getItem(1).icon =
                    resources.getDrawable(R.drawable.baseline_check_box_outline_blank_24)
                allChecked = false
                adapterHistorico!!.notifyDataSetChanged()
            }

            R.id.menu_deletar -> {
                deletarItens()
            }

            R.id.menu_check -> {
                checkAllBox(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deletarItens() {
        val itemDAO = ItemDAO(requireContext())
        for (item in listaHistorico) {
            if (item.boxChecked) {
                itemDAO.deletar(item)
            }
        }
        recyclerHistorico!!.removeOnItemTouchListener(listener!!)
        recuperarHistorico()
        ocultarItensDoMenu()
    }

    private fun ocultarCheckBox() {
        for (item in listaHistorico) {
            item.boxVisible = false
        }
    }

    private fun uncheckAllBox() {
        for (item in listaHistorico) {
            item.boxChecked = false
        }
    }

    private fun checkAllBox(item: MenuItem) {
        for (item in listaHistorico) {
            item.boxChecked = !allChecked
            adapterHistorico!!.notifyDataSetChanged()
        }
        if (allChecked) {
            item.icon = resources.getDrawable(R.drawable.baseline_check_box_outline_blank_24)
        } else {
            item.icon = resources.getDrawable(R.drawable.baseline_check_box_24)
        }
        allChecked = !allChecked
    }

    override fun onStart() {
        super.onStart()

        //Anúncios
        var mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}