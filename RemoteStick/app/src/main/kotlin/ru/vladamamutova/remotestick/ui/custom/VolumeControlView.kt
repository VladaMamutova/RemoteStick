package ru.vladamamutova.remotestick.ui.custom

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.ui.listeners.VolumeListener
import kotlin.math.*


class VolumeControlView (context: Context, attrs: AttributeSet? = null)
    : View(context, attrs) {
    companion object {
        private const val DEFAULT_PADDING = 30

        private const val DEFAULT_MIN_WIDTH = 100
        private const val DEFAULT_MIN_HEIGHT = 100

        private const val DEFAULT_MIN_VALUE = 0
        private const val DEFAULT_MAX_VALUE = 100
        private const val DEFAULT_STEP = 1

        private const val DEFAULT_ARC_WIDTH = 40
        private const val DEFAULT_THUMB_PADDING = 4
        private const val DEFAULT_START_ANGLE = 0
        private const val DEFAULT_END_ANGLE = 360
        private const val DEFAULT_VALUE_SIZE = 14

        private const val DEFAULT_THUMB_COLOR = Color.WHITE
        private const val DEFAULT_ARC_COLOR = Color.GRAY
        private const val DEFAULT_ARC_HIGHLIGHT_COLOR = Color.BLACK
        private const val DEFAULT_VALUE_COLOR = Color.BLACK

        /**
         * Неточность касания. Добавляется к радиусу бегунка
         * при определении, попадает ли точка касания в круг бегунка.
         */
        private const val DEFAULT_TOUCH_INACCURACY = 20
    }

    private class Thumb {
        var x: Float = 0f
        var y: Float = 0f
        var radius: Float = 0f
        var angle: Double = 0.0 // в радианах
        var border: Float = DEFAULT_THUMB_PADDING.toFloat()

        val paint: Paint = Paint().apply {
            color = DEFAULT_THUMB_COLOR
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val strokePaint: Paint = Paint().apply {
            color = DEFAULT_ARC_HIGHLIGHT_COLOR
            style = Paint.Style.STROKE
            strokeWidth = border
            isAntiAlias = true
        }

        fun isPointInside(xPoint: Float, yPoint: Float, touchInaccuracy: Int): Boolean {
            return (sqrt(
                (xPoint - x) * (xPoint - x) + (yPoint - y) * (yPoint - y).toDouble()
            ) < radius + border + touchInaccuracy)
        }
    }

    private var thumb: Thumb = Thumb()
    private var isThumbSelected: Boolean = false

    private var padding = 0

    private var min = DEFAULT_MIN_VALUE
    private var max = DEFAULT_MAX_VALUE
    private var step = DEFAULT_STEP
    private var value = min

    private var arcWidth: Float = DEFAULT_ARC_WIDTH.toFloat()
    private var arcColor = DEFAULT_ARC_COLOR
    private var arcHighlightStartColor = DEFAULT_ARC_HIGHLIGHT_COLOR
    private var arcHighlightEndColor = DEFAULT_ARC_HIGHLIGHT_COLOR
    private val arcPaint: Paint = Paint().apply {
        color = arcColor
        style = Paint.Style.STROKE
        strokeWidth = DEFAULT_ARC_WIDTH.toFloat()
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private val valuePaint: TextPaint = TextPaint().apply {
        color = DEFAULT_VALUE_COLOR
        isAntiAlias = true
    }
    private val viewRect = RectF()
    private val arcRect = RectF()

    private var x0: Float = 0f
    private var y0: Float = 0f
    private var radius: Int = 0

    /**
     * Угол открытия арки от 0 до 360 градусов с отчётом углов по часовой стрелке.
     */
    private var arcStartAngle: Float = DEFAULT_START_ANGLE.toFloat()

    /**
     * Угол поворота арки от 0 до 360 градусов с отчётом углов по часовой стрелке.
     */
    private var arcSweepAngle: Float = DEFAULT_END_ANGLE.toFloat()

    private var volumeListener: VolumeListener? = null

    fun setVolumeListener(listener: VolumeListener) {
        volumeListener = listener
    }

    init {
        val attributes = context.theme.obtainStyledAttributes(
            attrs, R.styleable.VolumeControlView, 0, 0
        )

        padding = attributes.getDimensionPixelSize(
            R.styleable.VolumeControlView_padding, DEFAULT_PADDING
        )

        min = attributes.getInteger(
            R.styleable.VolumeControlView_min, DEFAULT_MIN_VALUE
        )
        max = attributes.getInteger(
            R.styleable.VolumeControlView_max, DEFAULT_MAX_VALUE
        )
        step = attributes.getInteger(
            R.styleable.VolumeControlView_step, DEFAULT_STEP
        )
        min -= min % step // гарантия, что min будет нацело делиться на step
        max -= max % step // гарантия, что max будет нацело делиться на step

        value = attributes.getInteger(R.styleable.VolumeControlView_value, min)
        value -= value % step // гарантия, что value будет нацело делиться на step
        value = value.coerceAtLeast(min).coerceAtMost(max)
        valuePaint.color = attributes.getColor(
            R.styleable.VolumeControlView_value_color, DEFAULT_VALUE_COLOR
        )
        valuePaint.textSize = attributes.getDimensionPixelSize(
            R.styleable.VolumeControlView_value_size, DEFAULT_VALUE_SIZE
        ).toFloat()

        arcWidth = attributes.getDimensionPixelSize(
            R.styleable.VolumeControlView_arc_width, DEFAULT_ARC_WIDTH
        ).toFloat()
        arcPaint.strokeWidth = arcWidth
        arcColor = attributes.getColor(
            R.styleable.VolumeControlView_arc_color, DEFAULT_ARC_COLOR
        )
        arcHighlightStartColor = attributes.getColor(
            R.styleable.VolumeControlView_arc_highlight_start_color,
            DEFAULT_ARC_HIGHLIGHT_COLOR
        )
        arcHighlightEndColor = attributes.getColor(
            R.styleable.VolumeControlView_arc_highlight_end_color,
            DEFAULT_ARC_HIGHLIGHT_COLOR
        )

        thumb.paint.color = attributes.getColor(
            R.styleable.VolumeControlView_thumb_color, DEFAULT_THUMB_COLOR
        )
        thumb.strokePaint.color = arcHighlightEndColor

        var startAngle = attributes.getInteger(
            R.styleable.VolumeControlView_start_angle, DEFAULT_START_ANGLE
        )
        var endAngle = attributes.getInteger(
            R.styleable.VolumeControlView_end_angle, DEFAULT_END_ANGLE
        )
        startAngle = startAngle.coerceAtLeast(0).coerceAtMost(360)
        endAngle = endAngle.coerceAtLeast(0).coerceAtMost(360)

        arcStartAngle = startAngle.toFloat()
        arcSweepAngle = (endAngle - startAngle).toFloat()
        // Если угол поворота отрицательный, то добавляем полный круг,
        // чтобы привести его в диапазон от 0 до 360.
        if (arcSweepAngle < 0) arcSweepAngle += 360

        thumb.angle = Math.toRadians(getAngleByValue(value))
        thumb.radius = arcWidth / 2 - DEFAULT_THUMB_PADDING

        attributes.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Отрисовываем основу арки.
        arcPaint.color = arcColor
        canvas.drawArc(arcRect, arcStartAngle, arcSweepAngle, false, arcPaint)

        // Подсвечиваем выделенную бегунком арку.

        // Текущий угол бегунка в диапазоне от 0 до 360 градусов.
        val angle: Float = calcReverseSweepAngle(
            Math.toDegrees(thumb.angle),
            arcStartAngle
        ).toFloat()

        if (arcHighlightStartColor == arcHighlightEndColor) { // однотонная арка
            arcPaint.color = arcHighlightStartColor
        } else { // арка с градиентом
            // Так как дуга отрисовывается с закруглёнными углами, то при нанесении
            // разверточного градиента, необходимо учесть радиус скругления (радиус бегунка).

            // Угол в градусах, который опирается на хорду, равную радиусу бегунка.
            val thumbRadiusAngle = Math.toDegrees(
                asin((thumb.radius + thumb.border) / radius).toDouble()
            ).toFloat()
            val colors = intArrayOf(
                arcHighlightStartColor, arcHighlightEndColor, arcColor, arcColor
            )
            val positions = floatArrayOf(
                0f, ((angle + thumbRadiusAngle) / 360f),
                ((angle + thumbRadiusAngle + 1) / 360f), 1f
            )
            val gradient = SweepGradient(x0, y0, colors, positions)
            val angleRotate = arcStartAngle - thumbRadiusAngle
            val gradientMatrix = Matrix()
            gradientMatrix.preRotate(angleRotate, x0, y0)
            gradient.setLocalMatrix(gradientMatrix)
            arcPaint.shader = gradient
        }

        canvas.drawArc(arcRect, arcStartAngle, angle, false, arcPaint)
        arcPaint.clearShadowLayer()

        // Отрисовываем бегунок.
        thumb.x = (x0 + radius * cos(thumb.angle)).toFloat()
        thumb.y = (y0 - radius * sin(thumb.angle)).toFloat()
        canvas.drawCircle(thumb.x, thumb.y, thumb.radius, thumb.paint)
        canvas.drawCircle(
            thumb.x, thumb.y, thumb.radius + thumb.border / 2, thumb.strokePaint
        )

        // Отрисовываем текущее значение бегунка в центре компонента.
        val text = value.toString()
        val textBounds = Rect()
        valuePaint.getTextBounds(text, 0, text.length, textBounds)
        // measureText() является более точным, чем textBounds.width().
        val textWidth = valuePaint.measureText(text)
        canvas.drawText(
            text, x0 - textWidth / 2f, y0 + textBounds.height() / 2f, valuePaint
        )
    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                // Проверяем, было ли касание по бегунку, чтобы начать его перемещать.
                isThumbSelected = thumb.isPointInside(
                    motionEvent.x, motionEvent.y, DEFAULT_TOUCH_INACCURACY
                )
                if (isThumbSelected) {
                    volumeListener?.beforeVolumeChanged(value)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isThumbSelected) {
                    updateThumbState(thumb, motionEvent.x, motionEvent.y)
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                isThumbSelected = false
                updateThumbState(thumb, motionEvent.x, motionEvent.y)
                volumeListener?.afterVolumeChanged(value)
                invalidate()
            }
        }
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        arcRect.set(
            viewRect.left + padding, viewRect.top + padding,
            viewRect.right - padding, viewRect.bottom - padding
        )

        x0 = arcRect.centerX()
        y0 = arcRect.centerY()

        // радиус арки равен меньшей стророне
        radius = (if (arcRect.width() < arcRect.height()) {
            arcRect.width() / 2
        } else {
            arcRect.height() / 2
        }).toInt()

        super.onSizeChanged(w, h, oldw, oldh)
    }

    private fun updateThumbState(thumb: Thumb, touchX: Float, touchY: Float) {
        val dx = touchX - x0
        val dy = y0 - touchY // координата Y возрастает сверху вниз
        val radius = sqrt(dx.toDouble().pow(2.0) + dy.toDouble().pow(2.0))
        var angle = acos(dx / radius)

        // Если бегунок находится I и II четрвети,
        // то угол остаётся неизменным (от 0 до Пи).
        if (dy < 0) {
            // Если бегунок находится в III или IV четверти,
            // то угол будет зеркальным (от -Пи до 0).
            angle = -angle
        }

        // Получаем данный угол в градусах с отчетом уголов по часовой стрелке.
        var angleInDegrees: Double = Math.toDegrees(angle)
        angleInDegrees = calcReverseSweepAngle(angleInDegrees, arcStartAngle)
        if (angleInDegrees > arcSweepAngle) {
            // Если угол бегунка превышает угол поворота дуги,
            // устанавливаем начальный или конечный угол дуги в зависимости от
            // того, с каким из них он имеет наименьшую разницу.
            angle = if (abs(angleInDegrees - arcSweepAngle) < abs(360 - angleInDegrees)) {
                getRadianAngle((arcSweepAngle).toDouble(), arcStartAngle)
            } else {
                getRadianAngle(0.0, arcStartAngle)
            }
        }

        // Перемещаем бегунок, если значение не превосходит 1/6 количества делений.
        val nextValue: Int = getValueByAngle(angle)
        if (abs(nextValue - value) < (max - min) / 6) {
            // Отправляем событие изменения значения, когда
            // оно больше, чем установленный шаг.
            if (abs(nextValue - value) >= step) {
                volumeListener?.volumeChanged(nextValue, step)
            }
            thumb.angle = angle
            value = nextValue
        }
    }

    private fun getValueByAngle(angle: Double): Int {
        val stepSweepAngle: Double = (arcSweepAngle / (max / step - min)).toDouble()
        val steps = (calcReverseSweepAngle(Math.toDegrees(angle), arcStartAngle)
                / stepSweepAngle).roundToInt()
        return min + steps * step
    }

    private fun getAngleByValue(value: Int): Double {
        val stepSweepAngle: Double = (arcSweepAngle / (max - min)).toDouble()
        val steps: Float = (value - min).toFloat()
        val arcAngle: Double = arcStartAngle + stepSweepAngle * steps
        return 360 - arcAngle
    }

    /**
     * Переводит угол в диапазоне от 0 до 180 и от -180 до 0 градусов
     * против часовой стрелки в угол со значением от 0 до 360 по
     * часовой стрелке с указанным углом начала отсчёта в градусах.
     */
    private fun calcReverseSweepAngle(angle: Double, originAngle: Float): Double {
        var reverseSweepAngle = if (angle > 0) {
            // I и II четверти (0..180) - получаем этот же угол, но
            // в обратном направлении отсчёта углов (против часовой стрелки).
            360 - angle // от 180 до 360
        } else { // III и IV четверти (-180..0) - отражаем угол по вертикали.
            abs(angle) // от 0 до 180
        }

        // Вычитаем угол начала отсчёта.
        reverseSweepAngle -= originAngle

        // Если значение отрицательное, то добавляем полный круг, чтобы получить
        // значение угла в диапазоне от 0 до 360. Угол остаётся прежним.
        if (reverseSweepAngle < 0) reverseSweepAngle += 360

        return reverseSweepAngle
    }

    /**
     * Переводит угол в диапазоне от 0 до 360 градусов по часовой стрелке
     * в угол со значением от 0 до 180 или -180 до 0 против часовой стрелки
     * с указанным углом начала отсчёта в градусах.
     */
    private fun getRadianAngle(angleInDegrees: Double, originAngle: Float): Double {
        // Проделываем обратные операции из метода calcReverseSweepAngle().
        // Добавляем угол начала отсчёта и приводим угол в диапазон от 0 до 360.
        var angle = angleInDegrees + originAngle
        if (angle > 360) angle -= 360

        angle = if (angle > 180) {
            // III и IV четверти (180..360) - получаем этот же угол, но
            // в обратном направлении отсчёта углов (против часовой стрелки).
            360 - angle // от 0 до 180
        } else { // I и II четверти (0..180) - отражаем угол по вертикали.
            -angle // от -180 до 0
        }

        return Math.toRadians(angle)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> { // жёсткое задание размера компонента
                abs(widthSize)
            }
            MeasureSpec.AT_MOST -> { // размер не должен превышать размер родителя
                DEFAULT_MIN_WIDTH.coerceAtMost(widthSize)
            }
            else -> { // любой желаемый размер
                DEFAULT_MIN_WIDTH
            }
        }

        val height = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> { // жёсткое задание размера компонента
                abs(heightSize)
            }
            MeasureSpec.AT_MOST -> { // размер не должен превышать размер родителя
                DEFAULT_MIN_HEIGHT.coerceAtMost(heightSize)
            }
            else -> { // любой желаемый размер
                DEFAULT_MIN_HEIGHT
            }
        }

        // Определяем размеры области рисования с учётом ширины арки.
        val halfArcWidth: Float = arcWidth / 2f
        viewRect.set(
            halfArcWidth, halfArcWidth,
            width - halfArcWidth, height - halfArcWidth
        )

        setMeasuredDimension(width, height)
    }
}