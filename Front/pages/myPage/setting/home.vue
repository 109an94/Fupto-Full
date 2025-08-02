<script setup>

</script>

<template>
<main>
  <aside class="profileContainer">
    <h1>프로필 관리</h1>
    <div class="profile">
      <div>
        <img src="" alt="">
      </div>
      <div class="info">
        <div class="info-name">닉네임</div>
        <div class="info-email">****@naver.com</div>
        <div><button>프로필관리</button></div>
      </div>
      <div class="icon">
        <span>알림</span>
      </div>
    </div>
    <nav class="profileNav">
      <ul>
        <li>
          <router-link to="/my/setting/acount">
            <span>계정 정보</span>
          </router-link>
        </li>
        <li>
          <router-link to="/my/setting/alram">
            <span>알림 설정 ㅇ</span>
          </router-link>
        </li>
      </ul>
    </nav>
  </aside>
  <router-view></router-view>
</main>

</template>

<style scoped>
@import url("@/assets/css/mysetting.css");
@import url("@/assets/css/reset.css");
</style>