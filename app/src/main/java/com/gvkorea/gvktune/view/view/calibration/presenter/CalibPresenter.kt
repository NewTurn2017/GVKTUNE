package com.gvkorea.gvktune.view.view.calibration.presenter

import com.gvkorea.gvktune.MainActivity.Companion.CALIBRATION
import com.gvkorea.gvktune.MainActivity.Companion.isCalib
import com.gvkorea.gvktune.MainActivity.Companion.preference


class CalibPresenter {

    fun saveCalibrate(){

        val editor = preference.edit()
        editor.putFloat("calibration", CALIBRATION)
        editor.putBoolean("isCalib", isCalib)
        editor.apply()
    }

    fun loadCalibrate(){
        CALIBRATION = preference.getFloat("calibration", 0F)
        isCalib = preference.getBoolean("isCalib", false)
    }
}