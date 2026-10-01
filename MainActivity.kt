package com.example.irunaaccountswitcher

import android.accounts.Account
import android.accounts.AccountManager
import android.app.*
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import java.net.URLEncoder

class MainActivity : Activity() {
    private lateinit var slots: LinearLayout
    private val prefs by lazy { getSharedPreferences("slots", MODE_PRIVATE) }
    private val slot1Email = "clintmark566@gmail.com"
    private val defaults = arrayOf("Main","Mage","Tank","Farm","Alt 1","Alt 2","Alt 3","Alt 4")

    override fun onCreate(b: Bundle?) { super.onCreate(b); buildUi() }
    override fun onResume() { super.onResume(); if (::slots.isInitialized) renderSlots() }

    private fun buildUi() {
        val scroll=ScrollView(this)
        val root=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(24,24,24,24) }
        root.addView(TextView(this).apply { text="IRUNA 1-TAP ACCOUNT SWITCHER"; textSize=23f; gravity=17 })
        root.addView(TextView(this).apply {
            text="Slot 1 dikunci ke akun yang dikonfigurasi. Password Google tidak disimpan."
            textSize=14f; gravity=17; setPadding(0,8,0,16)
        })
        root.addView(Button(this).apply {
            text="⚙ Kelola akun Google"; setOnClickListener { startActivity(Intent(Settings.ACTION_SYNC_SETTINGS)) }
        })
        root.addView(Button(this).apply {
            text="⏱ Atur jeda buka Iruna"; setOnClickListener { chooseDelay() }
        })
        slots=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL }
        root.addView(slots); scroll.addView(root); setContentView(scroll); renderSlots()
    }

    private fun renderSlots() {
        slots.removeAllViews()
        val accounts=AccountManager.get(this).getAccountsByType("com.google")
        val slot1=accounts.firstOrNull { it.name.equals(slot1Email, ignoreCase=true) }
        if (slot1 == null) {
            slots.addView(TextView(this).apply {
                text="Slot 1 belum tersedia: tambahkan akun $slot1Email ke perangkat melalui Pengaturan Android."
                textSize=16f; setPadding(0,16,0,16)
            })
        } else addSlot(0, slot1, "Main")

        accounts.filterNot { it.name.equals(slot1Email, ignoreCase=true) }.take(7).forEachIndexed { i, a ->
            addSlot(i+1, a, defaults[i+1])
        }
    }

    private fun addSlot(index:Int, account:Account, defaultName:String) {
        val name=prefs.getString("name_${account.name}", defaultName) ?: defaultName
        val row=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(0,8,0,8) }
        row.addView(TextView(this).apply { text="Slot ${index+1}: $name\n${account.name}"; textSize=16f })
        row.addView(Button(this).apply { text="▶ BUKA SLOT ${index+1}"; setOnClickListener { switchAndOpen(account) } })
        row.addView(Button(this).apply { text="✏ Ubah nama"; setOnClickListener { rename(account,name) } })
        slots.addView(row)
    }

    private fun switchAndOpen(account:Account) {
        val email=URLEncoder.encode(account.name,"UTF-8")
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://accounts.google.com/AccountChooser?Email=$email")))
        val delay=prefs.getLong("delay",3000L)
        Toast.makeText(this,"Konfirmasi akun Google. Iruna akan dibuka otomatis.",Toast.LENGTH_LONG).show()
        android.os.Handler(mainLooper).postDelayed({ openIruna() },delay)
    }

    private fun openIruna() {
        listOf("com.asobimo.irunaonline","com.asobimo.irunaonline_en").firstNotNullOfOrNull {
            packageManager.getLaunchIntentForPackage(it)
        }?.let { startActivity(it); return }
        startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/search?q=Iruna%20Online&c=apps")))
    }

    private fun chooseDelay() {
        val labels=arrayOf("2 detik","3 detik","5 detik","8 detik","10 detik")
        val values=longArrayOf(2000,3000,5000,8000,10000)
        val current=prefs.getLong("delay",3000L)
        AlertDialog.Builder(this).setTitle("Jeda")
            .setSingleChoiceItems(labels,values.indexOf(current).coerceAtLeast(0)){d,w->prefs.edit().putLong("delay",values[w]).apply();d.dismiss()}
            .setNegativeButton("Batal",null).show()
    }

    private fun rename(account:Account,old:String) {
        val input=EditText(this).apply { setText(old) }
        AlertDialog.Builder(this).setTitle("Nama slot").setView(input)
            .setNegativeButton("Batal",null)
            .setPositiveButton("Simpan"){_,_->prefs.edit().putString("name_${account.name}",input.text.toString().ifBlank{old}).apply();renderSlots()}
            .show()
    }
}
