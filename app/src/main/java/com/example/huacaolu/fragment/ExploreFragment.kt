package com.example.huacaolu.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
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
import com.example.huacaolu.utils.Base64Util
import com.example.huacaolu.utils.DataBaseUtil
import com.google.gson.Gson
import java.io.IOException
import java.io.InputStream
import java.util.*


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
        val jsonString = getJson("plant.json")
        Log.e("ExplorePlantBean",jsonString!!)
        val mExplorePlantBean : ExplorePlantBean = Gson().fromJson(jsonString, ExplorePlantBean::class.java)
        mRecyclerView?.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        val staggeredGridAdapter = StaggeredGridAdapter(requireContext(), mExplorePlantBean.result)
        staggeredGridAdapter.setOnItemClickListener(this)
        mRecyclerView?.adapter = staggeredGridAdapter
        mRecyclerView?.itemAnimator?.changeDuration = 0
    }

    private fun initView(view: View) {
        mRecyclerView = view.findViewById<RecyclerView>(R.id.rv_pu)

    }

    private fun getJson(fileName: String): String? {
        try {
            val inputStream: InputStream = requireContext().assets.open(fileName)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            return String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun clickImage(url: String) {
        Log.e("ExplorePlantBean",url)
        if (!TextUtils.isEmpty(url)) {
            val intent = Intent(requireContext(), WebActivity::class.java)
            intent.putExtra("url",url)
            startActivity(intent)
        }
    }

    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun clickLike(explorePlantBean: ExplorePlantBean.Result) {
        val urlBase64String = Base64Util.encodeString(explorePlantBean.url)
        Log.e("clickLike",urlBase64String)
        val cursor  = DataBaseUtil.getInstance().query(urlBase64String)
        if (cursor.count == 0) {
            val insert = DataBaseUtil.getInstance().insert(urlBase64String, explorePlantBean)
            Log.e("insert", insert.toString())
        }else {
            val delete = DataBaseUtil.getInstance().delete(urlBase64String)
            Log.e("delete", delete.toString())
        }
//        while(cursor .moveToNext()){
//            val id = cursor .getString(cursor .getColumnIndex("id"))
//            val imagePath = cursor .getString(cursor .getColumnIndex("imagePath"))
//            val name = cursor .getString(cursor .getColumnIndex("name"))
//            val url = cursor .getString(cursor .getColumnIndex("url"))
//            Log.e("clickLike", "id = $id")
//            Log.e("clickLike", "imagePath = $imagePath")
//            Log.e("clickLike", "name = $name")
//            Log.e("clickLike", "url = $url")
//        }
        cursor.close()
    }



}