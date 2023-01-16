<template lang="html">
  <!-- 标签ui（目前用于todo显示“紧急/重要”等） -->
  <div class="tag tag__wrapper" :class="sizeClass" :style="style">
    <div class="tag__content">
      <slot></slot>
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      background: {
        type: String,
        default: '#fff'
      },
      textColor: {
        type: String,
        default: '#000'
      },
      bordered: {
        type: Boolean,
        default: false
      },
      size: {
        type: String,
        default: 'normal'
      }
    },
    computed: {
      sizeClass() {
        if (this.size === 'large') {
          return 'tag--large'
        } else if (this.size === 'small') {
          return 'tag--small'
        } else {
          return 'tag--normal'
        }
      },
      style() {
        let styles = []
        styles.push({
          background: `${this.background}`
        })
        styles.push({
          color: `${this.textColor}`
        })
        if (this.bordered) {
          styles.push({
            border: `1px solid ${this.textColor}`
          })
        }
        return styles
      }
    }
  }
</script>

<style lang="sass" scoped>
.tag
  width: max-content
  &--normal
    border-radius: 2rem
    padding: 3px 1.7rem
    font-size: 1.2rem

</style>
