package com.gabrielkaiki.encontrefacil.utils

import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private var bancoDeDados: DatabaseReference? = null
private var auth: FirebaseAuth? = null
private var storage: StorageReference? = null

fun getDataBase(): DatabaseReference {
    if (bancoDeDados == null) bancoDeDados = FirebaseDatabase.getInstance().reference
    return bancoDeDados!!
}

fun getAuth(): FirebaseAuth {
    if (auth == null) auth = FirebaseAuth.getInstance()
    return auth!!
}

fun getStorage(): StorageReference {
    if (storage == null) storage = FirebaseStorage.getInstance().reference
    return storage!!
}

fun getUserId(): String {
    return getAuth()!!.currentUser!!.uid
}