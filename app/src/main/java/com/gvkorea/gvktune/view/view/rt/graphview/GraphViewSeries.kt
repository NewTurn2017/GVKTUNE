package com.gvkorea.gvktune.view.view.rt.graphview

import android.annotation.TargetApi
import com.gvkorea.gvktune.view.view.rt.graphview.GraphView.*
import java.util.*

class GraphViewSeries(internal var description: String, style: GraphViewStyle?, internal var values: Array<GraphViewData?>) {
  internal var style: GraphViewStyle
  private val graphViews = ArrayList<GraphView>()

  /**
   * graph series style: color and thickness
   */
  class GraphViewStyle {
    var color = -0xff8834
    var thickness = 3

    constructor() : super() {}
    constructor(color: Int, thickness: Int) : super() {
      this.color = color
      this.thickness = thickness
    }
  }


  init {
    var style = style
    if (style == null) {
      style = GraphViewStyle()
    }
    this.style = style
  }

  fun setDescription(description: String) {
    this.description = description
  }

  fun setStyle(color: Int, thickness: Int) {
    val s = GraphViewStyle(color, thickness)
    this.style = s
  }

  /**
   * this graphview will be redrawn if data changes
   * @param graphView
   */
  fun addGraphView(graphView: GraphView) {
    this.graphViews.add(graphView)
  }

  /**
   * add one data to current data
   * @param value the new data to append
   * @param scrollToEnd true => graphview will scroll to the end (maxX)
   */
  @TargetApi(9)
  fun appendData(value: GraphViewData, scrollToEnd: Boolean) {
    val newValues = Arrays.copyOf(values, values.size + 1)
    newValues[values.size] = value
    values = newValues
    for (g in graphViews) {
      if (scrollToEnd) {
        g.scrollToEnd()
      }
    }
  }

  /**
   * clears the current data and set the new.
   * redraws the graphview(s)
   * @param values new data
   */
  fun resetData(values: Array<GraphViewData?>) {
    this.values = values
    for (g in graphViews) {
      g.redrawAll()
    }
  }
}