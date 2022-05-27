package com.example.huacaolu

import android.app.Application
import android.content.Context
import android.util.Base64
import android.util.Log
import com.example.huacaolu.bean.ExplorePlantBean
import com.example.huacaolu.utils.DataBaseUtil
import com.google.gson.Gson
import java.io.IOException
import java.io.InputStream


class MyApplication : Application() {
    private lateinit var instance : MyApplication
    override fun onCreate() {
        super.onCreate()
        instance = this
        DataBaseUtil.getInstance().createDataBase(this)
        copyData()
    }

    fun getInstance(): MyApplication {
        return instance
    }

    private fun copyData(){
        if(getIsSave()){
            return
        }
        val jsonString = getJson("plant.json")
        val mExplorePlantBean : ExplorePlantBean = Gson().fromJson(jsonString, ExplorePlantBean::class.java)
        for (result in mExplorePlantBean.result) {
            saveData(result)
        }
        val sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isDataCopy",true)
        editor.apply()
        val boolean = sharedPreferences.getBoolean("isDataCopy", false)
        Log.e("MuApplication copyData", boolean.toString())
    }

    private fun getIsSave(): Boolean {
        val sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE)
        val boolean = sharedPreferences.getBoolean("isDataCopy", false)
        Log.e("MuApplication getIsSave", boolean.toString())
        return boolean
    }

    private fun saveData(result: ExplorePlantBean.Result) {
        val urlBase64String = Base64.encodeToString(result.url.toByteArray(),Base64.DEFAULT)
        val cursor  = DataBaseUtil.getInstance().queryByID(urlBase64String)
        if (cursor.count == 0) {
            val insert = DataBaseUtil.getInstance().insert(urlBase64String, result)
            Log.e("insert", insert.toString())
        }else {
            val delete = DataBaseUtil.getInstance().update(urlBase64String,result)
            Log.e("update", delete.toString())
        }
    }

    private fun getJson(fileName: String): String? {
        try {
            val inputStream: InputStream = assets.open(fileName)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            return String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}