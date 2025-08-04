// plugins/error-handler.js
export default defineNuxtPlugin((nuxtApp) => {
    nuxtApp.vueApp.config.errorHandler = (error, instance, info) => {
        console.error('Vue Error:', error, instance, info);
    }

    nuxtApp.hook('vue:error', (error, instance, info) => {
        console.error('Vue Error:', error, instance, info);
    })

    nuxtApp.hook('app:error', (error) => {
        console.error('App Error:', error);
    })
})