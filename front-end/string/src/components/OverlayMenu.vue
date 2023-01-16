<template lang="html">
  <!--  小屏幕上的全屏菜单 -->
  <transition name="expandablecircle">
    <div class="overlaymenu__container overlaymenu" v-if="active">
      <ul class="overlaymenu__wrapper">
        <li class="overlaymenu__item" v-for="item in menuList" :key="item.id"><router-link @click="onMenuClick"
            class="overlaymenu__item-link" :to="item.href">{{ item.menuName }}
            <span class="iconfont icon icon-arrow-right">&#xe662;</span></router-link>
        </li>
      </ul>
    </div>
  </transition>
</template>

<script>
export default {
  props: {
    menuList: [Object],
  },
  data() {
    return {
       active: false // 是否处于显示状态
  }
},
  methods: {
    /**
     * 菜单项目点击后的处理
     */
    onMenuClick(){
      if(this.active){
        this.active = false
      }
    },
    /**
     * 切换菜单显示状态
     * @param {Boolean} menuActive 
     */
    toggleMenuHandler(menuActive) {
        this.active = menuActive
      }
    },
  mounted() {
    // 接收togglenavbtn传来的事件
    this.$emitter.on('toggle-menu',this.toggleMenuHandler)
  }
}
</script>

<style lang="sass" scoped>
@import '@/assets/sass/abstract/mixins.sass'
.overlaymenu
  z-index: 1900
  width: 100%
  height: 100%
  position: fixed
  top: 0
  left: 0
  display: flex
  justify-content: center
  align-items: center
  user-select: none
  background-color: #F3CB48
  clip-path: circle(100% at center)
  &__wrapper
    cursor: pointer
    @include absCenter
    width: 100%
    display: flex
    flex-direction: column
    justify-content: space-between
    align-items: center
  &__item
    &:not(:last-of-type)
      margin-bottom: 20px
  &__item-link
    display: block
    text-align: center
    width: 15rem
    height: 4.3rem
    line-height: 4.3rem
    position: relative
    font-size: 20px
    -webkit-transition: all .4s
    transition: all .4s
    // FF 3.6+
    background-image: -moz-linear-gradient(120deg, transparent 0%, transparent 50%,  #fff 50%)
    //  /* IE10 */
    background-image: -ms-linear-gradient(120deg, transparent 0%, transparent 50%,  #fff 50%)
    /* Safari 4+, Chrome 2+ */
    background-image: -webkit-gradient(120deg, transparent 0%, transparent 50%,  #fff 50%)
    /* Safari 5.1+, Chrome 10+ */
    background-image: -webkit-linear-gradient(120deg, transparent 0%, transparent 50%,  #fff 50%)
    /* Opera 11.10 */
    background-image: -o-linear-gradient(120deg, transparent 0%, transparent 50%,  #fff 50%)
    background-image: linear-gradient(120deg, transparent 0%, transparent 50%,  #fff 50%)
    background-size: 220%
    // ：active效果在手机上不起作用，通过ontouchstart=""的话，导致可能动画完成前页面跳转，所以去掉ontouchstart=""
    &:hover,&.router-link-active,&.router-link-exact-active
      background-position: 100%
    .icon-arrow-right
      margin-left: 5px
      font-size: 20px
.expandablecircle-enter-from, .expandablecircle-leave-to
  background-color: #F3CB48
  clip-path: circle(17rem at -17rem  -17rem )
.expandablecircle-enter-active, .expandablecircle-leave-active
  -webkit-transition: all .6s
  transition: all .6s
</style>
