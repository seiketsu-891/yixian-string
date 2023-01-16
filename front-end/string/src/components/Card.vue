<template lang="html">
  <!-- 卡片效果 -->
  <div class="card" :class="[rounded? 'card--rounded': '', shadowed? 'card--shadowed': '']">
    <slot></slot>
  </div>
</template>

<script>
export default {
  props: {
    shadowed: Boolean, // 是否有阴影
    rounded: Boolean,  // 是否是圆角
  }
}
</script>

<style lang="sass" scoped>
.card
  padding: 1rem
  background: white
  overflow: hidden
  @media(min-width: $bp-sm)
    padding: 2rem
.card--shadowed
  box-shadow: 1px 3px 5px #DFE8ED
.card--rounded
  border-radius: 1.5rem

</style>
