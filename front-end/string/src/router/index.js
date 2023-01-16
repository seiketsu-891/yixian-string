import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'

// 游客首页和相关链接
const LandingPage = () =>
    import ('@/views/guest/LandingPage')
const UIResources = () =>
    import ('@/views/more/UIResources')
import More from '@/views/more/More'

// 账号相关
const Account = () =>
    import ('@/views/guest/Account')
const Register = () =>
    import ('@/views/guest/Register')
const ForgotPassword = () =>
    import ('@/views/guest/ForgotPassword')
const Login = () =>
    import ('@/views/guest/Login')

//登入后基本页面相关
const User = () =>
    import ('@/views/user/User')
const UserHome = () =>
    import ('@/views/user/Home')
const TimeReport = () =>
    import ('@/views/user/TimeReport')
const ManageCat = () =>
    import ('@/views/user/ManageCat')
const Todos = () =>
    import ('@/views/user/Todos')
const GridDiary = () =>
    import ('@/views/user/GridDiary')
const Settings = () =>
    import ('@/views/user/Settings')

// 404页面
const PageNotFound = () =>
    import ('@/views/error/PageNotFound')


const routes = [
    { name: 'landingPage', path: '/index', component: LandingPage, meta: { LoggedUserVisitable: false } },

    {
        path: '/more',
        component: More,
        children: [
            { path: 'ui-resources', component: UIResources, alias: '/ui-resources' }
        ]
    },
    {
        path: '/account',
        component: Account,
        children: [
            { path: 'register', name: 'register', component: Register, alias: '/register', meta: { LoggedUserVisitable: false } },
            { path: 'login', name: 'login', component: Login, alias: '/login', meta: { LoggedUserVisitable: false } },
            { path: 'forgotpassword', name: 'forgotpassword', component: ForgotPassword, alias: '/forgotpassword', meta: { LoggedUserVisitable: false } },
        ]
    },

    {
        path: '/',
        name: 'user',
        component: User,
        redirect: '/home',
        meta: { requireAuth: true },
        children: [
            { path: 'home', name: 'userhome', component: UserHome, },
            { path: 'todos/:date?', component: Todos, name: 'todos', props: true },
            { path: 'griddiary/:date?', name: 'griddiary', component: GridDiary },
            { path: 'timereport/:from?/:to?', name: 'timereport', component: TimeReport },
            { path: 'settings', component: Settings, name: 'settings' },
            { path: 'entrycategory', component: ManageCat }

        ]
    },
    {
        path: '/404',
        component: PageNotFound,
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

router.beforeEach((to, from, next) => {
    // 没有匹配的路由则转向404
    if (!to.matched.length) {
        window.location = '/404'
        return
    }
    // 判定权限
    let isAuthenticated = store.getters.isLogged
    if (to.matched.some(record => record.meta.requireAuth) && !isAuthenticated) {
        if (to.path === '/home') {
            next({ name: 'landingPage' })
        } else {
            next({ path: '/login' })
        }

    } else if (to.matched.some(record => record.meta.LoggedUserVisitable === false) && isAuthenticated) {
        // 已登录用户不可访问注册、登录、找回密码已经landing page，会直接跳转到登录后主页
        next({ path: '/home' })
    } else {
        next()
    }
})



export default router