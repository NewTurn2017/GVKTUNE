package com.gvkorea.gvktune.view.view.data.presenter

import android.content.SharedPreferences
import android.os.Handler
import android.widget.Toast
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CH1
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CH2
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CHA
import com.gvkorea.gvktune.MainActivity.Companion.prefSettings
import com.gvkorea.gvktune.MainActivity.Companion.selectedClient
import com.gvkorea.gvktune.util.Protocol
import com.gvkorea.gvktune.util.TimeUtils
import com.gvkorea.gvktune.view.view.data.DataFragment
import com.gvkorea.gvktune.view.view.data.DataFragment.Companion.noiseVolume
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.avgStart
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq1Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq2Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq3Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq4Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq5Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq6Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq7Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq8Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq9Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq10Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq11Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq12Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq13Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq14Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq15Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq16Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq17Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq18Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq19Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq20Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq21Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq22Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq23Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq24Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq25Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq26Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq27Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq28Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq29Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq30Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freq31Sum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.freqSum
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.isMeasure
import com.gvkorea.gvktune.view.view.data.util.audio.RecordAudioRTA_31.Companion.spldB31
import com.opencsv.CSVWriter
import kotlinx.android.synthetic.main.fragment_data.*
import java.io.*
import java.text.DecimalFormat
import kotlin.collections.ArrayList
import kotlin.random.Random

class DataPresenter(val view: DataFragment, val handler: Handler, var tx_buff: ByteArray, val pref: SharedPreferences) {

    val timeUtils = TimeUtils()

    val NOISE_OFF = 3
    val SWEEP = 0
    val PINK = 2
    val WHITE = 1
    var protocol: Protocol = Protocol()

    private lateinit var outputStream: OutputStream
    private lateinit var dataOutputStream: DataOutputStream
    private lateinit var inputStream: InputStream
    private lateinit var dataInputStream: DataInputStream

    private val CMD_NOISE = 8
    private val CHNNEL_INPUT = 0
    private val CHNNEL_OUTPUT = 2
    private val NFILT_0 = 0
    private val NFILT_1 = 1
    private val NFILT_2 = 2
    private val NFILT_3 = 3
    private val NFILT_4 = 4
    private val CMD_EQ = 5
    private val EQINTERVAL = 50L
    private val CHECKINTERVAL = 50L
    val avgArray = Array(100) { DoubleArray(10) }
    val rmsAfterAvg = DoubleArray(10)
    private val ranEQ = IntArray(31)

    val CMD_PARA2_CH1 = '1'
    val CMD_PARA2_CH2 = '2'
    val CMD_PARA2_CHA = 'A'


    private var writer: CSVWriter? = null
    private var dataCount = 1

    fun mesureAvg(isStart: Boolean) {
        if (isStart) {
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
        } else {
            avgStart = true
            isMeasure = false
        }
    }



    fun eqReset() {

        SendPacket_InputGEQ_Reset(selectedChannal())

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

    private fun msg(msg: String) {
        Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
    }


    fun gainDataStart() {
        view.btn_start.text = "진행중"
        view.btn_start.isEnabled = false

        eqReset()
        handler.postDelayed({
            noiseVolume = -40
            NoiseVolumeControl(noiseVolume)
        }, 200)
    }

    fun noise(noise: Int, gain: Int) {
        if (noise != NOISE_OFF) {
            val para2 = selectedChannal()
            SendPacket_NoiseGenerator(para2, noise, gain.toFloat(), 1)
        } else {
            val para2 = selectedChannal()
            SendPacket_NoiseGenerator(para2, PINK, gain.toFloat(), 0)
        }
    }



    fun NoiseVolumeControl(progress: Int) {
        noise(PINK, progress)
        handler.postDelayed({
            val targetdB = view.sp_dataSpkVolume.selectedItem.toString().toInt()
            if (spldB31.toInt() < targetdB) {
                noiseVolume++
                NoiseVolumeControl(noiseVolume)
            } else {
                handler.postDelayed({
                    // 데이터 수집 시작
                    noise(NOISE_OFF, progress)
                    dataMiningStart()
                }, 100)
            }
        }, 500)
    }

    //수동설정
    //avgTime -> 16k = 1500L  8k = 1100L  4k = 1100L
    val avgTime = 1100L
    val noOfdataGathering = 3000

    private fun dataMiningStart() {
        for (i in ranEQ.indices) {
            if(i < 6){
                ranEQ[i] = (25..35).random()
            }else{
                ranEQ[i] = randomRange()
            }

        }
//        SendPacket_InputGEQ_ALL_Manual(ranEQ)
        SendPacket_EQ_All(selectedChannal(), ranEQ)
        handler.postDelayed({
            averageRMS_For_Data()
        }, 100)
        handler.postDelayed({
            CSV_Save()
        }, 300 + avgTime + 500)  // avg time + 500
        handler.postDelayed({
            repeatDataGathering()
        }, 300 + avgTime + 700)

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

    private fun repeatDataGathering() {
        if (dataCount > noOfdataGathering) {
            CSV_Save()
            view.btn_start.text = "수집 시작"
            view.btn_start.isEnabled = true
            view.btn_start.alpha = 1f
        } else {
            dataMiningStart()
        }
    }

    private fun CSV_Save() {
        // 파일 생성
        if (writer == null) {
            val baseDir = android.os.Environment.getExternalStorageDirectory().absolutePath
            val filename = "data_" + dataCount + "_" + timeUtils.getDate() + "_reverb(${prefSettings.getReverbTimePref()})" +"_${view.sp_dataSpkModel.selectedItem}" + "_${view.sp_dataSpkVolume.selectedItem}" + ".csv"
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
        if (dataCount > 1) {
            if (dataCount % 10 == 0) {
                CSV_SaveForData()
            }
        }
        dataCount++
    }

    private fun CSV_recordForData() {
//        val CurRMS = arrayOfNulls<String>(10)
        val CurEQVal = arrayOfNulls<String>(31)
        val datafile = arrayOfNulls<String>(63)

        for (i in 0..30) {
            CurEQVal[i] = ranEQ[i].toString()
        }

        for (i in 0..62) {
            when {
                i < 1 -> datafile[i] = dataCount.toString()
                i < 32 -> datafile[i] = CurEQVal[i - 1] //현재 EQ값
                i < 63 -> datafile[i] = freqSum[i - 32] // 측정값 dB
            }
        }

        if (writer != null) {
            writer!!.writeNext(datafile)
            msg(dataCount.toString() + "번째 데이터 저장되었습니다.")
        }
    }

    private fun CSV_SaveForData() {
        try {
            writer?.close()
            writer = null
            msg("데이터 중간 저장합니다.")

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun doubleToString(value: Double): String {
        val format = DecimalFormat("##.#")
        return format.format(value)
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


    private fun randomRange(): Int {

        return (Math.random() * 61).toInt()// 0~60
        // 25~48 : (int) (Math.random() * 25) + 24;
    }

    private fun randomRange(diffValue: Int): Int {
        val max = 30 + diffValue
        val min = 30 - diffValue
        return (Math.random() * (max - min + 1)).toInt() + min
    }

    fun averageRMS_For_Data() {
        noise(PINK, noiseVolume)
        mesureAvg(true)
        handler.postDelayed({
            mesureAvg(false)
            noise(NOISE_OFF, noiseVolume)
        }, avgTime)

    }

    fun gainDataStop() {
        dataCount = 1
        noise(NOISE_OFF, noiseVolume)
        handler.removeMessages(0)
        view.btn_start.text = "수집 시작"
        view.btn_start.isEnabled = true
        view.btn_start.alpha = 1f
        if (writer != null) {

            try {
                writer?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            writer = null
            msg("데이터를 저장하고 종료합니다...")
        }
    }






}