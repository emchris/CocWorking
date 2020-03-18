package com.example.cocworking

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.example.cocworking.Retrofit.IMyService
import com.example.cocworking.Retrofit.RetrofitClient
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.rengwuxian.materialedittext.MaterialEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {

    lateinit var iMyService : IMyService
    internal var compositeDisposable = CompositeDisposable()

    override fun onStop(){
        compositeDisposable.clear()
        super.onStop()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(findViewById(R.id.toolbar_white))

        //Init API
        val retrofit = RetrofitClient.getInstance()
        iMyService = retrofit.create(IMyService::class.java)

        //event
        Login.setOnClickListener {
            loginUser(EmailLogin.text.toString(), PasswordLogin.text.toString())
        }


        CreateAccount.setOnClickListener {
            val itemView = LayoutInflater.from(this@LoginActivity)
                .inflate(R.layout.activity_registrazione, null)

            MaterialStyledDialog.Builder(this@LoginActivity)
                .setIcon(R.drawable.ic_account)
                .setTitle("Registration")
                .setDescription("Please fill all fields")
                .setCustomView(itemView)
                .setNegativeText("Cancel")
                .onNegative { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveText("REGISTER")
                .onPositive(MaterialDialog.SingleButtonCallback{_, _ ->
                    val RegisterEmail = itemView.findViewById<View>(R.id.RegisterEmail) as EditText
                    val RegisterName = itemView.findViewById<View>(R.id.RegisterName) as EditText
                    val RegisterPassword = itemView.findViewById<View>(R.id.RegisterPassword) as EditText

                    if (TextUtils.isEmpty(RegisterEmail.text.toString())) {
                        Toast.makeText(this@LoginActivity, "Email can not be null or empty", Toast.LENGTH_SHORT).show()
                        return@SingleButtonCallback;
                    }
                    if (TextUtils.isEmpty(RegisterName.text.toString())) {
                        Toast.makeText(this@LoginActivity, "Name can not be null or empty", Toast.LENGTH_SHORT).show()
                        return@SingleButtonCallback;
                    }
                    if (TextUtils.isEmpty(RegisterPassword.text.toString())) {
                        Toast.makeText(this@LoginActivity, "Password can not be null or empty", Toast.LENGTH_SHORT).show()
                        return@SingleButtonCallback;
                    }

                    registerUser(RegisterEmail.text.toString(), RegisterName.text.toString(), RegisterPassword.text.toString())
                }).show()
        }
    }

    private fun registerUser(email: String, name: String, password: String) {

        compositeDisposable.addAll(iMyService.registerUser(email, name, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                Toast.makeText(this@LoginActivity, "" + result, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun loginUser(email: String, password: String) {

        //Check Empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(
                this@LoginActivity,
                "Email can not be null or empty",
                Toast.LENGTH_SHORT
            ).show()
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(
                this@LoginActivity,
                "Password can not be null or empty",
                Toast.LENGTH_SHORT
            ).show()
            return;
        }

        compositeDisposable.addAll(iMyService.loginUser(email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                Toast.makeText(this@LoginActivity, "" + result, Toast.LENGTH_SHORT).show()
            }
        )
    }


    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_access, menu)
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



