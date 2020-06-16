package com.gvkorea.gvktune.view.view.data.util.chart

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class ChartLayoutBarChartForRTA(var context: Context, var mBarChart: BarChart){

    private var xVal = ""
    lateinit var xAxisCompLine: XAxis

    fun initBarChartLayout_31(yAxisMax: Float, yAxisMin: Float){
        mBarChart.setDrawBarShadow(false)
        mBarChart.setDrawValueAboveBar(true)
        mBarChart.setTouchEnabled(true)
        mBarChart.dragDecelerationFrictionCoef = 0.9f
        mBarChart.setDrawGridBackground(false)
        mBarChart.isHighlightPerDragEnabled = true
        mBarChart.description.isEnabled = false
        mBarChart.setPinchZoom(false)
        mBarChart.setDrawBorders(false)
        mBarChart.setBackgroundColor(Color.WHITE)
        mBarChart.isDragEnabled = false
        mBarChart.setScaleEnabled(false)

        val freqArray = arrayOf("","", "", "","50", "", "", "100", "", "", "200", "", "", "", "500",
            "", "", "1k", "", "", "2k", "", "", "4k", "", "", "8k", "", "", "16k", "")




        xAxisCompLine = mBarChart.xAxis
        xAxisCompLine.position = XAxis.XAxisPosition.BOTTOM
        xAxisCompLine.setDrawGridLines(false)
        xAxisCompLine.textSize = 10.0f
        xAxisCompLine.labelCount = 31
        xAxisCompLine.valueFormatter = IndexAxisValueFormatter(freqArray)


        val leftAxis = mBarChart.axisLeft
        leftAxis.removeAllLimitLines() // reset all limit lines to avoid overlapping lines
        leftAxis.axisMaximum = yAxisMax
        leftAxis.axisMinimum = yAxisMin
        leftAxis.setDrawZeroLine(true)

        mBarChart.axisRight.isEnabled = false
        val l = mBarChart.legend
        l.form = Legend.LegendForm.LINE
    }
}