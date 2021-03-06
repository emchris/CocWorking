package com.example.cocworking

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class SetupActivity: AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        setSupportActionBar(findViewById(R.id.toolbar_white))

        //inserire il nome utente nella schermata impostazioni
        val informazioni = intent.extras
        val mypreference = MyPreference(this)
        var nome_account = mypreference.getAccountInfo()
        val NameProfile: TextView = findViewById(R.id.setup_setting_name) as TextView
        NameProfile.setText(nome_account)

    }

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_setup, menu)
        return true
    }

    //Funzioni che si attivano quando viene cliccato un elemento della barra
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.home -> {
            // uso "home" action per aprire MainActivity
            val home = Intent(applicationContext,MainActivity::class.java)
            startActivity(home)
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

}



