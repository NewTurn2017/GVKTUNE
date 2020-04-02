package com.gvkorea.gvktune.view.view.rt.listener

import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.CompoundButton
import com.gvkorea.gvktune.view.view.rt.ReverbFragment.Companion.isRepeat
import com.gvkorea.gvktune.view.view.rt.presenter.NoisePresenter

class CheckChangeListener(val presenter: NoisePresenter) : CompoundButton.OnCheckedChangeListener {
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        isRepeat = buttonView?.isChecked!!
    }
}