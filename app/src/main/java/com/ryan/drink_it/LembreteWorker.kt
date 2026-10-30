package com.ryan.drink_it

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class LembreteWorker(
    private val contexto: Context,
    params: WorkerParameters
) : Worker(contexto, params) {

    override fun doWork(): Result {
        val hora = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        if (hora >= 22 || hora < 8) return Result.success()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(contexto, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            return Result.success()
        }

        val notificacao = NotificationCompat.Builder(contexto, "canal_agua")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Hora da Água! 💧")
            .setContentText("Que tal beber um copo de água agora?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(contexto).notify(1, notificacao)
        return Result.success()
    }
}