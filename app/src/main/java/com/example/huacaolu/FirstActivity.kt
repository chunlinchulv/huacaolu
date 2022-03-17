package com.example.huacaolu

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class FirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        initView()
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