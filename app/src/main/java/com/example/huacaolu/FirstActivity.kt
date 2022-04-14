package com.example.huacaolu

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.UserDictionary.Words
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.baidu.aip.imageclassify.AipImageClassify


class FirstActivity : AppCompatActivity() {
    private val TAG = "FragmentActivity"
    companion object {
        const val APP_ID = "25964377"
        const val API_KEY = "U65Bwu0iGBsfOjV7uWTArjvf"
        const val SECRET_KEY = "U65Bwu0iGBsfOjV7uWTArjvf"
    }

    private val RESULT_CODE = 0
    // 所需的全部权限
    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 缺少权限时, 进入权限配置页面
        if (checkMyPermissions()) {
            requestMyPermissions(permissions, 1)
        }
        initAPI() // 百度API初始化
        setContentView(R.layout.activity_first)
        initView()
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume()")
    }

    /**
     * 检查APP是否拥有所需要的权限
     */
    private fun checkMyPermissions():Boolean {
        var isNeedRequestPermission = false
        for (permission in permissions) {
            val result = checkMyPermission(permission)
            Log.e(TAG, "相机权限 $permission 检查结果 $result")
            if (!result) {
                isNeedRequestPermission = true
                break
            }
        }
        return isNeedRequestPermission
    }
    /**
     * 申请权限
     */
    private fun requestMyPermissions(permissions: Array<String>, i: Int) {
        Log.e(TAG,"申请权限")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, RESULT_CODE)
        } else {

        }
    }

    private fun checkMyPermission(permission: String): Boolean {
        val result = checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun initAPI() {
        // 初始化一个AipImageClassify
        val client = AipImageClassify(Words.APP_ID, API_KEY, SECRET_KEY)
        // 可选：设置网络连接参数
//        client.setConnectionTimeoutInMillis(2000)
//        client.setSocketTimeoutInMillis(60000)
        // 调用接口
//        val path = "test.jpg"
//        val res: JSONObject = client.objectDetect(path, HashMap<String, String>())
//        println(res.toString(2))
    }

    private fun initView() {
        setTF() // 主页字体
        startHome() //跳转主页
    }

    /**
     * 跳转到主界面
     */
    private fun startHome() {
        val constraintLayout = findViewById<ConstraintLayout>(R.id.beginDesign)
        val intent = Intent(this,MainActivity::class.java)
        constraintLayout.setOnClickListener {
            if (checkMyPermissions()) {
                showDialog()
            }else{
                startActivity(intent)
            }
        }
    }

    private fun showDialog() {
        val alterDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alterDialog.setTitle("打开手机设置权限")
        alterDialog.setMessage("请打开设置权限")
        alterDialog.setPositiveButton("打开", DialogInterface.OnClickListener { dialog, which ->
            openPermissionConfig()
        })
        alterDialog.setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which -> })
        alterDialog.show()
    }

    /**
     * 打开手机设置界面
     */
    private fun openPermissionConfig() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.parse("package:" + this.packageName)
        startActivity(intent)
    }

    /**
     * 设置字体
     */
    private fun setTF() {
        val nameTextView = findViewById<TextView>(R.id.nameTextView)
        val tf = Typeface.createFromAsset(assets,"HYXiXingKaiJ.ttf")
        nameTextView.typeface = tf
    }
     /**
     * 权限请求回调
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RESULT_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //相机申请成功
                Log.e(TAG,"权限申请成功 ")
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                //权限拒绝
                Log.e(TAG,"权限申请失败")
                Toast.makeText(this,"权限申请失败,请前往手机设置打开权限",Toast.LENGTH_SHORT).show()
            }
        }
    }
}