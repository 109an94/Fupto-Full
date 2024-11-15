<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/admin/board-list.css" }],
});

import { ref, onMounted } from 'vue';

// 상태관리
// const router = useRouter()
const boards = ref([]);
const totalElements = ref(0);
const totalPages = ref(0);
const currentPage = ref(1);
const pageSize = ref(3);

const startDatePicker = ref(null);
const endDatePicker = ref(null);
const startDateInput = ref(null);
const endDateInput = ref(null);
const searchForm = ref(null);

const noDataMessage = ref('');

// 폼 데이터
const formData = ref({
  searchType: 'default_title', // 제목, 작성자, 내용(?)
  searchKeyWord: '',
  boardCategoryName: '',
  active: '',
  dateType: 'reg',
  startDate: '',
  endDate: '',
})

// 모달
const showModal = ref(false);
const selectedBoard = reactive({
  id: '',
  regMemberNickName: '',
  boardCategory: '',
  title: '',
  contents: '',
  img: ''
});

// 모달 열기
const openModal = (board) => {
  Object.assign(selectedBoard, board);
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
    if (formData.value.boardCategoryName) params.append("boardCategory", formData.value.boardCategory);
    if (formData.value.dateType) params.append("dateType", formData.value.dateType);
    if (formData.value.startDate) params.append("startDate", formData.value.startDate);
    if (formData.value.endDate) params.append("endDate", formData.value.endDate);

    const response = await fetch(`http://localhost:8080/api/v1/admin/boards?${params.toString()}`);
    const data = await response.json();
    console.log(data)
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
    const response = await fetch(`http://localhost:8080/api/v1/admin/boards/${boardId}/active?active=${active}`, {
      method: "PATCH",
    });
    if (!response.ok) {
      throw new Error("Active 상태 변경에 실패했습니다.");
    }
  } catch (error) {
    console.error("Error updating active status:", error);
  }
};

// active 토글 핸들러
const handleActiveChange = async (brand) => {
  await updateActive(brand.id, brand.active);
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
    selectedItems.value = new Set(brands.value.map((board) => board.id));
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
  selectAll.value = selectedItems.value.size === brands.value.length;
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
                    <option value="default_name">전체</option>
                    <option value="notice_comm">커뮤니티</option>
                    <option value="notice_info">공지사항</option>
                    <option value="notice_faq">FAQ</option>
                    <option value="notice_cen">고객센터</option>
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
                  <select v-model="formData.searchKeyWord" name="sc" class="select">
                    <option value="notice_all">전체</option>
                    <option value="title">제목</option>
                    <option value="regMemberNickName">작성자</option>
                    <option value="contents">내용</option>
                  </select>
                  <input type="text" name="ss" class="input-text" />
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
            <button class="btn btn-outline-danger">삭제</button>
          </div>

          <div class="d-flex">
            <select class="select ml-2" v-model="pageSize" @change="pageSizeChange">
              <option :value="3">3</option>
              <option :value="5">5</option>
              <option :value="10">10</option>
              <option :value="15">15</option>
            </select>
            <div>
            <router-link to="/admin/boards/reg" class="btn btn-outline-primary">글쓰기</router-link>
          </div>
          </div>
        </div>
        <div>
          <table class="table product-list-table">
            <thead>
              <tr class="text-md">
                <th>
                  <div class="custom-control custom-checkbox">
                    <input type="checkbox" class="custom-control-input" id="selectAll" />
                    <label class="custom-control-label" for="selectAll"></label>
                  </div>
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
                  <div class="custom-control custom-checkbox">
                    <input type="checkbox" class="custom-control-input" :id="'notice' + board.id" />
                    <label class="custom-control-label" :for="'notice' + board.id"></label>
                  </div>
                </td>
                <td>{{ board.id }}</td>
                <td class="product-cell"><a href="#">{{ board.title }}</a></td>
                <td class="text-md">{{ board.boardCategoryName }}</td>
                <td class="text-md">{{ board.regMemberNickName }}</td>
                <td>
                  <div class="custom-control custom-switch active-toggle">
                    <input type="checkbox" class="custom-control-input" v-model="board.active" :id="'active' + board.id" />
                    <label class="custom-control-label" :for="'active' + board.id"></label>
                  </div>
                </td>
                <td class="text-md">{{ board.createdAt }}</td>
                <td class="text-md">{{ board.modifiedAt }}</td>
                <td>
                  <button class="btn btn-outline-secondary btn-sm">
                    <i class="mdi mdi-pencil"></i>
                  </button>
                  <button class="btn btn-outline-danger btn-sm">
                    <i class="mdi mdi-delete"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </main>
</template>

