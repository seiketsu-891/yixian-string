<template lang="html">
  <!--日记内容格子 -->
  <div class="wrapper">
    <div class="row">
      <!-- 日记格子区 -->
      <div class="grid col-6 col-md-4" v-for="item in diaries" :key="item.id" @click="viewOrEdit(item)">
        <span class="grid-question">{{ item.question }}</span>
        <p class="grid-answer"><span>{{ item.answer }}</span></p>
      </div>
    </div>
    <!-- 点击日记格子后出现的修改框modal -->
    <teleport to="#app">
      <modal max-width="45rem" width="80%" :onBeforeClose="handleBeforeCloseEdit" 
        :title="currDiaryEntry.question || '查看日记'" ref="MDirayViewOrEdit">
        <template v-slot:body>
          <custom-input rows="12" maxlength="300" v-model="currDiaryEntry.answerNew" ref="diaryInputArea"
            class=""></custom-input>
          <div class="grid-modal__btns"></div>
        </template>
        <template v-slot:footer>
          <button class="btn btn--submit mt-2 mr-5" :disabled="saveNewAnsContentBtnDisabled"
            @click.prevent="submitEditDiaryEntry">保存</button>
          <button class="btn btn--danger mt-2" @click.prevent="$refs.MDelAns.open();">删除</button>
        </template>
      </modal>
    </teleport>

    <!-- 点击删除日记后出现的确认框 -->
    <teleport to="#app">
      <modal width="40rem" confirmbox :submitBtnDisabled="delAnsBtnDisabled" @confirm="delAns(currDiaryEntry)"
        ref="MDelAns"></modal>
    </teleport>

    <!-- 内容发现修改，但未点击保存就关闭modal后的提示框 -->
    <teleport to="#app">
      <modal ref="MIfDropChange" width="30rem">
        <template v-slot:body>
          日记内容发生了变化，确认不保存吗？
        </template>

        <template v-slot:footer>
          <button class="btn btn--submit mr-5"
            @click="$refs.MIfDropChange.close(); $refs.MDirayViewOrEdit.close()">确认</button>
          <button class="btn btn--default" @click="$refs.MIfDropChange.close()">返回</button>
        </template>
      </modal>
    </teleport>

  </div>
</template>

<script>
  import DiaryService from '@/apis/DiaryService.js'
  import Modal from '@/components/Modal'
  import CustomInput from '@/components/CustomInput'
  export default {
    emits: ['update'],
    components: {
      Modal,
      CustomInput,
    },
    props: {
      diaries: Array
    },
    data() {
      return {
        delAnsBtnDisabled: false,
        saveNewAnsContentBtnDisabled: false, // 打开修改日记modal后，保存按钮的禁用状态
        currDiaryEntry: { // 当前被点击的日记格子信息
          id: '',
          answerNew: '', 
          question: '',
          answer: '',
          date: ''
        },
      }
    },
    methods: {
      /**
       * 点击日记格子后的处理
       * @param {*} item 
       */
      viewOrEdit(item) {
        // 设置数据和文本框默认内容
        this.currDiaryEntry = item
        this.currDiaryEntry.answerNew = item.answer ? item.answer : ''
        this.$refs.MDirayViewOrEdit.open()
        this.$nextTick(() => {
          this.$refs.diaryInputArea.$refs.textarea.focus()
        })
      },
      /**
       * 点击关闭更新日记modal后的处理
       */
      handleBeforeCloseEdit() {
        // 内容不为空且内容与原内容不同时，提示是否放弃修改
        if (this.currDiaryEntry.answerNew.trim() !== '' && this.currDiaryEntry.answer.trim() !== this.currDiaryEntry.answerNew.trim()) {
          this.$refs.MIfDropChange.open()
        } else {
          this.$refs.MDirayViewOrEdit.close()
        }
      },
      /**
       * 发送请求删除日记某格内容
       * @param {*} item 
       */
      async delAns(item) {
        this.delAnsBtnDisabled = true
        const res = await DiaryService.delAns(this.userId, item.id, item.date)
        if (res.success) {
          this.$toast.show('删除成功')
          this.$refs.MDelAns.close()
          this.$refs.MDirayViewOrEdit.close()
          this.resetCurrDiary()
          this.$emit('delete')
        } else {
          this.$toast.error('删除失败')
        }
        this.delAnsBtnDisabled = false
      },
      /**
       * 提交更新日记
       */
      submitEditDiaryEntry() {
        if (this.currDiaryEntry.answerNew.trim() == '') {
          this.$toast.show('内容不能为空')
          return
        }
        if (this.currDiaryEntry.answer.trim() === this.currDiaryEntry.answerNew.trim()) {
          this.$toast.show('内容未变化')
          return
        }
        this.saveNewAnsContentBtnDisabled = true
        this.updateDiaryanswer()
      },
      /**
       * 发送请求更新日记答案内容
       */
      async updateDiaryanswer() {
        const diary = {
          id: this.currDiaryEntry.id,
          answer: this.currDiaryEntry.answerNew
        }
        const res = await DiaryService.updateAnsContent(this.userId, diary)
        if (res.success) {
          this.$toast.show('更新成功')
          this.$refs.MDirayViewOrEdit.close()
          this.resetCurrDiary()
          this.$emit('update')
        } else {
          this.$toast.error('日记更新失败')
        }
        this.saveNewAnsContentBtnDisabled = false
      },
      /**
       * 重置当前的日记内容
       */
      resetCurrDiary() {
        this.currDiaryEntry.id = ''
        this.currDiaryEntry.answerNew = ''
        this.currDiaryEntry.question = ''
        this.currDiaryEntry.answer = ''
      }
    },
    computed: {
      userId() {
        return this.$store.getters.user.id
      }
    }
  }
</script>

<style lang="sass" scoped>
  .grid
    padding: 2rem
    height: 20rem
    background: #fff
    border: 1px solid #F1F1F1
    cursor: pointer
    // 防止border重叠
    margin-right: -1px
    margin-bottom: -1px
    &:hover
      z-index: 4
      -webkit-box-shadow: 2px 8px 15px 5px rgba(153, 195, 195, .2)
      box-shadow: 2px 8px 15px 5px rgba(153, 195, 195, .3)
    &-question
      width: 100%
      display: inline-block
      font-weight: bold
      margin-bottom: 1rem
      white-space: nowrap
      overflow: hidden
      text-overflow: ellipsis
    &-answer
      position: relative
      width: 100%
      height: calc(100%-2rem)
      font-size: 1.4rem
      // 多行文字ellipsis
      overflow: hidden
      display: -webkit-box
      -webkit-box-orient: vertical
      -webkit-line-clamp: 6
      &--full
        position: absolute
        left: 0
        top: 0
        height: min-c
        background: #fff
        width: 100%
        padding: 2rem

</style>
