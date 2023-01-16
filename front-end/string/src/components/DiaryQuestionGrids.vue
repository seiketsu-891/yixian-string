<template lang="html">
  <!-- 日记问题九宫格中的格子部分 -->
  <loading-spinner height="30rem" v-if="loading" />
  <div class="wrapper" v-else>
    <div class="row">
      <div class="grid col-6 col-md-4" v-for="item in questionList" :key="item.id">
        <button class="btn iconfont grid-delbtn" @click="$refs.MDelQues.open({id: item.id})">&#xe676;</button>
        <p class="grid-question" spellcheck="false">{{item.description}}
        </p>
      </div>
      <div class="grid col-6 col-md-4 gird--add" :class="{ 'grid--transparent' : !addFormVisible}" v-if="questionList.length < 9">
        <button class="btn iconfont grid-delbtn" v-show="addFormVisible" @click="addFormVisible=false">&#xe676;</button>
        <button class="btn iconfont grid-addbtn" v-if="!addFormVisible" @click="addFormVisible=true">&#xe6c2;</button>
        <custom-input rows="4" maxlength="30" ref="newQuescontentFocus" v-else v-model="newQuesForm.description" @keydown="blockCarriageReturn" style="background: #fff; padding: 0;" class="grid-input"></custom-input>
        <button class="btn grid-savebtn btn--default" :disabled="addQuesBtnDisabled" v-show="addFormVisible" @click="submitAddNewQue()">保存</button>
      </div>
    </div>
  </div>
  <!-- 删除日记问题确认modal -->
  <teleport to="#app">
    <modal ref="MDelQues" del-msg="(之前使用该问题的日记不受影响)" :submitBtnDisabled="delQuesBtnDisabled" max-width="40rem" confirmbox @confirm="delQuestion"></modal>
  </teleport>
</template>

<script>
  import {
    mapGetters
  } from 'vuex'
  import DiaryService from '@/apis/DiaryService.js'
  import CustomInput from '@/components/CustomInput'
  import LoadingSpinner from '@/components/basicUI/LoadingSpinner'
  export default {
    components: {
      LoadingSpinner,
      CustomInput,
    },
    computed: {
      ...mapGetters([
        'user',
      ]),
    },
    data() {
      return {
        loading: true,
        delQuesBtnDisabled: false,
        addQuesBtnDisabled: false,
        questionList: [],
        addFormVisible: false,
        newQuesForm: {
          description: ''
        }
      }
    },
    watch: {
      addFormVisible(formVisible) {
        if (formVisible) {
          this.newQuesForm.description = ''
          this.$nextTick(() => {
            this.$refs.newQuescontentFocus.$refs.textarea.focus()
          })
        }
      }
    },
    mounted() {
      this.getAllInUseQues()
    },
    methods: {
      /**
       * 发请求获取所有问题
       */
      async getAllInUseQues() {
        const res = await DiaryService.listInUseQues(this.user.id)
        if (res.success) {
          this.questionList = res.content
          this.loading = false
        } else {
          this.$toast.error('获取日记问题失败')
        }
      },
      /**
       * 提交增加问题
       */
      submitAddNewQue() {
        this.addQuesBtnDisabled = true
        const desc = this.newQuesForm.description.trim()
        if (!desc) {
          this.addQuesBtnDisabled = false
          this.$toast.show('问题内容不能为空')
        } else {
          const newQue = {
            description: desc
          }
          this.addQuestion(newQue)
        }
      },
      /**
       * 发请求添加问题
       * @param {*} newQue 
       */
      async addQuestion(newQue) {
        const res = await DiaryService.addQues(this.user.id, newQue)
        if (res.success) {
          this.$toast.show('添加成功')
          this.addFormVisible = false
          this.getAllInUseQues()
        } else {
          this.$toast.error('添加失败')
        }
        this.addQuesBtnDisabled = false
      },
      /**
       * 阻止默认回车行为
       * @param {*} event 
       */
      blockCarriageReturn(event) {
        const key = event.which || event.keyCode || 0
        if (key === 13) {
          event.preventDefault()
        }
      },
      /**
       * 发请求删除问题
       * @param {*} args 
       */
      async delQuestion(args) {
        this.delQuesBtnDisabled = true
        const res = await DiaryService.delQues(this.user.id, args.id)
        if (res.success) {
          this.$toast.show('删除成功')
          this.$refs.MDelQues.close()
          this.getAllInUseQues()
        } else {
          this.$toast.error('删除失败')
        }
        this.delQuesBtnDisabled = false
      }
    }
  }
</script>

<style lang="sass" scoped>
  .grid
    position: relative
    padding: 2rem
    height: 20rem
    background: #fff
    border: 1px solid #F1F1F1
    // 防止border重叠
    margin-right: -1px
    margin-bottom: -1px
    display: flex
    align-items: center
    flex-wrap: wrap
    &-delbtn
      position: absolute
      top: 1rem
      right: 1rem
      color: #8F9EB0
      &:hover
        color: #5D5D5D
    &-addbtn
      color: #8F9EB0
      font-size: 1.6rem
      width: 100%
      height: 100%
    &-question
      width: 100%
      height: max-content
      font-weight: bold
      &:focus+span
        display: block
    &--transparent
      background: rgba(255,255 , 255, .2) !important
      border-style: dashed
</style>
