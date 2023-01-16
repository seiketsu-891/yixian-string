<template lang="html">
  <!-- 登录页面 -->
  <div class="form__content">
    <span class="form__title">登录</span>
    <form method="post" autocomplete="off">
      <div class="form__input-container">
        <input v-model="user.phoneNumber" ref="phoneNumberLoginInput"
          class="form__input input-item--border-bottom input-item text--small" type="phoneNumber" name="phoneNumber"
          placeholder="请输入手机号" @focus="$emit('update-input-focused', 0); errors.phoneNumber=''"
          @blur="$emit('update-input-focused', -1)">
        <span class="anim-border-bottom--center anim-border-bottom"></span>
      </div>
      <span class="form__errors"><span v-show="errors.phoneNumber">{{errors.phoneNumber}} </span></span>
      <div class="form__input-container">
        <input v-model="user.password" ref="passwordLoginInput"
          class="form__input input-item--password input-item--border-bottom input-item text--small" type="password"
          name="password" placeholder="请输入密码" @focus="$emit('update-input-focused', 1); errors.password=''"
          @blur="$emit('update-input-focused', -1)">
        <span class="anim-border-bottom--center anim-border-bottom"></span>
      </div>
      <span class="form__errors"><span v-show="errors.password">{{errors.password}}</span></span>
      <div class="form__input-container form__link-forgerpassword">
        <router-link to="/forgotpassword"
          class="text--small-2 link--dark-grey link--underline form__link-forgotpassword">忘记密码？</router-link>
      </div>
      <div class="form__input-container">
        <input type="checkbox" v-model="user.keepLogin" name="keepLogin" class="checkbox-item">
        <span class="checkbox-item__label text--small text--grey-main">30天内保持登陆</span>
      </div>
      <button type="submit" class="btn  btn--black btn--normal" :disabled="isFormBtnDisabled"
        @click.prevent="login()">{{formBtnTxt}}</button>
    </form>
  </div>
</template>

<script>
  import {
    LoginRegisterFormMixin
  } from '@/assets/mixins/Mixins.js'
  import AccountServide from '@/apis/AccountService.js'
  export default {
    mixins: [LoginRegisterFormMixin],
    name: 'Login',
    components: {},
    data() {
      return {
        // 登录信息
        user: {
          phoneNumber: '15533333333',
          password: '12345678',
          keepLogin: true
        },
        // 登录前端验证错误信息
        errors: {
          phoneNumber: '',
          password: '',
        },
        formBtnTxt: '登录',
        isFormBtnDisabled: false,
        key: 'PWD'
      }
    },
    mounted() {
      // 自动focus到手机号
      this.$nextTick(() => {
        this.$refs.phoneNumberLoginInput.focus()
      })
    },
    methods: {
      disableSubmit() {
        this.isFormBtnDisabled = true
        console.log('submit')
        this.formBtnTxt = '处理中'
      },
      enableSubmit() {
        this.isFormBtnDisabled = false
        this.formBtnTxt = '登录'
      },
      /**
       *  提交登录
       */
      async login() {
        if (!this.validForm()) {
          return
        }
        this.disableSubmit()
        const userInfo = {
          phoneNumber: this.user.phoneNumber,
          password: this.md5(this.user.password, this.key),
          keepLogin: this.user.keepLogin
        }
        const res = await AccountServide.login(userInfo)
        console.log(res)
        if (res.success) {
          const token = res.message
          const loginUser = res.content
          this.$store.dispatch('login', {
            loginUser,
            token
          })
          // 转到登录后首页
          this.$router.replace('/')
        } else {
          this.$toast.error(res.message, {
            position: 'top'
          })
          this.enableSubmit()
        }
      },
      /**
       * 登录表单验证
       */
      validForm() {
        this.validate(this.user.phoneNumber, 'phoneNumber')
        this.validate(this.user.password, 'password')
        for (const prop in this.errors) {
          if (this.errors[prop]) {
            return false;
          }
        }
        return true
      },
    }
  }
</script>

<style lang="sass" scoped>
@import '@/assets/sass/components/form.sass'
@import '@/assets/sass/components/link.sass'
.form
  height: min-content
.form__link-forgerpassword
  text-align: right
  margin-top: 1rem!important
.input-item
   @include eleSize($height: 4rem)
</style>
