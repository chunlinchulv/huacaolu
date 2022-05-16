package com.example.huacaolu.ui

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.SurfaceTexture.OnFrameAvailableListener
import android.hardware.Camera
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.example.huacaolu.helper.CameraProxy
import com.example.huacaolu.utils.CameraDrawer
import com.example.huacaolu.utils.OpenGLUtils
import java.io.ByteArrayOutputStream
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraGLSurfaceView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs),
    GLSurfaceView.Renderer,
    OnFrameAvailableListener {
    var cameraProxy: CameraProxy? = null
        private set
    var surfaceTexture: SurfaceTexture? = null
        private set
    private var mDrawer: CameraDrawer? = null
    private var mRatioWidth = 0
    private var mRatioHeight = 0
    private var mOldDistance = 0f
    private var mTextureId = -1
    private var surfaceViewCallbackListener: SurfaceViewCallbackListener? = null
    private fun init(context: Context) {
        cameraProxy = CameraProxy(context as Activity)
        setEGLContextClientVersion(2)
        setRenderer(this)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        Log.e(TAG, "onSurfaceCreated")
        mTextureId = OpenGLUtils.getExternalOESTextureID()
        surfaceTexture = SurfaceTexture(mTextureId)
        surfaceTexture!!.setOnFrameAvailableListener(this)
        cameraProxy!!.openCamera()
        mDrawer = CameraDrawer()
        surfaceViewCallbackListener!!.surfaceViewCreate()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceChanged. thread: " + Thread.currentThread().name)
        Log.d(
            TAG,
            "onSurfaceChanged. width: $width, height: $height"
        )
        val previewWidth = cameraProxy!!.previewWidth
        val previewHeight = cameraProxy!!.previewHeight
        if (width > height) {
            setAspectRatio(previewWidth, previewHeight)
        } else {
            setAspectRatio(previewHeight, previewWidth)
        }
        GLES20.glViewport(0, 0, width, height)
        cameraProxy!!.startPreview(surfaceTexture)
        surfaceViewCallbackListener!!.surfaceViewChange()
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        surfaceTexture!!.updateTexImage()
        mDrawer!!.draw(mTextureId, cameraProxy!!.isFrontCamera)
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture) {
        requestRender()
    }

    private fun setAspectRatio(width: Int, height: Int) {
        require(!(width < 0 || height < 0)) { "Size cannot be negative." }
        mRatioWidth = width
        mRatioHeight = height
        post {
            requestLayout() // must run in UI thread
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height)
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth)
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount == 1) {
            // 点击聚焦
            cameraProxy!!.focusOnPoint(event.x.toInt(), event.y.toInt(), width, height)
            return true
        }
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_POINTER_DOWN -> mOldDistance = getFingerSpacing(event)
            MotionEvent.ACTION_MOVE -> {
                val newDistance = getFingerSpacing(event)
                if (newDistance > mOldDistance) {
                    cameraProxy!!.handleZoom(true)
                } else if (newDistance < mOldDistance) {
                    cameraProxy!!.handleZoom(false)
                }
                mOldDistance = newDistance
            }
            else -> {
            }
        }
        return super.onTouchEvent(event)
    }

    fun setSurfaceViewCallback(surfaceViewCallbackListener: SurfaceViewCallbackListener?) {
        this.surfaceViewCallbackListener = surfaceViewCallbackListener
    }

    interface SurfaceViewCallbackListener {
        fun surfaceViewCreate()
        fun surfaceViewChange()
    }

    companion object {
        private const val TAG = "CameraGLSurfaceView"
        private fun getFingerSpacing(event: MotionEvent): Float {
            val x = event.getX(0) - event.getX(1)
            val y = event.getY(0) - event.getY(1)
            return Math.sqrt((x * x + y * y).toDouble()).toFloat()
        }
    }

    init {
        init(context)
    }

    fun setPreviewCallback(previewCallback : Camera.PreviewCallback) {
        cameraProxy!!.setPreviewCallback(previewCallback)
    }

}