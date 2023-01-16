import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios'
import mitt from 'mitt' //eventhub的代替
import Toaster from '@meforma/vue-toaster'

// 样式文件
import "./assets/sass/main.sass"

// bootstrap
import "./assets/css/bootstrap-grid.min.css"
import "./assets/css/bootstrap-utilities.css"
// iconfont
import "./assets/css/iconfont.css"

// 表单验证
import * as validators from '@/assets/js/validators.js'

// 常量
import constants from '@/assets/js/constants.js'

// 将Modal作为全局组件
import Modal from '@/components/Modal'

// md5加密函数
import { md5 } from '@/assets/js/md5.js'

// 时间相关的一些操作函数
import * as timeTool from '@/assets/js/timeUtils.js'

// dayjs
import dayjs from 'dayjs'
const isYesterday = require('dayjs/plugin/isYesterday')
const weekday = require('dayjs/plugin/weekday')
dayjs.extend(weekday)
const customParseFormat = require('dayjs/plugin/customParseFormat')
dayjs.extend(customParseFormat)
var utc = require('dayjs/plugin/utc') // dependent on utc plugin
var timezone = require('dayjs/plugin/timezone')
dayjs.extend(utc)
dayjs.extend(timezone)
dayjs.extend(isYesterday)

// element ui
import 'element-plus/lib/theme-chalk/index.css'
import "./assets/css/elementui-override.css"
import { locale, ElForm, ElFormItem, ElInput, ElButton, ElUpload, ElSelect, ElOption, ElRadioButton, ElRadioGroup, ElColorPicker, ElDropdown, ElDatePicker, ElDropdownMenu, ElDropdownItem, ElTooltip, ElPopover } from 'element-plus'
// 让element ui显示中文
import lang from 'element-plus/lib/locale/lang/zh-cn'
locale(lang)

// 解决safari无法smooth scroll的问题
import smoothscroll from 'smoothscroll-polyfill';
smoothscroll.polyfill();

// 导入enum
import enumInfo from './assets/js/enums.js'

// axios.defaults.baseURL = 'http://127.0.0.1:8080/api/v1/'
axios.defaults.baseURL = process.env.VUE_APP_SERVER + '/api/v1/'

// http request拦截器
axios.interceptors.request.use(config => {
    // 发送请求前做的事情
    if (store.getters.token) {
        config.headers.Authorization = `Bearer ${store.getters.token}`
    }
    return config
})

// http response拦截器
axios.interceptors.response.use(res => {
    return res
}, err => {
    console.log(err)
    if (!err.response) {
        app.$toast.error('您已断开网络连接！')
            //net::ERR_CONNECTION_REFUSED
    } else if (401 == err.response.status) {
        // 防止提示消息出现多次
        if (window.sessionStorage.getItem('hasLogout') == null) {
            sessionStorage.setItem('hasLogout', true); //登出标记
            app.$toast.error('没有权限或登录状态已失效！')
            setTimeout(() => {
                store.dispatch('logout')
                router.push('/login')
            }, 1000)

        }
    } else if (500 === err.response.status) {
        app.$toast.error('发生错误！')
    } else if (404 === err.response.status) {
        router.push('/404')
    }
    return Promise.reject(err.response.data)
})
export default axios

const app = createApp(App)

const emitter = mitt()

app.config.globalProperties.$emitter = emitter
app.config.globalProperties.$dayjs = dayjs
app.config.globalProperties.$timeTool = timeTool
app.config.globalProperties.$validator = validators
app.config.globalProperties.$const = constants
app.config.globalProperties.$enum = enumInfo
app.config.globalProperties.md5 = md5

app.use(Toaster, {
    duration: 2000
})

app.component('Modal', Modal)
app.use(router)

app.use(ElForm).use(ElFormItem).use(ElInput).use(ElButton).use(ElUpload).use(ElSelect).use(ElOption).use(ElRadioButton).use(ElRadioGroup).use(ElColorPicker).use(ElDropdownMenu).use(ElDropdownItem).use(ElDatePicker).use(ElDropdown).use(ElTooltip).use(ElPopover)
app.use(store).mount('#app')