<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/board-notice.css" }],
});

</script>
<template>
  <main>
    <div class="notice">
    <table>
    <tbody>
      <tr>
        <td class="num">
          <span>1</span>
        </td>
        <td>
          <div>
              <span clss="title">fupto 서비스 예정</span>
              <div class="smalls">
              <small class="wirter">Logan</small>
              <small class="date">10.25</small>
              <small class="hits">조회 4</small>
            </div>
          </div>

        </td>
        <td class="image">
          <img src="https://via.placeholder.com/70"  class="product-img" />
        </td>
                
        <td class ="comment-td">
          <div class ="comment-div">
            <span class="comment">댓글22</span>
          </div>
        </td>
      </tr>

      <tr>
        <td class="num">
          <span>2</span>
        </td>
        <td>
          <div>
            <div>
              <span clss="title">fupto 직원 공고</span>
              <div class="smalls">
              <small class="wirter">jenny</small>
              <small class="date">10.23</small>
              <small class="hits">조회 62</small>
            </div>
          </div>
          </div>
        </td>

        

        <td class="image">
          <img src="https://via.placeholder.com/70"  class="product-img" />
        </td>
                
        <td class ="comment-td">
          <div class ="comment-div">
            <span class="comment">댓글22</span>
          </div>
        </td>
      </tr>
    </tbody>
  </table>
  <button class="notice-btn">글쓰기</button>
    </div>
  </main>
</template>

