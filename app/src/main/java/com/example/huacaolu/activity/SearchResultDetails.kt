package com.example.huacaolu.activity

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.huacaolu.R
import com.example.huacaolu.bean.SearchImagePlantBean
import com.google.gson.Gson

class SearchResultDetails : AppCompatActivity() {
    private lateinit var ivResultImage: ImageView
    private lateinit var tvResultUrl: TextView
    private lateinit var tvResultDesc: TextView
    private lateinit var tvResultName: TextView
    lateinit var searchImagePlantBean : SearchImagePlantBean.ResultDTO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result_details)
        initView()
        initData()
    }

    private fun initData() {
        val jsonString = intent.extras?.get("jsonString") as String
        searchImagePlantBean= Gson().fromJson(jsonString, SearchImagePlantBean.ResultDTO::class.java)

        tvResultName.text = searchImagePlantBean.name ?: ""
        tvResultDesc.text = searchImagePlantBean.baike_info?.description ?: ""
//        tvResultUrl.text = searchImagePlantBean.baike_info?.baike_url ?: ""
        Glide.with(this).load(searchImagePlantBean.baike_info?.image_url).centerCrop().into(ivResultImage)

    }

    private fun initView() {
        tvResultName = findViewById<TextView>(R.id.tv_title)
        tvResultDesc = findViewById<TextView>(R.id.plant_desc_result)
        tvResultUrl = findViewById<TextView>(R.id.plant_url_result)
        ivResultImage = findViewById<ImageView>(R.id.search_result_Image)
        tvResultUrl.paint.flags = Paint.UNDERLINE_TEXT_FLAG; //下划线
        tvResultUrl.paint.isAntiAlias = true;//抗锯齿
        tvResultUrl.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra("url",searchImagePlantBean.baike_info?.baike_url ?: "")
            startActivity(intent)
        })
    }
}
