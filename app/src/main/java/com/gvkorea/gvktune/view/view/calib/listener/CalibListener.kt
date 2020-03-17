package com.gvkorea.gvktune.view.view.calib.listener

import android.view.View
import android.widget.Toast
import com.gvkorea.gvktune.MainActivity.Companion.CALIBRATION
import com.gvkorea.gvktune.MainActivity.Companion.isCalib
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.calib.audio.RecordAudioCalib.Companion.rmsValue
import com.gvkorea.gvktune.view.view.calib.presenter.CalibPresenter

class CalibListener(val view: View) : View.OnClickListener {
    var presenter = CalibPresenter()
    override fun onClick(p0: View?) {

        when(p0?.id){
            R.id.btn_Calib -> {
                presenter.loadCalibrate()
                CALIBRATION += (94 - rmsValue).toFloat()
                isCalib = true
                presenter.saveCalibrate()
            }
            R.id.btn_CalibReset -> {
                CALIBRATION = 0f
                isCalib = false
                Toast.makeText(view.context, "CALIBRATION 값이 초기화 되었습니다.", Toast.LENGTH_SHORT).show()
                presenter.saveCalibrate()
            }
        }
    }
}