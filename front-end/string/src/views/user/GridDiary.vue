<template lang="html">
  <!-- 日记页面 -->
  <div class="container-fluid">
    <div class="row">
      <!-- left -->
      <div class="col-12 col-lg-8 col-xxl-10">
        <div class="diary__returntotoday">
          <button class="btn shadowbtn" v-show="currentView!=='writing'" @click="handleReturnToChatClick" style="width: max-content"><span class="iconfont icon-return">&#xe74d;</span><span>返回聊天窗口</span></button>
        </div>
        <!-- 日记问题区域，仅在ques视图时可见 -->
        <div class="diary__questions" v-if="currentView==='ques'">
          <diary-question-grids :data="inUseQuestions" @update="getInUseQuestions()"> </diary-question-grids>
        </div>
        <!-- 日记内容区域，仅在content视图时可见 -->
        <div class="diary__content" v-if="currentView==='content'">
          <div class="diary__content-header">
            <span class="diary__content-date">{{currSelectedDate}}&nbsp;{{$timeTool.getWeekDay(currSelectedDate)}}</span>
          </div>
          <loading-spinner height="30rem" v-show="loading.diaryEntries" />
          <diary-grids v-show="!loading.diaryEntries" :diaries="currInViewDiary" @delete="handleDiaryDeleted(currSelectedDate)" @update="getDiaryEntries(currSelectedDate)"></diary-grids>
          <div class="diary__content--emptystate" v-show="isCurrDiaryEmpty && !loading.diaryEntries">
            <div class="diary__content--emptystate-img__wrapper">
              <span class="diary__content--emptystate-txt">无数据</span>
              <img class="diary__content--emptystate-img" src="@/assets/img/isolate-island.png">
            </div>
          </div>
        </div>
        <div>
          <!-- 聊天窗口写日记区域，仅在writing视图时可见 -->
          <div v-if="currentView==='writing'">
            <loading-spinner height="60vh" v-if="loading.diaryChatMsg" />
            <card class="diary__current" rounded v-show="!loading.diaryChatMsg">
              <div class="diary__chatwindow">
                <diary-chat-window @start-loading="loading.diaryChatMsg=true" @diary-chat-msg-loaded="loading.diaryChatMsg=false" @today-completed="todayCompleted=true">
                </diary-chat-window>
              </div>
            </card>
          </div>
        </div>
      </div>
      <!-- 日历区域，仅在content视图可见 -->
      <div class="col-12 col-lg-4 col-xxl-1">
        <div class="calendar__wrapper" v-if="currentView ==='content'">
          <basic-calendar :key="calendarRerenderKey" ref="calendar" :defaultDate="currSelectedDate" legend legendTxt="表示该日有日记记录" legendColor="#6BC5A3" :logs="monthHistory" @month-change="handleCalendarChangeMonth($event)" onlySameAndBeforeSelectable @select-date="handleDateChange()"
            class="calendar"></basic-calendar>
        </div>
        <div class="diary-btncontainer d-flex flex-md-column justify-content-between mt-5  align-items-center">
          <button type="button" @click="changeToViewMode()" class="btn diary-btn  mb-md-5 shadowbtn"><span
                  class="iconfont d-none d-md-inline">&#xe734;</span> 查看日记</button>
          <button type="button" @click="handleClickSameDateLastYear()" class="btn mb-md-5  diary-btn shadowbtn "><span
                  class="iconfont d-none d-md-inline">&#xe73a;</span> 去年今日</button>
          <button type="button" @click="currentView='ques'" class="btn diary-btn shadowbtn "><span
                  class="iconfont d-none d-md-inline">&#xe826;</span> 编辑问题</button>
        </div>
      </div>
    </div>
    <!-- right -->
  </div>
</template>

<script>
  import {
    mapGetters
  } from 'vuex'
  import LoadingSpinner from '@/components/basicUI/LoadingSpinner'
  import DiaryService from '@/apis/DiaryService.js'
  import BasicCalendar from '@/components/BasicCalendar'
  import DiaryQuestionGrids from '@/components/DiaryQuestionGrids'
  import DiaryGrids from '@/components/DiaryGrids'
  import Card from '@/components/Card'
  import DiaryChatWindow from '@/components/DiaryChatWindow'
  export default {
    components: {
      BasicCalendar,
      Card,
      DiaryChatWindow,
      DiaryGrids,
      DiaryQuestionGrids,
      LoadingSpinner
    },
    data() {
      return {
        loading: {
          diaryChatMsg: true,
          diaryEntries: true
        },
        calendarRerenderKey: -999999, // 用以重新渲染
        isCurrDiaryEmpty: false,
        monthHistory: [], //  存放选中月份哪些天写过日
        calendarVisible: true,
        todayCompleted: false,
        currentView: 'writing', //ques, writing,content
        currInViewDiary: [],
        currSelectedDate: '',
        currCalenderMonth: '',
        currCalenderYear: ''
      }
    },
    computed: {
      ...mapGetters(['user', ])
    },
    watch: {
      $route() {
        this.readDateAndJournalFromUrl()
      },
    },
    mounted() {
      this.readDateAndJournalFromUrl()
    },
    methods: {
      /**
       * 有日记被删除后的处理
       * @param String} currSelectedDate  当前被选中的日期
       */
      handleDiaryDeleted(currSelectedDate) {
        this.getDiaryEntries(currSelectedDate, true)
      },
      /**
       * 从地址中的日期参数中读取
       */
      readDateAndJournalFromUrl() {
        const dateUrl = this.$route.params.date
        if (dateUrl && this.$timeTool.isYMDformat(dateUrl) && this.currSelectedDate !== dateUrl) {
          this.currentView = 'content'
          this.currSelectedDate = dateUrl
          this.getDiaryEntries(this.currSelectedDate)
        }
      },
      /**
       * 点击返回聊天窗口后的处理
       */
      handleReturnToChatClick() {
        this.changeToWrtingMode()
      },
      /**
       * 变成聊天窗口模式
       */
      changeToWrtingMode() {
        this.currentView = 'writing'
        this.$router.push('/griddiary')
      },
      /**
       * 重新渲染日历
       */
      rerenderCalendar() {
        // 避免当calendar已经被渲染后当前选中日期不因点击按钮而变化
        this.calendarRerenderKey += 1
      },
      /**
       * 点击去年今日后的处理
       */
      handleClickSameDateLastYear() {
        const date = this.$timeTool.getSameDayOfLastYear()
        this.currSelectedDate = date
        this.getDiaryEntries(date)
        if (this.currentView === 'content') {
          this.rerenderCalendar()
        } else {
          this.currentView = 'content'
        }
      },
      /**
       * 日期点击后的处理
       */
      handleDateChange() {
        this.$router.push({
          name: 'griddiary',
          // 给地址栏添加日期参数
          params: {
            date: this.$timeTool.formatInYMD(this.$refs.calendar.selected)
          }
        })
      },
      /**
       * 切换到看日记模式
       */
      changeToViewMode() {
        this.isCurrDiaryEmpty = false
        // 如果本身已经处于view模式，则重新渲染，以更新default date为当日
        this.rerenderCalendar()
        if (this.currentView === 'content') {
          this.rerenderCalendar()
        } else {
          this.currentView = 'content'
        }
        const now = this.$timeTool.getCurrUserTime()
        this.currSelectedDate = this.$timeTool.formatInYMD(now)
        this.getDiaryEntries(this.currSelectedDate)
      },
      /**
       * 月份改变后的处理
       * @param {*} yearAndMonth 
       */
      handleCalendarChangeMonth(yearAndMonth) {
        //读取本月的日记情况
        const year = yearAndMonth.y
        let month = yearAndMonth.m
        if (month < 10) {
          month = '0' + month
        }
        this.readMonthHistory(year, month)
      },
      /**
       * 发请求读取某年某月有记录的日期
       * @param {Number} y 
       * @param {Number} m 
       */
      async readMonthHistory(y, m) {
        this.currCalenderMonth = m
        this.currCalenderYear = y
        const res = await DiaryService.readMonthHistory(this.user.id, y, m)
        if (res.success) {
          this.monthHistory = res.content
        } else {
          this.$toast.error('读取日记记录失败')
        }
      },
      /**
       * 发请求读取某日日记内容
       * @param {String} date ymd日期
       * @param {Boolean} isAfterDeleted 是否刚刚发生了删除日记的操作
       */
      async getDiaryEntries(date, isAfterDeleted) {
        this.loading.diaryEntries = true
        const res = await DiaryService.list(this.user.id, date)
        if (res.success) {
          this.currInViewDiary = res.content
          this.isCurrDiaryEmpty = res.content.length === 0 ? true : false
          // 今日日记完全被删除时，todayCompleted状态恢复为false
          if (this.isCurrDiaryEmpty && date === this.$timeTool.formatInYMD(this.$timeTool.getCurrUserTime())) {
            this.todayCompleted = false
          }
          this.loading.diaryEntries = false
          // 如果发生删除操作，并且日记已被全被删除，那么重新读取本月有日记的记录
          if (isAfterDeleted) {
            if (this.isCurrDiaryEmpty) {
              console.log(date)
              console.log(this.monthHistory)
              this.handleCalendarChangeMonth(this.$refs['calendar'].getContext())
            }
          }
        } else {
          this.$toast.error('读取日记失败')
        }
      },
    }
  }
</script>

<style lang="sass" scoped>
.diary__current
  background: rgba(255, 255, 255, .9) !important
  padding: 0
  &-hint
    margin-left: 2rem
    display: block
    color: #8F9EB0
    margin-bottom: 2rem
.diary
  &__returntotoday
    margin-bottom: 2rem
    .btn
      padding: 1rem
      .icon-return
        margin-right: 2rem

  &__content
    padding: 2rem
    &-header
      color: #8F9EB0
      display: inline-block
      margin-bottom: 2rem
      display: flex
      justify-content: space-between
      &__controller
        padding: 0
        border-bottom: 1px solid #8F9EB0
    &--emptystate
      display: flex
      justify-content: center
      align-items: center
      &-txt
        position: absolute
        width: min-content
        display: block
        color: #fff
        background: #BCC6D0
        padding: 1rem
        border-radius: 1rem
      &-img
        width: 100%
        height: auto
        &__wrapper
          width: 60vw
          max-width: 40rem
  &-btncontainer
    background: transparent
.calendar
  &__wrapper
    background: #fff
.shadowbtn
  color: #8F9DB0
  font-size: 1.4rem
  border-radius: 1rem
  background: #F4F9FA
  box-shadow:  -6px -6px 10px rgba(255, 255, 255, 0.8),6px 6px 10px rgba(153, 195, 195, .2)
  display: block
  width: min-content
  padding: 10px
  @media(min-width: $bp-md)
    width: 12rem
    padding: 1rem 0
  &:hover
    box-shadow: -6px -6px 10px rgba(255, 255, 255, 0.8),  6px 6px 10px rgba(153, 195, 195, .3)
  &:active
    box-shadow: inset 5px 5px 5px rgba(153, 195, 195, .3), inset -5px -5px 5px #ffffff
.diary-btncontainer
  @media( min-height: 820px ) and (min-width: 1500px )
    margin-top: 10rem!important
  @media( min-height: 820px ) and (min-width: 1500px )
    margin-top: 20rem!important
.diary-btn
  @media( min-height: 820px ) and (min-width: 1500px )
    margin-top: 1rem!important
  @media( min-height: 820px ) and (min-width: 1500px )
    margin-top: 3rem!important
.diary__content-header
  @media( min-height: 820px ) and (min-width: 1500px )
    margin-bottom: 6rem!important
  @media( min-height: 820px ) and (min-width: 1500px )
    margin-bottom: 8rem!important
.diary__returntotoday
  @media( min-height: 820px ) and (min-width: 1500px )
    margin-bottom: 3rem!important
  @media( min-height: 820px ) and (min-width: 1500px )
    margin-bottom: 6rem!important
</style>
