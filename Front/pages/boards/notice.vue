<script setup>
import { onMounted,ref } from 'vue';

useHead({
  link: [{ rel: "stylesheet", href: "/css/board-notice.css" }],
});

const notice = ref([]);
const totalElements = ref(0);
const totalPages = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

const noDataMessage = ref('');

const fetchNotice = async () => {
  try {
    const params = new URLSearchParams({
      page: currentPage.value.toString(),
      size: pageSize.value.toString(),
    });

    // const response = await fetch(`http://localhost:8080/api/v1/boards/notice`);
    const response = await fetch(`http://localhost:8080/api/v1/boards/notice?${params.toString()}`);
    const data = await response.json();

    // 데이터의 구조 확인
    console.log(data); // 서버에서 받은 데이터를 확인해 보세요.

    // data.notice가 배열인지 확인하고 할당
    if (Array.isArray(data.notice)) {
      notice.value = data.notice;
    } else {
      notice.value = []; // 만약 배열이 아니면 빈 배열로 설정
    }

    totalElements.value = data.totalElements;
    totalPages.value = data.totalPages;

    // 게시물이 없을 때 메시지 처리
    if (notice.value.length === 0) {
      noDataMessage.value = '게시글이 없습니다.';
    } else {
      noDataMessage.value = '';
    }
  } catch (error) {
    console.error('게시판 데이터를 가져오는 중 오류 발생:', error);
  }
};

onMounted(() => {
  fetchNotice();
});
</script>
<template>
  <main>
    <div class="notice">
    <table>
    <tbody>
      <tr v-for="n in notice" :key="n.id">
        <td class="num">
          <span>{{ n.id }}</span>
        </td>
        <td>
          <div>
              <span clss="title">{{ n.title }}</span>
              <div class="smalls">
              <small class="wirter">{{ n.regMemberNickName }}</small>
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

    </tbody>
  </table>
  <button class="notice-btn">글쓰기</button>
    </div>
  </main>
</template>

