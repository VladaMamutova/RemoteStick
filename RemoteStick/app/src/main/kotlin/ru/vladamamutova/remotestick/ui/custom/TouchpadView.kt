package ru.vladamamutova.remotestick.ui.custom

import android.content.Context
import android.graphics.*
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.ui.listeners.MouseActionListener
import kotlin.math.abs


class TouchpadView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    companion object {
        private const val LEFT_CLICK_TIME: Long = 100
        private const val START_DRAG_TIME: Long = 500
    }

    private var downTime: Long = 0
    private var prevX = 0f
    private var prevY = 0f

    private var isRightClick = false
    private var isMove = false
    private var isScroll = false
    private var isDrag = false

    // Параметры для отрисовки панели скролла.
    private var scrollX = 0f
    private val scrollWidth = resources.getDimension(R.dimen.tab_layout_height)
    private val scrollBottomMargin =
        scrollWidth + 2 * resources.getDimension(R.dimen.small_intent)
    private val scrollPaint: Paint = Paint().apply {
        color = ContextCompat.getColor(context!!, R.color.whiteOpacity30)
        style = Paint.Style.FILL
    }

    // Параметры для отрисовки стрелок на панели скролла.
    private val arrowWidth = 32f
    private val arrowHeight = 16f
    private val arrowMargin = 40f
    private val arrowPaint: Paint = Paint().apply {
        color = ContextCompat.getColor(context!!, R.color.whiteOpacity60)
        style = Paint.Style.STROKE
        strokeWidth = 2f
        pathEffect = CornerPathEffect(4f)
        isAntiAlias = true
    }

    private var mouseListener: MouseActionListener? = null

    fun setOnMouseActionListener(mouseActionListener: MouseActionListener) {
        mouseListener = mouseActionListener
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        scrollX = width - scrollWidth
        canvas.drawRect(
            scrollX, 0f, width.toFloat(),
            height.toFloat() - scrollBottomMargin,
            scrollPaint
        )

        val arrowX = scrollX + scrollWidth / 2
        canvas.drawPath(getVerticalArrowPath(arrowX, true), arrowPaint)
        canvas.drawPath(getVerticalArrowPath(arrowX, false), arrowPaint)
    }

    private fun getVerticalArrowPath(xTip: Float, isUpArrow: Boolean): Path {
        val y: Float = if (isUpArrow) {
            arrowMargin
        } else {
            height.toFloat() - scrollBottomMargin - arrowMargin
        }

        val height = if (isUpArrow) arrowHeight else -arrowHeight

        val path = Path().apply {
            moveTo(xTip, y)
            lineTo(xTip + arrowWidth / 2, y + height)
            lineTo(xTip - arrowWidth / 2, y + height)
            lineTo(xTip, y)
        }
        path.close()

        return path
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { // первое касание
                Log.d("TAG", "motion down")
                downTime = SystemClock.elapsedRealtime()
                prevX = event.x
                prevY = event.y

                // Если координаты первого нажатия попали в область панели скролла,
                // устанавливаем действие скролла.
                if (prevX > scrollX) {
                    isScroll = true
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> { // последующие касания
                if (event.pointerCount == 2) {
                    Log.d("TAG", "motion two down")
                    isRightClick = true
                }
            }
            MotionEvent.ACTION_MOVE -> motionMove(event) // движение
            MotionEvent.ACTION_UP -> motionUp() // отпускание последнего пальца
            else -> {
            }
        }
        return true
    }

    private fun motionMove(event: MotionEvent) {
        Log.d("TAG", "motion move")

        if (event.pointerCount == 1 && !isRightClick) {
            if (isScroll) {
                val dy = (event.y - prevY).toInt()
                if (dy != 0) {
                    mouseListener?.onScroll(dy)
                    Log.d("ACTION--------", "scroll: (${dy})")
                }
            } else {
                val dx = (event.x - prevX).toInt()
                val dy = (event.y - prevY).toInt()

                if (!isMove) {
                    // Устанавливаем действие перемещения, когда разница
                    // координат по одной из оси больше единицы
                    // (во избежание случайных перемещений во время клика
                    // или удерживания перед перетаскиванием).
                    if (abs(dx) > 1 || abs(dy) > 1) {
                        isMove = true
                    }

                    // Зажимаем левую кнопку мыши, инициируя перетаскивание,
                    // когда время удерживания превышает время для перетаскивания.
                    if (!isDrag &&
                        SystemClock.elapsedRealtime() - downTime > START_DRAG_TIME) {
                        isDrag = true
                        mouseListener?.onLeftDown()
                        Log.d("TAG", "left down")
                    }
                } else if (dx != 0 || dy != 0) {
                    mouseListener?.onMove(dx, dy)
                    Log.d("ACTION--------", "move: (${dx}, ${dy})")
                }
            }
        }

        prevX = event.x
        prevY = event.y
    }

    private fun motionUp() {
        val upTime = SystemClock.elapsedRealtime() - downTime
        Log.d("TAG", "motion up : $upTime")
        when {
            isRightClick -> {
                isRightClick = false
                mouseListener?.onRightClick()
                Log.d("ACTION--------", "right click")
            }
            isDrag -> {
                isDrag = false
                mouseListener?.onLeftUp()
                Log.d("TAG", "left up")
            }
            upTime < LEFT_CLICK_TIME -> {
                mouseListener?.onLeftClick()
                Log.d("ACTION--------", "left click")
            }
        }

        isMove = false
        isScroll = false
    }
}