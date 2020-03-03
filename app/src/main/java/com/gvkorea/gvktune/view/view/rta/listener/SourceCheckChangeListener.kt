package com.gvkorea.gvktune.view.view.rta.listener

import android.widget.RadioGroup
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.rta.RtaFragment.Companion.currentNoise
import com.gvkorea.gvktune.view.view.rta.presenter.NoisePresenter

class SourceCheckChangeListener(val presenter: NoisePresenter): RadioGroup.OnCheckedChangeListener {
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(checkedId){
            R.id.rb_analog_input -> {
                currentNoise = presenter.NOISE_OFF
                presenter.noise(presenter.NOISE_OFF)
            }
            R.id.rb_pink -> {
                currentNoise = presenter.PINK
                presenter.noise(presenter.PINK)
            }
            R.id.rb_white -> {
                currentNoise = presenter.WHITE
                presenter.noise(presenter.WHITE)
            }
            R.id.rb_sweep -> {
                currentNoise = presenter.SWEEP
                presenter.noise(presenter.SWEEP)
            }
        }
    }
}