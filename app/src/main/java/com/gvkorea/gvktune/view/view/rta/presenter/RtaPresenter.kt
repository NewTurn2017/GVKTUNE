package com.gvkorea.gvktune.view.view.rta.presenter

import com.gvkorea.gvktune.view.view.rta.RtaFragment
import com.gvkorea.gvktune.view.view.rta.RtaFragment.Companion.isStartedAudio
import com.gvkorea.gvktune.view.view.rta.RtaFragment.Companion.recordAudio_rta
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRTA
import kotlinx.android.synthetic.main.fragment_rta.*

class RtaPresenter(val view: RtaFragment) {
    fun startButton() {
        if (isStartedAudio) {
            isStartedAudio = false
            view.startStopButton.text = "ON"
            recordAudio_rta.cancel(true)
            view.valid[0]= 0.0
        } else {
            isStartedAudio = true
            view.startStopButton.text = "OFF"
            recordAudio_rta = RecordAudioRTA(view)
            recordAudio_rta.execute()
        }
    }


}