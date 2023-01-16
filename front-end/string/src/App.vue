<template>
  <div id="app">
  <router-view ></router-view>
  </div>
</template>

<script>

export default {
  name: 'App',
}
</script>

<style lang="sass" >
#app
  // 避免最下端空白
  margin-bottom: -3rem
</style>
