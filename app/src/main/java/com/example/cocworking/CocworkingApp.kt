package com.example.cocworking

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen


class CocworkingApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize ThreeTenABP library for backward compatibility of java.time classes (added in Java 8) used in calendar
        AndroidThreeTen.init(this)
    }

}