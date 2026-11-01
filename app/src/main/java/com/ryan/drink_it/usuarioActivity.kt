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


class UsuarioActivity : AppCompatActivity() {

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

        val dadosSalvos = getSharedPreferences("drink-it_data", MODE_PRIVATE)
        val nome_usuario = dadosSalvos.getString("nome_usuario", "pessoa") ?: "pessoa"


        val edtNome = findViewById<EditText>(R.id.edit_nome)
        edtNome.setText(nome_usuario)
        val btnConfirmar = findViewById<Button>(R.id.button_confirmar)
        val button_voltar = findViewById<Button>(R.id.button_voltar)

        val edit_quantidade_1 = findViewById<EditText>(R.id.edit_quantidade_1)
        val edit_quantidade_2 = findViewById<EditText>(R.id.edit_quantidade_2)
        val edit_quantidade_3 = findViewById<EditText>(R.id.edit_quantidade_3)

        edit_quantidade_1.setText(dadosSalvos.getString("quantidade_1", "180"))
        edit_quantidade_2.setText(dadosSalvos.getString("quantidade_2", "250"))
        edit_quantidade_3.setText(dadosSalvos.getString("quantidade_3", "500"))


        btnConfirmar.setOnClickListener {
            val nome = edtNome.text.toString()
            Toast.makeText(this, "É um belo nome, $nome!", Toast.LENGTH_SHORT).show()
            this.nome = nome

            val quantidade_1 = findViewById<EditText>(R.id.edit_quantidade_1)
            val quantidade_2 = findViewById<EditText>(R.id.edit_quantidade_2)
            val quantidade_3 = findViewById<EditText>(R.id.edit_quantidade_3)

            val dadosSalvos = getSharedPreferences("drink-it_data", MODE_PRIVATE)
            val editor = dadosSalvos.edit()
            editor.putString("nome_usuario", nome)         // Salva a quantidade de mls
            editor.putString("quantidade_1", quantidade_1.text.replace(Regex("[^0-9]"), "").ifEmpty { "180" })
            editor.putString("quantidade_2", quantidade_2.text.replace(Regex("[^0-9]"), "").ifEmpty { "250" })
            editor.putString("quantidade_3", quantidade_3.text.replace(Regex("[^0-9]"), "").ifEmpty { "500" })
            editor.apply()

            startActivity(Intent(this, MainActivity::class.java))
        }

        button_voltar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}