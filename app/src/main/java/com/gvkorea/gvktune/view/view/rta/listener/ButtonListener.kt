package com.gvkorea.gvktune.view.view.rta.listener

import android.view.View
import com.gvkorea.gvktune.view.view.rta.presenter.RtaPresenter

class ButtonListener(val presenter: RtaPresenter): View.OnClickListener {
    override fun onClick(v: View?) {
       presenter.startButton()
    }
}