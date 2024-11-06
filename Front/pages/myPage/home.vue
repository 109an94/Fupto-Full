<script setup>

</script>

<template>
<main class="mainLayout">
  <aside class="profileContainer">
    <h1>프로필 관리</h1>
    <div class="profile">
      <div>
        <img src="" alt="">
      </div>
      <ul class="info">
        <li class="info-name">닉네임</li>
        <li class="info-email">****@naver.com</li>
        <li><button>프로필관리</button></li>
      </ul>
      <div class="icon">
        <span>알림</span>
      </div>
    </div>
    <nav class="profileNav">
      <ul>
        <li>
          <router-link to="/my/board">
            <span>img</span>
            <span>게시판</span>
          </router-link>
        </li>
        <li>
          <router-link to="/my/event">
            <span>img</span>
            <span>이벤트</span>
          </router-link>
        </li>
        <li>
          <router-link to="/my/favorite">
            <span>img</span>
            <span>찜</span>
          </router-link>
        </li>
      </ul>
    </nav>
  </aside>
  <router-view></router-view>
</main>

</template>

<style scoped>
@import url("@/assets/css/myLayout.css");
</style>