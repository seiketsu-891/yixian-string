<template lang="html">
  <!-- 进度条 -->
  <div class="progressbar__container" :style="{ width: width }">
    <div  class="progressbar__wrapper progressbar__wrapper--linear" :style="{ height: barHeight }">
      <!-- 进度条完整部分 -->
      <div class="progressbar__full" :style="{ 'background-color': background }">
      </div>
      <!-- 进度条有进度的部分 -->
      <div class="progressbar__progress"
        :style="{'background-color': progressColor, width: percentage + '%', height: barHeight }">
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    percentage: Number,  // 进度百分比
    background: {   // 进度条背景颜色
      type: String,
      default: '#E3E9EC'
    },
    progressColor: { // 进度条颜色
      type: String,
      default: '#F17157'
    },
    width: {
      type: String,
      default: '80px'
    },
    barHeight: {  
      type: String,
      default: '10px'
    }
  }
}
</script>

<style lang="sass" scoped>
.progressbar
  &__wrapper
    width: 100%
    &--linear
      position: relative
      border-radius: 5px
      overflow: hidden
  &__full
    width: 100%
    height: 100%
  &__progress
    border-radius: 5px
    position: absolute
    left: 0
    top: 0
    animation-name: growingbar
    animation-duration: 1s
    animation-timing-function: ease-out

@keyframes growingbar
  0%
    opacity: 0
    width: 0
  100%
    opacity: 1

</style>
