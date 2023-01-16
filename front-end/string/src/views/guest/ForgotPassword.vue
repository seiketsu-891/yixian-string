<template>
  <!-- 忘记密码 -->
  <div class="form__content">
    <span class="form__title">
            找回密码</span>
    <form method="post">
      <div class="form__input-container">
        <input v-model="user.phoneNumber" class="form__input input-item--border-bottom input-item text--small" name="phoneNumber" placeholder="请输入手机号" @focus="$emit('update-input-focused', 0); errors=''" @blur="$emit('update-input-focused', -1)">
        <span class="anim-border-bottom--center anim-border-bottom"></span>
      </div>
      <span class="form__errors"><span v-show="errors">{{errors.phoneNumber}} </span></span>
      <div class="form__input-container">
        <input v-model="user.code" class="form__input input-item--border-bottom input-item text--small input-code" type="text" name="code" placeholder="验证码" @focus="$emit('update-input-focused', 0); errors.phoneNumber=''" @blur="$emit('update-input-focused', -1)">
        <span class="anim-border-bottom--center anim-border-bottom"></span>
      </div>
      <span class="form__errors"><span v-show="errors.code">{{errors.code}} </span></span>
      <!-- 发送短息验证码按钮 -->
      <button :disabled="isCodeBtnDisabled" class="btn--code" @click.prevent="sendSmsCodeForgotPassword" name="button">{{codeBtnTxt}}</button>
      <div class="form__input-container">
        <input v-model="user.password" class="form__input input-item--password input-item--border-bottom input-item text--small" type="password" name="password" placeholder="密码 (8-16位）" @focus="$emit('update-input-focused', 1);errors.password=''" @blur="$emit('update-input-focused', -1)">
        <span class="anim-border-bottom--center anim-border-bottom"></span>
      </div>
      <span class="form__errors"><span v-show="errors.password">{{errors.password}} </span></span>
      <div class="form__input-container">
        <input v-model="user.confirmPassword" class="form__input input-item--border-bottom input-item text--small" type="password" name="password2" placeholder="确认密码" @focus="$emit('update-input-focused', 2);errors.confirmPassword=''" @blur="$emit('update-input-focused', -1)">
        <span class="anim-border-bottom--center anim-border-bottom"></span></div>
      <span class="form__errors"><span v-show="errors.confirmPassword">{{errors.confirmPassword}} </span></span>
      <button type="submit" class="btn  btn--black btn--normal" :disabled="isFormBtnDisabled"  @click.prevent="handleBtnSumbit">{{formBtnTxt}}</button>
    </form>
  </div>
</template>

<script>
  import AccountService from '@/apis/AccountService.js'
  import {
    LoginRegisterFormMixin
  } from '@/assets/mixins/Mixins.js'
  export default {
    mixins: [LoginRegisterFormMixin],
    data() {
      return {
        key: 'PWD',
        RegExp,
        user: {
          phoneNumber: '',
          code: '',
          password: '',
          confirmPassword: ''
        },
        errors: {
          phoneNumber: '',
          code: ''
        },
        isFormBtnDisabled: false,
        formBtnTxt: '提交',
        isCodeBtnDisabled: false,
        codeBtnTxt: '发送验证码'
      }
    },
    methods: {
      /**
       * 点击发送短信验证码按钮的处理
       */
      async sendSmsCodeForgotPassword() {
        this.isCodeBtnDisabled = true
        if (!this.validPhoneNumber()) {
          this.isCodeBtnDisabled = false
          return
        }
        const sms = {
          mobile: this.user.phoneNumber,
          forWhat: this.$enum.SMS_USE.FORGOT.value
        }
        const res = await AccountService.sendCode(sms)
        if (res.success) {
          this.$toast.success("短信发送成功", {
            position: 'top',
            duration: 1000
          })
          // 按钮禁用并倒计时
          this.isCodeBtnDisabled = true
          let second = 60
          this.codeBtnTxt = `重发(${second}秒)`
          const countDown = setInterval(() => {
            second--;
            this.codeBtnTxt = `重发(${second}秒)`
            if (second == 0) {
              clearInterval(countDown);
              this.codeBtnTxt = '重发'
              this.isCodeBtnDisabled = false
            }
          }, 1000)
          countDown();
        } else {
          this.$toast.error(res.message, {
            position: 'top'
          })
          this.isCodeBtnDisabled = false
        }
      },
      /**
       * 点击发送短信验证码前校验手机号
       */
      validPhoneNumber() {
        this.clearErrors()
        this.validate(this.user.phoneNumber, 'phoneNumber')
        if (this.errors['phoneNumber']) {
          return false
        }
        return true
      },
      /**
       * 清除校验错误提示信息
       */
      clearErrors() {
        this.errors = {
          phoneNumber: '',
          code: '',
          password: '',
          confirmPassword: ''
        }
      },
      /**
       * 验证表单
       */
      validForm() {
        this.clearErrors()
        this.validate(this.user.phoneNumber, 'phoneNumber')
        this.validate(this.user.code, 'code')
        this.validate(this.user.password, 'password')
        this.validate(this.user.confirmPassword, 'confirmPassword')
        for (const prop in this.errors) {
          if (this.errors[prop]) {
            return false
          }
        }
        return true
      },
      /**
       *  点击提交按钮后的处理
       */
      handleBtnSumbit() {
        if (!this.validForm()) {
          this.enableSubmit()
          return
        }
        this.disableSubmit()
        const resetPwdForgot = {
          mobile: this.user.phoneNumber,
          code: this.user.code,
          password: this.md5(this.user.password, this.key),
          codeForWhat: this.$enum.SMS_USE.FORGOT.value
        }
        this.resetPasswordForgot(resetPwdForgot)
      },
      // 发送忘记密码后重设密码的请求
      async resetPasswordForgot(resetPwdForgot) {
        const res = await AccountService.resetPasswordForgot(resetPwdForgot)
        if (res.success) {
          this.$toast.success("注册成功！即将进入登陆后页面", {
            position: 'top',
            duration: 1000
          })
          setTimeout(() => this.$router.push('/login'), 1000)
        } else {
          this.$toast.error(res.message, {
            position: 'top'
          })
          this.user.code = ''
          this.enableSubmit()
        }
      },
      disableSubmit() {
        this.isFormBtnDisabled = true
        this.formBtnTxt = '处理中'
      },
      enableSubmit() {
        this.isFormBtnDisabled = false
        this.formBtnTxt = '提交'
      },
    }
  }
</script>

<style lang="sass" scoped>
@import '@/assets/sass/components/form.sass'
@import '@/assets/sass/components/link.sass'
.input-item
   @include eleSize($height: 4rem)
.form__input-container
    &:last-of-type
      margin-bottom: -.5rem
.form__errors
    &:last-of-type
      margin-top: .6rem!important
      margin-bottom: 2rem
</style>
