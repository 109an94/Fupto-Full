<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/myLayout.css" }],
  link: [{ rel: "stylesheet", href: "/css/myBoard.css" }]
});

</script>

<template>
  <div class="board-container">
    <div class="board-list">
      <div class="board-item">
        <!-- 좌측 번호 영역 -->
        <div class="item-number">1</div>

        <!-- 중앙 컨텐츠 영역 -->
        <div class="item-content">
          <div class="content-main">
            <h3 class="title">몸에 열이 많아서 힘드네요.</h3>
            <div class="content-info">
              <span class="writer">jenny</span>
              <span class="date">10.23</span>
              <span class="views">조회 62</span>
            </div>
          </div>
        </div>

        <!-- 우측 이미지 영역 -->
        <div class="item-image">
          <img src="https://via.placeholder.com/70" alt="게시글 이미지" />
        </div>

        <!-- 댓글 카운트 영역 -->
        <div class="item-comments">
          <span class="comment-count">댓글 22</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>