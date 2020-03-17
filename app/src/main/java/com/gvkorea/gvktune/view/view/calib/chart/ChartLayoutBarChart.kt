package com.gvkorea.gvktune.view.view.calib.chart

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis

class ChartLayoutBarChart(var context: Context, var mBarChart: BarChart) {

    fun initBarChartLayout(yAxisMax: Float, yAxisMin: Float){
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

        // x-axis limit line

        val xAxis = mBarChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(false)


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