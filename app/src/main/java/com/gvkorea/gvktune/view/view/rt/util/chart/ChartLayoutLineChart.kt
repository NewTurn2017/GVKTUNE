package com.gvkorea.gvktune.view.view.rt.util.chart

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ChartLayoutLineChart(val view: Context, var mLineChart: LineChart) {

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
                valuesArray.add(Entry(i.toFloat(), values[i]*1000))
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
}