package com.gvkorea.gvktune.view.view.rt.graphview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint

class LineGraphView(context: Context, title: String) : GraphView(context, title) {
  private val paintBackground: Paint
  /**
   * @param drawBackground true for a light blue background under the graph line
   */
  var drawBackground: Boolean = false

  init {

    paintBackground = Paint()
    paintBackground.setARGB(255, 20, 40, 60)
    paintBackground.strokeWidth = 4f
  }

  override fun drawSeries(canvas: Canvas, values: Array<GraphViewData?>, graphwidth: Float, graphheight: Float, border: Float, minX: Double, minY: Double, diffX: Double, diffY: Double, horstart: Float) {
    // draw background
    var lastEndY = 0.0
    var lastEndX = 0.0
    if (drawBackground) {
      val startY = graphheight + border
      for (i in values.indices) {
        val valY = values[i]!!.valueY - minY
        val ratY = valY / diffY
        val y = graphheight * ratY

        val valX = values[i]!!.valueX - minX
        val ratX = valX / diffX
        val x = graphwidth * ratX

        val endX = x.toFloat() + (horstart + 1)
        val endY = (border - y).toFloat() + graphheight + 2f

        if (i > 0) {
          // fill space between last and current point
          val numSpace = ((endX - lastEndX) / 3f).toInt() + 1
          for (xi in 0 until numSpace) {
            val spaceX = (lastEndX + (endX - lastEndX) * xi / (numSpace - 1)).toFloat()
            val spaceY = (lastEndY + (endY - lastEndY) * xi / (numSpace - 1)).toFloat()

            // start => bottom edge

            // do not draw over the left edge
            if (spaceX - horstart > 1) {
              canvas.drawLine(spaceX, startY, spaceX, spaceY, paintBackground)
            }
          }
        }

        lastEndY = endY.toDouble()
        lastEndX = endX.toDouble()
      }
    }

    // draw data
    lastEndY = 0.0
    lastEndX = 0.0
    for (i in values.indices) {
      val valY = values[i]!!.valueY - minY
      val ratY = valY / diffY
      val y = graphheight * ratY

      val valX = values[i]!!.valueX - minX
      val ratX = valX / diffX
      val x = graphwidth * ratX

      if (i > 0) {
        val startX = lastEndX.toFloat() + (horstart + 1)
        val startY = (border - lastEndY).toFloat() + graphheight
        val endX = x.toFloat() + (horstart + 1)
        val endY = (border - y).toFloat() + graphheight

        canvas.drawLine(startX, startY, endX, endY, paint)
      }
      lastEndY = y
      lastEndX = x
    }
  }
}