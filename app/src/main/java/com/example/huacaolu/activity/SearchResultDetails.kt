package com.example.huacaolu.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.huacaolu.R
import com.example.huacaolu.bean.PlantBean
import com.google.gson.Gson

class SearchResultDetails : AppCompatActivity() {
    private lateinit var ivResultImage: ImageView
    private lateinit var tvResultUrl: TextView
    private lateinit var tvResultDesc: TextView
    private lateinit var tvResultName: TextView
    lateinit var plantBean : PlantBean.ResultDTO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result_details)
        initView()
        initData()
    }

    private fun initData() {
        val jsonString = intent.extras?.get("jsonString") as String
        plantBean= Gson().fromJson(jsonString, PlantBean.ResultDTO::class.java)

        tvResultName.text = plantBean.name ?: ""
        tvResultDesc.text = plantBean.baike_info?.description ?: ""
        tvResultUrl.text = plantBean.baike_info?.baike_url ?: ""
        Glide.with(this).load(plantBean.baike_info?.image_url).centerCrop().into(ivResultImage)

    }

    private fun initView() {
        tvResultName = findViewById<TextView>(R.id.tv_title)
        tvResultDesc = findViewById<TextView>(R.id.plant_desc_result)
        tvResultUrl = findViewById<TextView>(R.id.plant_url_result)
        ivResultImage = findViewById<ImageView>(R.id.search_result_Image)

    }
}
