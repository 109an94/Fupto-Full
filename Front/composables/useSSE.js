// composables/useSSE.js
import { onUnmounted } from 'vue'
import { useAlerts } from '~/composables/useAlerts'

export const useSSE = () => {
    const { addAlert } = useAlerts()
    let eventSource = null
    const baseUrl = 'http://localhost:8085/api/v1'
    const userDetails = useUserDetails()


    const connectSSE = () => {
        if (eventSource) return
        const token = userDetails.token.value
        const url = `${baseUrl}/user/member/subscribe?token=${token}`

        eventSource = new EventSource(url, {
            withCredentials: true
        })
        console.log(eventSource)
        eventSource.onopen = () => {
            console.log('SSE connection established')
        }

        eventSource.onmessage = async (event) => {
            // 받은 데이터 로깅
            console.log('Received SSE event:', {
                name: event.name,
                data: event.data,
                type: event.type,
                lastEventId: event.lastEventId
            })

            try {
                const newAlert = JSON.parse(event.data)
                addAlert(newAlert)
                await fetchUnreadAlerts();
            } catch (error) {
                console.error('Error parsing SSE message:', error)
            }
        }

            // 특정 이벤트 이름으로 전송된 데이터 확인
            eventSource.addEventListener('connect', (event) => {
                console.log('Received connect event:', event.data)
            })

        eventSource.onerror = (error) => {
            console.error('SSE error:', error)
            eventSource?.close()
            eventSource = null

            // // 연결 실패 시 재시도 로직
            // setTimeout(() => {
            //     console.log('Attempting to reconnect SSE...')
            //     connectSSE()
            // }, 5000)
        }
    }

    const disconnectSSE = () => {
        if (eventSource) {
            eventSource.close()
            eventSource = null
            console.log('SSE connection closed')
        }
    }

    onUnmounted(() => {
        disconnectSSE()
    })

    return {
        connectSSE,
        disconnectSSE
    }
}