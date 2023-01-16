<template lang="html">
  <div>
    <div class="calendar">
      <!-- 日历头部 -->
      <div class="calendar__header">
        <!-- 上个月按钮 -->
        <button class="calendar__header-changemonthbtn--prev iconfont icon-prev btn"
          @click.prevent="subtractMonth">&#xe6a3;</button>
        <!-- 当前年月文字 -->
        <span class="calendar__header-title"><span>{{ year }}</span>年{{ month }}月</span>
        <!-- 下个月按钮 -->
        <button class="calendar__header-changemonthbtn--next iconfont icon-next btn" :disabled="nextMonthBtnDisabled"
          :class="{ 'calendar__header-changemonthbtn--disabled': nextMonthBtnDisabled }"
          @click.prevent="addMonth">&#xeee9;</button>
      </div>
      <!-- 日历日期部分 -->
      <div class="calendar__body">
        <!-- 星期 -->
        <ul class="calendar__weekdays calendar__gridcontainer ">
          <li v-for="(item, index) in days" :key="index" class="calendar__item calendar__item--week">{{ item }}</li>
        </ul>
        <!-- 日期 -->
        <ul class="calendar__dates calendar__gridcontainer">
          <!-- 月份第一天前面的空白，根据第一天的星期来决定，例如第一天是星期五，则从星期天开始有5个空白-->
          <li class="calendar__item calendar__item--blank" v-for="(blank, index) in firstDayOfMonth" :key="index"></li>
          <!-- 真正的日期 -->
          <li class="calendar__item calendar__item--date" v-for="(date, index) in daysInMonth" :key="index"
            :style="[isInLogs(date) && legend ? inLogsDateStyle : '']"
            :class="{ 'calendar__item--today': isToday(date), 'calendar__item--inlogs': isInLogs(date), 'calendar__item--date--selected': isSelected(date), 'calendar__item--aftertoday': onlySameAndBeforeSelectable && isAfter(date) }"
            @click="handleselected(date)">
            <span class="calendar__item-datetxt">{{ date }}</span>
          </li>
        </ul>
      </div>
    </div>
    <!-- 说明文字 -->
    <div class="legend" v-if="legend">
      <span class="legend-color" :style="{ background: legendColor }"> </span> <span
        class="legend-desc">{{ legendTxt }}</span>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    // 是否显示图例
    legend: {
      type: Boolean,
      default: false
    },
    legendTxt: {
      type: String,
      default: ''
    },
    legendColor: {
      type: String,
      default: ''
    },
    logs: { // logs中含有一系列日期，如果当前日期包含在logs中，则显示小圆点。
      type: Array,
      default: () => []
    },
    onlySameAndBeforeSelectable: {
      type: Boolean,
      default: false
    },
    defaultDate: { // 默认被选中的日期
      type: String,
      required: false,
    }
  },
  emits: [
    'select-date', // 当选中日期发送变化时
    'beforeToday', // 当选中任一过去的日期时
    'selected-again', // 当再次点击当前正被选中的日期时
    'month-change', // 当月份发生改变时
  ],
  data() {
    return {
      logsCopy: [], // 父组件传来的logs的备份，方便直接在这里修改数据
      today: this.$timeTool.getCurrUserTime(),
      days: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
      // 刚渲染时，如果设置了默认日期，则默认日期被选中，如果没有则当天日期被选中
      dateContext: this.defaultDate ? this.$dayjs(this.defaultDate) : this.$timeTool.getCurrUserTime(), // 当前日历所在日期（年月）
      selected: this.defaultDate ? this.$dayjs(this.defaultDate) : this.$timeTool.getCurrUserTime(), // 被选中的日期
    }
  },
  methods: {
    /**
     * 获取当前日历的年月
     */
    getContext() {
      return {
        y: this.year,
        m: this.month,
      }
    },
    isAfter(d) {
      const tobeComparedDate = `${this.year}-${this.month}-${d}`
      return this.$timeTool.compareDate(tobeComparedDate, this.today) === 1
    },
    /**
     * 月份加1
     */
    addMonth() {
      this.logsCopy = []
      const dateContextCurr = this.$dayjs(this.dateContext)
      this.dateContext = dateContextCurr.add(1, 'month')
      this.$emit('month-change', {
        y: this.year,
        m: this.month
      })
    },
    /**
     * 月份减1
     */
    subtractMonth() {
      // 先清空logscopy， 不然会出现切换月份后上月的logs紫色还显示的情况
      this.logsCopy = []
      const dateContextCurr = this.$dayjs(this.dateContext)
      this.dateContext = dateContextCurr.subtract(1, 'month')
      this.$emit('month-change', {
        y: this.year,
        m: this.month
      })
    },
    /**
     * 判断日期是否是今天的日期
     */
    isToday(date) {
      return date === this.dateToday && this.month === this.monthToday && this.year === this.yearToday
    },
    /**
     * 选择一个日期后的处理
     */
    handleselected(date) {
      if (this.onlySameAndBeforeSelectable) {
        if (this.isAfter(date, this.today)) {
          return
        }
      }
      if (this.isSelected(date)) {
        this.$emit('selected-again')
      } else {
        this.selected = this.$dayjs(this.selected).year(this.year).month(this.month - 1).date(date)
      }
    },
    /**
     * 判断一个日期是否被选中
     */
    isSelected(date) {
      return date === this.selectedDate && this.month === this.selectedMonth && this.year === this.selectedYear
    },
    /**
     * 判断一个日期是否在logs里
     */
    isInLogs(date) {
      date = date < 10 ? '0' + date : '' + date
      return this.logsCopy.includes(date)
    }
  },
  watch: {
    logs(newLogs) {
      this.logsCopy = newLogs
    },
    selected(selected) {
      this.$emit('select-date', selected)
    }
  },
  created() {

  },
  mounted() {
    // 被渲染时就发送month-change事件，以获取相关数据
    this.$emit('month-change', {
      y: this.year,
      m: this.month
    })
  },
  computed: {
    inLogsDateStyle() {
      return `background:${this.legendColor}`
    },
    nextMonthBtnDisabled() {
      // 当设置了不能选择今天之后的日期时，使得月份不能切换为本月之后的月份
      return this.onlySameAndBeforeSelectable && this.month === this.monthToday && this.year === this.yearToday
    },
    // 当前选中月份的天数
    daysInMonth() {
      return this.dateContext.daysInMonth()
    },
    // 当前选中几号
    currentDate() {
      return this.dateContext.get('date')
    },
    // 当前选中月第一天的星期
    firstDayOfMonth() {
      const firstDay = this.$dayjs(this.dateContext).subtract(this.currentDate - 1, 'day')
      return firstDay.weekday()
    },
    // 当前context的年份
    year() {
      return this.dateContext.format('YYYY')
    },
    // 当前context月份
    month() {
      return this.dateContext.format('M')
    },
    // 今天的数据， 也是initial data
    dateToday() {
      return this.today.get('date')
    },
    monthToday() {
      return this.today.format('M')
    },
    yearToday() {
      return this.today.format('YYYY')
    },
    // 被选中的日期的数据
    selectedDate() {
      return this.selected.get('date')
    },
    selectedMonth() {
      return this.selected.format('M')
    },
    selectedYear() {
      return this.selected.format('YYYY')
    }
  }
}
</script>

<style lang="sass" scoped>
@import '@/assets/sass/abstract/mixins.sass'

.calendar
  padding: 1rem
  font-size: 1.3rem
  color: #7C7B7F
  user-select: none
  width: 100%
  &__icon
    cursor: pointer
    margin-right: 1rem
  &__header
    font-size: 1.6rem
    // padding 可能还需要调整
    margin-bottom: 1rem
    display: flex
    justify-content: center
    align-items: center
    &-title
      color: #7C7B7F
      // xx年xx月
      font-size: 1.4rem
    &-changemonthbtn
      &--prev, &--next
        background-color: #fff
        height: 2.3rem
        width: 2.3rem
        border-radius: 50%
        color: #7C7B7F
      &--prev
        margin-right: 1rem
      &--disabled
        color: #C4C4C4
        cursor: default
  &__weekdays
    padding: 1rem 0
    border-top: 1px solid #F7F7F7
    border-bottom: 1px solid #F7F7F7
    color: #C4C4C4
    margin-bottom: 1rem
    cursor: pointer
    font-size: 1.2rem
  &__gridcontainer
    display: grid
    grid-template-columns: repeat(7,1fr)
    row-gap: 1rem
  &__item
    display: flex
    justify-content: center
    align-items: center
    height: 2.5rem
    width: 2.5rem
    position: relative
    border-radius: 50%
    cursor: default
    &-datetxt
      // 视错觉导致当第一个数字是1时，显得不居中
      margin-left: -.5px
    &--inlogs
      color: #fff !important
    &--date
      border: 1px solid #fff
      cursor: pointer !important
      &:hover,&--selected
        background: #FFBF46 !important
        color: #fff !important
      &--selected
        box-shadow: .2rem 1rem 1rem rgba(226, 159, 159, .16)
    &--today
      background: #F4F7F8
      color: inherit
    &--aftertoday
      color: #C4C4C4
      cursor: default !important
      &:hover
        background: #fff !important
        color: #C4C4C4  !important
.legend
  margin-left: 1rem
  &-color
    display: inline-block
    width: 1rem
    height: 1rem
    border-radius: 50%
    margin-right: 1rem
  &-desc
    color: #8F9EB0

</style>
