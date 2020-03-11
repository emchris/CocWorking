package com.example.cocworking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu


import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar_white))

        //se clicco sul bottone "Piantina" apre un Activity
        button_piantina.setOnClickListener{
            val pianta = Intent(applicationContext,PiantinaActivity::class.java)
            startActivity(pianta)
        }

        //se clicco sul bottone "Vetrina" apre un Activity
        button_vetrina.setOnClickListener{
            val vetrina = Intent(applicationContext,ConvenzioniActivity::class.java)
            startActivity(vetrina)
        }

        //se clicco sul bottone "Cocò Working" apre un Activity
        button_chi_siamo.setOnClickListener{
            val about = Intent(applicationContext,AboutUsActivity::class.java)
            startActivity(about)
        }

        //se clicco sul bottone "Bacheca" apre un Activity
        button_bacheca.setOnClickListener{
            val bacheca = Intent(applicationContext,BachecaActivity::class.java)
            startActivity(bacheca)
        }

        //se clicco sul bottone "Eventi" apre un Activity
        button_eventi.setOnClickListener{
            val eventi = Intent(applicationContext,EventiActivity::class.java)
            startActivity(eventi)
        }

    }

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
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

        R.id.user -> {
            //Se clicco su "profilo" si apre l'activity relativa
            //BISOGNA METTERE IL CONTROLLO PER VERIFICARE SE SI è GIà FATTO L'ACCESSO E QUINDI APRIRE IL PROFILO
            val profilo = Intent(applicationContext,LoginActivity::class.java)
            startActivity(profilo)
            true
        }
        R.id.allarm -> {
            // User chose the "Allarm" action for see notifications
            val notifiche = Intent(applicationContext,NotificheActivity::class.java)
            startActivity(notifiche)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

}
