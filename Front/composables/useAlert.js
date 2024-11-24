// import { ref, computed } from 'vue'
//
// export const useAlert = () => {
//     // 전역 상태로 알림 데이터 관리
//     const alerts = useState('alerts', () => [])
//
//     /**
//      * SSE 연결 초기화 및 이벤트 리스너 설정
//      * @returns {void}
//      */
//     const initializeSSE = () => {
//         if (process.client) {
//             const eventSource = new EventSource('/user/member/subscribe', {
//                 withCredentials: true
//             })
//
//             // 알림 이벤트 수신 처리
//             eventSource.addEventListener('alert', (event) => {
//                 const alertData = JSON.parse(event.data)
//                 alerts.value.push(formatAlertData(alertData))
//             })
//
//             // 에러 처리
//             eventSource.onerror = (error) => {
//                 console.error('SSE 연결 에러:', error)
//                 eventSource.close()
//             }
//         }
//     }
//
//     /**
//      * 서버로부터 읽지 않은 알림 데이터 로드
//      * @returns {Promise<void>}
//      */
//     const loadUnreadAlerts = async () => {
//         try {
//             const { data } = await use$Fetch('/user/member/unreadAlerts')
//             alerts.value = data.value.map(formatAlertData)
//         } catch (error) {
//             console.error('알림 로드 실패:', error)
//         }
//     }
//
//     /**
//      * 알림 데이터 포맷 변환
//      * @param {Object} alert - 서버로부터 받은 알림 데이터
//      * @returns {Object} - 포맷팅된 알림 데이터
//      */
//     const formatAlertData = (alert) => ({
//         id: alert.id,
//         title: `[가격 알림] ${alert.message}`,
//         sender: alert.productName,
//         isRead: false,
//         createDate: alert.createDate
//     })
//
//     /**
//      * 특정 알림을 읽음 처리
//      * @param {number} alertId - 알림 ID
//      * @returns {Promise<void>}
//      */
//     const markAsRead = async (alertId) => {
//         try {
//             await useFetch(`/user/member/alerts/${alertId}/read`, {
//                 method: 'PATCH'
//             })
//             const index = alerts.value.findIndex(alert => alert.id === alertId)
//             if (index !== -1) {
//                 alerts.value[index].isRead = true
//             }
//         } catch (error) {
//             console.error('알림 읽음 처리 실패:', error)
//         }
//     }
//
//     /**
//      * 모든 알림을 읽음 처리
//      * @returns {Promise<void>}
//      */
//     const markAllAsRead = async () => {
//         try {
//             await useFetch('/user/member/readAll', {
//                 method: 'PATCH'
//             })
//             alerts.value = alerts.value.map(alert => ({
//                 ...alert,
//                 isRead: true
//             }))
//         } catch (error) {
//             console.error('전체 알림 읽음 처리 실패:', error)
//         }
//     }
//
//     // 읽지 않은 알림만 필터링하는 computed 속성
//     const unreadAlerts = computed(() => {
//         return alerts.value.filter(alert => !alert.isRead)
//     })
//
//     return {
//         alerts,
//         unreadAlerts,
//         initializeSSE,
//         loadUnreadAlerts,
//         markAsRead,
//         markAllAsRead
//     }
// }