package com.example.huacaolu.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.huacaolu.R
import com.example.huacaolu.adapter.MyCollectionAdapter
import com.example.huacaolu.bean.ExplorePlantBean
import com.example.huacaolu.helper.DataBasePlantHelper
import com.example.huacaolu.utils.DataBaseUtil

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MyGardenFragment : Fragment(){
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mCollectionList: ArrayList<ExplorePlantBean.Result>
    private lateinit var mCollection : ListView
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
        return inflater.inflate(R.layout.fragment_my_garden, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initApi()
        initView(view)
        initData()
    }

    private fun initData() {
        mCollectionList = getData() as ArrayList<ExplorePlantBean.Result>
        mCollection.adapter = MyCollectionAdapter(mCollectionList,requireContext())

    }

    private fun initApi() {
    }

    private fun initView(view: View) {
        mCollection = view.findViewById<ListView>(R.id.lv_collection)

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            MyGardenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @SuppressLint("Range")
    private fun getData(): List<ExplorePlantBean.Result> {
        val resultList : MutableList<ExplorePlantBean.Result> = arrayListOf<ExplorePlantBean.Result>()
        val cursor  = DataBaseUtil.getInstance().queryAll()
        if (cursor.moveToFirst()){
            do {
                val collection = cursor.getInt(cursor.getColumnIndex(DataBasePlantHelper.COLLECTION))
                if (collection == 1) {
                    val result: ExplorePlantBean.Result = ExplorePlantBean.Result()
                    val id = cursor.getInt(cursor.getColumnIndex(DataBasePlantHelper.ID))
                    val imagePath =
                        cursor.getString(cursor.getColumnIndex(DataBasePlantHelper.IMAGE_PATH))
                    val name = cursor.getString(cursor.getColumnIndex(DataBasePlantHelper.NAME))
                    val url = cursor.getString(cursor.getColumnIndex(DataBasePlantHelper.URL))
                    val fabulous =
                        cursor.getInt(cursor.getColumnIndex(DataBasePlantHelper.FABULOUS))
                    result.id = id
                    result.imagePath = imagePath
                    result.name = name
                    result.url = url
                    result.fabulous = fabulous
                    result.collection = collection
                    resultList.add(result)
                }
            }while (cursor.moveToNext())
        }
        return resultList
    }

}