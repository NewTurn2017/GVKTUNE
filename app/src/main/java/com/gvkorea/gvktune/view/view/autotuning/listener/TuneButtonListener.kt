package com.gvkorea.gvktune.view.view.autotuning.listener

import android.view.View
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.autotuning.presenter.TunePresenter

class TuneButtonListener(val presenter: TunePresenter): View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_tune_start -> presenter.setTargetVolumeDialog()
            R.id.btn_tune_stop -> presenter.tuneStop()
            R.id.btn_showTable -> presenter.showTable()
            R.id.btn_showEQ -> presenter.showEQ()
        }
    }
}