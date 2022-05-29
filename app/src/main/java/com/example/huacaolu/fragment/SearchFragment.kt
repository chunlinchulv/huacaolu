package com.example.huacaolu.fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.helper.widget.Layer
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.huacaolu.R
import com.example.huacaolu.activity.RealTimeIdentsActivity
import com.example.huacaolu.activity.SearchResultActivity
import com.example.huacaolu.activity.SearchResultDetails
import com.example.huacaolu.api.ParsePlant
import com.example.huacaolu.bean.SearchImagePlantBean
import com.example.huacaolu.ui.MyPopupWindow
import com.google.gson.Gson
import java.io.FileOutputStream
import java.io.IOException


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SearchFragment : Fragment(),
    ParsePlant.ParsePlantApiListener {

    private val TAG = "SearchFragment"
    private var param1: String? = null
    private var param2: String? = null


    // 拍照识别
    val TAKE_PHOTO = 1
    val CHOOSE_PHOTO = 2
    val CROP_IMAGE = 3
    val HANDLERCROPIMAGE = 4

    //在注册表中配置的provider
    val FILE_PROVIDER_AUTHORITY = "com.example.huacaolu.fileProvider"
    lateinit var mImageUri: Uri
    lateinit var mPopupWindow: MyPopupWindow
    lateinit var mIvShowImage: ImageView
    lateinit var mIvTakePhoto: ImageView
    private lateinit var mPoetryTextView: TextView
    lateinit var mIvSelectAlbum: ImageView
    lateinit var mIvSelectScan: Layer
    lateinit var imageUrl: String
    lateinit var parsePlantApi: ParsePlant
    var isParsePlantSuccessByByteArray: Boolean = false
    private var isDestroy: Boolean = false
    private var isPause: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                HANDLERCROPIMAGE -> {
                    Log.e(TAG, "Handler what =  ${msg.what} obj = ${msg.obj.toString()}")
                    imageUrl = msg.obj.toString()
                    Handler(Looper.getMainLooper()).post {
                        showImage(imageUrl)
//                    parsePlantApi.parsePlantByHttp(imageUrl)
//                    parsePlantApi.parsePlantByLocalPath(imageUrl)
                        parsePlantApi.parsePlantByFilePath(imageUrl)
                    }

                }
            }
        }
    }


    private fun showImage(filePath: String) {
        Glide.with(requireContext()).load(filePath).centerCrop().into(mIvShowImage)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initApi()
        initView(view)
        initPopWindow()
    }

    override fun onResume() {
        super.onResume()
        isDestroy = false
        isPause = false
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    override fun onDestroy() {
        super.onDestroy()
        isDestroy = true
    }

    private fun initApi() {
        parsePlantApi = ParsePlant()
        parsePlantApi.setApiListener(this)
    }

    private fun initView(view: View) {
        val mFindYourPlant = view.findViewById<TextView>(R.id.search_find_text)
        mIvTakePhoto = view.findViewById(R.id.search_take_photo)
        mIvSelectAlbum = view.findViewById(R.id.search_album)
        mIvSelectScan = view.findViewById(R.id.layer)
        mIvShowImage = view.findViewById(R.id.iv_show_image)
        mPoetryTextView = view.findViewById(R.id.search_poetry_text)
        mIvShowImage.scaleType = ImageView.ScaleType.CENTER_CROP
        val mEtSearch = view.findViewById<TextView>(R.id.search_edit_text)

        mIvTakePhoto.setOnClickListener {
            hideKeyBoard()
            takePhoto()
        }
        mIvSelectAlbum.setOnClickListener {
            hideKeyBoard()
            chooseImage()
        }
        mIvSelectScan.setOnClickListener {
            hideKeyBoard()
            val intent = Intent(activity, RealTimeIdentsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun hideKeyBoard() {
        val imm: InputMethodManager =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity?.window?.peekDecorView()
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun initPopWindow() {
        mPopupWindow = MyPopupWindow(requireContext())
        mPopupWindow.setPopupWindowCallBackListener(object : MyPopupWindow.CallBack {
            override fun clickTakePhoto() {
                takePhoto()
            }

            override fun clickChooseImage() {
                chooseImage()
            }

            override fun clickCancel() {
                Toast.makeText(requireContext(), "未选择", Toast.LENGTH_SHORT).show()
            }
        })
        val width = activity?.windowManager?.defaultDisplay?.width
        mPopupWindow.width = width!! - 200
    }


    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, CHOOSE_PHOTO)
    }


    private fun takePhoto() {
        Toast.makeText(requireContext(), "打开相机", Toast.LENGTH_SHORT).show()
        val outputImage =
            requireContext().getExternalFilesDir("IMG_" + System.currentTimeMillis() + ".jpg")!!
        if (outputImage.exists()) {
            outputImage.delete()
        }
        outputImage.createNewFile()
        mImageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(requireContext(), FILE_PROVIDER_AUTHORITY, outputImage)
        } else {
            Uri.fromFile(outputImage)
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //打开相机的Intent
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
        startActivityForResult(intent, TAKE_PHOTO) //打开相机
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === RESULT_OK) {
            when (requestCode) {
                TAKE_PHOTO -> {
                    getImage(mImageUri)
                }
                CHOOSE_PHOTO, CROP_IMAGE -> {
                    Log.e(TAG, "选取图片requestCode =  $requestCode")
                    getImage(data)
                }
                else -> {

                }
            }
        }
    }

    /**
     *  这里是因为某些机型上拍照返回的Intent数据data是空的，(有可能是takePhoto的参数传递有问题)
     *  所以只能选择拍照时保存的uri作为参数
     *  但是改参数的path属性会在储存卡前面带上/root/，
     *  如/root/storage/emulated/0/Android/data/com.example.huacaolu/files/IMG_1650202550208.jpg
     *  这样的地址在读取数据的时候会造成FileNotFoundException,
     *  所以需要replace掉/root/
     */
    private fun getImage(imageUri: Uri) {
        val filePath = imageUri.path.toString().replace("/root/", "")
        Log.e(TAG, "拍照返回的照片 filePath =  $filePath")
        pictureCropping(filePath)
    }

    @SuppressLint("Range")
    private fun getImage(data: Intent?) {
        val uri = data?.data!!
        uri.apply {
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor =
                requireContext().contentResolver?.query(uri, filePathColumn, null, null, null)
            cursor?.moveToFirst()
            val filePath = cursor?.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            cursor?.close()
            filePath?.let { pictureCropping(it) }
        }
    }

    // 对图片进行裁剪
    private fun pictureCropping(filePath: String?) {
        val result = filePath?.let { getPictureSize(it) }!!
        if (result == -1) {
            Toast.makeText(requireContext(), "图片选择不符合标准，请重新选择", Toast.LENGTH_SHORT).show()
            return
        }
        // 裁剪是耗时操作，需要在子线程中进行，在裁剪完之后需要使用handler去通知其他需要使用裁剪结果的对象，告知结果可使用了。
        // 一般这种在异步操作，在本类中使用handler，如果是外部需要结果，则使用接口回调，参考ParsePlant的结果回调
        object : Thread() {
            override fun run() {
                super.run()
                val bitmap =
                    Glide.with(requireContext()).asBitmap().load(filePath).submit(result, result)
                        .get()
                val file =
                    requireContext().getExternalFilesDir("IMG_" + System.currentTimeMillis() + ".jpg")!!
                if (file.exists()) {
                    file.delete()
                }
                val outputStream = FileOutputStream(file)
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, outputStream)
                    outputStream.flush()
                    outputStream.close()
                    if (TextUtils.isEmpty(file.path.toString())) {
                        Log.e(TAG, "图片裁剪失败，请重新尝试")
                        return
                    }
                    val message = mHandler.obtainMessage()
                    message.what = HANDLERCROPIMAGE
                    message.obj = file.path.toString()
                    mHandler.handleMessage(message)
                } catch (e: IOException) {
                    Toast.makeText(requireContext(), "图片裁剪失败，请重新尝试", Toast.LENGTH_SHORT).show()
                }

            }
        }.start()
    }

    // 获取图片需要裁剪的大小
    // 此处主要是API的要求，图片不能长和宽皆不能小于15px，同时不能大于4096px
    private fun getPictureSize(path: String): Int {
        val bitmap = BitmapFactory.decodeFile(path)
        val height = bitmap.height
        val width = bitmap.width
        Log.e(TAG, "通过bitmap获取到的图片大小width: $width height: $height")
        return if (width > 4096 && height > 4096) {
            4096
        } else if (height < 15 || width < 15) {
            -1
        } else {
            if (width > height) {
                height
            } else {
                width
            }
        }
    }

    // 系统裁剪，有bug：在图片过大的时候会卡住主线程，同样也会造成OOM
    private fun pictureCropping(uri: Uri) {
        val intent = Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 4096)
        intent.putExtra("outputY", 4096)
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_IMAGE);
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
        parsePlantApi.parsePlantWithWords(plant)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getResult(jsonString: String, imagePath: String?, byteArray: ByteArray?) {
        if (isDestroy || isPause) {
            // 异步操作 不建议弹toast，在运行到此处之前关闭了应用切入后台，则会crash，报IllegalStateException,所以加入判断进行拦截
            return
        }
        val plantBean = Gson().fromJson(jsonString, SearchImagePlantBean::class.java)
        val results = plantBean.getResult()
        if (results == null || results.size == 0) {
            if (!TextUtils.isEmpty(imagePath)) {
                Toast.makeText(requireContext(), "数据解析失败，请重新尝试", Toast.LENGTH_SHORT).show()
            }
            isParsePlantSuccessByByteArray = false
            return
        }
        if (results[0]?.baike_info == null || TextUtils.equals("非植物", results[0]?.name)) {
            if (!TextUtils.isEmpty(imagePath)) {
                Toast.makeText(requireContext(), "${results[0]?.name} 请重试 ", Toast.LENGTH_SHORT)
                    .show()
            }
            isParsePlantSuccessByByteArray = false
            return
        }
        if (byteArray != null) {
            isParsePlantSuccessByByteArray = true
        }
        val dataSearchImagePlantBean: SearchImagePlantBean = SearchImagePlantBean()
        val resultArrayList: ArrayList<SearchImagePlantBean.ResultDTO?> =
            arrayListOf<SearchImagePlantBean.ResultDTO?>()
        for (data: SearchImagePlantBean.ResultDTO? in results) {
            if (data?.score!! > 0.05) {
                resultArrayList.add(data)
            }
        }
        dataSearchImagePlantBean.setLog_id(plantBean.getLog_id())
        dataSearchImagePlantBean.setResult(resultArrayList)
        if (resultArrayList.size == 1) {
            startActivityDetails(resultArrayList[0]!!, imagePath, byteArray)
        } else if (resultArrayList.size > 1) {
            startSearchResultActivity(Gson().toJson(dataSearchImagePlantBean), imagePath, byteArray)
        } else {

            Toast.makeText(requireContext(), "没有数据", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startActivityDetails(
        resultDTO: SearchImagePlantBean.ResultDTO,
        imagePath: String?,
        byteArray: ByteArray?
    ) {
        Log.e(TAG, "startActivityDetails: $imagePath")
        val intent = Intent(requireContext(), SearchResultDetails::class.java)
        intent.putExtra("jsonString", Gson().toJson(resultDTO))
        startActivity(intent)
    }

    private fun startSearchResultActivity(
        jsonString: String,
        imagePath: String?,
        byteArray: ByteArray?
    ) {
        Log.e(TAG, "startSearchResultActivity:")
        val intent = Intent(requireContext(), SearchResultActivity::class.java)
        intent.putExtra("jsonString", jsonString)
        startActivity(intent)
    }

    override fun parsePlantSuccess(string: String, imagePath: String) {
        Log.e(TAG, "onActivityResult: parsePlantSuccess = $string")
        // Toast需要在主线程弹
        Handler(Looper.getMainLooper()).post {
            getResult(string, imagePath, ByteArray(0))
        }
    }

    override fun parsePlantSuccess(string: String, byteArray: ByteArray) {
        Log.e(TAG, "parsePlantSuccess = $string")

        Handler(Looper.getMainLooper()).post {
            getResult(string, "", byteArray)
        }
    }

    override fun parsePlantFailure(string: String) {
        Log.e(TAG, "onActivityResult: parsePlantFailure = $string")
        isParsePlantSuccessByByteArray = false
    }

}
