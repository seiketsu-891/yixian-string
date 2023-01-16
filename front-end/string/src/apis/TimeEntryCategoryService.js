import axios from '@/main.js'
const URL = '/user/entrycat'

export default {
    // 增加分类
    add(userId, cat) {
        return axios.post(`${URL}/add/${userId}`, cat)
            .then(response => response.data)
    },
    // 列出所有分类
    list(userId) {
        return axios.get(`${URL}/list/${userId}`)
            .then(response => response.data)
    },
    // 更新分类
    update(userId, cat) {
        return axios.post(`${URL}/update/${userId}`, cat)
            .then(response => response.data)
    },
    // 删除分类
    del(userId, id) {
        return axios.delete(`${URL}/del/${userId}/${id}`)
            .then(response => response.data)
    },
}