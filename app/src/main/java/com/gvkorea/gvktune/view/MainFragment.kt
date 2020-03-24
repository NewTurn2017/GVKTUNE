package com.gvkorea.gvktune.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gvkorea.gvktune.MainActivity

import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.listener.MainButtonListener
import com.gvkorea.gvktune.view.view.autotuning.TuneFragment
import com.gvkorea.gvktune.view.view.calib.CalibFragment
import com.gvkorea.gvktune.view.presenter.MainMenuPresenter
import com.gvkorea.gvktune.view.view.rt.ReverbFragment
import com.gvkorea.gvktune.view.view.rta.RtaFragment
import com.gvkorea.gvktune.view.view.spkmodel.ModelFragment
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment(val mainActivity: MainActivity) : Fragment() {

    lateinit var presenter: MainMenuPresenter

    val rtaFragment: RtaFragment by lazy { RtaFragment() }
    val calibFragment: CalibFragment by lazy { CalibFragment() }
    val reverbFragment: ReverbFragment by lazy { ReverbFragment() }
    val modelFragment: ModelFragment by lazy { ModelFragment() }
    val tuneFragment: TuneFragment by lazy { TuneFragment() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = MainMenuPresenter(this, mainActivity)
        initListener()
    }

    private fun initListener() {
        btn_rta.setOnClickListener(MainButtonListener(presenter))
        btn_calib.setOnClickListener(MainButtonListener(presenter))
        btn_reverb.setOnClickListener(MainButtonListener(presenter))
        btn_model.setOnClickListener(MainButtonListener(presenter))
        btn_tune.setOnClickListener(MainButtonListener(presenter))
    }
}
