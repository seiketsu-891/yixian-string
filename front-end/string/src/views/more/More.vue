<template lang="html">
  <!-- ui资源介绍和帮助界面的layout -->
  <card class="content-container" shadowed rounded>
    <div class="content">
      <router-view/>
    </div>
  </card>
</template>

<script>
  import Card from '@/components/Card'

  export default {
    components: {
      Card,
    },
    data() {
      return {
        nav: [{
            id: 1,
            name: '首页',
            href: '/'
          },
          {
            id: 2,
            name: '素材说明',
            href: '/ui-resources'
          }
        ]
      }
    },
    mounted() {
    }
  }
</script>

<style lang="sass" scoped>
.content-container
  word-break: break-all
  margin: 4rem auto auto
  width: 90vw
  max-width: 100rem
  min-height: 20rem
  margin-bottom: 2rem
.content
  padding: .8rem
  // margin-top: 2rem
  line-height: 1.8

</style>
