package com.ryan.drink_it
import android.content.Context
import android.content.Intent
import android.net.Uri

object Utils {
    fun abrirInstagram(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/ryan.hideki10/"))
        context.startActivity(intent)
    }
}