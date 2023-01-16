<template lang="html">
  <!-- 聊天窗口 -->
  <div class="chatwindow-container">
    <div class="wrapper">
      <!-- 聊天窗口头部 -->
      <div class="header">
        <!-- 疑问图标及其tooltip -->
        <tooltip txtStyle="color: #8F9EB0;font-size: 1.6rem" class="iconfont" width="30rem" txt="&#xe75a;">请在撰写前先编辑好问题，撰写过程中若对日记问题进行了编辑，则需第二天才能生效</tooltip>
        <div class="header__title">
          日记助手
        </div>
        <span></span>
      </div>
      <!-- 聊天对话区域 -->
      <div class="dialog">
        <ul>
          <li v-for="(item,index) in dialogs" :key="index" class="dialog__item">
            <!-- 当fromComputer属性存在且值为true时，将对话放在左边区域 -->
            <div class="dialog--left" v-if="item.fromComputer && item.fromComputer === true">
              <!-- 电脑端的头像 -->
              <avatar :dropdown="false" class="dialog__avatar" width="40px"></avatar>
              <!-- 电脑端对话的内容-->
              <div class="dialog__content">
                <!-- 如果isLoadingMsg属性为true，显示正在加载中，否则显示对话的content -->
                <p class="dialog__content--txt keep-spaces">
                  <span v-if="item.isLoadingMsg">
                        <span class="loading__dot loading__dot--1 mr-2"></span>
                  <span class="loading__dot loading__dot--2 mr-2"></span>
                  <span class="loading__dot loading__dot--3"></span>
                  </span>
                  <span v-else>{{item.content}}</span>
                </p>
              </div>
            </div>
            <!-- 对话右边区域 -->
            <div v-else class="dialog--right">
              <!-- <button v-if="item.saveError || item.memoError" :disabled = "resendMsgBtnDisabled"  @click="resendMsg(currQuestionId, item.content, item.saveError, item.memoError)" class="btn iconfont icon-resend" style="font-size: 1.8rem; color:#D33A31; ">&#xe757;</button> -->
              <p class="diaglog__txt dialog__content--txt keep-spaces">{{item.content}}</p>
            </div>
          </li>
        </ul>
      </div>
    </div>
    <!-- 聊天框底部输入区域 -->
    <div class="input">
      <!-- 多行文本输入框 -->
      <custom-input :disabled="userInputDisabled" rows="3" maxlength="300" style="padding: 5px; padding-bottom: 0;font-size: 1.4rem" ref="chatInput" v-model="userInput"></custom-input>
      <!-- emoji区域 -->
      <!-- <div class="input__emoji-region" v-click-away="onClickAwayEmojiPicker"  >
                  <div  v-show="emojiPickerVisible"  >
                    <Picker  title='' :i18n="emojiI18n" color='#B28FEF' :emojiSize="22" :perLine ="8" :showPreview="false" :showSkinTones="false" :data="emojiIndex" set="twitter" @select="handleSelectEmoji" />
                  </div>
                  <button  class="btn iconfont input__emojibtn" @click="emojiPickerVisible= !emojiPickerVisible">&#xe746;</button>
               </div> -->
      <button type="submit" :disabled="sendMsgBtnDisabled" class="btn input__sendbtn" @click="handleSendingMsg">发送</button>
    </div>
  </div>
</template>

<script>
  import Tooltip from '@/components/basicUI/Tooltip'
  import {
    mapGetters
  } from 'vuex'
  import DiaryService from '@/apis/DiaryService.js'
  import CustomInput from '@/components/CustomInput'
  import {
    mixin as VueClickAway
  } from "vue3-click-away";
  // import "emoji-mart-vue-fast/css/emoji-mart.css"
  // import data from "emoji-mart-vue-fast/data/all.json"
  // import { Picker, EmojiIndex } from 'emoji-mart-vue-fast/src'
  import Avatar from '@/components/Avatar'
  // let emojiIndex = new EmojiIndex(data)
  export default {
    mixins: [VueClickAway],
    emits: ['today-completed', 'diary-chat-msg-loaded', 'start-loading'],
    components: {
      // Picker,
      Avatar,
      CustomInput,
      Tooltip,
    },
    props: {},
    data() {
      return {
        // 存储当日日记内容，当日日记都完成后统一发送给后端。
        diaries: [],
        sendMsgBtnDisabled: true,
        userInputDisabled: true,
        // 当日的y-m-d时间
        dateToday: '',
        // // emoji表情组件相关
        // emojiI18n: {
        //   search:'搜索',
        //   notfound: '未找到',
        //   categories: {
        //      search: 'Search Results',
        //      recent: '热门',
        //      smileys: '表情',
        //       people: '人物&身体',
        //      nature: '动物&自然',
        //      foods: '食物&饮料',
        //      activity: '活动',
        //      places: '旅行&场所',
        //      objects: '物件',
        //      symbols: '标志',
        //      flags: '旗帜',
        //      custom: '自定义',
        //   }
        // },
        // emojiPickerVisible: false,
        // emojiIndex: emojiIndex,
        // emojisOutput: '',
        // isBeginner: true,
        // 目前对话进行到第几轮,初始值为-1，也就是对话未开始状态
        currentDialogStage: -1, // z
        // 存储双方的对话数据，对话框界面据此渲染
        dialogs: [],
        // 存储用户使用中的日记问题
        questions: [],
        userInput: '',
        // 当前要问用户的问题id
        currQuestionId: '',
        // // 上次当日未结束的对话
        // latestMemo: '',
        // 这个没用？？？
        // userDecidingIfContinuing: true
      }
    },
    computed: {
      ...mapGetters(['user']),
    },
    methods: {
      /**
       * 发送获取日记问题请求
       */
      async getAllInUseQues() {
        const res = await DiaryService.listInUseQues(this.user.id)
        if (res.success) {
          this.questions = res.content
          // 获取问题成功后再读取历史信息
          this.getHistoryDiaglos()
        } else {
          this.$toast.error('获取日记问题失败')
        }
      },
      /**
       * 滚动到聊天窗底部，以使得能看到完整的聊天窗信息
       */
      scrollToEnd() {
        if (this.dialogs.length === 0) {
          return
        }
        const $msgs = document.getElementsByClassName('dialog__content--txt')
        if ($msgs.length < 1) return
        const $newest = $msgs.item($msgs.length - 1)
        $newest.scrollIntoView({
          behavior: 'smooth'
        })
      },
      /**
       * 用户发送信息的处理
       */
      handleSendingMsg() {
        this.disableUserInput()
        if (this.todayCompleted) {
          return
        }
        let userContent = this.userInput.trim()
        if (this.currentDialogStage == -1) {
          // 刚开始允许输入空内容
          if (userContent === '') {
            userContent = ` `
          }
        } else {
          if (userContent.trim() == '') {
            this.$toast.show('请输入内容')
            this.enableUserInput()
            return
          }
        }
        // 将内容作为diary对象存入diaries数组
        if (this.currentDialogStage > -1) {
          this.diaries.push({
            answer: userContent,
            question: this.questions[this.currentDialogStage].description,
            order: this.currentDialogStage
          })
        }
        this.dialogs.push({
          content: userContent
        })
        this.currentDialogStage++
          this.generateDialog()
      },
      /**
       * 禁止用户输入
       */
      disableUserInput() {
        this.userInputDisabled = true
      },
      /**
       * 允许用户输入
       * @param {Boolean} 是否保存之前用户的输入内容
       */
      enableUserInput(keepContent) {
        if (!keepContent) {
          this.userInput = ''
        }
        this.userInputDisabled = this.todayCompleted || false
      },
      /**
       * 添加对话
       * @param {String} content 信息内容
       * @param {Boolean} fromComputer 信息是否是来自电脑
       * @param {Boolean} isLoadingMsg  是否是表示loading状态的信息
       */
      addDialog(content, fromComputer, isLoadingMsg) {
        this.dialogs.push({
          fromComputer,
          content,
          isLoadingMsg
        })
        // 在对话更新并渲染后将滚动条滑至底端
        this.$nextTick(() => {
          this.scrollToEnd()
        })
      },
      /**
       * 显示loading状态
       */
      showLoading() {
        this.addDialog('', true, true)
      },
      /**
       * 根据当前所处的对话阶段生成机器方的对话
       */
      generateDialog() {
        let msg = ''
        if (this.currentDialogStage === -1) { // 尚未开始今日对话
          // 没有问题时提示后return
          if (this.questions.length === 0) {
            msg = '暂时还没有日记问题，请先点击编辑问题按钮添加问题'
            this.addDialog(msg, true)
            this.disableUserInput()
            return
          }
          msg = `请输入任意内容并发送以开始今天(${this.dateToday})的日记书写。当日日记完成前请不要关闭或切换页面。`
          this.addDialog(msg, true)
          this.enableUserInput()
        } else {
          // 显示机器正在输入的动画效果，然后发送问题
          this.showLoading()
          // 9个问题还未结束并且还有剩余问题时继续对话
          if (this.questions[this.currentDialogStage] && this.currentDialogStage < 9) {
            msg = this.questions[this.currentDialogStage].description
            this.currQuestionId = this.questions[this.currentDialogStage].id
          } else {
            // 用户已回答完，向服务器提交数据，
            // 加上判定，避免重复提交
            this.saveDiaries()
            return
          }
          setTimeout(() => this.showComputerReply(msg), 300)
        }
      },
      /**
       * 发请求保存日记内容和对话
       */
      async saveDiaries() {
        if (this.todayCompleted) {
          let msg = '今日日记已书写完毕，数据已保存到服务器'
          this.$emit('today-completed')
          setTimeout(() => this.showComputerReply(msg), 300)
          return
        }
        this.sendMsgBtnDisabled = true
        const diariesInfo = {
          content: this.diaries,
          // 保存时要去掉最后一项电脑的loading内容
          dialogs: JSON.stringify(this.dialogs.slice(0, this.dialogs.length - 1))
        }
        const resSaveDiary = await DiaryService.saveDiary(diariesInfo, this.user.id, this.dateToday)
        // const resSaveDialog = await DiaryService.saveDialogs(JSON.stringify(this.dialogs), this.user.id, this.dateToday)
        if (resSaveDiary.success) {
          // 显示书写完毕
          let msg = '今日日记已书写完毕，数据已保存到服务器'
          this.todayCompleted = true
          this.$emit('today-completed')
          setTimeout(() => this.showComputerReply(msg), 300)
        } else {
          this.$toast.error('日记内容保存失败！')
          this.dialogs.pop()
          this.sendMsgBtnDisabled = false
          // ???不起作用
        }
      },
      /**
       * 将电脑的新信息加入到dialogs中，取代上一条loading信息
       * @param {String} msg 信息内容
       */
      showComputerReply(msg) {
        this.dialogs[this.dialogs.length - 1] = {
          fromComputer: true,
          content: msg,
          isLoadingMsg: false
        }
        this.enableUserInput()
      },
      // // emoji打开后点击别处后emoji框关闭
      // onClickAwayEmojiPicker(){
      //   this.emojiPickerVisible =false
      // },
      // // 选中emoji后输入框显示emoji
      // handleSelectEmoji(emoji){
      //  // this.emojisOutput = this.emojisOutput + emoji.native;
      //  this.userInput += emoji.native
      // },
      // 
      /**
       * 发请求获取当天对话框历史记录，以便显示在对话框中（当天日记已完成时）
       */
      async getHistoryDiaglos() {
        const res = await DiaryService.getHistoryDiaglos(this.user.id, this.dateToday)
        if (res.success) {
          this.dialogs = JSON.parse(res.content)
          // 当天已经完成日记的话，将stage设高，这样就不会再生成新对话了。
          if (this.dialogs.length > 0) {
            this.todayCompleted = true
            this.currentDialogStage = 10
          }
          // 生成对话
          this.generateDialog()
          // 发送日记聊天信息已载入事件。以停止loading spinner动画
          this.$emit('diary-chat-msg-loaded')
        } else {
          this.$toast.error('读取后台记录失败')
        }
      }
    },
    created() {
      // 发送start-loading事件以标记loading状态，以便显示loading spinner
      this.$emit('start-loading')
    },
    mounted() {
      // 获取当日的y-m-d格式日期
      this.dateToday = this.$timeTool.formatInYMD(this.$timeTool.getCurrUserTime())
      // 设置焦点为输入框
      this.$refs.chatInput.$refs.textarea.focus()
      // 读取日记问题
      this.getAllInUseQues()
    },
    watch: {
      // 当日对话结束后禁止输入
      todayCompleted(completed) {
        this.userInputDisabled = completed
      },
      // 对话没结束时，保持自动聚焦于输入框状态
      userInputDisabled(disabled) {
        if (!disabled) {
          this.$nextTick(() => {
            this.$refs.chatInput.$refs.textarea.focus()
            this.sendMsgBtnDisabled = false
          })
        }
      }
    }
  }
</script>

<style lang="sass" scoped>
    @import '@/assets/sass/components/button.sass'
    .input__sendbtn
        &:disabled
          cursor: not-allowed
    .wrapper
        display: flex
        flex-direction: column
        color: #5D5D5D
        margin-top: 2rem
        border-radius: 9px
        height: 60vh
        min-height: 30rem
        @media(min-width: $bp-md)
          min-height: 35rem
          height: calc(100vh - 270px)
    .header
        padding: 0 2rem
        margin-top: -2rem
        display: flex
        flex-shrink: 0
        justify-content: space-between
        align-items: center
        height: 8rem
        width: 100%
        border-bottom: .5px solid  rgba(190, 190, 190, .5)

        // // todo height

    .dialog
      width: 100%
      max-width: 100%
      overflow-x: hidden
      padding: 0 2rem
      &__item
        margin-top: 20px
        &:last-of-type
          margin-bottom: 20px
        &:first-of-type
          margin-top: 30px
      &__avatar
        margin-right: 20px
      &__content
        width: 100%
        &--txt
          border-radius: 8px
          padding: 10px
          height: max-content
          width: max-content
          max-width: 70%
          position: relative
          &::before
            content: ''
            position: absolute
            width: 0
            height: 0
            border-style: solid
            top: 14px

      &--left, &--right
        width: 100%
        display: flex

      &--left
        justify-content: flex-start
        .dialog__content--txt
          box-shadow: 0 10px 20px rgba(214, 231, 255, .6)
          background-color: #D6E7FF
          &::before
            border-color: transparent  #D6E7FF transparent transparent
            box-shadow: 0 10px 20px rgba(214, 231, 255, .6)
            left: -10px
            border-width: 6px 10px 6px 0
      &--right
        justify-content: flex-end
        .dialog__content--txt
          box-shadow: 0 10px 20px rgba(252, 129, 129, .1)
          background: #ffe2d6

          &::before
            border-color: transparent  transparent transparent #ffe2d6
            box-shadow: 0 10px 20px rgba(252, 129, 129, .1)
            right: -10px
            border-width: 6px 0 6px 10px

    .input
      border-top: .5px solid rgba(190, 190, 190, .5)
      // background: blue
      display: flex
      padding:  1.5rem
      width: 100%
      justify-content: space-between
      margin-top: auto
      margin-bottom: 10px
      height: max-content

      // background: #FFDDAA
      // &__textarea
      //   resize: none
      //   outline: none
      //   border-radius: 8px
      //   border: none
      //   width: 100%
      &__sendbtn
        background: #B28FEE
        border-radius: 20px
        margin-left: 10px
        width: 8rem
        height: 3rem
        color: #fff
      &__emoji-region
        position: relative
        width: min-content
      &__emojibtn
        margin-left: 2rem
        height: 3rem
        font-size: 2.5rem
        display: none
        @media(min-width: 1000px)
          display: inline-block
    .emoji-mart
      max-height: 30rem
      position: absolute
      bottom: 4rem
      right: 0
    .loading__dot
      display: inline-block
      width: .3rem
      height: .3rem
      border-radius: 50%
      background: #5D5D5D
      animation: 2s blink infinite
      &--1
        animation-delay: 500ms
        margin-left: 10px
      &--2
        animation-delay: 1000ms
      &--3
        margin-right: 10px
    // 保留用户的空格和换行
    .keep-spaces
      white-space: pre-wrap
    @keyframes blink
      50%
        background: transparent
</style>
