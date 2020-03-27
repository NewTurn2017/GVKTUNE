package com.gvkorea.gvktune.view.view.rt.presenter

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Handler
import android.widget.Toast
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CH1
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CH2
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CHA
import com.gvkorea.gvktune.MainActivity.Companion.selectedClient
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.util.Protocol
import com.gvkorea.gvktune.view.view.rt.ReverbFragment
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
    private val CHECKINTERVAL = 50L

    val CMD_PARA2_CH1 = '1'
    val CMD_PARA2_CH2 = '2'
    val CMD_PARA2_CHA = 'A'

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
    fun noise() {
        val gain = view.sp_volume.selectedItem.toString().toFloat()
        noiseOn(gain)
        handler.postDelayed({
            noiseOff()
        }, 100)
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

    fun clapPlay() {
        val afd = view.activity?.assets?.openFd("ir_clap.wav")
        val play = MediaPlayer()
        play.setDataSource(afd?.fileDescriptor, afd?.startOffset!!, afd.length)
        play.prepare()
        play.start()
    }
}