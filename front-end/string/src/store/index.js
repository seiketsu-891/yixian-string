import { createStore } from 'vuex'
import VuexPersistence from 'vuex-persist'
// import SecureLS from 'secure-ls'

const LOGIN_INFO_KEY = '/api/v1/me'
    // const SK  = 'tJ15mwOxcRksKoMm88QbOJEiJ14hT0Cb'
    // const ls = new SecureLS({encodingType: 'AES', encryptionSecret: SK})

const vuexLocal = new VuexPersistence({
    key: LOGIN_INFO_KEY
})

export default createStore({
    modules: {},
    state() {
        return {
            loginUser: null,
            token: null,
        }
    },
    getters: {
        isLogged(state) {
            return state.loginUser !== null && state.token !== null
        },
        user(state) {
            return state.loginUser
        },
        token(state) {
            return state.token
        },

    },
    mutations: {
        login(state, { loginUser, token }) {
            state.loginUser = loginUser
            state.token = token
        },
        update(state, user) {
            state.loginUser = user
            window.localStorage.clear()
        },
        clear(state) {
            state.loginUser = null
            state.token = null
        }
    },
    actions: {
        login(context, { loginUser, token }) {
            context.commit('login', { loginUser, token })
        },
        update(context, user) {
            console.log('111')
            context.commit('update', user)

        },
        logout(context) {
            localStorage.clear()
            context.commit('clear')
        }
    },
    plugins: [
        vuexLocal.plugin
    ]
})