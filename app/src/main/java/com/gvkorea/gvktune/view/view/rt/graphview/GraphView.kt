package com.gvkorea.gvktune.view.view.rt.graphview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.*
import android.view.View
import android.widget.LinearLayout
import java.text.NumberFormat
import java.util.ArrayList

abstract class GraphView(context: Context, title: String?) : LinearLayout(context) {

    protected val paint: Paint
    private var horlabels: Array<String?>? = null
    private var verlabels: Array<String?>? = null
    private var title: String? = null
    /**
     * the user can scroll (horizontal) the graph. This is only useful if you use a viewport [.setViewPort] which doesn't displays all data.
     * @param scrollable
     */
    var isScrollable: Boolean = false
    private var viewportStart: Double = 0.toDouble()
    private var viewportSize: Double = 0.toDouble()
    private val viewVerLabels: View
    private var scaleDetector: ScaleGestureDetector? = null
    private var scalable: Boolean = false
    private var numberformatter: NumberFormat? = null
    private val graphSeries: MutableList<GraphViewSeries>
    var isShowLegend = false
    var legendWidth = 120f
    var legendAlign = LegendAlign.MIDDLE
    private var manualYAxis: Boolean = false
    private var manualMaxYValue: Double = 0.toDouble()
    private var manualMinYValue: Double = 0.toDouble()

    /**
     * returns the maximal Y value of all data.
     *
     * warning: only override this, if you really know want you're doing!
     */
    protected val maxY: Double
        get() {
            var largest: Double
            if (manualYAxis) {
                largest = manualMaxYValue
            } else {
                largest = Integer.MIN_VALUE.toDouble()
                for (i in graphSeries.indices) {
                    val values = _values(i)
                    for (ii in values.indices)
                        if (values[ii]!!.valueY > largest)
                            largest = values[ii]!!.valueY
                }
            }
            return largest
        }

    /**
     * returns the minimal Y value of all data.
     *
     * warning: only override this, if you really know want you're doing!
     */
    protected val minY: Double
        get() {
            var smallest: Double
            if (manualYAxis) {
                smallest = manualMinYValue
            } else {
                smallest = Integer.MAX_VALUE.toDouble()
                for (i in graphSeries.indices) {
                    val values = _values(i)
                    for (ii in values.indices)
                        if (values[ii]!!.valueY < smallest)
                            smallest = values[ii]!!.valueY
                }
            }
            return smallest
        }

    private object GraphViewConfig {
        internal val BORDER = 20f
        internal val VERTICAL_LABEL_WIDTH = 100f
        internal val HORIZONTAL_LABEL_HEIGHT = 80f
    }

    private inner class GraphViewContentView
    /**
     * @param context
     */
    (context: Context) : View(context) {
        private var lastTouchEventX: Float = 0.toFloat()
        private var graphwidth: Float = 0.toFloat()

        init {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT)
        }

        /**
         * @param canvas
         */
        override fun onDraw(canvas: Canvas) {

            paint.isAntiAlias = true

            // normal
            paint.strokeWidth = 0f

            val border = GraphViewConfig.BORDER
            val horstart = 0f
            val height = height.toFloat()
            val width = (width - 1).toFloat()
            val maxY = maxY
            val minY = minY
            val diffY = maxY - minY
            val maxX = getMaxX(false)
            val minX = getMinX(false)
            val diffX = maxX - minX
            val graphheight = height - 2 * border
            graphwidth = width

            if (horlabels == null) {
                horlabels = generateHorlabels(graphwidth)
            }
            if (verlabels == null) {
                verlabels = generateVerlabels(graphheight)
            }

            // vertical lines
            paint.textAlign = Paint.Align.LEFT
            val vers = verlabels!!.size - 1
            for (i in 1 until verlabels!!.size - 1) {
                paint.color = Color.LTGRAY
                val y = graphheight / vers * i + border
                canvas.drawLine(horstart, y, width, y, paint)
            }

            // horizontal labels + lines
            val hors = horlabels!!.size - 1
            for (i in horlabels!!.indices) {
                paint.color = Color.LTGRAY
                val x = graphwidth / hors * i + horstart
                if (i > 0 && i < horlabels!!.size - 1) {
                    canvas.drawLine(x, height - border, x, border, paint)
                }
                paint.textAlign = Paint.Align.CENTER
                if (i == horlabels!!.size - 1)
                    paint.textAlign = Paint.Align.RIGHT
                if (i == 0)
                    paint.textAlign = Paint.Align.LEFT
                paint.color = Color.DKGRAY
                canvas.drawText(horlabels!![i], x, height - 4, paint)
            }

            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(title!!, graphwidth / 2 + horstart, border - 4, paint)

            if (maxY != minY) {
                paint.strokeCap = Paint.Cap.ROUND

                for (i in graphSeries.indices) {
                    paint.strokeWidth = graphSeries[i].style.thickness.toFloat()
                    paint.color = graphSeries[i].style.color
                    drawSeries(canvas, _values(i), graphwidth, graphheight, border, minX, minY, diffX, diffY, horstart)
                }

                if (isShowLegend) drawLegend(canvas, height, width)
            }
        }

        private fun onMoveGesture(f: Float) {
            // view port update
            if (viewportSize != 0.0) {
                viewportStart -= f * viewportSize / graphwidth

                // minimal and maximal view limit
                val minX = getMinX(true)
                val maxX = getMaxX(true)
                if (viewportStart < minX) {
                    viewportStart = minX
                } else if (viewportStart + viewportSize > maxX) {
                    viewportStart = maxX - viewportSize
                }

                // labels have to be regenerated
                horlabels = null
                verlabels = null
                viewVerLabels.invalidate()
            }
            invalidate()
        }

        /**
         * @param event
         */
        override fun onTouchEvent(event: MotionEvent): Boolean {
            if (!isScrollable) {
                return super.onTouchEvent(event)
            }

            var handled = false
            // first scale
            if (scalable && scaleDetector != null) {
                scaleDetector!!.onTouchEvent(event)
                handled = scaleDetector!!.isInProgress
            }
            if (!handled) {
                // if not scaled, scroll
                if (event.action and MotionEvent.ACTION_DOWN == MotionEvent.ACTION_DOWN) {
                    handled = true
                }
                if (event.action and MotionEvent.ACTION_UP == MotionEvent.ACTION_UP) {
                    lastTouchEventX = 0f
                    handled = true
                }
                if (event.action and MotionEvent.ACTION_MOVE == MotionEvent.ACTION_MOVE) {
                    if (lastTouchEventX != 0f) {
                        onMoveGesture(event.x - lastTouchEventX)
                    }
                    lastTouchEventX = event.x
                    handled = true
                }
                if (handled)
                    invalidate()
            }
            if (handled) {
                parent.requestDisallowInterceptTouchEvent(true)
            }
            return handled
        }
    }

    /**
     * one data set for a graph series
     */
    class GraphViewData(val valueX: Double, val valueY: Double) {

        val xy: DoubleArray
            get() {
                val ret = DoubleArray(2)
                ret[0] = valueX
                ret[1] = valueY
                return ret
            }
    }

    enum class LegendAlign {
        TOP, MIDDLE, BOTTOM
    }

    private inner class VerLabelsView
    /**
     * @param context
     */
    (context: Context) : View(context) {
        init {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT, 10f)
        }

        /**
         * @param canvas
         */
        override fun onDraw(canvas: Canvas) {
            // normal
            paint.strokeWidth = 0f

            val border = GraphViewConfig.BORDER
            val height = height.toFloat()
            val graphheight = height - 2 * border

            if (verlabels == null) {
                verlabels = generateVerlabels(graphheight)
            }

            // vertical labels
            paint.textAlign = Paint.Align.LEFT
            val vers = verlabels!!.size - 1
            for (i in verlabels!!.indices) {
                val y = graphheight / vers * i + border
                paint.color = Color.DKGRAY
                canvas.drawText(verlabels!![i], 0f, y, paint)
            }
        }
    }

    init {
        var title = title
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT)

        if (title == null)
            title = ""
        else
            this.title = title

        paint = Paint()
        graphSeries = ArrayList<GraphViewSeries>()

        viewVerLabels = VerLabelsView(context)
        addView(viewVerLabels)
        addView(GraphViewContentView(context), LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT, 1f))
    }

    private fun _values(idxSeries: Int): Array<GraphViewData?> {
        val values = graphSeries[idxSeries].values
        if (viewportStart == 0.0 && viewportSize == 0.0) {
            // all data
            return values
        } else {
            // viewport
            val listData = ArrayList<GraphViewData>()
            for (i in values.indices) {
                if (values[i]!!.valueX >= viewportStart) {
                    if (values[i]!!.valueX > viewportStart + viewportSize) {
                        listData.add(values[i]!!) // one more for nice scrolling
                        break
                    } else {
                        listData.add(values[i]!!)
                    }
                } else {
                    if (listData.isEmpty()) {
                        listData.add(values[i]!!)
                    }
                    listData[0] = values[i]!! // one before, for nice scrolling
                }
            }
            return listData.toTypedArray()
        }
    }

    fun addSeries(series: GraphViewSeries) {
        series.addGraphView(this)
        graphSeries.add(series)
    }

    protected fun drawLegend(canvas: Canvas, height: Float, width: Float) {
        val shapeSize = 15
        val paints = IntArray(graphSeries.size)
        val legendgraphs = IntArray(graphSeries.size)
        for (i in paints.indices) {
            paints[i] = -1
            legendgraphs[i] = -1
        }
        var numpaints = 0
        var breakout: Boolean
        for (j in graphSeries.indices) {
            breakout = false
            for (i in paints.indices) {
                if (paints[i] == graphSeries[j].style.color) {
                    breakout = true
                    break
                }
            }
            if (!breakout) {
                paints[numpaints] = graphSeries[j].style.color
                legendgraphs[numpaints] = j
                numpaints++
            }
        }

        // rect
        paint.setARGB(0, 0, 0, 0)
        val legendHeight = ((shapeSize + 5) * numpaints + 5).toFloat()
        val lLeft = width - legendWidth - 10f
        val lTop: Float
        when (legendAlign) {
            LegendAlign.TOP -> lTop = 10f
            LegendAlign.MIDDLE -> lTop = height / 2 - legendHeight / 2
            else -> lTop = height - GraphViewConfig.BORDER - legendHeight - 10f
        }
        val lRight = lLeft + legendWidth
        val lBottom = lTop + legendHeight
        canvas.drawRoundRect(RectF(lLeft, lTop, lRight, lBottom), 8f, 8f, paint)

        for (i in 0 until numpaints) {
            paint.color = graphSeries[legendgraphs[i]].style.color
            canvas.drawRect(RectF(lLeft + 5, lTop + 5f + (i * (shapeSize + 5)).toFloat(), lLeft + 5f + shapeSize.toFloat(), lTop + (i + 1) * (shapeSize + 5)), paint)
            if (graphSeries[i].description != null) {
                paint.color = Color.BLACK
                paint.textAlign = Paint.Align.LEFT
                canvas.drawText(graphSeries[legendgraphs[i]].description, lLeft + 5f + shapeSize.toFloat() + 5f, lTop + shapeSize.toFloat() + (i * (shapeSize + 5)).toFloat(), paint)
            }
        }
    }

    abstract fun drawSeries(canvas: Canvas, values: Array<GraphViewData?>, graphwidth: Float, graphheight: Float, border: Float, minX: Double, minY: Double, diffX: Double, diffY: Double, horstart: Float)

    /**
     * formats the label
     * can be overwritten
     * @param value x and y values
     * @param isValueX if false, value y wants to be formatted
     * @return value to display
     */
    protected fun formatLabel(value: Double, isValueX: Boolean): String {
        if (numberformatter == null) {
            numberformatter = NumberFormat.getNumberInstance()
            val highestvalue = maxY
            val lowestvalue = minY
            if (highestvalue - lowestvalue < 0.1) {
                numberformatter!!.maximumFractionDigits = 6
            } else if (highestvalue - lowestvalue < 1) {
                numberformatter!!.maximumFractionDigits = 4
            } else if (highestvalue - lowestvalue < 20) {
                numberformatter!!.maximumFractionDigits = 3
            } else if (highestvalue - lowestvalue < 100) {
                numberformatter!!.maximumFractionDigits = 1
            } else {
                numberformatter!!.maximumFractionDigits = 0
            }
        }
        return numberformatter!!.format(value)
    }

    private fun generateHorlabels(graphwidth: Float): Array<String?> {
        val numLabels = (graphwidth / GraphViewConfig.VERTICAL_LABEL_WIDTH).toInt()
        val labels = arrayOfNulls<String>(numLabels + 1)
        val min = getMinX(false)
        val max = getMaxX(false)
        for (i in 0..numLabels) {
            labels[i] = formatLabel(min + (max - min) * i / numLabels, true)
        }
        return labels
    }

    @Synchronized
    private fun generateVerlabels(graphheight: Float): Array<String?> {
        val numLabels = (graphheight / GraphViewConfig.HORIZONTAL_LABEL_HEIGHT).toInt()
        val labels = arrayOfNulls<String>(numLabels + 1)
        val min = minY
        val max = maxY
        for (i in 0..numLabels) {
            labels[numLabels - i] = formatLabel(min + (max - min) * i / numLabels, false)
        }
        return labels
    }

    /**
     * returns the maximal X value of the current viewport (if viewport is set)
     * otherwise maximal X value of all data.
     * @param ignoreViewport
     *
     * warning: only override this, if you really know want you're doing!
     */
    protected fun getMaxX(ignoreViewport: Boolean): Double {
        // if viewport is set, use this
        if (!ignoreViewport && viewportSize != 0.0) {
            return viewportStart + viewportSize
        } else {
            // otherwise use the max x value
            // values must be sorted by x, so the last value has the largest X value
            var highest = 0.0
            if (graphSeries.size > 0) {
                var values = graphSeries[0].values
                highest = values[values.size - 1]!!.valueX
                for (i in 1 until graphSeries.size) {
                    values = graphSeries[i].values
                    highest = Math.max(highest, values[values.size - 1]!!.valueX)
                }
            }
            return highest
        }
    }

    /**
     * returns the minimal X value of the current viewport (if viewport is set)
     * otherwise minimal X value of all data.
     * @param ignoreViewport
     *
     * warning: only override this, if you really know want you're doing!
     */
    protected fun getMinX(ignoreViewport: Boolean): Double {
        // if viewport is set, use this
        if (!ignoreViewport && viewportSize != 0.0) {
            return viewportStart
        } else {
            // otherwise use the min x value
            // values must be sorted by x, so the first value has the smallest X value
            var lowest = 0.0
            if (graphSeries.size > 0) {
                var values = graphSeries[0].values
                lowest = values[0]!!.valueX
                for (i in 1 until graphSeries.size) {
                    values = graphSeries[i].values
                    lowest = Math.min(lowest, values[0]!!.valueX)
                }
            }
            return lowest
        }
    }

    fun redrawAll() {
        verlabels = null
        horlabels = null
        numberformatter = null
        invalidate()
        viewVerLabels.invalidate()
    }

    fun removeSeries(series: GraphViewSeries) {
        graphSeries.remove(series)
    }

    fun removeSeries(index: Int) {
        if (index < 0 || index >= graphSeries.size) {
            throw IndexOutOfBoundsException("No series at index $index")
        }

        graphSeries.removeAt(index)
    }

    fun scrollToEnd() {
        if (!isScrollable) throw IllegalStateException("This GraphView is not scrollable.")
        val max = getMaxX(true)
        viewportStart = max - viewportSize
        redrawAll()
    }

    /**
     * set's static horizontal labels (from left to right)
     * @param horlabels if null, labels were generated automatically
     */
    fun setHorizontalLabels(horlabels: Array<String?>) {
        this.horlabels = horlabels
    }

    /**
     * you have to set the bounds [.setManualYAxisBounds]. That automatically enables manualYAxis-flag.
     * if you want to disable the menual y axis, call this method with false.
     * @param manualYAxis
     */
    fun setManualYAxis(manualYAxis: Boolean) {
        this.manualYAxis = manualYAxis
    }

    /**
     * set manual Y axis limit
     * @param max
     * @param min
     */
    fun setManualYAxisBounds(max: Double, min: Double) {
        manualMaxYValue = max
        manualMinYValue = min
        manualYAxis = true
    }

    /**
     * this forces scrollable = true
     * @param scalable
     */
    @Synchronized
    fun setScalable(scalable: Boolean) {
        this.scalable = scalable
        if (scalable && scaleDetector == null) {
            isScrollable = true // automatically forces this
            scaleDetector = ScaleGestureDetector(context, object : SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val center = viewportStart + viewportSize / 2
                    viewportSize /= detector.scaleFactor
                    viewportStart = center - viewportSize / 2

                    // viewportStart must not be < minX
                    val minX = getMinX(true)
                    if (viewportStart < minX) {
                        viewportStart = minX
                    }

                    // viewportStart + viewportSize must not be > maxX
                    val maxX = getMaxX(true)
                    val overlap = viewportStart + viewportSize - maxX
                    if (overlap > 0) {
                        // scroll left
                        if (viewportStart - overlap > minX) {
                            viewportStart -= overlap
                        } else {
                            // maximal scale
                            viewportStart = minX
                            viewportSize = maxX - viewportStart
                        }
                    }
                    redrawAll()
                    return true
                }
            })
        }
    }

    /**
     * set's static vertical labels (from top to bottom)
     * @param verlabels if null, labels were generated automatically
     */
    fun setVerticalLabels(verlabels: Array<String?>) {
        this.verlabels = verlabels
    }

    /**
     * set's the viewport for the graph.
     * @param start x-value
     * @param size
     */
    fun setViewPort(start: Double, size: Double) {
        viewportStart = start
        viewportSize = size
    }
}