package com.gvkorea.gvktune.util

import com.gvkorea.gvktune.MainActivity.Companion.pref

class PrefSettings {

    val NOISE_VOLUME = "noiseVolume"
    val REVERB_TIME = "reverbTime"

    fun setNoiseVolumePref(value: Float){
        val editor = pref.edit()
        editor.putFloat(NOISE_VOLUME, value)
        editor.apply()
    }

    fun getNoiseVolumePref():Float{
        return pref.getFloat(NOISE_VOLUME, -40f)
    }

    fun setReverbTimePref(value: String){
        val editor = pref.edit()
        editor.putString(REVERB_TIME, value)
        editor.apply()
    }

    fun getReverbTimePref(): String{
        return pref.getString(REVERB_TIME, "Not measured yet")
    }

}