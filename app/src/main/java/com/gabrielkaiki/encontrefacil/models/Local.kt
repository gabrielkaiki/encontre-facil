package com.gabrielkaiki.encontrefacil.models

class Local {
    var geometry: Geometry? = null
    var name: String? = null
    var vicinity: String? = null
    var opening_hours: HoraAbre? = null
    var photos = arrayListOf<Photos>()
    var place_id: String? = null
    var icon: String? = null
}