package com.example.huacaolu.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.example.huacaolu.R
import com.example.huacaolu.api.ParsePlant
import com.example.huacaolu.bean.SearchImagePlantBean
import com.example.huacaolu.utils.DataBaseUtil

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MyGardenFragment : Fragment(), ParsePlant.ParsePlantApiListener {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mImageUri : Uri
    lateinit var parsePlantApi : ParsePlant
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
    }
    private fun initApi() {
        parsePlantApi = ParsePlant()
        parsePlantApi.setApiListener(this)
    }

    private fun initView(view: View) {
/*        mIvSearch.setOnClickListener {
            hideKeyBoard()
            val searchText = mEtSearch.text.trim().toString()
            if (!TextUtils.isEmpty(searchText)) {
                searchPlant(searchText)
            } else {
                Toast.makeText(requireContext(), "请输入需要查询的花草名", Toast.LENGTH_SHORT).show()
            }
        }*/

    }

    @SuppressLint("Range")
    private fun searchPlant(plantName: String) {
        // TODO 获取string 搜索内容展示结果
        Log.e("plantName",plantName)
        val cursor  = DataBaseUtil.getInstance().queryByName(plantName)
        if (cursor.count == 0) {
            Log.e("insert", "没有查询到数据")
        }else {
            val resultArrayList : ArrayList<SearchImagePlantBean.ResultDTO?> = arrayListOf<SearchImagePlantBean.ResultDTO?>()
            val dataSearchImagePlantBean : SearchImagePlantBean = SearchImagePlantBean()
            while(cursor.moveToNext()){
                val imagePath = cursor.getString(cursor.getColumnIndex("imagePath"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val url = cursor.getString(cursor.getColumnIndex("url"))
                val id = cursor.getString(cursor.getColumnIndex("id"))
            }

            Log.e("delete", cursor.count.toString())
        }
    }

    private fun hideKeyBoard() {
        val imm : InputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity?.window?.peekDecorView()
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken,0)
        }
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

    override fun parsePlantSuccess(string: String, imagePath: String) {

    }

    override fun parsePlantSuccess(string: String, byteArray: ByteArray) {

    }

    override fun parsePlantFailure(string: String) {

    }
}