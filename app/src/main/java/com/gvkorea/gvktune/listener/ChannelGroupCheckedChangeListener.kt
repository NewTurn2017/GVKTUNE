package com.gvkorea.gvktune.listener

import android.widget.RadioGroup
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.presenter.MainPresenter

class ChannelGroupCheckedChangeListener(val presenter: MainPresenter) : RadioGroup.OnCheckedChangeListener {
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(checkedId){
            R.id.rb_select_ch1 -> presenter.selectedCH1()
            R.id.rb_select_ch2 -> presenter.selectedCH2()
            R.id.rb_select_all -> presenter.selectedCHA()
        }
    }
}