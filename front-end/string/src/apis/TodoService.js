import axios from '@/main.js'
const URL = '/user/todos'

export default {
    // 读取当月有待办的天数
    readMonthHistory(userId, y, m) {
        return axios.get(`${URL}/memo/month/${userId}?y=${y}&m=${m}`)
            .then(response => response.data)
    },
    // 新增todo
    add(todo, userId) {
        return axios.post(`${URL}/add/${userId}`, todo)
            .then(response => response.data)
    },
    // 列出某用户某日的所有todo
    list(userId, date) {
        return axios.get(`${URL}/list/${userId}?date=${date}`)
            .then(response => response.data)
    },
    // 将某个todo设置为已完成状态
    markDone(userId, id, y, m, d) {
        return axios.post(`${URL}/done/${userId}?id=${id}&y=${y}&m=${m}&d=${d}`)
            .then(response => response.data)
    },
    // 删除todo
    del(userId, id, y, m, d) {
        return axios.delete(`${URL}/del/${userId}?id=${id}&y=${y}&m=${m}&d=${d}`)
            .then(response => response.data)
    },
    // 更新todo
    update(todo, userId) {
        return axios.post(`${URL}/edit/${userId}`, todo)
            .then(response => response.data)
    },

    // 调整todo顺序
    reorder(userId, destIndex, todo) {
        return axios.post(`${URL}/reorder/${userId}/${destIndex}`, todo)
            .then(response => response.data)
    }

}