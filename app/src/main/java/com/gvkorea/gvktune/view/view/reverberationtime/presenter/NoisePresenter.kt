package com.gvkorea.gvktune.view.view.reverberationtime.presenter

import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment.*
import android.os.Handler
import android.widget.Toast
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.gvkorea.gvktune.MainActivity
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CH1
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CH2
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CHA
import com.gvkorea.gvktune.MainActivity.Companion.prefSettings
import com.gvkorea.gvktune.MainActivity.Companion.sInstance
import com.gvkorea.gvktune.MainActivity.Companion.selectedClient
import com.gvkorea.gvktune.R.*
import com.gvkorea.gvktune.util.Protocol
import com.gvkorea.gvktune.view.view.reverberationtime.ReverbFragment
import com.gvkorea.gvktune.view.view.reverberationtime.ReverbFragment.Companion.arrList
import com.gvkorea.gvktune.view.view.reverberationtime.ReverbFragment.Companion.chart
import com.gvkorea.gvktune.view.view.reverberationtime.ReverbFragment.Companion.isRepeat
import com.gvkorea.gvktune.view.view.reverberationtime.ReverbFragment.Companion.labelList
import com.gvkorea.gvktune.view.view.reverberationtime.ReverbFragment.Companion.repeatCount
import com.gvkorea.gvktune.view.view.reverberationtime.ReverbFragment.Companion.valuesArrays
import com.gvkorea.gvktune.view.view.reverberationtime.util.GVAudioRecord
import com.gvkorea.gvktune.view.view.reverberationtime.util.GVPath
import kotlinx.android.synthetic.main.fragment_reverb.*
import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream

class NoisePresenter(val view: ReverbFragment, val handler: Handler) {
    private val protocol = Protocol()

    val NOISE_OFF = 3
    val SWEEP = 0
    val PINK = 2
    val WHITE = 1
    private lateinit var tx_buff: ByteArray
    private lateinit var outputStream: OutputStream
    private lateinit var dataOutputStream: DataOutputStream
    var audioRecord: GVAudioRecord

    private val CHECKINTERVAL = 50L

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
    fun noiseClap() {

        startRecord()
        handler.postDelayed({
            clapPlay()
        },200)
    }

    fun noise() {

        startRecord()
        handler.postDelayed({
            val gain = view.sp_volume.selectedItem.toString().toFloat()
            noiseOn(gain)
        },200)
        handler.postDelayed({
            noiseOff()
        }, 700)
    }

    private fun startRecord() {
        msg("측정을 시작합니다.")
        audioRecord.startRecord()
        handler.postDelayed({
            stopRecord()
        }, 3000)
    }
    fun stopRecord() {
        msg("잠시만 기다려 주세요...")
        audioRecord.stopRecord()
    }

    private fun noiseOn(gain: Float) {
        SendPacket_NoiseGenerator(selectedChannal(), PINK, gain,1 )
    }
    private fun noiseOff() {
        SendPacket_NoiseGenerator(selectedChannal(), PINK, -40f, 0)
    }

    fun SendPacket_NoiseGenerator(para2: Char, data0: Int, data1: Float, data5: Int) {
        if (selectedClient != null) {

            try {
                tx_buff = protocol.packet_NoiseGenerator(
                    para2, data0, data1, data5
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

    fun caculateRT60() {
        if(!Python.isStarted()){
            Python.start(AndroidPlatform(view.context))
        }
        getExternalStorageDirectory().absolutePath
            .toString() + "/" + MainActivity.sInstance.resources.getString(
            string.app_name) + "/"
        val py = Python.getInstance()
        val pyf = py.getModule("myscript")
        val wavPath = getExternalStorageDirectory().absolutePath + "/" + sInstance.resources.getString(string.app_name) + "/rt.wav"
        val graphPath = getExternalStorageDirectory().absolutePath + "/" + sInstance.resources.getString(string.app_name) + "/graph.png"
        val obj = pyf.callAttr("rt60", wavPath, graphPath)
        val arr = pyObjectToArray(obj.toString())
        view.iv_spectrogram.setImageResource(android.R.drawable.ic_delete)
        view.iv_spectrogram.setImageURI(Uri.parse(graphPath))
        view.iv_spectrogram.invalidate()
        drawLineChart(arr)
        val reverbTime_1khz = arr[3]
        view.tv_reverb.text = "RT60\n$reverbTime_1khz\n(sec)"
        prefSettings.setReverbTimePref(reverbTime_1khz.toString())
    }

    private fun drawLineChart(arr: FloatArray) {
        if(isRepeat){
            if(repeatCount == 0){
                arrList = ArrayList()
                valuesArrays = ArrayList()
                labelList = ArrayList()

            }
            repeatCount += 1
            arrList.add(arr)
            labelList.add("RT60($repeatCount)")
            chart.initGraphRepeat(arrList, labelList)

        }else{
            repeatCount = 0
            chart.initGraph(arr, "RT60(sec)", Color.BLUE)

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
        val afd = view.activity?.assets?.openFd("ir_clap.wav")
        val play = MediaPlayer()
        play.setDataSource(afd?.fileDescriptor, afd?.startOffset!!, afd.length)
        play.prepare()
        play.start()
    }
}