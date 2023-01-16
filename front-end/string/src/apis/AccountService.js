import axios from '@/main.js'
const URL = '/account'

export default {
    // 登录
    login(user) {
        return axios.post(URL + '/login', user)
            .then(response => response.data)
    },
    //注册
    register(user) {
        return axios.post(URL + '/register', user)
            .then(response => response.data)
    },
    // 发送短信验证码
    sendCode(sms){
      return axios.post(URL + '/sms/send', sms)
            .then(response => response.data)
    },

    // 更新
    update(user,id) {
        return axios.post(URL + `/update/${id}/`, user)
            .then(response => response.data)
    },


    // 登出
    logout(user){
        return axios.post(URL + '/logout', user)
          .then(response => response.data)
    },

    // 重置密码
    resetPassword(userId, pswds){
      return axios.post(URL + `/resetpswd/${userId}`, pswds)
          .then(response => response.data)
    },

    // 忘记密码时重置密码
  resetPasswordForgot(resetPwdForgot){
      return axios.post(URL + `/resetpswd-forgot/`, resetPwdForgot)
         .then(response => response.data)
  }
}
