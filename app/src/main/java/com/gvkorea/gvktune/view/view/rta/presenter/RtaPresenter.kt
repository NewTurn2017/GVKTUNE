package com.gvkorea.gvktune.view.view.rta.presenter

import android.os.Handler
import android.view.View
import android.widget.Toast
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CH1
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CH2
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CHA
import com.gvkorea.gvktune.MainActivity.Companion.preference
import com.gvkorea.gvktune.MainActivity.Companion.selectedClient
import com.gvkorea.gvktune.util.Protocol
import com.gvkorea.gvktune.view.view.rta.RtaFragment
import com.gvkorea.gvktune.view.view.rta.RtaFragment.Companion.counter
import com.gvkorea.gvktune.view.view.rta.RtaFragment.Companion.isShow
import com.gvkorea.gvktune.view.view.rta.RtaFragment.Companion.noiseVolume
import com.gvkorea.gvktune.view.view.rta.RtaFragment.Companion.targetdB
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.avgStart
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq1Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq2Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq3Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq4Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq5Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq6Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq7Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq8Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq9Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq10Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq11Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq12Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq13Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq14Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq15Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq16Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq17Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq18Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq19Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq20Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq21Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq22Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq23Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq24Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq25Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq26Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq27Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq28Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq29Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq30Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freq31Sum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.freqSum
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.isMeasure
import com.gvkorea.gvktune.view.view.rta.util.audio.RecordAudioRta.Companion.spldB
import kotlinx.android.synthetic.main.fragment_rta.*
import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream

class RtaPresenter(val view: RtaFragment, val handler: Handler) {

    private val protocol = Protocol()

    val NOISE_OFF = 3
    val SWEEP = 0
    val PINK = 2
    val WHITE = 1
    private lateinit var tx_buff: ByteArray
    private lateinit var outputStream: OutputStream
    private lateinit var dataOutputStream: DataOutputStream
    private val CHECKINTERVAL = 50L

    val CMD_PARA2_CH1 = '1'
    val CMD_PARA2_CH2 = '2'
    val CMD_PARA2_CHA = 'A'

    private val EQINTERVAL = 50L
    private val ranEQ = IntArray(31)


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

    private val hzArrays = arrayOf("20", "25", "31.5", "40", "50", "63", "80", "100", "125", "160",
        "200", "250", "315", "400", "500", "630", "800", "1000", "1250", "1600",
        "2000", "2500", "3150", "4000", "5000", "6300", "8000", "10000", "12500", "16000", "20000")

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

    fun adjustVolumeStart() {
        val calib = preference.getBoolean("isCalib", false)
        if(calib){
            view.btn_volumeStart.text = "진행중"
            view.btn_volumeStart.isEnabled = false
            noiseVolume = -40
            handler.postDelayed({
                eqReset()
            }, 200)
            handler.postDelayed({
                NoiseVolumeControl(noiseVolume)
            }, 500)
        }else{
            toastMsg("마이크 캘리브레이션이 되지 않았습니다. 캘리브레이션 후 튜닝바랍니다.")
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
                    toastMsg("마이크 캘리브레이션을 확인 바랍니다.(초기화 -> 캘리브레이션)")
                    view.btn_volumeStart.text = "음압셋팅"
                    handler.removeMessages(0)
                    noise(NOISE_OFF, noiseVolume)
                }
            }else {
                toastMsg("읍압 셋팅이 완료되었습니다.")
                noise(NOISE_OFF, noiseVolume)
                view.btn_volumeStart.text = "음압셋팅"
                view.btn_volumeStart.isEnabled = true
                view.btn_volumeStart.alpha = 1f
            }
        }, 500)
    }

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

    private fun eqReset() {
        SendPacket_InputGEQ_Reset(selectedChannal())
    }

    fun adjustVolumeStop() {
        noise(NOISE_OFF, noiseVolume)
        handler.removeMessages(0)
        view.btn_volumeStart.text = "음압셋팅"
        view.btn_volumeStart.isEnabled = true
    }
    private fun toastMsg(msg: String) {
        Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
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

    fun ranEQ() {
        for (i in ranEQ.indices) {
            ranEQ[i] = randomRange()
        }
        SendPacket_EQ_All(selectedChannal(), ranEQ)
    }
    private fun randomRange(): Int {

        return (Math.random() * 61).toInt()// 0~60
        // 25~48 : (int) (Math.random() * 25) + 24;
    }

    private fun SendPacket_EQ_All(para2: Char, ranEQ: IntArray) {
        val ranEQToFloat = changeRandomEQToFloatArray(ranEQ)
        if (selectedClient != null) {

            try {
                tx_buff = protocol.packet_InputGEQ_All(para2, ranEQToFloat)
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

    private fun changeRandomEQToFloatArray(ranEQ: IntArray): FloatArray {
        val ranEQFloatArray = FloatArray(ranEQ.size)
        for (i in ranEQ.indices) {
            ranEQFloatArray[i] = ranEQ[i] * 0.5f - 15
        }
        return ranEQFloatArray
    }

    fun average() {
        counter = 0
        measure(true)
        msg("평균 측정 중...")
        handler.postDelayed({
            measure(false)
            msg("평균 측정 갯수 $counter 개")

        }, 1100)
        handler.postDelayed({
            showTable()
        }, 1300)
    }

    private fun updateTable() {
        if(freqSum.size > 0){
            var str = String.format("%-20s%-20s", "Hz", "dBSPL") + "\n"
            for( i in 0 until 31){
                str += String.format("%-20s%-20s", hzArrays[i], freqSum[i]) + "\n"
                str += "----------------------------------------------\n"
            }
            view.tv_table.text = str
        } else{
            view.tv_table.text = "데이터가 없습니다."
        }
    }

    fun showTable() {
        if(!isShow){
            view.mChart.visibility = View.GONE
            view.scrollView.visibility = View.VISIBLE
            updateTable()
            view.btn_table.text = "RTA 보기"
            isShow = true
        }else{
            view.mChart.visibility = View.VISIBLE
            view.scrollView.visibility = View.GONE
            view.btn_table.text = "테이블 보기"
            isShow = false
        }
    }
}