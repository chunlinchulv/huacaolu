package com.example.huacaolu.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.baidu.aip.imageclassify.AipImageClassify
import com.example.huacaolu.R
import com.example.huacaolu.ui.MyPopupWindow
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.app.Activity.RESULT_OK
import android.graphics.Picture
import android.widget.Toast
import com.example.huacaolu.api.ParsePlant


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SearchFragment : Fragment() {

    private val TAG = "SearchFragment"
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mImageUri: Uri
    lateinit var outputImage: File  // 创建File对象 存储照片

    // 拍照识别
    val TAKE_PHOTO = 1
    val CHOOSE_PHOTO = 2

    //在注册表中配置的provider
    val FILE_PROVIDER_AUTHORITY = "com.example.huacaolu.fileProvider"

    lateinit var mPopupWindow: MyPopupWindow
    lateinit var mIvShowImage: ImageView
    lateinit var mIvSearch: ImageView
    lateinit var mIvTakePhoto: ImageView
    lateinit var mEtSearch: EditText

    lateinit var client: AipImageClassify

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
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAPI()
        initView(view)
        initPopWindow()

    }

    private fun initAPI() {
        // 初始化一个AipImageClassify
        client = AipImageClassify(APP_ID, API_KEY, SECRET_KEY)
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000)
        client.setSocketTimeoutInMillis(60000)
    }

    private fun initView(view: View) {
        mIvSearch = view.findViewById<ImageView>(R.id.search_button)
        mIvTakePhoto = view.findViewById<ImageView>(R.id.search_take_photo)
        mEtSearch = view.findViewById<EditText>(R.id.search_edit_text)
        mIvShowImage = view.findViewById<ImageView>(R.id.iv_show_image)
        mIvSearch.setOnClickListener {
            val searchText = mEtSearch.text.toString()
            if (!TextUtils.isEmpty(searchText)) {
                searchPlant(searchText)
            } else {
                Toast.makeText(context, "请输入需要查询的花草名", Toast.LENGTH_SHORT).show()
            }
        }

        mIvTakePhoto.setOnClickListener {
            mPopupWindow.showPopupWindow(view)
        }
    }
    // 初始化popupWindow
    private fun initPopWindow() {
        mPopupWindow = MyPopupWindow(activity!!)
        mPopupWindow?.setPopupWindowCallBack(object : MyPopupWindow.CallBack {
            override fun clickTakePhoto() {
                takePhoto()
            }

            override fun clickChooseImage() {
                chooseImage()
            }

            override fun clickCancel() {
                Toast.makeText(context, "未选择", Toast.LENGTH_SHORT).show()
            }
        })
        val width = activity?.windowManager?.defaultDisplay?.width
        mPopupWindow?.width = width!! - 200
    }

    // 选择图片
    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, CHOOSE_PHOTO)
    }

    // 拍照
    private fun takePhoto() {
        Toast.makeText(context, "打开相机", Toast.LENGTH_SHORT).show()
        outputImage = File(context?.externalCacheDir, "take_phopo_image.jpg")
        if (outputImage.exists()) {
            outputImage.delete()
        }
        outputImage.createNewFile()
        mImageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context!!, FILE_PROVIDER_AUTHORITY, outputImage)
        } else {
            Uri.fromFile(outputImage)
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //打开相机的Intent
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
        startActivityForResult(intent, TAKE_PHOTO) //打开相机
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === RESULT_OK) {
            when (requestCode) {
                TAKE_PHOTO -> {
                    /*如果拍照成功，将Uri用BitmapFactory的decodeStream方法转为Bitmap*/
                    val bitmap = BitmapFactory.decodeStream(
                        context?.contentResolver?.openInputStream(mImageUri)
                    )
                    Log.e(TAG, "onActivityResult: imageUri =  $mImageUri")
                    mIvShowImage.setImageBitmap(bitmap)
                    val filePath = mImageUri.path.toString().replace("/root/","")
                    Log.e(TAG, "onActivityResult: filePath = $filePath")
                    ParsePlant.plant(filePath,object : ParsePlant.ParsePlantCallback{
                        override fun parsePlantSuccess(string: String) {
                            Log.e(TAG, "onActivityResult: parsePlantSuccess = $string")
                        }

                        override fun parsePlantFailure(string: String) {
                            Log.e(TAG, "onActivityResult: parsePlantFailure = $string")
                        }
                    })
                }
                CHOOSE_PHOTO -> {
                    Log.e(TAG, "onActivityResult: ImageUriFromAlbum: ")
                    mImageUri = data?.data!!
                    mImageUri?.apply {
                        //从系统表中查询指定Uri对应的照片
                        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                        val cursor = context?.contentResolver?.query(mImageUri!!, filePathColumn, null, null, null)
                        cursor?.moveToFirst()
                        //获取照片路径
                        val filePath = cursor?.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                        cursor?.close()
                        val inputStream = context?.contentResolver?.openInputStream(mImageUri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        mIvShowImage.setImageBitmap(bitmap)
                        Log.e(TAG, "onActivityResult: filePath = $filePath")
                        ParsePlant.plant(filePath.toString(),object : ParsePlant.ParsePlantCallback{
                            override fun parsePlantSuccess(string: String) {
                                Log.e(TAG, "onActivityResult: parsePlantSuccess = $string")
                            }

                            override fun parsePlantFailure(string: String) {
                                Log.e(TAG, "onActivityResult: parsePlantFailure = $string")
                            }

                        })
                    }

                }
                else -> {

                }
            }
        } else {

        }
    }


    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle() // 将不再使用的Bitmap回收
        return rotatedBitmap
    }

    // 文字搜索植物
    private fun searchPlant(plant: String) {
        // TODO 获取string 搜索内容展示结果
        Log.e(TAG, plant)
    }

    companion object {
        const val APP_ID = "25964377"
        const val API_KEY = "U65Bwu0iGBsfOjV7uWTArjvf"
        const val SECRET_KEY = "U65Bwu0iGBsfOjV7uWTArjvf"

        @JvmStatic
        fun newInstance(param1: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}
