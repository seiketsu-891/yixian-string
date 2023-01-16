<template lang="html">
  <!-- 单个todo条目 -->
  <div class="todoitem__wrapper">
    <div class="todoitem-content">
      <!-- checkbox部分 -->
      <span class="todoitem__checkbox">
     <input type="checkbox" @change="handleChecked"  class="checkbox-item checkbox-item--grey" :disabled="checkBoxDisabled"  v-model="checked">
      </span>
      <!-- 条目内容描述 -->
      <span class="todoitem__desc" :class="{greyertext: checked}" spellcheck="false" contenteditable="true" @blur="onBlurTodoContent">
       {{description}}
     </span>
    </div>
    <!-- 条目编辑区 -->
    <div class="todoitem-crud" v-if="editable">
      <button v-if="movableCopy" class="btn iconfont icon-move todoitem-crud__btn todoitem-crud__btn--move" @click="$emit('drag')">&#xe782;</button>
      <button class="btn iconfont icon-delete todoitem-crud__btn  todoitem-crud__btn--move--del" @click="$emit('del')"> &#xe6e3;</button>
    </div>
  </div>
</template>

<script>
  import {isMobile} from '@/assets/js/utils.js'
  export default {
    emits: ['todo-checked', 'drag', 'edit', 'del'],
    props: {
      editable: {  
        type: Boolean,
        default: true
      },
      movable: {  
        type: Boolean,
        default: true
      },
      description: String,  // 描述内容
      done: Boolean, // 是否已完成
      checkBoxDisabled: { 
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        movableCopy: true, // movable的备份，主要用来修改在该组件内直接修改movable的值
        checked: false, // 主要用checked来决定css样式
      }
    },
    methods: {
      isMobile,
       /**
        * blur之后的处理（编辑）
        */
      onBlurTodoContent(event) {
        const oldContent = this.description
        const newContent = event.target.innerText.trim()
        // 只有在描述与原来不同并且不为空的情况下，才会传送edit请求
        if (oldContent == newContent) {
          event.target.innerText = oldContent
          return
        }
        if (!newContent) {
          event.target.innerText = oldContent
          this.$toast.error('待办描述不能为空')
          return
        }
        this.$emit('edit', {
          newContent
        })
      },
      /**
       * checkbox勾选之后的处理
       */
      handleChecked() {
        this.$emit('todo-checked')
      },
    },
    created(){
      // 移动设备上隐藏移动按钮
      if(this.isMobile()){
        this.movableCopy = false
      }
    },
    mounted() {
      this.movableCopy = this.movable
      this.checked = this.done
    }
  }
</script>

<style lang="sass" scoped>
@import '@/assets/sass/components/form.sass'
.todoitem
  &__wrapper
    position: relative
    padding: 5px
    border-radius: 5px
    background: #fff
    overflow: hidden
    &:hover .todoitem-crud
      display: block
  &-content
    width: 100%
    display: flex
    align-items: center
  &__checkbox
    margin-right: 6px
  &__desc
    word-break: break-all
  &-crud
    position: absolute
    top: 0
    right: 0
    width: max-content
    padding-top: 5px
    display: none
    background: #fff
    box-shadow: -2px -3px 18px 11px #fff
    &__btn
      color: #ADADAD
      &:not(:last-of-type)
        margin-right: 6px
      &:hover
        color: #5D5D5D
      &--move
        cursor: move
.checkbox-item
  border-radius: 50% !important
.greyertext
  color: #ADADAD

</style>
