// composables/useAlerts.js
import { ref } from 'vue'
import { useRuntimeConfig } from '#app'
import { useSSE } from '~/composables/useSSE'

export const useAlerts = () => {
    const alerts = ref([])
    // const baseUrl = 'http://localhost:8085/api/v1'

    // 기존 알림 데이터를 가져오는 함수
    const fetchUnreadAlerts = async () => {
        try {
            const { data } = await useAuthFetch(`/user/member/unreadAlerts`)
            alerts.value = data.value
        } catch (error) {
            console.error('Failed to fetch unread alerts:', error)
        }
        console.log("알림 가져오기 완료")
    }

    // 새로운 알림 추가
    const addAlert = (newAlert) => {
        alerts.value.push(newAlert)

        console.log('New alert added:', newAlert) // 새로운 알림 출력
        // fetchUnreadAlerts() // 새로운 알림이 추가될 때마다 전체 알림 목록 갱신
    }

    return {
        alerts,
        fetchUnreadAlerts,
        addAlert
    }
}