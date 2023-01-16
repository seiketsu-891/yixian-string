<template lang="html">
  <!-- 时间表 -->
  <div class="timetable-container">
    <div class="header">
      <span class="header-msg">点击时间块可查看该时间块详情</span>
    </div>
    <!-- 利用bootstrap的col以列的形式生成每天的时间表列 -->
    <div class="timetable d-xl">
      <!-- row start -->
      <div class="row no-gutters">
        <!-- 大屏幕时完整时间表的时间刻度start -->
        <div class="time-marks d-none d-xl-block col-xl">
          <div class="timetable__cell--header">时刻</div>
          <!-- hours存储的是时间刻度线上的数字 -->
          <div v-for="(hour,index) in hours " class="time-marks__hourmark timetable__cell" :key="index">{{hour}}</div>
        </div>
        <!-- 大屏幕时完整时间表的时间刻度end -->
        <!-- start -->
        <div class="col-12 time-col col-xl" v-for="(dailyData,index) in dataReversed" :key="index" style="display: flex">
          <!-- 小屏幕时的时间刻度 start -->
          <div class="time-marks  col-3  col-md-2 d-xl-none" style="padding: 0">
            <div class="timetable__cell--header">时刻</div>
            <div v-for="(hour,index) in hours " class="time-marks__hourmark timetable__cell" :key="index">{{hour}}</div>
          </div>
          <!-- 小屏幕时的时间刻度 end -->
          <!--时间表一列的内容 start  -->
          <div class="col-9 col-md-10 col-xl" style="padding: 0">
            <!--  表头的星期和时间 -->
            <div class="dailyblocks__date timetable__cell--header">
              {{dailyData.weekday}} {{dailyData.date.substring(5)}}
            </div>
            <!-- 背景表格和时间块区域 start -->
            <div class="timetable__slotswrapper">
              <!-- 生成当天time block的背景表格 start-->
              <div v-for="(hour, index) in hours" :key="index" class="timetable__hourslot timetable__cell">
              </div>
              <!--  生成当天time block的背景表格cell end -->
              <!-- 生成当天entry构成的时间块 start-->
              <div class="timetable__slotcontent-wrapper" v-if="dailyData.timeEntries.length > 0">
                <!-- 每个时间块 -->
                <div @click="showEntryDetail(entry)" class="timetable__slotcontent" :class="{centerabs: calBlockWidth(entry.shareNumber)==='95%'}" v-for="(entry,ind) in dailyData.timeEntries" :key="ind" :style="{top: percentageMinutesPast(entry.start), left: calBlockLeft(entry.shareOrder, entry.shareNumber), width: calBlockWidth(entry.shareNumber), height: percentageDuration(entry.durationRaw), background: calcBlockBg(entry.color)}">
                  <!-- 时间块上显示的信息 start -->
                  <div>
                    <!-- 这个span是时间块旁边类似边框的长方体 -->
                    <span class="timetable__slotcontent-rect" :style="{ background: entry.color }"></span>
                    <!-- 时间块真正的区域 -->
                    <div class="timetable__slotcontent-info" :style="{color: entry.color}">
                      <div class="timetable__slotcontent-info-catandbtn"><span class="timetable__slotcontent-info--cat"> {{entry.cat}}</span>
                      </div>
                      <span class="timetable__slotcontent-info--desc"> {{entry.desc}}</span>
                    </div>
                  </div>
                  <!-- 时间块上显示的信息  end -->
                </div>
              </div>
              <!-- 生成当天entry构成的time block  end-->
            </div>
            <!-- 背景表格和时间块区域 end -->
          </div>
          <!-- 时间表一列的内容 end-->
        </div>
        <!-- end -->
      </div>
      <!-- row end -->
      <!-- 时间块的详细信息小窗口 start-->
      <div class="timetable__slotcontent--detail" v-show="detailVisible">
        <span><span class="timetable__slotcontent--detail-catcolor" :style=" {'background': currEntry.color}"></span>{{currEntry.cat}}</span>
        <span>{{currEntry.desc}}</span>
        <span>{{currEntry.start}}-{{currEntry.end}}</span>
        <span>历时{{currEntry.duration}}</span>
        <div class="timetable__slotcontent--detail-btn">
          <button class="btn iconfont icon-edit" @click="detailVisible=false; $emit('edit', currEntry)">&#xe6eb;</button>
          <button class="btn iconfont icon-delete" @click="detailVisible=false;$emit('del', currEntry.id)">&#xe6e3;</button>
        </div>
      </div>
      <!-- 时间块的详细信息小窗口 end-->
    </div>
  </div>
</template>

<script>
  import {
    createPopper
  } from '@popperjs/core'
  import {
    toTransparentColor
  } from '@/assets/js/colorUtil'
  export default {
    components: {},
    emits: ['edit', 'del'],
    props: {
      timeFormat: {
        type: String,
        default: 'HOUR24'
      },
      data: {
        type: Array // [date {timeentries}],[]
      },
      startHour: { // 刻度线最早时刻，之所以设置这个是考虑到如果想改变开始时间的话（因为0点开始到起床可能都完全没有时间条目，导致表格大片空白）
        type: Number,
        default: 0
      },
    },
    data() {
      return {
        currEntry: {},
        // 当前是否显示时间条目显示信息菜单
        detailVisible: false,
        // 存储时间点刻度线数据信息
        hours: [],
        timeBlockData: [],
        // 每天的分钟数
        minutesPerDay: 24 * 60,
      }
    },
    mounted() {
      // 生成时间刻度
      this.generateHourMarks()
    },
    watch: {
      data() {
        this.detailVisible = false
      }
    },
    methods: {
      /**
       * 出现显示时间表详细信息的小菜单
       * @param {*} entry 
       */
      showEntryDetail(entry) {
        if (this.currEntry.id === entry.id) {
          this.detailVisible = !this.detailVisible
          return
        }
        if (this.detailVisible) {
          this.detailVisible = false
        }
        this.currEntry = entry
        const popperOption = {
          placement: 'left',
          modifiers: [{
            name: 'preventOverflow',
            options: {
              boundary: document.querySelector('.timetable'),
              altAxis: true,
            }
          }, ]
        }
        const $target = event.target
        const $tooltip = document.querySelector('.timetable__slotcontent--detail')
        createPopper($target, $tooltip, popperOption)
        this.detailVisible = true
      },
      /**
       * 计算时间块的左边距离
       * @param {Number} shareOrder  在需要share的时间块中的顺序
       * @param {Number} shareNumber 需要share的时间块总数
       */
      calBlockLeft(shareOrder, shareNumber) {
        if (!shareNumber) {
          return ''
        } else {
          if (shareOrder === 0) {
            return '2.5%' // (100% - 95%)/2 ， 95%是默认情况下的宽度
          }
          return shareOrder * (95 / shareNumber) + '%'
        }
      },
      /**
       * 计算时间块的宽度
       * @param {*} shareNumber 
       */
      calBlockWidth(shareNumber) {
        if (!shareNumber) {
          return '95%'
        } else {
          return 95 / shareNumber + '%'
        }
      },
      /**
       *  生成时间表左侧的小时刻度数据[01:00, 02:00....]
       */
      generateHourMarks() {
        // 如果是5点开始，那么就到第二天5点结束。
        const endHour = this.startHour + 24
        for (let i = this.startHour; i < endHour; i++) {
          //如果大于24，需要转化为i-24的形式，例如：25点转化为1点。
          let hour = i - 24 >= 0 ? i - 24 : i
          let suffix = ''
          // 如果用户设置为12小时制，则转化为加上PM等后缀
          if (this.timeFormat === 'HOUR12') {
            if (hour >= 12) {
              hour = hour - 12
              suffix = 'PM'
            } else {
              suffix = 'AM'
            }
          }
          hour = hour < 10 ? `0${hour}:00` : `${hour}:00`
          this.hours.push(hour + suffix)
        }
      },
      toTransparentColor,
      /**
       * 计算block距离时间表最上端的距离（css top值）
       * @param {String} start 时间块开始时间
       */
      percentageMinutesPast(start) {
        if (!start) {
          return
        }
        let hour = Number(start.split(':')[0])
        let min = start.split(':')[1]
        min = Number(min.substring(0, 2))
        if (this.timeFormat === 'HOUR12') {
          if (start.substring(start.length - 2) === 'PM') {
            hour += 12
          }
        }
        const minutesPastStartHour = hour * 60 + min - this.startHour * 60
        const percentage = minutesPastStartHour / this.minutesPerDay * 100
        return `${percentage}%`
      },
      /**
       * 计算time entry经历的时间在一天总分钟数中所占的比例，从而得出block在整个时间表的高度比例
       * @param {Number} duration 
       */
      percentageDuration(duration) {
        return `${duration/(this.minutesPerDay*60*1000)*100}%`
      },
      /**
       * 计算block背景颜色
       * @param {String} catColor 
       */
      calcBlockBg(catColor) {
        if (!catColor) return
        return this.toTransparentColor(catColor, 28)
      },
      /**
       * 显示编辑菜单
       */
      showCrudMenu(id) {
        const dropdownRef = `dp${id}`
        console.log(this.$refs[dropdownRef].active)
        this.$refs[dropdownRef].active = !this.$refs[dropdownRef].active
      },
      // /*
      //  * 计算当前时间条目与上一个时间条目是否有重叠
      //  * @param prev 前一个时间条目
      //  * @param curr 当前时间条目
      //  * @return true/false
      //  */
      // timeOverlap(prev, curr) {
      //   //如果当前条目的开始时间小于上一个条目的结束时间，说明有重叠
      //   return curr.startRaw < prev.endRaw
      // },
      /*
       * 设置当时间条目有重叠时的属性
       * @param arr 元素所在的数组
       * @param left 重叠开始的index；
       * @param right 重叠结束的index
       */
      setShareNumberAndOrder(arr, left, right) {
        // 数据有效性检验
        if (right <= left || left >= arr.length || right > arr.length) {
          return
        }
        // 给每个对应项目设置shareNumber和shareOrder
        const shareNumber = right - left + 1
        let shareOrder = 0
        for (let i = left; i <= right; i++) {
          arr[i].shareNumber = shareNumber
          arr[i].shareOrder = shareOrder++
        }
      },
      /**
       * 标记时间块重叠性
       * @param {Array} list 某一天的时间块数组
       * @param {Number} index 当前项的索引
       */
      checkOverlap(list, index) {
        if(index === list.length-1) return
        // 对于某一项，检查后面的项开始时间是不是小于自己的结束时间
        for (let i = index + 1; i < list.length; i++) {
          if (list[i].startRaw < list[index].endRaw) {
            list[i].overlapPrev = true
          }
        }
      }
    },
    computed: {
      /*
       * 根据数据库信息更新每周时间条目的值
       */
      dataReversed() {
        const listWeek = this.data.slice().reverse()
        // 处理时间条目时间重叠问题
        listWeek.forEach((item) => {
          // 获取每一天的时间条目数据并存放在一个数组中
          const dailyList = item.timeEntries
          dailyList.sort((a, b) => a.startRaw - b.startRaw)
          console.log(dailyList)
          // 为时间条目增加一个overlapPrev（boolean）属性，如果和前一个时间块有重叠，则进行属性增加
          dailyList.forEach((curr, i) => {
            this.checkOverlap(dailyList, i)
            // if (i > 0) {
            //   const prev = dailyList[i - 1]
            //   // 标记当前时间条目的时间块是否和前面有时间重叠
            //   if (this.timeOverlap() {
            //     console.log("当前:" + curr.start)
            //     curr.overlapPrev = true
            //   }
            // }
          })
          // share指的是多少个时间条目会互相有时间重叠的部分，
          // 例如第一个条目是[8:00-9：00]，第二个是[8:30-10:00]，第三个是[9:00-10:30].那么这三个条目在时间表中显示的时候每个只站列宽的1/3
          // 这种情况下会给每个条目设置属性shareNumber=3
          // 如果时间条目时间有重叠，时间条目的宽度会是列宽除以share的份数，而距离左边的宽度要根据它在重叠中的顺序来计算。
          let i = 1
          while (i < dailyList.length) {
            if (dailyList[i].overlapPrev) {
              // left指的是需要share的元素中最左边的那个
              const left = i - 1
              //如果后面的元素一直连续和前面重叠，则i一直增加
              while (dailyList[i] && dailyList[i].overlapPrev) {
                i++
              }
              const right = i - 1
              this.setShareNumberAndOrder(dailyList, left, right) // 将left到right的部分都设置shareNumber和order属性属性
            } else {
              i++
            }
          }
        })
        return listWeek
      }
    }
  }
</script>

<style lang="sass" scoped>
@import '@/assets/sass/abstract/mixins.sass'
.header
  padding: 2rem
  &-msg
    color: #93999F
    border: 1px solid #F4F4F4
    background: #FCFCFC
    display: block
    border-radius: 1.2rem
    padding: 1rem 2rem
    width: max-content
.time-marks
  color: #93999F
  font-size: 12px
  background: #fff
  display: block
  width: 90px
  text-align: center

.timetable
  user-select: none
  border-radius: 10px
  overflow: hidden
  // border: 1px solid rgba(147, 153, 159, .2)
  &__cell

    border-right: 1px solid #E9EBEC
    height: 9rem
    &--header
      padding: 8px
      color: #93999F
      background: #F9FAFD
      text-align: center
      font-size: 14px
      line-height: 4rem

  &__hourslot
    &:not(:first-of-type)
      border-top: 1px solid #F7F7F7
  &__slotswrapper
    position: relative
    // border: 1px solid black
  &__slotcontent
    overflow: hidden
    cursor: pointer
    position: absolute
    // background: #C7CCDC
    border-top-right-radius: 8px
    border-bottom-right-radius: 8px
    &--detail
      font-size: 1.3rem
      position: absolute
      background: #fff
      // left: -3rem
      //todo test when name is very long
      width: max-content
      max-width: 80vw
      padding: 1.5rem
      background: #f9f9f9
      border-radius: 9px
      color: inherit
      box-shadow: 0 5px 7px rgb(0 0 0 / 10%)
      // color: #fff
      // box-shadow: 2rem 3rem 4rem #E1E9EB
      z-index: 99999
      @media(min-width: 400px)
        max-width: 30rem
      > *
        display: block
        &:not(:last-of-type)
          margin-bottom: 7px
        @include text-nowrap
      &-catcolor
        display: inline-block
        height: 1rem
        width: 1rem
        border-radius: 50%
        margin-right: 1rem
      &-btn
        overflow: visible
        margin-top: 1rem
        display: flex
        justify-content: flex-end
        > *
          font-size: 1.1rem
          width: 2rem
          height: 2rem
          border-radius: 50%
          box-shadow:  0px 3px 3px rgba(0, 0, 0, 0.1)
          transition: all .3s
          &:hover
            transform: translateY(-3px)
          &:first-child
            margin-right: 1rem
    &-rect
      display: block
      width: 4px
      position: absolute
      height: 100%
      // background: pink
      top: 0
      left: 0
    &-info
      margin-top: 10px
      padding-left: 10px
      display: flex
      font-size: 12px
      width: max-content
      width: 100%
      line-height: 2
      display: flex
      flex-direction: column
      &-catandbtn
        justify-content: space-between
        display: flex
      &--cat
        @include text-nowrap
      &--crudbtn
        color: inherit
      &--desc
       @include text-nowrap
    &-crud-menu
      position: absolute
      color: #6A767E
      font-size: 13px
      width: max-content
      top: 40px
      left: calc(100% - 30px)
  .listitem
    width: max-content
    transition: all .3s
    padding : 0  25px
    &:hover
      color: var(--color-black)
    &:not(:last-of-type)
      margin-bottom: var(--margin-listitem--normal)
.centerabs
  left: 50%
  transform: translateX(-50%)


</style>
