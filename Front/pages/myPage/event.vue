<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/myLayout.css" }],
});

</script>

<template>
  <div class="eventCard">
    <img src="">
    <div class="eventBanner">
      <router-link to="/">
        <div class="title">브랜드명 or title</div>
        <div class="description">description like human made 가 입점되었습니다~ 혹은 할인 이벤트중~</div>
      </router-link>
    </div>
    <button class="icon:close" aria-label="close">
        <span>닫기</span>
    </button>
  </div>

  <div class="eventCard">
    <img src="">
    <div class="eventBanner">
      <router-link to="/">
        <div class="title">브랜드명 or title</div>
        <div class="description">description like human made 가 입점되었습니다~ 혹은 할인 이벤트중~</div>
      </router-link>
    </div>
    <button class="icon:close" aria-label="close">
      <span>닫기</span>
    </button>
  </div>

  <div class="eventCard">
    <img src="">
    <div class="eventBanner">
      <router-link to="/">
        <div class="title">브랜드명 or title</div>
        <div class="description">description like human made 가 입점되었습니다~ 혹은 할인 이벤트중~</div>
      </router-link>
    </div>
    <button class="icon:close" aria-label="close">
      <span>닫기</span>
    </button>
  </div>
</template>

