<template lang="html">
    <!-- 未登录时的首页 -->
    <div class="bodybox">
        <!-- 首页的不规则背景图形 -->
        <div class="backgroundshape">
            <svg class="svgicon svgicon-irregularshape">
              <use :xlink:href="require('@/assets/icon/sprite.svg') + '#svgicon-irregularshape'"></use>
            </svg>
        </div>
        <!-- header部分-->
        <header class="header row no-gutters">
            <!-- 网站logo -->
            <div class="header-logo">
                <svg class="svgicon-logo svgicon">
           <use :xlink:href="require('@/assets/icon/sprite.svg') + '#svgicon-logo'"></use>
         </svg>
            </div>
            <!-- 顶部导航菜单 -->
            <nav class="header-nav d-none d-sm-block col-1 col-sm-6">
                <ul class="header-nav__wrapper visible-menu">
                    <li class="header-nav__item"><a href="#" @click.prevent="jumpTo('section-features')">主要功能</a></li>
                    <li class="header-nav__item"><a href="#" @click.prevent="jumpTo('section-fqa')">常见问题</a></li>
                    <li class="header-nav__item">
                        <router-link to="/ui-resources">素材来源</router-link>
                    </li>
                    <li class="header-nav__item">
                        <tooltip width="max-content" txt="联系我们">邮箱: yixian-offical@foxmail.com</tooltip>
                    </li>
                </ul>
                <div class="dropdown">
                    <a class="morebtn d-none header-nav__btn iconfont" heref="#">&#xe6c1;</a>
                    <ul class="header-hiddennav hidden-menu"></ul>
                </div>
            </nav>
            <!-- 登陆按钮-->
            <div class="header-login col-6 col-sm-3">
                <button class="header-login__btn btn" @click="$router.push('login')">登录</button>
            </div>
        </header>
        <!-- hero 首页宣传大字和图-->
        <section class="hero row no-gutters">
            <div class="col-12 col-md-6 hero__txt">
                <div class="hero__txt--content">
                    <h1 class="hero__txt--title ">易弦</h1>
                    <h2 class="hero__txt--subtitle ">让自我持续<em class="hero__txt--em">升级</em></h2>
                    <p class="hero__txt--more">易弦是帮助您进行时间追踪和管理的个人生产力工具，<br>名字源自于「改弦易辙」,意指自我的更新升级</p>
                    <button class="btn hero__txt-btn " @click="$router.push('register')">立刻加入</button>
                </div>
            </div>
            <div class="col-12 col-md-6 hero__img">
                <svg class="svgicon-peoplewithhourglass svgicon">
         <use :xlink:href="require('@/assets/icon/sprite.svg') + '#svgicon-peoplewithhourglass'"></use>
       </svg>
            </div>
        </section>
        <!-- features 网站特点简介-->
        <section class="feature feature--1 row no-gutters" id="section-features">
            <div class="feature-frame col-12 col-md-6">
                <div class="feature-frame__bg feature-frame__bg--right feature-frame__bg--pink">
                </div>
                <div class="feature-frame__wrapper">
                    <!-- top -->
                    <div class="feature-frame__top  feature-frame__top--1">
                        <div class="feature-frame__connectedcards feature-frame__connectedcards--toright wow animated animate__animated  animate__fadeInLeft">
                            <span class="feature-frame__connectedcards__bridge--toright feature-frame__connectedcards__bridge">
                </span>
                            <div class="feature-frame__connectedcards-biggercard feature-frame__connectedcards-biggercard--toright frame__box--avatartxt feature-frame__connectedcards-biggercard--toright">
                                <div class="frame-avatar">
                                    <div class="frame-avatar__wrapper">
                                        <svg class="svgicon-databoard svgicon frame-avatar__svg">
                     <use :xlink:href="require('@/assets/icon/sprite.svg') + '#svgicon-databoard'"></use>
                   </svg>
                                    </div>
                                </div>
                                <div class="frame-txt ml-2 ml-lg-5">
                                    <h2 class="frame-txt__title mt-3 mb-lg-3">时间追踪</h2>
                                    <p class="frame-txt__desc d-none d-sm-block ml-0">追踪和了解在各项任务上的时间分配情况</p>
                                    <p class="frame-txt__desc d-block d-sm-none  mt-2">追踪时间分配情况</p>
                                </div>
                            </div>
                            <div class="feature-frame__connectedcards-smallercard">
                                <div class="feature-frame__connectedcards-smallercard-imgwrapper">
                                    <img src="@/assets/img/circulargraph.png" alt="任务百分比环形图">
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- top end -->
                    <div class="feature-frame__bottom  feature-frame__bottom--1">
                        <svg class="svgicon-datawithbg svgicon ">
                   <use :xlink:href="require('@/assets/icon/sprite.svg') + '#svgicon-datawithbg'"></use>
                 </svg>
                        <div class="feature-frame__bottom-imgwrapper">
                            <img src="@/assets/img/area-chart.png" alt="area图表">
                        </div>
                    </div>
                </div>
            </div>
            <div class="feature__main mt-5 mt-md-0 pl-sm-5 col-12 col-md-6">
                <div class="feature__main-wrapper  pl-lg-5 ml-0 mr-lg-0 ml-lg-auto ">
                    <span class="feature__main-tag  feature__main-tag--red"><span class="iconfont mr-2">&#xe6cd;</span>追踪时间</span>
                    <div class="feature__main-txt">
                        <h2 class="feature__main-txt--title">时间利用可视化</h2>
                        <p class="feature__main-txt--desc">通过数据和图表了解自己在各种事务上的时间分配和执行效率。有目的调整每日行动</p>
                        <ul class="feature__main-bulletpoints">
                            <li> <span class="feature__main-bulletpoints--prefix mr-3"></span> 数据图表</li>
                            <li> <span class="feature__main-bulletpoints--prefix mr-3"></span> 时间追踪</li>
                        </ul>
                    </div>
                </div>
            </div>
        </section>
        <!--  feature  2-->
        <section class="feature feature--2 row no-gutters">
            <div class="feature-frame col-12 col-md-6 order-first order-md-2">
                <div class="feature-frame__bg feature-frame__bg--middle feature-frame__bg--green">
                </div>
                <div class="feature-frame__wrapper">
                    <!-- top -->
                    <div class="feature-frame__top feature-frame__top--2">
                        <div class="feature-frame__connectedcards feature-frame__connectedcards--toleft wow animated animate__animated  animate__fadeInRight">
                            <span class="feature-frame__connectedcards__bridge--toleft feature-frame__connectedcards__bridge">
                </span>
                            <div class="feature-frame__connectedcards-smallercard  feature-frame__connectedcards-smallercard--toright">
                                <svg class="svgicon-diary-page  svgicon card-icon">
                     <use :xlink:href="require('@/assets/icon/sprite.svg') + '#svgicon-diary-page'"></use>
                   </svg>
                            </div>
                            <div class="feature-frame__connectedcards-biggercard feature-frame__connectedcards-biggercard--toleft frame__box--avatartxt">
                                <div class="frame-avatar">
                                    <div class="frame-avatar__wrapper  frame-avatar__wrapper--green">
                                        <svg class="svgicon-speech-bubble      svgicon frame-avatar__svg">
                   <use :xlink:href="require('@/assets/icon/sprite.svg') + '#svgicon-speech-bubble'"></use>
                 </svg>
                                    </div>
                                </div>
                                <div class="frame-txt ml-2 ml-lg-5">
                                    <h2 class="frame-txt__title mt-3 mb-lg-3">聊天式日记</h2>
                                    <p class="frame-txt__desc d-none d-sm-block ml-0">在窗口输入回答即可完成日记</p>
                                    <p class="frame-txt__desc d-block d-sm-none  mt-2">回答问题快速生成日记</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="feature-frame__bottom--2 feature-frame__bottom  wow animated  animate__animated  animate__fadeInLeft">
                        <div class="feature-frame__connectedcards">
                            <div class="feature-frame__connectedcards-biggercard feature-frame__connectedcards-biggercard--toright frame__box--avatartxt">
                                <div class="frame-avatar">
                                    <div class="frame-avatar__wrapper  frame-avatar__wrapper--green frame-avatar">
                                        <svg class="svgicon-penwithpaper svgicon frame-avatar__svg">
                           <use :xlink:href="require('@/assets/icon/sprite.svg') + '#svgicon-penwithpaper'"></use>
                         </svg>
                                    </div>
                                </div>
                                <div class="frame-txt ml-2 ml-lg-5">
                                    <h2 class="frame-txt__title mt-3 mb-lg-3">自定义问题</h2>
                                    <p class="frame-txt__desc d-none d-sm-block ml-0">自由编辑自己需要的问题</p>
                                </div>
                            </div>
                        </div>
                        <div class="iconwrapper">
                            <svg class="svgicon-bookswithbg svgicon">
                  <use :xlink:href="require('@/assets/icon/sprite.svg') + '#svgicon-bookswithbg'"></use>
                </svg>
                        </div>
                    </div>
                </div>
            </div>
            <!-- feature2 文字部分 -->
            <div class="feature__main mt-5 mt-md-0  col-12 col-md-6 ">
                <div class="feature__main-wrapper ml-0 mr-lg-0 ml-lg-auto ">
                    <span class="feature__main-tag  feature__main-tag--red"><span class="iconfont mr-2">&#xe6cd;</span>日记书写</span>
                    <div class="feature__main-txt">
                        <h2 class="feature__main-txt--title">聊天式撰写日记</h2>
                        <p class="feature__main-txt--desc">易弦小助手通过询问您9个问题帮您生成九宫格日记，只需花费10分钟，记录您每一天的生活</p>
                        <ul class="feature__main-bulletpoints">
                            <li> <span class="feature__main-bulletpoints--prefix mr-3"></span> 九宫格日记</li>
                            <li> <span class="feature__main-bulletpoints--prefix mr-3"></span>自定义问题</li>
                        </ul>
                    </div>
                </div>
            </div>
        </section>
        <!-- 常见问题 -->
        <section class="questions" id="section-fqa">
            <div class="questions__illustration wow animated  animate__animated  animate__fadeInRight">
                <img src="@/assets/img/screenshotfadeout.png" alt="易弦用户界面截图">
            </div>
            <h2 class="questions__title">常见问题</h2>
            <ul class="questions-list">
                <li class="questions-list__item">
                    <span class="questions-list__item--que">是否收费？</span>
                    <span class="questions-list__item--ans">本网站不包含任何收费内容。</span>
                </li>
                <li class="questions-list__item">
                    <span class="questions-list__item--que">数据安全吗？</span>
                    <span class="questions-list__item--ans">您记录的数据都是加密储存的。(后台未实现加密，仅展示用)</span>
                </li>
            </ul>
        </section>
        <!-- footer部分 -->
        <section class="footer">
            <div class="footer__fullwidthbox">
                <div class="footer__content">
                    <ul class="footer__content-itemwrapper">
                        <li><button class="btn" style="color:inherit; font-size: inherit" @click="scrollToTop"> 返回顶部</button></li>
                        <li>
                            <router-link to="/ui-resources">关于网站使用的素材</router-link>
                        </li>
                        <li>yixian-offical@foxmail.com</li>
                        <li> &copy;2022 易弦-string</li>
                    </ul>
                </div>
            </div>
        </section>
    </div>
</template>

<script>
    import {
        scrollToTop
    } from '@/assets/js/utils.js'
    import Tooltip from '@/components/basicUI/Tooltip'
    export default {
        components: {
            Tooltip
        },
        data() {
            return {}
        },
        methods: {
            scrollToTop,
            // 页面自动滚动到相应的元素位置
            jumpTo(eleId) {
                document.getElementById(eleId).scrollIntoView({
                    behavior: 'smooth'
                })
            },
        },
        mounted() {

        },
        unmounted() {},
    }
</script>

<style lang="sass" scoped>
@import '@/assets/sass/components/hero.sass'
@import '@/assets/sass/components/headerguest.sass'
@import '@/assets/sass/components/features.sass'
@import '@/assets/sass/components/footer.sass'
@import '@/assets/sass/components/fqa.sass'
.bodybox
  // 避免移动设备字太小
  font-size: 1.5rem!important
.dropdown
  display: inline-block
  position: relative
.morebtn
  display: inline-block
  cursor: pointer
  width: max-content
.hidden-menu
  visibility: hidden
  color: #5D5D5D
  background: #fff
  z-index: 50
  line-height: 2
  padding: 3rem
  padding-right: 1rem
  top: 100%
  right: 0
  position: absolute
  border-radius: 1rem
  box-shadow: 3px 3px 6px rgba(0, 0, 0, .1)
  > *
    display: block !important
.bodybox
  position: relative
  overflow: hidden
  max-width: 1440px
  background: #fff
  margin: 0  auto
  box-shadow: 0 16px 48px rgba(255,255,255,0.5)
  padding: 1.6rem
  padding-bottom: 0
  @media(min-width: 376px)
    padding: 2.5rem
    padding-bottom: 0
  @media(min-width: 700px)
    padding: 3.3rem
    padding-bottom: 0
  @media(min-width: $bp-sm)
    padding: 2rem + calc(5% + 15px)
    padding-bottom: 0
  > *
    max-width: 1140px
    margin: 0 auto

.backgroundshape
  position: absolute
  right: 0
  top: 0
  transform: translate(28%,-86%)
  width: 140rem
  height: 80rem
  @media(min-width: $bp-sm)
    transform: translate(38%,-85%)
    width: 140rem
    height: 80rem
  @media(min-width: $bp-md)
    transform: translate(50%,-30%)
    width: 120rem
    height: 70rem
  @media(min-width: $bp-lg)
    transform: translate(40%,-30%)
    width: 120rem
    height: 70rem
  @media(min-width: $bp-xl)
    transform: translate(28%,-30%)
    width: 120rem
    height: 70rem
// 调整图标大小使得它和frame能保持一个适当的距离
.feature--2 .feature-frame__connectedcards-smallercard
  width: 5rem
  height: 5rem
  @media(min-width: 400px)
    width: 6rem
    height: 6rem
// ============ icon =====
.svgicon
  display: block
  &-logo
    width: 9.4rem
    height: 3.8rem
    cursor: pointer
  &-peoplewithhourglass,&-irregularshape,&-bookswithbg
    width: 100%
    height: 100%
</style>
