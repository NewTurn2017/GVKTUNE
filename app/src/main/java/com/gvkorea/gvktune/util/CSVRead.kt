package com.gvkorea.gvktune.util

import android.content.res.AssetManager
import android.util.Log
import com.opencsv.CSVReader
import java.io.*

class CSVRead {

    var s: Array<String>? = null
    fun readCsv(spkNo: Int): MutableList<Array<String>> {
        val data = mutableListOf<Array<String>>()
        val baseDir = android.os.Environment.getExternalStorageDirectory().absolutePath + "/gvkorea"
        val folder = File(baseDir)
        if (!folder.exists()) {
            folder.mkdir()
        }
        val filename = "Spk${spkNo}_tunedEQ" + ".csv"
        val filePath = baseDir + File.separator + filename
        try {
            val reader = CSVReader(FileReader(filePath) as Reader)
            s = reader.readNext()
            if (s != null) {
                data.add(s!!)
            }

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return data
    }

    fun readSettingCsv(): MutableList<Array<String>> {
        val data = mutableListOf<Array<String>>()
        val baseDir = android.os.Environment.getExternalStorageDirectory().absolutePath + "/gvkorea"
        val folder = File(baseDir)
        if (!folder.exists()) {
            folder.mkdir()
        }
        val filename = "settings.csv"
        val filePath = baseDir + File.separator + filename
        try {
            val reader = CSVReader(FileReader(filePath))
            while (reader.readNext().let { s = it; it != null }) {
                if (s != null) {
                    data.add(s!!)
                }
            }


        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return data
    }

    fun readCalibCsv(am: AssetManager, filename: String?): MutableList<Array<String>> {
        copyAssets("calibration", am, filename)
        val data = mutableListOf<Array<String>>()
        val baseDir = android.os.Environment.getExternalStorageDirectory().absolutePath + "/gvkorea_calib"
        val folder = File(baseDir)
        if (!folder.exists()) {
            folder.mkdir()
        }
        val filePath = baseDir + File.separator + filename
        try {
            val reader = CSVReader(FileReader(filePath))
            while (reader.readNext().let { s = it; it != null }) {
                if (s != null) {
                    data.add(s!!)
                }
            }


        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return data
    }

    fun copyAssets(srcDir: String, am: AssetManager?, filename: String?) {

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            inputStream = am!!.open("$srcDir/$filename")
            val folder = File(android.os.Environment.getExternalStorageDirectory().absolutePath + "/gvkorea_calib")
            if (!folder.exists()) {
                folder.mkdir()
            }
            val outFile = File(folder, filename)
            outputStream = FileOutputStream(outFile)
            copyFile(inputStream, outputStream)
        } catch (e: IOException) {
            Log.e("tag", "Failed to copy asset file: $filename", e)
        } finally {

            try {
                inputStream?.close()
            } catch (e: IOException) {
                // NOOP
            }
            try {
                outputStream?.close()
            } catch (e: IOException) {
                // NOOP
            }
        }
    }

    private fun copyFile(inputStream: InputStream, outputStream: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (inputStream.read(buffer).let { read = it; it != -1 }) {
            outputStream.write(buffer, 0, read)
        }
    }
}