package com.gvkorea.gvktune.view.view.evaluation.presenter

import android.graphics.Color
import android.os.Handler
import android.widget.Toast
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CH1
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CH2
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CHA
import com.gvkorea.gvktune.MainActivity.Companion.prefSettings
import com.gvkorea.gvktune.MainActivity.Companion.selectedClient
import com.gvkorea.gvktune.util.Protocol
import com.gvkorea.gvktune.util.WaitingDialog
import com.gvkorea.gvktune.view.view.evaluation.EvaluateFragment
import com.gvkorea.gvktune.view.view.evaluation.EvaluateFragment.Companion.arrEvalList
import com.gvkorea.gvktune.view.view.evaluation.EvaluateFragment.Companion.averageTime
import com.gvkorea.gvktune.view.view.evaluation.EvaluateFragment.Companion.chart
import com.gvkorea.gvktune.view.view.evaluation.EvaluateFragment.Companion.isEvalRepeat
import com.gvkorea.gvktune.view.view.evaluation.EvaluateFragment.Companion.labelEvalList
import com.gvkorea.gvktune.view.view.evaluation.EvaluateFragment.Companion.repeatEvalCount
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.avgStart
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq1Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq2Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq3Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq4Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq5Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq6Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq7Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq8Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq9Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq10Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq11Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq12Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq13Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq14Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq15Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq16Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq17Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq18Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq19Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq20Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq21Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq22Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq23Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq24Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq25Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq26Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq27Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq28Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq29Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq30Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freq31Sum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.freqSum
import com.gvkorea.gvktune.view.view.evaluation.util.audio.RecordAudioRta.Companion.isMeasure
import com.opencsv.CSVWriter
import kotlinx.android.synthetic.main.fragment_eval.*
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EvalPresenter(val view: EvaluateFragment, val handler: Handler) {

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

    private var writer: CSVWriter? = null
    private var dataCount = 1


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

    fun noise(noise: Int) {
        if (noise != NOISE_OFF) {
            val para2 = selectedChannal()
            val gain = prefSettings.getNoiseVolumePref()
            SendPacket_NoiseGenerator(para2, noise, gain, 1)
        } else {
            val para2 = selectedChannal()
            val gain = prefSettings.getNoiseVolumePref()
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


    // For Test
    private fun changeRandomEQToFloatArray(ranEQ: IntArray): FloatArray {
        val ranEQFloatArray = FloatArray(ranEQ.size)
        for (i in ranEQ.indices) {
            ranEQFloatArray[i] = ranEQ[i] * 0.5f - 15
        }
        return ranEQFloatArray
    }


    fun average() {
        measure(true)
        WaitingDialog(view.context!!).create("평균 측정 중입니다..", averageTime.toLong())
        handler.postDelayed({
            measure(false)
        }, averageTime.toLong()+100)
        handler.postDelayed({
            CVS_Save()
            updateTableList()
            drawLineChart()

        }, averageTime.toLong()+500)

    }

    private fun drawLineChart() {
        if(isEvalRepeat){
            if(repeatEvalCount == 0){
                arrEvalList = ArrayList()
                labelEvalList = ArrayList()
            }
            repeatEvalCount += 1
            arrEvalList.add(freqSum)
            labelEvalList.add("$repeatEvalCount")
            chart.initGraphRepeat(arrEvalList, labelEvalList)

        }else{
            repeatEvalCount = 0
            chart.initGraph(freqSum, "Avg.", Color.BLUE)

        }
    }

    private fun CVS_Save() {
        // 파일 생성
        if (writer == null) {
            val baseDir = android.os.Environment.getExternalStorageDirectory().absolutePath
            val date = LocalDateTime.now()
            val time = LocalDateTime.now()
            val formatter_date = DateTimeFormatter.ofPattern("yyMMdd")
            val formatter_time = DateTimeFormatter.ofPattern("HHmmss")
            val formatted_date = date.format(formatter_date)
            val formatted_time = date.format(formatter_time)


            val filename = "${formatted_date}_${formatted_time}_$dataCount.csv"
            val filePath = baseDir + File.separator + filename
            try {
                writer = CSVWriter(FileWriter(filePath, true))
            } catch (e: IOException) {
                e.printStackTrace()
            }


            CSV_recordForData()
        } else {
            // 파일 생성 하지 않고 기록
            CSV_recordForData()
        }

    }

    fun CSV_SaveForData() {
        try {
            writer?.close()
            writer = null
            msg("데이터 파일을 저장합니다.")
            dataCount++

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun CSV_recordForData() {
        val datafile = arrayOfNulls<String>(31)

        for (i in 0..30) {
            datafile[i] = freqSum[i]
        }
        if (writer != null) {
            writer!!.writeNext(datafile)
        }
    }



    private fun updateTableList() {
        if(freqSum.size > 0){
            var dB = "SPL\n"
            for( i in 0 until 31){
                dB += freqSum[i] + "\n"
            }
            view.tv_curAvg.text = dB
        } else{
            msg("데이터가 없습니다.")
        }
    }

    fun initTableList() {
        var freq = "Freq\n"
        val dB = "SPL\n"
        for( i in 0 until 31){
            freq += hzArrays[i] + "\n"
        }
        view.tv_curFreq.text = freq
        view.tv_curAvg.text = dB
    }


}