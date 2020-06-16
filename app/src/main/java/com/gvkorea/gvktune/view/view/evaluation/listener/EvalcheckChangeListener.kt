package com.gvkorea.gvktune.view.view.evaluation.listener

import android.widget.CompoundButton
import com.gvkorea.gvktune.view.view.evaluation.EvaluateFragment.Companion.isEvalRepeat

class EvalcheckChangeListener : CompoundButton.OnCheckedChangeListener {
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        isEvalRepeat = buttonView?.isChecked!!
    }
}