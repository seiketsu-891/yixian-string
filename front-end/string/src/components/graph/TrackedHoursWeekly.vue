<template lang="html">
  <!-- 一周时间追踪图表 -->
  <div>
    <apexchart type="area" :height="300" class="chart" :options="chartOptions" :series="series"></apexchart>
  </div>
</template>

<script>
  import VueApexCharts from "vue3-apexcharts"
  export default {
    components: {
      apexchart: VueApexCharts,
    },
    props: {
      data: Object,
      currCat: { // 当前要显示的时间条目分类，没有传则显示全部
        type: String,
        required: false
      }
    },
    methods: {
      updateCategories(cats) {
        const newOption = {
          xaxis: {
            categories: cats,
            axisBorder: {
              show: false
            },
            axisTicks: {
              show: false
            },
            labels: {
              style: {
                colors: '#8E979D'
              }
            }
          }
        }
        this.chartOptions = {
          ...this.chartOptions,
          ...newOption,
        }
      },
    },
    watch: {
      // 当传入的数据更新时更新xy轴
      data: {
        immediate: true,
        handler(val) {
          // 更新x轴数据
          this.updateCategories(val.dates)
          this.series = [{
            name: '当日追踪',
            data: val.dailySum
          }]
          // 更新y轴数据
          let max = Math.ceil(Math.max(...this.series[0].data))
          if (max < 4) {
            max = 4
          }
          const newOption = {
            yaxis: {
              showAlways: false,
              showForNullSeries: false,
              max: max,
              seriesName: '',
              tickAmount: 4,
              floating: false,
              axisBorder: {
                show: false
              },
              axisTicks: {
                show: false
              },
              labels: {
                formatter: function(val) {
                  return val.toFixed(0)
                }
              }
            },
          }
          this.chartOptions = {
            ...this.chartOptions,
            ...newOption,
          }
        }
      },
    },
    data() {
      return {
        series: [{
          name: '当前分类',
          data: [0, 0, 0, 0, 0, 0, 0], // default value
        }, ],
        chartOptions: {
          colors: ['#F9C778'],
          dataLabels: { // 不直接显示数据
            enabled: false
          },
          stroke: {
            width: 2.5
          },
          grid: {
            borderColor: "#fff", // 隐藏平行于x轴的线
          },
          xaxis: {
            axisBorder: {
              show: false
            },
            axisTicks: {
              show: false
            },
            categories: ['01/01', '01/02', '01/03', '01/04', '01/05', '01/06', '01/07'],
            labels: {
              style: {
                colors: '#8E979D'
              }
            }
          },
          fill: {
            type: 'gradient',
            gradient: {
              gradientToColors: ['#fff'] // 从上面的colors渐变到这里
            },
          },
          tooltip: {
            enabled: true,
            // theme: 'dark',
            marker: {
              show: false
            },
            x: {
              show: false
            },
            y: {
              formatter: function(val) {
                return val.toFixed(2) + '小时'
              }
            }
          },
          yaxis: {
            showAlways: false,
            showForNullSeries: false,
            max: 4,
            seriesName: '',
            tickAmount: 4,
            floating: false,
            axisBorder: {
              show: false
            },
            axisTicks: {
              show: false
            },
            labels: {
              formatter: function(val) {
                return val.toFixed(0)
              }
            }
          },
          chart: {
            toolbar: {
              show: false,
              tools: {
                download: false,
                selection: false,
                zoom: false,
                zoomin: false,
                zoomout: false,
                pan: false,
              }
            },
          },
        },
      }
    }
  }
</script>

<style lang="sass" scoped>


</style>
