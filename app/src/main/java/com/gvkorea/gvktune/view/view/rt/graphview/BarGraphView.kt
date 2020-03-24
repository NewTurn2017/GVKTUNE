package com.gvkorea.gvktune.view.view.rt.graphview

import android.content.Context
import android.graphics.Canvas

class BarGraphView(context: Context, title: String) : GraphView(context, title) {

    override fun drawSeries(canvas: Canvas, values: Array<GraphViewData?>, graphwidth: Float, graphheight: Float,
                            border: Float, minX: Double, minY: Double, diffX: Double, diffY: Double,
                            horstart: Float) {
        val colwidth = graphwidth / values.size

        // draw data
        for (i in values.indices) {
            val valY = (values[i]!!.valueY - minY).toFloat()
            val ratY = (valY / diffY).toFloat()
            val y = graphheight * ratY
            canvas.drawRect(i * colwidth + horstart, border - y + graphheight, i * colwidth + horstart + (colwidth - 1), graphheight + border - 1, paint)
        }
    }
}