<template lang="html">
  <!-- 通知铃铛及其下拉菜单 -->
  <div class="badge__container">
    <div class="badge" @click="toggleNotiDropdown" :style="{width: width, height: height, 'line-height': height}">
      <span class="badge__bell iconfont icon icon-bell" :style="{ 'font-size': iconfontsize }">&#xe654; <span
          class="badge__point" v-show="hasUnreadNotification">
        </span></span>
    </div>
    <dropdown ref="notiDropdown" class="badge__dropdown" type="basic">
      <slot>
        <div class="msg__list">
          <div class="list__title">通知</div>
          <span v-if="isLoading" class="list__emptymsg">通知读取中</span>
          <!-- 通知读取比较快，用loading反而会有一闪而过的颜色 -->
          <span v-if="notificationList.length === 0 && !isLoading" class="list__emptymsg">当前暂无任何通知</span>
          <ul v-if="!isLoading && notificationList.length > 0" class="listgroup">
            <li v-for="(item, index) in notificationList" :key="item.id" class="listgroup__item">
              <div class="listgroup__item--title listgroup__item--info"> <span> <span
                    class="listgroup__item--title-prefix" v-if="!item.status">NEW</span><span
                    class="listgroup__item--title-content"
                    @click="showNotification(index, item.id)">{{ item.title }}</span> </span>
                <!-- 删除单条通知按钮 -->
                <button class="listgroup__item--title-suffix iconfont btn icon icon-delete"
                  :disabled="delNotiBtnDisabled" @click="removeNotification(item.id)">&#xe676;</button>
              </div>
              <span class="listgroup__item--content listgroup__item--info">{{ item.content }} </span> <span
                class="divider divider--hr"></span>
            </li>
          </ul>
          <div v-if="notificationList.length > 0" class="list__emptybtn"><button class="btn"
              @click="handleDelAllClicked">清空所有通知</button>
          </div>
        </div>
      </slot>
    </dropdown>
    <!-- 显示通知信息详细内容的模态框 -->
    <teleport to="body">
      <modal :title="currNotification.title" ref="MShowNotification" maxWidth="50rem" @close="handleModalClosed">
        <template v-slot:body>
          <div class="notiModal">
            <div class="notiModal__content">
              {{ currNotification.content }}
            </div>
          </div>
        </template>

      </modal>
    </teleport>
    <!-- 确认是否清楚全部通知的模态框 -->
    <teleport to="body">
      <modal ref="MDelAllMsg" class="modal" max-width="35rem" :submitBtnDisabled="delAllNotisBtnDisabled" confirmbox
        @confirm="delAllNotification" @close="handleModalClosed"></modal>
    </teleport>

  </div>
</template>

<script>
import NotificationService from '@/apis/NotificationService.js'
import Dropdown from '@/components/Dropdown'
import {
  mapGetters
} from 'vuex'
import Modal from "@/components/Modal";
export default {
  components: {
    Dropdown,
    Modal,
  },
  props: {
    width: {
      type: String,
      default: '40px'
    },
    height: {
      type: String,
      default: '40px'
    },
    iconfontsize: {
      type: String,
      default: '22px'
    },
  },
  data() {
    return {
      isLoading: true,
      hasUnreadNotification: false,
      // 读取到的通知列表
      notificationList: [],
      notificationCheckTimer: 0,
      // 当前被打开的通知
      currNotification: {},
      delNotiBtnDisabled: false,
      delAllNotisBtnDisabled: false
    }
  },
  watch: {},
  computed: {
    ...mapGetters([
      'user',
    ]),
  },
  mounted() {
    /**
     * 每15分钟检查一次是否有未读通知
     */
    this.$nextTick(() => {
      this.checkNotifiations()
    })
    this.notificationCheckTimer = setInterval(this.checkNotifiations, 15 * 60 * 1000)
  },
  unmounted() {
    clearInterval(this.notificationCheckTimer)
  },
  methods: {
    /**
     * 打开和关闭通知菜单
     */
    toggleNotiDropdown() {
      if (this.$refs.notiDropdown.active) {
        this.$refs.notiDropdown.close()
      } else {
        this.$refs.notiDropdown.open()
        this.getNotifications()
      }
    },
    /**
     *  点击清空所有通知按钮后的处理
     */
    handleDelAllClicked() {
      this.disableDropdownClickAway()
      this.$refs['MDelAllMsg'].open()
    },
    /**
     *  检查是否有未读通知
     */
    async checkNotifiations() {
      const res = await NotificationService.check(this.user.id)
      if (res.success) {
        this.hasUnreadNotification = res.content
      } else {
        this.$toast.error('拉取通知内容失败')
      }
    },
    /**
     *  获取所有通知
     */
    async getNotifications() {
      this.isLoading = true
      const res = await NotificationService.list(this.user.id)
      if (res.success) {
        this.notificationList = res.content
        this.hasUnreadNotification = this.notificationList.filter(item => item.status === false).length > 0
      } else {
        this.$toast.error('拉取通知内容失败')
      }
      this.isLoading = false
    },
    /**
     * 删除某条通知
     * @param {string} 通知id
     */
    async removeNotification(id) {
      this.delNotiBtnDisabled = true
      const res = await NotificationService.del(id, this.user.id)
      if (res.success) {
        this.$toast.show('通知删除成功')
        this.getNotifications()
      } else {
        this.$toast.error('通知删除失败')
      }
      this.delNotiBtnDisabled = false
    },
    /**
     * 设置某条通知为已读
     * @param {string} id 通知id
     */
    async markRead(id) {
      const res = await NotificationService.setRead(id, this.user.id)
      if (res.success) {
        this.getNotifications()
      }
    },
    /**
     * 清空所有通知
     */
    async delAllNotification() {
      this.delAllNotisBtnDisabled = true
      const res = await NotificationService.delAll(this.user.id)
      if (res.success) {
        this.$toast.show('通知清空成功')
        this.getNotifications()
        // 清空成功后关闭modal
        this.$refs.MDelAllMsg.close()
      } else {
        this.$toast.error('通知清空失败')
      }
      this.delAllNotisBtnDisabled = false
    },
    /**
     * 点击通知标题后显示通知具体内容模态框
     * @param {number} index 点击的通知的index
     * @param {string} id 点击的通知的id
     */
    showNotification(index, id) {
      this.disableDropdownClickAway()
      this.currNotification = this.notificationList[index]
      this.$refs.MShowNotification.open()
      this.markRead(id)
    },
    /**
     *  不允许dropdown在点击别处后关闭
     */
    disableDropdownClickAway() {
      console.log('cancel')
      this.$refs.notiDropdown.clickAwayDisabled = true
    },
    /**
     *  处理modal关闭后
     */
    handleModalClosed() {
      // 允许dropdown在点击别处后关闭
      setTimeout(() => {
        this.$refs.notiDropdown.clickAwayDisabled = false
      }, 0)
    }
  }
}
</script>

<style lang="sass" scoped>
@import '@/assets/sass/components/divider.sass'
.badge__container
  position: relative
.badge
  user-select: none
  cursor: pointer
  text-align: center
  &__dropdown
    min-width: 26rem
    user-select: none
    padding: 0 !important
    overflow: hidden
    right: 3px
    z-index: 99999
    top: 5rem
    height: max-content
    position: absolute
    border-radius: 1rem
    // height: 380px
    // 随邮箱长度调整宽度
    width: min-content
    z-index: 100
  &:hover
    background: #F7F5F6
    border-radius: 5px
  &__bell
    color: #696969
    position: relative
  &__point
    display: inline-block
    width: 6px
    height: 6px
    border-radius: 50%
    background: #F27B3C
    position: absolute
    top: -3px
    right: -2px
.list
  &__title
    background: #fafafa
    padding: 1.5rem
    color: #93999F
    font-size: 1.3rem
  &__emptymsg
    background: #fff
    display: block
    height: 8rem
    line-height: 8rem
    font-size: 1.5rem
    text-align: center
  &__emptybtn
    height: 4rem
    display: flex
    justify-content: center
    align-items: center
    background: #fff
    color: #93999F
    font-size: 1.4rem
    &:hover
      color: #5D5D5D
.listgroup
  max-height: 30rem
  overflow-y: scroll
  font-size: 1.5rem
  padding: 1rem 2rem
  padding-bottom: 0
  margin: var(--margin-listitem--normal) 0
  margin-bottom: 0
  cursor: pointer
  &__item
    &:not(:last-of-type)
      margin-bottom: var(--margin-listitem--normal)
    &--info
      display: block
    &--title
      display: flex
      justify-content: space-between
      align-items: flex-center
      font-size: 1.5rem
      margin-bottom: calc(var(--margin-listitem--normal) * 0.6)
      &-prefix
        font-size: 1.2rem
        margin-right: 1rem
        color: #F47B31
      &-suffix
        align-self: flex-start5D5D5D
        color: #93999F
        font-size: 1.6rem
        &:hover
          color: #5D5D5D
      &-content
        display: inline-block
        border-bottom: 1px solid transparent
        &:hover
           border-bottom: 1px solid  #F47B31
           transition: .3s all
           -moz-transition: .3s all
           -webkit-transition: .3s all
           -o-transition: .3s all
    &--content
      margin-bottom: calc(var(--margin-listitem--normal) * 0.6)
      color: #93999F
      font-size: 1.4rem
      overflow: hidden
      text-overflow: -o-ellipsis-lastline
      text-overflow: ellipsis
      display: -webkit-box
      -webkit-line-clamp: 3
      line-clamp: 3
      -webkit-box-orient: vertical
      cursor: default
    &--link
      width: max-content
      color: #606060
      font-size: 1.3rem
      &:visited
        color:  #93999F
      &:hover
        color: #5D5D5D
.divider--hr
  background-color: var(--border-grey)
  margin-top: 1.5rem
// 通知模态框
.notiModal
  &__content
    font-size: 1.5rem

</style>
