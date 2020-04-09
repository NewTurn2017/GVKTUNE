package com.gvkorea.gvktune.view.view.evaluation.listener

import android.view.View
import android.widget.AdapterView
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.evaluation.EvalueateFragment
import com.gvkorea.gvktune.view.view.evaluation.EvalueateFragment.Companion.averageTime

class SpinnerItemSeletedListener(val fragment: EvalueateFragment) : AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val time = fragment.resources.getStringArray(R.array.avgTime_array)[position]
        averageTime = time.toInt() * 1000

    }
}