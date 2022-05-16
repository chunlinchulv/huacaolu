package com.example.huacaolu

import android.app.Application
import com.example.huacaolu.utils.DataBaseUtil

class MyApplication : Application() {
    private lateinit var instance : MyApplication
    override fun onCreate() {
        super.onCreate()
        instance = this
        DataBaseUtil.getInstance().createDataBase(this)
    }

    fun getInstance(): MyApplication {
        return instance
    }
}