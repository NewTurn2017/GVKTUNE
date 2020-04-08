package com.gvkorea.gvktune

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gvkorea.gvktune.listener.ChannelGroupCheckedChangeListener
import com.gvkorea.gvktune.listener.SpeakerSelectedListener
import com.gvkorea.gvktune.presenter.MainPresenter
import com.gvkorea.gvktune.util.PrefSettings
import com.gvkorea.gvktune.util.replace
import com.gvkorea.gvktune.view.MainFragment
import com.gvkorea.gvktune.view.view.calibration.presenter.CalibPresenter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.DataInputStream
import java.io.IOException
import java.lang.Exception
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class MainActivity : AppCompatActivity() {

    private var permission_list = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION
    )

    var mFlag = false
    private var mHandlerBackPress: Handler? = Handler {
        if (it.what == 0) {
            mFlag = false
        }
        return@Handler true
    }

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_RSV -> {
                    presenter.appendText(msg)
                }
                MSG_INFO -> {
                    presenter.ipInfoText(msg)
                }
                MSG_QUIT -> {
                    presenter.appendTextQuit(msg)
                }
                MSG_SOCK -> {
                    presenter.arrangeSockets(msg)
                }
            }
            super.handleMessage(msg)
        }
    }

    lateinit var mSleepLock: PowerManager.WakeLock
    private lateinit var wifiManager: WifiManager
    private lateinit var connection: WifiInfo
    lateinit var presenter: MainPresenter

    private val SERVERPORT = 5001
    private val threadList = ArrayList<ClientThread>()
    private val lock: ReentrantLock = ReentrantLock()
    private lateinit var thread: ServerThread
    private var loop: Boolean = false
    private var isResigterWifi = false

    private val mainFragment: MainFragment by lazy { MainFragment(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        sleepLock()
        sInstance = this

        val PREF_SETUP_KEY = "Settings"
        pref = applicationContext.getSharedPreferences(PREF_SETUP_KEY, Context.MODE_PRIVATE)
        presenter = MainPresenter(this, mHandler)
        nowFragment = mainFragment
        replace(R.id.fragment_container, mainFragment as Fragment)

        initListener()
        preference = getSharedPreferences("pref_calib", Context.MODE_PRIVATE)
        prefSettings = PrefSettings()
        val calibPreference = CalibPresenter()
        calibPreference.loadCalibrate()
        selectedMicName = preference.getString("selectedMic", "iSEMic725TR-3511903-freefield.csv")
    }

    private fun initListener() {
        btn_Connect.isEnabled = true
        btn_DisConnect.isEnabled = false
        btn_Connect.setOnClickListener {
            otherClientNo = 0
            connectServer()
        }
        btn_DisConnect.setOnClickListener {
            otherClientNo = 0
            disconnect()
        }

        presenter.resetSpeakerUI()

        btn_spk1.setOnClickListener(
            SpeakerSelectedListener(
                presenter
            )
        )
        btn_spk2.setOnClickListener(
            SpeakerSelectedListener(
                presenter
            )
        )
        btn_spk3.setOnClickListener(
            SpeakerSelectedListener(
                presenter
            )
        )
        btn_spk4.setOnClickListener(
            SpeakerSelectedListener(
                presenter
            )
        )

        btn_main.setOnClickListener {
            nowFragment = mainFragment
            replace(R.id.fragment_container, mainFragment as Fragment)
        }

        rg_channel.setOnCheckedChangeListener(ChannelGroupCheckedChangeListener(presenter))
    }

    private fun connectServer() {
        try {
            thread = ServerThread(SERVERPORT)
            thread.start()
            val m = Message()
            m.what = MSG_RSV
            m.obj = "서버가 시작되었습니다."
            mHandler.sendMessage(m)

            btn_Connect.isEnabled = false
            btn_DisConnect.isEnabled = true
            btn_DisConnect.alpha = 1f
            presenter.connect()
        } catch (e: IOException) {
            val m = Message()
            m.what = MSG_RSV
            m.obj = "Server Thread를 시작하지 못했습니다.$e"
            mHandler.sendMessage(m)
        }
    }

    private fun disconnect() {

        val t = Thread(Runnable {
            if (loop) {
                thread.shutdown()
                thread.server.close()
                thread.join()

            }
            val m = Message()
            m.what = MSG_QUIT
            m.obj = "서버를 종료합니다."
            mHandler.sendMessage(m)
            clearSocket()
        })
        t.start()
        try {
            t.join()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clearSocket() {
        spk1Client = null
        spk2Client = null
        spk3Client = null
        spk4Client = null
        otherClient = ArrayList()
    }

    val wifiStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val wifiStateExtra =
                intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)

            when (wifiStateExtra) {
                WifiManager.WIFI_STATE_ENABLED -> {

                    wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

                    connection = wifiManager.connectionInfo

                    val hostaddr = presenter.getIPAddress()
                    val wifiInfo = "WIFI 연결됨\n SSID: ${connection.ssid}\n IP: $hostaddr"
                    tv_ip_info.text = wifiInfo
                    if(isResigterWifi){
                        wifi_refresh()
                    }
                    onStart()


                }
                WifiManager.WIFI_STATE_DISABLED -> {
                    tv_ip_info.text = "WIFI 연결안됨"
                    disconnect()
                }
            }
        }

    }

    fun wifi_refresh() {
        unregisterReceiver(wifiStateReceiver)
        val intentFilter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
        registerReceiver(wifiStateReceiver, intentFilter)

    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
        registerReceiver(wifiStateReceiver, intentFilter)
        isResigterWifi = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (true == loop) {
            val t = Thread(Runnable {
                thread.shutdown()
                thread.join()
            })
            t.start()
            t.join()
        }

        unregisterReceiver(wifiStateReceiver)

    }




    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        when (nowFragment) {
            mainFragment -> {
                supportFragmentManager.beginTransaction().detach(mainFragment).attach(mainFragment)
                        .commit()
            }


        }
    }

    @SuppressLint("InvalidWakeLockTag")
    private fun sleepLock() {
        val power = getSystemService(Context.POWER_SERVICE) as PowerManager
        mSleepLock = power.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NonSleepActivity")
        mSleepLock.acquire(10 * 60 * 1000L /*10 minutes*/)
    }

    override fun onBackPressed() {
        if (!mFlag) {
            Toast.makeText(
                applicationContext,
                "\'Back\' 버튼을 한번 더 누르시면 종료됩니다.",
                Toast.LENGTH_SHORT
            )
                .show()
            mFlag = true
            mHandlerBackPress?.sendEmptyMessageDelayed(0, (1000 * 2).toLong())
        } else {
            super.onBackPressed()
        }
    }

    private fun checkPermission() {

        for (permission: String in permission_list) {

            val chk = checkCallingOrSelfPermission(permission)

            if (chk == PackageManager.PERMISSION_DENIED) {
                requestPermissions(permission_list, 0)
                break
            }
        }

    }

    inner class ServerThread @Throws(IOException::class)
    constructor(port: Int) : Thread() {
        val server: ServerSocket = ServerSocket(port)
        private val pool: ExecutorService
        private val poolSize = 8

        init {
            pool = Executors.newFixedThreadPool(poolSize)
            loop = true
        }

        override fun run() {
            while (loop) {
                try {
                    val thread = ClientThread(server.accept())
                    lock.lock()
                    threadList.add(thread)
                    lock.unlock()
                    pool.execute(thread)
                } catch (e: InterruptedException) {
//                    e.printStackTrace()
                } catch (e: IOException) {
                    val m = mHandler.obtainMessage()
                    m.what = MSG_QUIT
                    m.obj = "서버를 중지합니다."
                    mHandler.sendMessage(m)
                    pool.shutdown()
                    break
                }
            }

            try {
                server.close()
            } catch (e: Exception) {
                Log.e(TAG, "ServerThread 예외가 발생하였습니다.$e")
            }
        }

        fun shutdown() {
            pool.shutdown()
            try {
                if (!pool.awaitTermination(100L, TimeUnit.MILLISECONDS)) {
                    pool.shutdownNow()
                }
            } catch (ie: InterruptedException) {
                pool.shutdownNow()
            }

            clearList()
            loop = false

        }

        private fun clearList() {
            if (threadList.isNotEmpty()) {
                lock.lock()
                for (index in threadList) {
                    index.quit()
                }
                lock.unlock()
            }
        }
    }

    inner class ClientThread(private val sock: Socket) : Thread() {

        private val inetaddr: InetAddress = sock.inetAddress

        var isStarted = false
        var din: DataInputStream? = null
        var receivedData = ""
        var rxByteList = ArrayList<Byte>()
        var curSpkNo = ""

        init {

            val m = Message()
            m.what = MSG_RSV
            m.obj = inetaddr.hostAddress + "로부터 접속하였습니다."
            mHandler.sendMessage(m)
        }

        override fun run() {
            try {
                isStarted = true
                din = DataInputStream(sock.getInputStream())
                while (isStarted) {
                    val lengthByte = ByteArray(1)
                    rxByteList = ArrayList()
                    do {
                        din!!.read(lengthByte, 0, 1)
                        rxByteList.add(lengthByte[0])
                        val x  = Integer.parseInt(String.format("%02x", rxByteList[0]), 16)
                    } while (rxByteList.size - 1 != x)

                    for (i in rxByteList.indices) {
                        if (i < rxByteList.size - 1) {
                            receivedData += String.format("0x%02x ", rxByteList[i])

                        } else {
                            receivedData += String.format("0x%02x", rxByteList[i])
                        }
                    }
                    val msg = receivedData
                    receivedData = ""
                    if (msg.isNotEmpty() && !msg.startsWith("0x00")) {

                        if (msg.contains("0x52 0x43 0x64 0x00")) {
                            val infoArray = msg.split(" ")
                            val spkNo = presenter.hexToAscii(infoArray)
                            if(spkNo.length > 1){
                                curSpkNo = "0"
                            }else{
                                curSpkNo = spkNo
                            }

                        }
                        val m = Message()
                        m.what = MSG_RSV
                        m.obj = "$msg(ID: $curSpkNo)"
                        mHandler.sendMessage(m)
                    }


                    if (msg.contains("0x52 0x43 0x64 0x00")) {
                        val infoArray = msg.split(" ")
                        val spkNo = presenter.hexToAscii(infoArray)
                        if(spkNo.length > 1){
                            val m = Message()
                            m.what = MSG_SOCK
                            m.arg1 = 0
                            m.obj = sock
                            mHandler.sendMessage(m)
                        }else{
                            val m = Message()
                            m.what = MSG_SOCK
                            m.arg1 = spkNo.toInt()
                            m.obj = sock
                            mHandler.sendMessage(m)
                        }
                    }
                }
            } catch (e: InterruptedException) {

            } catch (e: Exception) {
                Log.e(TAG, "ClientThread 예외가 발생하였습니다.$e")
            } finally {
                val m3 = Message()
                m3.what = MSG_RSV
                m3.obj = inetaddr.hostAddress + "와의 접속이 종료되었습니다."
                mHandler.sendMessage(m3)

                try {
                    lock.lock()
                    threadList.remove(this)
                    lock.unlock()
                    sock.close()
                } catch (e: Exception) {
                    Log.e(TAG, "ClientThread 예외가 발생하였습니다.$e")
                }
            }
        }

        fun quit() {
            try {
                sock.close()
            } catch (e: IOException) {
                Log.e(TAG, "ClientThread 예외가 발생하였습니다.$e")
            }
        }

        private fun hexToString(presetNameHex: String): String {
            val output = StringBuilder("")
            var i = 0
            while (i < presetNameHex.length) {
                val str: String = presetNameHex.substring(i, i + 2)
                output.append(str.toInt(16).toChar())
                i += 2
            }
            return output.toString()

        }
    }

    companion object {
        var isSelected_CH1 = false
        var isSelected_CH2 = false
        var isSelected_CHA = false
        var no: Int = 0
        val MSG_RSV = 1
        val MSG_INFO = 4
        val MSG_QUIT = 2
        val MSG_SOCK = 3
        var selectedClient: Socket? = null
        var spk1Client: Socket? = null
        var spk2Client: Socket? = null
        var spk3Client: Socket? = null
        var spk4Client: Socket? = null

        var otherClient: ArrayList<Socket?> = ArrayList()
        lateinit var pref: SharedPreferences
        var selectedSpkNo = 0
        var sockets = ArrayList<Socket?>()
        lateinit var nowFragment: Fragment
        var otherClientNo = 0
        val TAG = "MultiSocketServer"
        var selectedMicName: String? = null
        lateinit var preference: SharedPreferences
        var CALIBRATION = 0F
        var isCalib = false

        lateinit var sInstance: MainActivity

        lateinit var prefSettings: PrefSettings
    }


}
