<template lang="html">
  <!-- 下拉菜单 -->
  <transition name="fade">
    <div v-click-away="onClickAway" class="dropdown-container  dropdown card--normal" v-if="active">
      <!-- 1. 类型: basic，内容放到slot里即可-->
      <div v-if="type === 'basic'">
        <slot></slot>
      </div>
      <!--  2.  类型: advaned ,有搜索框，也能记忆被选择的item-->
      <div v-if="type === 'advanced'">
        <!-- 搜索框-->
        <div class="dropdown__search-bar search-bar">
          <input ref="inputsearch" @compositionstart="handleCompositionStart"
            @compositionupdate="handleCompositionUpdate" @compositionend="handleCompositionEnd" @input="handleInput"
            class="search-bar__input" type="text" name="" value="" :placeholder="searchPlaceholder">
          <span class="icon icon-search icon--pushed-left">&#xe6d0;</span>
        </div>
        <!-- 下拉选择框内容 -->
        <div class="dropdown__options">
          <ul>
            <li class="dropdown__item" @click="optSelectedHandler(item)"
              :class="{ 'dropdown__item--selected': !multiple && selected[0] === item.id }"
              v-for="item in optionsDisplay" :key="item.id">
              <span v-if="withColor"> <span class="dropdown__item-color" :style="{ 'background': item.color }"></span>
                {{ item.name }} </span>
              <span class="item-with-checkbox-container" v-if="withCheckBox">
                <input type="checkbox" @row-clicked="this.checked = true" :value="item.id" v-model="selected"
                  class="checkbox-item dropdown__item-checkbox"> <span class="checkbox-item-label">{{ item.name }}</span>
              </span>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
  import {
    mixin as VueClickAway
  } from 'vue3-click-away'
  export default {
    emits: ['selected', 'selected-multiple'],
    mixins: [VueClickAway],
    props: {
      grouplist: Array,  
      options: Array, 
      searchPlaceholder: String,
      addBtnText: String,
      multiple: Boolean, // 是否option可以被同时选中多项
      selectedOptions: Array,
      withColor: Boolean, // 是否有颜色图标
      withCheckBox: Boolean, // 是否有checkbox
      selectedOtions: Array, // 被选中的options
      type: { //['basic' 'advanced']
        type: String,
        default: 'basic'
      } 
    },
    data() {
      return {
        isComposing: false,
        optionsDisplay: [], // 显示的option items（主要是用于根据用户keyword筛选的情况）
        active: false,
        selected: [], // 被选中的项
        // 设置是否会处理clickaway（目的是让特定窗口点击后dropdown不关闭）
        clickAwayDisabled: false
      }
    },
    computed: {
      route() {
        return this.$route
      }
    },
    watch: {
      /* eslint no-unused-vars: */
      route(to) {
        if (this.active) {
          this.active = false
        }
      },
      active(isActive) {
        if (isActive) {
          this.optionsDisplay = this.options
          if (this.type === 'advanced') {
            this.$nextTick(() => {
              this.$refs.inputsearch.focus()
            })
          }
        }
      }
    },
    methods: {
      /**
       * 打开
       */
      open() {
        this.active = true
      },
      /**
       * 关闭
       */
      close() {
        this.active = false
      },
      /**
       * 选择一项后的处理
       * @param {*} item 
       */
      optSelectedHandler(item) {
        if (!this.multiple) {
          // （1）传送选中项至parent component，以用来更新parent component中的name 和id
          this.$emit('selected', item)
          // （2）更新selected，以用来改变选中项的css样式。
          this.selected = []
          this.selected.push(item.id)
        } else {
          // (1) 改变checkbox对应model的值，如果checkbox已被选中，则从数组中删除对应id
          const index = this.selected.findIndex(id => id === item.id)
          if (index === -1) {
            this.selected.push(item.id)
          } else {
            this.selected.splice(index, 1)
          }
          //（2）将新的选中的项构成的数组传送给parent component
          this.$emit('selected-multiple', this.selected)
        }
        this.close()
      },
      handleCompositionStart() {
        this.isComposing = true
      },
      handleCompositionEnd() {
        this.isComposing = false
        // 输入中文时最后一个空格键不会触发input事件，所以要手动触发
        this.handleInput()
      },
      /**
       * 输入框输入内容时的处理
       */
      handleInput() {
        if (this.isComposing) {
          return
        }
        const keyword = event.target.value.trim()
        this.optionsDisplay = this.options.filter(item => item.name.includes(keyword))
      },
      /**
       * 点击其他地方时的处理
       */
      onClickAway() {
        // 如果被外部禁用了clickaway则什么也不做
        if (this.clickAwayDisabled) {
          return
        }
        if (this.active) {
          this.active = false
        }
      },
    }
  }
</script>

<style lang="sass" scoped>
  @import '@/assets/sass/components/card.sass'
  @import '@/assets/sass/components/form.sass'
  @import '@/assets/sass/components/icon.sass'
  @import '@/assets/sass/abstract/mixins.sass'
.dropdown
   padding: 1.5rem 1.8rem
   padding-bottom: 0
   &-container
    z-index: 99999
    width: 30rem
    box-shadow: 0 1px 10px  rgba(0,0,0, .1)
   &__options
     margin: .7rem 0
     //最多同时能看到7个选项
     max-height: calc(3.5rem * 7 )
     overflow-y: scroll
     &::-webkit-scrollbar
       -webkit-appearance: none
       width: 4px
     &::-webkit-scrollbar-thumb
       border-radius: 3px
       background-color: var(--color-grey-light-1)
   &__item, &__additem
      user-select: none
      font-size: 1.3rem
      color: var(--color-grey-main)
   &__item
      width: 100%
      display: block
      padding: 0 1rem
      @include text-nowrap
      height: 3.5rem
      line-height: 3.5rem
      cursor: pointer
      &:hover,&--selected
       background: #F1F1F1
       border-radius: 3px
      &-color
        display: inline-block
        width: .7rem
        height: .7rem
        margin-right: .5rem
   &__additem
     padding: 1.4rem 0
     display: flex
     align-items: center
     border-top: 1px solid var(--color-grey-light-1)
     color: #919191
     text-align: left
     padding-left: 1rem
     &:hover
      color: var(--color-grey-main)
     &-text
      cursor: pointer
     .iconfont
      margin-right: 1rem
      font-size: 1.6rem

.checkbox-item
  margin-left: 0
  &:checked
    &::before
      visibility: hidden
  &-label
    width: 100%
    margin-left: 5px
    @include text-nowrap
.item-with-checkbox-container
  display: flex
  align-items: center
  height: 100%
  width: 100%
// icons
.icon-search
  color: #A2A2A2
.fade-enter-active,
.fade-leave-active
  transition: opacity 0.3s ease-in
.fade-enter-from,
.fade-leave-to
  opacity: 0
</style>
