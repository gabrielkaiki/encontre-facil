package com.gabrielkaiki.encontrefacil.ui.dashboard

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.gabrielkaiki.encontrefacil.DAO.ItemDAO
import com.gabrielkaiki.encontrefacil.R
import com.gabrielkaiki.encontrefacil.databinding.FragmentDashboardBinding
import com.gabrielkaiki.encontrefacil.models.Item
import com.gabrielkaiki.encontrefacil.utils.pesquisaAtual
import com.gabrielkaiki.encontrefacil.utils.raioAtual
import com.gabrielkaiki.encontrefacil.utils.somenteLocaisAbertos
import com.google.android.gms.ads.AdRequest
import com.google.android.material.textfield.TextInputEditText
import com.vdx.designertoast.DesignerToast

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    lateinit var botaoPesquisa: Button
    private var campoTipoLugar: TextInputEditText? = null
    private var campoRaioDeDistancia: TextInputEditText? = null
    lateinit var navBottom: Menu
    lateinit var checkBox: CheckBox

    private fun armazenarItemNoBanco(item: Item) {
        val itemDAO = ItemDAO(requireContext())
        itemDAO.salvar(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setHasOptionsMenu(true)

        //Inicializar componentes
        inicializarComponentes()

        //Click listeners
        botaoPesquisa.setOnClickListener {
            pesquisar()
        }

        return root
    }

    private fun inicializarComponentes() {
        campoTipoLugar = binding.textInputTipoDeLocal
        campoRaioDeDistancia = binding.textInputRaio
        botaoPesquisa = binding.buttonSearch
        checkBox = binding.checkBox
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun pesquisar() {
        val tipo = campoTipoLugar!!.text.toString()
        val raio = campoRaioDeDistancia!!.text.toString()

        if (tipo.isNotEmpty()) {
            if (raio.isNotEmpty()) {
                pesquisaAtual = campoTipoLugar!!.text.toString()
                raioAtual = campoRaioDeDistancia!!.text.toString()

                val item = Item()
                item.pesquisa = pesquisaAtual
                item.raio = raioAtual
                armazenarItemNoBanco(item)

                somenteLocaisAbertos = checkBox.isChecked
                redirecionarParaMapaEPesquisar()
            } else {
                DesignerToast.Warning(
                    requireContext(),
                    "O campo raio de distância não pode ser vazio!",
                    Gravity.BOTTOM,
                    Toast.LENGTH_SHORT
                );

            }
        } else {
            DesignerToast.Warning(
                requireContext(),
                "O campo local à ser pesquisado não pode ser vazio!",
                Gravity.BOTTOM,
                Toast.LENGTH_SHORT
            );
        }
    }

    private fun redirecionarParaMapaEPesquisar() {
        val navController =
            requireActivity().findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.navigation_home)
    }

    override fun onStart() {
        super.onStart()

        //Anúncio
        val mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }
}