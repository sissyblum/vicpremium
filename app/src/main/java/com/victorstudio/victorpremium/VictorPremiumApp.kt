package com.victorstudio.victorpremium

import android.app.Application
import com.google.firebase.FirebaseApp

class VictorPremiumApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
