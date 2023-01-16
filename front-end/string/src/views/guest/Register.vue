<template lang="html">
  <!-- 注册页面 -->
  <div class="form__content">
    <span class="form__title">
            注册</span>
    <form method="post">
      <div>
        <div class="form__input-container">
          <input v-model="user.phoneNumber" ref="phoneNumberRegisterInput" class="form__input input-item--border-bottom input-item text--small input-phone" type="text" name="phoneNumber" placeholder="手机号(将作为唯一登陆凭证）" @focus="$emit('update-input-focused', 0); errors.phoneNumber=''"
            @blur="$emit('update-input-focused', -1)">
          <span class="anim-border-bottom--center anim-border-bottom"></span>
        </div>
        <span class="form__errors"><span v-show="errors.phoneNumber">{{errors.phoneNumber}} </span></span>
        <div class="form__input-container">
          <input v-model="user.code" class="form__input input-item--border-bottom input-item text--small input-code" type="text" name="code" placeholder="验证码" @focus="$emit('update-input-focused', 0); errors.phoneNumber=''" @blur="$emit('update-input-focused', -1)">
          <span class="anim-border-bottom--center anim-border-bottom"></span>
        </div>
        <span class="form__errors"><span v-show="errors.code">{{errors.code}} </span></span>
        <!-- 发送短息验证码按钮 -->
        <button :disabled="isCodeBtnDisabled" class="btn--code" @click.prevent="sendSmsCodeRegister" name="button">{{codeBtnTxt}}</button>
        <div class="form__input-container">
          <input v-model="user.password" class="form__input input-item--password input-item--border-bottom input-item text--small" type="password" name="password" placeholder="密码 (8-16位）" @focus="$emit('update-input-focused', 1);errors.password=''" @blur="$emit('update-input-focused', -1)">
          <span class="anim-border-bottom--center anim-border-bottom"></span>
        </div>
        <span class="form__errors"><span v-show="errors.password">{{errors.password}} </span></span>
        <div class="form__input-container">
          <input v-model="user.confirmPassword" class="form__input input-item--border-bottom input-item text--small" type="password" name="password2" placeholder="确认密码" @focus="$emit('update-input-focused', 2);errors.confirmPassword=''" @blur="$emit('update-input-focused', -1)">
          <span class="anim-border-bottom--center anim-border-bottom"></span></div>
        <span class="form__errors"><span v-show="errors.confirmPassword">{{errors.confirmPassword}} </span></span>
        <div class="form__input-container">
          <input type="checkbox" v-model="user.agreeToTerms" name="agreeToTerms" class="checkbox-item" @click="errors.agreeToTerms=''">
          <span class="checkbox-item__label text--small text--grey-main">已阅读并同意<span><a class="link--dark-grey link--inline-underline btn" href="#" @click.prevent="showTerms">「使用规约」</a>
        <span class="form__errors"><span v-show="errors.agreeToTerms">{{errors.agreeToTerms}} </span></span>
          </span>
          </span>
        </div>
        <button type="submit" @click.prevent="register" :disabled="isFormBtnDisabled" class="btn  btn--black btn--normal">{{registerBtnTxt}}</button>
      </div>
    </form>
  </div>
  <!-- 使用条款modal框 -->
  <teleport to="#app">
    <modal ref="termsModal" title="使用条款">
      <template v-slot:body>
        仅展示用
</template>
 </modal>
 </teleport>
</template>

<script>
  import AccountService from '@/apis/AccountService.js'
  import {
    LoginRegisterFormMixin
  } from '@/assets/mixins/Mixins.js'
  import Modal from '@/components/Modal'
  export default {
    mixins: [LoginRegisterFormMixin],
    components: {
      Modal
    },
    name: 'Register',
    data() {
      return {
        RegExp,
        // 存储注册用户信息
        user: {
          phoneNumber: '',
          password: '',
          confirmPassword: '',
          agreeToTerms: true,
          code: ''
        },
        //密码传输加密盐值
        key: 'PWD',
        // 存储前端数据验证错误
        errors: {
          phoneNumber: '',
          password: '',
          confirmPassword: '',
          agreeToTerms: '',
          code: ''
        },
        registerBtnTxt: '注册',
        codeBtnTxt: '发送验证码',
        isCodeBtnDisabled: false,
        isFormBtnDisabled: false
      }
    },
    mounted() {
      // 自动focus到手机号
      this.$nextTick(() => {
        this.$refs.phoneNumberRegisterInput.focus()
      })
    },
    methods: {
      /**
       * 显示使用规约
       */
      showTerms() {
        this.$refs.termsModal.show = true
      },
      disableSubmit() {
        this.isFormBtnDisabled = true
        this.registerBtnTxt = '处理中'
      },
      enableSubmit() {
        this.isFormBtnDisabled = false
        this.registerBtnTxt = '注册'
      },
      /**
       * 发送短信
       */
      sendSmsCodeRegister() {
        this.isCodeBtnDisabled = true
        if (!this.validPhoneNumber()) {
          this.isCodeBtnDisabled = false
          return
        }
        const sms = {
          mobile: this.user.phoneNumber,
          forWhat: this.$enum.SMS_USE.REGISTER.value
        }
        this.sendSmsCode(sms);
      },
      /**
       * 发送短信验证码
       */
      async sendSmsCode(sms) {
        const res = await AccountService.sendCode(sms)
        console.log(sms)
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
       * 点击注册按钮后的处理
       */
      async register() {
        if (!this.validForm()) {
          return
        }
        this.disableSubmit()
        const newUser = {
          phoneNumber: this.user.phoneNumber, //cryption
          // 前端加密密码
          password: this.md5(this.user.password, this.key),
          timezone: this.$timeTool.guessUserTz(),
          code: this.user.code
        }
        console.log(newUser)
        const res = await AccountService.register(newUser)
        // 注册成功后自动进入登陆状态
        if (res.success) {
          this.$toast.success("注册成功！即将进入登陆后页面", {
            position: 'top',
            duration: 1000
          })
          const loginUser = res.content
          const token = res.message
          this.$store.commit('login', {
            loginUser,
            token
          })
          setTimeout(() => this.$router.push('/'), 1000)
        } else {
          this.$toast.error(res.message, {
            position: 'top'
          })
          this.enableSubmit()
        }
      },
      /**
       * 点击发送短信验证码后对手机号的验证
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
       * 清除表单错误信息
       */
      clearErrors() {
        this.errors = {
          phoneNumber: '',
          password: '',
          confirmPassword: '',
          agreeToTerms: '',
          code: ''
        }
      },
      /**
       * 表单验证
       */
      validForm() {
        this.clearErrors()
        if (this.user.agreeToTerms === false) {
          this.errors.agreeToTerms = '必须同意条款才可注册'
        }
        this.validate(this.user.phoneNumber, 'phoneNumber')
        this.validate(this.user.password, 'password')
        this.validate(this.user.confirmPassword, 'confirmPassword'),
          this.validate(this.user.code, 'code')
        for (const prop in this.errors) {
          if (this.errors[prop]) {
            return false
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
.input-item
   @include eleSize($height: 4rem)
.form__errors
  &:last-of-type
    margin-top: .6rem


</style>
