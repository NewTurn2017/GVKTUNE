package com.gvkorea.gvktune.view.view.reverberationtime.presenter

import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment.*
import android.os.Handler
import android.provider.MediaStore
import android.widget.Toast
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.gvkorea.gvktune.MainActivity
import com.gvkorea.gvktune.MainActivity.Companion.prefSettings
import com.gvkorea.gvktune.MainActivity.Companion.sInstance
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.R.*
import com.gvkorea.gvktune.util.Protocol
import com.gvkorea.gvktune.view.view.reverberationtime.ReverbFragment
import com.gvkorea.gvktune.view.view.reverberationtime.ReverbFragment.Companion.reverbCount
import com.gvkorea.gvktune.view.view.reverberationtime.util.GVAudioRecord
import com.gvkorea.gvktune.view.view.reverberationtime.util.GVPath
import kotlinx.android.synthetic.main.fragment_reverb.*
import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlin.math.abs

class ReverbPresenter(val view: ReverbFragment, val handler: Handler) {
    var audioRecord: GVAudioRecord
    var rt60Arrays = ArrayList<Float>(5)

    var protocol: Protocol = Protocol()
    private val EQINTERVAL = 50L
    private var tx_buff = ByteArray(13)
    var play: MediaPlayer? = null

    private lateinit var outputStream: OutputStream
    private lateinit var dataOutputStream: DataOutputStream

    val CMD_PARA2_CH1 = '1'
    val CMD_PARA2_CH2 = '2'
    val CMD_PARA2_CHA = 'A'
    init {
        val path =  GVPath()
        path.checkDownloadFolder()
        audioRecord = GVAudioRecord(path, this)
    }

    fun selectedChannal(): Char {
        var para2 = 'A'
        when {
            MainActivity.isSelected_CH1 -> {
                para2 = CMD_PARA2_CH1
            }
            MainActivity.isSelected_CH2 -> {
                para2 = CMD_PARA2_CH2
            }
            MainActivity.isSelected_CHA -> {
                para2 = CMD_PARA2_CHA
            }
        }
        return para2
    }


    fun noiseClap() {

        startRecord()
        handler.postDelayed({
            clapPlay()
        },500)
        handler.postDelayed({
            play?.stop()
            play?.reset()
            if(play != null){
                play?.release()
                play = null
            }
        }, 2600)

    }

    private fun startRecord() {
        msg("측정을 시작합니다.")
        audioRecord.startRecord()
        handler.postDelayed({
            stopRecord()
        }, 4000)

    }
    fun stopRecord() {
        msg("잠시만 기다려 주세요...")
        audioRecord.stopRecord()
    }


    fun msg(msg: String) {
        Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
    }

    fun caculateRT60() {
        reverbCount++
        if(!Python.isStarted()){
            Python.start(AndroidPlatform(view.context))
        }
        getExternalStorageDirectory().absolutePath
            .toString() + "/" + MainActivity.sInstance.resources.getString(
            string.app_name) + "/"
        val py = Python.getInstance()
        val pyf = py.getModule("reverberation")
        val wavPath = getExternalStorageDirectory().absolutePath + "/" + sInstance.resources.getString(string.app_name) + "/rt.wav"
        val graphPath = getExternalStorageDirectory().absolutePath + "/" + sInstance.resources.getString(string.app_name) + "/graph.png"
        val obj = pyf.callAttr("rt60", wavPath, graphPath)
        val arr = pyObjectToArray(obj.toString())
        view.iv_spectrogram.setImageResource(android.R.drawable.ic_delete)
        view.iv_spectrogram.setImageURI(Uri.parse(graphPath))
        view.iv_spectrogram.invalidate()
//        drawLineChart(arr)
        val reverbTime_500hz = arr[0]
        view.tv_reverb.text = "RT60: $reverbTime_500hz (sec)"
        rt60Arrays.add(reverbTime_500hz)
        var testResult = ""
        for(i in rt60Arrays.indices){
            testResult += "${i+1}회차 결과: ${rt60Arrays[i]} sec\n"
        }
        if(reverbCount < 5){
            view.tv_Reverb_result.text = testResult
            noiseClap()
        }else{
            val saveReverb = String.format("%.2f", rt60Arrays.average())
            testResult += "평균 : $saveReverb sec (저장됨)"
            view.tv_Reverb_result.text = testResult
            prefSettings.setReverbTimePref(saveReverb)
            rt60Arrays = ArrayList()
            impulseButtonEnable()

        }
    }

    fun pyObjectToArray(objects: String): FloatArray{
        val arr2 = objects.split(" ").toMutableList()
        val rt60Arrays = FloatArray(arr2.size)
        for(i in arr2.indices){
            if(i== 0){
                rt60Arrays[i] = arr2[i].removePrefix("[").toFloat()
            }else if(i == 6){
                rt60Arrays[i] = arr2[i].removeSuffix("]").toFloat()
            }else{
                rt60Arrays[i] = arr2[i].toFloat()
            }
        }
        return rt60Arrays
    }

    fun clapPlay() {
        play = MediaPlayer.create(view.context, R.raw.ir_clap)
        play?.start()
    }

    fun testReset() {
        view.tv_Reverb_result.text = "측정 결과"
        reverbCount = 0
        rt60Arrays = ArrayList()
        handler.removeMessages(0)
        eqReset()

    }

    fun eqReset() {

        SendPacket_InputGEQ_Reset(selectedChannal())

    }

    fun SendPacket_InputGEQ_Reset(para2: Char) {
        if (MainActivity.selectedClient != null) {

            try {
                tx_buff = protocol.packet_input_EQ_Reset(para2)
                outputStream = MainActivity.selectedClient!!.getOutputStream()
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

    fun impulseButtonDisenable() {
        view.btn_noiseClap.isEnabled = false
    }

    fun impulseButtonEnable(){
        view.btn_noiseClap.isEnabled = true
        view.btn_noiseClap.alpha = 1f
    }

}