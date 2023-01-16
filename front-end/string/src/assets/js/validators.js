import * as RegExp from './regExp.js'

// element ui 表单验证用
export const checkUsername = (rule, value, cb) => {
    if (value.trim() === '') {
        cb(new Error('用户名不能为空'))
    } else if (value.length < 3 || value.length > 16) {
        cb(new Error('用户名长度应为3到16位'))
    } else if (!RegExp.USERNAME_PATTERN.test(value)) {
        cb(new Error('用户名包含非法字符'))
    } else {
        cb()
    }
}

export const checkPassword = (rule, value, cb) => {
    if (value.trim() === '') {
        cb(new Error('密码不能为空'))
    } else if (!RegExp.PASSWORD_PATTERN.test(value)) {
        cb(new Error('密码格式不正确'))
    } else {
        cb()
    }
}


export const checkPhoneNumber = (rule, value, cb) => {
    if (value.trim() === '') {
        cb(new Error('手机号不能为空'))
    } else if (!RegExp.MOBILE_PATTERN.test(value)) {
        cb(new Error('手机号格式非法'))
    } else {
        cb()
    }
}




export const checkTodo = (rule, value, cb) => {
    if (!value || value.trim() === '') {
        cb(new Error('代办名称不能为空'))
    } else {
        cb()
    }

}

export const checkNotEmpty = (rule, value, cb) => {
    if (!value || value.trim() === '') {
        cb(new Error('该项不能为空'))
    } else {
        cb()
    }
}