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

/** 获取一天的开始时间(00:00:00)  */
fun Date.start(): Long {
    val calendar = calendar(this)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    return calendar.timeInMillis
}

/** 获取一天的结束时间(23:59:59)  */
fun Date.end(): Long {
    val calendar = calendar(this)
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    return calendar.timeInMillis
}

/** 日期加减计算  */
fun Long.add(offset: Int, filed: Int = Calendar.DAY_OF_MONTH): Long {
    val calendar = calendar(this)
    calendar.add(filed, offset)
    return calendar.timeInMillis
}

/** 日期加减计算  */
fun Date.add(offset: Int, filed: Int = Calendar.DAY_OF_MONTH): Long {
    val calendar = calendar(this)
    calendar.add(filed, offset)
    return calendar.timeInMillis
}

/** 获取时间，精确到分钟(秒重置为0) */
fun Long.accurateToMinute(): Long {
    val calendar = calendar(this)
    calendar.set(Calendar.SECOND, 0)
    return calendar.timeInMillis
}

/**
 * 根据日期获得星期
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
 * 格式化秒
 * > 示例：01:56:30
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
 * 格式化
 * @receiver Date
 * @return String
 */
fun Date.format(): String {
    val calendar = Calendar.getInstance()
    //当前年
    val currYear = calendar[Calendar.YEAR]
    //当前日
    val currDay = calendar[Calendar.DAY_OF_YEAR]
    //当前时
    val currHour = calendar[Calendar.HOUR_OF_DAY]
    //当前分
    val currMinute = calendar[Calendar.MINUTE]
    //当前秒
    val currSecond = calendar[Calendar.SECOND]
    calendar.time = this
    val msgYear = calendar[Calendar.YEAR]
    //说明不是同一年
    if (currYear != msgYear) {
        return SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).format(date)
    }
    val msgDay = calendar[Calendar.DAY_OF_YEAR]
    //超过7天，直接显示xx月xx日
    if (currDay - msgDay > 7) {
        return SimpleDateFormat("MM月dd日", Locale.getDefault()).format(date)
    }
    //不是当天
    if (currDay - msgDay > 0) {
        return if (currDay - msgDay == 1) "昨天"
        else "${currDay - msgDay}天前"
    }
    val msgHour = calendar[Calendar.HOUR_OF_DAY]
    val msgMinute = calendar[Calendar.MINUTE]
    //不是当前小时内
    if (currHour - msgHour > 0) {
        //如果当前分钟小，说明最后一个不满一小时
        return if (currMinute < msgMinute) {
            if (currHour - msgHour == 1) { //当前只大一个小时值，说明不够一小时
                "${60 - msgMinute + currMinute}分钟前"
            } else {
                "${currHour - msgHour - 1}小时前"
            }
        } else "${currHour - msgHour}小时前"
        //如果当前分钟数大，够了一个周期
    }
    val msgSecond = calendar[Calendar.SECOND]
    //不是当前分钟内
    return if (currMinute - msgMinute > 0) {
        //如果当前秒数小，说明最后一个不满一分钟
        if (currSecond < msgSecond) {
            if (currMinute - msgMinute == 1) { //当前只大一个分钟值，说明不够一分钟
                "刚刚"
            } else {
                "${currMinute - msgMinute - 1}分钟前"
            }
        } else "${currMinute - msgMinute}分钟前"
        //如果当前秒数大，够了一个周期
    } else "刚刚"
    //x秒前
}

/**
 *@param date 日期 2020-05-15
 *@return String 星座
 */

fun getConstellation(date: String): String {
    val s = date.replace("-", "").substring(4, 8).toInt()
    LogCat.e(s)
    return when (s) {
        in 321..420 -> "白羊座"
        in 421..521 -> "金牛座"
        in 522..621 -> "双子座"
        in 622..722 -> "巨蟹座"
        in 723..823 -> "狮子座"
        in 824..923 -> "处女座"
        in 924..1023 -> "天秤座"
        in 1024..1122 -> "天蝎座"
        in 1123..1221 -> "射手座"
        in 1222..1231 -> "摩羯座"
        in 101..120 -> "摩羯座"
        in 121..219 -> "水瓶座"
        in 220..320 -> "双鱼座"
        else -> "未知"
    }
}

/**
*@param date 日期 2020-05-15
*@return int 年龄
*/
fun getAge(date: String): Int {
    val calendar = Calendar.getInstance()//取得当前时间的年月日 时分秒
    val year = calendar[Calendar.YEAR]
    val substring = date.substring(0, 4).toInt()
    return year - substring
}