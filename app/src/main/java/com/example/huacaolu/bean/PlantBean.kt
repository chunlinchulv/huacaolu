package com.example.huacaolu.bean

class PlantBean {
    private var log_id: Long = 0
    private var result: List<ResultDTO?>? = null

    fun getLog_id(): Long {
        return log_id
    }

    fun setLog_id(log_id: Long) {
        this.log_id = log_id
    }

    fun getResult(): List<ResultDTO?>? {
        return result
    }

    fun setResult(result: List<ResultDTO?>?) {
        this.result = result
    }

    override fun toString(): String {
        return "PlantBean(log_id=$log_id, result=${result.toString()})"
    }

    class ResultDTO {
        var score = 0.0
        var name: String? = null
        var baike_info: BaikeInfoDTO? = null

        class BaikeInfoDTO {
            var baike_url: String? = null
            var description: String? = null
            override fun toString(): String {
                return "BaikeInfoDTO(baike_url=$baike_url, description=$description)"
            }

        }

        override fun toString(): String {
            return "ResultDTO(score=$score, name=$name, baike_info=${baike_info.toString()})"
        }

    }


}