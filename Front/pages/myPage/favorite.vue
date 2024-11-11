<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/myLayout.css" }],
});
</script>

<template>
  <div class="favoriteContainer">

    <div class="favoriteCard">
       <div class="favoriteDetail">
         <div class="favoriteTitle">
          <img src="" alt="">
           <div class="favoriteBanner">
             <div class="title">브랜드명</div>
             <div class="description">trouser</div>
             <div class="description"><span>svg</span> 1,000,000원</div>
           </div>
           </div>
         <div class="favoriteButton">
             <button class="icon:close" aria-label="close">
               <span>닫기</span>
             </button>
             <button>
               이동
             </button>
           </div>
       </div>
       <div>아래로 확장 버튼</div>
    </div>

    <div class="favoriteCard">
      <div class="favoriteDetail">
        <img src="" alt="">
        <div>
          <div>브랜드명</div>
          <div>상품명 준나김 잉잉ㅇ 트라우저</div>
          <div><span>svg로고</span> 1,000,000원</div>
        </div>
        <div class="favoriteButton">
          <button class="icon:close" aria-label="close">
            <span>닫기</span>
          </button>
          <button>
            이동
          </button>
        </div>
      </div>
      <div>아래로 확장 버튼</div>
    </div>
  </div>
</template>
