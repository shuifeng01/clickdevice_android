package com.example.clickdevice.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.clickdevice.Util
import com.example.clickdevice.bean.RecordBean
import com.example.clickdevice.bean.RecordScriptCmd

class RecordClickView : View {

    private val LONG_PRESS_TIME: Long = 500

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs, 0)


    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    )

    private var paint = Paint()
    private var paintText = Paint()

    private fun initPaint() {
        paint.color = Color.parseColor("#7FFF0000")
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.setStrokeWidth(2f)

        paintText.color = Color.WHITE
        paintText.isAntiAlias = true
        paintText.textSize = 34f;
        paintText.isAntiAlias = true

    }

    private var scriptPath: Path? = null
    private var data: MutableList<RecordBean> = mutableListOf()
    private var enable = true
    private var isRecording = false
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        if (isRecording.not())return
        initPaint()
        data.forEachIndexed { index, item->
            var point = PointF(item.x1.toFloat(), item.y1.toFloat() - Util.getStatusHeight(context))
            canvas?.drawCircle(point.x, point.y, 30f, paint)
            var text = "${index+1}"
            val strWidth = paintText.measureText(text)
            var rect =  Rect()
            paintText.getTextBounds(text, 0, text.length, rect)
            canvas?.drawText(text, point.x - strWidth / 2, point.y + (rect.height()) / 2, paintText)
            Log.d("RecordClickView", "clickPoint >$point index>${index + 1}")
        }
    }

    var scriptListener: ScriptListener? = null
    private var downTime = 0L
    private var upTime = 0L
    private var point_down:PointF = PointF()
    private var point_up:PointF = PointF()
    private var preClickTime = 0L
    private var delayClickTime = 0

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!enable) {
             return false
        }
        //Log.d("RecordClickView", "onTouchEvent ${event?.action}   x>${event?.x} y>${event?.y}")
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                downTime = SystemClock.uptimeMillis()
                scriptListener?.onActionDown()
                scriptPath = Path()
                point_down.set(event.rawX, event.rawY)
                scriptPath?.moveTo(event.rawX, event.rawY)
            }
            MotionEvent.ACTION_MOVE -> {
                scriptPath?.lineTo(event.rawX, event.rawY)
            }
            MotionEvent.ACTION_UP -> {
                upTime = SystemClock.uptimeMillis()
                point_up.set(event.rawX, event.rawY)
                //将事件传递下去
                scriptListener?.apply {
                    val createGestureCMD = RecordScriptCmd.createGestureCMD(
                            mutableListOf(),
                            (SystemClock.uptimeMillis() - downTime).toInt()
                    )
                    onUpdate(createGestureCMD, scriptPath!!)
                }
                if (isMoved().not()){
                    //如果按住的时间超过了长按时间，不处理
                    if (upTime - downTime > LONG_PRESS_TIME){
                        //长按事件

                    }else{
                        //正常点击事件
                        delayClickTime = (SystemClock.uptimeMillis() - preClickTime).toInt()
                        preClickTime = SystemClock.uptimeMillis()

                        var recordData = RecordBean.BuildRandomClickCMD(point_down.x.toInt(),
                                point_down.y.toInt(), point_up.x.toInt(), point_up.y.toInt(),
                                (upTime - downTime).toInt(),delayClickTime)
                        data?.add(recordData)
                        scriptListener?.onActionClick(data)
                    }
                }else{
                    //滑动事件--另作处理

                }
            }
        }
        invalidate()
        return true
    }

    fun startRecord() {
        data?.clear()
        isRecording = true
    }
    fun finishRecordClick(){
        data?.clear()
        isRecording = false
    }
    interface ScriptListener {
        fun onActionDown()

        fun onUpdate(recordScriptCmd: RecordScriptCmd, path: Path)

        fun onActionClick(recordList: MutableList<RecordBean>)
    }

    fun isMoved():Boolean {
        //允许有5的偏差 在判断是否移动的时候
        return !(Math.abs(point_down.x - point_up.x) <= 5 && Math.abs(point_down.y - point_up.y) <= 5)
    }
}