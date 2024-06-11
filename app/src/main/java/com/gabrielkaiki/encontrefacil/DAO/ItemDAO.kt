package com.gabrielkaiki.encontrefacil.DAO

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.gabrielkaiki.encontrefacil.interfaces.iItemDAO
import com.gabrielkaiki.encontrefacil.models.Item
import com.gabrielkaiki.encontrefacil.utils.BancoDeDados

class ItemDAO : iItemDAO {
    private var escreve: SQLiteDatabase? = null
    private var le: SQLiteDatabase? = null
    private var contexto: Context? = null

    constructor(context: Context?) {
        val banco = BancoDeDados(context!!)
        escreve = banco.getWritableDatabase()
        le = banco.getReadableDatabase()
        contexto = context
    }

    override fun salvar(item: Item?): Boolean {
        val cv = ContentValues()
        cv.put("pesquisa", item!!.pesquisa)
        cv.put("raio", item.raio)
        try {
            escreve!!.insert("historico", null, cv)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun deletar(item: Item?): Boolean {
        val args = arrayOf(item!!.id!!)
        try {
            escreve!!.delete("historico", "id = ?", args)
            return true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun listar(): ArrayList<Item> {
        val lista = arrayListOf<Item>()
        val nomeTabela = "historico"
        try {
            val cursor = le!!.rawQuery("SELECT * FROM $nomeTabela", null)
            while (cursor.moveToNext()) {
                val item = Item()
                item.id = cursor.getString(0)
                item.pesquisa = cursor.getString(1)
                item.raio = cursor.getString(2)
                lista.add(item)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return lista
    }
}