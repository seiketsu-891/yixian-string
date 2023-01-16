<template lang="html">
  <!-- 页面右上角的头像图标及其下拉菜单 -->
  <div class="avatar__container">
    <img class="avatar__img" @click="toggleDropdown" @error="showDefaultImg" :src="user.profileImg" alt="用户头像" :style="{width: width, height: width}">
    <div v-if="dropdown">
      <dropdown ref="avatarDropdown" class="avatar__dropdown" type="basic">
        <slot>
          <div class="avatar__basicinfo" @click="goToSettings">
            <img class="avatar__basicinfo_img avatar__img" @error="showDefaultImg" :src="user.profileImg" alt="用户头像" :style="{ width: width, height: width }">
            <div>
              <span class="avatar__basicinfo__text">{{ username }}</span>
              <span class="avatar__basicinfo__text">{{ phoneNumber }}</span>
            </div>
          </div>
          <div class="avatar__lists">
            <ul class="listgroup">
              <li class="listgroup__title">使用帮助</li>
              <li class="listgroup__item"><a target="_blank" href="https://gitee.com/louie2022"><span class="iconfont icon icon-help">&#xe65e;</span>网站源码</a></li>
              <li class="listgroup__item"><a href="mailto:yixian-offical@foxmail.com"><span
                        class="iconfont icon icon-email">&#xe65e;</span>联系我们</a></li>
            </ul>
          </div>
          <div class="avatar__dropdownbottom">
            <button class="btn" @click="logout" :disabled="logoutBtnDisabled"><span
                    class="iconfont icon icon-logout">&#xe658;</span>退出登陆</button>
          </div>
        </slot>
      </dropdown>
    </div>
  </div>
</template>

<script>
  import AccountService from '@/apis/AccountService.js'
  import Dropdown from '@/components/Dropdown'
  import {
    mapGetters
  } from 'vuex'
  export default {
    components: {
      Dropdown,
    },
    props: {
      dropdown: {
        type: Boolean,
        default: true
      },
      width: {
        type: String,
        default: '2rem'
      },
    },
    data() {
      return {
        logoutBtnDisabled: false
      }
    },
    computed: {
      imgUrl() {
        return this.user.profileImg
      },
      username() {
        return this.user.username
      },
      phoneNumber() {
        return this.user.phoneNumber
      },
      ...mapGetters([
        'user',
        'token'
      ]),
      route() {
        return this.$route
      }
    },
    watch: {},
    methods: {
      /**
       * 切换头像下面的菜单的可见度
       */
      toggleDropdown() {
        const dp = this.$refs.avatarDropdown
        if (dp.active) {
          dp.close()
        } else {
          dp.open()
        }
      },
      /**
       * 显示获取头像失败时的图片
       */
      showDefaultImg(event) {
        event.target.src = "https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png"
      },
      /**
      /* 发送请求退出登录
      **/
      async logout() {
        this.logoutBtnDisabled = true
        if (!this.token) {
          this.$router.push('/')
        } else {
          const res = await AccountService.logout({
            token: this.token,
            userId: this.user.id
          })
          if (res.success) {
            this.$toast.show('登出成功')
            setTimeout(() => {
              this.$store.dispatch('logout')
            }, 500)
          } else {
            this.$toast.error('登出失败')
            this.logoutBtnDisabled = false
          }
        }
      },
      /**
      /* 转到个人信息设置页面
      **/
      goToSettings() {
        this.$router.push('/settings')
      }
    }
  }
</script>

<style lang="sass" scoped>
@import '@/assets/sass/abstract/mixins.sass'
@import '@/assets/sass/components/divider.sass'
.avatar
  user-select: none
  &__container
    position: relative
  &__img
    cursor: pointer
    border-radius: 50%
  &__dropdown
    min-width: 25rem
    user-select: none
    padding: 0 !important
    overflow: hidden
    right: 3px
    z-index: 99999
    top: 5rem
    height: max-content
    position: absolute
    border-radius: 1rem
    // height: 380px
    // 随邮箱长度调整宽度
    width: min-content
    z-index: 100
  &__basicinfo
    background: #fafafa
    padding: 2rem
    display: flex
    &__text
      max-width: 20rem
      @include text-nowrap
      padding-left: 8px
      // padding-right: 5px
      display: block
      &:not(:last-of-type)
        margin-bottom: 4px
      &:not(:first-of-type)
        color: #93999F
        font-size: 13px
  &__dropdownbottom
    width: 100%
    @include heightAndLineheight(4rem)
    text-align: center
    background: #fafafa
    color: #93999F
    margin-top: var(--margin-listitem--normal)
    &:hover
      color: #5D5D5D
.listgroup
  padding-left: 3rem
  margin-top: var(--margin-listitem--normal)
  cursor: pointer
  &__title
    color: #b9bfc4
    font-size: 1.3rem
    margin: var(--margin-listitem--normal) 0
  &__item
    border-right: 5px solid transparent
    &:not(:last-of-type)
      margin-bottom: var(--margin-listitem--normal)
    &:hover
      border-right: 5px solid #F6CA48
.divider--hr
  background-color: var(--border-grey)
  margin: 1.5rem 0
.icon
  margin-right: 1rem
.icon-logout
  font-size: 14px

</style>
