<template lang="html">
  <!-- 切换小屏幕全屏菜单的按钮 -->
  <div class="togglebtn__container" @click.prevent="toggleMenu">
    <!-- 三个横线 -->
    <span class="togglebtn__topline" :class="[menuActive ? 'rotatedown' : '']"></span>
    <span class="togglebtn__middleline" v-if="menuActive === false"></span>
    <span class="togglebtn__bottomline" :class="[menuActive ? 'rotateup' : '']"></span>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        menuActive: false
      }
    },
    methods: {
     /**
      * 切换菜单状态
      */
     toggleMenu() {
        this.menuActive = !this.menuActive
        if (this.menuActive) {
          document.body.style.overflow = "hidden"
        } else {
          document.body.style.overflow = "scroll"
        }
        // 向overlaymenu传值
        this.$emitter.emit('toggle-menu', this.menuActive)
      }
    }
  }
</script>

<style lang="sass" scoped>
.togglebtn
  &__container
   position: relative
   cursor: pointer
  &__middleline, &__topline, &__bottomline
    display: block
    position: absolute
    transition: all .4s
    height: 3px
    background: #696969
  &__topline
    width: 2.8rem
    top: 0
  &__middleline
    width: 1.5rem
    top: 50%
    transform: translateY(-50%)
  &__bottomline
    width: 2.8rem
    bottom: 0
.rotatedown
  top: 50%
  transform:  rotate(45deg)
.rotateup
  top: 50%
  transform:  rotate(-45deg)

</style>
