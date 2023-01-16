<template lang="html">
  <div class="container-fluid">
    <div class="row">
      <div class="col-12 col-md-8 col-xl-9">
        <card rounded>
          <!-- 导航区 -->
          <bread-crumb class="mb-5" :data="breadcrumbData"> </bread-crumb>
          <div class="top-msg" v-if="isPastDates()">
            <span class="top-msg__txt">提示：已经过去的日期无法添加新的代办</span>
          </div>
          <!-- 日历 -->
          <basic-calendar :default-date="hasValidQueryDate()?$route.params.date: '' " ref="calendar" legend legendTxt="表示该日有未完成代办" :logs="monthHistory" @month-change="handleCalendarChangeMonth($event,yearAndMonth)" :legendColor="calendarLegendColor" @select-date="onSelectDate"
            class="d-md-none"></basic-calendar>
          <!-- 待办显示区 -->
          <div class="row">
            <div class="mt-5 mt-md-0 col-12 col-lg-6">
              <todo-collection :date="currFormattedDate" :data="taskList" priority="11" @update="handleTodoUpdate($event)"></todo-collection>
            </div>
            <div class="mt-5 mt-md-0 col-12 col-lg-6">
              <todo-collection :date="currFormattedDate" :data="taskList" priority="10" @update="handleTodoUpdate($event)"></todo-collection>
            </div>
            <div class="mt-5  col-12 col-lg-6">
              <todo-collection :date="currFormattedDate" :data="taskList" priority="01" @update="handleTodoUpdate($event)"></todo-collection>
            </div>
            <div class="mt-5 col-12 col-lg-6">
              <todo-collection :date="currFormattedDate" :data="taskList" priority="00" @update="handleTodoUpdate($event)"></todo-collection>
            </div>
          </div>
        </card>
      </div>
      <div class="container__right d-none d-md-block col-4 col-xl-3">
        <card class="container__right--card right" rounded>
          <div class="right__calendarcontainer calendar">
            <!-- 电脑端的日历 -->
            <basic-calendar :default-date="hasValidQueryDate()?$route.params.date: '' " ref="calendar" legend legendTxt="表示该日有未完成待办" :logs="monthHistory" @month-change="handleCalendarChangeMonth($event)" :legendColor="calendarLegendColor" @select-date="onSelectDate"
              class="calendar--desktop"> </basic-calendar>
          </div>
          <!-- 完成情况环形图 -->
          <div class="right__progresscontainer progress">
            <span class="progress__title">完成情况</span>
            <todo-progress class="progress__chart" :percentage="donePercentage"></todo-progress>
            <span class="progress__text">
                 {{countDone}}/{{countTotal}}
              </span>
          </div>
        </card>
      </div>
    </div>
  </div>
</template>

<script>
  import {
    mapGetters
  } from 'vuex'
  import TodoService from '@/apis/TodoService.js'
  import BreadCrumb from "@/components/basicUI/BreadCrumb";
  import Card from "@/components/Card";
  import BasicCalendar from "@/components/BasicCalendar";
  import TodoProgress from "@/components/graph/TodoProgress";
  import TodoCollection from "@/components/TodoCollection";
  export default {
    components: {
      Card,
      BasicCalendar,
      BreadCrumb,
      TodoProgress,
      TodoCollection,
    },
    mounted() {
      this.readDateFromUrlIfExisted()
      this.getTaskList()
    },
    data() {
      return {
        calendarLegendColor: '#b3b0ed',
        // 存储当月有未完成待办的日期
        monthHistory: [],
        test: 100,
        taskList: [], // todo list
        // 默认日期为当天
        selectedDate: this.$timeTool.getCurrUserTime(),
        breadcrumbData: [{
            id: 1,
            name: "用户首页",
            href: "/home",
          },
          {
            id: 2,
            name: "待办管理",
            href: "/todos",
          },
        ],
      }
    },
    computed: {
      currFormattedDate() {
        return this.$timeTool.parseYMD(this.selectedDate)
      },
      countTotal() { // 任务总计数量
        return this.taskList.length
      },
      countDone() { // 已完成任务数
        return this.taskList.filter(item => item.done).length
      },
      donePercentage() { // 完成的百分比
        // 一开始list数据没获取到或list为空时，分母会变为0
        if (this.countTotal === 0) {
          return 0
        } else {
          return +(this.countDone / this.countTotal * 100)
        }
      },
      ...mapGetters([
        'user'
      ]),
    },
    methods: {
      /**
       * 如果地址有日期，则读取地址中的日期
       */
      readDateFromUrlIfExisted() {
        if (this.hasValidQueryDate()) {
          this.selectedDate = this.$route.params.date
        }
      },
      /**
       * 是否是已经过去的日期
       */
      isPastDates() {
        const today = this.$timeTool.parseYMD(this.$timeTool.getCurrUserTime())
        const comp = this.$timeTool.compareDate(this.currFormattedDate, today)
        return comp === -1
      },
      /**
       * 地址栏是否有有效日期
       */
      hasValidQueryDate() {
        if (!this.$route.params || !this.$route.params.date) {
          return false
        }
        return this.$timeTool.isYMDformat(this.$route.params.date)
      },
      /**
       * todo更新后的处理
       * @param {*} args 
       */
      handleTodoUpdate(args) {
        this.getTaskList()
        const oper = args.operation
        if (oper === 'add' || oper === 'del' || oper === 'markDone') {
          // 手动触发changemonth，以重新读取history
          this.handleCalendarChangeMonth(this.$refs['calendar'].getContext())
        }
      },
      /**
       * 日历切换月份后的处理
       * @param {*} yearAndMonth 
       */
      handleCalendarChangeMonth(yearAndMonth) {
        //读取本月哪些天有未完成的待办
        const year = yearAndMonth.y
        let month = yearAndMonth.m
        if (month < 10) {
          month = '0' + month
        }
        this.readTodoMemoMonthHistory(year, month)
      },
      // 请求读取本月未完成待办
      async readTodoMemoMonthHistory(y, m) {
        const res = await TodoService.readMonthHistory(this.user.id, y, m)
        if (res.success) {
          const hasUndonesDates = res.content
          this.monthHistory = hasUndonesDates
        } else {
          this.$toast.error('读取代办完成记录失败')
        }
      },
      // 请求获取代办列表
      async getTaskList() {
        const date = this.$timeTool.formatInYMD(this.selectedDate)
        const res = await TodoService.list(this.user.id, date)
        if (res.success) {
          this.taskList = res.content
        } else {
          this.$toast.error('获取待办失败')
        }
      },
      // 在日历上选择日期的处理
      onSelectDate(newDate) {
        this.selectedDate = newDate
        const date = this.currFormattedDate
        this.$router.push({
          name: 'todos',
          params: {
            date
          }
        })
      }
    },
    watch: {
      selectedDate() {
        this.getTaskList()
      }
    }
  }
</script>

<style lang="sass" scoped>
.top-msg
  margin-bottom: 1rem
  margin-top: -2rem
  &__txt
    display: block
    padding: .8rem 1.2rem
    width: max-content
    font-size: 1.4rem
    color: #7E8B94
.container
  &__right
    &--card
      padding-left: .9rem !important
      padding-right: .9rem !important
.right
  user-select: none
  &__calendarcontainer
    // background: pink
    max-width: 40rem
    margin: 0 auto
  &__progresscontainer
    max-width: 40rem
    margin: 0 auto
    background: #FBFCFD
    border-radius: 1.2rem
    border: 1px solid #EFF1F7
    padding: .8rem
    margin-top: 3rem
  .progress
    display: flex
    flex-direction: column
    text-align: right
    &__title
      width: max-content
      align-self: flex-end
      padding: .3rem 2rem
      background: #EEF2F7
      border-radius: 1.2rem
      display: inline-block
      margin-bottom: .3rem
    &__chart
      align-self: center
    &__text
      align-self: center
      margin-top: -.4rem
</style>
