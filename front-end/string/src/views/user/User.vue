<template lang="html">
  <!-- 用户登录后界面的整体lay out -->
  <div>
    <div class="body row no-gutters">
      <!-- aside -->
      <aside class="sidenav d-none d-lg-block col-lg-2">
        <sidebar width="inherit" :menuList="menuList">
          <!-- 网站logo -->
          <template v-slot:logo>
            <router-link to="/"><svg-logo width="7.5rem"> </svg-logo></router-link>
          </template>
        </sidebar>
      </aside>
      <div class="content col-lg-10 offset-lg-2 col-12">
        <div class="container content-container mb-5">
          <div class="row">
            <header class="header col-12">
              <!-- 头部 -->
              <user-header> </user-header>
            </header>
          </div>
          <div class="row">
            <main class="maincontent col-12">
              <!-- 主体区域，填充router相关内容 -->
              <router-view></router-view>
            </main>
          </div>
        </div>
      </div>
    </div>
    <teleport to="#app">
      <!-- 小屏幕的全屏菜单 -->
      <div class="overlaymenu d-lg-none">
        <overlay-menu :menuList="menuList"> </overlay-menu>
      </div>
    </teleport>
  </div>
</template>

<script>
  import UserHeader from '@/components/UserHeader'
  import Sidebar from '@/components/Sidebar'
  import SvgLogo from '@/components/icons/SvgLogo'
  import OverlayMenu from '@/components/OverlayMenu'
  import {
    mapGetters
  } from 'vuex'
  export default {
    components: {
      Sidebar,
      SvgLogo,
      UserHeader,
      OverlayMenu
    },
    computed: {
      ...mapGetters(
        ['user', 'token', 'isLogged'])
    },
    created() {
      // 配置dayjs的时区
      this.$timeTool.config(this.user.timezone)
    },
    watch: {
      // 如果用户信息发生变化，则重新配置时区
      user(user) {
        if (user) {
          this.$timeTool.config(user.timezone)
        }
      },
      // 如果已退出登录，则转向登录页面
      isLogged(isLogged) {
        if (!isLogged) {
          this.$router.push('/login')
        }
      }
      //todo 时间格式添加说明影响哪些地方
    },
    data() {
      return {
        // 左侧菜单
        menuList: [{
            id: '0',
            menuName: '用户首页',
            icon: '\ue65f',
            iconName: 'index',
            href: '/home'
          },
          {
            id: '1',
            menuName: '时间清单',
            icon: '\ue61a',
            iconName: 'time',
            href: '/timereport'
          },
          {
            id: '2',
            menuName: '日记撰写',
            icon: '\ue61a',
            iconName: 'time',
            href: '/griddiary'
          },
          {
            id: '3',
            menuName: '待办管理',
            icon: '\ue609',
            iconName: 'pen',
            href: '/todos'
          },
          {
            id: '4',
            menuName: '个人设置',
            icon: '\ue659',
            iconName: 'profile',
            href: '/settings'
          },
        ]
      }
    }
  }
</script>

<style lang="sass" scoped>
.body
  background: #F4F9FA
  background: #EFF4F7
  min-height: 100vh
.content-container
  max-width: 1800px
  margin: 0 auto
.sidenav
  position: fixed

.maincontent
  // 不同屏幕用户页面内容的padding
  @media(min-width: 1500px)
    padding: 2.5rem 5rem
  @media(min-width: 1600px)
    padding: 2.5rem 8rem
</style>
