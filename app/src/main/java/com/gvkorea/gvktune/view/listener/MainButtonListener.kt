package com.gvkorea.gvktune.view.listener

import android.view.View
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.presenter.MainMenuPresenter

class MainButtonListener(val presenter: MainMenuPresenter): View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_spectrum -> presenter.selectFunction("spectrum")
            R.id.btn_calib -> presenter.selectFunction("calib")
            R.id.btn_reverb -> presenter.selectFunction("reverb")
            R.id.btn_model -> presenter.selectFunction("model")
            R.id.btn_tune -> presenter.selectFunction("tune")
        }
    }
}