<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/myAlert.css" }],
});
const alerts = ref([
  {
    title: '[가격 알림] 해당 상품이 ${alertPrice} 보다 낮아졌습니다..',
    sender: 'fupto관리자',
    isRead: 0
  },
  {
    title: '[가격 알림] 해당 상품이 더 저렴해졌습니다..',
    sender: 'fupto관리자',
    isRead: 0
  }
])

const filteredAlerts = computed(() => {
  return alerts.value.filter(alert => alert.isRead === 0)
})

const markAsRead = async (alertId) => {
  try {
    // API 호출 예시
    // const response = await fetch(`/api/alerts/${alertId}`, {
    //   method: 'PUT',
    //   body: JSON.stringify({ isRead: 1 })
    // })

    // 성공 시 프론트엔드 상태 업데이트
    const alert = alerts.value.find(a => a.id === alertId)
    if (alert) {
      alert.isRead = 1
    }
  } catch (error) {
    console.error('알림 상태 업데이트 실패:', error)
  }
}
</script>

<template>
  <div class="alert-container">
    <div class="alert" v-for="(alert, index) in filteredAlerts" :key="index">
      <div class="alert-profile">
       <div class="profile-image"></div>
        <div class="alert-content">
          <div class="alert-title">{{ alert.title }}</div>
          <div class="alert-info">발급 전 : {{ alert.sender }}</div>
        </div>
        <button class="close-btn" @click="markAsRead(alert.id)"><img src="/public/imgs/icon/cancel.svg"></button>
      </div>
    </div>
  </div>
</template>

