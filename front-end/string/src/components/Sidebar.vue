<template lang="html">
  <!-- 登录后的sidebar -->
  <nav class=" navbar navbar__container" :style="{ width: width }">
    <!-- logo -->
    <div class="navbar__logocontainer">
      <slot name="logo"></slot>
    </div>
    <!-- 菜单 -->
    <ul class="navbar__menu">
      <li v-for="item in menuList" :key="item.id" class="navbar__menuitem">
        <router-link :to="item.href" tag="a" class="navbar__menuitem-link">
          <span class="iconfont icon navbar__menuitem-icon" :class="['icon-' + item.iconName]">{{ item.icon }}</span> <span
            class="navbar__menuitem-name">{{ item.menuName }}</span></router-link>
      </li>
    </ul>
    <!-- footer -->
    <div class="navbar__footer">
    </div>
  </nav>
</template>

<script>
  export default {
    props: {
      menuList: { // {icon, iconName, menuName, href}
        type: Array,
        default: () => []
      },
      width: {
        type: String,
        default: '100%'
      },
      mobilemode: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        fullSidebar: true
      }
    }
  }
</script>

<style lang="sass" scoped>
@import '@/assets/sass/components/sidebar.sass'

</style>
