import axios from '@/main.js'
const URL = '/user/timeentry'

export default {
    // 列出时间分类和对应累计时间
    listdur(userId) {
        return axios.get(`${URL}/durs/${userId}`)
            .then(response => response.data)
    },
    // 增加时间条目
    add(userId, entry, isManual) {
        return axios.post(`${URL}/add/${userId}?manual=${isManual}`, entry)
            .then(response => response.data)
    },
    // 列出指定一周的时间条目
    list(userId, weekStart, weekEnd, tz, showShortEntry) {
        return axios.get(`${URL}/list/${userId}?start=${weekStart}&end=${weekEnd}&tz=${tz}&all=${showShortEntry}`)
            .then(response => response.data)
    },
    // 更新时间条目
    update(userId, entry) {
        return axios.post(`${URL}/update/${userId}`, entry)
            .then(response => response.data)
    },
    // 删除时间条目
    del(userId, id) {
        return axios.delete(`${URL}/del/${userId}?id=${id}`)
            .then(response => response.data)
    },
    // 读取正在计时中的时间条目
    readRunningEntry(userId) {
        return axios.get(`${URL}/read/${userId}`)
            .then(response => response.data)
    },
    // 停止正在计时的时间条目
    stopTimer(userId, id, end) {
        return axios.post(`${URL}/timer-stop/${userId}?id=${id}&end=${end}`)
            .then(response => response.data)
    }
}