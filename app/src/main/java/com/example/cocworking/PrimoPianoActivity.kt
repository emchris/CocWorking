package com.example.cocworking

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_piantina.*
import kotlinx.android.synthetic.main.activity_primo_piano.*


class PrimoPianoActivity: AppCompatActivity() {

    private var defaultUserId: String? = ""
    private var defaultEmail: String? = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        val mypreference = MyPreference(this)
        defaultUserId = mypreference.getAccountInfo()
        defaultEmail = mypreference.getPreferenceEmail()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_primo_piano)
        setSupportActionBar(findViewById(R.id.toolbar_orange))

        //se clicco sul bottone "Sala Riunioni" apre la propria Activity
        button_sala_riunioni.setOnClickListener{
            val meeting = Intent(applicationContext,SalaRiunioniActivity::class.java)
            startActivity(meeting)
        }

    }

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    //Funzioni che si attivano quando viene cliccato un elemento della barra
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.setting -> {
            // User chose the "Settings" item, show the app settings UI...
            val setting = Intent(applicationContext,SetupActivity::class.java)
            startActivity(setting)
            true
        }
        R.id.account -> {
            //Se clicco su "profilo" si apre l'activity relativa
            val profilo = Intent(applicationContext,ProfiloActivity::class.java)
            startActivity(profilo)
            true
        }
        R.id.allarm -> {
            // User chose the "Allarm" action for see notifications
            val notifiche = Intent(applicationContext,NotificheActivity::class.java)
            startActivity(notifiche)
            true
        }
        R.id.home -> {
            // uso "home" action per aprire MainActivity
            val home = Intent(applicationContext,MainActivity::class.java)
            startActivity(home)
            true
        }
        R.id.logout -> {
            // uso "home" action per aprire MainActivity
            val mypreference = MyPreference(this)
            mypreference.deleteAccountInfo(defaultUserId, defaultEmail)
            val login = Intent(applicationContext,LoginActivity::class.java)
            startActivity(login)
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

}



