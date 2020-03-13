package com.gvkorea.gvktune.util

import java.util.*

class MovingAverage(period: Int) {
    private val window: Queue<Double> = LinkedList()
    private val period: Int
    private var sum = 0.0
    fun newNum(num: Double) {
        sum += num
        window.add(num)
        if (window.size > period) {
            sum -= window.remove()
        }
    }

    // technically the average is undefined
    val avg: Double
        get() = if (window.isEmpty()) 0.0 else sum / window.size // technically the average is undefined

    init {
        assert(period > 0) { "Period must be a positive integer" }
        this.period = period
    }
}