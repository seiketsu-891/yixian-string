<template lang="html">
   <!-- 404时的显示界面  -->
   <div class="error-container">
        <!-- 头部 -->
        <header class="header">
            <!-- logo -->
            <div class="header-logo">
                <svg class="svgicon-logo svgicon" @click="goToIndex">
        <use :xlink:href="require('@/assets/icon/sprite.svg') + '#svgicon-logo'"></use>
      </svg>
            </div>
        </header>
        <!-- 内容区域： 404截图 -->
        <div class="error-body">
            <img src="@/assets/img/404.jpg" alt="404页面提示">
        </div>
    </div>
</template>
<script>
export default {
methods:{
    goToIndex(){
      this.$router.push('/')
    }
  }
}
</script>
<style lang="sass" scoped>
.error-container
  width: 100vw
  height: 100vh
  display: flex
  flex-direction: column
.header
  width: 100%
  padding: 4rem
  height: 8rem
  background-color: white
  position: relative
  .svgicon-logo
    position: absolute
    top: 50%
    margin-top: -1.9rem
    width: 9.4rem
    height: 3.8rem
    cursor: pointer
.error-body
   flex-grow: 1
   width: 100%
   display: flex
   justify-content: center
   align-items: center
   img
     border-radius: 2rem
     width: 80%
     max-width: 40rem
</style>
