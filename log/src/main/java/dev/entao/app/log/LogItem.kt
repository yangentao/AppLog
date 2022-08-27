package dev.entao.app.log

import java.text.SimpleDateFormat
import java.util.*


@Suppress("MemberVisibilityCanBePrivate")
class LogItem(val level: LogLevel, val items: List<Any?>) {
    val time: Long = System.currentTimeMillis()
    val threadId: Long = Thread.currentThread().id
    val itemsText: String by lazy {
        items.joinToString(" ") {
            AppLog.toLogText(it)
        }
    }
    val logText: String by lazy {
        val sb = StringBuilder(itemsText.length + 64)
        val ff = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val date = ff.format(Date(time))
        sb.append(date)
        sb.append(String.format(Locale.getDefault(), "%6d %c: ", threadId, level.name.first()))
        sb.append(itemsText)
        sb.toString()
    }

}