package com.gvkorea.gvktune.util

import java.nio.ByteBuffer

class Protocol {

    val CH_1 = '1'
    val CH_2 = '2'
    val CH_ALL = 'A'


    val CMD_SET = 'S'
    val CMD_REQUEST = 'R'
    val CMD_BRIDGE = 'b'
    val CMD_UDP_TCP_SERVER_IFNO = 'B'
    val CMD_UDP_TCP_SERVER_IFNO_PARA2 = 'I'
    val CMD_INPUT_GAIN = 'g'
    val CMD_INPUT_MUTE = 't'
    val CMD_INPUT_PEAK_COMP = 'k'
    val CMD_INPUT_RMS_COMP = 'r'
    val CMD_INPUT_NOISE_GATE = 'c'
    val CMD_INPUT_PHASE_INVERTER = 'v'
    val CMD_INPUT_DELAY = 'd'
    val CMD_EQ_BYPASS = 'y'
    val CMD_EQ_BYPASS_BLOCK_GEQ = 'q'
    val CMD_EQ_BYPASS_BLOCK_PEQ = 'P'
    val CMD_INPUT_GEQ = 'q'
    val CMD_INPUT_GEQ_ALL = 'i'

    val CMD_OUTPUT_BPF = 'X'
    val CMD_OUTPUT_PEQ = 'P'
    val CMD_OUTPUT_GAIN = 'G'
    val CMD_OUTPUT_LIMITER = 'L'
    val CMD_OUTPUT_MUTE = 'T'
    val CMD_EQ_RESET = 'F'
    val CMD_EQ_RESET_BLOCK_GEQ = 'q'
    val CMD_EQ_RESET_BLOCK_PEQ = 'P'
    val CMD_EQ_RESET_BLOCK_ALL = 'A'

    val CMD_NOISE_GENERATOR = 'h'
    val CMD_REVERB = 'V'
    val CMD_REVER_ALL = 'A'
    val CMD_DEVICE_INFO_PARA1 = 'C'
    val CMD_DEVICE_INFO_PARA2 = 'd'
    val CMD_PRESET_PARA1 = 'E'
    val CMD_PRESET_PARA1_REQUEST_NAME = 'R'
    val CMD_PRESET_PARA2_REQUEST_NAME = 'E'
    val CMD_PRESET_PARA2_NAME = 'N'
    val CMD_PRESET_PARA2_SELECT = 'E'
    val CMD_PRESET_PARA2_LOAD = 'L'
    val CMD_PRESET_PARA2_SAVE = 'S'
    val CMD_PRESET_PARA2_DELETE = 'D'
    val CMD_PRESET_PARA2_REQUEST_ALL = 'a'
    val CMD_PRESET_PARA2_VALUE_REQUEST = 'R'
    val CMD_PRESET_PARA2_VALUE_RESPONSE = 'R'
    val CMD_PRESET_PARA2_WRITE_START = 'W'
    val CMD_PRESET_PARA2_WRITE_END = 'w'
    val CMD_OUTPUT_PEQ_ALL = 'K'


    val FREQ_20HZ = 0
    val FREQ_25HZ = 1
    val FREQ_32HZ = 2
    val FREQ_40HZ = 3
    val FREQ_50HZ = 4
    val FREQ_63HZ = 5
    val FREQ_80HZ = 6
    val FREQ_100HZ = 7
    val FREQ_125HZ = 8
    val FREQ_160HZ = 9
    val FREQ_200HZ = 10
    val FREQ_250HZ = 11
    val FREQ_315HZ = 12
    val FREQ_400HZ = 13
    val FREQ_500HZ = 14
    val FREQ_630HZ = 15
    val FREQ_800HZ = 16
    val FREQ_1000HZ = 17
    val FREQ_1250HZ = 18
    val FREQ_1600HZ = 19
    val FREQ_2000HZ = 20
    val FREQ_2500HZ = 21
    val FREQ_3150HZ = 22
    val FREQ_4000HZ = 23
    val FREQ_5000HZ = 24
    val FREQ_6300HZ = 25
    val FREQ_8000HZ = 26
    val FREQ_10000HZ = 27
    val FREQ_12500HZ = 28
    val FREQ_16000HZ = 29
    val FREQ_20000HZ = 30

    val PRESET1 = "PRESET01"
    val PRESET2 = "PRESET02"
    val PRESET3 = "PRESET03"
    val PRESET4 = "PRESET04"
    val PRESET5 = "PRESET05"
    val PRESET6 = "PRESET06"
    val PRESET7 = "PRESET07"
    val PRESET8 = "PRESET08"
    val PRESET9 = "PRESET09"
    val PRESET10 = "PRESET10"
    val PRESET11 = "PRESET11"

    fun packet_Connect(
        commandID: Char,
        para1: Char,
        para2: Char,
        data0: Int,
        data1: Int,
        data2: Int,
        data3: Int
    ): ByteArray {

        val mCmd = IntArray(9)
        val tx_buff = ByteArray(mCmd.size)
        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data3
        mCmd[5] = data2
        mCmd[6] = data1
        mCmd[7] = data0
        mCmd[8] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_InputGain(para2: Char, data: Float): ByteArray {

        val commandID = CMD_SET
        val para1 = CMD_INPUT_GAIN

        val mCmd = IntArray(9)
        val tx_buff = ByteArray(mCmd.size)

        val bytes = floatToBytes(data)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = bytes[3].toInt()
        mCmd[5] = bytes[2].toInt()
        mCmd[6] = bytes[1].toInt()
        mCmd[7] = bytes[0].toInt()
        mCmd[8] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_OutputGain(para2: Char, data: Float): ByteArray {

        val commandID = CMD_SET
        val para1 = CMD_OUTPUT_GAIN

        val mCmd = IntArray(9)
        val tx_buff = ByteArray(mCmd.size)

        val bytes = floatToBytes(data)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = bytes[3].toInt()
        mCmd[5] = bytes[2].toInt()
        mCmd[6] = bytes[1].toInt()
        mCmd[7] = bytes[0].toInt()
        mCmd[8] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    private fun floatToBytes(data: Float): ByteArray {
        val bits = java.lang.Float.floatToIntBits(data)
        val bytes = ByteArray(4)
        bytes[0] = (bits and 0xff).toByte()
        bytes[1] = (bits shr 8 and 0xff).toByte()
        bytes[2] = (bits shr 16 and 0xff).toByte()
        bytes[3] = (bits shr 24 and 0xff).toByte()
        return bytes
    }

    fun packet_InputMute(para2: Char, data: Int): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_INPUT_MUTE

        val mCmd = IntArray(6)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data
        mCmd[5] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_OutputMute(para2: Char, data: Int): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_OUTPUT_MUTE

        val mCmd = IntArray(6)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data
        mCmd[5] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_InputPeakComp(
        para2: Char,
        data0: Float,
        data4: Float,
        data8: Float,
        data12: Int
    ): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_INPUT_PEAK_COMP
        val bytesThreshold = floatToBytes(data0)
        val bytesReleaseSec = floatToBytes(data4)
        val bytesAttackms = floatToBytes(data8)

        val mCmd = IntArray(18)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = bytesThreshold[3].toInt()
        mCmd[5] = bytesThreshold[2].toInt()
        mCmd[6] = bytesThreshold[1].toInt()
        mCmd[7] = bytesThreshold[0].toInt()
        mCmd[8] = bytesReleaseSec[3].toInt()
        mCmd[9] = bytesReleaseSec[2].toInt()
        mCmd[10] = bytesReleaseSec[1].toInt()
        mCmd[11] = bytesReleaseSec[0].toInt()
        mCmd[12] = bytesAttackms[3].toInt()
        mCmd[13] = bytesAttackms[2].toInt()
        mCmd[14] = bytesAttackms[1].toInt()
        mCmd[15] = bytesAttackms[0].toInt()
        mCmd[16] = data12
        mCmd[17] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_InputRMSComp(
        para2: Char,
        data0: Float,
        data4: Float,
        data8: Float,
        data12: Int,
        data13: Int,
        data14: Int
    ): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_INPUT_RMS_COMP
        val bytesThreshold = floatToBytes(data0)
        val bytesReleaseSec = floatToBytes(data4)
        val bytesAttackms = floatToBytes(data8)

        val mCmd = IntArray(20)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = bytesThreshold[3].toInt()
        mCmd[5] = bytesThreshold[2].toInt()
        mCmd[6] = bytesThreshold[1].toInt()
        mCmd[7] = bytesThreshold[0].toInt()
        mCmd[8] = bytesReleaseSec[3].toInt()
        mCmd[9] = bytesReleaseSec[2].toInt()
        mCmd[10] = bytesReleaseSec[1].toInt()
        mCmd[11] = bytesReleaseSec[0].toInt()
        mCmd[12] = bytesAttackms[3].toInt()
        mCmd[13] = bytesAttackms[2].toInt()
        mCmd[14] = bytesAttackms[1].toInt()
        mCmd[15] = bytesAttackms[0].toInt()
        mCmd[16] = data12
        mCmd[17] = data13
        mCmd[18] = data14
        mCmd[19] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_InputNoiseGate(
        para2: Char,
        data0: Float,
        data4: Float,
        data8: Float,
        data12: Int
    ): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_INPUT_NOISE_GATE
        val bytesThreshold = floatToBytes(data0)
        val bytesReleaseSec = floatToBytes(data4)
        val bytesAttackms = floatToBytes(data8)

        val mCmd = IntArray(18)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = bytesThreshold[3].toInt()
        mCmd[5] = bytesThreshold[2].toInt()
        mCmd[6] = bytesThreshold[1].toInt()
        mCmd[7] = bytesThreshold[0].toInt()
        mCmd[8] = bytesReleaseSec[3].toInt()
        mCmd[9] = bytesReleaseSec[2].toInt()
        mCmd[10] = bytesReleaseSec[1].toInt()
        mCmd[11] = bytesReleaseSec[0].toInt()
        mCmd[12] = bytesAttackms[3].toInt()
        mCmd[13] = bytesAttackms[2].toInt()
        mCmd[14] = bytesAttackms[1].toInt()
        mCmd[15] = bytesAttackms[0].toInt()
        mCmd[16] = data12
        mCmd[17] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }


    fun packet_InputPhaseInvert(para2: Char, data: Int): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_INPUT_PHASE_INVERTER

        val mCmd = IntArray(6)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data
        mCmd[5] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_Bridge(data0: Int): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_BRIDGE
        val para2 = CH_ALL
        val data1 = 0

        val mCmd = IntArray(7)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0
        mCmd[5] = data1
        mCmd[6] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_input_GEQ_Bypass(para2: Char, data: Int): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_EQ_BYPASS
        val data1 = CMD_EQ_BYPASS_BLOCK_GEQ

        val mCmd = IntArray(7)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data
        mCmd[5] = data1.toInt()
        mCmd[6] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }


    fun packet_output_PEQ_Bypass(para2: Char, data: Int): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_EQ_BYPASS
        val data1 = CMD_EQ_BYPASS_BLOCK_PEQ

        val mCmd = IntArray(7)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data
        mCmd[5] = data1.toInt()
        mCmd[6] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_input_EQ_Reset(para2: Char): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_EQ_RESET
        val data0 = CMD_EQ_RESET_BLOCK_GEQ
        val data1 = 1

        val mCmd = IntArray(7)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0.toInt()
        mCmd[5] = data1
        mCmd[6] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_output_EQ_Reset(para2: Char): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_EQ_RESET
        val data0 = CMD_EQ_RESET_BLOCK_PEQ
        val data1 = 1

        val mCmd = IntArray(7)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0.toInt()
        mCmd[5] = data1
        mCmd[6] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_OutputLimiter_RMSComp(
        para2: Char,
        data0: Float,
        data4: Float,
        data8: Float,
        data12: Int,
        data13: Int,
        data14: Int
    ): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_OUTPUT_LIMITER
        val bytesThreshold = floatToBytes(data0)
        val bytesReleaseSec = floatToBytes(data4)
        val bytesAttackms = floatToBytes(data8)

        val mCmd = IntArray(20)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = bytesThreshold[3].toInt()
        mCmd[5] = bytesThreshold[2].toInt()
        mCmd[6] = bytesThreshold[1].toInt()
        mCmd[7] = bytesThreshold[0].toInt()
        mCmd[8] = bytesReleaseSec[3].toInt()
        mCmd[9] = bytesReleaseSec[2].toInt()
        mCmd[10] = bytesReleaseSec[1].toInt()
        mCmd[11] = bytesReleaseSec[0].toInt()
        mCmd[12] = bytesAttackms[3].toInt()
        mCmd[13] = bytesAttackms[2].toInt()
        mCmd[14] = bytesAttackms[1].toInt()
        mCmd[15] = bytesAttackms[0].toInt()
        mCmd[16] = data12
        mCmd[17] = data13
        mCmd[18] = data14
        mCmd[19] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }


    fun packet_Delay(para2: Char, data: Int, data4: Int): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_INPUT_DELAY

        val bytes = fromUnsignedInt(data.toLong())
        val mCmd = IntArray(10)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = bytes[0].toInt()
        mCmd[5] = bytes[1].toInt()
        mCmd[6] = bytes[2].toInt()
        mCmd[7] = bytes[3].toInt()
        mCmd[8] = data4
        mCmd[9] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun fromUnsignedInt(value: Long): ByteArray {
        val bytes = ByteArray(8)
        ByteBuffer.wrap(bytes).putLong(value)
        return bytes.copyOfRange(4, 8)
    }

    fun packet_InputGEQ(para2: Char, data0: Float, data4: Float, data8: Int): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_INPUT_GEQ

        val bytesFreq = floatToBytes(data0)
        val bytesGain = floatToBytes(data4)

        val mCmd = IntArray(14)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = bytesFreq[3].toInt()
        mCmd[5] = bytesFreq[2].toInt()
        mCmd[6] = bytesFreq[1].toInt()
        mCmd[7] = bytesFreq[0].toInt()
        mCmd[8] = bytesGain[3].toInt()
        mCmd[9] = bytesGain[2].toInt()
        mCmd[10] = bytesGain[1].toInt()
        mCmd[11] = bytesGain[0].toInt()
        mCmd[12] = data8
        mCmd[13] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_InputGEQ_All(para2: Char, data4: FloatArray): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_INPUT_GEQ_ALL

        val arrayByteGain = ArrayList<ByteArray>()
        for (i in data4.indices) {
            arrayByteGain.add(floatToBytes(data4[i]))
        }
        val mCmd = IntArray(160)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        var j = 0
        for (i in data4.indices) {
            mCmd[j + 4] = arrayByteGain[i][3].toInt()
            mCmd[j + 5] = arrayByteGain[i][2].toInt()
            mCmd[j + 6] = arrayByteGain[i][1].toInt()
            mCmd[j + 7] = arrayByteGain[i][0].toInt()
            mCmd[j + 8] = 1
            j += 5
        }
        mCmd[159] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_OutputPEQ(
        para2: Char,
        data0: Int,
        data1: Char,
        data2: Float,
        data6: Float,
        data10: Float,
        data14: Float
    ): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_OUTPUT_PEQ

        val bytesFreq = floatToBytes(data2)
        val bytesGain = floatToBytes(data6)
        val bytesQFactor = floatToBytes(data10)
        val bytesSlope = floatToBytes(data14)

        val mCmd = IntArray(24)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0
        mCmd[5] = data1.toInt()
        mCmd[6] = bytesFreq[3].toInt()
        mCmd[7] = bytesFreq[2].toInt()
        mCmd[8] = bytesFreq[1].toInt()
        mCmd[9] = bytesFreq[0].toInt()
        mCmd[10] = bytesGain[3].toInt()
        mCmd[11] = bytesGain[2].toInt()
        mCmd[12] = bytesGain[1].toInt()
        mCmd[13] = bytesGain[0].toInt()
        mCmd[14] = bytesQFactor[3].toInt()
        mCmd[15] = bytesQFactor[2].toInt()
        mCmd[16] = bytesQFactor[1].toInt()
        mCmd[17] = bytesQFactor[0].toInt()
        mCmd[18] = bytesSlope[3].toInt()
        mCmd[19] = bytesSlope[2].toInt()
        mCmd[20] = bytesSlope[1].toInt()
        mCmd[21] = bytesSlope[0].toInt()
        mCmd[22] = 1
        mCmd[23] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }
        return tx_buff
    }


    fun packet_OutputBPF(
        para2: Char,
        data0: Int,
        data1: Int,
        data2: Float,
        data6: Float,
        data10: Float,
        data18: Int
    ): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_OUTPUT_BPF
        val bytesFreqLowCutoff = floatToBytes(data2)
        val bytesFreqHiCutoff = floatToBytes(data6)
        val bytesGain = floatToBytes(data10)

        val mCmd = IntArray(20)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0
        mCmd[5] = data1
        mCmd[6] = bytesFreqLowCutoff[3].toInt()
        mCmd[7] = bytesFreqLowCutoff[2].toInt()
        mCmd[8] = bytesFreqLowCutoff[1].toInt()
        mCmd[9] = bytesFreqLowCutoff[0].toInt()
        mCmd[10] = bytesFreqHiCutoff[3].toInt()
        mCmd[11] = bytesFreqHiCutoff[2].toInt()
        mCmd[12] = bytesFreqHiCutoff[1].toInt()
        mCmd[13] = bytesFreqHiCutoff[0].toInt()
        mCmd[14] = bytesGain[3].toInt()
        mCmd[15] = bytesGain[2].toInt()
        mCmd[16] = bytesGain[1].toInt()
        mCmd[17] = bytesGain[0].toInt()
        mCmd[18] = data18
        mCmd[19] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_NoiseGenerator(para2: Char, data0: Int, data1: Float, data5: Int): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_NOISE_GENERATOR
        val bytesGain = floatToBytes(data1)
        val mCmd = IntArray(11)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0
        mCmd[5] = bytesGain[3].toInt()
        mCmd[6] = bytesGain[2].toInt()
        mCmd[7] = bytesGain[1].toInt()
        mCmd[8] = bytesGain[0].toInt()
        mCmd[9] = data5
        mCmd[10] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_StatusRequest_GEQ(para2: Char, ch: Char, freqNo: Int): ByteArray {
        val commandID = CMD_REQUEST
        val para1 = CMD_REQUEST
        val freqNo = freqNo
        val mCmd = IntArray(7)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = ch.toInt()
        mCmd[5] = freqNo
        mCmd[6] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_StatusRequest_Common(para2: Char, ch: Char): ByteArray {
        val commandID = CMD_REQUEST
        val para1 = CMD_REQUEST
        val mCmd = IntArray(6)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = ch.toInt()
        mCmd[5] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_StatusRequest_Common_data0(para2: Char, ch: Char, data0: Int): ByteArray {
        val commandID = CMD_REQUEST
        val para1 = CMD_REQUEST
        val mCmd = IntArray(7)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0
        mCmd[5] = ch.toInt()
        mCmd[6] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_StatusRequest_DeviceInfo(ch: Char, data: Int): ByteArray {
        val commandID = CMD_REQUEST
        val para1 = CMD_REQUEST
        val para2 = CMD_DEVICE_INFO_PARA1
        val mCmd = IntArray(7)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data
        mCmd[5] = ch.toInt()
        mCmd[6] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_StatusRequest_PresetName(presetNo: Int): ByteArray {
        val commandID = CMD_REQUEST
        val para1 = CMD_REQUEST
        val para2 = CMD_PRESET_PARA1
        val data0 = CMD_PRESET_PARA2_NAME
        val data1 = presetNo

        val mCmd = IntArray(7)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0.toInt()
        mCmd[5] = data1
        mCmd[6] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }


    fun packet_Reverb(data0: Int, data1: Int, data2: Int, data3: Int): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_REVERB
        val para2 = CMD_REVER_ALL

        val mCmd = IntArray(9)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0
        mCmd[5] = data1
        mCmd[6] = data2
        mCmd[7] = data3
        mCmd[8] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_preset_list(): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_PRESET_PARA1
        val para2 = CMD_PRESET_PARA2_SAVE
        val data0 = 0

        val mCmd = IntArray(6)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0
        mCmd[5] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_Preset_SetName(presetNo: Int, name: String): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_PRESET_PARA1
        val para2 = CMD_PRESET_PARA2_NAME
        val data0 = presetNo
        val strings = name.toCharArray()

        val mCmd = IntArray(14)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0
        mCmd[5] = strings[0].toInt()
        mCmd[6] = strings[1].toInt()
        mCmd[7] = strings[2].toInt()
        mCmd[8] = strings[3].toInt()
        mCmd[9] = strings[4].toInt()
        mCmd[10] = strings[5].toInt()
        mCmd[11] = strings[6].toInt()
        mCmd[12] = strings[7].toInt()
        mCmd[13] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff


    }

    fun packet_Preset_GetName(presetNo: Int): ByteArray {
        val commandID = CMD_REQUEST
        val para1 = CMD_PRESET_PARA1_REQUEST_NAME
        val para2 = CMD_PRESET_PARA2_REQUEST_NAME
        val data0 = CMD_PRESET_PARA2_NAME
        val data1 = presetNo

        val mCmd = IntArray(7)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0.toInt()
        mCmd[5] = data1
        mCmd[6] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff


    }

    fun packet_Preset_Save(presetNo: Int): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_PRESET_PARA1
        val para2 = CMD_PRESET_PARA2_SAVE
        val data0 = presetNo

        val mCmd = IntArray(6)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0
        mCmd[5] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_Preset_Delete(presetNo: Int): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_PRESET_PARA1
        val para2 = CMD_PRESET_PARA2_DELETE
        val data0 = presetNo

        val mCmd = IntArray(6)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0
        mCmd[5] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_Preset_Load(presetNo: Int): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_PRESET_PARA1
        val para2 = CMD_PRESET_PARA2_LOAD
        val data0 = presetNo

        val mCmd = IntArray(6)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0
        mCmd[5] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_Preset_Select(presetNo: Int): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_PRESET_PARA1
        val para2 = CMD_PRESET_PARA2_SELECT
        val data0 = presetNo

        val mCmd = IntArray(6)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0
        mCmd[5] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_Preset_Value_Request(): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_PRESET_PARA1
        val para2 = CMD_PRESET_PARA2_VALUE_REQUEST
        val data0 = CH_1
        val data1 = 0
        val mCmd = IntArray(7)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0.toInt()
        mCmd[5] = data1
        mCmd[6] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_DeivceInfo(ip: String, id: Char): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_DEVICE_INFO_PARA1
        val para2 = CMD_DEVICE_INFO_PARA2
        val data0 = 0
        val data1 = ip.split('.')
        val gateway = "192.168.0.1"
        val mask = "255.255.255.0"
        val mac = "00-128-255-00-00-00"
        val data5 = gateway.split('.')
        val data9 = mask.split('.')
        val data13 = mac.split('-')
        val data19 = id

        val mCmd = IntArray(26)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0
        mCmd[5] = data1[3].toInt()
        mCmd[6] = data1[2].toInt()
        mCmd[7] = data1[1].toInt()
        mCmd[8] = data1[0].substring(1).toInt()
        mCmd[9] = data5[3].toInt()
        mCmd[10] = data5[2].toInt()
        mCmd[11] = data5[1].toInt()
        mCmd[12] = data5[0].toInt()
        mCmd[13] = data9[3].toInt()
        mCmd[14] = data9[2].toInt()
        mCmd[15] = data9[1].toInt()
        mCmd[16] = data9[0].toInt()
        mCmd[17] = data13[5].toInt()
        mCmd[18] = data13[4].toInt()
        mCmd[19] = data13[3].toInt()
        mCmd[20] = data13[2].toInt()
        mCmd[21] = data13[1].toInt()
        mCmd[22] = data13[0].toInt()
        mCmd[23] = data19.toInt()
        mCmd[24] = 0
        mCmd[25] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_GetID(ip: String): ByteArray {
        val commandID = CMD_REQUEST
        val para1 = CMD_DEVICE_INFO_PARA1
        val para2 = CMD_DEVICE_INFO_PARA2
        val data0 = 0
        val data1 = ip.split('.')
        val gateway = "192.168.0.1"
        val mask = "255.255.255.0"
        val mac = "00-128-255-00-00-00"
        val data5 = gateway.split('.')
        val data9 = mask.split('.')
        val data13 = mac.split('-')
//        val data19 = id

        val mCmd = IntArray(24)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0
        mCmd[5] = data1[3].toInt()
        mCmd[6] = data1[2].toInt()
        mCmd[7] = data1[1].toInt()
        mCmd[8] = data1[0].substring(1).toInt()
        mCmd[9] = data5[3].toInt()
        mCmd[10] = data5[2].toInt()
        mCmd[11] = data5[1].toInt()
        mCmd[12] = data5[0].toInt()
        mCmd[13] = data9[3].toInt()
        mCmd[14] = data9[2].toInt()
        mCmd[15] = data9[1].toInt()
        mCmd[16] = data9[0].toInt()
        mCmd[17] = data13[5].toInt()
        mCmd[18] = data13[4].toInt()
        mCmd[19] = data13[3].toInt()
        mCmd[20] = data13[2].toInt()
        mCmd[21] = data13[1].toInt()
        mCmd[22] = data13[0].toInt()
//        mCmd[23] = data19.toInt()
//        mCmd[24] = 0
        mCmd[23] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_SetID(ip: String, id: Char): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_DEVICE_INFO_PARA1
        val para2 = CMD_DEVICE_INFO_PARA2
        val data0 = 0
        val data1 = ip.split('.')
        val gateway = "192.168.0.1"
        val mask = "255.255.255.0"
        val mac = "00-128-255-00-00-00"
        val data5 = gateway.split('.')
        val data9 = mask.split('.')
        val data13 = mac.split('-')
        val data19 = id.toInt()

        val mCmd = IntArray(26)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0
        mCmd[5] = data1[3].toInt()
        mCmd[6] = data1[2].toInt()
        mCmd[7] = data1[1].toInt()
        mCmd[8] = data1[0].substring(1).toInt()
        mCmd[9] = data5[3].toInt()
        mCmd[10] = data5[2].toInt()
        mCmd[11] = data5[1].toInt()
        mCmd[12] = data5[0].toInt()
        mCmd[13] = data9[3].toInt()
        mCmd[14] = data9[2].toInt()
        mCmd[15] = data9[1].toInt()
        mCmd[16] = data9[0].toInt()
        mCmd[17] = data13[5].toInt()
        mCmd[18] = data13[4].toInt()
        mCmd[19] = data13[3].toInt()
        mCmd[20] = data13[2].toInt()
        mCmd[21] = data13[1].toInt()
        mCmd[22] = data13[0].toInt()
        mCmd[23] = data19
        mCmd[24] = 0
        mCmd[25] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_Reset_Default(para2: Char): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_EQ_RESET
        val data0 = CMD_EQ_RESET_BLOCK_ALL
        val data1 = 1

        val mCmd = IntArray(7)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = data0.toInt()
        mCmd[5] = data1
        mCmd[6] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_preset_Start(): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_PRESET_PARA1
        val para2 = CMD_PRESET_PARA2_WRITE_START

        val mCmd = IntArray(5)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_preset_End(): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_PRESET_PARA1
        val para2 = CMD_PRESET_PARA2_WRITE_END

        val mCmd = IntArray(5)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

    fun packet_Preset_Request_All(): ByteArray {
        val commandID = CMD_SET
        val para1 = CMD_PRESET_PARA1
        val para2 = CMD_PRESET_PARA2_REQUEST_ALL

        val mCmd = IntArray(5)
        val tx_buff = ByteArray(mCmd.size)

        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.toInt()
        mCmd[2] = para1.toInt()
        mCmd[3] = para2.toInt()
        mCmd[4] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

}