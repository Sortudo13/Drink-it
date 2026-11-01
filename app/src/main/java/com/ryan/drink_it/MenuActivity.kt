package com.ryan.drink_it

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Botões
        val button_usuario = findViewById<Button>(R.id.button_usuario)
        button_usuario.setOnClickListener {
            startActivity(Intent(this, UsuarioActivity::class.java))
        }

        val button_creditos = findViewById<Button>(R.id.button_creditos)
        button_creditos.setOnClickListener {
            startActivity(Intent(this, CreditosActivity::class.java))
        }

        val button_voltar = findViewById<Button>(R.id.button_voltar)
        button_voltar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val text_creditos = findViewById<TextView>(R.id.text_creditos)
        text_creditos.setOnClickListener {
            Utils.abrirInstagram(this)
        }
    }
}