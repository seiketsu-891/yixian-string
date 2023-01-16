<template lang="html">
  <!-- 管理时间条目分类页面 -->
  <div class="container-fluid main">
    <card rounded>
      <div>
        <button class="btn btn--default mb-5" @click="$router.push('/timereport')" style="display: block;">BACK</button>
        <button class="btn btn--submit mb-5" v-show="!catFormVisible" @click="showCatForm">添加分类</button>
        <!-- 添加或编辑分类的表单 -->
        <div class="cats-form" v-show="catFormVisible">
          <el-form :model="catForm" ref="timeEntryCatForm" :rules="catFormRules" class="row catform">
            <el-form-item class="col-12 col-sm-4" prop="name">
              <el-input ref="catNameInput" v-model="catForm.name" placeholder="分类名称"></el-input>
            </el-form-item>
            <el-form-item class="col-12 col-sm-3">
              <el-color-picker v-model="catForm.color" size="mini"></el-color-picker>
            </el-form-item>
            <div class="catform-btns col-5">
              <button type="submit" class="btn btn--default" :disabled="catFormSubmitDisabled" @click.prevent="submitCatForm">保存</button>
              <button class="btn btn--default ml-3" @click.prevent="closeCatForm">取消</button>
            </div>
          </el-form>
        </div>
      </div>
      <!-- 分类信息表格区域 -->
      <responsive-table :loading="entryCatIsLoading" @edit="showCatForm($event)" @del="openDelCat($event)" :data="timeEntryCats" :columns="timeEntryCatsColumns"> </responsive-table>
    </card>
  </div>
  <!-- 删除分类的modal -->
  <teleport to="#app">
    <modal ref="MDelCat" :submitBtnDisabled="delCatBtnDisabled" max-width="35rem" confirmbox @confirm="delCat"></modal>
  </teleport>
</template>

<script>
  import TimeEntryCategoryService from '@/apis/TimeEntryCategoryService'
  import TimeEntryService from '@/apis/TimeEntryService'
  import ResponsiveTable from '@/components/ResponsiveTable'
  import Card from '@/components/Card'
  export default {
    components: {
      Card,
      ResponsiveTable
    },
    computed: {
      user() {
        return this.$store.getters.user
      }
    },
    data() {
      return {
        catFormSubmitDisabled: false,
        delCatBtnDisabled: false,
        entryCatIsLoading: true,
        timeEntryCats: [],
        timeEntryCatsColumns: [{
            label: '分类名称',
            fieldName: 'name',
            withColor: true
          },
          {
            label: '已追踪小时数',
            fieldName: 'duration',
            numericalSort: true,
            minW: true
          }
        ],
        catFormVisible: false,
        catForm: {
          id: '',
          name: '',
          color: ''
        },
        catFormRules: {
          name: [{
            validator: this.$validator.checkNotEmpty,
            trigger: 'submit'
          }]
        },
      }
    },
    mounted() {
      this.getTimeEntryCatList()
    },
    methods: {
      /**
       * 打开删除分类的modal
       */
      openDelCat(id) {
        this.$refs['MDelCat'].open({
          id
        })
      },
      /**
       * 隐藏编辑分类的表单
       */
      closeCatForm() {
        this.$refs['timeEntryCatForm'].clearValidate()
        this.catFormVisible = false
      },
      /**
       * 显示编辑分类的表单
       * @param {*} item 
       */
      showCatForm(item) {
        if (item) {
          this.catForm.id = item.id
          this.catForm.name = item.name
          this.catForm.color = item.color
        } else {
          this.$refs['timeEntryCatForm'].resetFields()
        }
        this.catFormVisible = true;
        this.$refs.catNameInput.focus()
      },
      /**
       * 提交分类编辑表单
       */
      submitCatForm() {
        this.catFormSubmitDisabled = true
        this.$refs['timeEntryCatForm'].validate(valid => {
          if (this.catForm.name.trim() === '默认分类') {
            this.$toast.error('添加失败！已存在同名分类')
            this.catFormSubmitDisabled = false
            return
          }
          if (valid) {
            const cat = {
              name: this.catForm.name.trim(),
              color: this.catForm.color ? this.catForm.color : this.$const.DEFAULT_CAT_COLOR
            }
            if (!this.catForm.id) {
              this.addCat(cat)
            } else {
              cat.id = this.catForm.id
              this.editCat(cat)
            }
          } else {
            this.catFormSubmitDisabled = false
            return false
          }
        })
      },
      /**
       * 发送编辑分类请求
       * @param {*} cat 
       */
      async editCat(cat) {
        const res = await TimeEntryCategoryService.update(this.user.id, cat)
        if (res.success) {
          this.$toast.show('修改成功')
          this.closeCatForm()
          this.getTimeEntryCatList()
        } else {
          this.$toast.error('修改失败')
        }
        this.catFormSubmitDisabled = false
      },
      /**
       * 发送删除分类请求
       * @param {*} args 
       */
      async delCat(args) {
        this.delCatBtnDisabled = true
        const res = await TimeEntryCategoryService.del(this.user.id, args.id)
        if (res.success) {
          this.$refs['MDelCat'].close()
          this.$toast.show('删除成功')
          this.getTimeEntryCatList()
        } else {
          this.$toast.error('删除失败')
        }
        this.delCatBtnDisabled = false
      },
      /**
       * 发送增加分类请求
       * @param {*} cat 
       */
      async addCat(cat) {
        const res = await TimeEntryCategoryService.add(this.user.id, cat)
        if (res.success) {
          this.$toast.show('添加成功')
          this.closeCatForm()
          this.getTimeEntryCatList()
        } else {
          this.$toast.error('添加失败!' + res.message)
        }
        this.catFormSubmitDisabled = false
      },
      /**
       * 发送获取所以分类条目相关信息请求
       */
      async getTimeEntryCatList() {
        this.entryCatIsLoading = true
        // （1）从cat表中获取cat列表，（2）从entry表中获取各个cat的duration，（3）将duration添加到cat中
        const res_getDurations = await TimeEntryService.listdur(this.user.id)
        const res_getCatList = await TimeEntryCategoryService.list(this.user.id)
        if (res_getDurations.success && res_getCatList.success) {
          this.entryCatIsLoading = false
          const durs = res_getDurations.content
          const cats = res_getCatList.content
          // 如果没读取出任何累计时间信息，则直接把每个分类的时间设置为0即可。
          if (durs.length == 0) {
            console.log("000")
            cats.forEach((c) => {
              c.duration = 0
            })
            this.timeEntryCats = cats
            // 表格中第一项没有编辑选项
            this.timeEntryCats[0].id = 0
            return
          }
          // 先把所有cat的默认值都设为0
          // 对于每一项duration数据，把的累计时间添加到category list对应数据中
          cats.forEach(c => {
            c.duration = 0
          });
          durs.forEach((item) => {
            const currDurCatId = item.categoryId
            const currDuration = item.duration
            const cat = cats.find(c => c.id == currDurCatId)
            if (cat) {
              cat.duration += currDuration
            } else {
              // 如果对应的cat id在cat list中不存在（例如已被删除），那么把它的时间加入到默认分类中。
              cats[0].duration += currDuration
            }
          })
          this.timeEntryCats = cats
          this.timeEntryCats[0].id = 0
          cats.forEach((item) => {
            // 把累计时间的毫秒数值转化成小时形式
            item.duration = (item.duration / 1000 / 60 / 60).toFixed(2)
          });
        } else {
          this.$toast.show('"获取分类失败')
        }
      },
    }
  }
</script>

<style lang="css" scoped>

</style>
