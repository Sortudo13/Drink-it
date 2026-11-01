package com.ryan.drink_it


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.graphics.Color
import android.view.SoundEffectConstants
import android.media.MediaPlayer
// notificação
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.util.Log
// Notificação ao longo do dia
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
// Resetar dia
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class MainActivity : AppCompatActivity() {
    private var quantidade_a_ser_adicionada: Int = 180
    private var mls_bebidos_hoje : Int = 0
    private var nome_usuario: String = "pessoa"

    // Notificação
    private val CHANNEL_ID = "canal_agua"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Encontra os componentes na tela
        val txt_mls_bebidos = findViewById<TextView>(R.id.txt_mls_bebidos)

        // 2. Abre o arquivo de dados salvos
        val dadosSalvos = getSharedPreferences("drink-it_data", MODE_PRIVATE)

        // 3. Lê os valores salvos. Se não encontrar nada (ex: primeira vez abrindo o app), assume 0
        mls_bebidos_hoje = dadosSalvos.getInt("mls", 0)
        val meta_diaria : Int = (dadosSalvos.getInt("meta_diaria", 2500))
        this.nome_usuario = dadosSalvos.getString("nome_usuario", "pessoa") ?: "pessoa"

        // 4. Joga os valores de volta na tela para o usuário ver
        if(mls_bebidos_hoje == 0){
            txt_mls_bebidos.text = "0 / $meta_diaria ml"
        } else{

            txt_mls_bebidos.text = "$mls_bebidos_hoje / $meta_diaria ml"
            if (mls_bebidos_hoje >= meta_diaria){
                txt_mls_bebidos.setTextColor(Color.parseColor("#4CAF50"))
            } else{
                txt_mls_bebidos.setTextColor(Color.WHITE)
            }
        }


        // Quantidades
        val botao_180mls = findViewById<Button>(R.id.button_180ml)
        selecionarQuantidade(botao_180mls)

        val txt_mensagem = findViewById<TextView>(R.id.txt_mensagem)
        txt_mensagem.text = ""

        // Notificação
        criarCanalNotificacao()
        solicitarPermissaoNotificacao()

        val pedido = PeriodicWorkRequestBuilder<LembreteWorker>(90, TimeUnit.MINUTES).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "lembrete_agua",
            ExistingPeriodicWorkPolicy.KEEP,
            pedido
        )

        val btn_notificacao = findViewById<Button>(R.id.btn_notificacao)
        btn_notificacao.setOnClickListener {
            dispararNotificacao()
        }

        // Animação inicial
        playAnimacaoInicial()

        // Menu
        val btn_menu = findViewById<Button>(R.id.btn_menu)
        btn_menu.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }

        val frasesSaudacao= listOf(
            "Olá $nome_usuario!",
            "Oi $nome_usuario!",
            "Eai $nome_usuario!",
            "$nome_usuario, Você voltou! ",
            "Como vai, $nome_usuario?",
            "De boas $nome_usuario?",
            "Bom te ver por aqui $nome_usuario!",
            "Sabia que não iria longe $nome_usuario!"
        )
        val text_ola = findViewById<TextView>(R.id.text_ola)
        text_ola.text = frasesSaudacao.random()

        val botao_resetar = findViewById<Button>(R.id.botao_resetar)
        botao_resetar.setOnClickListener {
            reiniciarDia()
        }

    }

    override fun onResume() {
        super.onResume()
        verificarDia()
    }

    public fun adicionarAgua(view: View){

        // Atualiza quantos mls bebeu
        val txt_mls_bebidos = findViewById<TextView>(R.id.txt_mls_bebidos)
        var texto: String = txt_mls_bebidos.text.toString()
        mls_bebidos_hoje += quantidade_a_ser_adicionada

        // Pega a meta diária
        val dadosSalvos = getSharedPreferences("drink-it_data", MODE_PRIVATE)
        val meta_diaria : Int = (dadosSalvos.getInt("meta_diaria", 2500))


        txt_mls_bebidos.text = "$mls_bebidos_hoje / $meta_diaria ml"
        txt_mls_bebidos.textSize = 34f

        if (mls_bebidos_hoje >= meta_diaria){
            txt_mls_bebidos.setTextColor(Color.parseColor("#4CAF50"))
        } else{
            txt_mls_bebidos.setTextColor(Color.WHITE)
        }



        view.playSoundEffect(SoundEffectConstants.CLICK)


        // 3. SALVA OS DADOS (Nova parte!)
        val editor = dadosSalvos.edit()
        editor.putInt("mls", mls_bebidos_hoje)         // Salva a quantidade de mls
        editor.apply()                          // Confirma a gravação dos dados

        val frasesDeIncentivo = listOf(
            "Muito bem!",
            "Excelente!",
            "O Mestre aprova essa ação!",
            "Pequenos hábitos, grandes resultados!",
            "Seu corpo agradece!",
            "Até breve!"
        )
        val txt_mensagem = findViewById<TextView>(R.id.txt_mensagem)
        txt_mensagem.animation
        txt_mensagem.text = frasesDeIncentivo.random()
        val animacaoImediata = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in)
        txt_mensagem.startAnimation(animacaoImediata)

        // Som
        val player = MediaPlayer.create(this, R.raw.win)
        player.setOnCompletionListener { it.release() }
        player.start()
    }

    public fun reiniciarDia(){
        val txt_mls_bebidos = findViewById<TextView>(R.id.txt_mls_bebidos)
        val frasesDeIncentivo = listOf(
            "Comece com pouco. \n A constância é o segredo!",
            "Água é vida! \n Cuide da sua saúde!",
            "Cada gota conta! \n Mantenha o foco na hidratação.",
            "Pequenos hábitos, \n grandes resultados!",
            "Seu corpo agradece! \n Não esqueça de beber água."
        )
        txt_mls_bebidos.text = frasesDeIncentivo.random()
        txt_mls_bebidos.textSize = 20f
        txt_mls_bebidos.setTextColor(Color.WHITE)

        val txt_mensagem = findViewById<TextView>(R.id.txt_mensagem)
        txt_mensagem.text = ""

        mls_bebidos_hoje = 0
        // 3. SALVA OS DADOS (Nova parte!)
        val dadosSalvos = getSharedPreferences("drink-it_data", MODE_PRIVATE)
        val editor = dadosSalvos.edit()
        editor.putInt("mls", 0)         // Salva a quantidade de mls
        editor.apply()
    }

    public fun selecionarQuantidade(view: View){
        val botaoClicado = view as Button
        var quantidade: Int = (botaoClicado.text.filter { it.isDigit() }).toString().toInt()
        quantidade_a_ser_adicionada = quantidade;

        // Atualiza o botão;
        val botao_180mls = findViewById<TextView>(R.id.button_180ml)
        val botao_250mls = findViewById<TextView>(R.id.button_250mls)
        val botao_500mls = findViewById<TextView>(R.id.button_500mls)

        botao_180mls.setBackgroundColor(Color.parseColor("#9FD2F8"))
        botao_180mls.setTextColor(Color.BLACK)
        botao_250mls.setBackgroundColor(Color.parseColor("#9FD2F8"))
        botao_250mls.setTextColor(Color.BLACK)
        botao_500mls.setBackgroundColor(Color.parseColor("#9FD2F8"))
        botao_500mls.setTextColor(Color.BLACK)

        botaoClicado.setBackgroundColor(Color.parseColor("#2C9EF6"))
        botaoClicado.setTextColor(Color.WHITE)

        // 2. Abre o arquivo de dados salvos
        val dadosSalvos = getSharedPreferences("drink-it_data", MODE_PRIVATE)

        // 3. Lê os valores salvos. Se não encontrar nada (ex: primeira vez abrindo o app), assume 0
        val quantidade_1 = dadosSalvos.getString("quantidade_1", "180")
        val quantidade_2 = dadosSalvos.getString("quantidade_2", "250")
        val quantidade_3 = dadosSalvos.getString("quantidade_3", "500")

        botao_180mls.setText("$quantidade_1 ml")
        botao_250mls.setText("$quantidade_2 ml")
        botao_500mls.setText("$quantidade_3 ml")


    }

    private fun criarCanalNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nome = "Lembrete de Água"
            val descricaoText = "Canal para avisos de hidratação"
            val importancia = NotificationManager.IMPORTANCE_HIGH // Faz o balão aparecer no topo
            val canal = NotificationChannel(CHANNEL_ID, nome, importancia).apply {
                description = descricaoText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }

    private fun solicitarPermissaoNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    private fun dispararNotificacao() {
        if(deveSilenciarNotificacao()){
            return
        }

        // Verifica se temos permissão antes de exibir (evita erros no app)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        // Constrói a notificação
        val construtor = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Ícone temporário do sistema
            .setContentTitle("Hora da Água! 💧")
            .setContentText("Que tal beber um copo de água agora?")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Garante que ela apareça imediatamente
            .setAutoCancel(true) // Desaparece quando o usuário clica nela

        // Mostra a notificação na tela
        with(NotificationManagerCompat.from(this)) {
            notify(1, construtor.build()) // O ID '1' identifica essa notificação específica
        }
    }

    private fun deveSilenciarNotificacao(): Boolean {
        val horaAtual = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        Log.d("Notif", "Hora que o app enxerga: $horaAtual")
        return horaAtual >= 22 || horaAtual < 8
    }

    private fun verificarDia(){
        val prefs = getSharedPreferences("drinkit", MODE_PRIVATE)
        val hoje = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val ultimoDia = prefs.getString("ultimo_dia", null)

        if(ultimoDia != hoje){
            prefs.edit()
                .putInt("mls", 0)
                .putString("ultimo_dia", hoje)
                .apply()

            reiniciarDia()
        }
    }

    public fun abrirInstagram(view: View) {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
        intent.data = android.net.Uri.parse("https://www.instagram.com/ryan.hideki10/")
        startActivity(intent)
    }

    private fun playAnimacaoInicial(){
        // 1. Carrega a animação normal para o primeiro TextView
        val animacaoImediata = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in)

        // 2. Carrega uma cópia da animação para o resto dos componentes e adiciona 2 segundos (2000 milissegundos) de atraso
                val animacaoComAtraso = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in)
                animacaoComAtraso.startOffset = 700

        // 3. Encontra todas as TextViews pelos IDs
                val textView = findViewById<android.widget.TextView>(R.id.text_ola)
                val textView2 = findViewById<android.widget.TextView>(R.id.textView2)
                val txtMlsBebidos = findViewById<android.widget.TextView>(R.id.txt_mls_bebidos)
                val txtMensagem = findViewById<android.widget.TextView>(R.id.txt_mensagem)
                val textView3 = findViewById<android.widget.TextView>(R.id.textView3)

        // 4. Encontra todos os Botões pelos IDs
                val botaoAdicionar = findViewById<android.widget.Button>(R.id.botao_adicionar)
                val botaoResetar = findViewById<android.widget.Button>(R.id.botao_resetar)
                val button180ml = findViewById<android.widget.Button>(R.id.button_180ml)
                val button250mls = findViewById<android.widget.Button>(R.id.button_250mls)
                val button500mls = findViewById<android.widget.Button>(R.id.button_500mls)
                val btnNotificacao = findViewById<android.widget.Button>(R.id.btn_notificacao)

        // 5. O primeiro TextView inicia IMEDIATAMENTE
                textView.startAnimation(animacaoImediata)

        // 6. Todo o resto inicia APENAS depois de 2 segundos
                textView2.startAnimation(animacaoComAtraso)
                txtMlsBebidos.startAnimation(animacaoComAtraso)
                txtMensagem.startAnimation(animacaoComAtraso)
                textView3.startAnimation(animacaoComAtraso)

                botaoAdicionar.startAnimation(animacaoComAtraso)
                botaoResetar.startAnimation(animacaoComAtraso)
                button180ml.startAnimation(animacaoComAtraso)
                button250mls.startAnimation(animacaoComAtraso)
                button500mls.startAnimation(animacaoComAtraso)
                btnNotificacao.startAnimation(animacaoComAtraso)
    }
}