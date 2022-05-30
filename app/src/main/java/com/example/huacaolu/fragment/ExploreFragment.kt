package com.example.huacaolu.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.huacaolu.R
import com.example.huacaolu.activity.WebActivity
import com.example.huacaolu.adapter.StaggeredGridAdapter
import com.example.huacaolu.bean.ExplorePlantBean
import com.example.huacaolu.helper.DataBasePlantHelper
import com.example.huacaolu.utils.Base64Util
import com.example.huacaolu.utils.DataBaseUtil


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ExploreFragment : Fragment(), StaggeredGridAdapter.OnItemClickListener {
    private var mRecyclerView : RecyclerView? = null
    private var param1: String? = null
    private var param2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explore_recycler_view, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            ExploreFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initData()
    }

    private fun initData() {
        Log.e("initData","initData")
        val result : MutableList<ExplorePlantBean.Result> = getData()
        mRecyclerView?.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        val staggeredGridAdapter = StaggeredGridAdapter(requireContext(), result)
        staggeredGridAdapter.setOnItemClickListener(this)
        mRecyclerView?.adapter = staggeredGridAdapter
        mRecyclerView?.itemAnimator?.changeDuration = 0
    }

    @SuppressLint("Range")
    private fun getData(): MutableList<ExplorePlantBean.Result> {
        val resultList : MutableList<ExplorePlantBean.Result> = arrayListOf<ExplorePlantBean.Result>()
        val cursor  = DataBaseUtil.getInstance().queryAll()
        if (cursor.moveToFirst()){
            do {
                val result : ExplorePlantBean.Result = ExplorePlantBean.Result()
                val id = cursor.getInt(cursor.getColumnIndex(DataBasePlantHelper.ID))
                val imagePath = cursor.getString(cursor.getColumnIndex(DataBasePlantHelper.IMAGE_PATH))
                val name = cursor.getString(cursor.getColumnIndex(DataBasePlantHelper.NAME))
                val url = cursor.getString(cursor.getColumnIndex(DataBasePlantHelper.URL))
                val fabulous = cursor.getInt(cursor.getColumnIndex(DataBasePlantHelper.FABULOUS))
                val collection = cursor.getInt(cursor.getColumnIndex(DataBasePlantHelper.COLLECTION))
                result.id = id
                result.imagePath = imagePath
                result.name = name
                result.url = url
                result.fabulous = fabulous
                result.collection = collection
                resultList.add(result)
            }while (cursor.moveToNext())
        }
        return resultList
    }

    private fun initView(view: View) {
        mRecyclerView = view.findViewById<RecyclerView>(R.id.rv_pu)

    }

    override fun clickImage(url: String) {
        Log.e("ExplorePlantBean",url)
        if (!TextUtils.isEmpty(url)) {
            val intent = Intent(requireContext(), WebActivity::class.java)
            intent.putExtra("url",url)
            startActivity(intent)
        }
    }

    override fun clickLike(explorePlantBean: ExplorePlantBean.Result) {
        val urlBase64String = Base64.encodeToString(explorePlantBean.url.toByteArray(), Base64.DEFAULT)
        Log.e("clickLike",urlBase64String)
        val cursor  = DataBaseUtil.getInstance().queryByID(urlBase64String)
        if (cursor.moveToFirst()){
            do {
                if (explorePlantBean.fabulous == 0) {
                    explorePlantBean.fabulous = 1
                }else{
                    explorePlantBean.fabulous = 0
                }
                val update = DataBaseUtil.getInstance().update(urlBase64String, explorePlantBean)
                Log.e("update", update.toString())
            }while (cursor.moveToNext())
        }else{
            Log.e("delete", "没有查询到该数据")
        }
        cursor.close()
    }

    override fun clickColl(explorePlantBean: ExplorePlantBean.Result) {
        val urlBase64String = Base64.encodeToString(explorePlantBean.url.toByteArray(), Base64.DEFAULT)
        Log.e("clickLike",urlBase64String)
        val cursor  = DataBaseUtil.getInstance().queryByID(urlBase64String)
        if (cursor.moveToFirst()){
            do {
                if (explorePlantBean.collection == 0) {
                    explorePlantBean.collection = 1
                }else{
                    explorePlantBean.collection = 0
                }
                val update = DataBaseUtil.getInstance().update(urlBase64String, explorePlantBean)
                Log.e("update", update.toString())
            }while (cursor.moveToNext())
        }else{
            Log.e("delete", "没有查询到该数据")
        }
        cursor.close()
    }


}