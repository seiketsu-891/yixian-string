<template lang="html">
  <!-- 时间报告页面 -->
  <div class="container-fluid main">
    <!-- 头部（日期范围） -->
    <div class="timesheet-header">
      <div class="timesheet-header-weekcontroller timesheet-header-controller">
        <!-- 星期-1按钮 -->
        <button class="btn iconfont" @click.prevent="changeWeek('sub')">&#xe690;</button>
        <div class="timesheet-header-weekcontroller__txt">
          <span v-if="contextWeekRange[0] === currWeekRange[0]">本周</span>
          <span v-else-if="contextWeekRange[0] ===nextWeekRange[0]">下周</span>
          <span v-else-if="contextWeekRange[0] === prevWeekRange[0]">上周</span>
          <span v-else><span class="timesheet-header-weekcontroller__date">{{contextWeekRange[0]}}</span>-<span
              class="timesheet-header-weekcontroller__date"> {{contextWeekRange[6]}}</span></span>
        </div>
        <!-- 星期+1按钮 -->
        <button :disabled="contextWeekRange[0] === currWeekRange[0]" class="btn iconfont"
          @click.prevent="changeWeek('add')">&#xe606;</button>
      </div>
    </div>
    <!-- header end  -->
    <div class="row row-eq-height">
      <!-- <img class="statistics-illustration col-6" src="@/assets/img/woman-works-with-computer.png"> -->
      <div class="statistics-hours statistics-block col-12 col-md-6  col-xl-4 ">
        <card rounded>
          <span class="statistics-hours__title chart__title">连续7天小时数</span>
          <div class="btn statistics-hours__filterbtn">
            <button class="btn statistics-hours__filterbtn--cat" @click="$refs.DdCatOptions.open()"><span
                v-if="!filterCat">按分类查看</span><span v-else>{{filterCat.name}}</span>&nbsp;<span
                class="iconfont icon-down statistics-hours__filterbtn--icon">&#xe791;</span></button>
            <button class="btn" @click="clearFilter">查看全部</button>
          </div>
          <dropdown @selected="handleCatDropDownSelected" searchPlaceholder="搜索分类"
            style="position:absolute;max-width: 80vw;" ref="DdCatOptions" :options="timeEntryCats" type="advanced"
            withColor> </dropdown>
          <div class="statistics-hours__btns">
          </div>
          <loading-spinner v-if="loading.chartData" height="300px" />
          <tracked-hours-weekly v-else ref="chartTrackedHourWeekly" :data="trackedHoursChartData">
          </tracked-hours-weekly>
        </card>
      </div>
      <!-- 2.本周分类分布 -->
      <div class="statistics-catpercentage col-12 col-md-6  col-xl-5 mt-5 mt-md-0">
        <card rounded class="d-flex flex-column justify-content-between">
          <span class="statistics-hours__title chart__title">本周分类分布</span>
          <loading-spinner v-if="loading.chartData" height="200px" />
          <category-pie v-else class="mb-4" style="" :data="weeklyCatHours"></category-pie>
          <div class="otherstatistics-board" style="">
            <div class="otherstatistics-board__header">
              <svg style="display:block" class=" svgicon ">
                <use :xlink:href="require('@/assets/icon/sprite.svg') + '#svgicon-tag'"></use>
              </svg>
              <span>管理分类</span>
            </div>
            <button class="btn btn--go iconfont" @click="handleGoToManageCat"> &#xe78a;</button>
          </div>
        </card>
      </div>
      <!-- 3. 统计信息 -->
      <div class="col-12 col-xl-3 mt-5 mt-xl-0">
        <card rounded class="d-flex flex-column justify-content-between">
          <!-- 一周追踪小时数 -->
          <div class="otherstatistics-board otherstatistics-board--oneofthree">
            <div class="otherstatistics-board__header">
              <svg style="display:block" class="svgicon ">
                <use :xlink:href="require('@/assets/icon/sprite.svg') + '#svgicon-airplane'"></use>
              </svg>
              <span> 该周追踪</span>
            </div>
            <loading-spinner v-if="loading.chartData || sumHourWeek==='' " />
            <span v-else class="otherstatistics-board__digit">{{sumHourWeek}}h</span>
          </div>
          <!-- 平均每天小时数 -->
          <div class="otherstatistics-board otherstatistics-board--oneofthree">
            <div class="otherstatistics-board__header">
              <svg style="display:block" class=" svgicon ">
                <use :xlink:href="require('@/assets/icon/sprite.svg') + '#svgicon-experiment'"></use>
              </svg>
              <span> 平均每天</span>
            </div>
            <loading-spinner v-if="loading.chartData || sumHourWeek === ''" />
            <span v-else class="otherstatistics-board__digit">{{(sumHourWeek/7).toFixed(2)}}h</span>
          </div>
          <!-- 一周投入时间最多的分类 -->
          <div class="otherstatistics-board otherstatistics-board--oneofthree otherstatistics-board--dark">
            <div class="otherstatistics-board__header">
              <svg style="display:block" class=" svgicon ">
                <use :xlink:href="require('@/assets/icon/sprite.svg') + '#svgicon-champion'"></use>
              </svg>
              <span>该周最多投入在</span>
            </div>
            <loading-spinner v-if="loading.chartData || longestCat ===''" />
            <span v-else class="otherstatistics-board__digit--small otherstatistics-board__digit">{{longestCat}}</span>
          </div>
        </card>
      </div>
    </div>
    <!-- 时间条目展示区 -->
    <div class="row mt-5">
      <div class="col-12">
        <card rounded class="card-extensive">
          <span class="timesheet-header-notice" v-show="!user.showShortEntry">已隐藏时常不足1分钟的条目</span>
          <div class="timesheet-header-viewcontroller timesheet-header-controller">
            <button @click="timeEntryListView = !timeEntryListView" class="btn btn--default" style="margin-left: 0; ">
              切换为<span v-if="timeEntryListView">时间表视图</span>
              <span v-else>列表视图</span>
            </button>
          </div>
          <loading-spinner v-if="loading.timeEntriesWeeklyOutput" height="20rem" />
          <empty-state items="timeEntries" span="week" v-else-if="entryWeeklyEmpty" icon></empty-state>
          <div v-else-if="timeEntryListView " class="timesheet-list">
            <div v-for="(dailyEntries,index) in timeEntriesWeeklyOutput" :key="index">
              <div v-if="dailyEntries.timeEntries.length > 0">
                <!-- 日期以及该日总追踪小时数 start-->
                <div class="timesheet-list__info">
                  <span class="timesheet-list__datetxt">{{dailyEntries.date}}&nbsp;{{dailyEntries.weekday}}</span>
                  <span class="timesheet-list__sumhours">{{(dailyEntries.sum/1000/60/60).toFixed(2)}}小时</span>
                </div>
                <!-- 日期以及该日总追踪小时数 end-->
                <!-- 时间条目表格 -->
                <responsive-table @del="openDelEntry($event)" @edit="showEditEntry($event)"
                  :data="dailyEntries.timeEntries" :columns="timeTableColumns"> </responsive-table>
              </div>
            </div>
          </div>
          <!-- 时间表区域 -->
          <time-table :timeFormat="user.timeFormat" v-else :data="timeEntriesWeeklyOutput" @edit="showEditEntry($event)"
            @del="openDelEntry($event)"></time-table>
        </card>
      </div>
    </div>
  </div>
  <!-- 删除时间条目modal -->
  <teleport to="#app">
    <modal ref="MDelEntry" max-width="35rem" :submitBtnDisabled="submitDelEntryBtnDisabled" confirmbox
      @confirm="delEntry"></modal>
  </teleport>
  <!-- 编辑时间条目modal -->
  <teleport to="#app">
    <modal ref="MEditEntry" max-width="35rem" form title="编辑条目" :submitBtnDisabled="submitEditEntryBtnDisabled"
      @submit="submitEditEntry">
      <template v-slot:body>
        <el-form ref="editEntryForm">
          <el-form-item label="描述">
            <el-input spellcheck="false" ref="editEntryFormFirstFocus" v-model="entryForm.description"
              maxlength="100"></el-input>
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="entryForm.categoryId" filterable placeholder="请选择分类" :popper-append-to-body="false"
              @visible-change="getTimeEntryCatList" no-data-text="暂无数据" no-match-text="无匹配数据">
              <el-option v-for="cat in timeEntryCats" :key="cat.id" :label="cat.name" :value="cat.id"> <span
                  :style="{background: cat.color} "
                  style="width: 1rem; height: 1rem; display: inline-block; margin-right: 1rem"> </span>{{cat.name}}
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </template>
    </modal>
  </teleport>
</template>

<script>
  // api
  import TimeEntryService from '@/apis/TimeEntryService'
  import TimeEntryCategoryService from '@/apis/TimeEntryCategoryService'
  //mixin
  import {
    TimeEntryMixin
  } from '@/assets/mixins/Mixins'
  // component
  import EmptyState from '@/components/basicUI/EmptyState'
  import Dropdown from '@/components/Dropdown'
  import CategoryPie from '@/components/graph/CategoryPie'
  import TrackedHoursWeekly from '@/components/graph/TrackedHoursWeekly'
  import ResponsiveTable from '@/components/ResponsiveTable'
  import Card from '@/components/Card'
  import TimeTable from '@/components/time/TimeTable'
  import LoadingSpinner from '@/components/basicUI/LoadingSpinner'
  export default {
    mixins: [TimeEntryMixin],
    components: {
      EmptyState,
      Dropdown,
      TimeTable,
      Card,
      ResponsiveTable,
      TrackedHoursWeekly,
      CategoryPie,
      LoadingSpinner
    },
    emits: ['edit', 'del'],
    data() {
      return {
        loading: {
          chartData: true,
          timeEntriesWeeklyOutput: true
        },
        submitEditEntryBtnDisabled: false,
        submitDelEntryBtnDisabled: false,
        filterCat: '', // 当前筛选的时间条目分类
        weeklyCatHours: [],
        entryForm: { // 时间条目表单信息
          id: '',
          description: '',
          categoryId: '',
        },
        timeEntryListView: true, // 是否时间条目是列表形式
        nextWeekRange: [], // 下周日期范围
        prevWeekRange: [], // 上周日期范围
        catModalTitle: '',
        timeEntryCats: [],
        timeTableColumns: [{
            label: '内容',
            fieldName: 'desc'
          },
          {
            label: '分类',
            fieldName: 'cat',
            withColor: true
          },
          {
            label: '起始',
            fieldName: 'time',
            minW: true
          },
          {
            label: '时长',
            fieldName: 'duration',
            minW: true
          }
        ],
      }
    },
    methods: {
      /**
       * 从地址中读取时间范围参数并获取期所在的星期日期范围
       */
      readDateRangeFromUrl() {
        console.log(this.$route.params)
        if (!this.$route.params || !this.$route.params.from) {
          return
        }
        if (this.$timeTool.isYMDformat(this.$route.params.from)) {
          this.contextWeekRange = this.$timeTool.getWeekRange(this.$route.params.from, null)
        }
      },
      /**
       * 清除分类筛选
       */
      clearFilter() {
        if (this.filterCat) {
          this.filterCat = ''
        }
      },
      /**
       * 分类筛选下拉框选择一项后的处理
       */
      handleCatDropDownSelected(item) {
        if (item.id !== this.filterCat.id) {
          this.filterCat = item
        }
      },
      /**
       * 跳转到管理分类页面
       */
      handleGoToManageCat() {
        this.$router.push('/entrycategory')
      },
      /**
       * 改变星期
       * @param {*} oper 如何改变 ['sub' 'add']
       */
      changeWeek(oper) {
        this.contextWeekRange = this.$timeTool.getWeekRange(this.contextWeekRange[0], oper)
        this.$router.push({
          name: 'timereport',
          params: {
            from: this.contextWeekRange[0],
            to: this.contextWeekRange[6]
          }
        })
        this.getTimeEntryCatList(true)
      },
      /**
       * 打开删除时间条目modal
       */
      openDelEntry(id) {
        this.$refs['MDelEntry'].open({
          id
        })
      },
      /**
       * 打开编辑时间条目modal
       * @param {*} item 
       */
      showEditEntry(item) {
        this.$refs['MEditEntry'].open({
          item
        })
        this.$nextTick(() => {
          this.entryForm.description = item.desc === '暂无描述' ? '' : item.desc
          // 如果分类已被删除，则将它设置为默认id
          console.log(item)
          console.log(this.timeEntryCats)
          this.entryForm.categoryId = this.timeEntryCats.find(cat => cat.id === item.categoryId) ? item.categoryId : this.timeEntryCats[0].id
          this.entryForm.id = item.id
          this.$refs['editEntryFormFirstFocus'].focus()
        })
      },
      /**
       * 提交编辑时间条目
       * @param {*} args 
       */
      submitEditEntry(args) {
        this.submitEditEntryBtnDisabled = true
        const original = args.item
        if (original.desc === "暂无描述" && this.entryForm.description === '' || original.desc === this.entryForm.description) {
          if (original.categoryId === this.entryForm.categoryId) {
            this.$toast.show('数据无变化')
            this.submitEditEntryBtnDisabled = false
            return
          }
        }
        const entry = {
          id: this.entryForm.id,
          description: this.entryForm.description.trim(),
          categoryId: this.entryForm.categoryId
        }
        this.editEntry(entry)
      },
      /**
       * 发送请求编辑时间条目
       * @param {*} entry 
       */
      async editEntry(entry) {
        const res = await TimeEntryService.update(this.user.id, entry)
        if (res.success) {
          this.$toast.show('更新成功')
          this.$refs['MEditEntry'].close()
          this.getTimeEntryCatList(true)
        } else {
          this.$toast.error('更新失败')
        }
        this.submitEditEntryBtnDisabled = false
      },
      /**
       * 发送请求获取所有分类
       * @param {*} updateEntries 
       */
      async getTimeEntryCatList(updateEntries) {
        const res = await TimeEntryCategoryService.list(this.user.id);
        if (res.success) {
          this.timeEntryCats = res.content
          if (updateEntries) { // 获取cat之后再获取条目
            this.contextWeekRange = this.$timeTool.getWeekRange(this.$route.params.from, null)
            this.getAllTimeEntriesOfWeek(this.contextWeekRange[0], this.contextWeekRange[6])
          }
          this.loading.chartData = false
        } else {
          this.$toast.error("获取时间条目分类失败")
        }
      },
      /**
       * 发送请求删除时间条目
       * @param {*} args 
       */
      async delEntry(args) {
        this.submitDelEntryBtnDisabled = true
        const res = await TimeEntryService.del(this.user.id, args.id)
        if (res.success) {
          this.$refs['MDelEntry'].close()
          this.$toast.show('删除成功')
          this.getTimeEntryCatList(true)
        } else {
          this.$toast.error('删除失败')
        }
        this.submitDelEntryBtnDisabled = false
      },
    },
    computed: {
      // 一周时间条目是否数据为空
      entryWeeklyEmpty() {
        if (this.timeEntriesWeeklyOutput.length === 0) {
          return true
        }
        let isEmpty = true
        this.timeEntriesWeeklyOutput.forEach((item) => {
          if (item.timeEntries.length > 0) {
            isEmpty = false
          }
        })
        return isEmpty
      },
      // 一周花费时间最多的分类名
      longestCat() {
        if (this.loading.chartData) {
          return ''
        }
        let max = 0
        let cat = ''
        this.weeklyCatHours.forEach((item) => {
          if (max < item.hours) {
            max = item.hours
            cat = item.catDesc
          }
        })
        return cat ? cat : '暂无分类'
      },
      // 一周追踪总时间
      sumHourWeek() {
        if (this.loading.chartData) {
          return ''
        }
        if (this.timeEntriesWeeklyOutput.length > 0) {
          const sum = this.timeEntriesWeeklyOutput.reduce((acc, item) => acc + item.sum, 0)
          return (sum / 1000 / 60 / 60).toFixed(2)
        } else {
          return ''
        }
      },
      user() {
        return this.$store.getters.user
      },
    },
    mounted() {
      this.currWeekRange = this.$timeTool.getWeekRange()
      this.readDateRangeFromUrl()
      //如果url参数未提供合法日期，则显示该周数据
      if (!this.contextWeekRange || this.contextWeekRange.length === 0) {
        this.contextWeekRange = this.currWeekRange
      }
      this.nextWeekRange = this.$timeTool.getWeekRange(this.currWeekRange[0], 'add', true)
      this.prevWeekRange = this.$timeTool.getWeekRange(this.currWeekRange[0], 'sub', true)
      this.getTimeEntryCatList(true) // 获取分类和条目
      // 当header组建中的模态框添加时间条目后，刷新时间条目顺序
      this.$emitter.on('timeEntryUpdate', () => this.getTimeEntryCatList(true))
    },
    unmounted() {
      this.$emitter.off('timeEntryUpdate', () => this.getTimeEntryCatList(true))
    },
    watch: {
      // 筛选分类发生变化后更新数据
      filterCat() {
        this.getTimeEntryCatList(true)
      }
    }
  }
</script>

<style lang="sass" scoped>
.timesheet
  &-header
    display: flex
    justify-content: space-between
    margin: 2rem
    &-notice
      display: block
      margin-bottom: 1rem
      color: #8F9EB0
      font-size: 1.4rem
      margin: 0 0 2rem 1rem
    &-controller
      width: max-content
      color: #5D5D5D
      font-size: 1.4rem !important
      border-radius: 1rem
    &-viewcontroller
      width: 100%
      display: flex
      justify-content: space-between
    &-weekcontroller
      background: transparent
      display: flex
      padding: 0 1rem
      align-items: center
      &__txt
        line-height: 1.4rem
        height: min-content
        display: inline-block
        min-width: 5rem
        text-align: center
      .btn
        line-height: .8rem
      .iconfont
        font-size: .8rem
      &__date
        font-size: 1.3rem
        margin: 0 1rem

.timesheet-list
  width: 100%
  margin-bottom: 3rem
  &__info
    display: flex
    justify-content: space-between
  &__datetxt, &__sumhours
    color: #8F9DB0
    display: block
    margin: 2rem
.statistics
 &-hours
   &__title
     // todo padding?
     font-weight: bold
     display: block
     margin-bottom: 2rem
   &__btns
     display: flex
     font-size: 1.3rem
     justify-content: space-between
   &__filterbtn
     width: 100%
     max-width: 20rem
     display: flex
     justify-content: space-between
     > *
       padding: 1rem 2rem
       background: #F5F6FB
       border-radius: 1rem
     &--icon
       font-size: 1.7rem
     &--cat
       margin-right: 2rem
       > span
         width: min-content
         max-width: 10rem
         text-align: left
         display: inline-block
         text-overflow: ellipsis
         white-space: nowrap
         display: inline-block
         word-break: break-all
         overflow: hidden
         vertical-align: middle
.chart__title
  font-size: 1.5rem
  margin-left: 1rem
  color: #393E45
.otherstatistics
  &-board
    color: #8F9EB0
    display: flex
    padding: 1rem
    padding-top: 1.5rem
    justify-content: center
    flex-direction: column
    border: 1px solid #E1E0E1
    border-radius: 6px
    &--oneofthree
      height: 30%
    .btn--go
      margin-right: 0
      margin-left: auto
      display: block
      margin-top: 2rem
      background: #F5F6FB
      width: 3rem
      height: 3rem
      border-radius: 50%
      &:hover
        color: #fff
        background: #FFBF46
        transition: all .3s
    &__digit
      margin-top: 2rem
      display: block
      font-size: 2.3rem
      color: #5D5D5D
      &--small
        font-size: 1.4rem
        text-overflow: ellipsis
        white-space: nowrap
        overflow: hidden
    &--dark
      background-color: #FFEFD6
      border: 0
    &__header
      display: flex
      justify-content: space-between
      align-items: center
.svgicon
  width: 3rem
  height: 3rem
.card
  height: 100%
.btn[disabled]
  color: #8F9EB0
  cursor: default
.card-extensive
  min-height: 30rem
</style>
