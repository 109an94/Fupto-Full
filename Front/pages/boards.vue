<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/board-index.css" }]
});
</script>

<template>
<main>
<nav>
<ul class="board_ul">
  <li>
    <nuxt-link to="/boards/notice">
          <button>공지사항</button>
        </nuxt-link>
  </li>
  <li>
    <nuxt-link to="/boards/community">
          <button>커뮤니티</button>
        </nuxt-link>
  </li>
  <li>
    <nuxt-link to="/boards/show">
          <button>자랑하기</button>
        </nuxt-link>
  </li>
  <li>
    <nuxt-link to="/boards/faq">
          <button>FAQ</button>
        </nuxt-link>
  </li>
  <li>
    <nuxt-link to="/boards/center">
          <button>고객센터</button>
        </nuxt-link>
  </li>
  
</ul>
</nav>
<nuxt-page/>
</main>
</template>
