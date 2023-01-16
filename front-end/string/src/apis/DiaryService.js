import axios from '@/main.js'
const URL = "/user/grid-diary"

export default {
    // ---- 日记内容---------
    // 保存日记
    saveDiary(diariesInfo, userId, date) {
        return axios.post(`${URL}/add/${userId}?date=${date}`, diariesInfo)
            .then(response => response.data)
    },
    // 读取当月有日期的天数
    readMonthHistory(userId, y, m) {
        return axios.get(`${URL}/history/month/${userId}?y=${y}&m=${m}`)
            .then(response => response.data)
    },
    // 读取当天历史聊天记录（日记已完成时）
    getHistoryDiaglos(userId, date) {
        return axios.get(`${URL}/history-dialogs/${userId}?date=${date}`)
            .then(response => response.data)
    },
    // 读取某用户某天的日记
    list(userId, date) {
        return axios.get(`${URL}/list/${userId}?date=${date}`)
            .then(response => response.data)
    },

    // -----日记问题 ------------
    // 新增问题
    addQues(userId, ques) {
        return axios.post(`${URL}/ques/add/${userId}`, ques)
            .then(response => response.data)
    },
    // 列出用户的问题
    listInUseQues(userId) {
        return axios.get(`${URL}/ques/list/${userId}`)
            .then(response => response.data)
    },
    // 删除问题
    delQues(userId, id) {
        return axios.delete(`${URL}/ques/del/${userId}?id=${id}`)
            .then(response => response.data)
    },

    // -----  answer --------
    // 更新日记回答
    updateAnsContent(userId, diary) {
        return axios.post(`${URL}/ans/update/${userId}`, diary)
            .then(response => response.data)
    },

    // 删除日记某一格子
    delAns(userId, id, date) {
        return axios.delete(`${URL}/del/${userId}?id=${id}&date=${date}`)
            .then(response => response.data)
    }
}