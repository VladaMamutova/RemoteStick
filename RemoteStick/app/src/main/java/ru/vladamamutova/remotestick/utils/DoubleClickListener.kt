package ru.vladamamutova.remotestick.utils

import android.os.Handler
import android.os.SystemClock
import android.view.View

/**
 * Слушатель нажатий одиночных и двойных кликов.
 */
abstract class DoubleClickListener() : View.OnClickListener {
    companion object {
        private const val DEFAULT_DOUBLE_CLICK_DELAY: Long = 200
    }

    private var isSingleEvent = false
    private var doubleClickDelay: Long = DEFAULT_DOUBLE_CLICK_DELAY
    private var lastClickDelay: Long = 0
    private var handler: Handler = Handler()
    private var runnable: Runnable

    init {
        runnable = Runnable {
            if (isSingleEvent) {
                onSingleClick()
            }
        }
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - lastClickDelay < doubleClickDelay) {
            isSingleEvent = false
            handler.removeCallbacks(runnable)
            onDoubleClick()
            return
        }
        isSingleEvent = true
        handler.postDelayed(runnable, DEFAULT_DOUBLE_CLICK_DELAY)
        lastClickDelay = SystemClock.elapsedRealtime()
    }

    abstract fun onDoubleClick()
    abstract fun onSingleClick()
}