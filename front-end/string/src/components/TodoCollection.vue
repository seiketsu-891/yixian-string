<template lang="html">
  <!-- 待办框 -->
  <!-- 如果提供了 hideWhenNoData 属性，那么在任务为空时会隐藏-->
  <div class="collection-container" v-if="(!hideWhenNoData || hideWhenNoData && taskList.length)" >
    <card rounded :class="{liststyle: showAsList}">
      <!-- 头部 -->
      <div class="header">
        <span class="header__title ">
          <tag v-if="priority==='11'" background="#FFE8E8" textColor="#F47573" class="home__todos-tag">紧急/重要</tag>
          <tag v-if="priority==='10'" background="#E9E8FF" textColor="#7A83B5" class="home__todos-tag">紧急/不重要</tag>
          <tag v-if="priority==='01'" background="#FFF6D8" textColor="#D3AD48" class="home__todos-tag">不紧急/重要</tag>
          <tag v-if="priority==='00'" background="#F5F5F5" textColor="#A5ADA5" class="home__todos-tag">不紧急/不重要</tag>
        </span>
        <button type="button" class="btn header__addbtn" v-show="isTodayAndAfter" @click="showAddForm">+</button>
      </div>
      <!-- 内容区 -->
      <div class="body" @dragleave="handleDragLeaveList">
        <div class="body__listwrapper drag-region" :class="{ hasminheight : !showAsList}" @drop="handleDropItem"
          @dragover="handleDragOver" @dragenter="handleDragEnterList">
          <div class="drag-placeholder-emptylist drag-placeholder hide" :data-index="-1"></div>
          <!--当所有待办都为空或均已完成时占位区-->
          <div class="list-placeholder" v-if="todos.length === 0 ">
            <!-- 当所有待办已完成 显示：  -->
            <div v-show="dones.length > 0" class="body__listwrapper-alldonetxt my-3">任务均已完成</div>
            <!-- 当任务列表完全为空（无待办和已完成）时显示 -->
            <empty-state v-if="dones.length === 0 && !isLoading" items="todos" :icon="false"> </empty-state>
          </div>
          <!-- todo内容列表 -->
          <ul class="todo__list body__list">
            <li v-for="(item,index) in todos" :key="item.id">
              <div class="" @dragover="handleDragOver" @dragenter="handleDragEnterItem">
                <!-- 每个todo前面的拖拽占位区域 -->
                <div class="drag-placeholder hide" :data-index="index" :class="{'drag-placeholder-first': index === 0}">
                </div>
                <!-- 单个todo条目内容-->
                <simple-todo-item draggable="true" @dragstart="handleDragStart(item.id,item.priority,index)"
                  @dragend="handleDragEnd" :checkBoxDisabled="false" @edit="handleEdit(item.id, $event)"
                  @del="handleDel(item)" @todo-checked="handleTodoChecked(item)" :description="item.description"
                  class="list__item" :done="item.done">
                </simple-todo-item>
                <!-- 最后一个todo后面还要再显示一个占位符 -->
                <div v-if="index === todos.length-1" class="drag-placeholder hide drag-placeholder-last"
                  :data-index="index+1"></div>
              </div>
            </li>
          </ul>
          <!-- 添加todo表单 -->
          <el-form ref="addForm" :model="addForm" class="addform" v-show="addFormVisible" :rules="addFormRules">
            <el-form-item prop="description">
              <el-input class="addForm__input" ref="addFormNameInput" spellcheck="false" v-model="addForm.description"
                placeholder="待办名称"></el-input>
            </el-form-item>
            <button type="submit" class="btn btn--submit addform__btn" :disabled="addTodoBtnDisabled"
              @click.prevent="submitAddTodo()">添加</button>
            <button type="reset" class="btn btn--default addform__cancelbtn" @click="resetAddForm()">取消</button>
          </el-form>
          <!-- 已完成的待办区域 -->
          <ul class="done__list body__list">
            <span class="body__divider" v-if="dones.length > 0 "></span>
            <li class="sortable-item" v-for="item in dones" :key="item.id">
              <simple-todo-item :checkBoxDisabled="true" :movable="false" @edit="handleEdit(item.id,  $event)"
                @del="handleDel(item)" checkDisabled :description="item.description" :done="item.done">
              </simple-todo-item>
            </li>
          </ul>
        </div>
      </div>
      <!-- body end -->
    </card>
  </div>
  <teleport to="body">
    <modal ref="MDelTodo" class="modal" max-width="35rem" confirmbox :submitBtnDisabled="delTodoBtnDisabled"
      @confirm="delTodo"></modal>
  </teleport>
</template>

<script>
  import {
    scrollUntilFullyVisible
  } from '@/assets/js/utils.js'
  import EmptyState from '@/components/basicUI/EmptyState'
  import TodoService from '@/apis/TodoService.js'
  import SimpleTodoItem from '@/components/listitem/SimpleTodoItem'
  import Card from '@/components/Card'
  import Tag from '@/components/basicUI/Tag'
  export default {
    inheritAttrs: false,
    components: {
      EmptyState,
      Card,
      Tag,
      SimpleTodoItem,
    },
    props: {
      priority: String,
      data: Array,
      showAsList: {
        type: Boolean,
        default: false
      },
      date: String,
      hideWhenNoData: {
        type: Boolean,
        default: false
      }
    },
    emits: ['update'], // 当todo 数据变化时发送update事件，使得母组件刷新数据,args: operation
    data() {
      return {
        isLoading: true,
        // markDoneTodoDisabled: false,
        delTodoBtnDisabled: false,
        addTodoBtnDisabled: false,
        // 用于避免页面一开始数据还未从后端获取时就出现“暂时没有待办”的占位
        addForm: {
          description: ''
        },
        addFormVisible: false,
        addFormRules: {
          description: [{
            validator: this.$validator.checkTodo,
            trigger: 'submit'
          }]
        }
      }
    },
    computed: {
      // 判断是否可以添加新的待办（已经过去的日期不能添加）
      isTodayAndAfter() {
        const today = this.$timeTool.parseYMD(this.$timeTool.getCurrUserTime())
        console.log(today)
        const comp = this.$timeTool.compareDate(this.date, today)
        console.log(this.date)
        return comp !== -1
      },
      listTodoAndDone() {
        return this.todos.concat(this.dones)
      },
      todos() {
        return this.taskList.filter(item => !item.done)
      },
      dones() {
        // comparedate的结果需要加上一个负号才是降序
        return this.taskList.filter(item => item.done).sort((a, b) => {
          let date = Date.parse(a.updateTime)
          let date2 = Date.parse(b.updateTime)
          console.log(date)
          console.log(date2)
          return date2 - date
        })
      },
      userId() {
        return this.$store.getters.user.id
      },
      taskList() {
        return this.data.filter(item => item.priority === this.priority)
      }
    },
    mounted() {},
    watch: {
      data() {
        // 实现当数据读取后，才会显示“暂时没有待办的占位符”
        this.isLoading = false
      }
    },
    methods: {
      /**
       *  当拖拽进入list区域（item之外）
       */
      handleDragEnterList() {
        const height = event.dataTransfer.types[3]
        event.stopPropagation()
        event.preventDefault()
        let $placeholder
        // 如果当前list本身没有项目则选取empty-placeholder，否则则作为最后一个插入
        if (this.todos.length === 0) {
          $placeholder = event.currentTarget.querySelector('.drag-placeholder-emptylist')
          event.currentTarget.querySelector('.list-placeholder').classList.add('hide')
        } else {
          $placeholder = event.currentTarget.querySelector('.drag-placeholder-last')
        }
        if ($placeholder) {
          this.removePlacehoder()
          $placeholder.style.height = height + 'px'
          $placeholder.classList.remove('hide')
          $placeholder.classList.add('temp')
        }
      },
      /**
       * 拖拽经过列表item时
       */
      handleDragEnterItem() {
        event.stopPropagation()
        event.preventDefault()
        // 删除之前的占位符
        this.removePlacehoder()
        // 获取当前占位符高度（被拖拽的li的高度）
        console.log(event.dataTransfer.types)
        const height = event.dataTransfer.types[3]
        console.log('placeholder-height' + height)
        // 设置该item上方的placeholder高度及令其显示
        let $placeholder
        $placeholder = event.currentTarget.firstElementChild
        if ($placeholder) {
          $placeholder.style.height = height + 'px'
          $placeholder.classList.remove('hide')
          $placeholder.classList.add('temp')
        }
      },
      /**
       *  使上次出现的placeholder不可见
       */
      removePlacehoder() {
        // 选取可见的placeholder
        const $tmpPlacholder = document.querySelector('.temp')
        if ($tmpPlacholder) {
          $tmpPlacholder.style.height = 0
          $tmpPlacholder.classList.add('hide')
          $tmpPlacholder.classList.remove('temp')
        }
      },
      // 文档中有说要允许放置，则需要进行默认行为的取消
      handleDragOver() {
        event.preventDefault()
      },
      /**
       * 当离开整个list区域时去除placeholder
       */
      handleDragLeaveList() {
        // 避免在子element上也触发dragleave事件
        if (event.target.classList.contains('body')) {
          console.log('leave list')
          this.removePlacehoder()
        }
      },
      /**
       *  drop的处理
       */
      handleDropItem() {
        const $placeholder = document.querySelector('.temp')
        if (!$placeholder) {
          return
        }
        const destIndex = $placeholder.getAttribute('data-index')
        const todo = {
          id: event.dataTransfer.getData('id'),
          priority: event.dataTransfer.getData('priority'),
          index: event.dataTransfer.getData('index'),
          date: this.date
        }
        // console.log('destIndex:'+ destIndex,'oriIndex:'+ todo.index )
        // 检测是否需要调整位置（顺序）
        let noPosChange = false
        // 如果优先区域未g发生改变 或者优先区域未变但是移动到自己的下一个元素之前，也就是自己原来的位置，则不需要调整顺序
        if (todo.priority == this.priority && ((Number)(todo.index) + 1 == destIndex)) {
          noPosChange = true
        }
        if (noPosChange) {
          console.log('there are no changes in order')
          return
        }
        todo.priority = this.priority
        this.reorder(destIndex, todo)
      },
      /**
       * 发送调整todo顺序请求
       * @param {Number} destIndex 目标位置的index
       * @param {*} currTodo 被拖拽的todo
       */
      async reorder(destIndex, currTodo) {
        console.log('api send')
        const res = await TodoService.reorder(this.userId, destIndex, currTodo)
        if (res.success) {
          this.$toast.show('操作成功')
          this.$emit('update', {
            operation: 'reorder'
          })
        } else {
          this.$toast.error('操作失败')
        }
      },
      /**
       * 结束drag
       */
      handleDragEnd() {
        // 隐藏”无待办“文字
        if (this.todos.length === 0) {
          event.currentTarget.querySelector('.list-placeholder').classList.remove('hide')
        }
        // 显示被拖拽项目，隐藏drag的placeholder
        event.currentTarget.classList.remove('hide')
        this.removePlacehoder()
      },
      /**
       * 开始drag的处理
       * @param {String} id 
       * @param {String} priority 
       * @param {Number} index 
       */
      handleDragStart(id, priority, index) {
        const $curr = event.currentTarget
        // 隐藏原位置上的element
        window.requestAnimationFrame(function() {
          $curr.classList.add('hide')
        })
        event.dataTransfer.setData('id', id)
        event.dataTransfer.setData('priority', priority)
        event.dataTransfer.setData('index', index)
        //只有在drop时才能用dataTransfer.getData获取值，所以这里为了在enter阶段能获取高度，将高度直接设置到类似key（而不是value）的位置
        event.dataTransfer.setData(event.target.offsetHeight, '')
      },
      /**
       * 点击编辑后的处理
       * @param {String} id 
       * @param {*} $event 
       */
      handleEdit(id, $event) {
        this.editTodo({
          id,
          description: $event.newContent
        })
      },
      /**
       * 发送删除todo请求
       * @param {*} args 
       */
      async delTodo(args) {
        this.delTodoBtnDisabled = true
        const item = args.item
        const ymd = this.$timeTool.getYMDStrings(item.date)
        const res = await TodoService.del(this.userId, item.id, ymd.y, ymd.m, ymd.d)
        if (res.success) {
          this.$toast.show('删除成功')
          this.$emit('update', {
            operation: 'del'
          })
        } else {
          this.$toast.error('删除失败')
        }
        this.$refs.MDelTodo.close()
        this.delTodoBtnDisabled = false
      },
      /**
       * 发送编辑todo请求
       * @param {*} todo 
       */
      async editTodo(todo) {
        const res = await TodoService.update(todo, this.userId)
        if (res.success) {
          this.$toast.show('修改成功')
          this.$emit('update', {
            operation: 'edit'
          })
        } else {
          this.$toast.error('修改待办失败')
        }
      },
      // handleDrag() {
      //   console.log('ondrag')
      // },
      /**
       * 点击删除后的处理
       * @param {*} item 
       */
      handleDel(item) {
        this.$refs['MDelTodo'].open({
          item
        })
      },
      /**
       * 发送将todo标记为已完成的请求
       * @param {*} item 
       */
      async handleTodoChecked(item) {
        const ymd = this.$timeTool.getYMDStrings(item.date)
        const res = await TodoService.markDone(this.userId, item.id, ymd.y, ymd.m, ymd.d)
        if (res.success) {
          this.$emit('update', {
            operation: 'markDone'
          })
        } else {
          this.$toast.error('设置待办完成失败')
        }
      },
      /**
       * 显示新增todo表单
       */
      showAddForm() {
        this.addForm.description = ''
        this.addFormVisible = true
        this.$nextTick(() => {
          this.$refs.addFormNameInput.focus()
          // 使得addform完整显示
          scrollUntilFullyVisible(this.$refs.addForm.$el)
        })
      },
      /**
       * 提交“todo添加”表单
       */
      submitAddTodo() {
        this.addTodoBtnDisabled = true
        this.$refs.addForm.validate(valid => {
          if (valid) {
            this.addTodo()
          } else {
            this.addTodoBtnDisabled = false
            return false
          }
        })
      },
      /**
       * 发送增加todo的请求
       */
      async addTodo() {
        const todo = {
          description: this.addForm.description,
          priority: this.priority,
          date: this.date
        }
        const res = await TodoService.add(todo, this.userId)
        if (res.success) {
          this.$toast.show('添加成功')
          this.resetAddForm()
          this.$emit('update', {
            operation: 'add'
          })
        } else {
          this.$toast.error('添加失败！' + res.message)
        }
        this.addTodoBtnDisabled = false
      },
      /**
       * 重置添加todo表单
       */
      resetAddForm() {
        this.addForm.desc = ''
        this.addFormVisible = false
        this.$refs['addForm'].clearValidate()
      }
    }
  }
</script>

<style lang="sass" scoped>

.header
  display: flex
  justify-content: space-between
  align-items: flex-start
  &__addbtn
    background: #F7F7F7
    color: #B9B9B9
    border-radius: 50%
    height: 30px
    width: 30px
.body
  padding: 5px
  &__listwrapper
      width: 100%
      padding-bottom: 2rem
      min-height: max-content
      &-alldonetxt
        color: #B7B7B7
  &__divider
    display: block
    background: #F7F7F7
    height: 1px
    margin: 10px 0
    width: 100%
.addform
  margin-top: 2rem
  &__cancelbtn
    margin-left: 2rem

.hasminheight
  min-height: 20rem
// override
.card
  border: 1px solid #EFF1F7
  padding-bottom: 0
.liststyle
  border: none !important
  padding: 0 !important

.drag-placeholder
 width: 100%
 transition: all .3s
 background: #F7F7F7
 border-radius: 5px
.hide
 display: none

</style>
