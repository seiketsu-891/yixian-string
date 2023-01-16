<template lang="html">
  <!-- 用户登录后的首页 -->
  <div class="container-fluid home">
    <div class="row ">
      <!-- left column -->
      <div class="col-12 col-xl-4  home__left">
        <div class="row">
          <div class="col-md-6  col-xl-12">
            <!-- banner区域 -->
            <div class="home__banner col-12">
              <!-- 插图 -->
              <div class="home__banner__left">
                <img class="home__banner-img" src="@/assets/img/manwithcomputer.png" alt="拿着电脑使用易弦的男性插图">
              </div>
              <!-- 时刻 -->
              <div class="home__banner__right">
                <span class="home__banner-time">{{ currentTime }}</span>
                <!-- 个人目标 -->
                <div class="home__banner-goal">
                  <p class="home__banner-goal__content"><span class="home__banner-goal__txt">{{ goals }}</span><span class="home__banner-goal__editbtn iconfont" @click="showGoalModal">编辑</span></p>
                </div>
              </div>
            </div>
            <!-- 时间银行区域 -->
            <card class="d-none d-md-block d-xl-none mt-5" rounded style="padding-bottom: 2.2rem; padding-top: 2.2rem">
              <span class="iconfont chart__icon">&#xe709;</span>
              <div class="chart__title">时间银行</div>
              <time-bank layout="vertical" :year-percentage="80" :month-percentage="20"></time-bank>
            </card>
          </div>
          <!-- simple statistics -->
          <div class=" col-12 col-md-6  col-xl-12 mt-5 mt-md-0 mt-xl-5">
            <card rounded class="home__simpletatistics d-flex d-sm-flex flex-column flex-sm-row flex-md-column">
              <div class="lastSevendayData">
                <span class="iconfont chart__icon">&#xe7ba;</span>
                <h3 class="chart__title">本周追踪小时数</h3>
                <loading-spinner v-if="hasLoadingData" height="300px" />
                <tracked-hours-weekly v-else keep-alive style="margin-left: -1rem; margin-right:-1.2rem; margin-top: -1.5rem;" class="mb-xl-3 mt-xl-1" :data="trackedHoursChartData"></tracked-hours-weekly>
              </div>
              <!-- 小屏幕的时间银行区域 -->
              <div class="timebank d-md-none d-xl-block d-xl-mt-5">
                <span class="iconfont chart__icon">&#xe709;</span>
                <span class="chart__title">时间银行</span>
                <time-bank></time-bank>
              </div>
            </card>
          </div>
        </div>
      </div>
      <!-- middle column -->
      <div class=" home__middle col-12 col-xl-4 mt-5 mt-xl-0">
        <card rounded class="home__timeentries">
          <span class="title-icon icon-log iconfont">&#xe7c8;</span>
          <h3 class="title">今日时间清单</h3>
          <loading-spinner v-if="hasLoadingData" height="10rem" />
          <empty-state items="timeEntries" v-if="!hasLoadingData && timeEntriesOfToday.length === 0" icon></empty-state>
          <ul class="home__timeentries-list card__main">
            <li v-for="item in timeEntriesOfToday" :key="item.id" class="home__timeentries-listitem listitem">
              <simple-time-entry :description="item.desc" :color="item.catColor" :startTime="item.start" :endTime="item.end"> </simple-time-entry>
            </li>
          </ul>
        </card>
      </div>
      <!-- right column -->
      <div class="home__right col-12 col-xl-4 mt-5 mt-xl-0">
        <card rounded class="home__todos">
          <span class="title-icon icon-todo iconfont">&#xe699;</span>
          <h3 class="title">今日待办</h3>
          <div class="card__main">
            <loading-spinner v-if="hasLoadingData" height="10rem" />
            <empty-state v-else-if=" todosOfToday.length === 0" items="todos"> </empty-state>
            <empty-state v-else-if="taskAllComplished" items="todos" all-done :data="todosOfToday"> </empty-state>
            <div v-else>
              <todo-collection hideWhenNoData priority="11" :date="dateToday" show-as-list :data="todosOfToday" class="mb-4" @update="getTodosOfToday"></todo-collection>
              <todo-collection hideWhenNoData priority="10" :date="dateToday" show-as-list :data="todosOfToday" class="mb-4" @update="getTodosOfToday"></todo-collection>
              <todo-collection hideWhenNoData priority="01" :date="dateToday" show-as-list :data="todosOfToday" class="mb-4" @update="getTodosOfToday"></todo-collection>
              <todo-collection hideWhenNoData priority="00" :date="dateToday" show-as-list :data="todosOfToday" class="mb-5" @update="getTodosOfToday"></todo-collection>
            </div>
          </div>
        </card>
      </div>
    </div>
  </div>
  <!-- 修改个人目标的modal -->
  <teleport to="#app">
    <modal ref="editGoalModal" class="home__goalmodal" width="80%" title="修改首页展示句" max-width="60rem">
      <template v-slot:body>
                            <p class="home__goalmodal-msg">提示:最多可输入30个字符。</p>
                            <custom-input  rows = "4" maxlength="30" v-model="goalInput"  name="goal" ref="goalInput" class="home__goalmodal-textarea" @keydown="blockCarriageReturn"></custom-input>
</template>

<template v-slot:footer>
  <div class="">
    <button priority="button" class="home__goalmodal-resetbtn  btn--reset btn" @click="resetGoalModal">复原</button>
    <button priority="button" class="btn--confirm btn" :disabled="goalSubmitBtnDisabled" @click="updateGoals(goalInput)">{{goalSubmitBtnTxt}}</button>
  </div>
</template>

</modal>
</teleport>

</template>

<script>
  import {
    mapGetters
  } from 'vuex'
  // mixin
  import {
    TimeEntryMixin
  } from '@/assets/mixins/Mixins'
  // service
  import TimeEntryService from '@/apis/TimeEntryService'
  import TimeEntryCategoryService from '@/apis/TimeEntryCategoryService'
  import TodoService from '@/apis/TodoService.js'
  import AccountService from '@/apis/AccountService.js'
  // component
  import LoadingSpinner from '@/components/basicUI/LoadingSpinner'
  import EmptyState from '@/components/basicUI/EmptyState'
  import Card from '@/components/Card'
  import Modal from '@/components/Modal'
  import CustomInput from '@/components/CustomInput'
  import TimeBank from '@/components/graph/TimeBank'
  import SimpleTimeEntry from '@/components/listitem/SimpleTimeEntry'
  import TodoCollection from '@/components/TodoCollection'
  import TrackedHoursWeekly from '@/components/graph/TrackedHoursWeekly'
  export default {
    mixins: [TimeEntryMixin],
    components: {
      TrackedHoursWeekly,
      EmptyState,
      Card,
      Modal,
      CustomInput,
      TimeBank,
      SimpleTimeEntry,
      TodoCollection,
      LoadingSpinner
    },
    watch: {
      goalSubmitBtnDisabled(disabled) {
        if (disabled) {
          this.goalSubmitBtnTxt = '处理中'
        } else {
          this.goalSubmitBtnTxt = '提交'
        }
      }
    },
    computed: {
      // 首页是否有数据正在加载中
      hasLoadingData() {
        for (let key in this.isLoading) {
          if (this.isLoading[key]) {
            return true
          }
        }
        return false
      },
      // 今天日期的ymd格式
      dateToday() {
        const t = this.$timeTool.getCurrUserTime()
        return this.$timeTool.parseYMD(t)
      },
      // 今日待办是否都完成
      taskAllComplished() {
        return this.todosOfToday.length === this.todosOfToday.filter(i => i.done).length
      },
      // 显示在首页上的目标
      goals() {
        const userGoals = this.user.goals
        return userGoals ? userGoals : '暂未设定目标。点击编辑设定目标或者写上喜欢的句子'
      },
      ...mapGetters([
        'user',
      ]),
    },
    data() {
      return {
        goalSubmitBtnTxt: '提交',
        isLoading: {
          todo: true,
          timeEntry: true,
          timeEntryCat: true
        },
        goalSubmitBtnDisabled: false,
        goalInput: '', // 用户输入的goal
        filterCat: null, // 时间条目没有分类限制，表示显示全部
        onlyTimeEntryDurAndDate: true, // timeentry改装数据时候需要的判定
        timeEntryCats: [], // 时间条目分类
        timeEntriesTmp: [], //
        timeEntriesOfToday: [], // 当日时间条目
        todosOfToday: [], // 当日待办
        currentTime: '', // 存储当前时间，显示在首页上。根据用户的设置显示为24小时或12小时制
        clock: 0 // 用来存储时间更新setInterval函数
      }
    },
    unmounted() {
      // 结束首页的计时
      if (this.clock) {
        clearInterval(this.clock)
      }
      this.$emitter.off('timeEntryUpdate', () => this.getTimeEntryCatList())
    },
    mounted() {
      this.currWeekRange = this.$timeTool.getWeekRange()
      this.contextWeekRange = this.currWeekRange
      // this.updateCurrentTime()
      this.startClock()
      this.getTimeEntryCatList()
      this.getTodosOfToday()
      // 监听header传来的timeEntryUpdate事件，如果在header的计时表单中添加了新的时间条目，则主页显示的时间条目需要重新获取
      this.$emitter.on('timeEntryUpdate', () => {
        this.getTimeEntryCatList()
      })
    },
    methods: {
      /**
       * 设定每秒更新时间
       */
      startClock() {
        this.currentTime = this.$timeTool.formatInClockTime(this.user.timeFormat)
        this.clock = setInterval(this.updateCurrentTime, 1000)
      },
      /**
       * 更新钟表时间
       */
      updateCurrentTime() {
        this.currentTime = this.$timeTool.formatInClockTime(this.user.timeFormat)
      },
      /**
       * 显示编辑goal的modal
       */
      showGoalModal() {
        this.setCurrUserGoal()
        this.$refs.editGoalModal.open()
        this.focusGoalInput()
      },
      /**
       *  将输入框中的文字设定为用户当前goal
       */
      setCurrUserGoal() {
        // 防止出现null情况造成textarea无法使用
        this.goalInput = this.user.goals ? this.user.goals : ''
      },
      /**
       * focus到输入框
       */
      focusGoalInput() {
        this.$nextTick(() => {
          const $textArea = this.$refs.goalInput.$refs.textarea
          $textArea.focus()
          const len = $textArea.value.length
          setTimeout(() => {
            // 修正移动设备focus在开头的情况
            $textArea.selectionStart = $textArea.selectionEnd = len
          }, 10)
        })
      },
      /**
       * 将内容还原为之前的内容
       */
      resetGoalModal() {
        this.setCurrUserGoal()
        this.focusGoalInput()
      },
      /**
       * 修改目标时禁用回车
       */
      blockCarriageReturn(event) {
        const key = event.which || event.keyCode || 0
        if (key === 13) {
          event.preventDefault()
        }
      },
      /**
       * 发送更新个人目标请求
       */
      async updateGoals(newGoals) {
        newGoals = newGoals.trim()
        this.goalSubmitBtnDisabled = true
        if (newGoals === '') {
          this.$toast.show('内容不能为空')
          this.focusGoalInput()
          this.goalSubmitBtnDisabled = false
          return
        }
        if (newGoals === this.user.goals) {
          this.$toast.show('内容未发生变化')
          this.focusGoalInput()
          this.goalSubmitBtnDisabled = false
          return
        }
        const user = {
          goals: newGoals
        }
        const res = await AccountService.update(user, this.user.id)
        if (res.success) {
          this.$toast.show('更新成功')
          const updatedUser = res.content
          this.$store.dispatch('update', updatedUser)
          this.$refs.editGoalModal.close()
        } else {
          this.$toast.error('更新失败')
          this.focusGoalInput()
        }
        this.goalSubmitBtnDisabled = false
      },
      /**
       * 发送获取当日待办请求
       */
      async getTodosOfToday() {
        const date = this.$timeTool.formatInYMD(this.$timeTool.getCurrUserTime())
        const res = await TodoService.list(this.user.id, date)
        if (res.success) {
          this.todosOfToday = res.content
          this.isLoading.todo = false
        } else {
          this.$toast.error('获取待办失败')
        }
      },
      /**
       * 发送获取时间条目分类请求
       */
      async getTimeEntryCatList() {
        const res = await TimeEntryCategoryService.list(this.user.id);
        if (res.success) {
          this.timeEntryCats = res.content
          const today = this.$timeTool.getCurrUserTime()
          this.getTimeEntriesOfToday(this.$timeTool.formatInYMD(today))
          this.getAllTimeEntriesOfWeek(this.contextWeekRange[0], this.contextWeekRange[6])
          this.isLoading.timeEntryCat = false
        } else {
          this.$toast.error("获取数据失败")
        }
      },
      /**
       * 发送获取时间条目请求
       */
      async getTimeEntriesOfToday(date) {
        const res = await TimeEntryService.list(this.user.id, date, date, this.user.timezone, true)
        if (res.success) {
          this.timeEntriesTmp = res.content
          this.getParsedTimeEntriesOfToday()
          this.isLoading.timeEntry = false
        } else {
          this.$toast.error('获取时间条目失败')
        }
      },
      /**
       * 将获取的后台时间条目改装成需要展示的格式
       */
      getParsedTimeEntriesOfToday() {
        const res = []
        this.timeEntriesTmp.forEach(entry => {
          const e = {}
          e.id = entry.id,
            // 描述为空时显示暂无描述
            e.desc = entry.description || '暂无描述'
          // 时间由毫秒转化为需要的时间格式
          const startTime = this.$timeTool.convertAndFormatDateAndTime(entry.start, this.user.timeFormat)
          const endTime = this.$timeTool.convertAndFormatDateAndTime(entry.end, this.user.timeFormat)
          e.start = startTime.substring(10).trim()
          e.end = endTime.substring(10).trim()
          // 如果时间条目的分类已经不存在（例如已被删除，把颜色设置为默认颜色）
          const cat = this.timeEntryCats.find(c => c.id === entry.categoryId)
          e.catColor = cat ? cat.color : this.$const.DEFAULT_CAT_COLOR
          res.push(e)
        })
        this.timeEntriesOfToday = res
      },
    }
  }
</script>

<style lang="sass" scoped>
@import '@/assets/sass/components/button.sass'
@import '@/assets/sass/components/card.sass'
.chart
  // background: blue
  &__title
    user-select: none
    margin-bottom: 2rem
    font-size: 1.7rem
    color: #393E45
    display: inline-block
  &__icon
    user-select: none
    margin-right: 10px
    font-size: 15px
    padding: 2px
    border-radius: 50%
    width: 30px
    height: 30px
    text-align: center
    line-height: 30px
    display: inline-block
    background: #FEF8EC
    color: #FAC872
.title
  user-select: none
  margin-bottom: 2rem
  display: inline-block
  font-size: 20px
  color: #393E45
.title-icon
  user-select: none
  margin-right: 10px
  font-size: 15px
  padding: 2px
  border-radius: 50%
  width: 30px
  height: 30px
  text-align: center
  line-height: 30px
  display: inline-block
  background: #FEF8EC
  color: #FAC872

.home
  &__banner
    display: flex
    height: 100%
    background: #FFDCAD
    position: relative
    border-radius: 2rem
    box-shadow: 0 1rem 3rem rgba(205,215,217,73%)
    @media(min-width: 320px)
      height: 17rem
    &-img
      max-width: 100%
      max-height: 14rem
    &__left
     // 始终让img超出container下面5%的距离
     width: 30%
     min-width: 11rem
     max-width: 16rem
     height: 105%
     display: none
     // 至少iphone5大小时开始显示图片
     @media(min-width: 320px)
       font-size: 3.2rem
       display: flex
       align-items: flex-end
    &__right
      padding-left: .8rem
      width: auto
      display: flex
      flex-direction: column
      align-items: flex-start
      justify-content: center
    &-time
      user-select: none
      font-size: 2.8rem
      @media(min-width: 358px)
        font-size: 3.2rem
    &-goal
      display: inline-block
      word-break: break-all
      margin-top: 0
      font-size: 1.2rem
      @media(min-width: 358px)
        font-size: 1.5rem
      &__txt
        display: inline-block
        min-height: 4rem
      &__content
        margin-left: .4rem
        color: #707070
        font-size: 14px
      &__editbtn
        align-self: center
        width: max-content
        cursor: pointer
        color: #C3947F
        display: block
        margin: .7rem 0
        border-bottom: 1px solid transparent
        &:hover
          border-bottom: 1px solid #C3947F
  &__simpletatistics
    height: 100%
    .lastSevendayData
      width: 100%
      height: 100%
    .timebank
      width: 100%

  &__timeentries
    .listitem
      &:not(:last-of-type)
        margin-bottom: var(--margin-listitem--normal)
  &__todos
    &-list
      padding: 1rem 0
      &:not(:last-of-type)
        border-bottom: 1px solid #EAEAEA
      .listitem
        &:not(:last-of-type)
         margin-bottom: var(--margin-listitem--normal)

    &-tag
      &:not(:first-of-type)
        margin-top: var(--margin-listitem--normal)
    //modal
.home
  &__goalmodal
    &-msg
      margin-bottom: 2rem
      padding-left: 1rem
    &-textarea
      // height: 8rem
    &-resetbtn
      margin-right: var(  --margin-row--bottom)
.title
  font-size: 1.7rem

// ========================= svgicon =====================================
.svgicon
  &-notimeentries
    display: block
    width: 8rem
    height: 9rem
</style>
