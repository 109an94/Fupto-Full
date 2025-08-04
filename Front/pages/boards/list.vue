<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/board-index.css" }],
});

import { ref, onMounted} from 'vue'
import { use$Fetch } from "~/composables/use$Fetch";

const boards = ref([]);
const totalElements = ref(0);
const totalPages = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

const searchForm = ref(null);
const noDataMessage = ref('');
const imageUrl = ref('');

const formData = ref({
  searchType: 'title', // 제목, 작성자, 내용(?)
  searchKeyWord: '',
  boardCategoryName: '',
})

const getImageUrl = (url) => {
  if (!url) return '';
  if (url.startsWith('data:')) {
    // 새로 업로드된 이미지의 경우 (data URL)
    return url;
  }
  // 서버에서 가져온 이미지 URL의 경우
  return `${config.public.apiBase}${url}`;
};

const handleCategoryClick = (categoryName) => {
  formData.value.boardCategoryName = categoryName;
  currentPage.value = 1; 
  fetchBoards(); 
};

const fetchBoards = async () => {
  try{
    const params = new URLSearchParams({
      page: currentPage.value.toString(),
      size: pageSize.value.toString()
    });
    if (formData.value.searchType) params.append("searchType", formData.value.searchType);
    if (formData.value.searchKeyWord) params.append("searchKeyWord", formData.value.searchKeyWord);
    if (formData.value.boardCategoryName) params.append("boardCategory", formData.value.boardCategoryName);
    params.append("active", true);

    const data = await use$Fetch(`/boards/list?${params.toString()}`);
    // const data = await response.json();
    boards.value = data.boards;
    imageUrl.value = data.img ? (data.img.startsWith('/') ? data.img : '/' + data.img) : '';
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

const formatDate = (dateString) => {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 +1
  const day = String(date.getDate() -1 ).padStart(2, '0'); // 날짜가 한 자리 수일 경우 0을 추가

  return `${year}-${month}-${day}`;
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

// const visiblePages = computed(() => {
//   const startPage = Math.floor((currentPage.value - 1) / 5) * 5 + 1;
//   const endPage = Math.min(startPage + 4, totalPages.value);
//   return Array.from({ length: endPage - startPage + 1 }, (_, i) => startPage + i);
// });


onMounted(() => {
  fetchBoards();
});
</script>

<template>
<main>
<nav>
<ul class="board_ul">
  <li>
        <button 
          @click="handleCategoryClick('공지사항')" 
          :class="{ active: formData.boardCategoryName === '공지사항' }">
          공지사항
        </button>
      </li>
      <li>
        <button 
          @click="handleCategoryClick('커뮤니티')" 
          :class="{ active: formData.boardCategoryName === '커뮤니티' }">
          커뮤니티
        </button>
      </li>

      <li>
        <button 
          @click="handleCategoryClick('FAQ')" 
          :class="{ active: formData.boardCategoryName === 'FAQ' }">
          FAQ
        </button>
      </li>
      <li>
        <button 
          @click="handleCategoryClick('고객센터')" 
          :class="{ active: formData.boardCategoryName === '고객센터' }">
          고객센터
        </button>
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
              <span clss="title"><nuxt-link :to="`/boards/${ board.id }/detail`">{{ board.title }}</nuxt-link></span>
              <div class="smalls">
              <small class="wirter">{{ board.regMemberNickName }}</small>
              <small class="date">{{ formatDate(board.createdAt) }}</small>
            </div>
          </div>

        </td>
        <td class="product-img">
              <div class="d-flex align-items-center">
                <img v-if="board.img" :src="'http://localhost:8085/api/v1/' + board.img" class="product-img" />
                <!-- 이미지가 없을 경우, 빈 공간 표시되지 않음 -->
              </div>
            </td>

        <td class="comment">

        </td>

        
              
      </tr>

    </tbody>
  </table>

  <div class="write">
  <button class="write-btn"><nuxt-link :to="`/boards/reg`">글쓰기</nuxt-link></button>
  <!-- <nuxt-link :to="`/boards/re`">{{ board.title }}[댓글 수]</nuxt-link> -->
  </div>

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


  <div class="Pagination-container">
    <nav aria-label="Page navigation">
      <ul class="Pagination justify-content-center">
        <li class="Page-item" :class="{ disabled: currentPage === 1 }">
          <a class="Page-link" href="#" @click.prevent="pageChange(currentPage - 1)"><</a>
        </li>
        <li class="Page-item" v-for="page in visiblePages" :key="page" :class="{ active: currentPage === page }">
          <a class="Page-link" href="#" @click.prevent="pageChange(page)">{{ page }}</a>
        </li>
        <li class="Page-item" :class="{ disabled: currentPage === totalPages }">
          <a class="Page-link" href="#" @click.prevent="pageChange(currentPage + 1)">></a>
        </li>
      </ul>
    </nav>
  </div>
</div>
    
</main>
</template>
