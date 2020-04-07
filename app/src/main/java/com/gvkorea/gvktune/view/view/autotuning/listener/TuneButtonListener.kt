package com.gvkorea.gvktune.view.view.autotuning.listener

import android.view.View
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.autotuning.TuneFragment.Companion.noiseVolume
import com.gvkorea.gvktune.view.view.autotuning.presenter.TunePresenter

class TuneButtonListener(val presenter: TunePresenter): View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_tune_start -> presenter.tuneStart()
            R.id.btn_tune_stop -> presenter.tuneStop()
            R.id.btn_eqreset -> presenter.eqReset()
            R.id.btn_noiseOn -> presenter.noise(presenter.PINK, noiseVolume)
            R.id.btn_noiseOff -> presenter.noise(presenter.NOISE_OFF, noiseVolume)
            R.id.btn_avg -> presenter.average()
            R.id.btn_open -> presenter.openLoop()
            R.id.btn_closed -> presenter.closedLoop()
        }
    }
}