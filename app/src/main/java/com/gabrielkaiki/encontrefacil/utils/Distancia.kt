package com.gabrielkaiki.encontrefacil.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.text.DecimalFormat
import kotlin.math.roundToInt

fun calcularDistancia(inicial: LatLng, final: LatLng): Float {
    val localUsuario = Location("Local usuário")
    localUsuario.latitude = inicial.latitude
    localUsuario.longitude = inicial.longitude

    val localEstabelicimento = Location("Local estabelecimento")
    localEstabelicimento.latitude = final.latitude
    localEstabelicimento.longitude = final.longitude

    return localUsuario.distanceTo(localEstabelicimento)
}

fun formatarDistancia(distancia: Float): String {
    val copiaDistancia = distancia
    val distanciaFormatada: String
    if (copiaDistancia < 1000) {
        distanciaFormatada = copiaDistancia.roundToInt().toString() + " M "
    } else {
        val decimal = DecimalFormat("#.#")
        val distanciaConvertida = copiaDistancia / 1000
        distanciaFormatada = decimal.format(distanciaConvertida.toDouble()) + " KM "
    }

    return distanciaFormatada
}