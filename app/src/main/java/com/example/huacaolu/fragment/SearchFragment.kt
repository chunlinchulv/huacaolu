package com.example.huacaolu.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.baidu.aip.imageclassify.AipImageClassify
import com.example.huacaolu.R
import com.example.huacaolu.ui.MyPopupWindow
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


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

    lateinit var client:AipImageClassify

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
            }else {
                Toast.makeText(context,"请输入需要查询的花草名", Toast.LENGTH_SHORT).show()
            }
        }

        mIvTakePhoto.setOnClickListener {
            mPopupWindow.showPopupWindow(view)
        }
    }

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
                Toast.makeText(context, "未选择",Toast.LENGTH_SHORT).show()
            }
        })
        val width = activity?.windowManager?.defaultDisplay?.width
        mPopupWindow?.width = width!! - 200
    }

    private fun chooseImage() {
        val openAlbumIntent = Intent(Intent.ACTION_GET_CONTENT)
        openAlbumIntent.type = "image/*"
        startActivityForResult(openAlbumIntent, CHOOSE_PHOTO) //打开相册
    }

    // 拍照识别植物
    private fun takePhoto() {
        Toast.makeText(context,"打开相机",Toast.LENGTH_SHORT).show()
        outputImage = File(context?.externalCacheDir,"take_phopo_image.jpg")
        if (outputImage.exists()) {
            outputImage.delete()
        }
        outputImage.createNewFile()
        mImageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context!!,FILE_PROVIDER_AUTHORITY,outputImage)
        } else {
            Uri.fromFile(outputImage)
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //打开相机的Intent
        intent.putExtra(MediaStore.EXTRA_OUTPUT,mImageUri)
        startActivityForResult(intent, TAKE_PHOTO) //打开相机
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var imageFile: File? = null
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imageFile!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === RESULT_OK) {
            when (requestCode) {
                TAKE_PHOTO ->
                    try {
                        val bitmap = BitmapFactory.decodeStream(context?.contentResolver?.openInputStream(mImageUri))
                        /*如果拍照成功，将Uri用BitmapFactory的decodeStream方法转为Bitmap*/
                        Log.i(TAG, "onActivityResult: imageUri $mImageUri")
                        mIvShowImage.setImageBitmap(bitmap)
                        val res: JSONObject = client.objectDetect(mImageUri.path, HashMap<String, String>())
                        println(res.toString(2))
                        Log.e(TAG, "xuwenting" + res.toString(2))
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                CHOOSE_PHOTO -> {
                    Log.i(TAG, "onActivityResult: ImageUriFromAlbum: ")
                    val extras = data?.extras
                    if (extras != null) {
                        val bitmap: Bitmap = extras.get("data") as Bitmap
                        mIvShowImage.setImageBitmap(bitmap)
                    }
                }
                else -> {

                }
            }
        } else {

        }
    }

    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(outputImage.path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap,90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap,180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap,270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true)
        bitmap.recycle() // 将不再使用的Bitmap回收
        return rotatedBitmap
    }

    // 文字搜索植物
    private fun searchPlant(plant: String) {
        // TODO 获取string 搜索内容展示结果
        Log.e(TAG,plant)
    }

    companion object {
        // TODO: Rename and change types and number of parameters
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
