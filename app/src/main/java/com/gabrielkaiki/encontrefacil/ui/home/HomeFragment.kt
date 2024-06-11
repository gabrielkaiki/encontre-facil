package com.gabrielkaiki.encontrefacil.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.gabrielkaiki.encontrefacil.ListaActivity
import com.gabrielkaiki.encontrefacil.R
import com.gabrielkaiki.encontrefacil.api.NearbySearhServices
import com.gabrielkaiki.encontrefacil.api.TranslationServices
import com.gabrielkaiki.encontrefacil.databinding.FragmentHomeBinding
import com.gabrielkaiki.encontrefacil.models.Local
import com.gabrielkaiki.encontrefacil.models.NearbySearch
import com.gabrielkaiki.encontrefacil.models.Translation
import com.gabrielkaiki.encontrefacil.utils.calcularDistancia
import com.gabrielkaiki.encontrefacil.utils.formatarDistancia
import com.gabrielkaiki.encontrefacil.utils.getRetrofitLocaisDeBusca
import com.gabrielkaiki.encontrefacil.utils.getRetrofitTraducao
import com.gabrielkaiki.encontrefacil.utils.listaDeLocaisAtual
import com.gabrielkaiki.encontrefacil.utils.localUsuarioAtual
import com.gabrielkaiki.encontrefacil.utils.marcadorAtual
import com.gabrielkaiki.encontrefacil.utils.pesquisaAtual
import com.gabrielkaiki.encontrefacil.utils.raioAtual
import com.gabrielkaiki.encontrefacil.utils.somenteLocaisAbertos
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import com.vdx.designertoast.DesignerToast
import dmax.dialog.SpotsDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class HomeFragment : Fragment(), OnMapReadyCallback {
    private var menu_home: Menu? = null
    private var listaLocais = arrayListOf<Local>()
    private lateinit var locationCallBack: LocationCallback
    private lateinit var mMap: GoogleMap
    private lateinit var localizacao: LatLng
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setHasOptionsMenu(true)
        MobileAds.initialize(requireContext()) {}
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener {
            encontrarMarcador(it)
            return@setOnMarkerClickListener true
        }

        buscarLocalizacao()
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun encontrarMarcador(it: Marker) {
        for (marker in listaDeLocaisAtual) {
            if (it.title == marker.name) {
                marcadorAtual = marker
                exibirDialog()
                return
            }
        }
    }

    private fun renderizaView(): View? {
        val detalhes_view = layoutInflater.inflate(R.layout.dialog_detalhes, null)

        //Nome do local
        val nome = detalhes_view.findViewById<TextView>(R.id.nome_dialog)
        nome.text = marcadorAtual!!.name
        nome.typeface = ResourcesCompat.getFont(requireContext(), R.font.fonte)

        //Coordenadas
        val distancia_view = detalhes_view.findViewById<TextView>(R.id.distancia_dialog)
        val latitude = marcadorAtual!!.geometry!!.location!!.lat!!
        val longitude = marcadorAtual!!.geometry!!.location!!.lng!!
        val localMarcador = LatLng(latitude.toDouble(), longitude.toDouble())
        val distancia = calcularDistancia(localUsuarioAtual!!, localMarcador)
        distancia_view.text = formatarDistancia(distancia)

        //Imagem
        val progressBar_view = detalhes_view.findViewById<ProgressBar>(R.id.progressBar_dialog)
        val imagem_view = detalhes_view.findViewById<ImageView>(R.id.imagem_dialog)
        val semFotos = marcadorAtual!!.photos.size == 0
        if (!semFotos) {
            progressBar_view.visibility = View.VISIBLE
            val url =
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&key=AIzaSyDBaSfV2vGuPPLLkIFK67eZQjvUsmivBnw&photo_reference="
            val urlCompleta = url + marcadorAtual!!.photos[0].photo_reference
            Picasso.get().load(Uri.parse(urlCompleta))
                .into(imagem_view, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        progressBar_view.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        progressBar_view.visibility = View.GONE
                    }

                })
        } else {
            imagem_view.setImageResource(R.drawable.construcao)
            progressBar_view.visibility = View.GONE
        }

        //Endereço
        val endereco_view = detalhes_view.findViewById<TextView>(R.id.endereco_dialog)
        endereco_view.text = marcadorAtual!!.vicinity


        //Aberto ou fechado
        val disponibilidade_view = detalhes_view.findViewById<TextView>(R.id.abertoFechado_dialog)

        val estaAberto = marcadorAtual!!.opening_hours?.open_now
        if (estaAberto != null) {
            if (estaAberto) {
                disponibilidade_view.text = "Aberto"
                disponibilidade_view.setTextColor(Color.rgb(14, 209, 69))
            } else {
                disponibilidade_view.text = "Fechado"
                disponibilidade_view.setTextColor(Color.RED)
                disponibilidade_view.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.door_closed,
                    0,
                    0,
                    0
                )
            }
        } else {
            disponibilidade_view.text = "Horário de atendimento não informado"
            disponibilidade_view.setTextColor(Color.rgb(14, 209, 69))
            disponibilidade_view.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.door_open,
                0,
                0,
                0
            )
        }

        return detalhes_view
    }

    private fun exibirDialog() {
        val view: View = renderizaView()!!
        val construtor = AlertDialog.Builder(requireContext())
            .setView(view)

        construtor.setNegativeButton("Fechar") { _, _ ->

        }

        val dialog = construtor.create()
        dialog.show()
    }


    fun buscarLocalizacao() {
        val dialog = SpotsDialog.Builder().setContext(requireContext()).setCancelable(false)
            .setMessage("Buscando localização...").build()
            .apply { show() }

        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        val locationRequest = LocationRequest.create()
        locationRequest.interval = 2 * 3000
        locationRequest.priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY

        val settingsRequest =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val settingsClient = LocationServices.getSettingsClient(requireContext())
        settingsClient.checkLocationSettings(settingsRequest).addOnSuccessListener {
        }.addOnFailureListener {
            if (it is ResolvableApiException) {
                val resolvableException: ResolvableApiException = it
                resolvableException.startResolutionForResult(requireActivity(), 1)
            }
        }

        locationCallBack = object : LocationCallback() {

            override fun onLocationResult(p0: LocationResult) {
                fusedLocationProviderClient.removeLocationUpdates(locationCallBack)

                localizacao =
                    LatLng(p0.lastLocation!!.latitude, p0.lastLocation!!.longitude)
                localUsuarioAtual = localizacao
                mudarPosicaoNoMapa()
                dialog.dismiss()
            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallBack,
            Looper.getMainLooper()
        )
    }

    private fun mudarPosicaoNoMapa() {
        mMap.addMarker(
            MarkerOptions()
                .position(localizacao)
                .title("Eu")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario))
        )
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(localizacao, 15f)
        )

        if (raioAtual != null) {
            menu_home?.getItem(0)!!.isVisible = true
            configuraRaioAoRedorDaLocalizacao()
        } else {
            menu_home?.getItem(0)!!.isVisible = false
        }
    }

    private fun configuraRaioAoRedorDaLocalizacao() {
        var raio = if (raioAtual != null) raioAtual else 250.0
        var circle = mMap.addCircle(
            CircleOptions()
                .center(localizacao)
                .radius(raioAtual!!.toDouble())
                .fillColor(Color.argb(90, 255, 153, 0))
                .strokeColor(Color.argb(190, 255, 152, 0))
        )

        if (pesquisaAtual != null)  fazerTraducaoDaPesquisa()
    }

    private fun fazerTraducaoDaPesquisa() {
        var dialog = SpotsDialog.Builder()
            .setCancelable(false)
            .setContext(requireContext())
            .setMessage("Carregando...")
            .build().apply { show() }

        checarResultadoDeTraducao("restaurant", dialog)

      /*  val retrofit = getRetrofitTraducao()
        val requisicao = retrofit.create(TranslationServices::class.java)

        requisicao.getTranslation(
            pesquisaAtual!!,
            "pt-BR",
            "en",
            "text",
            "AIzaSyA1n-jEXg-ATD3WZlQ1OhIpDvL7Dnz1xDE"
        ).enqueue(object : Callback<Translation> {
            override fun onResponse(call: Call<Translation>, response: Response<Translation>) {
                if (response.isSuccessful) {
                    val translation = response.body()
                    val pesquisa = translation!!.data!!.translations[0].translatedText
                    checarResultadoDeTraducao(pesquisa, dialog)
                } else {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Erro!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Translation>, t: Throwable) {
                Toast.makeText(requireContext(), "Erro!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

        })*/
    }

    private fun checarResultadoDeTraducao(pesquisa: String?, dialog: android.app.AlertDialog) {
        var caixaBaixa = pesquisa!!.lowercase()
        if (caixaBaixa.contains(" ")) {
            caixaBaixa = pesquisa.replace(' ', '_')
        }
        fazerRequisicaoDeLocaisProximos(caixaBaixa, dialog)
    }

    private fun fazerRequisicaoDeLocaisProximos(pesquisa: String, dialog: android.app.AlertDialog) {
        val retrofit = getRetrofitLocaisDeBusca()
        val requisicao = retrofit.create(NearbySearhServices::class.java)
        requisicao.getLocaisProximos(
            "${localUsuarioAtual!!.latitude}, ${localUsuarioAtual!!.longitude}",
            raioAtual!!,
            pesquisa,
            "pt-BR",
            "AIzaSyC5sw2c8TV9ZB6KmjhrDlJldRT9OxJZPRg"
        ).enqueue(object : Callback<NearbySearch> {
            override fun onResponse(call: Call<NearbySearch>, response: Response<NearbySearch>) {
                if (response.isSuccessful) {
                    listaLocais.clear()
                    listaDeLocaisAtual.clear()
                    val nearbySearch = response.body()
                    listaLocais = nearbySearch!!.results
                    listaDeLocaisAtual = listaLocais
                    if (!listaLocais.isNullOrEmpty()) {
                        configuraMarcadores(listaLocais, dialog)
                    } else {
                        dialog.dismiss()
                        DesignerToast.Error(
                            requireContext(),
                            "Nenhum resultado",
                            Gravity.BOTTOM,
                            Toast.LENGTH_SHORT
                        )
                    }
                } else {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Erro!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NearbySearch>, t: Throwable) {
                dialog.dismiss()
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun configuraMarcadores(
        listaLocais: ArrayList<Local>,
        dialog: android.app.AlertDialog
    ) {
        val listaAuxiliar = arrayListOf<Local>()

        val construtor = LatLngBounds.Builder()
        construtor.include(localUsuarioAtual!!)

        for (marcador in listaLocais) {
            val lat = marcador.geometry!!.location!!.lat!!
            val lng = marcador.geometry!!.location!!.lng!!
            val posicao = LatLng(lat.toDouble(), lng.toDouble())
            val nomeLocal = marcador.name!!
            if (somenteLocaisAbertos) {
                val temHorario = marcador.opening_hours?.open_now
                if (temHorario != null) {
                    if (temHorario) {
                        listaAuxiliar.add(marcador)
                        mMap.addMarker(MarkerOptions().position(posicao).title(nomeLocal))
                        construtor.include(posicao)
                    }
                }
            } else {
                mMap.addMarker(MarkerOptions().position(posicao).title(nomeLocal))
                construtor.include(posicao)
            }
        }
        if (somenteLocaisAbertos) listaDeLocaisAtual = listaAuxiliar
        somenteLocaisAbertos = false

        val bounds = construtor.build()
        val largura = resources.displayMetrics.widthPixels
        val altura = resources.displayMetrics.heightPixels
        val espacoInterno = (largura * 0.20).toInt()
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngBounds(bounds, largura, altura, espacoInterno)
        )
        dialog.dismiss()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_lista_detalhes, menu)
        menu_home = menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_detalhes -> {
                val intent = Intent(requireContext(), ListaActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}