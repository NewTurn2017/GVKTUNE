package com.gvkorea.gvktune.view.view.reverberationtime.listener

import android.view.View
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.reverberationtime.presenter.NoisePresenter

class NoiseListener(val presenter: NoisePresenter): View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_noise -> presenter.noise()
            R.id.btn_noiseClap -> presenter.noiseClap()
        }
    }
}