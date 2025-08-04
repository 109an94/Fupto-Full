// export default defineNuxtPlugin((nuxtApp) => {
//     const emitterState = reactive({
//         eventSource: null,
//         connected: false
//     })
//
//     // 전역 상태로 관리하기 위한 provide/inject 설정
//     nuxtApp.provide('emitterState', emitterState)
//
//     // 인증 상태 확인 후 SSE 연결 설정
//     nuxtApp.hook('app:mounted', () => {
//         const { eventSource } = useEmitter()
//         emitterState.eventSource = eventSource
//     })
// })