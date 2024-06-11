package com.gabrielkaiki.encontrefacil.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BancoDeDados(context: Context) : SQLiteOpenHelper(context, NOME_DB, null, VERSION) {
    companion object {
        var VERSION = 1;
        var NOME_DB = "bancoEncontreFacil"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val sql =
            "CREATE TABLE IF NOT EXISTS historico (id INTEGER PRIMARY KEY AUTOINCREMENT, pesquisa TEXT, raio TEXT)"
        try {
            db!!.execSQL(sql)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}