<template lang="html">
  <!-- 面包屑导航 -->
  <div class=" breadcrumb__container breadcrumb">
    <ul class="breadcrumb__list">
      <li class="breadcrumb__item"   v-for="(item,index) in data" :key="item.id">
        <router-link class="breadcrumb__item-link"  :to="item.href">
        {{item.name}}
      </router-link><span class="breadcrumb__item-divider" v-if="index!==data.length-1">/</span>
    </li>
    </ul>
  </div>
</template>

<script>
export default {
  props: {
    data: Array // name , href, id
  }
}
</script>

<style lang="sass" scoped>
.breadcrumb
  user-select: none
  &__item
    display: inline-block
    width: max-content
    &-divider
      margin: 0 1rem
      color: #69767E
    &:link, &:visited, &:hover
      color: #69767E
  &__list
    background-color: #F7F7F7
    width: max-content
    padding: 8px 20px
    border-radius: 14px
    color: #69767E
</style>
