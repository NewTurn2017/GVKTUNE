package com.gvkorea.gvktune.view.view.rta

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.view.view.rta.listener.SourceCheckChangeListener
import com.gvkorea.gvktune.view.view.rta.listener.SourceSeekBarChangeListener
import com.gvkorea.gvktune.view.view.rta.presenter.NoisePresenter
import kotlinx.android.synthetic.main.fragment_rta.*


class RtaFragment : Fragment() {

    lateinit var presenter: NoisePresenter
    val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = NoisePresenter(this)
        initListener()
    }


    private fun initListener() {
        rg_SorceSelect.setOnCheckedChangeListener(SourceCheckChangeListener(presenter))
        sb_source_gain.setOnSeekBarChangeListener(SourceSeekBarChangeListener(presenter))
    }



    companion object {
        var currentNoise = 0
        var isStarted = false
    }

}
