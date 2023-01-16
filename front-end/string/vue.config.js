const CompressionPlugin = require('compression-webpack-plugin')
const TerserPlugin = require('terser-webpack-plugin')

module.exports = {
    chainWebpack: config => {
        config.plugins.delete('preload')
        config.plugins.delete('prefetch')
        config.plugin('html')
            .tap(args => {
                args[0].title = '易弦(String)'
                return args
            })
    },
    transpileDependencies: ['vuex-persist'],
    css: {
        loaderOptions: {
            sass: {
                additionalData: `
               @import "~@/assets/sass/abstract/_variables.sass"
               @import "~@/assets/sass/abstract/_mixins.sass"
               `
            },
        }
    },
    configureWebpack: () => {
       if(process.env.NODE_ENV === 'production'){
         return {
            externals: {
              wowjs: 'WOW',
             'cos-js-sdk-v5': 'COS',
              apexcharts: 'ApexCharts',
             },
           plugins: [
             new CompressionPlugin({
                test: /\.js$|\.html$|\.css$/, // 匹配文件名
                threshold: 10240, // 对超过10k的数据压缩
                deleteOriginalAssets: false // 是否删除未压缩的源文件，谨慎设置，如果希望提供非gzip的资源，可不设置或者设置为false（比如删除打包后的gz后还可以加载到原始资源文件）
             })
           ],
           optimization: {
             minimizer: [
                 new TerserPlugin({
                   // 使用多进程并发运行以提高构建速度（webpack是单线程，开启多线程压缩速度更快）
                   parallel: 4,
                   // 是否将注释剥离到单独的文件中（默认为true）: 去除js打包后的LICENSE.txt文件(里面是注释)
                   extractComments: false,
                   terserOptions: {
                      // 去除打包的console.log
                      compress: {
                      warnings: false,
                      drop_console: true,
                      drop_debugger: true,
                      pure_funcs: ['console.log']
                   },
                   // 去除注释
                  output: {
                       comments: false
                  }
          }
        })
             ]
           }
         }
       }else{
         return {
           externals: {
             wowjs: 'WOW',
            'cos-js-sdk-v5': 'COS',
             apexcharts: 'ApexCharts',
            },
         }
       }
    },
    devServer: {
        proxy: 'http://localhost:8081'
    },
    // 生产环境下关闭SourceMap，不然很容易被看到源码
    productionSourceMap: false,
}
