package com.gvkorea.gvktune.view.view.autotuning.presenter

import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.gvkorea.gvktune.MainActivity
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CH1
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CH2
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CHA
import com.gvkorea.gvktune.MainActivity.Companion.selectedClient
import com.gvkorea.gvktune.util.Protocol
import com.gvkorea.gvktune.util.WaitingDialog
import com.gvkorea.gvktune.view.view.autotuning.TuneFragment
import com.gvkorea.gvktune.view.view.autotuning.TuneFragment.Companion.lineChart
import com.gvkorea.gvktune.view.view.autotuning.TuneFragment.Companion.targetValues
import com.gvkorea.gvktune.view.view.autotuning.TuneFragment.Companion.targetdB
import com.gvkorea.gvktune.view.view.autotuning.util.ann.ANN_Open
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.avgStart
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq1Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq2Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq3Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq4Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq5Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq6Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq7Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq8Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq9Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq10Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq11Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq12Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq13Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq14Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq15Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq16Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq17Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq18Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq19Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq20Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq21Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq22Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq23Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq24Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq25Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq26Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq27Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq28Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq29Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq30Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freq31Sum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.freqSum
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.isMeasure
import com.gvkorea.gvktune.view.view.autotuning.util.audio.RecordAudioTune.Companion.spldB
import kotlinx.android.synthetic.main.fragment_tune.*
import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class TunePresenter(val view: TuneFragment, val handler: Handler) {

    private val protocol = Protocol()

    val NOISE_OFF = 3
    val PINK = 2

    private lateinit var tx_buff: ByteArray
    private lateinit var outputStream: OutputStream
    private lateinit var dataOutputStream: DataOutputStream
    private val CHECKINTERVAL = 50L
    private val EQINTERVAL = 50L
    var curEQ = IntArray(31)
    val CMD_PARA2_CH1 = '1'
    val CMD_PARA2_CH2 = '2'
    val CMD_PARA2_CHA = 'A'
    var noiseVolume = -40

    fun selectedChannal(): Char {
        var para2 = 'A'

        when {
            isSelected_CH1 -> {
                para2 = CMD_PARA2_CH1
            }
            isSelected_CH2 -> {
                para2 = CMD_PARA2_CH2
            }
            isSelected_CHA -> {
                para2 = CMD_PARA2_CHA
            }
        }


        return para2
    }

    fun tuneStart() {
        adjustVolumeStart()
        view.btn_tune_start.text = "음압셋팅 중.."
        view.btn_tune_stop.isEnabled = true
    }

    fun tuneStop() {
        noise(NOISE_OFF, noiseVolume)
        handler.removeMessages(0)
        view.btn_tune_start.text = "자동튜닝시작"
        view.btn_tune_start.isEnabled = true
        view.btn_tune_start.alpha = 1f

    }
    fun adjustVolumeStart() {
        val calib = MainActivity.preference.getBoolean("isCalib", false)
        if(calib){
            view.btn_tune_start.text = "진행중"
            view.btn_tune_start.isEnabled = false
            noiseVolume = -40
            handler.postDelayed({
                eqReset()
            }, 200)
            handler.postDelayed({
                NoiseVolumeControl(noiseVolume)
            }, 500)
        }else{
            msg("마이크 캘리브레이션이 되지 않았습니다. 캘리브레이션 후 튜닝바랍니다.")
        }
    }


    private fun NoiseVolumeControl(progress: Int) {
        noise(PINK, progress)
        handler.postDelayed({
            if (spldB.toInt() < targetdB) {
                noiseVolume++
                if(noiseVolume < 0){
                    NoiseVolumeControl(noiseVolume)
                }else{
                    msg("마이크 캘리브레이션을 확인 바랍니다.(초기화 -> 캘리브레이션)")
                    view.btn_tune_start.text = "자동튜닝시작"
                    handler.removeMessages(0)
                    noise(NOISE_OFF, noiseVolume)
                }
            }else {
                msg("읍압 셋팅이 완료되었습니다.")
                noise(NOISE_OFF, noiseVolume)
                view.btn_tune_start.text = "자동튜닝 중.."
                view.btn_tune_start.isEnabled = true
                view.btn_tune_start.alpha = 1f
                autoTuning()
            }
        }, 500)
    }

    private fun autoTuning() {
        openLoop()
    }

    fun setTarget(targetdB: Int): FloatArray {
        val target = targetdB.toFloat()
        val targetdBs = FloatArray(31)
        targetdBs[0] = target - 15
        targetdBs[1] = target - 13
        targetdBs[2] = target - 11
        targetdBs[3] = target - 9
        targetdBs[4] = target - 7
        targetdBs[5] = target - 5
        targetdBs[6] = target - 3
        targetdBs[7] = target
        targetdBs[8] = target
        targetdBs[9] = target
        targetdBs[10] = target
        targetdBs[11] = target
        targetdBs[12] = target
        targetdBs[13] = target
        targetdBs[14] = target
        targetdBs[15] = target
        targetdBs[16] = target
        targetdBs[17] = target
        targetdBs[18] = target
        targetdBs[19] = target
        targetdBs[20] = target
        targetdBs[21] = target
        targetdBs[22] = target
        targetdBs[23] = target
        targetdBs[24] = target
        targetdBs[25] = target
        targetdBs[26] = target
        targetdBs[27] = target
        targetdBs[28] = target
        targetdBs[29] = target
        targetdBs[30] = target
        return targetdBs
    }

    fun eqReset() {
        SendPacket_InputGEQ_Reset(selectedChannal())

    }

    fun SendPacket_InputGEQ_Reset(para2: Char) {
        if (selectedClient != null) {

            try {
                tx_buff = protocol.packet_input_EQ_Reset(para2)
                outputStream = selectedClient!!.getOutputStream()
                dataOutputStream = DataOutputStream(outputStream)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            Thread {
                try {
                    dataOutputStream.write(tx_buff, 0, tx_buff.size)
                    dataOutputStream.flush()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()

            try {
                Thread.sleep(EQINTERVAL)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            Thread.currentThread()
            Thread.interrupted()
        } else {
            msg("TCP Socket 연결 안됨")
        }
    }

    fun noise(noise: Int, progress: Int) {
        if (noise != NOISE_OFF) {
            val para2 = selectedChannal()
            val gain = progress.toFloat()
            SendPacket_NoiseGenerator(para2, noise, gain, 1)
        } else {
            val para2 = selectedChannal()
            val gain = progress.toFloat()
            SendPacket_NoiseGenerator(para2, PINK, gain, 0)
        }


    }

    fun average(){
        measure(true)
        WaitingDialog(view.context!!).create("평균 측정 중입니다..", 1000)
        handler.postDelayed({
            measure(false)
        }, 1100)
        handler.postDelayed({
            lineChart.drawGraph(freqSum, "현재 측정값(dB)", Color.RED)
            msg("측정 완료")
        }, 1300)    }

    fun measure(isStart:Boolean){
        if(isStart){
            handler.postDelayed({
                freq1Sum = ArrayList()
                freq2Sum = ArrayList()
                freq3Sum = ArrayList()
                freq4Sum = ArrayList()
                freq5Sum = ArrayList()
                freq6Sum = ArrayList()
                freq7Sum = ArrayList()
                freq8Sum = ArrayList()
                freq9Sum = ArrayList()
                freq10Sum = ArrayList()
                freq11Sum = ArrayList()
                freq12Sum = ArrayList()
                freq13Sum = ArrayList()
                freq14Sum = ArrayList()
                freq15Sum = ArrayList()
                freq16Sum = ArrayList()
                freq17Sum = ArrayList()
                freq18Sum = ArrayList()
                freq19Sum = ArrayList()
                freq20Sum = ArrayList()
                freq21Sum = ArrayList()
                freq22Sum = ArrayList()
                freq23Sum = ArrayList()
                freq24Sum = ArrayList()
                freq25Sum = ArrayList()
                freq26Sum = ArrayList()
                freq27Sum = ArrayList()
                freq28Sum = ArrayList()
                freq29Sum = ArrayList()
                freq30Sum = ArrayList()
                freq31Sum = ArrayList()
                freqSum = ArrayList()
                avgStart = true
                isMeasure = true
            }, 100)
        }else{
            avgStart = true
            isMeasure = false
        }
    }

    fun openLoop() {
        val open = ANN_Open(view.activity?.assets!!, targetValues!!)
        val eqValues = open.getControlEQ_Open()
        val eqFloats = floatToInt(eqValues)
        curEQ = eqFloats
        SendPacket_EQ_All(selectedChannal(), curEQ)
        handler.postDelayed({

        },200)
    }

    private fun floatToInt(results: FloatArray): IntArray {
        val resultsToInt = IntArray(31)
        for (i in results.indices) {
            if(results[i] >= 60){
                resultsToInt[i] = 60
            }else if(results[i] <= 0){
                resultsToInt[i] = 0
            }else{
                resultsToInt[i] = results[i].roundToInt()
            }
        }
        return resultsToInt
    }

    private fun SendPacket_EQ_All(para2: Char, eqValues: IntArray) {
        val eq = changeEQValues(eqValues)
        if (selectedClient != null) {

            try {
                tx_buff = protocol.packet_InputGEQ_All(para2, eq)
                outputStream = selectedClient!!.getOutputStream()
                dataOutputStream = DataOutputStream(outputStream)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            Thread {
                try {
                    dataOutputStream.write(tx_buff, 0, tx_buff.size)
                    dataOutputStream.flush()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()

            try {
                Thread.sleep(EQINTERVAL)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            Thread.currentThread()
            Thread.interrupted()
        } else {
            msg("TCP Socket 연결 안됨")
        }
    }

    private fun changeEQValues(eqValues: IntArray): FloatArray {
        val eqFloatArray = FloatArray(eqValues.size)
        for (i in eqValues.indices) {
            eqFloatArray[i] = eqValues[i] * 0.5f - 15
        }
        return eqFloatArray
    }

    fun closedLoop() {

    }


    fun SendPacket_NoiseGenerator(
        para2: Char, data0: Int, data1: Float, data5: Int
    ) {
        if (selectedClient != null) {

            try {
                tx_buff = protocol.packet_NoiseGenerator(
                    para2,
                    data0,
                    data1,
                    data5
                )
                outputStream = selectedClient!!.getOutputStream()
                dataOutputStream = DataOutputStream(outputStream)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            Thread {
                try {
                    dataOutputStream.write(tx_buff, 0, tx_buff.size)
                    dataOutputStream.flush()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()

            try {
                Thread.sleep(CHECKINTERVAL)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            Thread.currentThread()
            Thread.interrupted()
        } else {
            msg("TCP Socket 연결 안됨")
        }
    }

    fun msg(msg: String) {
        Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
    }
}