package com.gvkorea.gvktune.view.view.reverberationtime.listener

import android.view.View
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.reverberationtime.ReverbFragment.Companion.reverbCount
import com.gvkorea.gvktune.view.view.reverberationtime.presenter.ReverbPresenter

class ReverbListener(val presenter: ReverbPresenter): View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_noiseClap -> {
                presenter.impulseButtonDisenable()
                reverbCount = 0
                presenter.noiseClap()
            }
            R.id.btn_testReset -> presenter.testReset()
        }
    }
}