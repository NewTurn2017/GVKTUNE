package com.gvkorea.gvktune.listener

import android.view.View
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.presenter.MainPresenter

class SpeakerSelectedListener(val presenter: MainPresenter): View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_spk1 -> presenter.selectSpeaker(1)
            R.id.btn_spk2 -> presenter.selectSpeaker(2)
            R.id.btn_spk3 -> presenter.selectSpeaker(3)
            R.id.btn_spk4 -> presenter.selectSpeaker(4)

        }
    }
}