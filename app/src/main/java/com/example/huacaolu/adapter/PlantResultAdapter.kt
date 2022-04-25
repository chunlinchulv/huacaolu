package com.example.huacaolu.adapter

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.huacaolu.R
import com.example.huacaolu.bean.PlantBean

class PlantResultAdapter(context: Context,dataList: ArrayList<PlantBean.ResultDTO?>?) : BaseAdapter() {
    private var context: Context = context
    private var dataList: ArrayList<PlantBean.ResultDTO?>? = dataList
    private var layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int {
        return dataList?.size!!
    }

    override fun getItem(p0: Int): Any {
        return dataList!!?.get(p0)!!
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View? {
        var view = p1
        if (p1 == null) {
            view = layoutInflater.inflate(R.layout.search_result_item,null)
            val tvResultName = view.findViewById(R.id.plant_name_result) as TextView
            val tvResultDesc = view.findViewById(R.id.plant_desc_result) as TextView
            val tvResultUrl = view.findViewById(R.id.plant_url_result) as TextView
            val ivResultImage = view.findViewById(R.id.plant_image) as ImageView
            tvResultName.text = dataList?.get(p0)?.name ?: ""
            tvResultDesc.text = dataList?.get(p0)?.baike_info?.description ?: ""
            tvResultUrl.text = dataList?.get(p0)?.baike_info?.baike_url ?: ""
            Glide.with(context).load(dataList?.get(p0)?.baike_info?.image_url).centerCrop().into(ivResultImage)
        }
        return view
    }

}