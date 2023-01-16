<template lang="html">
  <!-- 待办完成进度环形图 -->
  <div class="chart">
    <apexchart class="chart" :width="200" type="radialBar" :options="chartOptions" :series="series"></apexchart>
  </div>
</template>
<script>
  import VueApexCharts from "vue3-apexcharts"
  export default {
    components: {
      apexchart: VueApexCharts,
    },
    props: {
      percentage: {
        type: Number,
        default: 0
      }
    },
    data() {
      return {
        chartOptions: {
          labels: [''],
          fill: {
            colors: ['#FF7A50']
          },
          stroke: {
            lineCap: 'round'
          },
          plotOptions: {
            radialBar: {
              dataLabels: {
                value: {
                  show: false
                }
              },
            },
          }, // plotOptions end
        }, // chartOptions end
      }
    },
    computed: {
      series() {
        return [this.percentage]
      }
    }
  }
</script>

<style lang="sass" scoped>
.chart
  width: min-content
  height: min-content
</style>
