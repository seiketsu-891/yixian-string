<template lang="html">
  <!-- 自定义多行文本框 -->
<div class="cusinput__container">
  <!-- 文本框 -->
   <textarea spellcheck="false"  :disabled="disabled" :value = "modelValue" class="textarea cusinput-textarea" ref="textarea" v-bind="$attrs"  @input="handleInput" @focus="handleFocus"
      @compositionstart="handleCompositionStart"
      @compositionend="handleCompositionEnd"
      @blur = "handleBlur" @keydown="handleKeyDown" @keyup="handleKeyUp">
    </textarea>
    <!-- 统计字符的区域 -->
    <span v-if="maxLen > 0"  class="cusinput__suffix--textarealimit cusinput__suffix" >
       {{ userInputLen}}/{{ maxLen }}
    </span>
</div>
</template>

<script>
export default {
  props: {
    // modelValue用来绑定原生输入框的输入值
    modelValue: {
      type: String,
    },
    disabled: {    // 是否禁止输入
      type: Boolean,
      default: false
    }
  },
  components: {

  },
  data() {
    return {
      isComposing: false,
    }
  },
  emits: ['update:modelValue','focus','blur','change','input'],

  methods: {
    // 实现输入值的绑定
    handleInput(){
       this.$emit('update:modelValue', event.target.value)
    },
    // 将focus事件emit到父组件
    handleFocus(event){
      this.$emit('focus', event)
    },
    // 将focus事件emit到父组件
    handleBlur(event){
      // this.focused = false ??
      this.$emit('blur', event)
    },

   handleKeyUp(){
     // 按键结束后，如果不是isComposing，则更新数据长度统计值
     if(this.isComposing){
       return
     }
     this.$emit('update:modelValue', event.target.value)
   },
    handleCompositionStart() {
      this.isComposing = true
    },
    handleCompositionChange() {

    },
    handleCompositionEnd() {
      this.isComposing = false
    },
  },
  computed: {
    maxLen() {         // 最大限制长度
      return this.$attrs.maxlength
    },
    userInputLen () {  // 用户已输入的长度
      return this.modelValue.length
    }
  },
}

</script>

<style lang="sass" scoped>
// @import '@/assets/sass/components/textarea.sass'
.textarea
  width: 100%
  resize: none !important
  outline: none !important
  border: none !important
  background: #F8F8F7
  padding: 2.5rem
  line-height: 1.6
  color: inherit

.cusinput
  &__container
    position: relative
    width: 100%
    height: auto
  &-textarea
    word-break: break-all
  &__suffix
    width: 100%
    position: absolute
    top: 100%
    display: inline-block
    text-align: right
    right: 0
    color: #C1C1C1
.ps
  height: 1rem
textarea
  &:disabled
   cursor: not-allowed
</style>
