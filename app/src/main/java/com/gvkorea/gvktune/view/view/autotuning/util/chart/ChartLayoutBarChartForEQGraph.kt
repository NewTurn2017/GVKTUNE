package com.gvkorea.gvktune.view.view.autotuning.util.chart

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

class ChartLayoutBarChartForEQGraph(var context: Context, var mBarChart: BarChart) {

  private var xVal = ""
  lateinit var xAxisCompLine: XAxis

  internal fun initLineChartLayout(yAxisMax: Float, yAxisMin: Float) {

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

    xAxisCompLine = mBarChart.xAxis
    xAxisCompLine.position = XAxis.XAxisPosition.BOTTOM
    xAxisCompLine.setDrawGridLines(false)
    xAxisCompLine.labelCount = 31
    xAxisCompLine.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
      override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val array = arrayOf("","", "", "","50", "", "", "100", "", "", "200", "", "", "", "500", "", "", "1k", "", "", "2k", "", "", "4k", "", "", "8k", "", "", "16k", "")
        when (value.toInt()) {
          0  -> xVal = array[0]
          1  -> xVal = array[1]
          2  -> xVal = array[2]
          3  -> xVal = array[3]
          4  -> xVal = array[4]
          5  -> xVal = array[5]
          6  -> xVal = array[6]
          7  -> xVal = array[7]
          8  -> xVal = array[8]
          9  -> xVal = array[9]
          10 -> xVal = array[10]
          11 -> xVal = array[11]
          12 -> xVal = array[12]
          13 -> xVal = array[13]
          14 -> xVal = array[14]
          15 -> xVal = array[15]
          16 -> xVal = array[16]
          17 -> xVal = array[17]
          18 -> xVal = array[18]
          19 -> xVal = array[19]
          20 -> xVal = array[20]
          21 -> xVal = array[21]
          22 -> xVal = array[22]
          23 -> xVal = array[23]
          24 -> xVal = array[24]
          25 -> xVal = array[25]
          26 -> xVal = array[26]
          27 -> xVal = array[27]
          28 -> xVal = array[28]
          29 -> xVal = array[29]
          30 -> xVal = array[30]
        }
        return xVal
      }
    }
    xAxisCompLine.textSize = 8.0f


    val leftAxis = mBarChart.axisLeft
    leftAxis.removeAllLimitLines() // reset all limit lines to avoid overlapping lines
    leftAxis.axisMaximum = yAxisMax
    leftAxis.axisMinimum = yAxisMin
    leftAxis.setDrawZeroLine(true)

    mBarChart.axisRight.isEnabled = false
    val l = mBarChart.legend
    l.form = Legend.LegendForm.LINE
  }

  fun initGraph(values: FloatArray) {
    val curModelValues: ArrayList<BarEntry> = ArrayList()
    val colors = java.util.ArrayList<Int>()

    val green = Color.rgb(110, 190, 102)
    val red = Color.rgb(211, 74, 88)

    if (values != null) {
      for (i in 0..30) {
        if(values[i] >= 0){
          colors.add(red)
        }else{
          colors.add(green)
        }

        curModelValues.add(BarEntry(i.toFloat(), values[i].toFloat()))
      }
    } else {

      for (i in 0..30) {
        colors.add(red)
        curModelValues.add(BarEntry(i.toFloat(), 0.toFloat()))
      }
    }



    val BarDataSet1 = BarDataSet(curModelValues, "스피커 현재 EQ")
    BarDataSet1.colors = colors
//    BarDataSet1.setValueTextColors(colors)
    BarDataSet1.setDrawValues(false)
    val data = BarData(BarDataSet1)
    data.barWidth = 0.8f
    xAxisCompLine.axisMaximum = data.xMax + 0.4f
    xAxisCompLine.axisMinimum = data.xMin - 0.4f

    mBarChart.data = data
    mBarChart.data.notifyDataChanged()
    mBarChart.notifyDataSetChanged()
    mBarChart.invalidate()


  }

  private inner class ValueFormatter : IValueFormatter {

    private val mFormat: DecimalFormat = DecimalFormat("######")

    override fun getFormattedValue(value: Float, entry: Entry, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String {
      return mFormat.format(value.toDouble())
    }
  }

}