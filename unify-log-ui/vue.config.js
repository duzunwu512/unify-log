module.exports = {
    publicPath: './',
    assetsDir: 'static',
    productionSourceMap: false,
     devServer: {
        publicPath: '/',
         proxy: {
             '/api':{
                 target:'http://localhost:8080/log',
                 changeOrigin:true,
                 pathRewrite: {
                    '/api': ''
                  }
             }
         }
     }
}