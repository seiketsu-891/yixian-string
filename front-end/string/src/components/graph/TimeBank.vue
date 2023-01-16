<template lang="html">
  <!-- 时间银行（本年已过进度条和本月已过进度条） -->
  <div class="timebank" :class="{ 'timebank--vertical': isVerticalLayout }">
    <div class="timebank__main">
      <!-- 年的部分 -->
      <div class="timebank--year">
        <div class="timebank__msg">
          本年已过<span class="timebank__msg-percentage">{{ yearPercentage }}%</span>
        </div>
        <div class="timebank__progressbar">
          <progress-bar  width="100%" :percentage="yearPercentage" :progressColor="yearBarColor" :barHeight="heightBar"></progress-bar>
          <span class="timebank__progressbar-label">{{ data.year.passed }}/{{ data.year.total }}</span>
        </div>
      </div>
      <!-- 月的部分 -->
      <div class="timebank--month">
        <div class="timebank__msg">
          本月已过<span class="timebank__msg-percentage">{{ monthPercentage }}%</span>
        </div>
        <div class="timebank__progressbar">
          <progress-bar width="100%" :percentage="monthPercentage" :progressColor="monthBarColor" :barHeight="heightBar"></progress-bar>
          <span class="timebank__progressbar-label">{{ data.month.passed }}/{{ data.month.total }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import {
    calcPercentage
  } from '@/assets/js/mathUtils.js'
  import ProgressBar from '@/components/basicUI/ProgressBar'
  export default {
    components: {
      ProgressBar
    },
    props: {
      layout: {
        type: String, //  vertical指的是两个在一行并排显示，horizontal指每个排一行
        default: 'horizontal'
      },
    },
    data() {
      return {
        yearBarColor: '#86C8B1', // 本年部分的进度条颜色
        monthBarColor: '#F8A071', // 本月部分的进度条颜色
        heightBar: '.6rem', // 进度条高度
        data: {}, // 用于存储当年/月已过天数、总共天数
        // timer: 0 // 定时器（用于及时更新数据）
      }
    },
    computed: {
      // 用于更改class，以设置布局
      isVerticalLayout() {
        return this.layout === 'vertical'
      },
      yearPercentage() {
        return +calcPercentage(this.data.year.passed, this.data.year.total)
      },
      monthPercentage() {
        return +calcPercentage(this.data.month.passed, this.data.month.total)
      }
    },
    created() {
      // 获取数据
      this.updateData()
      // // 数据实时更新
      // this.timer = setInterval(this.updateData, 1000)
    },
    unmounted() {
      // if (this.timer) {
      //   clearInterval(this.timer)
      // }
    },
    methods: {
      /**
       * 获取时间银行相关的数据
       */
      updateData() {
        this.data = this.$timeTool.getTimeBankData()
      }
    }
  }
</script>

<style lang="sass" scoped>
.timebank--year, .timebank--month
  display: flex
  padding: 1rem
.timebank
  user-select: none
  &__progressbar
    width: 100%
    display: inline-block
    display: flex
    justify-content: center
    align-items: center
    &-label
      min-width: 4rem
      color: #ADADAD
      font-size: 1.2rem
      padding: 0  .5ren
  &__msg
    min-width: max-content
    display: inline-block
    color: #ADADAD
    font-size: 1.3rem
    margin-right: 2rem
    &-percentage
      display: block
      font-size: 2.6rem
      color: #5D5D5D
.timebank--vertical
  .timebank__main
     display: flex
     justify-content: space-between
  .timebank--year, .timebank--month
    flex-direction: column
    width: 100%
.chart__title
  font-size: 1.7rem
  color: #393E45



</style>
