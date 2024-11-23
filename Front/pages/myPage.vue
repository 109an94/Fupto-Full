<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/myLayout.css" }],
});

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
          <li><nuxt-link to="/myPage/setting/account"><button>프로필관리</button></nuxt-link></li>
        </ul>
        <div class="icon">
          <span>알림</span>
        </div>
      </div>
      <nav class="profileNav">
        <ul>
          <li>
            <nuxt-link to="/myPage/board">
              <span>img</span>
              <span>게시판</span>
            </nuxt-link>
          </li>
          <li>
            <nuxt-link to="/myPage/alert">
              <span>img</span>
              <span>이벤트</span>
            </nuxt-link>
          </li>
          <li>
            <nuxt-link to="/myPage/favorite">
              <span>img</span>
              <span>찜</span>
            </nuxt-link>
          </li>
        </ul>
      </nav>
    </aside>
    <nuxt-page/>
  </main>

</template>
