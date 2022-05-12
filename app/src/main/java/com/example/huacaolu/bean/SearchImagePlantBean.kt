package com.example.huacaolu.bean

import android.os.Parcel
import android.os.Parcelable

class SearchImagePlantBean() : Parcelable {
    private var log_id: Long = 0
    private var result: ArrayList<ResultDTO?>? = null

    constructor(parcel: Parcel) : this() {
        log_id = parcel.readLong()
    }

    fun getLog_id(): Long {
        return log_id
    }

    fun setLog_id(log_id: Long) {
        this.log_id = log_id
    }

    fun getResult(): ArrayList<ResultDTO?>? {
        return result
    }

    fun setResult(result: ArrayList<ResultDTO?>?) {
        this.result = result
    }

    override fun toString(): String {
        return "PlantBean(log_id=$log_id, result=${result.toString()})"
    }

    class ResultDTO() : Parcelable{
        var score = 0.0
        var name: String? = null
        var baike_info: BaikeInfoDTO? = null

        constructor(parcel: Parcel) : this() {
            score = parcel.readDouble()
            name = parcel.readString()
        }

        class BaikeInfoDTO() : Parcelable{
            var baike_url: String? = null
            var image_url: String? = null
            var description: String? = null

            constructor(parcel: Parcel) : this() {
                baike_url = parcel.readString()
                image_url = parcel.readString()
                description = parcel.readString()
            }

            override fun toString(): String {
                return "BaikeInfoDTO(baike_url=$baike_url,image_url = $image_url, description=$description)"
            }

            override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(baike_url)
                parcel.writeString(image_url)
                parcel.writeString(description)
            }

            override fun describeContents(): Int {
                return 0
            }

            companion object CREATOR : Parcelable.Creator<BaikeInfoDTO> {
                override fun createFromParcel(parcel: Parcel): BaikeInfoDTO {
                    return BaikeInfoDTO(parcel)
                }

                override fun newArray(size: Int): Array<BaikeInfoDTO?> {
                    return arrayOfNulls(size)
                }
            }

        }

        override fun toString(): String {
            return "ResultDTO(score=$score, name=$name, baike_info=${baike_info.toString()})"
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeDouble(score)
            parcel.writeString(name)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ResultDTO> {
            override fun createFromParcel(parcel: Parcel): ResultDTO {
                return ResultDTO(parcel)
            }

            override fun newArray(size: Int): Array<ResultDTO?> {
                return arrayOfNulls(size)
            }
        }

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(log_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchImagePlantBean> {
        override fun createFromParcel(parcel: Parcel): SearchImagePlantBean {
            return SearchImagePlantBean(parcel)
        }

        override fun newArray(size: Int): Array<SearchImagePlantBean?> {
            return arrayOfNulls(size)
        }
    }


}