package ru.vladamamutova.remotestick.utils

import android.os.Handler
import android.os.SystemClock
import android.view.View

/**
 * Слушатель нажатий одиночных и двойных кликов.
 */
abstract class DoubleClickListener : View.OnClickListener {
    companion object {
        private const val DEFAULT_DOUBLE_CLICK_DELAY: Long = 200
    }

    private var isSingleClick = false
    private var doubleClickDelay: Long = DEFAULT_DOUBLE_CLICK_DELAY
    private var lastClickDelay: Long = 0
    private var handler: Handler = Handler()
    private var runnable = Runnable {
        if (isSingleClick) {
            onSingleClick()
        }
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - lastClickDelay < doubleClickDelay) {
            isSingleClick = false
            handler.removeCallbacks(runnable)
            onDoubleClick()
            return
        }
        isSingleClick = true
        handler.postDelayed(runnable, DEFAULT_DOUBLE_CLICK_DELAY)
        lastClickDelay = SystemClock.elapsedRealtime()
    }

    abstract fun onDoubleClick()
    abstract fun onSingleClick()
}