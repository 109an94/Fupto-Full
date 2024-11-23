// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: "2024-04-03",
  devtools: { enabled: true },
  runtimeConfig: {
    public: {
      apiBase: "http://localhost:8085/api/v1",
      // apiBase: "http://hi.newlecture.com:8085/api/v1",
    },
  },
  // nitro:{
  //   devProxy:{
  //     '/api/v1':{
  //       target:'http://localhost:8080/api/v1',
  //       changeOrigin:true,
  //       prependPath:true
  //     }
  //   }
  // }
  app:{

  }
});
