<template lang="html">
  <!-- 用户信息设置页面-->
  <div class="container-fluid">
    <div class="row">
      <!-- 基本信息部分 -->
      <div class="col-12  col-lg-5 mb-5 mb-lg-0">
        <card rounded class="info form-container">
          <!-- 标题 -->
          <h3 class="form--title">
            用户信息
          </h3>
          <!-- 时区不符的提示 -->
          <div class="alert" v-if="timezoneMismatch">
            <div> <span class="iconfont icon-alert alert-icon ">&#xe797;</span></div>
            <div class="alert-main"><span class="alert-msg">当前选择的时区与所在时区不符。</span>
              <button class="alert-main--btn btn" @click="changeTimezone">点此切换至{{ userTimezoneGuess }}</button>
            </div>
          </div>
          <!-- 头像 -->
          <div class="avatar">
            <img v-if="profileImg" class="avatar-img" :src="profileImg">
            <img v-if="!profileImg" class="avatar-img" :src="userInfo.profileImg">
            <!-- <el-upload
                    class="avatar-uploader" :headers="uploadReqHeader"
                    action="http://127.0.0.1:8080/api/v1/file/upload/img"
                    :show-file-list="false"
                    http-request="handleAvatarSuccess"
                    :before-upload="beforeAvatarUpload"
                  > -->
            <el-upload class="avatar-uploader" action="#" :show-file-list="false"
              :http-request="uploadProfileImg" :before-upload="beforeAvatarUpload">
              <span class="avatar-btn">上传头像</span>
            </el-upload>
          </div>
          <!-- 信息表单 -->
          <el-form class="form form-basicinfo" ref="userBasicInfo" :model="userInfo" :rules="basicInfoFormRules">
            <el-form-item spellcheck="false" label="用户名（3-16位中英文、数字、下划线）" prop="username">
              <el-input v-model="userInfo.username" maxlength="16" show-word-limit minlength="3"></el-input>
            </el-form-item>
            <el-form-item label="手机号" prop="phoneNumber">
              <el-input v-model="userInfo.phoneNumber" disabled></el-input>
            </el-form-item>
            <el-form-item label="时区" prop="timezone">
              <span> </span>
              <!-- element ui 大数组似乎存在性能问题，所以使用原生select-->
              <select class="select--elementui" v-model="userInfo.timezone">
                <option v-for="(tz, i) in timezoneList" :key="i" :value="tz">{{ tz }}</option>
              </select>
            </el-form-item>
            <button type="submit" class="btn btn--submit" :disabled="btnDisabled.updateBasicInfo"
              @click.prevent="submitUpdateBasicInfo">保存</button>
            <button class="btn btn--reset" @click.prevent="resetUserInfo()">还原</button>
          </el-form>
        </card>
      </div>
      <!-- 偏好设定部分 -->
      <div class="col-12  col-lg-7 ">
        <card rounded class="info form-container">
          <h3 class="form--title">
            偏好设定
          </h3>
          <el-form class="form" ref="userPreferences" :model="userPreferences">
            <div>
              <el-form-item label="时间格式">
                <el-radio-group v-model="userPreferences.timeFormat">
                  <el-radio-button label="HOUR24">24小时制</el-radio-button>
                  <el-radio-button label="HOUR12">12小时制</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="显示<1min的时间条目（不影响首页）">
                <el-radio-group v-model="userPreferences.showShortEntry">
                  <el-radio-button :label="true">是</el-radio-button>
                  <el-radio-button :label="false">否</el-radio-button>
                </el-radio-group>
              </el-form-item>
            </div>
            <button type="submit" class="btn btn--submit" :disabled="btnDisabled.updatePreferences"
              @click.prevent="submitUpdatePreferences">保存</button>
            <button class="btn btn--reset" @click.prevent="getUserPreferences">还原</button>
          </el-form>
          <span class="divider--hr divider mt-5"></span>
          <!-- 账号安全部分 -->
          <h3 class="form--title mt-5">
            账号安全
          </h3>
          <button class="btn btn--default" @click="showresetPasswordFormModal">修改密码</button>
        </card>
      </div>
    </div>
    <!-- 重置密码的modal-->
    <teleport to="#app">
      <modal ref="resetPasswordFormModal" class="" width="80%" title="修改密码" max-width="40rem">
        <template v-slot:body>
          <el-form :model="resetPswdForm" ref="resetPasswordForm" :rules="resetPswdRules">
            <el-form-item label="原密码" prop="old" autocomplete="off">
              <el-input v-model="resetPswdForm.old" type="password" placeholder="输入原密码"></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="new" autocomplete="off">
              <el-input v-model="resetPswdForm.new" type="password" placeholder="8-16位"></el-input>
            </el-form-item>
            <el-form-item label="新密码确认" prop="newConfirm" autocomplete="off">
              <el-input v-model="resetPswdForm.newConfirm" type="password" placeholder="确认密码"></el-input>
            </el-form-item>
          </el-form>
        </template>
        <template v-slot:footer>
          <button type="submit" class="btn--confirm btn" :disabled="btnDisabled.resetPswd"
            @click.prevent="submitResetPswd">确定</button>
        </template>
      </modal>
    </teleport>
  </div>
</template>

<script>
  import AccountService from '@/apis/AccountService.js'
  import * as Validators from '@/assets/js/validators.js'
  import Modal from '@/components/Modal'
  import {
    copy,
    propNoChange
  } from '@/assets/js/utils.js'
  import Timezones from '@/assets/js/timezones.js'
  // import FileService from '@/apis/FileService.js'
  import Card from '@/components/Card'
  import {
    mapGetters
  } from 'vuex'
  export default {
    components: {
      Card,
      Modal
    },
    computed: {
      ...mapGetters([
        'user', 'token'
      ]),
      currProfile() {
        return this.user.profileImg
      }
    },
    data() {
      return {
        key: 'PWD', // 密码加密盐值
        btnDisabled: {
          resetPswd: false,
          updatePreferences: false,
          updateBasicInfo: false
        },
        userTimezoneGuess: '', // 用户实际所在时区
        timezoneMismatch: false, // 用户设定时区是否与实际时区相符
        basicInfoFormRules: {
          username: [{
            validator: Validators.checkUsername,
            trigger: 'change'
          }],
          phoneNumber: [{
            validator: Validators.checkPhoneNumber,
            trigger: 'change'
          }],
        },
        resetPswdRules: {
          old: [{
            validator: Validators.checkPassword,
            trigger: 'change'
          }],
          new: [{
            validator: Validators.checkPassword,
            trigger: 'change'
          }],
          newConfirm: [{
            validator: (rule, value, cb) => {
              if (value === '') {
                cb(new Error('确认密码不能为空'))
              } else if (value !== this.resetPswdForm.new) {
                cb(new Error('两次密码不一致'))
              } else {
                cb()
              }
            },
            trigger: 'submit'
          }]
        },
        timezoneList: Timezones,
        profileImg: null, // 用户上传的图片
        userInfo: {
          username: '',
          phoneNumber: '',
          profileImg: '',
          timezone: ''
        },
        userPreferences: {
          showShortEntry: '',
          timeFormat: ''
        },
        resetPswdForm: {
          old: '',
          new: '',
          newConfirm: ''
        }
      }
    },
    methods: {
      /*
       * 点击个人资料还原按钮后恢复信息
       */
      resetUserInfo() {
        this.getUserInfo()
        this.profileImg = null // 将上传的图片清除
      },
      changeTimezone() {
        const user = {
          timezone: this.userTimezoneGuess,
        }
        this.updateUserAccount(user, 0)
      },
      /**
       * 发送更新用户信息的请求
       * @param {*} user 
       */
      async updateUserAccount(user) {
        const res = await AccountService.update(user, this.user.id)
        if (res) {
          this.btnDisabled.updateBasicInfo = false
          this.btnDisabled.updatePreferences = false
        }
        if (res.success) {
          this.$toast.show('更新成功')
          const userUpdated = res.content
          this.$store.commit('update', userUpdated)
          this.getUserInfo()
          this.getUserPreferences()
        } else {
          const msg = res.message ? res.message : '更新失败'
          this.$toast.error(msg)
          this.btnDisabled.updateBasicInfo = false
          this.btnDisabled.updatePreferences = false
        }
      },
      /**
       * 提交更新偏好
       */
      submitUpdatePreferences() {
        if (propNoChange(this.userPreferences, this.user)) {
          this.$toast.show('数据未变化')
          return
        }
        this.btnDisabled.updatePreferences = true
        this.updateUserAccount(this.userPreferences)
      },
      /**
       * 提交更新基本信息
       */
      submitUpdateBasicInfo() {
        this.$refs['userBasicInfo'].validate(valid => {
          if (valid) {
            if (propNoChange(this.userInfo, this.user)) {
              this.$toast.show('数据未变化')
              return
            }
            this.btnDisabled.updateBasicInfo = true
            this.updateUserAccount(this.userInfo)
          } else {
            return false
          }
        })
      },
      /**
       * 发送修改密码的请求
       * @param {String} passwords 
       */
      async resetPassword(passwords) {
        this.btnDisabled.resetPswd = true
        const res = await AccountService.resetPassword(this.user.id, passwords)
        if (res) {
          this.btnDisabled.resetPswd = false
        }
        if (res.success) {
          this.$toast.show('修改成功，请重新登录')
          setTimeout(() => {
            this.$router.push('/login')
            this.$store.dispatch('logout')
          }, 500)
        } else {
          this.$toast.error(res.message)
        }
      },
      /**
       * 提交修改密码
       */
      submitResetPswd() {
        this.$refs['resetPasswordForm'].validate(valid => {
          if (valid) {
            const passwords = {
              oldPassword: this.md5(this.resetPswdForm.old, this.key),
              newPassword: this.md5(this.resetPswdForm.new, this.key)
            }
            this.resetPassword(passwords)
          } else {
            return false
          }
        })
      },
      /**
       * 打开重置密码的modal
       */
      showresetPasswordFormModal() {
        this.$nextTick(() => {
          this.$refs.resetPasswordFormModal.open()
        })
      },
      /**
       * 复制一份用户的基本信息，并检查时区是否相符
       */
      getUserInfo() {
        this.userInfo = copy(this.userInfo, this.user)
        this.checkIfTimezoneMatch()
      },
      /**
       * 复制一份用户的偏好
       */
      getUserPreferences() {
        this.userPreferences = copy(this.userPreferences, this.user)
      },
      /**
       * 上传用户提交的头像图片
       * @param {*} res 
       */
      uploadProfileImg(res) {
        if (res.file) {
          var COS = require('cos-js-sdk-v5');
          var cos = new COS({
            SecretId: '{secretId}',  // 公开版代码中隐藏这里的值
            SecretKey: '{secretKey}}'
          })
          cos.putObject({
            Bucket: 'save-1304263782',
            /* 填入您自己的存储桶，必须字段 */
            Region: 'ap-hongkong',
            /* 存储桶所在地域，例如ap-beijing，必须字段 */
            Key: res.file.name,
            /* 存储在桶里的对象键（例如1.jpg，a/b/test.txt），必须字段 */
            StorageClass: 'STANDARD',
            Body: res.file, // 上传文件对象
          }, (err, data) => {
            console.log(err || data)
            // 上传成功之后
            if (data.statusCode === 200) {
              this.$toast.show('上传成功')
              console.log(data.Location)
              this.profileImg = `https://${data.Location}`
              this.userInfo.profileImg = this.profileImg
            }
          })
        }
      },
      /**
       * 上传头像之前的处理
       * @param {*} file 
       */
      beforeAvatarUpload(file) {
        const isJPG = file.type === 'image/jpeg'
        const isLt1M = file.size / 1024 / 1024 < 1
        if (!isJPG) {
          this.$toast.show('只能上传JPG 格式')
        }
        if (!isLt1M) {
          this.$toast.show('图片大小不可超过1MB')
        }
        return isJPG && isLt1M
      },
      // async uploadAvatar(arg){
      //   console.log(arg)
      //   let formData  = new FormData();
      //   formData.append("file", arg.file.raw);
      //   const res = await FileService.uploadAvatar(formData)
      //   if(res.success){
      //     this.profileImg = res.content
      //   }else{
      //     this.$toast.error('图片上传失败')
      //   }
      // },
      //
      /**
       * 检查用户时区设置是否与所在时区相符
       */
      checkIfTimezoneMatch() {
        if (this.userTimezoneGuess && this.user.timezone) {
          if (this.userTimezoneGuess !== this.user.timezone) {
            this.timezoneMismatch = true
            return
          }
        }
        this.timezoneMismatch = false
      }
    },
    mounted() {
      this.userTimezoneGuess = this.$dayjs.tz.guess()
      this.getUserInfo()
      this.getUserPreferences()
    }
  }
</script>

<style lang="sass" scoped>
@import '@/assets/sass/components/divider.sass'
@import '@/assets/sass/components/form.sass'
.form-container
  width: 100%
  padding: 2rem
  @media(min-width: $bp-sm)
    padding: 4rem

  .form
    margin: 1.5rem 0
    width: 100%
    max-width: 40rem
    &--title
      margin-bottom: 2rem
      background: #F7F7F7
      width: max-content
      padding: 1rem 2rem
      border-radius: 2px
    &-basicinfo
      &-phoneNumbermsg
        color: #7d8b94
        padding: 1rem
        background: #F7F7F7
        font-size: 1.4rem
        margin-bottom: 2rem
.avatar
   // margin: 0 auto
   margin-bottom: 2rem
   display: flex
   align-items: center
   max-width: 40rem
   &-img
     width: 6rem
     height: 6rem
     border-radius: 50%
     margin-right: 4rem
   &-btn
     border: 1px solid #F5DBB2
     border-radius: 3px
     display: block
     padding: .6rem 2rem
     background: #FDF6ED
     color: #E7A23D
.btn--reset
  margin-left: 2rem
  margin-top: 2rem
.alert
  width: max-content
  line-height: 1.5
  height: max-content
  padding: 1rem
  border-radius: 1rem
  border: 1px solid #F5C5BB
  background: #FCEDEB
  margin-bottom: 3rem
  display: flex
  align-items: flex-start
  box-shadow: 9px 7px 30px #FCEDEB
  &-icon
    font-size: 2.4rem
    color: #F47573
    margin-right: 1rem
  &-main
    margin-top: 4.2px
    &--btn
      padding: 0
      margin-top: 1rem
      display: block
      &:hover
        color: #222B39

</style>
