package com.gvkorea.gvktune.view.view.rt.util

import android.content.Context
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.gvkorea.gvktune.R

class NowLayout : LinearLayout, ViewTreeObserver.OnGlobalLayoutListener {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        //Log.d(TAG, "NowLayout created");
        initLayoutObserver()

    }

    constructor(context: Context) : super(context) {
        initLayoutObserver()
    }

    private fun initLayoutObserver() {
        orientation = VERTICAL
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        viewTreeObserver.removeGlobalOnLayoutListener(this)
        val heightPx = context.resources.displayMetrics.heightPixels

        var inversed = false
        val childCount = childCount

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            val location = IntArray(2)

            child.getLocationOnScreen(location)

            if (location[1] > heightPx) {
                break
            }

            if (!inversed) {
                child.startAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.slide_up_left))
            } else {
                child.startAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.slide_up_right))
            }

            inversed = !inversed
        }

    }



}