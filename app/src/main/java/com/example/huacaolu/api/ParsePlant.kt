package com.example.huacaolu.api

import android.util.Log
import com.baidu.aip.error.AipError
import com.baidu.aip.imageclassify.AipImageClassify
import com.baidu.aip.util.Util
import com.example.huacaolu.utils.Base64Util
import com.example.huacaolu.utils.FileUtil
import com.example.huacaolu.utils.HttpUtil
import com.example.huacaolu.utils.HttpUtils
import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder





class ParsePlant {

    private lateinit var listener: ParsePlantApiListener

    companion object {
        private const val APP_ID = "25964377"
        private const val API_KEY = "U65Bwu0iGBsfOjV7uWTArjvf"
        private const val SECRET_KEY = "U65Bwu0iGBsfOjV7uWTArjvf"
        private const val accessToken =
            "24.a83419bc7d8f35842b3245f42af93bcd.2592000.1652691973.282335-25964377"
        private var client: AipImageClassify? = null
        private val options : HashMap<String,String>
        init {
            client = AipImageClassify(APP_ID, API_KEY, SECRET_KEY)
            // 可选：设置网络连接参数
            client?.setConnectionTimeoutInMillis(2000)
            client?.setSocketTimeoutInMillis(60000)
            options = HashMap<String, String>()
            options["baike_num"] = "5"
            options["access_token"] = accessToken

        }
    }

    fun parsePlantByLocalPath(path : String){
        // 调用接口
        object : Thread(){
            override fun run() {
                super.run()
                try {
                    val data = Util.readFileByBytes(path)
                    val result = client!!.objectDetect(data, HashMap())
                    listener.parsePlantSuccess(result.toString())
                }catch (e: IOException){
                    e.printStackTrace()
                    listener.parsePlantFailure( AipError.IMAGE_READ_ERROR.toJsonResult().toString())
                }

            }
        }.start()
    }

    fun parsePlantByByte(path : String) {
        object : Thread(){
            override fun run() {
                super.run()
                try {
                    val data = Util.readFileByBytes(path)
                    val result = client!!.plantDetect(data, options)
                    listener.parsePlantSuccess(result.toString())
                }catch (e: IOException){
                    e.printStackTrace()
                    listener.parsePlantFailure( AipError.IMAGE_READ_ERROR.toJsonResult().toString())
                }

            }
        }.start()
    }

    fun parsePlantByHttp(filePath : String) {
        // 网络请求需要在线程中进行
        object : Thread(){
            override fun run() {
                val url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/plant"
                try {
                    val imgData: ByteArray = FileUtil.readFileByBytes(filePath)
                    val imgStr = Base64Util.encode(imgData)
                    val imgPara = URLEncoder.encode(imgStr, "UTF-8")

                    val para = "image=$imgPara"

                    /**
                     * 百度 调用鉴权接口获取的token，三十天有效，过期需要重新获取
                     * 终端命令：curl -i -k 'https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=U65Bwu0iGBsfOjV7uWTArjvf&client_secret=GyZfupKKM7KtHxrsF5ybRnX5TsHk85DW'
                     */
                    val accessToken =
                        "24.a83419bc7d8f35842b3245f42af93bcd.2592000.1652691973.282335-25964377"

                    val result = HttpUtil.post(url, accessToken, para)
                    println("结果:$result")
                    listener.parsePlantSuccess(result)
                } catch (e: Exception) {
                    e.printStackTrace()
                    listener.parsePlantFailure(e.message.toString())
                }
            }
        }.start()
    }

    fun getAuth(ak: String, sk: String) {
        object : Thread(){
            override fun run() {
                super.run()
                // 获取token地址
                val authHost = "https://aip.baidubce.com/oauth/2.0/token?"
                val getAccessTokenUrl = (authHost // 1. grant_type为固定参数
                        + "grant_type=client_credentials" // 2. 官网获取的 API Key
                        + "&client_id=" + ak // 3. 官网获取的 Secret Key
                        + "&client_secret=" + sk)
                try {
                    val realUrl = URL(getAccessTokenUrl)
                    // 打开和URL之间的连接
                    val connection = realUrl.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connect()
                    // 获取所有响应头字段
                    val map = connection.headerFields
                    // 遍历所有的响应头字段
                    for (key in map.keys) {
                        System.err.println(key + "--->" + map[key])
                    }
                    // 定义 BufferedReader输入流来读取URL的响应
                    val inputStreamReader =
                        BufferedReader(InputStreamReader(connection.inputStream))
                    var result = ""
                    var line: String
                    while (inputStreamReader.readLine().also { line = it } != null) {
                        result += line
                    }
                    /**
                     * 返回结果示例
                     */
                    System.err.println("result:$result")
                    val jsonObject = JSONObject(result)

                    val token = jsonObject.getString("access_token")
                } catch (e: Exception) {
                    System.err.printf("获取token失败！")
                    e.printStackTrace(System.err)
                }
            }
        }

    }

    fun setApiListener(listener : ParsePlantApiListener){
        this.listener = listener
    }

    fun parsePlantWithWords(plantName:String) {
        Log.e("xxx plantName = " , plantName)
        val host = "https://baike.market.alicloudapi.com"
        val path = "/baike"
        val method = "POST"
        val appcode = "33599eb7c1674e9685e0daec0ac2b0da"
        val headers: MutableMap<String, String> = HashMap()
        headers["Authorization"] = "APPCODE $appcode"
        //根据API的要求，定义相对应的Content-Type
        //根据API的要求，定义相对应的Content-Type
        headers["Content-Type"] = "application/x-www-form-urlencoded; charset=UTF-8"
        val querys: Map<String, String> = HashMap()
        val bodys: MutableMap<String, String> = HashMap()
        bodys["src"] = plantName


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             * */

            val response: HttpResponse =
                HttpUtils.doPost(host, path, method, headers, querys, bodys)
            if (response == null) {
                listener.parsePlantFailure("");
            }else{
                listener.parsePlantSuccess(EntityUtils.toString(response.entity))
            }
            println(response.toString())
            //获取response的body
            println(EntityUtils.toString(response.entity))
            Log.e("xxx response = " , response.toString())
            Log.e("xxx EntityUtils = " , EntityUtils.toString(response.entity))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    interface ParsePlantApiListener {
        fun parsePlantSuccess(string:String)
        fun parsePlantFailure(string:String)
    }


}