import dayjs from 'dayjs'
const weekday = require('dayjs/plugin/weekday')
const utc = require('dayjs/plugin/utc')
const timezone = require('dayjs/plugin/timezone')
const customParseFormat = require('dayjs/plugin/customParseFormat')
const isLeapYear = require('dayjs/plugin/isLeapYear')
const dayOfYear = require('dayjs/plugin/dayOfYear')
const isSameOrBefore = require('dayjs/plugin/isSameOrBefore')
const isSameOrAfter = require('dayjs/plugin/isSameOrAfter')
const duration = require('dayjs/plugin/duration')


dayjs.extend(duration)
dayjs.extend(weekday)
dayjs.extend(utc)
dayjs.extend(timezone)
dayjs.extend(customParseFormat)
dayjs.extend(isLeapYear)
dayjs.extend(dayOfYear)
dayjs.extend(isSameOrAfter)
dayjs.extend(isSameOrBefore)

let userTimezone = ''



export function getYMDStrings(date) {
    let s = date.split('-')
    return {
        y: s[0],
        m: s[1],
        d: s[2]
    }
}

export function compareDate(d1, d2) {
    // d1在前返回-1
    d1 = dayjs(d1)
    d2 = dayjs(d2)
    if (!d1.isSameOrAfter(d2)) {
        return -1
    } else if (!d1.isSameOrBefore(d2)) {
        return 1
    } else {
        return 0
    }
}

export function compareTime(t1, t2) {
    // d1在前返回-1
    t1 = dayjs(t1)
    t2 = dayjs(t2)
    if (!t1.isAfter(t2, 'second')) {
        return -1
    } else {
        return 1
    }
}


// 检查字符串是否是合法时间形式
function validateDate(s, format) {
    return dayjs(s).format(format) === s
}

// 检查是否是ymd形式字符串
export function isYMDformat(s) {
    return validateDate(s, 'YYYY-MM-DD')
}

export function config(tz) {
    userTimezone = tz
}

// 获取当前时刻
export function getCurrUTCTime() {
    return dayjs().utc()
}
// 获取当前用户时区的时刻
export function getCurrUserTime() {
    return dayjs(getCurrUTCTime()).tz(userTimezone)
}
// 将某字符串转化为用户当前时区时刻
export function convertToUserTime(s) {
    return dayjs(s).tz(userTimezone)
}

//获取指定日期的用户时区形式
export function parseYMD(t) {
    // config ?
    const time = dayjs(dayjs(t).tz(userTimezone))
    return formatInYMD(time)
}

//把年月日和时刻转换为utc时间
export function convertTMDHMtoUTC(t) {
    return dayjs(dayjs(t).tz(userTimezone)).utc().format()
}

// 把年月日和时刻转化为utc时间毫秒形式
export function convertTMDHMtoUTCMill(t) {
    return dayjs(dayjs(t).tz(userTimezone)).utc().valueOf()
}

// 返回24或12小时制的钟表时间
export function formatInClockTime(format, t) {
    if (!t) {
        t = getCurrUserTime()
    } else {
        t = dayjs(t)
    }
    return format === 'HOUR24' ?
        t.format('HH:mm') : t.format('hh:mm A')
}

// 返回类似2020-01-01的形式
export function formatInYMD(t) {
    return dayjs(t).format('YYYY-MM-DD')
}

/**
 * 将传入的用户输入的时间变成06:03这样的形式
 * @param {*} t 传入的是xx:xx这样的形式，但里面可能包含非数字
 * @param {*} needCheckValidity
 * @returns
 */
export function formatInHourMinS(t, needCheckValidity) {
    console.log('format task')
        // 如果传入数据不确定有效性，
    if (needCheckValidity) {
        if (!t.includes(':')) {
            return '00:00'
        }
        let hourAndMin = t.split(':')
        console.log(hourAndMin)
        for (let i = 0; i < 2; i++) {
            hourAndMin[i] = hourAndMin[i].trim()
            if (hourAndMin[i] === '') {
                hourAndMin[i] = '00'
            }
            // 去掉每部分的非数字，如果只有一位数，在前面补0
            hourAndMin[i] = hourAndMin[i].replace(/[^0-9]+/g, '')
            console.log(hourAndMin[i])
            if (hourAndMin[i].length === 1) {
                hourAndMin[i] = '0' + hourAndMin[i]
            }
        }
        t = hourAndMin[0] + ':' + hourAndMin[1]
        console.log(t)

        // 前面加上日期以便它能够转化为dayjs obj
        t = '2020-01-01-' + t
    }

    // 让dayjs帮我们完成其余的有效性转化任务，例如25:00转为1:00
    return dayjs(t).format('HH:mm')
}

/**
 * 获取时间银行相关数据
 * @returns 年和月总共和已过的天数
 */
export function getTimeBankData() {
    let t = getCurrUserTime()
    return {
        year: {
            passed: t.dayOfYear(),
            total: t.isLeapYear() ? 366 : 365
        },
        month: {
            passed: t.date(),
            total: t.daysInMonth()
        },
    }
}

/**
 * 获取用户时区
 * @returns 用户时区
 */
export function guessUserTz() {
    return dayjs.tz.guess()
}



// 将毫秒转为时分秒格式 obj
export function formatDuration(ms, returnObj) {
    const dur = dayjs.duration(ms)
    const hour = dur.hours()
    const min = dur.minutes()
    const sec = dur.seconds()

    if (!returnObj) {
        return `${hour}h ${min}m ${sec}s`
    }
    return {
        h: hour < 10 ? '0' + hour : hour,
        m: min < 10 ? '0' + min : min,
        s: sec < 10 ? '0' + sec : sec
    }

}

export function isSameDay(a, b) {
    return dayjs(a).isSame(b, 'date')
}


export function subtractTime(s1, s2) {
    const f = 'HH:mm'
    const t1 = dayjs(s1, f)
    const t2 = dayjs(s2, f)
    dayjs.duaration(t1.diff(t2)).format('HH:mm')
}

// 获得7天的日期
export function getWeekDays(first, last, middle) {
    if (middle) {
        first = dayjs(middle).startOf('week')
    }
    if (last) {
        first = dayjs(last).weekday(-7)
    }

    const dates = []
    for (let i = 0; i < 7; i++) {
        dates.push(first)
        first = dayjs(first).add(1, 'day')
    }
    return dates

}

export function getWeekRange(firstWeekDay, operation, onlyFirstDay) {
    // 如果提供了一周第一天的日期，则在这个日期的基础上加减7天，
    if (firstWeekDay) {
        if (operation === 'add') {
            firstWeekDay = dayjs(firstWeekDay).weekday(7)
        } else if (operation === 'sub') {
            firstWeekDay = dayjs(firstWeekDay).weekday(-7)
        } else {
            firstWeekDay = dayjs(firstWeekDay)
        }
    }

    if (!firstWeekDay) firstWeekDay = dayjs(getCurrUserTime()).startOf('week')
        //将星期每一天的年月日放入res数组
    const res = []

    for (let i = 7; i > 0; i--) {
        // 只需要一周第一天的日期的时候
        if (onlyFirstDay && i < 7) {
            return res
        }
        res.push(formatInYMD(dayjs(firstWeekDay)))
        firstWeekDay = dayjs(firstWeekDay).add(1, 'day')

    }
    return res
}


export function convertAndFormatDateAndTime(s, format) {

    return format === 'HOUR24' ? dayjs(s).tz(userTimezone).format('YYYY-MM-DD HH:mm') :
        dayjs(s).tz(userTimezone).format('YYYY-MM-DD hh:mmA')

}

export function getWeekDay(s) {
    const days = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];
    const dayIndex = dayjs(s).get('day')
    return days[dayIndex]
}
export function getSameDayOfLastYear() {
    const date = getCurrUserTime().subtract(1, 'year')
    return formatInYMD(date)
}


export function convertStrToMill(s) {
    const time = dayjs(s).utc()
    return time.valueOf()
}


// 返回当前时刻utc时间的毫秒表示
export function currUtcMill() {
    return dayjs().utc().valueOf()
}