package ru.vladamamutova.remotestick.utils

import android.util.Log
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent


class AdvancedGestureListener : SimpleOnGestureListener() {
    override fun onDown(event: MotionEvent): Boolean {
        Log.d("TAG", "onDown: ")

        // don't return false here or else none of the other
        // gestures will work
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        Log.i("TAG", "onSingleTapConfirmed: ")
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        Log.i("TAG", "onLongPress: ")
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        Log.i("TAG", "onDoubleTap: ")
        return true
    }

    override fun onScroll(
        e1: MotionEvent, e2: MotionEvent,
        distanceX: Float, distanceY: Float
    ): Boolean {
        Log.i("TAG", "onScroll: ")
        return true
    }

    override fun onFling(
        event1: MotionEvent, event2: MotionEvent,
        velocityX: Float, velocityY: Float
    ): Boolean {
        Log.d("TAG", "onFling: ")
        return true
    }
}
