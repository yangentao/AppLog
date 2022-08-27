@file:Suppress("unused")

package dev.entao.app.log

import android.util.Log

interface XLogPrinter {
    fun uninstall() {}
    fun flush() {}
    fun printLog(item: LogItem)
}


object LogcatPrinter : XLogPrinter {
    var tagName = "xlog"

    override fun printLog(item: LogItem) {
        var n = item.level.n
        if (n < Log.VERBOSE) {
            n = Log.VERBOSE
        }
        val msg = item.itemsText
        var from = 0
        while (from + 1000 < msg.length) {
            Log.println(n, tagName, msg.substring(from, from + 1000))
            from += 1000
        }
        if (from < msg.length) {
            Log.println(n, tagName, msg.substring(from))
        }
    }
}

object ConsolePrinter : XLogPrinter {

    override fun printLog(item: LogItem) {
        var s = item.logText
        if (s.isEmpty()) {
            println()
            return
        }
        while (s.length > 1024) {
            println(s.substring(0, 1024))
            s = s.substring(1024)
        }
        if (s.isNotEmpty()) {
            println(s)
        }
    }

}

class TreePrinter(vararg ps: XLogPrinter) : XLogPrinter {
    private val printers = ArrayList<XLogPrinter>()

    init {
        printers += ps
    }


    override fun uninstall() {
        for (p in printers) {
            p.uninstall()
        }
        super.uninstall()
    }

    fun clear() {
        printers.forEach {
            it.flush()
            it.uninstall()
        }
        printers.clear()
    }

    fun add(p: XLogPrinter) {
        printers.add(p)

    }

    fun remove(p: XLogPrinter) {
        printers.remove(p)
        p.flush()
        p.uninstall()
    }

    fun remove(acceptor: (XLogPrinter) -> Boolean) {
        val ls = printers.filter(acceptor)
        for (p in ls) {
            printers.remove(p)
        }
    }

    override fun flush() {
        printers.forEach { it.flush() }
    }


    override fun printLog(item: LogItem) {
        printers.forEach { it.printLog(item) }
    }

}