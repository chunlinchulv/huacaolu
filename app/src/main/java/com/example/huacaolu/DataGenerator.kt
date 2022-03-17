package com.example.huacaolu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.huacaolu.fragment.ExploreFragment
import com.example.huacaolu.fragment.MyGardenFragment
import com.example.huacaolu.fragment.SearchFragment

class DataGenerator {
    companion object {
        val mTabRes = arrayOf(R.drawable.explore,R.drawable.search,R.drawable.my_garden)
        val mTabResPress = arrayOf(R.drawable.explore_press,R.drawable.search_press,R.drawable.my_garden_press)
        private val mTabTitle = arrayOf("探索","识别","我的花园")

        fun getFragments(position: String?): Array<Fragment> {
            return arrayOf(
                ExploreFragment.newInstance(position.toString()),
                SearchFragment.newInstance(position.toString()),
                MyGardenFragment.newInstance(position.toString())
            )
        }

        // 获取Tab显示的内容
        fun getTabView(context: Context,position: Int): View? {
            val view = LayoutInflater.from(context).inflate(R.layout.bottom_tab_layout_content,null)
            val tabIcon = view.findViewById<ImageView>(R.id.tab_content_image)
            tabIcon.setImageResource(mTabRes[position])
            val tabText = view.findViewById<TextView>(R.id.tab_content_text)
            tabText.text = mTabTitle[position]
            return view
        }

    }
}