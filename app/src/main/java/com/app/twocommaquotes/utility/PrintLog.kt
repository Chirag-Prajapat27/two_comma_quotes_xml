package com.app.twocommaquotes.utility

import android.os.Environment
import android.util.Log

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * <h1>PrintLog.java</h1>
 *
 *
 * The PrintLog class implements Log functionality which filters
 * Application logs with the TAG provided by the developer.
 *
 *
 * developer can easily filter logs by using this class.
 *
 */
object PrintLog {
    private const val TAG = "VLPL"
    private var isDebug = true
    private var isPersistent = false

    //method is use to disable log
    fun disableLog() {
        isDebug = false
    }

    /**
     * This method is used to print log in "Android Monitor".
     * This is a simplest method to print log , message will be defined
     * by the developer.
     *
     *
     * If developer wants to save log on sdcard, then it is possible
     * using the append method.
     *
     *
     * It will print log if isPersistent is true.
     *
     * @param msg : This is the message developer wants to print.
     * @return Nothing
     */
    fun logI(msg: String) {
        if (isDebug) {
            Log.i(TAG, msg)
            if (isPersistent) {
                appendLog(msg)
            }
        }
    }

    /**
     * This method is used print log in "Android Monitor".
     * This is a simplest method to print log , message will be defined
     * by the developer.
     *
     * @param tag : This is the filter tag,by which user can filter logs.
     * @param msg : This is the message developer wants to print.
     * @return Nothing
     */
    fun logI(tag: String, msg: String) {
        if (isDebug) {
            Log.i(tag, msg)
        }
    }

    /**
     * This method is used print log in "Android Monitor".
     * This is a simplest method to print log , message will be defined
     * by the developer.
     *
     * @param msg : This is the message developer wants to print.
     * @return Nothing
     */
    fun logE(msg: String) {
        if (isDebug) {
            Log.e(TAG, msg)
        }
    }

    /**
     * This is the method used to save log into sdcard.
     * path will be defined in file object.
     *
     * @param text : Message, which requires to save in .txt file.
     * @return Nothing
     */
    private fun appendLog(text: String) {
        val logFile = File(Environment.getExternalStorageDirectory().toString() + "/VLPL.txt")
        if (!logFile.exists())
            try {
                logFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        try {
            // BufferedWriter for performance, true to set append to file flag
            val buf = BufferedWriter(FileWriter(logFile, true))
            buf.append(text)
            buf.newLine()
            buf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}