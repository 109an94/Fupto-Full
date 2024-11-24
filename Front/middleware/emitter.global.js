// export default defineNuxtRouteMiddleware(async () => {
//     if (process.client) {
//         const { initializeSSE } = useEmitter()
//         const userDetails = useUserDetails()
//
//         // 사용자 정보 로드 후 SSE 초기화
//         await userDetails.loadUserFromStorage()
//         if (!userDetails.isAnonymous()) {
//             await initializeSSE()
//         }
//     }
// })