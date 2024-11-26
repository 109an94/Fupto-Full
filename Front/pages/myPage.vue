<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/myLayout.css" }],
});

const userDetails = useUserDetails()
const member = ref({
    id : userDetails.id.value,
    email : userDetails.email.value,
    username : userDetails.username.value
});
import { onMounted } from 'vue'
import { useAlerts } from '~/composables/useAlerts'
import { useSSE } from '~/composables/useSSE'

const { fetchUnreadAlerts } = useAlerts()
const { connectSSE } = useSSE()

onMounted(() => {
  fetchUnreadAlerts()
  connectSSE()
})

</script>

<template>
  <main class="mainLayout">
    <aside class="profileContainer">
<!--      <h1>프로필 관리</h1>-->
      <div class="profile">
        <div>
          <img src="" alt="">
        </div>
        <ul class="info">
          <li class="info-name">{{ member.username }}</li>
          <li class="info-email">{{ member.email }}</li>
          <li><nuxt-link to="/myPage/setting/account"><button>프로필관리</button></nuxt-link></li>
        </ul>
<!--        <div class="icon">-->
<!--          <span>알림</span>-->
<!--        </div>-->
      </div>
      <nav class="profileNav">
        <ul>
          <li>
            <nuxt-link to="/myPage/board">
              <span><img src="/imgs/icon/board.svg"></span>
              <span>게시판</span>
            </nuxt-link>
          </li>
          <li>
            <nuxt-link to="/myPage/alert">
              <span><img src="/imgs/icon/alerts.svg"></span>
              <span>알람</span>
            </nuxt-link>
          </li>
          <li>
            <nuxt-link to="/myPage/favorite">
              <span><img src="/imgs/icon/favorite.svg"></span>
              <span>찜</span>
            </nuxt-link>
          </li>
        </ul>
      </nav>
    </aside>
    <nuxt-page/>
  </main>

</template>
