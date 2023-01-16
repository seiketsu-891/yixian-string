<template lang="html">
  <!-- 分类饼图 -->
  <div>
    <div class="chart-placeholder" v-show="noData && !isLoading">暂无数据</div>
    <apexchart height="200" type="pie" :style="{visibility: noData || isLoading? 'hidden': 'visible'}" :options="chartOptions" :series="series"></apexchart>
  </div>
</template>

<script>
  import VueApexCharts from "vue3-apexcharts"
  export default {
    components: {
      apexchart: VueApexCharts,
    },
    props: {
      data: Array,
      default: () => []
    },
    watch: {
      data(val) {
        if (val.length === 0) {
          this.noData = true
          this.isLoading = false
          return
        }
        this.noData = false
        this.isLoading = true
        this.updateChartOptions(val)
      }
    },
    methods: {
      /**
       * 判断两个数组是否相同，主要是用于检查数据是否有变动
       * @param {*} arr1 
       * @param {*} arr2 
       */
      arrayEqual(arr1, arr2) {
        if (arr1 && arr2) {
          return arr1.toString() === arr2.toString()
        } else {
          return false
        }
      },
      /**
       * 更新表格
       * @param {Arrray} val 
       */
      updateChartOptions(val) {
        const catDescs = []
        const hours = []
        const catColors = []
        let totalHours = 0
        val.forEach((item) => {
          const desc = item.catDesc.length < 10 ? item.catDesc : item.catDesc.substring(0, 6) + '...'
          catDescs.push(desc)
          totalHours += item.hours
          hours.push(item.hours)
          catColors.push(item.color)
        });
        hours.forEach((item, index) => {
          hours[index] = (item / totalHours).toFixed(2) * 100
        });
        // 避免当数据未变化时图表仍进行更新而闪动
        if (this.arrayEqual(this.chartOptions.colors, catColors) && this.arrayEqual(this.chartOptions.labels, catDescs) && this.arrayEqual(this.series, hours)) {
          console.log('notchange')
          this.isLoading = false
          return
        }
        this.series = hours
        const newOptions = {
          labels: catDescs,
          colors: catColors
        }
        this.chartOptions = {
          ...this.chartOptions,
          ...newOptions,
        }
        this.isLoading = false
      }
    },
    data() {
      return {
        isLoading: false,
        noData: false,
        series: [],
        chartOptions: {
          colors: [],
          labels: [],
          theme: {
            monochrome: {
              enabled: false
            }
          },
          plotOptions: {
            pie: {
              dataLabels: {
                offset: -5,
              },
              expandOnClick: false,
            }
          },
          dataLabels: {
            formatter(val, opts) {
              if (isNaN(opts.w.globals.radialSize)) {
                opts.w.globals.radialSize = 0
              }
              const name = opts.w.globals.labels[+opts.seriesIndex]
              return [name, val.toFixed(1) + '%']
            }
          },
          legend: {
            show: true,
            position: 'right',
          }
        },
      }
    },
  }
</script>

<style lang="sass" scoped>
.chart-placeholder
  height: 100%
  width: 100%
  display: flex
  justify-content: center
  align-items: center
  color: #8F9FB0
</style>
