<template lang="html">
  <!-- 当没有时间条目和待办时的显示图 -->
  <!-- 待办 -->
  <div v-if="items === 'todos'" class="">
    <div class=" placeholder ">
      <svg v-if="icon" class="svgicon-notasks placeholder-icon">
        <use :xlink:href="require('@/assets/icon/sprite.svg') + '#svgicon-notasks'"></use>
      </svg>
      <span class="placeholder-txt mt-3" v-if="allDone">所有待办均已完成</span>
      <span class="placeholder-txt mt-3" v-if="!allDone">暂时没有待办</span>
    </div>
  </div>
  <!-- 时间条目 -->
  <div v-if="items === 'timeEntries'" class="">
    <div class="placeholder">
      <svg v-if="icon" class="svgicon-notimeentries placeholder-icon">
        <use :xlink:href="require('@/assets/icon/sprite.svg') + '#svgicon-notimeentries'"></use>
      </svg>
      <span v-if="span === 'week'" class="placeholder-txt mt-3">该周尚无任何时间条目</span>
      <span v-else class="placeholder-txt mt-3">当日尚无任何时间条目</span>
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      items: {  // 空状态是待办还是时间条目 ['todos, timeEntries']
        type: String, 
        default: 'default'
      },
      span: {  // 时间条目是一周还是当天
        type: String,
        required: false
      },
      icon: {  // 是否显示图
        type: Boolean,
        default: true
      },
      allDone: { // 是否待办都已完成
        type: Boolean,
        default: false
      }
    }
  }
</script>

<style lang="sass" scoped>
  .placeholder
    display: flex
    flex-direction: column
    align-items: center
    justify-content: center
    margin: 2rem
    &-txt
      // margin-top: 3rem
      display: block
      color: #B7B7B7
    &-icon
      height: 10rem
</style>
