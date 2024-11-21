<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/board-index.css" }],
});

import { ref, onMounted} from 'vue'

const boards = ref([]);
const totalElements = ref(0);
const totalPages = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

const searchForm = ref(null);
const noDataMessage = ref('');

const formData = ref({
  searchType: 'title', // 제목, 작성자, 내용(?)
  searchKeyWord: '',
  boardCategoryName: '',
})

const fetchBoards = async () => {
  try{
    const params = new URLSearchParams({
      page: currentPage.value.toString(),
      size: pageSize.value.toString()
    });
    if (formData.value.searchType) params.append("searchType", formData.value.searchType);
    if (formData.value.searchKeyWord) params.append("searchKeyWord", formData.value.searchKeyWord);
    if (formData.value.boardCategoryName) params.append("boardCategory", formData.value.boardCategoryName);
    
    const response = await fetch(`http://localhost:8080/api/v1/boards?${params.toString()}`);
    const data = await response.json();
    boards.value = data.boards;
    totalElements.value = data.totalElements;
    totalPages.value = data.totalPages;
    if (boards.value.length === 0) {
      noDataMessage.value = '게시글이 없습니다.';
    } else {
      noDataMessage.value = '';
    }
  } catch (error) {
    console.error('게시판 데이터를 가져오는 중 오류 발생:', error);
  } 
};

const handleSearch = (event) => {
  event.preventDefault();
  currentPage.value = 1; // 검색 시 첫 페이지로 리셋
  fetchBoards();
};

const pageChange = (newPage) => {
  if (newPage < 1 || newPage > totalPages.value) return;
  currentPage.value = newPage;
  fetchBoards();
};

const visiblePages = computed(() => {
  const startPage = Math.floor((currentPage.value - 1) / 5) * 5 + 1;
  const endPage = Math.min(startPage + 4, totalPages.value);
  return Array.from({ length: endPage - startPage + 1 }, (_, i) => startPage + i);
});

onMounted(() => {
  fetchBoards();
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
<div class="board">
    <table>
    <tbody>
      <tr v-for="board in boards" :key="board.id">
        <td class="num">
          <span>{{ board.id }}</span>
        </td>
        <td>
          <div>
              <span clss="title">{{ board.title }}[댓글 수]</span>
              <div class="smalls">
              <small class="wirter">{{ board.regMemberNickName }}</small>
              <small class="date">10.25</small>
              <small class="hits">조회 4</small>
            </div>
          </div>

        </td>
        <td class="image">
          <img src="https://via.placeholder.com/70"  class="product-img" />
        </td>
              
      </tr>

    </tbody>
  </table>

  <button class="write-btn">글쓰기</button>


  <form ref ="searchForm" @submit="handleSearch" class="searchBox">
    <select v-model="formData.searchType" name="sc" class="type">
      <option value="title">제목</option>
      <option value="regMemberNickName">작성자</option>
      <option value="contents">내용</option>
    </select>
    <input v-model="formData.searchKeyWord" type="text" name="ss" class="keyword" />
      <div class="text-center">
    <button type="submit" class="searchBtn" value="검색">검색</button>
      </div>                    
    </form>


  <div class="pagination-container">
    <nav aria-label="Page navigation">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <a class="page-link" href="#" @click.prevent="pageChange(currentPage - 1)"><</a>
        </li>
        <li class="page-item" v-for="page in visiblePages" :key="page" :class="{ active: currentPage === page }">
          <a class="page-link" href="#" @click.prevent="pageChange(page)">{{ page }}</a>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <a class="page-link" href="#" @click.prevent="pageChange(currentPage + 1)">></a>
        </li>
      </ul>
    </nav>
  </div>
</div>
<nuxt-page/>
    
</main>
</template>
