package com.example.huacaolu

import android.app.Application
import com.example.huacaolu.utils.DataBaseUtil

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DataBaseUtil.getInstance().createDataBase(this)
    }
}