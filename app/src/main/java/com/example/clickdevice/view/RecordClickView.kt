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
import com.example.clickdevice.bean.Bean
import com.example.clickdevice.bean.RecordScriptCmd

class RecordClickView : View {
    private val CLICK_SPACING_TIME: Long = 300

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
    private var data: MutableList<Bean>? = mutableListOf()
    private var enable = false

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {

        initPaint()
        data?.forEachIndexed { index, item->
            var point = PointF(item.x.toFloat(), item.y.toFloat() - Util.getStatusHeight(context))
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
    private var point_down:PointF = PointF()
    private var point_up:PointF = PointF()

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!enable) {
             return false
        }
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                downTime = SystemClock.uptimeMillis()
                scriptListener?.onActionDown()
                scriptPath = Path()
                point_down.set(event.rawX, event.rawY)
                scriptPath!!.moveTo(event.rawX, event.rawY)
                //data?.add(Bean(event.rawX.toInt(), event.rawY.toInt()))
            }
            MotionEvent.ACTION_MOVE -> {
                scriptPath?.lineTo(event.rawX, event.rawY)
                //data?.add(Bean(event.rawX.toInt(), event.rawY.toInt()))
            }
            MotionEvent.ACTION_UP -> {
                point_up.set(event.rawX, event.rawY)
                //将事件传递下去
                scriptListener?.apply {
                    val createGestureCMD = RecordScriptCmd.createGestureCMD(
                            data,
                            (SystemClock.uptimeMillis() - downTime).toInt()
                    )
                    onUpdate(createGestureCMD, scriptPath!!)
                }
                if (isMoved().not()){
                    //如果按住的时间超过了长按时间，不处理
                    if (SystemClock.uptimeMillis() - downTime > LONG_PRESS_TIME){
                        //长按事件

                    }else{
                        //正常点击事件
                        data?.add(Bean(event.rawX.toInt(), event.rawY.toInt()))

                    }
                }else{
                    //滑动事件--另作处理

                }
//                scriptPath?.lineTo(event.rawX, event.rawY)
//                data?.add(Bean(event.rawX.toInt(), event.rawY.toInt()))
//                scriptListener?.apply {
//                    val createGestureCMD = RecordScriptCmd.createGestureCMD(
//                            data,
//                            (SystemClock.uptimeMillis() - downTime).toInt()
//                    )
//                    onUpdate(createGestureCMD, scriptPath!!)
//                }
            }
        }
        invalidate()
        return false
    }

    override fun setOnTouchListener(l: OnTouchListener?) {
        super.setOnTouchListener(l)
    }
    fun initRecordClick() {
        data?.clear()
        enable = true;
    }
    fun finishRecordClick(){
        data?.clear()
        enable = false;
    }
    interface ScriptListener {
        fun onActionDown() {

        }

        fun onUpdate(recordScriptCmd: RecordScriptCmd, path: Path)
    }

    fun isMoved():Boolean {
        //允许有5的偏差 在判断是否移动的时候
        return !(Math.abs(point_down.x - point_up.x) <= 5 && Math.abs(point_down.y - point_up.y) <= 5)
    }
}