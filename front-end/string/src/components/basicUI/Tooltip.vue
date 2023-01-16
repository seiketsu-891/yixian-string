<template lang="html">
  <!--文字提示 -->
  <div class="tooltip__box" @mouseover="show = true" @mouseleave="show=false">
    <!-- 鼠标放在下面这部分文字上面即可出现tooltip-->
    <a href="#" :style="txtStyle">{{txt}}</a>
    <!-- 对话框tip区域 -->
    <div v-show="show" class="tooltip">
      <!-- 为了在鼠标移动到上方文字和tip中间空白时tip不消失，所以设置这个span -->
      <span class="tooltip__space"></span>
      <span class="triangle"></span>
      <div class="tooltop__content" :style="{width: width}">
        <slot>
          <!-- 此处插入tooptip里的文字 -->
        </slot>
      </div>
    </div>
  </div>
</template>
<script>
  export default {
    name: 'Tooltip',
    props: {
      width: {
        type: String,
        default: '50vw'
      },
      txt: { // 原文
        type: String,
        required: true
      },
      // 修改上方文字格式
      txtStyle: {
        type: String,
        required: false
      }
    },
    data() {
      return {
        show: false,
      }
    }
  }
</script>

<style lang="sass" scoped>
.tooltip__box--enterable
  position: absolute
  left: 0
  border: 1px solid black
  background: pink
.tooltip
  cursor: pointer
  max-width: 80vh
  z-index: 999
  background: #f9c658
  color: #fff
  padding: 1rem 2rem 1rem 2rem
  border-radius: 1rem
  position: absolute
  box-shadow: .3rem .4rem 1.8rem #e0e6f0
  top: calc(100% + 12px)
  left: 0
  &__box
    width: max-content
    position: relative
    height: min-content
  &__space
    cursor: default
    height: 1.2rem
    width: 100%
    left: 0
    top: -1.2rem
    height: 1.2rem
    position: absolute
.triangle
  width: 0
  height: 0
  border-style: solid
  border-width: 0 5px 6px 5px
  border-color: transparent transparent #f9c658 transparent
  left: 2rem
  position: absolute
  top: -6px
</style>
