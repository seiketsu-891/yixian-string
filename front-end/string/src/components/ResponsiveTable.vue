<template lang="html">
  <!-- 响应式表格（时间条目分类区） -->
  <div>
    <table class="table">
      <tr>
        <!-- 表格头部 -->
        <th class="table-header" v-for="(col, index) in columns" :key="index"
          :class="{ 'no-border-left': col.mergeBefore, 'd-none': index > 0, 'd-lg-table-cell': index > 0, 'min-width-col': col.minW, 'first-header-col': index === 0 }">
          <div :class="{ 'first-col': index === 0, 'sortable-col': col.numericalSort }">
            {{ col.label }}
            <div v-if="col.numericalSort" class="sort-wrapper"><span class=" sortbtn sortbtn--asc iconfont icon-up"
                @click="sort(col.fieldName, 'asc')">&#xe70d;</span><span
                class=" sortbtn sortbtn--desc iconfont icon-down" @click="sort(col.fieldName, 'desc')">&#xe71b;</span>
            </div>
          </div>
        </th>
        <th class="table-header d-none d-lg-table-cell" style="width: 6rem;">操作</th>
      </tr>
      <!-- 表格内容区 -->
      <!-- 当读取数据中或是数据为空时的占位行 -->
      <tr class="table-emptyplaceholder" style="height: 20rem" v-if="loading">
        <td :colspan="columns.length + 1" align="center">
          <loading-spinner height="20rem" />
        </td>
      </tr>
      <!-- 占位行 end -->
      <!-- 正式内容区 -->
      <tr v-for="item in dataCopy" :key="item.id" class="table-body__row">
        <td class="" :class="{ 'd-none': index > 0, 'd-lg-table-cell': index > 0 }" v-for="(col, index) in columns"
          :key="index">
          <div :class="{ 'first-col': index === 0 }" class="table-uncollapsedcontainer">
            <div> <span v-if="col.withColor" class="table-body-colorshape" :style="{ background: item.color }"></span>
              <span>{{ item[col.fieldName] }}</span>
            </div>
            <button @click="showCollapsedCollumns()" class="table-collapsebtn btn d-inline d-lg-none "><span
                class="table-collapsebtn__txt icon-pull-down iconfont">&#xe6f6;</span>
              <span class="table-collapsebtn__txt icon-pull-up iconfont d-none">&#xe6f2;</span></button>
          </div>
          <!-- 被折叠的内容 -->
          <table class="table-collapsedcontainer d-lg-none unexpanded">
            <tr class="table-collapsedcontent" v-for="(col, index) in columns" :key="index">
              <td v-if="index > 0" class="collapsedcontent--prop ">{{ col.label }}</td>
              <td v-if="index > 0" class="collapsedcontent--val ">
                <span v-if="col.withColor" class="table-body-colorshape" :style="{ background: item.color }"></span> {{
                    item[col.fieldName]
                }}
              </td>
            </tr>
            <tr>
              <td width="50%">操作</td>
              <td>
                <span v-if="item.id == 0">不可修改</span>
                <button v-if="item.id != 0" class="btn btn--default" style="margin-right: 2rem"
                  @click.prevent="$emit('edit', item)">编辑</button>
                <button v-if="item.id != 0" class="normal btn btn--danger"
                  @click.prevent="$emit('del', item.id)">删除</button>
              </td>
            </tr>
          </table>
          <!--end 被折叠内容 -->
        </td>
        <td class="d-none d-lg-table-cell" style="padding: 1.7rem; ;">
          <!-- dropdown 编辑删除菜单-->
          <el-dropdown trigger="click">
            <span class="el-dropdown-link">
              <span v-if="item.id != 0" class="iconfont icon-more">&#xe6bf;</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click.prevent="$emit('edit', item)">编辑</el-dropdown-item>
                <el-dropdown-item @click.prevent="$emit('del', item.id)">删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </td>
      </tr>
    </table>
  </div>
</template>
<script>
  import LoadingSpinner from '@/components/basicUI/LoadingSpinner'
  export default {
    components: {
      LoadingSpinner
    },
    props: {
      loading: Boolean,
      data: Array,
      columns: Array, // [{label, fieldName, filter,withColor, mergeAfter, mergeBefore, numericalSort}
    },
    data() {
      return {
        dataCopy: this.data, 
        currSort: { // 存储当前排序依据的属性和排序方式，避免刷新数据后排序丢失
          filed: '',
          order: ''
        }
      }
    },
    watch: {
      data() {
        this.dataCopy = this.data
        // 防止刷新数据后失去之前的排序
        if (this.currSort.field && this.currSort.order) {
          this.keepSort(this.currSort.field, this.currSort.order)
        }
      }
    },
    methods: {
      /**
       * 保持排序
       * @param {*} propName 
       * @param {*} order 
       */
      keepSort(propName, order) {
        if (this.dataCopy) {
          if (order === 'asc') {
            this.dataCopy.sort((a, b) => a[propName] - b[propName])
          } else if (order === 'desc') {
            this.dataCopy.sort((a, b) => b[propName] - a[propName])
          }
        } else {
          return
        }
      },
      /**
       * 数据排序
       * @param {*} propName  // 数据排序时依据哪个属性值进行排序
       * @param {*} order  // 顺序['asc', 'desc']
       */
      sort(propName, order) {
        event.target.classList.add('sortbtn--highlighted')
        this.currSort.field = propName
        this.currSort.order = order
        if (order === 'asc') {
          this.dataCopy.sort((a, b) => a[propName] - b[propName])
          // 设置排序按钮css
          event.target.nextElementSibling.classList.remove('sortbtn--highlighted')
        } else if (order === 'desc') {
          this.dataCopy.sort((a, b) => b[propName] - a[propName])
          event.target.previousElementSibling.classList.remove('sortbtn--highlighted')
        }
      },
      /**
       * 显示被折叠区域（小屏幕时）
       */
      showCollapsedCollumns() {
        const $btn = event.currentTarget
        const $pullUpIcon = $btn.querySelector('.icon-pull-up')
        const $pullDownIcon = $btn.querySelector('.icon-pull-down')
        const tableClasses = $btn.parentNode.nextElementSibling.classList
        // 收回
        if (tableClasses.contains('expanded')) {
          tableClasses.remove('expanded')
          tableClasses.add('unexpanded')
          // 显示向下的icon
          $pullUpIcon.classList.add('d-none')
          $pullDownIcon.classList.remove('d-none')
        } else {
          // 展开
          tableClasses.add('expanded')
          tableClasses.remove('unexpanded')
          $pullDownIcon.classList.add('d-none')
          $pullUpIcon.classList.remove('d-none')
        }
      },
    }
  }
</script>
<style lang="sass" scoped>
table, td, th
  font-size: 1.4rem
  border: 1px solid #E9EBEC
  padding: 1rem
td
  vertical-align: middle
  word-break: break-all

.table
  width: 100%
  table-layout: fixed
  border-collapse: collapse
  &-header
    // 使表头永远完整显示
    word-break: keep-all
    padding: 1.7rem
    font-weight: bold
    color: #8F9CB0
    text-align: left
    background: #F9FAFD
    .sortable-col
      display: flex
    .sort-wrapper
      user-select: none
      margin: auto
      vertical-align: center
      width: min-content
      height: min-content
      display: inline-block
      margin-left: 1.5rem
      display: flex
      flex-direction: column
      .sortbtn
        cursor: pointer
        font-size: .5rem
        height: min-content
        &--highlighted
          color: #409EFF
  &-emptyplaceholder
    &__nodatatxt
      color: #B7B7B7
  &-body
    &-colorshape
      display: inline-block
      height: 1rem
      width: 1rem
      border-radius: 50%
      margin-right: 1rem
  &-collapsebtn
    font-size: 2rem
    color: #8F9DB0
    &:hover
      color: #5D5D5D
  &-uncollapsedcontainer
    padding: .7rem
  &-collapsedcontainer
    width: 100%
  &-collapsedcontent
    width: 100%

.no-border-left
  border-left: hidden
.first-col
  display: flex
  justify-content: space-between
.unexpanded
  display: none
.expanded
  display: table
.min-width-col
  width: 18rem

</style>
