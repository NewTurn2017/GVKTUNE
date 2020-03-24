package com.gvkorea.gvktune.view.view.rt.util

interface AudioClipListener {
    fun heard(audioData: ShortArray, sampleRate: Int): Boolean
}