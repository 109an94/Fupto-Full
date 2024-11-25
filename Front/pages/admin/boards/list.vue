<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/admin/board-list.css" }],
});

import { ref, onMounted } from 'vue';
import { use$Fetch } from "~/composables/use$Fetch";

// 상태관리
// const router = useRouter()
const boards = ref([]);
const totalElements = ref(0);
const totalPages = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

const startDatePicker = ref(null);
const endDatePicker = ref(null);
const startDateInput = ref(null);
const endDateInput = ref(null);
const searchForm = ref(null);

const noDataMessage = ref('');

// 폼 데이터
const formData = ref({
  searchType: 'title', // 제목, 작성자, 내용(?)
  searchKeyWord: '',
  boardCategoryName: '',
  active: '',
  dateType: 'cre',
  startDate: '',
  endDate: '',
})

// 모달
const showModal = ref(false);
const selectedBoard = reactive({
  id: '',
  title: '',
  boardCategoryName: '',
  regMemberNickName: '',
  contents: '',
  img: ''
});

// 모달 열기
const openModal = (board) => {
  Object.assign(selectedBoard, board);
  console.log(selectedBoard);
  showModal.value = true;
};

//모달 닫기
const closeModal = () => {
  showModal.value = false;
};

// 체크박스 상태
const selectAll = ref(false);
const selectedItems = ref(new Set());

// 게시판 데이터 가져오기
const fetchBoards = async () => {
  try {
    const params = new URLSearchParams({
      page: currentPage.value.toString(),
      size: pageSize.value.toString(),
    });

    if (formData.value.searchType) params.append("searchType", formData.value.searchType);
    if (formData.value.searchKeyWord) params.append("searchKeyWord", formData.value.searchKeyWord);
    if (formData.value.active) params.append("active", formData.value.active);
    if (formData.value.boardCategoryName) params.append("boardCategory", formData.value.boardCategoryName);
    if (formData.value.dateType) params.append("dateType", formData.value.dateType);
    if (formData.value.startDate) params.append("startDate", formData.value.startDate);
    if (formData.value.endDate) params.append("endDate", formData.value.endDate);

    const data = await use$Fetch(`/admin/boards?${params.toString()}`);
    // const data = await response.json();
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

// active
const updateActive = async (boardId, active) => {
  try {
    console.log(`Updating active status: boardId=${boardId}, active=${active}`);
    const response = await use$Fetch(`/admin/boards/${boardId}/active?active=${active}`, {
      method: "PATCH",
    });
    if (!response.ok) {
      throw new Error("Active 상태 변경에 실패했습니다.");
    }
  } catch (error) {
    console.error("Error updating active status:", error);
  }
};


// 삭제
const confirmDelete = (boardId) => {
  if (confirm('삭제하시겠습니까?')) {
    handleDelete(boardId);
  }
};

const handleDelete = async (boardId) => {
  try {
    await use$Fetch(`/admin/boards/${boardId}`, {
      method: "DELETE"
    });

    // if (!response.ok) {
    //   throw new Error("삭제에 실패했습니다.");
    // }

    alert('삭제되었습니다.');
    fetchBoards();

  } catch (error) {
    console.error("Error deleting board:", error);
    alert(error.message);
  }
};


// 선택삭제
const selectedDelete = async () => {
  if (selectedItems.value.size === 0) {
    alert('삭제할 게시글을 선택해주세요.');
    return;
  }

  if (confirm('선택한 게시글을 삭제하시겠습니까?')) {
    try {
      await use$Fetch('/admin/boards/selected', {
        method: 'DELETE',
        body:Array.from(selectedItems.value),
      });

      if (!response.ok) {
        throw new Error('삭제에 실패했습니다.');
      }

      alert('삭제되었습니다.');
      selectedItems.value.clear();
      selectAll.value = false;
      fetchBoards();
    } catch (error) {
      console.error('Error deleting boards:', error);
      alert(error.message);
    }
  }
};

// active 토글 핸들러
const handleActiveChange = async (board) => {
  await updateActive(board.id, board.active);
};

// 페이지네이션 핸들러
const pageChange = (newPage) => {
  if (newPage < 1 || newPage > totalPages.value) return;
  currentPage.value = newPage;
  fetchBoards();
};

const pageSizeChange = (event) => {
  pageSize.value = parseInt(event.target.value);
  currentPage.value = 1;
  fetchBoards();
};

// 현재 페이지에 따라 표시할 페이지 번호 범위를 동적으로 계산
const visiblePages = computed(() => {
  const startPage = Math.floor((currentPage.value - 1) / 5) * 5 + 1;
  const endPage = Math.min(startPage + 4, totalPages.value);
  return Array.from({ length: endPage - startPage + 1 }, (_, i) => startPage + i);
});


// Flatpickr 로딩 체크
const waitForFlatpickr = (callback) => {
  if (window.flatpickr) {
    callback();
  } else {
    setTimeout(() => waitForFlatpickr(callback), 100);
  }
};

// Flatpickr 초기화
const initializeFlatpickr = () => {
  const config = {
    dateFormat: "Y-m-d",
    showMonths: 1,
    static: true,
    monthSelectorType: "static",
  };
  
  // 시작일 Picker 초기화
  startDatePicker.value = window.flatpickr(startDateInput.value, {
    ...config,
    onChange: (selectedDates) => {
      const selectedDate = selectedDates[0];
      if (selectedDate && endDatePicker.value) {
        endDatePicker.value.set('minDate', selectedDate); // 종료일 Picker의 최소 날짜 설정
      }
    },
  });

  // 종료일 Picker 초기화
  endDatePicker.value = window.flatpickr(endDateInput.value, {
    ...config,
    onChange: (selectedDates) => {
      const selectedDate = selectedDates[0];
      if (selectedDate && startDatePicker.value) {
        startDatePicker.value.set('maxDate', selectedDate); // 시작일 Picker의 최대 날짜 설정
      }
    },
  });
  
}

// 날짜 설정 함수들
const setYesterday = () => {
  const yesterday = new Date();
  yesterday.setDate(yesterday.getDate() - 1);
  if (startDatePicker.value && endDatePicker.value) {
    startDatePicker.value.setDate(yesterday);
    endDatePicker.value.setDate(yesterday);
  }
};


const setToday = () => {
  const today = new Date();
  if (startDatePicker.value && endDatePicker.value) {
    startDatePicker.value.setDate(today);
    endDatePicker.value.setDate(today);
  }
};

const setThisWeek = () => {
  const today = new Date();
  const firstDayOfWeek = new Date(today);
  firstDayOfWeek.setDate(today.getDate() - today.getDay());
  const lastDayOfWeek = new Date(firstDayOfWeek);
  lastDayOfWeek.setDate(firstDayOfWeek.getDate() + 6);

  if (startDatePicker.value && endDatePicker.value) {
    startDatePicker.value.setDate(firstDayOfWeek);
    endDatePicker.value.setDate(lastDayOfWeek);
  }
};

const setThisMonth = () => {
  const today = new Date();
  const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
  const lastDayOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);

  if (startDatePicker.value && endDatePicker.value) {
    startDatePicker.value.setDate(firstDayOfMonth);
    endDatePicker.value.setDate(lastDayOfMonth);
  }
};


// 체크박스 핸들러
const handleSelectAll = (event) => {
  event.stopPropagation();
  event.preventDefault();
  const checked = event.target.checked;
  if (checked) {
    selectedItems.value = new Set(boards.value.map((board) => board.id));
  } else {
    selectedItems.value.clear();
  }
  selectAll.value = checked;
};

const handleSelectItem = (event, boardId) => {
  event.stopPropagation();
  event.preventDefault();
  if (selectedItems.value.has(boardId)) {
    selectedItems.value.delete(boardId);
  } else {
    selectedItems.value.add(boardId);
  }
  selectAll.value = selectedItems.value.size === boards.value.length;
};

const handleSearch = (event) => {
  event.preventDefault();
  currentPage.value = 1; // 검색 시 첫 페이지로 리셋
  fetchBoards();
};

// 날짜 포맷팅 함수
const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);

   // 로컬 날짜를 YYYY-MM-DD 형식으로 포맷
   const ymd =
    date.getUTCFullYear() + "-" + 
    String(date.getUTCMonth() + 1).padStart(2, "0") + "-" + 
    String(date.getUTCDate()).padStart(2, "0");

  // 로컬 시간 (HH:mm:ss) 형식으로 포맷
  const time =
    String(date.getUTCHours()).padStart(2, "0") +
    ":" +
    String(date.getUTCMinutes()).padStart(2, "0") +
    ":" +
    String(date.getUTCSeconds()).padStart(2, "0");

  return [ymd, time]; // 배열로 반환
};

// 컴포넌트 마운트 시 데이터 가져오기
onMounted(() => {
  fetchBoards();
  waitForFlatpickr(() => {
    initializeFlatpickr();
  });
});


</script>

<template>
  <main>
    <h1 class="title">게시판</h1>
    <ul class="breadcrumbs">
      <li><a href="#">FUPTO</a></li>
      <li class="divider">/</li>
      <li><a href="#" class="active">USER</a></li>
      <li class="divider">/</li>
      <li><a href="#" class="active">게시판</a></li>
      <li class="divider">/</li>
      <li><a href="#" class="active">게시판 목록</a></li>
    </ul>
    <div class="card">
      <div class="card-body">
        <form ref="searchForm" @submit="handleSearch">
          <table class="table">
            <tbody>
              <tr>
                <th>날짜</th>
                <td>
                  <select v-model="formData.dateType" class="select">
                    <option value="cre" selected>등록일</option>
                    <option value="mod">수정일</option>
                  </select>
                  <input ref="startDateInput" v-model="formData.startDate" type="text" class="input-text date" readonly />
                  <button type="button" class="btn-calendar" @click="() => startDatePicker?.open()">&#x1F4C5;</button>
                  ~
                  <input ref="endDateInput" v-model="formData.endDate" type="text" class="input-text date" readonly />
                  <button type="button" class="btn-calendar mr-2" @click="() => endDatePicker?.open()">&#x1F4C5;</button>
                  <button type="button" class="btn btn-primary mr-2" @click="setYesterday">어제</button>
                  <button type="button" class="btn btn-primary mr-2" @click="setToday">오늘</button>
                  <button type="button" class="btn btn-primary mr-2" @click="setThisWeek">이번 주</button>
                  <button type="button" class="btn btn-primary" @click="setThisMonth">이번 달</button>
                </td>
              </tr>

              <tr>
                <th>게시판</th>
                <td>
                  <select v-model="formData.boardCategoryName" name="st" class="select">
                    <option value="">전체</option>
                    <option value="공지사항">공지사항</option>
                    <option value="커뮤니티">커뮤니티</option>
                    <option value="FAQ">FAQ</option>
                    <option value="고객센터">고객센터</option>
                  </select>
                </td>
              </tr>

              <tr>
                <th>공개</th>
                <td>
                  <select v-model="formData.active" class="select">
                    <option value="" selected>전체</option>
                    <option value="true">공개</option>
                    <option value="false">비공개</option>
                  </select>
                </td>
              </tr>

              <tr>
                <th>게시글</th>
                <td>
                  <select v-model="formData.searchType" name="sc" class="select">
                    <option value="title">제목</option>
                    <option value="regMemberNickName">작성자</option>
                    <option value="contents">내용</option>
                  </select>
                  <input v-model="formData.searchKeyWord" type="text" name="ss" class="input-text" />
                </td>
              </tr>

            </tbody>
          </table>
          <div class="text-center">
            <button type="submit" class="btn btn-primary">검 색</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 게시판 목록 테이블 -->
    <div class="card">
      <div class="card-body">
        <div class="d-flex">
          <div class="d-flex-1">
            <button class="btn btn-outline-danger" @click="selectedDelete">삭제</button>
          </div>

          <div class="d-flex">
            <select class="select ml-2" v-model="pageSize" @change="pageSizeChange">
              <option :value="5">5</option>
              <option :value="10">10</option>
              <option :value="15">15</option>
              <option :value="20">20</option>
            </select>
            <div>
            <nuxt-link to="/admin/boards/reg" class="btn btn-outline-primary">글쓰기</nuxt-link>
          </div>
          </div>
        </div>
        <div>
          <table class="table product-list-table">
            <thead>
              <tr class="text-md">
                <!-- <th>
                  <div class="custom-control custom-checkbox">
                    <input type="checkbox" class="custom-control-input" id="selectAll" />
                    <label class="custom-control-label" for="selectAll"></label>
                  </div>
                </th> -->
                <th>
                  <input type="checkbox" id="selectAll" :checked="selectAll" @change="handleSelectAll" class="pl-checkbox" />
                </th>
                <th>NO.</th>
                <th>Title</th>
                <th>Type</th>
                <th>Writer</th>
                <th>ACTIVE</th>
                <th>CREATE</th>
                <th>UPDATE</th>
                <th>EDIT</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="board in boards" :key="board.id">
                <td>
                    <input
                    type="checkbox"
                    :id="'board' + board.id"
                    :checked="selectedItems.has(board.id)"
                    @change="(e) => handleSelectItem(e, board.id)"
                    class="pl-checkbox"
                  />
                </td>
                <td>{{ board.id }}</td>
                
                <td class="product-cell"><nuxt-link :to="`/boards/${ board.id }/detail`">{{ board.title }}</nuxt-link></td>
                
                <td class="text-md">{{ board.boardCategoryName }}</td>
                <td class="text-md">{{ board.regMemberNickName }}</td>
                <td>
                  <div class="custom-control custom-switch active-toggle">
                    <input type="checkbox" class="custom-control-input" v-model="board.active" :id="'active' + board.id" @change="() => handleActiveChange(board)"/>
                    <label class="custom-control-label" :for="'active' + board.id"></label>
                  </div>
                </td>
                <td class="text-md">{{ formatDate(board.createdAt)[0] }}<br>{{ formatDate(board.createdAt)[1] }}</td>
                <td class="text-md">{{ formatDate(board.modifiedAt)[0] }}<br>{{ formatDate(board.modifiedAt)[1] }}</td>
                <td>
                  <button class="btn btn-outline-third btn-sm toggle-brands" @click="openModal(board)">
                    <i class="mdi mdi-chevron-down"></i>
                  </button>
                  <nuxt-link :to="`/admin/boards/${ board.id }/edit`">
                    <button class="btn btn-outline-secondary btn-sm">
                      <i class="bx bxs-pencil"></i>
                    </button>
                  </nuxt-link>
                  <button class="btn btn-outline-danger btn-sm" @click="confirmDelete(board.id)">
                    <i class="bx bx-trash"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

          <div class="pagination-container">
          <nav aria-label="Page navigation">
            <ul class="pagination justify-content-center">
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <a class="page-link" href="#" @click.prevent="pageChange(currentPage - 1)">이전</a>
              </li>
              <li class="page-item" v-for="page in visiblePages" :key="page" :class="{ active: currentPage === page }">
                <a class="page-link" href="#" @click.prevent="pageChange(page)">{{ page }}</a>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                <a class="page-link" href="#" @click.prevent="pageChange(currentPage + 1)">다음</a>
              </li>
            </ul>
          </nav>
          </div>

      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h5 class="modal-title">게시글</h5>
          <button type="button" class="close" @click="closeModal">
            &times;
          </button>
        </div>
        
        <div class="modal-body">
          <p class="modal-no"><strong>NO. </strong> {{ selectedBoard.id }}</p>
          <p class="modal-tit"><strong>TITLE: </strong> {{ selectedBoard.title }}</p>
          <p class="modal-cat"><strong>CATERORY: </strong> {{ selectedBoard.boardCategoryName }}</p>
          <p class="modal-wri"><strong>WRITER: </strong> {{ selectedBoard.regMemberNickName }}</p>
          <p><strong>IMAGE:</strong><br>
              <img v-if="selectedBoard.img" :src="'http://localhost:8085/api/v1/' + selectedBoard.img" :alt="selectedBoard.img" />
              <p v-else>이미지 없음</p>
          </p>
          <br>
          <p class="modal-con"><strong>CONTENTS:</strong>{{ selectedBoard.contents }}</p>
        </div>
        
      </div>
    </div>
  </main>
</template>

