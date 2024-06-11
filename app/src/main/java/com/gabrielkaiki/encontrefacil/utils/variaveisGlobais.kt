package com.gabrielkaiki.encontrefacil.utils

import com.gabrielkaiki.encontrefacil.models.Local
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

var pesquisaAtual: String? = "PonTo de interesse"
var raioAtual: String? = null
var somenteLocaisAbertos = false
var listaDeLocaisAtual = arrayListOf<Local>()
var localUsuarioAtual: LatLng? = null
var marcadorAtual: Local? = null