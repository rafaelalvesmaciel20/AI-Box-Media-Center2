package com.aibox.mediacenter

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
 private lateinit var adapter: AppAdapter
 private lateinit var status: TextView
 private var allApps: List<AppItem> = emptyList()
 private var currentFilter: AppCategory? = null
 // Substitua SEU_USUARIO pelo usuário/organização do GitHub.
 private val versionJsonUrl = "https://raw.githubusercontent.com/SEU_USUARIO/AI-Box-Media-Center/main/version.json"

 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState); setContentView(R.layout.activity_main)
  status = findViewById(R.id.txtStatus)
  val recycler = findViewById<RecyclerView>(R.id.appsRecycler)
  recycler.layoutManager = GridLayoutManager(this, 5)
  adapter = AppAdapter(emptyList(), ::launchApp, ::confirmUninstall); recycler.adapter = adapter
  findViewById<MaterialButton>(R.id.btnPlayStore).setOnClickListener { openPlayStore() }
  findViewById<MaterialButton>(R.id.btnUpdate).setOnClickListener { checkForUpdate() }
  findViewById<MaterialButtonToggleGroup>(R.id.filterGroup).addOnButtonCheckedListener { _, checkedId, isChecked ->
   if (isChecked) { currentFilter = when (checkedId) { R.id.filterVideo -> AppCategory.VIDEO; R.id.filterTv -> AppCategory.TV; R.id.filterAudio -> AppCategory.AUDIO; else -> null }; applyFilter() }
  }
  loadInstalledApps()
 }

 @Suppress("DEPRECATION")
 private fun loadInstalledApps() {
  val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
  val activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
  allApps = activities.filter { it.activityInfo.packageName != packageName }.map { info ->
   val pkg = info.activityInfo.packageName; val label = info.loadLabel(packageManager).toString()
   AppItem(label, pkg, info.loadIcon(packageManager), classify(pkg, label))
  }.distinctBy { it.packageName }.sortedBy { it.name.lowercase() }
  applyFilter(); status.text = "${allApps.size} aplicativos encontrados • toque para abrir"
 }

 private fun classify(packageName: String, label: String): AppCategory {
  val value = "$packageName $label".lowercase()
  return when {
   listOf("netflix", "youtube", "primevideo", "prime video", "disney", "hbo", "max.", "plex", "vlc", "video", "player", "kodi", "twitch", "globoplay", "paramount").any { it in value } -> AppCategory.VIDEO
   listOf("iptv", "telecine", "tvexpress", "pluto", "tivimate", "canais", "live tv", "android tv").any { it in value } || value.contains(" tv") -> AppCategory.TV
   listOf("spotify", "deezer", "music", "musica", "audio", "podcast", "soundcloud", "youtube.music").any { it in value } -> AppCategory.AUDIO
   else -> AppCategory.OTHER
  }
 }

 private fun applyFilter() {
  val visible = if (currentFilter == null) allApps else allApps.filter { it.category == currentFilter }
  adapter.submitItems(visible)
  status.text = if (visible.isEmpty()) "Nenhum app identificado nesta categoria. Use Todos para ver os demais." else "${visible.size} aplicativos • toque para abrir • pressione e segure para opções"
 }

 private fun launchApp(item: AppItem) {
  val launchIntent = packageManager.getLaunchIntentForPackage(item.packageName)
  if (launchIntent == null) { Toast.makeText(this, "Não foi possível abrir ${item.name}.", Toast.LENGTH_SHORT).show(); return }
  try { startActivity(launchIntent) } catch (_: ActivityNotFoundException) { Toast.makeText(this, "Aplicativo indisponível.", Toast.LENGTH_SHORT).show() }
 }

 private fun openPlayStore() {
  try { startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MARKET)) }
  catch (_: ActivityNotFoundException) {
   try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store"))) }
   catch (_: Exception) { Toast.makeText(this, "Play Store não está disponível neste dispositivo.", Toast.LENGTH_LONG).show() }
  }
 }

 private fun confirmUninstall(item: AppItem) {
  AlertDialog.Builder(this).setTitle("Opções do aplicativo").setMessage("${item.name}

Deseja abrir a tela do Android para desinstalar este aplicativo?")
   .setNegativeButton("Cancelar", null).setPositiveButton("Desinstalar") { _, _ ->
    try { startActivity(Intent(Intent.ACTION_DELETE, Uri.parse("package:${item.packageName}"))) }
    catch (_: Exception) { Toast.makeText(this, "O Android não permitiu abrir a tela de desinstalação.", Toast.LENGTH_LONG).show() }
   }.show()
 }

 private fun checkForUpdate() {
  status.text = "Verificando atualização..."
  thread {
   var connection: HttpURLConnection? = null
   try {
    connection = (URL(versionJsonUrl).openConnection() as HttpURLConnection).apply { connectTimeout = 8000; readTimeout = 8000; requestMethod = "GET" }
    if (connection.responseCode != HttpURLConnection.HTTP_OK) error("HTTP ${connection.responseCode}")
    val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
    val remoteCode = json.optLong("versionCode", 0L); val remoteName = json.optString("versionName", "versão nova"); val apkUrl = json.optString("apkUrl", "")
    val currentCode = packageManager.getPackageInfo(packageName, 0).longVersionCode
    runOnUiThread {
     if (remoteCode > currentCode && apkUrl.startsWith("https://")) {
      AlertDialog.Builder(this).setTitle("Atualização disponível").setMessage("Versão $remoteName disponível. O endereço será aberto para baixar o APK; a instalação precisa ser confirmada no Android.")
       .setNegativeButton("Agora não", null).setPositiveButton("Abrir download") { _, _ -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl))) }.show()
     } else Toast.makeText(this, "Você já está usando a versão atual (${BuildConfig.VERSION_NAME}).", Toast.LENGTH_LONG).show()
     status.text = "Aplicativos instalados neste dispositivo"
    }
   } catch (e: Exception) {
    runOnUiThread { AlertDialog.Builder(this).setTitle("Não foi possível verificar")
     .setMessage("Confira a URL de versionJsonUrl no MainActivity.kt e tente novamente. Detalhe: ${e.message ?: "erro de conexão"}").setPositiveButton("OK", null).show(); status.text = "Verificação de atualização indisponível" }
   } finally { connection?.disconnect() }
  }
 }
}
