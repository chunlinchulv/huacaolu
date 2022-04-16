package com.example.huacaolu.api

import com.example.huacaolu.utils.Base64Util
import com.example.huacaolu.utils.FileUtil
import com.example.huacaolu.utils.HttpUtil
import java.net.URLEncoder

class ParsePlant {
    companion object {
        fun plant(): String {
            val url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/plant"
            try {
                var filePath = "drawable/plant.jpg"
                val imgData: ByteArray = FileUtil.readFileByBytes(filePath)
                var imgStr = Base64Util.encode(imgData)
                var imgPara = URLEncoder.encode(imgStr, "UTF-8")

                var para = "image$imgPara"

                /**
                 * 百度 调用鉴权接口获取的token，三十天有效，过期需要重新获取
                 * 终端命令：curl -i -k 'https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=U65Bwu0iGBsfOjV7uWTArjvf&client_secret=GyZfupKKM7KtHxrsF5ybRnX5TsHk85DW'
                 */
                val accessToken =
                    "24.a83419bc7d8f35842b3245f42af93bcd.2592000.1652691973.282335-25964377"

                var result = HttpUtil.post(url, accessToken, para)
                println("结果:$result")
                return result
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null.toString()
        }
    }

}