package com.example.huacaolu.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.huacaolu.R
import com.example.huacaolu.bean.ExplorePlantBean


class StaggeredGridAdapter(
    private val mContext: Context,
    private val resultList: MutableList<ExplorePlantBean.Result>,
) :
    RecyclerView.Adapter<StaggeredGridAdapter.ViewHolder>() {

    private lateinit var listener: StaggeredGridAdapter.OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.layout_staggere_grid_item, parent, false)
        )
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(mContext).load(resultList[position].imagePath).into(holder.mPlantImage)
        holder.mPlantName.text = resultList[position].name
        holder.mPlantImage.setOnClickListener(View.OnClickListener {
            listener.clickImage(resultList[position].url)
        })
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mPlantImage: ImageView = itemView.findViewById<View>(R.id.iv_plant_image) as ImageView
        val mPlantName: TextView = itemView.findViewById<View>(R.id.tv_plant_name) as TextView
    }

    interface OnItemClickListener {
        fun clickImage(url : String)
    }

    public fun setOnItemClickListener (listener : OnItemClickListener){
        this.listener = listener
    }
}
