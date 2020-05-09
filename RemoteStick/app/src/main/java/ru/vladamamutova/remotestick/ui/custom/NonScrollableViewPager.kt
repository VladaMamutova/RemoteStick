package ru.vladamamutova.remotestick.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager
import java.lang.reflect.Field


class NonScrollableViewPager : ViewPager {
    constructor(context: Context) : super(context) {
        setMyScroller()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setMyScroller()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        performClick()
        // Не позволяем скролл при перемещении между вкладками.
        return false
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        // Не позволяем скролл при перемещении между вкладками.
        return false
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    private fun setMyScroller() {
        try {
            val viewpager: Class<*> = ViewPager::class.java
            val scroller: Field = viewpager.getDeclaredField("mScroller")
            scroller.isAccessible = true
            scroller.set(this, MyScroller(context))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private class MyScroller(context: Context?) : Scroller(context, DecelerateInterpolator()) {
        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            // Для плавного открытия вкладки устанавливаем длительность скролла в 0.005 секунд.
            super.startScroll(startX, startY, dx, dy, 5 /*0,005 sec*/)
        }
    }
}