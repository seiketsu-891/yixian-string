<template>
  <!-- loading状态的旋转效果 -->
  <div class="spinner" :style="[{height: height}]">
    <!-- loading效果图 -->
    <span class="loader"></span>
  </div>
</template>

<script>
  export default {
    props: {
      height: {
        type: String,
        required: false
      }
    }
  }
</script>

<style lang="sass" scoped>
.spinner
  // 外部容器，让旋转图始终保持在中间
  display: flex
  justify-content: center
  align-items: center
.loader
  box-sizing: border-box
  width: 4.8rem
  height: 4.8rem
  border: .3rem solid #F9C779
  border-bottom-color: transparent
  border-radius: 50%
  display: inline-block
  animation: rotation 1s linear infinite

  @keyframes rotation
    0%
      transform: rotate(0deg)
    100%
      transform: rotate(360deg)

</style>
