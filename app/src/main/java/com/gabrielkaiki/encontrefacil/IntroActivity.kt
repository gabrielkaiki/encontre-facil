package com.gabrielkaiki.encontrefacil

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.gabrielkaiki.encontrefacil.databinding.ActivityIntroBinding
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide

class IntroActivity : IntroActivity() {
    lateinit var binding: ActivityIntroBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("preferencias", 0)
        val primeiraVez = sharedPreferences.getBoolean("firstTime", true)
        editor = sharedPreferences.edit()

        if (!primeiraVez) startActivity(Intent(this@IntroActivity, MainActivity::class.java))

        isButtonBackVisible = false
        isButtonNextVisible = false

        addSlide(
            FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_1)
                .build()
        )

        addSlide(
            FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_2)
                .build()
        )

        addSlide(
            FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_3)
                .build()
        )

        addSlide(
            FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_cadastro)
                .canGoForward(false)
                .build()
        )
    }

    fun abrirTelaPrincipal(view: View) {
        editor.putBoolean("firstTime", false)
        editor.apply()
        startActivity(Intent(this@IntroActivity, MainActivity::class.java))
    }
}