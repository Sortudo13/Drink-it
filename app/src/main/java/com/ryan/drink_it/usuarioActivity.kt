package com.ryan.drink_it

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.widget.EditText
import android.widget.Button
import android.widget.Toast


class usuarioActivity : AppCompatActivity() {

    private var nome: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_usuario)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val edtNome = findViewById<EditText>(R.id.edit_nome)
        val btnConfirmar = findViewById<Button>(R.id.button_confirmar)
        val button_voltar = findViewById<Button>(R.id.button_voltar)

        btnConfirmar.setOnClickListener {
            val nome = edtNome.text.toString()
            Toast.makeText(this, "É um belo nome, $nome!", Toast.LENGTH_SHORT).show()
            this.nome = nome

            val dadosSalvos = getSharedPreferences("drink-it_data", MODE_PRIVATE)
            val editor = dadosSalvos.edit()
            editor.putString("nome_usuario", nome)         // Salva a quantidade de mls
            editor.apply()

            startActivity(Intent(this, MainActivity::class.java))
        }

        button_voltar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}