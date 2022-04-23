package com.example.huacaolu.ui

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.huacaolu.R

class MyPopupWindow : PopupWindow {

    private var popupView: View? = null
    private var takePhotoView: TextView? = null
    private var chooseImageView: TextView? = null
    private var cancelView: TextView? = null


    constructor(context: Context) : super(context) {
        init(context)
    }

    private var callBack: CallBack? = null

    private fun init(context: Context) {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        popupView = inflater.inflate(R.layout.dialog_popup_window, null)
        this.contentView = popupView
        this.width = LinearLayout.LayoutParams.MATCH_PARENT
        this.height = LinearLayout.LayoutParams.WRAP_CONTENT
        this.isTouchable = true
        this.isFocusable = false
        this.isOutsideTouchable = false
        this.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_bg_normal))
        this.animationStyle = android.R.style.Animation_Dialog
        takePhotoView = popupView?.findViewById(R.id.take_photo)
        chooseImageView = popupView?.findViewById(R.id.choose_image)
        cancelView = popupView?.findViewById(R.id.cancel)

        takePhotoView?.setOnClickListener {
            callBack?.clickTakePhoto()
            dismiss()
        }
        chooseImageView?.setOnClickListener {
            callBack?.clickChooseImage()
            dismiss()
        }
        cancelView?.setOnClickListener {
            callBack?.clickCancel()
            dismiss()
        }
    }

    fun setPopupWindowCallBackListener(callBack: CallBack) {
        this.callBack = callBack
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    fun showPopupWindow(parent: View) {
        if (!this.isShowing) {
            this.showAtLocation(parent, Gravity.BOTTOM, 0, 300)
        }
    }


    interface CallBack {
        fun clickTakePhoto()
        fun clickChooseImage()
        fun clickCancel()
    }

}