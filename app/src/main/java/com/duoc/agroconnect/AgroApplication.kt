package com.duoc.agroconnect

import android.app.Application

class AgroApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.iniciar(this)
    }
}
