<template lang="html">
  <!-- ui资源页面 -->
  <div class="">
    <bread-crumb class="bread-crumb" :data="nav"></bread-crumb>
    <p>
      本网站UI素材(插图、icon等)均来源于网络上标明<em>「可免费用于非商业项目」</em>的网站或者由自己制作，部分素材有修改。对提供素材的网站和设计者表示感谢。
      <span class="contact-msg">(如您发现我使用了您的素材但并未注明，欢迎联系yixian-offical@foxmail.com)</span>
    </p>
    <hr class="hr--dashed">
    <div class="resources-links">
      <span class="right-triangle"></span>素材来源网站包括
      <ul>
        <li><a href="https://www.flaticon.com">flaticon</a></li>
        <li><a href="https://www.freepik.com">freepik</a></li>
        <li><a href="https://www.iconfont.cn">iconfont</a></li>
        <li><a href="https://iconscout.com">iconscout</a></li>
        <li> <a href="https://dribbble.com/shots/5624973-Empty-State-Status-Icons">Empty State Status Icons</a></li>
      </ul>
    </div>
    <hr class="hr--dashed">
    <div class="references-links">
      <span class="right-triangle"></span>页面设计主要参考了如下设计稿
      <ul>
        <li>用户界面参考<a href="https://dribbble.com/shots/10858979-FREE-Freelancer-Schedule-Web"> FREE - Freelancer Schedule Web</a></li>
        <li>主页基于<a href="https://dribbble.com/shots/12099957-Performance-Landing-Page"> Performance Landing Page</a></li>
        <li>时间表界面参考<a href="https://dribbble.com/shots/9089543-Schedule-Dashboard-Design">Schedule Dashboard Design</a></li>
        <li> 时间统计界面参考<a href="https://gumroad.com/d/e651add9374af1f5e76a4e3878ef7020">Gem-dashboard_ui-all-lastdoor.store_1.0</a></li>
      </ul>
    </div>
  </div>
</template>

<script>
  import BreadCrumb from '@/components/basicUI/BreadCrumb'
  export default {
    components:{
      BreadCrumb
    },
    data() {
      return {
        nav: [{
            id: 1,
            name: '首页',
            href: '/'
          },
          {
            id: 2,
            name: '素材说明',
            href: '/ui-resources'
          }
        ]
      }
    }
  }
</script>

<style lang="sass" scoped>
@import '@/assets/sass/abstract/mixins.sass'
.contact-msg
  font-size: 1.4rem
  display: block
p
  margin: 1rem 0
a
  transition: all .3s
  border-bottom: 3px solid #C6F1FD
  &:hover
    border-bottom: 3px solid #90CCC9

li
  margin: 1rem
.bread-crumb
  margin-bottom: 3rem
.right-triangle
  display: inline-block
  margin-right: 1rem
  @include right-triangle(1rem, #FBDE7E)

</style>
