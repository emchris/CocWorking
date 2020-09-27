package com.example.cocworking

import android.content.Context
import android.content.SharedPreferences

class MyPreference(context : Context) {

    val PREFERENCE_NAME = "SharedPreference"
    val PREFERENCE_ACCOUNT = "AccountInfo"
    val PREFERENCE_EMAIL = "AccountEmail"
    val SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)


    fun setAccountInfo(name : String?, email : String?){
        val editor = SharedPreferences.edit() // edit Shared Preferences to put data
        editor.putString(PREFERENCE_ACCOUNT, name)
        editor.putString(PREFERENCE_EMAIL, email)
        editor.apply() //apply changes to shared preferences
    }

    fun getAccountInfo() : String? { //get data from shared preferences
        return SharedPreferences.getString(PREFERENCE_ACCOUNT, "")
    }

    fun getPreferenceEmail() : String? { //get data from shared preferences
        return SharedPreferences.getString(PREFERENCE_EMAIL, "")
    }

    fun deleteAccountInfo(name : String?, email : String?){
        val editor = SharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

}