<template lang="html">
  <!-- 单个时间条目（用于用户首页的展示） -->
  <div class="entry__container">
    <!-- 左边的颜色条 -->
    <div class="entry--left">
      <div class="entry__colorbox" :style="{ 'background': color }"></div>
      <!-- 信息 -->
      <div class="entry__info">
        <span class="entry__info-desc">{{ description }}</span>
        <span class="entry__info-time">
            <span class="entry__info-timeicon iconfont">&#xe77b;</span> {{ startTime }}-{{ endTime }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      description: String,
      startTime: String,
      endTime: String,
      color: String
    }
  }
</script>

<style lang="sass" scoped>
@import '@/assets/sass/abstract/mixins.sass'
.entry
  &__container
    display: flex
    justify-content: space-between
    width: 100%
    margin-bottom: 10px
    height: max-content
    overflow: hidden
  &--left
    display: flex
    width: 100%
    padding-right: 8px
  &__colorbox
    flex-shrink: 0
    width: 4px
    height: 3rem
    margin-top: 3px
    border-radius: 2px
    margin-right: 1rem
  &__info
    &-desc
      padding-top: 3px
      display: block
      margin-bottom: 8px
      font-size: 14px
    &-time
      color: #ADADAD
      font-size: 12px
    &-timeicon
      margin-right: 6px
  &__timespan
    width: max-content
    height: 4rem
    flex-shrink: 0
    background-color: #F6F7FB
    text-align: center
    line-height: 4rem
    padding: 0 8px
    color: #6C769B
    border-radius: 8px
</style>
