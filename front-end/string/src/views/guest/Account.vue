<template lang="html">
    <!-- 账号信息相关（登录、注册、找回密码）的基本layout-->
    <div class="main-container">
        <div class="form-wrapper card--paperlike__container">
            <div class="form form-wrapper__form card--paperlike__paper">
                <!-- 接收子组件emit的update-input-focused值，以更新页面中铅笔的旋转角度-->
                <router-view @update-input-focused="val => inputFocusedIndex = val">
                </router-view>
            </div>
        </div>
        <div class=" d-none d-md-block main-container__img">
            <!-- 铅笔图片样式中的角度动态获取 -->
            <img src="../../assets/img/pen.png" class="img-item" :style="{ transform: 'rotate(' + pencilDeg + ')' }" alt="">
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            // 默认index为1，也就是默认铅笔不倾斜
            inputFocusedIndex: -1
        }
    },
    methods: {
    },
    computed: {
        // 计算铅笔倾斜的角度
        pencilDeg: function () {
            // index每加1，向左移8度。
            return (this.inputFocusedIndex + 1) * (-8) + 'deg'
        }
    }
}
</script>

<style lang="sass" scoped>
@import '@/assets/sass/components/card.sass'
.main-container
  min-height: 100vh
  height: max-content
  // 使网页放大时能水平滚动而不因屏幕大小限制裁剪内容
  width: 100%
  min-width: max-content
  display: flex
  align-items: center
  justify-content: center
  &__img
    width: 20rem
    height: 60rem
  .img-item
   height: 100%
   transition: all .8s
.form-wrapper
  width: max-content
  height: max-content
.form
  width: max-content
  height: max-content
  border: none
  position: relative
  display: flex
  padding: 2rem
  $bp-sm: 576px
  $bp-md: 768px
  $bp-lg: 992px
  $bp-xl: 1200px
  @media( min-width: $bp-sm)
    padding: 5rem
</style>
