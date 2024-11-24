// export const useEmitter = () => {
//     // useState를 사용하여 전역 상태 관리
//     const alerts = useState('alerts', () => [])
//     const emitterState = useState('emitterState', () => ({
//         connected: false,
//         eventSource: null
//     }))
//
//     const initializeSSE = async () => {
//         // userDetails와 동일하게 로그인 상태 확인
//         const userDetails = useUserDetails()
//         await userDetails.loadUserFromStorage()
//
//         if (process.client && !emitterState.value.connected && !userDetails.isAnonymous()) {
//             const eventSource = new EventSource('/user/member/subscribe', {
//                 withCredentials: true
//             })
//
//             eventSource.addEventListener('alert', (event) => {
//                 const alertData = JSON.parse(event.data)
//                 alerts.value = [...alerts.value, alertData]
//             })
//
//             emitterState.value = {
//                 connected: true,
//                 eventSource
//             }
//         }
//     }
//
//     const closeSSE = () => {
//         if (emitterState.value.eventSource) {
//             emitterState.value.eventSource.close()
//             emitterState.value.connected = false
//         }
//     }
//
//     return {
//         alerts,
//         emitterState,
//         initializeSSE,
//         closeSSE
//     }
// }