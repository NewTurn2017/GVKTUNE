package com.gvkorea.gvktune.view.view.rta.listener

import android.view.View
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.rta.presenter.RtaPresenter
import kotlinx.android.synthetic.main.fragment_rta.view.*

class SetVolumeListener(val presenter: RtaPresenter): View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_volumeStart -> presenter.adjustVolumeStart()
            R.id.btn_volumeStop -> presenter.adjustVolumeStop()
        }
    }
}