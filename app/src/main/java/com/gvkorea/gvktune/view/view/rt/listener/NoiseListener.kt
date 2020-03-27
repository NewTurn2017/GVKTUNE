package com.gvkorea.gvktune.view.view.rt.listener

import android.view.View
import com.gvkorea.gvktune.view.view.rt.presenter.NoisePresenter

class NoiseListener(val presenter: NoisePresenter): View.OnClickListener {
    override fun onClick(v: View?) {
        presenter.clapPlay()
    }
}