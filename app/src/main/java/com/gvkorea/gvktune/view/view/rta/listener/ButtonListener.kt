package com.gvkorea.gvktune.view.view.rta.listener

import android.view.View
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.rta.RtaFragment.Companion.noiseVolume
import com.gvkorea.gvktune.view.view.rta.presenter.RtaPresenter
import kotlinx.android.synthetic.main.fragment_rta.view.*

class ButtonListener(val presenter: RtaPresenter): View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_ranEQ -> presenter.ranEQ()
            R.id.btn_noiseOn -> presenter.noise(presenter.PINK, noiseVolume)
            R.id.btn_noiseOff -> presenter.noise(presenter.NOISE_OFF, noiseVolume)
            R.id.btn_measureAvg -> presenter.average()
            R.id.btn_table -> presenter.showTable()
        }
    }
}