<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/myAlert.css" }],
});

const alerts =ref([])

const fetchAlert = async ()=> {
  try {
    const response = await use$Fetch('/user/member/unreadAlerts');
    if (!response) {
      throw new Error('데이터 조회 실패');
    }
    console.log(response)
    alerts.value = response;
    console.log(alerts.value);
  } catch (error) {
    console.error("데이터 조회 오류:", error);
  }
}
// const filteredAlerts = computed(() => {
//   return alerts.value.filter(alert => alert.isRead === 0)
// })

const markAsRead = async (alertId) => {
  try {
    const response = await use$Fetch(`/user/member/alerts/${alertId}/read`, {
      method: 'PATCH'
      // body: JSON.stringify({ isRead: 1 })
    })
    console.log(response)
  } catch (error) {
    console.error('알림 상태 업데이트 실패:', error)
  }
}
onMounted(()=>{
  fetchAlert()
  markAsRead()
})
</script>

<template>
  <div class="alert-container">
    <div class="alert" v-for="(alert, index) in alerts" :key="index">
      <div class="alert-profile">
       <div class="profile-image"></div>
        <div class="alert-content">
          <div class="alert-title">{{ alert.message }}</div>
          <div class="alert-info">Fupto : {{ alert.createDate }}</div>
        </div>
        <button class="close-btn" @click="markAsRead(alert.id)"><img src="/public/imgs/icon/cancel.svg"></button>
      </div>
    </div>
  </div>
</template>

