package dev.entao.app.log

/**
 * Created by yangentao on 2015/11/21.
 * entaoyang@163.com
 */

fun logv(vararg args: Any?) {
    AppLog.v(*args)
}

fun logd(vararg args: Any?) {
    AppLog.d(*args)
}

fun logi(vararg args: Any?) {
    AppLog.i(*args)
}

fun logw(vararg args: Any?) {
    AppLog.w(*args)
}

fun loge(vararg args: Any?) {
    AppLog.e(*args)

}

