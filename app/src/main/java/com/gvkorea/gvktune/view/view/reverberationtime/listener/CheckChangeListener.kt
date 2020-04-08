package com.gvkorea.gvktune.view.view.reverberationtime.listener

import android.widget.CompoundButton
import com.gvkorea.gvktune.view.view.reverberationtime.ReverbFragment.Companion.isRepeat
import com.gvkorea.gvktune.view.view.reverberationtime.presenter.NoisePresenter

class CheckChangeListener(val presenter: NoisePresenter) : CompoundButton.OnCheckedChangeListener {
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        isRepeat = buttonView?.isChecked!!
    }
}