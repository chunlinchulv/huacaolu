package com.example.huacaolu.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.huacaolu.R
import com.example.huacaolu.api.ParsePlant
import com.example.huacaolu.bean.SearchImagePlantBean
import com.example.huacaolu.helper.CameraProxy
import com.example.huacaolu.ui.CameraGLSurfaceView
import com.google.gson.Gson
import java.io.ByteArrayOutputStream

class RealTimeIdentsActivity : AppCompatActivity(), ParsePlant.ParsePlantApiListener {
    lateinit var mGLSurfaceView: CameraGLSurfaceView
    lateinit var mCameraProxy: CameraProxy
    private var isDestroy: Boolean = false
    private var isPause: Boolean = false
    lateinit var parsePlantApi : ParsePlant


    var isParsePlantSuccessByByteArray: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_idents)
        initApi()
        initView()
        initGLSurfaceView()
    }

    private fun initView() {
        mGLSurfaceView = findViewById<CameraGLSurfaceView>(R.id.gl_surface_view)
    }

    override fun onResume() {
        super.onResume()
        mGLSurfaceView.requestRender()
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    override fun onDestroy() {
        super.onDestroy()
        isDestroy = true
        mCameraProxy.releaseCamera()
    }

    override fun parsePlantSuccess(string: String, imagePath: String) {
        TODO("Not yet implemented")
    }

    override fun parsePlantSuccess(string: String, byteArray: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun parsePlantFailure(string: String) {
        TODO("Not yet implemented")
    }

    private fun initGLSurfaceView() {
        mCameraProxy = mGLSurfaceView.cameraProxy!!
        mGLSurfaceView.setSurfaceViewCallback(object : CameraGLSurfaceView.SurfaceViewCallbackListener {
            override fun surfaceViewCreate() {

            }

            override fun surfaceViewChange() {
                mGLSurfaceView.setPreviewCallback(object : Camera.PreviewCallback{
                    override fun onPreviewFrame(p0: ByteArray?, p1: Camera?) {
                        mGLSurfaceView.setPreviewCallback(this)
                        if (isParsePlantSuccessByByteArray) {
                            isParsePlantSuccessByByteArray = true
                            Log.e(TAG, "setPreviewCallback")
                            val previewSize = p1?.parameters?.previewSize!! //获取尺寸,格式转换的时候要用到

                            val newOpts = BitmapFactory.Options()
                            newOpts.inJustDecodeBounds = true
                            val yuvimage = YuvImage(
                                p0,
                                ImageFormat.NV21,
                                previewSize.width,
                                previewSize.height,
                                null
                            )
                            val baos = ByteArrayOutputStream()
                            yuvimage.compressToJpeg(
                                Rect(0, 0, previewSize.width, previewSize.height),
                                100,
                                baos
                            ) // 80--JPG图片的质量[0-100],100最高

                            val rawImage: ByteArray = baos.toByteArray()

                            parsePlantApi.parsePlantByByte(rawImage)
                        }
                    }

                })
            }

        })

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
                Toast.makeText(this, "数据解析失败，请重新尝试", Toast.LENGTH_SHORT).show()
            }
            isParsePlantSuccessByByteArray = false
            return
        }
        if (results[0]?.baike_info == null || TextUtils.equals("非植物",results[0]?.name)) {
            if (!TextUtils.isEmpty(imagePath)) {
                Toast.makeText(this, "${results[0]?.name} 请重试 ", Toast.LENGTH_SHORT)
                    .show()
            }
            isParsePlantSuccessByByteArray = false
            return
        }
        if (byteArray != null) {
            isParsePlantSuccessByByteArray = true
        }
        val dataSearchImagePlantBean : SearchImagePlantBean = SearchImagePlantBean()
        val resultArrayList : ArrayList<SearchImagePlantBean.ResultDTO?> = arrayListOf<SearchImagePlantBean.ResultDTO?>()
        for (data : SearchImagePlantBean.ResultDTO? in results) {
            if (data?.score!! > 0.05 ) {
                resultArrayList.add(data)
            }
        }
        dataSearchImagePlantBean.setLog_id(plantBean.getLog_id())
        dataSearchImagePlantBean.setResult(resultArrayList)
        if (resultArrayList.size == 1) {
            startActivityDetails(resultArrayList[0]!!,imagePath,byteArray)
        }else if (resultArrayList.size > 1){
            startSearchResultActivity(Gson().toJson(dataSearchImagePlantBean),imagePath,byteArray)
        }
    }

    private fun startActivityDetails(resultDTO: SearchImagePlantBean.ResultDTO, imagePath: String?,byteArray: ByteArray?) {
        Log.e(TAG, "startActivityDetails: $imagePath")
        val intent = Intent(this, SearchResultDetails::class.java)
        intent.putExtra("jsonString",Gson().toJson(resultDTO))
        startActivity(intent)
    }

    private fun startSearchResultActivity(jsonString: String,imagePath: String?,byteArray: ByteArray?) {
        Log.e(TAG, "startSearchResultActivity:")
        val intent = Intent(this, SearchResultActivity::class.java)
        intent.putExtra("jsonString",jsonString)
        startActivity(intent)
    }

    private fun initApi() {
        parsePlantApi = ParsePlant()
        parsePlantApi.setApiListener(this)
    }
}