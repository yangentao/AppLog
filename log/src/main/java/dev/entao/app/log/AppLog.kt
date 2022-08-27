@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package dev.entao.app.log

import android.content.Context
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.collections.ArrayList

/**
 * Created by entaoyang@163.com on 2018/11/8.
 */
enum class LogLevel(val n: Int) {
    DISABLE(0), VERBOSE(2), DEBUG(3), INFO(4), WARN(5), ERROR(6);

    fun ge(level: LogLevel): Boolean {
        return this.ordinal >= level.ordinal
    }
}

interface LogValueFormatter {
    fun accept(value: Any): Boolean
    fun format(value: Any): String
}

object AppLog {
    private val formatList: ArrayList<LogValueFormatter> = ArrayList()
    val printer: TreePrinter = TreePrinter(LogcatPrinter)
    var level = LogLevel.VERBOSE

    fun addDefaultDirPrinter(context: Context, dirName: String = "xlog") {
        printer.add(DirPrinter.create(context, dirName))
    }

    fun addPrinter(p: XLogPrinter) {
        printer.add(p)
    }

    fun flush() {
        printer.flush()
    }

    fun v(vararg args: Any?) {
        printItem(LogItem(LogLevel.VERBOSE, args.toList()))
    }

    fun d(vararg args: Any?) {
        printItem(LogItem(LogLevel.DEBUG, args.toList()))
    }

    fun w(vararg args: Any?) {
        printItem(LogItem(LogLevel.WARN, args.toList()))
    }

    fun e(vararg args: Any?) {
        printItem(LogItem(LogLevel.ERROR, args.toList()))
        printer.flush()
    }

    fun i(vararg args: Any?) {
        printItem(LogItem(LogLevel.INFO, args.toList()))
    }


    fun fatal(vararg args: Any?) {
        e(*args)
        throw RuntimeException("fatal error!")
    }

    fun printItem(item: LogItem) {
        if (level == LogLevel.DISABLE) {
            return
        }
        if (item.level.ordinal < level.ordinal) {
            return
        }

        printer.printLog(item)
    }

    fun addFormatter(formatter: LogValueFormatter) {
        formatList += formatter
    }

    fun toLogText(obj: Any?): String {
        if (obj == null) {
            return "null"
        }
        if (obj is String) {
            return obj
        }
        if (obj.javaClass.isPrimitive) {
            return obj.toString()
        }
        for (f in formatList) {
            if (f.accept(obj)) {
                return f.format(obj)
            }
        }

        if (obj is Throwable) {
            val sw = StringWriter(512)
            val pw = PrintWriter(sw)
            obj.printStackTrace(pw)
            return sw.toString()
        }
//        if (obj is YsonValue) {
//            return obj.toString()
//        }
        if (obj is Array<*>) {
            val s = obj.joinToString(",") { toLogText(it) }
            return "ARRAY[$s]"
        }
        if (obj is List<*>) {
            val s = obj.joinToString(", ") { toLogText(it) }
            return "LIST[$s]"
        }
        if (obj is Map<*, *>) {
            val s = obj.map { "${toLogText(it.key)} = ${toLogText(it.value)}" }.joinToString(",")
            return "MAP{$s}"
        }
        if (obj is Iterable<*>) {
            val s = obj.joinToString(", ") { toLogText(it) }
            return "ITERABLE[$s]"
        }
        return obj.toString()
    }
}


