package com.gvkorea.gvktune.view.view.autotuning.util.chart

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.gvkorea.gvktune.view.view.autotuning.TuneFragment.Companion.targetValues
import com.gvkorea.gvktune.view.view.rt.ReverbFragment.Companion.valuesArrays
import java.util.*
import kotlin.collections.ArrayList

class ChartLayoutLineChartForTune(val view: Context, var mLineChart: LineChart) {

    private lateinit var xAxis: XAxis

    fun initLineChartLayout(yAxisMax: Float, yAxisMin: Float) {
        mLineChart.dragDecelerationFrictionCoef = 0.9f
        mLineChart.setDrawGridBackground(false)
        mLineChart.description.isEnabled = false
        mLineChart.setPinchZoom(false)
        mLineChart.setDrawBorders(false)
        mLineChart.setBackgroundColor(Color.WHITE)
        mLineChart.isDragEnabled = false

        mLineChart.setScaleEnabled(false)
        xAxis = mLineChart.xAxis
        val freqArray = arrayOf("","", "", "","50", "", "", "100", "", "", "200", "", "", "", "500",
            "", "", "1k", "", "", "2k", "", "", "4k", "", "", "8k", "", "", "16k", "")


        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 10.0f
        xAxis.labelCount = 31
        xAxis.valueFormatter = IndexAxisValueFormatter(freqArray)

        val leftAxis = mLineChart.axisLeft
        leftAxis.removeAllLimitLines()
        leftAxis.setDrawZeroLine(true)
        leftAxis.axisMaximum = yAxisMax
        leftAxis.axisMinimum = yAxisMin
        mLineChart.axisRight.isEnabled = false

    }

    fun initGraph(values: FloatArray?, label: String, color: Int) {
        val valuesArray: ArrayList<Entry> = ArrayList()

        if (values != null) {
            for (i in 0..30) {
                valuesArray.add(Entry(i.toFloat(), values[i]))
            }
        } else {
            for (i in 0..30) {
                valuesArray.add(Entry(i.toFloat(), 0.toFloat()))
            }
        }

        val lineDataSet = LineDataSet(valuesArray, label)
        lineDataSet.color = color
        lineDataSet.setDrawCircles(false)
        lineDataSet.lineWidth = 2f
//        lineDataSet.valueTextColor = color
//        lineDataSet.valueTextSize = 8.0f
        lineDataSet.setDrawValues(false)

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

    fun drawGraph(values: ArrayList<String>?, label: String, color: Int) {
        val targetArray: ArrayList<Entry> = ArrayList()
        val valuesArray: ArrayList<Entry> = ArrayList()

        if (values != null) {
            for (i in 0..30) {
                targetArray.add(Entry(i.toFloat(), targetValues!![i]))
                valuesArray.add(Entry(i.toFloat(), values[i].toFloat()))
            }
        } else {
            for (i in 0..30) {
                targetArray.add(Entry(i.toFloat(), 0.toFloat()))
                valuesArray.add(Entry(i.toFloat(), 0.toFloat()))
            }
        }


        val lineDataSet1 = LineDataSet(targetArray, "타겟(dB)")
        lineDataSet1.color = Color.BLUE
        lineDataSet1.setDrawCircles(false)
        lineDataSet1.lineWidth = 2f
//        lineDataSet.valueTextColor = color
//        lineDataSet.valueTextSize = 8.0f
        lineDataSet1.setDrawValues(false)

        lineDataSet1.mode = LineDataSet.Mode.LINEAR

        val lineDataSet2 = LineDataSet(valuesArray, label)
        lineDataSet2.color = color
        lineDataSet2.setDrawCircles(false)
        lineDataSet2.lineWidth = 2f
//        lineDataSet.valueTextColor = color
//        lineDataSet.valueTextSize = 8.0f
        lineDataSet2.setDrawValues(false)

        lineDataSet2.mode = LineDataSet.Mode.LINEAR


        val data: LineData
        data = LineData(lineDataSet1, lineDataSet2)

        xAxis.axisMaximum = data.xMax + 0.4f
        xAxis.axisMinimum = data.xMin - 0.4f

        mLineChart.data = data
        mLineChart.data.notifyDataChanged()
        mLineChart.notifyDataSetChanged()
        mLineChart.invalidate()
    }


}