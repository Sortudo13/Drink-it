package com.ryan.drink_it

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CreditosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_creditos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button_voltar = findViewById<Button>(R.id.button_voltar)
        button_voltar.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }

        val text_creditos = findViewById<TextView>(R.id.creditos)
        text_creditos.setOnClickListener {
            Utils.abrirInstagram(this)
        }
    }
}