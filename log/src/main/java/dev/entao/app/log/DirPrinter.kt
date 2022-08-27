@file:Suppress("MemberVisibilityCanBePrivate")

package dev.entao.app.log

import android.content.Context
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by entaoyang@163.com on 2016-10-28.
 */

class DirPrinter(val logdir: File, val keepDays: Int = 30) : XLogPrinter {
    private val reg = Regex("\\d{4}-\\d{2}-\\d{2}.log")

    private var writer: BufferedWriter? = null
    private var dateStr: String = ""

    init {
        if (!logdir.exists()) {
            logdir.mkdirs()
        }
    }

    private fun checkWriter(): BufferedWriter? {
        val ff = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val ds = ff.format(Date(System.currentTimeMillis()))
        if (ds == dateStr) {
            return makeSureWriter(ds)
        }
        writer?.flush()
        writer?.close()
        writer = null

        dateStr = ds
        deleteOldLogs(keepDays)
        return makeSureWriter(ds)
    }

    private fun makeSureWriter(dateText: String): BufferedWriter? {
        if (writer == null) {
            try {
                writer = BufferedWriter(FileWriter(File(logdir, "$dateText.log"), true), 20 * 1024)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        return writer
    }

    fun listLogFiles(): List<File> {
        return logdir.listFiles()?.filter { it.name.matches(reg) }?.sortedByDescending { it.name } ?: emptyList()
    }


    private fun deleteOldLogs(days: Int) {
        val ls = listLogFiles()
        val dayBefore = System.currentTimeMillis() - days * 24 * 3600 * 1000
        for (f in ls) {
            if (f.lastModified() < dayBefore) {
                f.delete()
            }
        }
    }

    override fun uninstall() {
        writer?.flush()
        writer?.close()
        writer = null
    }

    override fun printLog(item: LogItem) {
        val w = checkWriter() ?: return
        val s = item.logText
        try {
            w.write(s)
            w.write("\n")
            w.flush()
        } catch (e: IOException) {
            e.printStackTrace()
            writer?.close()
            writer = null
        }
    }

    override fun flush() {
        try {
            writer?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    companion object {
        fun create(context: Context, dirName: String = "xlog"): DirPrinter {
            val ld = File(context.externalCacheDir ?: context.cacheDir, dirName)
            if (!ld.exists()) {
                ld.mkdir()
            }
            return DirPrinter(ld)
        }
    }

}