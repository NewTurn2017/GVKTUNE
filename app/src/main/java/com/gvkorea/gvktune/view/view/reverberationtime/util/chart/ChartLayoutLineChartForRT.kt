package com.gvkorea.gvktune.view.view.reverberationtime.util.chart

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.gvkorea.gvktune.view.view.reverberationtime.ReverbFragment.Companion.valuesArrays
import java.util.*
import kotlin.collections.ArrayList

class ChartLayoutLineChartForRT(val view: Context, var mLineChart: LineChart) {

    private lateinit var xAxis: XAxis
    fun initLineChartLayout() {
        mLineChart.dragDecelerationFrictionCoef = 0.9f
        mLineChart.setDrawGridBackground(false)
        mLineChart.description.isEnabled = false
        mLineChart.setPinchZoom(false)
        mLineChart.setDrawBorders(false)
        mLineChart.setBackgroundColor(Color.WHITE)
        mLineChart.isDragEnabled = false
        mLineChart.setScaleEnabled(false)
        xAxis = mLineChart.xAxis
        val freqArray = arrayListOf("125hz", "250hz", "500hz", "1khz", "2khz", "4khz", "8khz")


        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.labelCount = 7
        xAxis.textSize = 10.0f
        xAxis.valueFormatter = IndexAxisValueFormatter(freqArray)

        val leftAxis = mLineChart.axisLeft
        leftAxis.removeAllLimitLines()
        leftAxis.setDrawZeroLine(true)
        mLineChart.axisRight.isEnabled = false

    }

    fun initGraph(values: FloatArray?, label: String, color: Int) {
        val valuesArray: ArrayList<Entry> = ArrayList()

        if (values != null) {
            for (i in 0..6) {
                valuesArray.add(Entry(i.toFloat(), values[i]))
            }
        } else {
            for (i in 0..6) {
                valuesArray.add(Entry(i.toFloat(), 0.toFloat()))
            }
        }


        val lineDataSet = LineDataSet(valuesArray, label)
        lineDataSet.color = color
        lineDataSet.setDrawCircles(false)
        lineDataSet.lineWidth = 2f
        lineDataSet.valueTextColor = color
        lineDataSet.valueTextSize = 8.0f

        lineDataSet.mode = LineDataSet.Mode.LINEAR


        val data: LineData
        data = LineData(lineDataSet)

        xAxis.axisMaximum = data.xMax + 0.4f
        xAxis.axisMinimum = data.xMin - 0.4f

        mLineChart.data = data
        mLineChart.data.notifyDataChanged()
        mLineChart.notifyDataSetChanged()
        mLineChart.invalidate()


    }

    fun initGraphRepeat(values: ArrayList<FloatArray?>, label: ArrayList<String>) {
        var valuesArray: ArrayList<Entry> = ArrayList()

        for (i in values.indices) {

            for (j in 0..6) {
                valuesArray.add(Entry(j.toFloat(), values[i]!![j]))

            }
            valuesArrays.add(valuesArray)
            valuesArray = ArrayList()
        }

        val data = LineData()

        for (i in values.indices) {
            val lineDataSet = LineDataSet(valuesArrays[i], label[i])
            val color = randomColor()
            lineDataSet.color = color
            lineDataSet.setDrawCircles(false)
            lineDataSet.lineWidth = 2f
            lineDataSet.valueTextColor = color
            lineDataSet.valueTextSize = 8.0f

            lineDataSet.mode = LineDataSet.Mode.LINEAR
            data.addDataSet(lineDataSet)
        }

        xAxis.axisMaximum = data.xMax + 0.4f
        xAxis.axisMinimum = data.xMin - 0.4f

        mLineChart.data = data
        mLineChart.data.notifyDataChanged()
        mLineChart.notifyDataSetChanged()
        mLineChart.invalidate()
    }

    private fun randomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }
}