package com.example.huacaolu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.huacaolu.R
import com.example.huacaolu.bean.ExplorePlantBean

class MyCollectionAdapter(mCollectionList: ArrayList<ExplorePlantBean.Result>, mContext: Context) : BaseAdapter() {
    private var mCollectionList: ArrayList<ExplorePlantBean.Result> = mCollectionList
    private var mContext : Context = mContext

    override fun getCount(): Int {
        return mCollectionList.size
    }

    override fun getItem(position: Int): Any {
        return mCollectionList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder: ViewHolder? = null
        var view : View?
        if (convertView == null) {
            holder = ViewHolder()
            view = LayoutInflater.from(mContext).inflate(R.layout.my_graden_item,null)
            holder.image = view.findViewById(R.id.iv_image)
            holder.title = view.findViewById(R.id.tv_title)
            view.tag = holder
        }else{
            view = convertView
            holder = view.tag as ViewHolder
        }
        Glide.with(mContext).load(mCollectionList[position].imagePath).into(holder.image)
        holder.title.text = mCollectionList[position].name
        return view!!
    }

    class ViewHolder {
        lateinit var image : ImageView
        lateinit var title : TextView
    }
}