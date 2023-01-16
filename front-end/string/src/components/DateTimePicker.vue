<template lang="html">
  <!-- 带日期选择的输入框 -->
  <div class="input__wrapper">
    <input autocomplete="false" spellcheck="false" v-model="time" type="text" class="inputbox" @change="$emit('update: time')" @blur="validateTime">
    <button v-if="dateChangable" @click.prevent="toggleCalendarVisibility" class="suffix datepicker datepicker-btn btn">
            <!-- 当未选择或传入不合法数据或者选择了今天，显示今天 -->
            <span v-if="!selectedDate || !isValidDate(selectedDate) || isToday(selectedDate)">今天</span>
            <span v-else>{{ selectedDate }}</span>
      </button>
    <!-- 日历部分 -->
    <div class="calendar__wrapper" v-if="calendarVisible">
      <basic-calendar :defaultDate="selectedDate" v-click-away="onClickAwayFromCalendar" onlySameAndBeforeSelectable @selected-again="handleDateChange" ref="calendar" @select-date="handleDateChange()" class="calendar">
      </basic-calendar>
    </div>
  </div>
</template>

<script>
  import {
    mixin as VueClickAway
  } from "vue3-click-away"
  import BasicCalendar from '@/components/BasicCalendar'
  export default {
    mixins: [VueClickAway],
    emits: ['{update: time}'],
    components: {
      BasicCalendar
    },
    props: {
      hasSiblings: { // 为true时候表示它日期变化会引起sibling的日期变化，例如输入框右边有个日期
        type: Boolean,
        default: false,
      },
      noLaterDate: { // 日期只能是今天及之前
        type: Boolean,
        default: true
      },
      dateChangable: { // 输入框是否允许改变日期，如果不允许，则不会在右边显示日期和点击日期后显示日历
        type: Boolean,
        default: true
      },
      noLaterTime: { // 时刻不能晚于现在
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        time: this.$timeTool.formatInHourMinS(this.$timeTool.getCurrUserTime()),
        today: this.$timeTool.getCurrUserTime(),
        selectedDate: this.$timeTool.parseYMD(this.$timeTool.getCurrUserTime()),
        calendarVisible: false,
      }
    },
    methods: {
      /**
       * 从该组件获取日期时间数据
       */
      getOutput() {
        // 如果设定了时间不能晚于现在但却有时间晚于现在，则返回-1
        if (this.noLaterTime && this.$timeTool.compareDate(this.output, this.$timeTool.getCurrUserTime()) === 1) {
          return -1
        }
        return this.output
      },
      /**
       * 点击别处后关闭日历
       */
      onClickAwayFromCalendar() {
        if (this.calendarVisible) {
          this.calendarVisible = false
        }
      },
      /**
       * 把用户传来的时间点数据变成合法数据
       */
      validateTime() {
        this.time = this.$timeTool.formatInHourMinS(this.time, true)
      },
      /**
       * 日期是否是今天
       * @param {string} s  ymd日期
       */
      isToday(s) {
        return this.$timeTool.compareDate(s, this.$timeTool.parseYMD(this.$timeTool.getCurrUserTime())) === 0
      },
      /**
       * 是否是合法日期数据
       * @param {string} s  日期
       */
      isValidDate(s) {
        if (this.noLaterDate) {
          return this.$timeTool.isYMDformat(s) && this.$timeTool.compareDate(s, this.$timeTool.parseYMD(this.$timeTool.getCurrUserTime())) != 1
        } else {
          return this.$timeTool.isYMDformat(s)
        }
      },
      /**
       * 在日历点击某个日期后的处理
       */
      handleDateChange() {
        this.toggleCalendarVisibility()
        this.updateDate(this.$timeTool.formatInYMD(this.$refs.calendar.selected))
        // 让输入框右边的日期文字也发生变化
        if (this.hasSiblings) {
          this.$emitter.emit('forceUpdateDate', this.selectedDate)
        }
      },
      /**
       * 更改选中的日期数据
       * @param {string} d 新日期y-m-d
       */
      updateDate(d) {
        this.selectedDate = d
      },
      /**
       * 改变日历可见性
       */
      toggleCalendarVisibility() {
        this.calendarVisible = !this.calendarVisible
      },
    },
    mounted() {
      if (this.hasSiblings) {
        this.$emitter.on('forceUpdateDate', d => this.updateDate(d))
      }
    },
    unmounted() {
      if (this.hasSiblings) {
        this.$emitter.off('forceUpdateDate', d => this.updateDate(d))
      }
    },
    computed: {
      // 输出日期时间数据
      output() {
        return this.selectedDate + ' ' + this.time
      }
    }
  }
</script>

<style lang="sass" scoped>
.input__wrapper
  position: relative
  width: 19.3rem
  color: #ADADAD
.inputbox
  line-height: 3.5rem
  width: 100%
  height: 4.2rem
  border: 1px solid #E0E3EC
  padding: 0 6rem 0 15px
  color: #606266
  &:hover
    border: 1px solid #C2C4D0
.datepicker
  position: absolute
  right: 15px
  cursor: pointer
  &-btn
    height: 100%
    width: 5rem
.calendar
  padding: 10px
  box-shadow: 1px 3px 5px #dfe8ed
  z-index: 999999
  float: right
  position: absolute
  bottom: 3rem
  left: 87%
  width: max-content
  height: max-content
  background: #fff
  &__wrapper
    width: max-content
    height: max-content
.calendar
  // 在小屏幕时日历在输入框上方，避免放右边屏幕装不下
  @media(max-width: $bp-sm)
    left: 0
    bottom: 4rem

</style>
