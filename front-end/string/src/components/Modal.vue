<template lang="html">
  <transition name="modal-fade">
    <!-- modal 遮罩，top为当前滚动条的位置-->
    <div v-if="show" class="modal__mask" :style="{top: scrollPos + 'px'}" >
      <!-- modal窗部分 -->

      <div class="modal__wrapper " :style="{ width: width, 'max-width': maxWidth }">
        <!-- 头部 -->
        <header class="modal__header">
          <span class="modal__header-title">{{ title }} </span>
          <span v-if="confirmbox && !title">删除确认</span>
          <button v-if="topCloseBtn" class="button--close button iconfont icon-close" @click.prevent="handleClickCloseBtn">&#xe676;</button>
        </header>
        <!-- 正文部分 -->
        <section class="modal__body">
          <slot name="body"></slot>
          <span v-if="confirmbox">确定删除吗？{{ delMsg }}</span>
        </section>
        <!-- 尾部 -->
        <footer class="modal__footer">
          <slot name="footer">
            <div v-if="confirmbox">
              <button type="button" class="btn--danger btn" :disabled="submitBtnDisabled" @click.prevent="$emit('confirm', args)">确定</button>
              <button type="button" class="btn--return btn ml-3" @click.prevent="show = false, $emit('cancle')">取消</button>
            </div>
            <div v-if="form">
              <button type="button" class="btn--submit btn" :disabled="submitBtnDisabled" @click="$emit('submit', args)">确定</button>
              <button type="button" class="btn--return btn ml-3" @click="$emit('reset')">重置</button>
            </div>
          </slot>
        </footer>
      </div>
    </div>
  </transition>
</template>

<script>
  import {
    enableBodyScroll,
    disableBodyScroll
  } from 'body-scroll-lock'
  import {isMobile} from '@/assets/js/utils.js'
  export default {
    emits: ['confirm', 'submit', 'reset', 'close', 'cancle'],
    props: {
      topCloseBtn: { // 右上角是否有关闭按钮
        type: Boolean,
        default: true,
      },
      onBeforeClose: { // 关闭之前的handler
        type: Function,
        required: false
      },
      confirmbox: { // modal类型是否为确认框
        type: Boolean,
        default: false
      },
      form: { // modal是否为表单
        type: Boolean,
        default: false
      },
      title: { // 标题
        type: String,
        default: '提示'
      },
      width: {
        type: String,
        default: '80%'
      },
      maxWidth: {
        type: String,
        default: '80vw'
      },
      delMsg: { // 删除时的确认消息
        type: String,
        default: '',
      },
      submitBtnDisabled: { // 提交按钮是否被禁用
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        scrollPos: '', // 点开modal之前滚动条的位置
        show: false, // modal是否显示
        args: {} // 保存接收的参数
      }
    },
    computed:{
      position(){
        return window.scrollY
      }
    },
    mounted(){

    },
    methods: {
      isMobile,
      /**
       * 关闭时如果提供了onBeforeClose函数，则先执行onBeforeClose
       */
      async handleClickCloseBtn() {
        if (this.onBeforeClose) {
          const shouldClose = await this.onBeforeClose()
          if (shouldClose) {
            this.close()
          }
        } else {
          this.close()
        }
      },
      /**
       * 打开modal
       * @param {*} payload 传入的参数
       */
      open(payload) {
        this.activate()
        if (payload) {
          this.args = payload
        }
      },
      /**
       * 关闭modal
       */
      close() {
        this.$emit('close')
        this.args = {}
        this.deactivate()
      },
      /**
       * 禁止页面滚动（非手机端）
       */
      disableScrolling() { 
         const $modal = document.querySelector('.modal__mask')
         this.scrollPos = `-${window.scrollY}px`;      
         if(!this.isMobile()){
           disableBodyScroll($modal)
         }
      },
      /**
       * 允许页面滚动
       */
      enableScrolling() {
        if(this.isMobile()) return
        const $modal = document.querySelector('.modal__mask')
        enableBodyScroll($modal)
      },
      /**
       * 打开modal后的一些处理
       */
      activate() {
        this.show = true
        // 禁止页面滚动
        this.$nextTick(() => {
          this.disableScrolling()
        })
      },
      /**
       * modal关闭时的一些处理
       */
      deactivate() {
         this.enableScrolling()
         this.$nextTick(() => {
           this.show = false
         })
      }
    },
  }
</script>

<style lang="sass" scoped>
.modal
  &__mask
    z-index: 99998
    width: 100%
    height: 100%
    position: fixed
    overflow-y: hidden
    top: 0
    display: flex
    justify-content: center
    align-items: center
    background-color: rgba(0, 0, 0, 0.2)
  &__wrapper
    z-index: 99999
    height: max-content
    background: #fff
    padding: 3rem
    box-shadow: .2rem .2rem 2rem rgba(122, 111, 134, .16)
    border-radius: 1rem
  &__header
    display: flex
    justify-content: space-between
    align-items: center
    height: max-content
    &-title
      user-select: none
      padding: .4rem
      display: inline-block
      height: 100%
      font-size: 1.6rem
      border-left: .5rem solid #F5C287
      padding-left: 1rem
  &__body
    margin: 3rem 0
  &__footer
    display: flex
    justify-content: flex-end
.button--close
  background: none
  border: none
  outline: none
  padding: 0
  font-size: 1.8rem
  padding: 0
  color: var(--color--grey-main)
</style>
