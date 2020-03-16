package com.gvkorea.gvktune

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.*
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import com.gvkorea.gvktune.analyzer.*
import com.gvkorea.gvktune.listener.ChannelGroupCheckedChangeListener
import com.gvkorea.gvktune.listener.SpeakerSelectedListener
import com.gvkorea.gvktune.presenter.MainPresenter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.DataInputStream
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class AnalyzerActivity : AppCompatActivity(), View.OnLongClickListener, View.OnClickListener,
    AdapterView.OnItemClickListener, AnalyzerGraphic.Ready {

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

    val handler = Handler()


    //Analyzer



    lateinit var graphInit: Thread
    private var bSamplingPreparation = false
    lateinit var analyzerViews: AnalyzerViews
    var samplingThread: SamplingLoop? = null
    lateinit var rangeViewDialogC: RangeViewDialogC
    lateinit var mDetector: GestureDetectorCompat

    lateinit var analyzerParam: AnalyzerParameters

    var rmsValues: ArrayList<Double>? = null
    var dtRMS = 0.0
    var dtRMSFromFT = 0.0
    var maxAmpDB = 0.0
    var maxAmpFreq = 0.0
    var viewRangeArray: DoubleArray? = null

    private var isLockViewRange = false

    var bSaveWav = false


    var calibLoad: CalibrationLoad = CalibrationLoad() // data for calibration of spectrum

    override fun onCreate(savedInstanceState: Bundle?) {
        //  Debug.startMethodTracing("calc");
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        Log.i(TAG, " max runtime mem = " + maxMemory + "k");
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        checkPermission()
        sleepLock()
        val PREF_SETUP_KEY = "Settings"
        pref = applicationContext.getSharedPreferences(PREF_SETUP_KEY, Context.MODE_PRIVATE)
        presenter = MainPresenter(this, mHandler, pref)

        initListener()

        val res = resources

        analyzerParam = AnalyzerParameters(res)

        // Initialized preferences by default values

        // Initialized preferences by default values
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        // Read preferences and set corresponding variables
        // Read preferences and set corresponding variables
        loadPreferenceForView()

        analyzerViews = AnalyzerViews(this)

        // travel Views, and attach ClickListener to the views that contain android:tag="select"

        // travel Views, and attach ClickListener to the views that contain android:tag="select"
        visit(analyzerViews.graphView.getRootView() as ViewGroup, object : Visit {

            override fun exec(view: View) {
                view.setOnLongClickListener(this@AnalyzerActivity)
                view.setOnClickListener(this@AnalyzerActivity)
                (view as TextView).freezesText = true
            }
        }, "select")

        rangeViewDialogC = RangeViewDialogC(this, analyzerViews.graphView)

        mDetector = GestureDetectorCompat(this, AnalyzerGestureListener(analyzerViews))

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
        rg_channel.setOnCheckedChangeListener(ChannelGroupCheckedChangeListener(presenter))


        btn_measure.setOnClickListener { showTable() }



        sp_smooth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val value =
                    java.lang.String.valueOf(parent.getItemIdAtPosition(position))
                if (value == "0") {
                    analyzerViews.smooth = 0
                } else if (value == "1") {
                    analyzerViews.smooth = 5
                } else if (value == "2") {
                    analyzerViews.smooth = 10
                } else if (value == "3") {
                    analyzerViews.smooth = 50
                } else if (value == "4") {
                    analyzerViews.smooth = 100
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        sp_smooth.setSelection(4)

    }

    private fun showTable() {
//        tv_tableValues = ""
//        updateTableDoubleArray(analyzerViews.graphView.spectrumPlot.peakHold.v_peak)
//        analyzerViews.graphView.spectrumPlot.peakHold.drop_speed = 500.0
//        handler.postDelayed(Runnable {
//            analyzerViews.graphView.spectrumPlot.peakHold.drop_speed = 0.0
//        }, 2000)
        analyzerViews.measure(true);
        Toast.makeText(applicationContext, "그래프 평균 중...", Toast.LENGTH_SHORT).show();
        handler.postDelayed({
            analyzerViews.measure(false);
            Toast.makeText(
                getApplicationContext(),
                "총 데이터 ..." + analyzerViews.rmsSum.size + "개",
                Toast.LENGTH_SHORT
            ).show();

        }, 1000)
        handler.postDelayed({
            tv_tableValues = ""
            updateTable(analyzerViews.movingAvg);
        }, 1500)
    }

    var tv_tableValues = ""

    private fun updateTableDoubleArray(vPeak: DoubleArray) {
//        int[] table30Array = new int[]{3, 4, 5, 7, 9, 11, 15, 19, 24, 30, 39, 49, 63, 80, 102, 129
//                , 165, 209, 266, 338, 430, 546, 694, 882, 1121, 1425, 1810, 2300, 2923, 3715};
        for (i in 1 until vPeak.size) {
            val str =
                (Math.round(i * 5.383301 * 100.0) / 100.0).toString() + " hz: " + Math.round(
                    vPeak[i] * 100.0
                ) / 100.0 + " dB\n"
            tv_tableValues += str
        }
        tv_table.text = tv_tableValues
    }

    private fun updateTable(movingAvg: java.util.ArrayList<Double>) {
//        int[] table30Array = new int[]{3, 4, 5, 7, 9, 11, 15, 19, 24, 30, 39, 49, 63, 80, 102, 129
//                , 165, 209, 266, 338, 430, 546, 694, 882, 1121, 1425, 1810, 2300, 2923, 3715};
        for (i in 1 until movingAvg.size) {
            val str =
                (Math.round(i * 5.383301 * 100.0) / 100.0).toString() + " hz: " + Math.round(
                    movingAvg[i] * 100.0
                ) / 100.0 + " dB\n"
            tv_tableValues += str
        }
        tv_table.text = tv_tableValues
    }

    override fun onResume() {
        super.onResume()
        LoadPreferences();
        analyzerViews?.graphView?.setReady(this);  // TODO: move this earlier?
        analyzerViews?.enableSaveWavView(bSaveWav);

        // Used to prevent extra calling to restartSampling() (e.g. in LoadPreferences())
        bSamplingPreparation = true;

        // Start sampling
        restartSampling(analyzerParam);
    }


    override fun onPause() {
        Log.d(TAG, "onPause()")
        bSamplingPreparation = false
        if (samplingThread != null) {
            samplingThread!!.finish()
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onPause()
    }


    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        Log.d(TAG, "onSaveInstanceState()")
        savedInstanceState.putDouble("dtRMS", dtRMS)
        savedInstanceState.putDouble("dtRMSFromFT", dtRMSFromFT)
        savedInstanceState.putDouble("maxAmpDB", maxAmpDB)
        savedInstanceState.putDouble("maxAmpFreq", maxAmpFreq)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d(TAG, "onRestoreInstanceState()")
        // will be called after the onStart()
        super.onRestoreInstanceState(savedInstanceState)
        dtRMS = savedInstanceState.getDouble("dtRMS")
        dtRMSFromFT = savedInstanceState.getDouble("dtRMSFromFT")
        maxAmpDB = savedInstanceState.getDouble("maxAmpDB")
        maxAmpFreq = savedInstanceState.getDouble("maxAmpFreq")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.info, menu)
        return true
    }

    val REQUEST_AUDIO_GET = 1
    val REQUEST_CALIB_LOAD = 2



    private fun restartSampling(_analyzerParam: AnalyzerParameters) {
        // Stop previous sampler if any.
        if (samplingThread != null) {
            samplingThread!!.finish()
            try {
                samplingThread!!.join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            samplingThread = null
        }
        if (viewRangeArray != null) {
            analyzerViews.graphView.setupAxes(analyzerParam)
            val rangeDefault = analyzerViews.graphView.viewPhysicalRange
            Log.i(
                TAG,
                "restartSampling(): setViewRange: " + viewRangeArray!![0]
                    .toString() + " ~ " + viewRangeArray!![1]
            )
            analyzerViews.graphView.setViewRange(viewRangeArray, rangeDefault)
            if (!isLockViewRange) viewRangeArray = null // do not conserve
        }

        // Set the view for incoming data
        graphInit = Thread(Runnable { analyzerViews.setupView(_analyzerParam) })
        graphInit!!.start()

        // Check and request permissions
        if (!bSamplingPreparation) return

        // Start sampling
        samplingThread = SamplingLoop(this, _analyzerParam)
        samplingThread!!.start()
    }

    // For call requestPermissions() after each showPermissionExplanation()
    private val count_permission_explanation = 0

    // For preventing infinity loop: onResume() -> requestPermissions() -> onRequestPermissionsResult() -> onResume()
    private val count_permission_request = 0

    fun processClick(v: View?): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val value: String
        value = if (v is SelectorText) {
            v.value
        } else {
            (v as TextView).text.toString()
        }
        return when (v.id) {
            R.id.button_recording -> {
                bSaveWav = value == "Rec"
                //  SelectorText st = (SelectorText) findViewById(R.id.run);
                //  if (bSaveWav && ! st.getText().toString().equals("stop")) {
                //    st.nextValue();
                //    if (samplingThread != null) {
                //      samplingThread.setPause(true);
                //    }
                //  }
                analyzerViews.enableSaveWavView(bSaveWav)
                true
            }
            R.id.run -> {
                val pause = value == "stop"
                if (samplingThread != null && samplingThread!!.getPause() != pause) {
                    samplingThread!!.setPause(pause)
                }
                analyzerViews.graphView.spectrogramPlot.setPause(pause)
                false
            }
            R.id.freq_scaling_mode -> {
                Log.d(
                    TAG,
                    "processClick(): freq_scaling_mode = $value"
                )
                analyzerViews.graphView.setAxisModeLinear(value)
                editor.putString("freq_scaling_mode", value)
                editor.commit()
                false
            }
            R.id.dbA -> {
                analyzerParam.isAWeighting = value != "dB"
                if (samplingThread != null) {
                    samplingThread!!.setAWeighting(analyzerParam.isAWeighting)
                }
                editor.putBoolean("dbA", analyzerParam.isAWeighting)
                editor.commit()
                false
            }
            R.id.spectrum_spectrogram_mode -> {
                if (value == "spum") {
                    analyzerViews.graphView.switch2Spectrum()
                } else {
                    analyzerViews.graphView.switch2Spectrogram()
                }
                editor.putBoolean("spectrum_spectrogram_mode", value == "spum")
                editor.commit()
                false
            }
            else -> true
        }
    }

    private fun vibrate(ms: Int) {
        //((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(ms);
    }

    /**
     * Visit all subviews of this view group and run command
     *
     * @param group  The parent view group
     * @param cmd    The command to run for each view
     * @param select The tag value that must match. Null implies all views
     */
    private fun visit(group: ViewGroup, cmd: Visit, select: String) {
        exec(group, cmd, select)
        for (i in 0 until group.childCount) {
            val c = group.getChildAt(i)
            if (c is ViewGroup) {
                visit(c, cmd, select)
            } else {
                exec(c, cmd, select)
            }
        }
    }

    private fun exec(v: View, cmd: Visit, select: String?) {
        if (select == null || select == v.tag) {
            cmd.exec(v)
        }
    }

    /**
     * Interface for view hierarchy visitor
     */
    internal interface Visit {
        fun exec(view: View)
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
                    if (isResigterWifi) {
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
                        val x = Integer.parseInt(String.format("%02x", rxByteList[0]), 16)
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
                            if (spkNo.length > 1) {
                                curSpkNo = "0"
                            } else {
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
                        if (spkNo.length > 1) {
                            val m = Message()
                            m.what = MSG_SOCK
                            m.arg1 = 0
                            m.obj = sock
                            mHandler.sendMessage(m)
                        } else {
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




    // Load preferences for Views
    // When this function is called, the SamplingLoop must not running in the meanwhile.
    @SuppressLint("SetTextI18n")
    private fun loadPreferenceForView() {
        // load preferences for buttons
        // list-buttons
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        analyzerParam.sampleRate = sharedPref.getInt("button_sample_rate", 8000)
        analyzerParam.fftLen = sharedPref.getInt("button_fftlen", 8192)
        analyzerParam.nFFTAverage = sharedPref.getInt("button_average", 1)
        // toggle-buttons
        analyzerParam.isAWeighting = sharedPref.getBoolean("dbA", false)
        if (analyzerParam.isAWeighting) {
            (R.id.dbA as SelectorText).nextValue()
        }
        val isSpam = sharedPref.getBoolean("spectrum_spectrogram_mode", true)
        if (!isSpam) {
            spectrum_spectrogram_mode.nextValue()
        }
        val axisMode = sharedPref.getString("freq_scaling_mode", "linear")
        freq_scaling_mode.value = axisMode
        Log.i(
            TAG, """loadPreferenceForView():
  sampleRate  = ${analyzerParam.sampleRate}
  fftLen      = ${analyzerParam.fftLen}
  nFFTAverage = ${analyzerParam.nFFTAverage}"""
        )
        button_sample_rate.setText(
            Integer.toString(
                analyzerParam.sampleRate
            )
        )
        button_fftlen.setText(
            Integer.toString(
                analyzerParam.fftLen
            )
        )
        button_average.setText(
            Integer.toString(
                analyzerParam.nFFTAverage
            )
        )
    }

    private fun LoadPreferences() {
        // Load preferences for recorder and views, beside loadPreferenceForView()
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val keepScreenOn = sharedPref.getBoolean("keepScreenOn", true)
        if (keepScreenOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        analyzerParam.audioSourceId = sharedPref.getString(
            "audioSource",
            Integer.toString(analyzerParam.RECORDER_AGC_OFF)
        ).toInt()
        analyzerParam.wndFuncName = sharedPref.getString("windowFunction", "Hanning")
        analyzerParam.spectrogramDuration = sharedPref.getString(
            "spectrogramDuration",
            java.lang.Double.toString(6.0)
        ).toDouble()
        analyzerParam.overlapPercent =
            sharedPref.getString("fft_overlap_percent", "50.0").toDouble()
        analyzerParam.hopLen =
            (analyzerParam.fftLen * (1 - analyzerParam.overlapPercent / 100) + 0.5).toInt()

        // Settings of graph view
        // spectrum
        analyzerViews.graphView.setShowLines(sharedPref.getBoolean("showLines", false))
        // set spectrum show range
        analyzerViews.graphView.setSpectrumDBLowerBound(
            sharedPref.getString(
                "spectrumRange",
                java.lang.Double.toString(AnalyzerGraphic.minDB)
            ).toFloat().toDouble()
        )

        // spectrogram
        analyzerViews.graphView.setSpectrogramModeShifting(
            sharedPref.getBoolean(
                "spectrogramShifting",
                false
            )
        )
        analyzerViews.graphView.setShowTimeAxis(sharedPref.getBoolean("spectrogramTimeAxis", true))
        analyzerViews.graphView.setShowFreqAlongX(
            sharedPref.getBoolean(
                "spectrogramShowFreqAlongX",
                true
            )
        )
        analyzerViews.graphView.setSmoothRender(
            sharedPref.getBoolean(
                "spectrogramSmoothRender",
                false
            )
        )
        analyzerViews.graphView.setColorMap(sharedPref.getString("spectrogramColorMap", "Hot"))
        // set spectrogram show range
        analyzerViews.graphView.setSpectrogramDBLowerBound(
            sharedPref.getString(
                "spectrogramRange",
                java.lang.Double.toString(analyzerViews.graphView.spectrogramPlot.spectrogramBMP.dBLowerBound)
            ).toFloat().toDouble()
        )
        analyzerViews.graphView.setLogAxisMode(
            sharedPref.getBoolean("spectrogramLogPlotMethod", true)
        )
        analyzerViews.bWarnOverrun = sharedPref.getBoolean("warnOverrun", false)
        analyzerViews.setFpsLimit(
            sharedPref.getString(
                "spectrogramFPS",
                getString(R.string.spectrogram_fps_default)
            ).toDouble()
        )

        // Apply settings by travel the views with android:tag="select".
        visit((analyzerViews.graphView.rootView as ViewGroup), object : Visit {
            override fun exec(view: View) {
                processClick(view)
            }
        }, "select")

        // Get view range setting
        val isLock = sharedPref.getBoolean("view_range_lock", false)
        if (isLock) {
            Log.i(TAG, "LoadPreferences(): isLocked")
            // Set view range and stick to measure mode
            var rr: DoubleArray? = DoubleArray(AnalyzerGraphic.VIEW_RANGE_DATA_LENGTH)
            for (i in rr!!.indices) {
                rr[i] = AnalyzerUtil.getDouble(sharedPref, "view_range_rr_$i", 0.0 / 0.0)
                if (java.lang.Double.isNaN(rr[i])) {  // not properly initialized
                    Log.w(
                        TAG,
                        "LoadPreferences(): rr is not properly initialized"
                    )
                    rr = null
                    break
                }
            }
            if (rr != null) {
                viewRangeArray = rr
            }
            stickToMeasureMode()
        } else {
            stickToMeasureModeCancel()
        }
    }

    fun stickToMeasureMode() {
        isLockViewRange = true
        switchMeasureAndScaleMode() // Force set to Measure mode
    }

    fun stickToMeasureModeCancel() {
        isLockViewRange = false
        if (isMeasure) {
            switchMeasureAndScaleMode() // Force set to ScaleMode
        }
    }




    // Button processing
    fun showPopupMenu(view: View?) {
        analyzerViews.showPopupMenu(view)
    }

    /**
     * Gesture Listener for graphView (and possibly other views)
     * How to attach these events to the graphView?
     *
     * @author xyy
     */
    inner class AnalyzerGestureListener(val analyzerViews: AnalyzerViews) : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent?): Boolean {  // enter here when down action happen
            flyingMoveHandler.removeCallbacks(flyingMoveRunnable)
            return true
        }

        override fun onLongPress(event: MotionEvent) {
            if (isInGraphView(event.getX(0), event.getY(0))) {
                if (!isMeasure) {  // go from "scale" mode to "cursor" mode
                    switchMeasureAndScaleMode()
                }
            }
            measureEvent(event) // force insert this event
        }

        override fun onDoubleTap(event: MotionEvent?): Boolean {
            if (!isMeasure) {
                scaleEvent(event) // ends scale mode
                analyzerViews.graphView.resetViewScale()
            }
            return true
        }

        override fun onFling(
            event1: MotionEvent?, event2: MotionEvent?,
            velocityX: Float, velocityY: Float
        ): Boolean {
            if (isMeasure) {
                // seems never reach here...
                return true
            }
            // Log.d(TAG, "  AnalyzerGestureListener::onFling: " + event1.toString()+event2.toString());
            // Fly the canvas in graphView when in scale mode
            shiftingVelocity =
                Math.sqrt(velocityX * velocityX + velocityY * velocityY.toDouble())
            shiftingComponentX = velocityX / shiftingVelocity
            shiftingComponentY = velocityY / shiftingVelocity
            val DPRatio: Float = getResources().getDisplayMetrics().density
            flyAcceleration = 1200 * DPRatio.toDouble()
            timeFlingStart = SystemClock.uptimeMillis()
            flyingMoveHandler.postDelayed(flyingMoveRunnable, 0)
            return true
        }

        var flyingMoveHandler = Handler()
        var timeFlingStart // Prevent from running forever
                : Long = 0
        var flyDt = 1 / 20.0 // delta t of refresh
        var shiftingVelocity // fling velocity
                = 0.0
        var shiftingComponentX // fling direction x
                = 0.0
        var shiftingComponentY // fling direction y
                = 0.0
        var flyAcceleration =
            1200.0 // damping acceleration of fling, pixels/second^2
        var flyingMoveRunnable: Runnable = object : Runnable {
            override fun run() {
                var shiftingVelocityNew = shiftingVelocity - flyAcceleration * flyDt
                if (shiftingVelocityNew < 0) shiftingVelocityNew = 0.0
                // Number of pixels that should move in this time step
                val shiftingPixel =
                    (shiftingVelocityNew + shiftingVelocity) / 2 * flyDt
                shiftingVelocity = shiftingVelocityNew
                if (shiftingVelocity > 0f
                    && SystemClock.uptimeMillis() - timeFlingStart < 10000
                ) {
                    // Log.i(TAG, "  fly pixels x=" + shiftingPixelX + " y=" + shiftingPixelY);
                    val graphView: AnalyzerGraphic = analyzerViews.graphView
                    graphView.xShift =
                        graphView.xShift - shiftingComponentX * shiftingPixel / graphView.canvasWidth / graphView.xZoom
                    graphView.yShift =
                        graphView.yShift - shiftingComponentY * shiftingPixel / graphView.canvasHeight / graphView.yZoom
                    // Am I need to use runOnUiThread() ?
                    analyzerViews.invalidateGraphView()
                    flyingMoveHandler.postDelayed(this, (1000 * flyDt).toLong())
                }
            }
        }

        fun isInGraphView(x: Float, y: Float): Boolean {
            analyzerViews.graphView.getLocationInWindow(windowLocation)
            return x >= windowLocation.get(0) && y >= windowLocation.get(1) && x < windowLocation.get(
                0
            ) + analyzerViews.graphView.getWidth() && y < windowLocation.get(1) + analyzerViews.graphView.getHeight()
        }



    }

    fun isInGraphView(x: Float, y: Float): Boolean {
        analyzerViews.graphView.getLocationInWindow(windowLocation)
        return x >= windowLocation.get(0) && y >= windowLocation.get(1) && x < windowLocation.get(
            0
        ) + analyzerViews.graphView.getWidth() && y < windowLocation.get(1) + analyzerViews.graphView.getHeight()
    }


    fun switchMeasureAndScaleMode() {
        if (isLockViewRange) {
            isMeasure = true
            return
        }
        isMeasure = !isMeasure
        //SelectorText st = (SelectorText) findViewById(R.id.graph_view_mode);
        //st.performClick();
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isInGraphView(event.getX(0), event.getY(0))) {
            mDetector.onTouchEvent(event)
            if (isMeasure) {
                measureEvent(event)
            } else {
                scaleEvent(event)
            }
            analyzerViews.invalidateGraphView()
            // Go to scaling mode when user release finger in measure mode.
            if (event.getActionMasked() === MotionEvent.ACTION_UP) {
                if (isMeasure) {
                    switchMeasureAndScaleMode()
                }
            }
        } else {
            // When finger is outside the plot, hide the cursor and go to scaling mode.
            if (isMeasure) {
                analyzerViews.graphView.hideCursor()
                switchMeasureAndScaleMode()
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * Manage cursor for measurement
     */
    private fun measureEvent(event: MotionEvent) {
        when (event.getPointerCount()) {
            1 -> analyzerViews.graphView.setCursor(event.getX(), event.getY())
            2 -> if (isInGraphView(event.getX(1), event.getY(1))) {
                switchMeasureAndScaleMode()
            }
        }
    }

    /**
     * Manage scroll and zoom
     */
    private val INIT = Double.MIN_VALUE
    private var isPinching = false
    private var xShift0 = INIT
    private  var yShift0:kotlin.Double = INIT
    private var x0 = 0.0
    private  var y0:kotlin.Double = 0.0
    private val windowLocation = IntArray(2)

    private fun scaleEvent(event: MotionEvent?) {
        if (event?.action !== MotionEvent.ACTION_MOVE) {
            xShift0 = INIT
            yShift0 = INIT
            isPinching = false
            // Log.i(TAG, "scaleEvent(): Skip event " + event.getAction());
            return
        }

        // Log.i(TAG, "scaleEvent(): switch " + event.getAction());
        val graphView = analyzerViews.graphView
        when (event.getPointerCount()) {
            2 -> {
                if (isPinching) {
                    graphView.setShiftScale(
                        event.getX(0).toDouble(),
                        event.getY(0).toDouble(),
                        event.getX(1).toDouble(),
                        event.getY(1).toDouble()
                    )
                } else {
                    graphView.setShiftScaleBegin(
                        event.getX(0).toDouble(),
                        event.getY(0).toDouble(),
                        event.getX(1).toDouble(),
                        event.getY(1).toDouble()
                    )
                }
                isPinching = true
            }
            1 -> {
                val x: Float = event.getX(0)
                val y: Float = event.getY(0)
                graphView.getLocationInWindow(windowLocation)
                // Log.i(TAG, "scaleEvent(): xy=" + x + " " + y + "  wc = " + wc[0] + " " + wc[1]);
                if (isPinching || xShift0 == INIT) {
                    xShift0 = graphView.xShift
                    x0 = x.toDouble()
                    yShift0 = graphView.yShift
                    y0 = y.toDouble()
                } else {
                    // when close to the axis, scroll that axis only
                    if (x0 < windowLocation[0] + 50) {
                        graphView.yShift =
                            yShift0 + (y0 - y) / graphView.canvasHeight / graphView.yZoom
                    } else if (y0 < windowLocation[1] + 50) {
                        graphView.xShift =
                            xShift0 + (x0 - x) / graphView.canvasWidth / graphView.xZoom
                    } else {
                        graphView.xShift =
                            xShift0 + (x0 - x) / graphView.canvasWidth / graphView.xZoom
                        graphView.yShift =
                            yShift0 + (y0 - y) / graphView.canvasHeight / graphView.yZoom
                    }
                }
                isPinching = false
            }
            else -> Log.i(TAG, "Invalid touch count")
        }
    }

    override fun onLongClick(v: View?): Boolean {
        vibrate(300)
        return true
    }

    override fun onClick(v: View?) {
        if (processClick(v)) {
            restartSampling(analyzerParam);
        }
        analyzerViews.invalidateGraphView();
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // get the tag, which is the value we are going to use

        // get the tag, which is the value we are going to use
        val selectedItemTag: String = view?.getTag().toString()
        // if tag() is "0" then do not update anything (it is a title)
        // if tag() is "0" then do not update anything (it is a title)
        if (selectedItemTag == "0") {
            return
        }

        // get the text and set it as the button text

        // get the text and set it as the button text
        val selectedItemText = (view as TextView).text.toString()

        val buttonId = parent!!.tag.toString().toInt()
        val buttonView: Button = findViewById<View>(buttonId) as Button
        buttonView.setText(selectedItemText)

        val b_need_restart_audio: Boolean

        // Save the choosen preference

        // Save the choosen preference
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor: SharedPreferences.Editor = sharedPref.edit()

        // so change of sample rate do not change view range

        // so change of sample rate do not change view range
        if (!isLockViewRange) {
            viewRangeArray = analyzerViews.graphView.viewPhysicalRange
            // if range is align at boundary, extend the range.
            Log.i(
                TAG,
                "set sampling rate:a " + viewRangeArray?.get(0)
                    .toString() + " ==? " + viewRangeArray?.get(6)
            )
            if (viewRangeArray?.get(0) === viewRangeArray?.get(6)) {
                viewRangeArray?.set(0, 0.0)
            }
        }

        // dismiss the pop up
        when (buttonId) {
            R.id.button_sample_rate -> {
                analyzerViews.popupMenuSampleRate?.dismiss()
                if (!isLockViewRange) {
                    Log.i(
                        TAG,
                        "set sampling rate:b " + viewRangeArray!![1]
                            .toString() + " ==? " + viewRangeArray!![6 + 1]
                    )
                    if (viewRangeArray!![1] === viewRangeArray!![6 + 1]) {
                        viewRangeArray!![1] = (selectedItemTag.toInt() / 2).toDouble()
                    }
                    Log.i(
                        TAG,
                        "onItemClick(): viewRangeArray saved. " + viewRangeArray!![0]
                            .toString() + " ~ " + viewRangeArray!![1]
                    )
                }
                analyzerParam.sampleRate = selectedItemTag.toInt()
                b_need_restart_audio = true
                editor.putInt("button_sample_rate", analyzerParam.sampleRate)
            }
            R.id.button_fftlen -> {
                analyzerViews.popupMenuFFTLen.dismiss()
                analyzerParam.fftLen = selectedItemTag.toInt()
                analyzerParam.hopLen =
                    (analyzerParam.fftLen * (1 - analyzerParam.overlapPercent / 100) + 0.5).toInt()
                b_need_restart_audio = true
                editor.putInt("button_fftlen", analyzerParam.fftLen)
                fillFftCalibration(analyzerParam, calibLoad)
            }
            R.id.button_average -> {
                analyzerViews.popupMenuAverage.dismiss()
                analyzerParam.nFFTAverage = selectedItemTag.toInt()
                if (analyzerViews.graphView != null) {
                    analyzerViews.graphView.setTimeMultiplier(analyzerParam.nFFTAverage)
                }
                b_need_restart_audio = false
                editor.putInt("button_average", analyzerParam.nFFTAverage)
            }
            else -> {
                Log.w(TAG, "onItemClick(): no this button")
                b_need_restart_audio = false
            }
        }

        editor.commit()

        if (b_need_restart_audio) {
            restartSampling(analyzerParam)
        }
    }

    fun selectFile(requestType: Int) {
        // https://developer.android.com/guide/components/intents-common.html#Storage
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        if (requestType == REQUEST_AUDIO_GET) {
            intent.type = "audio/*"
        } else {
            intent.type = "*/*"
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, requestType)
        } else {
            Log.e(TAG, "No file chooser found!.")

            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(
                this, "Please install a File Manager.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    fun fillFftCalibration(
        _analyzerParam: AnalyzerParameters?,
        _calibLoad: CalibrationLoad
    ) {
        if (_calibLoad.freq == null || _calibLoad.freq.size === 0 || _analyzerParam == null) {
            return
        }
        val freqTick = DoubleArray(_analyzerParam.fftLen / 2 + 1)
        for (i in freqTick.indices) {
            freqTick[i] = i.toDouble() / _analyzerParam.fftLen * _analyzerParam.sampleRate
        }
        _analyzerParam.micGainDB =
            AnalyzerUtil.interpLinear(_calibLoad.freq, _calibLoad.gain, freqTick)
        _analyzerParam.calibName = _calibLoad.name
//        for (int i = 0; i < _analyzerParam.micGainDB.length; i++) {
//            Log.i(TAG, "calib: " + freqTick[i] + "Hz : " + _analyzerParam.micGainDB[i]);
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CALIB_LOAD && resultCode == Activity.RESULT_OK) {
            val uri: Uri = data!!.data
            calibLoad.loadFile(uri, this)
            Log.w(TAG, "mime:" + contentResolver.getType(uri))
            fillFftCalibration(analyzerParam, calibLoad)
        } else if (requestCode == REQUEST_AUDIO_GET) {
            Log.w(TAG, "requestCode == REQUEST_AUDIO_GET")
        }
    }


    override fun ready() {
        // put code here for the moment that graph size just changed
        Log.v(TAG, "ready()");
        analyzerViews.invalidateGraphView()
    }

    companion object {
        var isMeasure = false
        @Volatile

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
        val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1 // just a number

        val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2

        const val MYPREFERENCES_MSG_SOURCE_ID = "AnalyzerActivity.SOURCE_ID"
        const val MYPREFERENCES_MSG_SOURCE_NAME = "AnalyzerActivity.SOURCE_NAME"
    }
}
