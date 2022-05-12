package com.example.huacaolu.activity

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import com.example.huacaolu.R
import com.example.huacaolu.adapter.PlantResultAdapter
import com.example.huacaolu.bean.SearchImagePlantBean
import com.google.gson.Gson

class SearchResultActivity : AppCompatActivity() {
    val TAG = "SearchResultActivity"
    lateinit var searchImagePlantBean : SearchImagePlantBean
    lateinit var imageUrl: String
    lateinit var ivResultPlant : ImageView
    lateinit var lvResult : ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        initView()
        initData()
    }

    private fun initView() {
        lvResult = findViewById(R.id.lv_result)
        ivResultPlant = findViewById(R.id.search_result_Image)
        ivResultPlant.scaleType = ImageView.ScaleType.CENTER_CROP

    }

    private fun initData() {
        val jsonString = intent.extras?.get("jsonString") as String
        imageUrl = intent.extras?.get("imageUrl") as String
        searchImagePlantBean= Gson().fromJson(jsonString, SearchImagePlantBean::class.java)
        Log.e(TAG,searchImagePlantBean.toString())
        val dataList = searchImagePlantBean.getResult()
        lvResult.adapter = PlantResultAdapter(this,dataList)
        lvResult.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            Log.e(TAG, "onItemClickListener $position")
            val data = dataList?.get(position)
            data?.let { startActivityDetails(it) }
        }
        ivResultPlant.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
    }

    private fun startActivityDetails(resultDTO: SearchImagePlantBean.ResultDTO) {
        val intent = Intent(this, SearchResultDetails::class.java)
        intent.putExtra("jsonString",Gson().toJson(resultDTO))
        intent.putExtra("imageUrl",imageUrl)
        startActivity(intent)
    }
}