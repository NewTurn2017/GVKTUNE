package com.gvkorea.gvktune.view.view.rta.listener

import android.widget.SeekBar
import com.gvkorea.gvktune.view.view.rta.RtaFragment.Companion.currentNoise
import com.gvkorea.gvktune.view.view.rta.RtaFragment.Companion.isStarted
import com.gvkorea.gvktune.view.view.rta.presenter.NoisePresenter

class SourceSeekBarChangeListener(val presenter: NoisePresenter): SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if(!isStarted){
            currentNoise = presenter.NOISE_OFF
            isStarted = true
        }else{
            presenter.noise(currentNoise)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}