package com.example.huacaolu

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.UserDictionary.Words
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONObject
import com.baidu.aip.imageclassify.AipImageClassify
import javax.crypto.Cipher


class FirstActivity : AppCompatActivity() {
    companion object {
        const val APP_ID = "25964377"
        const val API_KEY = "U65Bwu0iGBsfOjV7uWTArjvf"
        const val SECRET_KEY = "U65Bwu0iGBsfOjV7uWTArjvf"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAPI() // 百度API初始化
        setContentView(R.layout.activity_first)
        initView()
    }

    private fun initAPI() {
        // 初始化一个AipImageClassify
        val client = AipImageClassify(Words.APP_ID, API_KEY, SECRET_KEY)
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000)
        client.setSocketTimeoutInMillis(60000)
        // 调用接口
        val path = "test.jpg"
        val res: JSONObject = client.objectDetect(path, HashMap<String, String>())
        println(res.toString(2))
    }

    private fun initView() {
        setTF() // 主页字体
        startHome() //跳转主页
    }

    private fun startHome() {
        val constraintLayout = findViewById<ConstraintLayout>(R.id.beginDesign)
        val intent = Intent(this,MainActivity::class.java)
        constraintLayout.setOnClickListener {
            startActivity(intent)
        }
    }

    private fun setTF() {
        val nameTextView = findViewById<TextView>(R.id.nameTextView)
        val tf = Typeface.createFromAsset(assets,"HYXiXingKaiJ.ttf")
        nameTextView.typeface = tf
    }
}