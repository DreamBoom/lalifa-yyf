@file:Suppress("unused")

package com.lalifa.extension

import com.drake.logcat.LogCat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Date extensions
 *
 * @author Classic
 * @version v1.0, 2019-05-20 15:48
 */
const val PATTERN_FULL = "yyyy-MM-dd HH:mm:ss"
const val PATTERN_DATE = "yyyy-MM-dd"
const val PATTERN_DATE_POINT = "yyyy.MM.dd"
const val PATTERN_DATE_SIMPLE = "yyyyMMdd"
const val PATTERN_TIME = "HH:mm:ss"
const val PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm"
const val PATTERN_DATE_TIME_POINT = "yyyy.MM.dd HH:mm"
const val PATTERN_DATE_TIME_SHORT = "MM-dd HH:mm"
const val PATTERN_YEAR_MONTH = "yyyyMM"
const val PATTERN_HOUR_MINUTE = "HH:mm"

@Suppress("SpellCheckingInspection")
const val PATTERN_DATE_TIME_SIMPLE = "yyyyMMddHHmmss"

fun calendar(time: Long = System.currentTimeMillis()): Calendar =
    Calendar.getInstance().apply { timeInMillis = time }

fun calendar(date: Date): Calendar = Calendar.getInstance().apply { time = date }
fun calendar(year: Int, month: Int, day: Int): Calendar {
    val calendar = calendar()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month)
    calendar.set(Calendar.DAY_OF_MONTH, day)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    return calendar
}

fun year(): Int = calendar().get(Calendar.YEAR)

/** Month(0-11) */
fun month(): Int = calendar().get(Calendar.MONTH)
fun day(): Int = calendar().get(Calendar.DAY_OF_MONTH)
fun dayOfYear(): Int = calendar().get(Calendar.DAY_OF_YEAR)
fun hour(): Int = calendar().get(Calendar.HOUR_OF_DAY)
fun minute(): Int = calendar().get(Calendar.MINUTE)
fun second(): Int = calendar().get(Calendar.SECOND)
fun week(): Int = calendar().get(Calendar.DAY_OF_WEEK)


fun Calendar.format(pattern: String = PATTERN_DATE_TIME): String {
    return this.time.format(pattern)
}

fun Date.format(pattern: String = PATTERN_DATE_TIME): String =
    SimpleDateFormat(pattern, Locale.CHINA).format(this)

fun Long?.format(pattern: String = PATTERN_DATE_TIME): String {
    if (null == this) return ""
    return when (this.toString().length) {
        13 -> {
            Date(this).format(pattern)
        }
        10 -> {
            Date(this * 1000).format(pattern)
        }
        else -> ""
        // throw IllegalArgumentException("Invalid timestamp. $this")
    }
}

fun String.parse(pattern: String = PATTERN_DATE_TIME): Date = try {
    SimpleDateFormat(pattern, Locale.CHINA).parse(this)!!
} catch (e: Exception) {
    Date(0L)
}

fun String?.convert(sourcePattern: String, newPattern: String): String {
    if (this.isNullOrEmpty()) return ""
    return try {
        val date = this.parse(sourcePattern)
        date.format(newPattern)
    } catch (e: Exception) {
        ""
    }
}

fun String?.toTime(pattern: String = PATTERN_FULL): Long {
    if (this.isNullOrEmpty()) return 0
    return this.parse(pattern).time
}

fun Calendar.isToday() = format(PATTERN_DATE) == calendar().format(PATTERN_DATE)

/** ???????????????????????????(00:00:00)  */
fun Date.start(): Long {
    val calendar = calendar(this)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    return calendar.timeInMillis
}

/** ???????????????????????????(23:59:59)  */
fun Date.end(): Long {
    val calendar = calendar(this)
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    return calendar.timeInMillis
}

/** ??????????????????  */
fun Long.add(offset: Int, filed: Int = Calendar.DAY_OF_MONTH): Long {
    val calendar = calendar(this)
    calendar.add(filed, offset)
    return calendar.timeInMillis
}

/** ??????????????????  */
fun Date.add(offset: Int, filed: Int = Calendar.DAY_OF_MONTH): Long {
    val calendar = calendar(this)
    calendar.add(filed, offset)
    return calendar.timeInMillis
}

/** ??????????????????????????????(????????????0) */
fun Long.accurateToMinute(): Long {
    val calendar = calendar(this)
    calendar.set(Calendar.SECOND, 0)
    return calendar.timeInMillis
}

/**
 * ????????????????????????
 */
fun Long.getDayOfWeek(): Int {
    val calendar = calendar(this)
    var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    if (dayOfWeek < 0) {
        dayOfWeek = 0
    }
    return dayOfWeek
}

const val SECOND_2_MINUTE = 60
const val SECOND_2_HOUR = SECOND_2_MINUTE * 60
const val SECOND_2_DAY = SECOND_2_HOUR * 24

/**
 * ????????????
 * > ?????????01:56:30
 */
fun Int.formatSecond(): String {
    if (this <= 0) return "00:00"
    val hour = if (this >= SECOND_2_HOUR) this / SECOND_2_HOUR else 0
    val minute =
        if (this >= SECOND_2_MINUTE) ((this - hour * SECOND_2_HOUR) / SECOND_2_MINUTE) else 0
    val second =
        if (this < SECOND_2_MINUTE) this else (this - hour * SECOND_2_HOUR - minute * SECOND_2_MINUTE)
    return if (hour > 0) {
        "${hour.fillZero()}:${minute.fillZero()}:${second.fillZero()}"
    } else {
        "${minute.fillZero()}:${second.fillZero()}"
    }
}

fun Long.formatSecond(): String {
    if (this <= 0) return "00:00"
    val hour = if (this >= SECOND_2_HOUR) this / SECOND_2_HOUR else 0
    val minute =
        if (this >= SECOND_2_MINUTE) ((this - hour * SECOND_2_HOUR) / SECOND_2_MINUTE) else 0
    val second =
        if (this < SECOND_2_MINUTE) this else (this - hour * SECOND_2_HOUR - minute * SECOND_2_MINUTE)
    return if (hour > 0) {
        "${hour.fillZero()}:${minute.fillZero()}:${second.fillZero()}"
    } else {
        "${minute.fillZero()}:${second.fillZero()}"
    }
}

fun Long.fillZero() = if (this > 9) this.toString() else "0$this"
fun Int.fillZero() = if (this > 9) this.toString() else "0$this"


class TimeModel {
    var day: Int = 0
    var hour: Int = 0
    var minute: Int = 0
    var second: Int = 0
}

fun Int.convert(): TimeModel {
    val model = TimeModel()
    if (this <= 0) return model
    model.day = this / SECOND_2_DAY
    model.hour = (this - model.day * SECOND_2_DAY) / SECOND_2_HOUR
    model.minute = (this - model.day * SECOND_2_DAY - model.hour * SECOND_2_HOUR) / SECOND_2_MINUTE
    model.second = this % SECOND_2_MINUTE
    return model
}

/*fun Long.format(): String {
    return Date(this).format()
}*/

/**
 * ?????????
 * @receiver Date
 * @return String
 */
fun Date.format(): String {
    val calendar = Calendar.getInstance()
    //?????????
    val currYear = calendar[Calendar.YEAR]
    //?????????
    val currDay = calendar[Calendar.DAY_OF_YEAR]
    //?????????
    val currHour = calendar[Calendar.HOUR_OF_DAY]
    //?????????
    val currMinute = calendar[Calendar.MINUTE]
    //?????????
    val currSecond = calendar[Calendar.SECOND]
    calendar.time = this
    val msgYear = calendar[Calendar.YEAR]
    //?????????????????????
    if (currYear != msgYear) {
        return SimpleDateFormat("yyyy???MM???dd???", Locale.getDefault()).format(date)
    }
    val msgDay = calendar[Calendar.DAY_OF_YEAR]
    //??????7??????????????????xx???xx???
    if (currDay - msgDay > 7) {
        return SimpleDateFormat("MM???dd???", Locale.getDefault()).format(date)
    }
    //????????????
    if (currDay - msgDay > 0) {
        return if (currDay - msgDay == 1) "??????"
        else "${currDay - msgDay}??????"
    }
    val msgHour = calendar[Calendar.HOUR_OF_DAY]
    val msgMinute = calendar[Calendar.MINUTE]
    //?????????????????????
    if (currHour - msgHour > 0) {
        //?????????????????????????????????????????????????????????
        return if (currMinute < msgMinute) {
            if (currHour - msgHour == 1) { //???????????????????????????????????????????????????
                "${60 - msgMinute + currMinute}?????????"
            } else {
                "${currHour - msgHour - 1}?????????"
            }
        } else "${currHour - msgHour}?????????"
        //?????????????????????????????????????????????
    }
    val msgSecond = calendar[Calendar.SECOND]
    //?????????????????????
    return if (currMinute - msgMinute > 0) {
        //?????????????????????????????????????????????????????????
        if (currSecond < msgSecond) {
            if (currMinute - msgMinute == 1) { //???????????????????????????????????????????????????
                "??????"
            } else {
                "${currMinute - msgMinute - 1}?????????"
            }
        } else "${currMinute - msgMinute}?????????"
        //??????????????????????????????????????????
    } else "??????"
    //x??????
}

/**
 *@param date ?????? 2020-05-15
 *@return String ??????
 */

fun getConstellation(date: String): String {
    val s = date.replace("-", "").substring(4, 8).toInt()
    LogCat.e(s)
    return when (s) {
        in 321..420 -> "?????????"
        in 421..521 -> "?????????"
        in 522..621 -> "?????????"
        in 622..722 -> "?????????"
        in 723..823 -> "?????????"
        in 824..923 -> "?????????"
        in 924..1023 -> "?????????"
        in 1024..1122 -> "?????????"
        in 1123..1221 -> "?????????"
        in 1222..1231 -> "?????????"
        in 101..120 -> "?????????"
        in 121..219 -> "?????????"
        in 220..320 -> "?????????"
        else -> "??????"
    }
}

/**
*@param date ?????? 2020-05-15
*@return int ??????
*/
fun getAge(date: String): Int {
    val calendar = Calendar.getInstance()//?????????????????????????????? ?????????
    val year = calendar[Calendar.YEAR]
    val substring = date.substring(0, 4).toInt()
    return year - substring
}