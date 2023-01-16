import axios from '@/main.js'
const URL = '/user/notification'

export default {
    // 查看用户是否有未读通知（有的话小铃铛显示提示红点）
    check(userId) {
        return axios.get(`${URL}/check/${userId}`)
            .then(response => response.data)
    },
    // 列出用户所有通知
    list(userId) {
        return axios.get(`${URL}/list/${userId}`)
            .then(response => response.data)
    },
    // 将通知标记为已读
    setRead(id, userId) {
        return axios.post(`${URL}/read/${userId}/${id}`)
            .then(response => response.data)
    },
    // 删除一条通知
    del(id, userId) {
        return axios.delete(`${URL}/del/${userId}/${id}`)
            .then(response => response.data)
    },
    // 删除当前用户所有通知
    delAll(userId) {
        return axios.delete(`${URL}/delAll/${userId}`)
            .then(response => response.data)
    }
}