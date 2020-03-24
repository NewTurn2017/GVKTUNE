package com.gvkorea.gvktune.view.view.rt.util

class Smooth {

    fun smooth(signal: FloatArray, window: Int): FloatArray {
        val size = Math.floor((signal.size / window).toDouble()).toInt()
        val data = FloatArray(size)

        data[0] = signal[0]
        for (i in 1 until size) {
            data[i] = (1 - ALPHA) * data[i - 1] + ALPHA * signal[i * window]
        }
        return data
    }

    companion object {
        internal val ALPHA = 0.1f
    }

}