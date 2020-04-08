package com.gvkorea.gvktune.presenter

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gvkorea.gvktune.MainActivity
import com.gvkorea.gvktune.MainActivity.Companion.MSG_RSV
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CH1
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CH2
import com.gvkorea.gvktune.MainActivity.Companion.isSelected_CHA
import com.gvkorea.gvktune.MainActivity.Companion.otherClient
import com.gvkorea.gvktune.MainActivity.Companion.otherClientNo
import com.gvkorea.gvktune.MainActivity.Companion.pref
import com.gvkorea.gvktune.MainActivity.Companion.selectedClient
import com.gvkorea.gvktune.MainActivity.Companion.selectedSpkNo
import com.gvkorea.gvktune.MainActivity.Companion.sockets
import com.gvkorea.gvktune.MainActivity.Companion.spk1Client
import com.gvkorea.gvktune.MainActivity.Companion.spk2Client
import com.gvkorea.gvktune.MainActivity.Companion.spk3Client
import com.gvkorea.gvktune.MainActivity.Companion.spk4Client
import com.gvkorea.gvktune.R
import com.gvkorea.gvktune.util.Protocol
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.net.*
import java.text.SimpleDateFormat
import java.util.*

class MainPresenter(val view: MainActivity, val handler: Handler) {

    private lateinit var tx_buff: ByteArray
    private val protocol = Protocol()
    private val uPort = 5000


    fun getIPAddress(): String {
        val wifiMan = view.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
        val wifiInf = wifiMan.connectionInfo
        val ipAddress = wifiInf.ipAddress
        val ip = String.format(
            "%d.%d.%d.%d",
            ipAddress and 0xff,
            ipAddress shr 8 and 0xff,
            ipAddress shr 16 and 0xff,
            ipAddress shr 24 and 0xff
        )
        return ip
    }

    fun appendText(msg: Message) {
        view.tv_received.append("${++MainActivity.no}. " + msg.obj as String + "(${getTime()})\n")
        view.scrollView_info.post {
            view.scrollView_info.fullScroll(View.FOCUS_DOWN)
        }
        if (MainActivity.no % 500 == 0) {
            view.tv_received.text = ""
        }

    }

    @SuppressLint("SimpleDateFormat")
    fun getTime(): String {
        val date = SimpleDateFormat("hh:mm:ss")
        return date.format(Date(System.currentTimeMillis()))
    }

    fun appendTextQuit(msg: Message) {
        view.tv_received.append("${++MainActivity.no}. " + msg.obj as String + "(${getTime()})\n")
        view.scrollView_info.post {
            view.scrollView_info.fullScroll(View.FOCUS_DOWN)
        }
        if (MainActivity.no % 500 == 0) {
            view.tv_received.text = ""
        }

        view.btn_Connect.isEnabled = true
        view.btn_Connect.alpha = 1f
        view.btn_DisConnect.isEnabled = false

        clearSocketUI()
        resetSpeakerUI()
    }

    fun selectedCH1() {
        isSelected_CH1 = true
        isSelected_CH2 = false
        isSelected_CHA = false
    }

    fun selectedCH2() {
        isSelected_CH1 = false
        isSelected_CH2 = true
        isSelected_CHA = false
    }

    fun selectedCHA() {
        isSelected_CH1 = false
        isSelected_CH2 = false
        isSelected_CHA = true
    }

    private fun clearSocketUI() {
        view.btn_spk1.setImageResource(R.drawable.spk1_off)
        view.btn_spk2.setImageResource(R.drawable.spk2_off)
        view.btn_spk3.setImageResource(R.drawable.spk3_off)
        view.btn_spk4.setImageResource(R.drawable.spk4_off)
    }

    fun hexToAscii(infoArray: List<String>): String {

        if (infoArray.size > 23) {
            val hexStr = findDeviceName(infoArray)
            val output = StringBuilder("")
            var i = 0
            while (i < hexStr.length) {
                val str = hexStr.substring(i, i + 2)
                output.append(str.toInt(16).toChar())
                i += 2
            }
            return output.toString()
        }
        return "0"
    }

    fun findDeviceName(infoArray: List<String>): String {
        var hexStr = ""
        for (i in 23..infoArray.size) {
            if (infoArray[i] != "0x00") {
                hexStr += infoArray[i].substring(2, 4)
            } else {
                break
            }

        }
        return hexStr
    }

    fun ipInfoText(msg: Message) {
        view.tv_ip_info.text = (msg.obj as String)
    }

    fun arrangeSockets(msg: Message) {
        setupPrefInt("speaker${msg.arg1}", msg.arg1)
        when (msg.arg1) {

            1 -> {
                spk1Client = msg.obj as Socket

                if (spk1Client != null) {
                    view.btn_spk1.setImageResource(R.drawable.spk1)
                }
            }
            2 -> {
                spk2Client = msg.obj as Socket
                if (spk2Client != null) {
                    view.btn_spk2.setImageResource(R.drawable.spk2)
                }
            }
            3 -> {
                spk3Client = msg.obj as Socket
                if (spk3Client != null) {
                    view.btn_spk3.setImageResource(R.drawable.spk3)
                }
            }
            4 -> {
                spk4Client = msg.obj as Socket
                if (spk4Client != null) {
                    view.btn_spk4.setImageResource(R.drawable.spk4)
                }
            }


            else -> {
                otherClient.add(msg.obj as Socket)
                otherClientNo++
                val m = Message()
                m.what = MSG_RSV
                m.obj = "SPK ID가 지정되지 않은 DSP 발견 \n 갯수: $otherClientNo 개"
                handler.sendMessage(m)
                Toast.makeText(view, "ID가 없는 DSP 발견! SET ID 실행요청", Toast.LENGTH_SHORT).show()
            }

        }
    }


    fun connect() {
        SendPacket_Connect()

        handler.postDelayed({
            makeListSockets()
            selectFirstSpeaker()
        }, 700L)

    }

    fun setupPrefInt(key: String, value: Int) {
        val editor = pref.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    private fun selectFirstSpeaker() {
        if (sockets.size > 0) {
            for (i in sockets.indices) {
                if (sockets[i] != null) {
                    selectSpeaker(i + 1)
                    break
                }
            }
        }
    }

    private fun SendPacket_Connect() {

        val str = getIPAddress()
        val ipAddr = str.split('.')
        val commandID = protocol.CMD_SET
        val para1 = protocol.CMD_UDP_TCP_SERVER_IFNO
        val para2 = protocol.CMD_UDP_TCP_SERVER_IFNO_PARA2
        val data0 = ipAddr[0].toInt()
        val data1 = ipAddr[1].toInt()
        val data2 = ipAddr[2].toInt()
        val data3 = ipAddr[3].toInt()


        Thread(Runnable {
            tx_buff = protocol.packet_Connect(commandID, para1, para2, data0, data1, data2, data3)

            val dSocket: DatagramSocket?
            try {
                dSocket = DatagramSocket()
                val dPacket = DatagramPacket(
                    tx_buff,
                    tx_buff.size,
                    InetAddress.getByName("192.168.$data2.255"),
                    uPort
                )

                dSocket.send(dPacket)
                dSocket.close()
            } catch (e: SocketException) {
                e.printStackTrace()
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()

        Thread.currentThread()
        Thread.interrupted()
    }

    fun selectSpeaker(spkNo: Int) {
        resetSpeakerUI()
        when (spkNo) {
            1 -> {
                view.btn_spk1.setBackgroundColor(ContextCompat.getColor(view, R.color.black))
                selectedClient = spk1Client
                selectedSpkNo = 1
            }
            2 -> {
                view.btn_spk2.setBackgroundColor(ContextCompat.getColor(view, R.color.black))
                selectedClient = spk2Client
                selectedSpkNo = 2
            }
            3 -> {
                view.btn_spk3.setBackgroundColor(ContextCompat.getColor(view, R.color.black))
                selectedClient = spk3Client
                selectedSpkNo = 3
            }
            4 -> {
                view.btn_spk4.setBackgroundColor(ContextCompat.getColor(view, R.color.black))
                selectedClient = spk4Client
                selectedSpkNo = 4
            }

        }
    }

    fun resetSpeakerUI() {
        view.btn_spk1.setBackgroundColor(ContextCompat.getColor(view, R.color.white))
        view.btn_spk2.setBackgroundColor(ContextCompat.getColor(view, R.color.white))
        view.btn_spk3.setBackgroundColor(ContextCompat.getColor(view, R.color.white))
        view.btn_spk4.setBackgroundColor(ContextCompat.getColor(view, R.color.white))
    }

    fun makeListSockets() {
        sockets = ArrayList()

        sockets.add(spk1Client)
        sockets.add(spk2Client)
        sockets.add(spk3Client)
        sockets.add(spk4Client)

    }
}