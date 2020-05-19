package ru.vladamamutova.remotestick.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import ru.vladamamutova.remotestick.utils.MouseActionListener


class TouchpadView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    companion object {
        private const val DOUBLE_CLICK_DELAY: Long = 200
        private const val FIRST_MOVE_DELAY: Long = 150
    }

    private var isSingleClick = false
    private var lastClickDelay: Long = 0
    private var firstMoveDelay: Long = 0
    private var clickHandler: Handler = Handler()
    private var runnable = Runnable {
        if (isSingleClick) {
            mouseListener?.onLeftClick()
            Log.d("ACTION--------", "left click")
        }
    }

    private var prevX = 0f
    private var prevY = 0f
    private var currX = 0f
    private var currY = 0f

    /*private var viewWidth = 0f
    private var viewHeight = 0f*/
    private var btnY = 0f
    private var btnLeftXend = 0f
    private var btnMidXend = 0f

    /**
     * Точка щелчка по координате x
     */
    private var x1 = 0f
    private var y1 = 0f

    /**
     * Координата x второй точки при 2-точечном касании
     */
    private var x2 = 0f
    private var y2 = 0f

    private var isRightClick = false
    private var isMove = false

    private var isLeftBtnDown = false
    private var isMidBtnDown = false
    private var isRightBtnDown = false
    private var isLeftMove = false
    private var isWY = false
    private var mouseListener: MouseActionListener? = null

    fun setOnMouseActionListener(mouseActionListener: MouseActionListener) {
        mouseListener = mouseActionListener
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
/*        viewWidth = width.toFloat()
        viewHeight = height.toFloat()

        // Ключевая зона
        btnY = (viewHeight * 9 / 10f)//.toInt()
        // Левый конец X
        btnLeftXend = (viewWidth * 2 / 5f)//.toInt()
        // Средний конец x
        btnMidXend = (viewWidth * 3 / 5f)//.toInt()

        val p = Paint()
        p.style = Paint.Style.FILL
        if (isLeftBtnDown) {
            p.color = Color.YELLOW
            canvas.drawRect(0f, btnY, btnLeftXend, viewHeight, p)
        } else {
            p.color = Color.GRAY
            canvas.drawRect(0f, btnY, btnLeftXend, viewHeight, p)
        }
        if (isMidBtnDown) {
            p.color = Color.YELLOW
            canvas.drawRect(btnLeftXend, btnY, btnMidXend, viewHeight, p)
        } else {
            p.color = Color.DKGRAY
            canvas.drawRect(btnLeftXend, btnY, btnMidXend, viewHeight, p)
        }
        if (isRightBtnDown) {
            p.color = Color.YELLOW
            canvas.drawRect(btnMidXend, btnY, viewWidth, viewHeight, p)
        } else {
            p.color = Color.GRAY
            canvas.drawRect(btnMidXend, btnY, viewWidth, viewHeight, p)
        }
        p.color = Color.RED
        canvas.drawText(
            "左键", viewWidth * 0.7f / 5f,
            viewHeight * 9.5f / 10f, p
        )
        canvas.drawText(
            "中键", viewWidth * 2.3f / 5f,
            viewHeight * 9.5f / 10f, p
        )
        canvas.drawText(
            "右键", viewWidth * 3.7f / 5f,
            viewHeight * 9.5f / 10f, p
        )*/
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> motionOneDown(event)  // первое касание
            MotionEvent.ACTION_POINTER_DOWN -> // последующие касания
                if (event.pointerCount == 2) {
                    //Запишите функцию комбинации клавиш, сначала нажмите левую кнопку или правую кнопку
                    motionTwoDown(event)
                }
            MotionEvent.ACTION_MOVE -> motionMove(event) // движение
            // прерывание касания, кроме последнего
            //MotionEvent.ACTION_POINTER_UP
            MotionEvent.ACTION_UP -> motionUp(event) // отпускание последнего пальца
            else -> {
            }
        }
        return true
    }

    private fun motionOneDown(event: MotionEvent) {
        Log.d("TAG", "motion down")
        // Запись одноточечных координат, когда одноточечное
        // событие может быть только событием нажатия клавиши
        prevX = event.x
        prevY = event.y
        firstMoveDelay = SystemClock.elapsedRealtime()
        //isLeftDown = true

       /* // 左键点击
        if (x1 < btnLeftXend && y1 > btnY) { // Координаты клика в левой области
            isLeftBtnDown = true // Щелчок левой кнопкой мыши
            //mouseListener.sendMouseAction(MouseAction.LEFT_DOWN, 0, 0)
            Log.d("TAG", "LEFT_DOWN: ")
            // Обесцвечивание зон
            invalidate()
        }

        // 中键点击
        if (x1 > btnLeftXend && x1 < btnMidXend && y1 > btnY) {
            //mouseListener.sendMouseAction(MouseAction.MIDDLE_DOWN, 0, 0)
            Log.d("TAG", "MIDDLE_DOWN: ")
            isMidBtnDown = true
            invalidate()
        }

        // 右键点击
        if (x1 > btnMidXend && y1 > btnY) {
            //mouseListener.sendMouseAction(MouseAction.RIGHT_DOWN, 0, 0)
            Log.d("TAG", "RIGHT_DOWN: ")
            isRightBtnDown = true
            invalidate()
        }*/
    }

    /**
     * Обработка двойного щелчка
     */
    private fun motionTwoDown(event: MotionEvent) {
        Log.d("TAG", "motion two down")
        isRightClick = true

        /*if (!isLeftMove) { // Если не левая кнопка и перемещение, то состояние функции комбинации
            // Запишите координаты первой точки
            x1 = event.getX(0)
            y1 = event.getY(0)
            // Установка левого нажатия
            if (x1 < btnLeftXend && y1 > btnY) {
                isLeftBtnDown = true
                //mouseListener.sendMouseAction(MouseAction.LEFT_DOWN, 0, 0)
                Log.d("TAG", "LEFT_DOWN: ")
            }
            // 设置右键按下
            if (x1 > btnMidXend && y1 > btnY) {
                isRightBtnDown = true
                //mouseListener.sendMouseAction(MouseAction.RIGHT_DOWN, 0, 0)
                Log.d("TAG", "RIGHT_DOWN: ")
            }
        }
        x2 = event.getX(1)
        y2 = event.getY(1)*/
    }

    private fun motionMove(event: MotionEvent) {
        Log.d("TAG", "motion move")

        currX = event.x
        currY = event.y
        if (event.pointerCount == 1) {
            Log.d("TAG", "isMove = $isMove")
            if (isMove || SystemClock.elapsedRealtime() - firstMoveDelay > FIRST_MOVE_DELAY) {
                isMove = true
                isSingleClick = false
                val dx = (currX - prevX).toInt() //* dpi * sensitivity
                val dy = (currY - prevY).toInt() //* dpi * sensitivity

                if (dx != 0 || dy != 0) {
                    mouseListener?.onMove(dx, dy)
                    Log.d("ACTION--------", "move: (${dx}, ${dy})")
                }
            } else {
                isSingleClick = true
            }
        }
        prevX = currX
        prevY = currY
/*        if (event.pointerCount == 1 && event.y < btnY) { // Перемещение передней одноточечной области сенсорной панели
            // Одноточечная область сенсорной панели, которая отменяет состояние нажатия левой кнопки
            if (isLeftBtnDown) {
                //mouseListener.sendMouseAction(MouseAction.LEFT_UP, 0, 0)
                Log.d("TAG", "LEFT_UP: ")
                isLeftBtnDown = false
            }

            // Сдвиг записи назад
            x1 = event.x - x1
            y1 = event.y - y1
            if (x1 == 0f && y1 == 0f) {
                // Режим одного клика
                isWY = false
            } else {
                // Одноточечный режим смещения
                isWY = true
                // Сдвиг передачи
                //mouseListener.sendMouseAction(MouseAction.MOVE, x1.toInt(), y1.toInt())
                Log.d("TAG", "MOVE: ")
            }
            x1 = event.x
            y1 = event.y
        } else if (event.pointerCount == 2) {
            // Многоточечное движение, передает координаты второй точки,
            // а вторая точка-это функция комбинации клавиш для перемещения в области сенсорной панели
            x2 = event.getX(1) - x2
            y2 = event.getY(1) - y2
            if (isLeftBtnDown || isRightBtnDown) { // Левая и правая функция комбинации клавиш
                // Перетаскивание, состояние перетаскивания
                isLeftMove = true
                //mouseListener.sendMouseAction(MouseAction.MOVE, xT.toInt(), yT.toInt())
                Log.d("TAG", "Состояние перетаскивания MOVE: ")
            }
            x2 = event.getX(1)
            y2 = event.getY(1)
        }*/
    }

    private fun motionUp(event: MotionEvent) {
        Log.d("TAG", "motion up")
        if(isRightClick) {
            isRightClick = false
            mouseListener?.onRightClick()
            Log.d("ACTION--------", "right click")
        } else if(isSingleClick || !isMove) {
            if (SystemClock.elapsedRealtime() - lastClickDelay < DOUBLE_CLICK_DELAY) {
                isSingleClick = false
                clickHandler.removeCallbacks(runnable)
                Log.d("ACTION--------", "double click")
                mouseListener?.onDoubleClick()
            } else {
                isSingleClick = true
                clickHandler.postDelayed(runnable, DOUBLE_CLICK_DELAY)
                lastClickDelay = SystemClock.elapsedRealtime()
            }
        }

        isMove = false

        // Левая кнопка вверх
    /*    if (x1 < btnLeftXend && y1 > btnY) {
            isLeftBtnDown = false
            isLeftMove = false
            //mouseListener.sendMouseAction(MouseAction.LEFT_UP, 0, 0)
            Log.d("TAG", "LEFT_UP: ")
            invalidate()
        }
        // Средняя кнопка вверх
        if (x1 > btnLeftXend && x1 < btnMidXend && y1 > btnY) {
            //mouseListener.sendMouseAction(MouseAction.MIDDLE_UP, 0, 0)
            Log.d("TAG", "MIDDLE_UP: ")
            //区域变色
            isMidBtnDown = false
            invalidate()
        }
        // Правая кнопка вверх
        if (x1 > btnMidXend && y1 > btnY) {
            isRightBtnDown = false
            isLeftMove = false
            //mouseListener.sendMouseAction(MouseAction.RIGHT_UP, 0, 0)
            Log.d("TAG", "RIGHT_UP: ")
            isRightBtnDown = false
            invalidate()
        }
        if (y1 < btnY && !isWY) { // Если это не режим смещения, а в области трекпада,
            // быстрый щелчок, чтобы поднять, это эквивалентно событию левой кнопки мыши
            //mouseListener.sendMouseAction(MouseAction.LEFT_DOWN, 0, 0)
            //mouseListener.sendMouseAction(MouseAction.LEFT_UP, 0, 0)
            Log.d("TAG", "LEFT_DOWN-LEFT_UP: ")
        }
        if (y1 < btnY && isWY) { // Отмените состояние режима смещения
            isWY = false
        }
        x1 = 0f
        y1 = 0f
        x2 = 0f
        y2 = 0f*/
    }
}