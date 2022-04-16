package com.example.huacaolu.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.huacaolu.ui.DataGenerator
import com.example.huacaolu.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class MainActivity : AppCompatActivity() {

    private lateinit var mFragments: Array<Fragment>
    private lateinit var fragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFragments = DataGenerator.getFragments("TabLayout Tab")
        initView()
    }

    private fun initView() {
        val mTabLayout = findViewById<TabLayout>(R.id.bottom_tab_layout)
        mTabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let { onTabItemSelected(it.position) }
                // Tab选中之后，改变各个Tab的状态
                for (i in 0.. mTabLayout.tabCount) {
                    val view = mTabLayout.getTabAt(i)?.customView
                    val icon = view?.findViewById<ImageView>(R.id.tab_content_image)
                    if (i == tab?.position) { // Tab选中状态
                        icon?.setImageResource(DataGenerator.mTabResPress[i])
                    }else { // Tab未选中状态
                        icon?.setImageResource(DataGenerator.mTabRes[i])
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        // 提供自定义的布局添加Tab
        for (i in 0.. 2) {
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(DataGenerator.getTabView(this, i)))
        }

    }


    private fun onTabItemSelected(position: Int) {
        when(position) {
            0 -> fragment = mFragments[0]
            1 -> fragment = mFragments[1]
            2 -> fragment = mFragments[2]
        }
        supportFragmentManager.beginTransaction().replace(R.id.home_container,fragment).commit()
    }

}