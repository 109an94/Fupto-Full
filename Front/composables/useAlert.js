// composables/useAlerts.js
import { ref } from 'vue'

export const useAlerts = () => {
    const alerts = ref([])

    // 기존 알림 데이터를 가져오는 함수
    const fetchUnreadAlerts = async () => {
        try {
            const { data } = await useFetch('/api/unread-alerts')
            alerts.value = data.value
        } catch (error) {
            console.error('Failed to fetch unread alerts:', error)
        }
    }

    // SSE 연결을 설정하는 함수
    const connectSSE = () => {
        const eventSource = new EventSource('/api/subscribe', { withCredentials: true })

        eventSource.onmessage = (event) => {
            const newAlert = JSON.parse(event.data)
            alerts.value.push(newAlert)
        }

        eventSource.onerror = (error) => {
            console.error('SSE error:', error)
            eventSource.close()
        }
    }

    return {
        alerts,
        fetchUnreadAlerts,
        connectSSE
    }
}