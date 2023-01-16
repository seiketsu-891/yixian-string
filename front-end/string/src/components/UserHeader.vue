<template lang="html">
  <!-- 登录后页面的头部 -->
  <div class="">
    <!-- ######### < small#############-->
    <div class="d-lg-none row no-gutters px-3 userheader userheader--smallscreen mb-5">
      <div class="userheader-togglemenu col-4">
        <div class="userheader-togglemenu__btndecoration" @click.capture="$refs.toggleMenuBtn.toggleMenu()">
        </div>
        <nav-toggle-btn class="userheader-togglemenu__btn" ref="toggleMenuBtn">
        </nav-toggle-btn>
      </div>
      <!-- 小于iphone6时，调整header上的几个button大小和页面padding -->
      <div class="userheader-logo col-4">
        <router-link class="userheader-logo__link" to="/">
          <svg-logo width="6rem"> </svg-logo>
        </router-link>
      </div>
      <div class="userheader-user col-4 d-inline-flex flex-nowrap justify-content-end align-items-center">
        <button type="button" name="button" class="mr-4  btn--timersmallscreen  btn"
          @click.prevent="showTimerForm">计时</button>
        <badge class="userheader-user__badge mr-4" width="2.5rem" height="2.5rem">
        </badge>
        <avatar class="userheader-user__avatar" width="2.8rem"></avatar>
      </div>
    </div>
    <!-- sm屏幕 -->
    <!-- ######### largerscreen #############-->
    <div class="d-none d-lg-block userheader userheader--largerscreen mt-3 mb-5"
      style="display: none">
      <div class="userheader__wrapper d-flex justify-content-end align-items-center">
        <!-- user information -->
        <button type="button" name="button" class="btn--floating  btn btn--readyfortimer"
          @click.prevent="showTimerForm">前往计时</button>
        <badge class="userheader-badge" width="4.5rem" height="4.5rem"></badge>
        <avatar class="userheader-avatar" width="4rem"></avatar>
      </div>
      <!-- 时间追踪表单 -->
      <teleport to="body">
        <modal :key="modalTimeFormKey" class="modal-timerform" :submitBtnDisabled="MtimeTrackBtnDisabled"
          @submit="handleTimeTrackFormSubmit" ref="MtimeTrackForm" title="准备计时" max-width="40rem" form
          @reset="resetTimeTrackForm" @close="timeTrackFormVisble = false; resetTimeTrackForm(true);"
          @keyup.enter="formTimeTrackHandleKeyEnter" @keydown.enter="formTimeTrackHandleKeyEnter">
          <template v-slot:body>
            <div class="trackform-type">
              <el-radio-group v-model="isManualTrack">
                <el-radio-button :label="false">自动计时</el-radio-button>
                <el-radio-button :label="true"> 手动添加</el-radio-button>
              </el-radio-group>
            </div>
            <el-form ref="timeTrackerForm">
              <el-form-item label="内容描述（你要做什么？）">
                <el-input placeholder="可留空" ref="timeTrackFormFirstFocus" v-model="timeTrackForm.description"
                  maxlength="100"></el-input>
              </el-form-item>
              <el-form-item label="请选择类型">
                <el-select v-model="timeTrackForm.catId" filterable placeholder="请选择分类" :popper-append-to-body="false"
                  @visible-change="getTimeEntryCatList" no-data-text="暂无数据" no-match-text="无匹配数据">
                  <el-option v-for="cat in timeEntryCatList" :key="cat.id" :label="cat.name" :value="cat.id"> <span
                      :style="{ background: cat.color }"
                      style="width: 1rem; height: 1rem; display: inline-block; margin-right: 1rem"> </span>{{ cat.name }}
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="开始时间">
                <div v-show="isManualTrack">
                  <date-time-picker  hasSiblings noLaterTime noLaterDate ref="startTimePicker"> </date-time-picker>
                </div>
                <div v-show="!isManualTrack">
                  <date-time-picker noLaterTime noLaterDate :dateChangable="false" ref="startTimePickerAuto">
                  </date-time-picker>
                </div>
              </el-form-item>
              <div v-show="isManualTrack">
                <el-form-item label="结束时间">
                  <date-time-picker noLaterTime hasSiblings noLaterDate ref="endTimePicker"></date-time-picker>
                </el-form-item>
              </div>
            </el-form>
          </template>
        </modal>
      </teleport>
    </div>
    <!-- 正在计时的时间条目的modal -->
    <teleport to="body">
      <modal  ref="MtimerRunning" :topCloseBtn="false" title="计时中，请专注" width="38rem">
        <template v-slot:body>
          <div class="running-timer">
            <img class="running-timer-img" src="@/assets/img/meditation.png" />
            <div v-if="!runningEntryDuration" class="running-timer-duration--loading">
              数据同步中...
            </div>
            <div class="running-timer-duration" v-if="runningEntryDuration">
              <div class="running-timer-duration-block">
                <span class="running-timer-duration__desc">Hours</span>
                <span class="running-timer-duration__digit">{{ timerDurFormated.h }}</span>
              </div>
              <div class="running-timer-duration-block">
                <span class="running-timer-duration__desc">&nbsp;</span>
                <span class="running-timer-duration__divider running-timer-duration__divider--blink">:</span>
              </div>
              <div class="running-timer-duration-block">
                <span class="running-timer-duration__desc">Minutes</span>
                <span class="running-timer-duration__digit">{{ timerDurFormated.m }}</span>
              </div>
            </div>
            <div class="running-timer-btns" v-if="runningEntryDuration">
              <button class="btn btn-finish iconfont running-timer-btn running-timer-btn--finish"
                :disabled="finishTimerBtnDisabled" @click="finishTimer">&#xe765;</button>
            </div>
          </div>
        </template>
      </modal>
    </teleport>
  </div>
</template>

<script>
  //service
  import TimeEntryService from '@/apis/TimeEntryService'
  import TimeEntryCategoryService from '@/apis/TimeEntryCategoryService'
  // components
  import DateTimePicker from '@/components/DateTimePicker'
  import Avatar from "@/components/Avatar";
  import Badge from "@/components/Badge.vue";
  import NavToggleBtn from "@/components/NavToggleBtn";
  import SvgLogo from "@/components/icons/SvgLogo";
  import Modal from "@/components/Modal";
  export default {
    components: {
      Avatar,
      Badge,
      NavToggleBtn,
      SvgLogo,
      Modal,
      DateTimePicker
    },
    props: {
      // header的高度
      // height: {
      //   type: String,
      //   default: "3rem",
      // },
    },
    computed: {
      user() {
        return this.$store.getters.user
      },
      timerDurFormated() {
        return this.$timeTool.formatDuration(this.runningEntryDuration, true)
      }
    },
    data() {
      return {
        MtimeTrackBtnDisabled: false, // 自己计时中的提交按钮禁用状态
        finishTimerBtnDisabled: false, // 结束计时的按钮禁用状态
        modalTimeFormKey: -999, // 时间条目表单key值，强制每次打开重新渲染
        runningEntryDuration: '', // 正在进行中的时间条目已经过时间
        runningTimeEntry: {}, // 正在进行中的时间条目
        timeTrackFormVisble: false, // 是否显示时间追踪表单
        isManualTrack: false, // 是否是手动添加时间条目
        timeEntryCatList: [], // 时间条目分类
        timeTrackForm: { // 时间追踪表单内容
          description: '',
          catId: '',
        },
        // avatarUrl: "avatar.png",  // todo
      };
    },
    mounted() {
      // 检测有无正在计时的entry
      this.getRunningTimeEntry()
    },
    methods: {
      /*
       * 阻止表单enter后自动打开日期选择菜单
       */
      formTimeTrackHandleKeyEnter(e){
         e.preventDefault();

      },
      /*
       * 结束自动计时并向服务器发送更新请求
       */
      async finishTimer() {
        this.finishTimerBtnDisabled = true
        // 停止冒号的跳动效果
        const $colon = document.querySelector('.running-timer-duration__divider--blink')
        if ($colon) {
          $colon.classList.remove('running-timer-duration__divider--blink')
        }
        const end = this.$timeTool.currUtcMill()
        const res = await TimeEntryService.stopTimer(this.user.id, this.runningTimeEntry.id, end)
        if (res.success) {
          this.$toast.show('时间条目添加成功')
          this.$refs.MtimerRunning.close()
          this.runningTimeEntry = ''
          this.$emitter.emit('timeEntryUpdate')
          if (this.$route.path === '/' || this.$route.path === '/home') {
            this.finishTimerBtnDisabled = false
            return
          }
          this.$router.push('/timereport')
        } else {
          this.$toast.error('操作失败')
        }
        this.finishTimerBtnDisabled = false
      },
      /**
       * 提交时间追踪表单后的处理
       */
      handleTimeTrackFormSubmit() {
        this.MtimeTrackBtnDisabled = true
        const entry = {
          description: this.timeTrackForm.description.trim(),
          categoryId: this.timeTrackForm.catId,
          start: this.isManualTrack ? this.$refs['startTimePicker'].getOutput() : this.$refs['startTimePickerAuto'].getOutput()
        }
        if (entry.categoryId == '') {
          this.$toast.show('请选择项目分类')
          this.MtimeTrackBtnDisabled = false
          return
        }
        if (entry.start === -1) {
          this.$toast.show('开始时间不能晚于现在')
          this.MtimeTrackBtnDisabled = false
          return
        }
        entry.start = this.$timeTool.convertTMDHMtoUTC(entry.start)
        entry.start = this.$timeTool.convertStrToMill(entry.start)
        if (this.isManualTrack) {
          entry.end = this.$refs['endTimePicker'].getOutput()
          if (entry.end === -1) {
            this.$toast.show('结束时间不能早于现在')
            this.MtimeTrackBtnDisabled = false
            return
          }
          entry.end = this.$timeTool.convertTMDHMtoUTC(entry.end)
          console.log(entry.start)
          if (this.$timeTool.compareDate(entry.start, entry.end) === 1 || this.$timeTool.compareDate(entry.start, entry.end) === 0) {
            this.$toast.show('结束时间需要晚于开始时间')
            this.MtimeTrackBtnDisabled = false
            return
          }
          entry.end = this.$timeTool.convertStrToMill(entry.end)
          this.addTimeEntry(entry, true)
        }
        if (!this.isManualTrack) {
          this.addTimeEntry(entry, false)
        }
      },
      /**
       * 进入计时模式
       */
      enterTimerMode() {
        this.modalTimeFormKey++;
        this.$refs.MtimeTrackForm.close()
        // 打开显示正在计时的modal
        this.$nextTick(() => {
          this.$refs.MtimerRunning.open()
        })
        this.runningEntryDuration = this.$timeTool.currUtcMill() - this.runningTimeEntry.start
        this.durationtimer()
      },
      /**
       * 计算从条目开始后经过的时间
       */
      durationtimer() {
        setInterval(() => {
          this.runningEntryDuration = this.$timeTool.currUtcMill() - this.runningTimeEntry.start
        }, 1000)
      },
      /**
       * 添加条目分类
       * @param {object} entry  时间条目
       * @param {boolean} isManual 是否是手动添加开始结束时间
       */
      async addTimeEntry(entry, isManual) {
        console.log(entry)
        const res = await TimeEntryService.add(this.user.id, entry, isManual)
        if (res.success) {
          if (isManual) {
            this.$toast.show('添加成功')
            this.$refs.MtimeTrackForm.close()
            this.$emitter.emit('timeEntryUpdate')
            // 当添加后，当不在主页和时间清单页面时，自动跳转到时间清单页面,
            if (this.$route.path === '/' || this.$route.path === '/home') {
              this.MtimeTrackBtnDisabled = false
              return
            }
            this.$router.push('/timereport')
          } else {
            // 如果是自动计时模式
            this.runningTimeEntry = res.content
            this.enterTimerMode()
          }
        } else {
          this.$toast.error('操作失败')
        }
        this.MtimeTrackBtnDisabled = false
      },
      /**
       * 重置计时表单
       * @param {boolean} isClosing true代表是关闭modal后清除表单信息，false代表是在modal保持打开的状态下清除
       */
      resetTimeTrackForm(isClosing) {
        for (let prop in this.timeTrackForm) {
          this.timeTrackForm[prop] = ''
        }
        if (isClosing) {
          this.isManualTrack = false
        } else {
          this.$nextTick(() => {
            this.$refs['timeTrackFormFirstFocus'].focus()
          })
        }
      },
      /**
       * 获取时间条目分类
       */
      async getTimeEntryCatList() {
        const res = await TimeEntryCategoryService.list(this.user.id);
        if (res.success) {
          this.timeEntryCatList = res.content
        } else {
          this.$toast.error("获取分类失败")
        }
      },
      /**
       * 显示计时表单
       */
      showTimerForm() {
        this.timeTrackFormVisble = true
        this.$refs.MtimeTrackForm.open();
        this.$nextTick(() => {
          this.$refs['timeTrackFormFirstFocus'].focus()
        })
      },
      /**
       * 获取尚未计时结束的时间条目
       */
      async getRunningTimeEntry() {
        const res = await TimeEntryService.readRunningEntry(this.user.id)
        if (res.success) {
          this.runningTimeEntry = res.content
          if (this.runningTimeEntry) {
            this.enterTimerMode()
          }
        } else {
          this.$toast.error('同步数据失败')
        }
      }
    },
    watch: {
      // 在计时表单切换为手动的时候自动focus
      isManualTrack() {
        if (this.timeTrackFormVisble) {
          this.$nextTick(() => {
            this.$refs['timeTrackFormFirstFocus'].focus()
          })
        }
      }
    },
  };
</script>

<style lang="sass" scoped>
.userheader--smallscreen
  margin-left: -1.5rem
  margin-right: -1.5rem
  background: #fff
  height: 7rem
  box-shadow: 1rem 1rem 2rem #F0F0F0
  align-items: center
  position: relative
  .userheader
    &-togglemenu
      position: relative
      z-index: 2000
      height: 100%
      &__btndecoration
        cursor: pointer
        width: 12.5rem
        height: 60%
        left: -5rem
        box-shadow: 1rem 1.5rem 2rem rgba(159, 149, 136, 16%)
        border-radius: 3rem
        background: #FFDCAD
      &__btn
        height: 27%
        left: var(--padding-body--small-1)
      &__btn, &__btndecoration
        top: 50%
        transform: translateY(-50%)
        position: absolute
    &-logo
      height: 100%
      width: 100%
      position: relative
      &__link
        display: inline-block
        position: absolute
        top: 50%
        left: 50%
        transform: translate(-50%, -50%)

.userheader--largerscreen
  padding-right: 4rem
  .btn--readyfortimer
    width: 16rem
    height: 4.5rem
    border-radius: 4rem
    margin-right: 3rem
  .userheader
    &__wrapper
      width: 100%
      height: 100%
    &-badge
      margin-right: 2rem
      border-radius: 50%
      &:hover
        background: none !important
    &-avatar
      cursor: pointer
.userheader--largerscreen
   @media(min-width:1500px)
     margin-top: 4rem!important

.modal-timerform
  &__main
    padding-top: 4rem
    padding-bottom: 3rem
    width: 100%
    display: flex
    justify-content: center
    flex-direction: column
  .trackform
    &-type
      margin-bottom: 3rem

.running-timer
  user-select: none
  display: flex
  flex-direction: column
  align-items: center
  &-img
    width: 20rem
  &-duration
    width: max-content
    height: min-content
    margin: 0 auto
    display: flex
    &--loading
      line-height: 9.9rem
      margin-top: 2rem
      color: #ADADAD
    &__digit
      line-height: 5rem
      vertical-align: bottom
    &__divider
      line-height: 4.5rem
      // 当用户点击停止计时后，先把blink停下来等待api响应。
      &--blink
        animation: blinker 1s ease-out infinite
    &-block
      font-size: 4.5rem
      display: flex
      flex-direction: column
      align-items: center
      margin-right: 2rem
      &:last-of-type
        margin-right: 0
    &__desc
      font-size: 1.3rem
      color: #ADADAD
  &-btns
     display: flex
     justify-content: center
     margin-top: 2rem
     width: 12rem
  &-btn
    background: #F4D271
    width: 3rem
    height: 3rem
    display: inline-block
    border-radius: 50%
    color: #fff
    cursor: pointer
    &--finish
      font-size: 1.2rem
.btn--timersmallscreen
    background-color:  #FFDDAD
    width: 3.8rem
    height: 3.8rem
    border-radius: 1rem

.userheader--largerscreen
  height: 8rem
@keyframes blinker
  50%
    opacity: 0

</style>
