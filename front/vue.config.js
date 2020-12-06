module.exports = {
  devServer: {
    proxy: {
      "/kd": {
        target: "http://www.kuaidi100.com",
        ws: true,
        changeOrigin: true,
        secure: false,
        pathRewrite: {
          "^/kd": ""
        }
      },
      "/fakeapi": {
        target: "https://yesno.wtf",
        ws: true,
        changeOrigin: true,
        secure: false,
        pathRewrite: {
          "^/fakeapi": ""
        }
      }
    }
  }
};
