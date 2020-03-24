package com.gvkorea.gvktune.view.view.rt.graphview

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import java.lang.reflect.Method

class ScaleGestureDetector
/**
 * @param context
 * @param simpleOnScaleGestureListener
 */
(context: Context, simpleOnScaleGestureListener: SimpleOnScaleGestureListener) {

  private var realScaleGestureDetector: Any? = null
  private var method_getScaleFactor: Method? = null
  private var method_isInProgress: Method? = null
  private var method_onTouchEvent: Method? = null

  val scaleFactor: Double
    get() {
      if (method_getScaleFactor != null) {
        try {
          return (method_getScaleFactor!!.invoke(realScaleGestureDetector) as Float).toDouble()
        } catch (e: Exception) {
          e.printStackTrace()
          return 1.0
        }

      }
      return 1.0
    }

  val isInProgress: Boolean
    get() {
      if (method_getScaleFactor != null) {
        try {
          return method_isInProgress!!.invoke(realScaleGestureDetector) as Boolean
        } catch (e: Exception) {
          e.printStackTrace()
          return false
        }

      }
      return false
    }


  interface SimpleOnScaleGestureListener {
    fun onScale(detector: ScaleGestureDetector): Boolean
  }

  init {
    try {
      // check if class is available
      Class.forName("android.view.ScaleGestureDetector")

      // load class and methods
      val classRealScaleGestureDetector = Class.forName("com.jjoe64.graphview.compatible.RealScaleGestureDetector")
      method_getScaleFactor = classRealScaleGestureDetector.getMethod("getScaleFactor")
      method_isInProgress = classRealScaleGestureDetector.getMethod("isInProgress")
      method_onTouchEvent = classRealScaleGestureDetector.getMethod("onTouchEvent", MotionEvent::class.java)

      // create real ScaleGestureDetector
      val constructor = classRealScaleGestureDetector.getConstructor(Context::class.java, javaClass, SimpleOnScaleGestureListener::class.java)
      realScaleGestureDetector = constructor.newInstance(context, this, simpleOnScaleGestureListener)
    } catch (e: Exception) {
      // not available
      Log.w("com.jjoe64.graphview", "*** WARNING *** No scaling available for graphs. Exception:")
      e.printStackTrace()
    }

  }

  fun onTouchEvent(event: MotionEvent) {
    if (method_onTouchEvent != null) {
      try {
        method_onTouchEvent!!.invoke(realScaleGestureDetector, event)
      } catch (e: Exception) {
        e.printStackTrace()
      }

    }
  }
}
