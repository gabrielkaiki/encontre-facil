package com.gabrielkaiki.encontrefacil

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.gabrielkaiki.encontrefacil.databinding.ActivityMainBinding
import com.gabrielkaiki.encontrefacil.utils.Permissoes
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var permissoes = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissoesAceitas = !Permissoes.validarPermissoes(permissoes, this, 0)

        if (permissoesAceitas) {
            configurarElementosDeInterface()
        }
    }

    private fun configurarElementosDeInterface() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var permissoesNegadas = false
        for (resultadoPermissao in grantResults) {
            if (resultadoPermissao == PackageManager.PERMISSION_DENIED) {
                mostrarDialog()
                permissoesNegadas = true
            }
        }

        if (!permissoesNegadas) {
            configurarElementosDeInterface()
        }
    }

    private fun mostrarDialog() {
        val construtor = AlertDialog.Builder(this@MainActivity)
            .setTitle("Você precisa aceitar todas as permissões para usar o aplicativo!")
            .setCancelable(false)

        construtor.setNegativeButton("Fechar") { _, _ ->
           finish()
        }

        val dialog = construtor.create()
        dialog.show()
    }
}