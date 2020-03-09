package com.gvkorea.gvktune.view.activity.util

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter

class ChartLayoutLineChart(var context: Context, var mLineChart: LineChart) {
    private var xVal = ""
    lateinit var xAxisCompLine: XAxis

    internal fun initLineChartLayout(yAxisMax: Float, yAxisMin: Float) {

        mLineChart.dragDecelerationFrictionCoef = 0.9f
        mLineChart.setDrawGridBackground(false)
        mLineChart.description.isEnabled = false
        mLineChart.setPinchZoom(false)
        mLineChart.setDrawBorders(false)
        mLineChart.setBackgroundColor(Color.WHITE)
        mLineChart.isDragEnabled = false
        mLineChart.setScaleEnabled(false)
        // x-axis limit line

        // x-axis limit line
        val xIAxisValueFormatter = IAxisValueFormatter { value, _ ->
            val xValue = value.toInt()

            when (xValue) {
                0 -> xVal = "20"
                1 -> xVal = "25"
                2 -> xVal = "31.5"
                3 -> xVal = "40"
                4 -> xVal = "50"
                5 -> xVal = "63"
                6 -> xVal = "80"
                7 -> xVal = "100"
                8 -> xVal = "125"
                9 -> xVal = "160"
                10 -> xVal = "200"
                11 -> xVal = "250"
                12 -> xVal = "315"
                13 -> xVal = "400"
                14 -> xVal = "500"
                15 -> xVal = "630"
                16 -> xVal = "800"
                17 -> xVal = "1k"
                18 -> xVal = "1.25k"
                19 -> xVal = "1.6k"
                20 -> xVal = "2k"
                21 -> xVal = "2.5k"
                22 -> xVal = "3.15k"
                23 -> xVal = "4k"
                24 -> xVal = "5k"
                25 -> xVal = "6.3k"
                26 -> xVal = "8k"
                27 -> xVal = "10k"
                28 -> xVal = "12.5k"
                29 -> xVal = "16k"
                30 -> xVal = "20k"
            }
            return@IAxisValueFormatter xVal
        }

        xAxisCompLine = mLineChart.xAxis
        xAxisCompLine.position = XAxis.XAxisPosition.BOTTOM
        xAxisCompLine.setDrawGridLines(false)
        xAxisCompLine.labelCount = 31
        xAxisCompLine.valueFormatter = xIAxisValueFormatter
        xAxisCompLine.textSize = 8.0f

        val leftAxis = mLineChart.axisLeft
        leftAxis.removeAllLimitLines() // reset all limit lines to avoid overlapping lines
        leftAxis.axisMaximum = yAxisMax
        leftAxis.axisMinimum = yAxisMin
        leftAxis.setDrawZeroLine(true)
        mLineChart.axisRight.isEnabled = false
        val l = mLineChart.legend
        l.form = Legend.LegendForm.LINE
    }
    fun initGraph(values: ArrayList<Double>) {
        val curModelValues: ArrayList<Entry> = ArrayList()

        for (i in 0 until values.size) {
            curModelValues.add(Entry(i.toFloat(), values[i].toFloat()))
        }





        val lineDataSet1 = LineDataSet(curModelValues, "Avg.")
        lineDataSet1.color = Color.RED
        lineDataSet1.setDrawCircles(false)
        lineDataSet1.lineWidth = 2f
        lineDataSet1.valueTextColor = Color.RED
        lineDataSet1.mode = LineDataSet.Mode.CUBIC_BEZIER

        val data = LineData(lineDataSet1)

        xAxisCompLine.axisMaximum = data.xMax + 0.4f
        xAxisCompLine.axisMinimum = data.xMin - 0.4f

        mLineChart.data = data
        mLineChart.data.notifyDataChanged()
        mLineChart.notifyDataSetChanged()
        mLineChart.invalidate()


    }

}