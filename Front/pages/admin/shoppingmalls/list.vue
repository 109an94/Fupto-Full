<script setup>
import { ref, onMounted} from "vue";
import { use$Fetch } from "~/composables/use$Fetch";

useHead({
    link: [{ rel: "stylesheet", href: "/css/admin/shoppingmall-list.css"}],
});

const shoppingmalls = ref([]);
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
  nameType: 'default_name',  // 쇼핑몰명 유형
  name: '',                  // 쇼핑몰명 입력
  active: '',                // 사용여부
  dateType: 'reg',
  startDate: '',
  endDate: ''
});

// 모달 표시 여부와 선택된 쇼핑몰 데이터
const showModal = ref(false);
const selectedshoppingmall = reactive({ 
  id: '',
  korName: '',
  engName: '',
  description: '',
  img: '',
  url: '',
  deliveryfee: '',
  taxes: ''
});

// 모달 열기 함수
const openModal = (shoppingmall) => {
    Object.assign(selectedshoppingmall, shoppingmall); // 선택된 쇼핑몰 정보를 selectedshoppingmall에 저장
    showModal.value = true; // 모달 표시
};

// 모달 닫기 함수
const closeModal = () => {
    showModal.value = false; // 모달 숨기기
};

// 체크박스 상태
const selectAll = ref(false);
const selectedItems = ref(new Set());

// API 호출
const fetchshoppingmalls = async () => {
  try {
    const params = new URLSearchParams({
      page: currentPage.value.toString(),
      size: pageSize.value.toString(),
    });

    if (formData.value.active) params.append("active", formData.value.active);
    if (formData.value.nameType) params.append("nameType", formData.value.nameType);
    if (formData.value.name) params.append("name", formData.value.name);
    if (formData.value.dateType) params.append("dateType", formData.value.dateType);
    if (formData.value.startDate) params.append("startDate", formData.value.startDate);
    if (formData.value.endDate) params.append("endDate", formData.value.endDate);

    // use$Fetch 호출
    const data = await use$Fetch(`/admin/shoppingmalls?${params.toString()}`);
    
    shoppingmalls.value = data.shoppingmalls;
    totalElements.value = data.totalElements;
    totalPages.value = data.totalPages;

    if (shoppingmalls.value.length === 0) {
      noDataMessage.value = '데이터가 없습니다.';
    } else {
      noDataMessage.value = '';
    }
  } catch (error) {
    console.error("Error fetching shoppingmalls:", error);
  }
};

// // API 호출
// const fetchshoppingmalls = async () => {
//   try {
//     const response = await fetch(
//       `http://localhost:8080/api/v1/admin/shoppingmalls?page=${currentPage.value}&size=${pageSize.value}&active=${formData.value.active}&nameType=${formData.value.nameType}&name=${formData.value.name}&dateType=${formData.value.dateType}&startDate=${formData.value.startDate}&endDate=${formData.value.endDate}`
//     );
//     const data = await response.json();
//     console.log(data);
//     shoppingmalls.value = data.shoppingmalls;
//     totalElements.value = data.totalElements;
//     totalPages.value = data.totalPages;
//     if (shoppingmalls.value.length === 0) {
//       noDataMessage.value = '데이터가 없습니다.';
//     } else {
//       noDataMessage.value = '';
//     }
//   } catch (error) {
//     console.error("Error fetching shoppingmalls:", error);
//   }
// };

// active 상태 변경
const updateActive = async (shoppingmallId, active) => {
  try {
    console.log(`Updating active status: shoppingmallId=${shoppingmallId}, active=${active}`);
    const response = await use$Fetch(`/admin/shoppingmalls/${shoppingmallId}/active?active=${active}`, {
      method: "PATCH",
    });
    if (!response.ok) {
      throw new Error("Active 상태 변경에 실패했습니다.");
    }
  } catch (error) {
    console.error("Error updating active status:", error);
  }
};

const confirmDelete = (shoppingmallId) => {
  if (confirm('정말 삭제하시겠습니까?')) {
    handleDelete(shoppingmallId);
  }
};

// const handleDelete = async (shoppingmallId) => {
//   try {
//     const response = await use$Fetch(`/admin/shoppingmalls/${shoppingmallId}/state`, {
//       method: "PATCH"
//     });

//     if (!response.ok) {
//       throw new Error("쇼핑몰 삭제에 실패했습니다.");
//     }

//     // 삭제 성공
//     alert('정상적으로 삭제되었습니다.');
//     selectedItems.value.clear();
//     selectAll.value = false;
//     fetchshoppingmalls();

//   } catch (error) {
//     console.error("Error deleting shoppingmall:", error);
//     alert(error.message);
//   }
// };

const handleDelete = async (shoppingmallId) => {
  try {
    await use$Fetch(`/admin/shoppingmalls/${shoppingmallId}/state`, {
      method: "PATCH",
    });

    // 삭제 성공
    alert("정상적으로 삭제되었습니다.");
    await fetchshoppingmalls();
  } catch (error) {
    console.error("Error deleting shoppingmall:", error);
    alert(error.message || "쇼핑몰 삭제에 실패했습니다.");
  }
};

// const handleBulkDelete = async () => {
//   if (selectedItems.value.size === 0) {
//     alert('삭제할 쇼핑몰을 선택해주세요.');
//     return;
//   }

//   if (confirm('선택한 쇼핑몰을 모두 삭제하시겠습니까?')) {
//     try {
//       const response = await use$Fetch('/admin/shoppingmalls/bulk-update-state', {
//         method: 'PATCH',
//         headers: {
//           'Content-Type': 'application/json',
//         },
//         body: JSON.stringify(Array.from(selectedItems.value)),
//       });

//       if (!response.ok) {
//         throw new Error('쇼핑몰 일괄 삭제에 실패했습니다.');
//       }

//       alert('선택한 쇼핑몰이 성공적으로 삭제되었습니다.');
//       selectedItems.value.clear();
//       selectAll.value = false;
//       fetchshoppingmalls();
//     } catch (error) {
//       console.error('Error deleting shoppingmalls:', error);
//       alert(error.message);
//     }
//   }
// };

const handleBulkDelete = async () => {
  if (selectedItems.value.size === 0) {
    alert("삭제할 쇼핑몰을 선택해주세요.");
    return;
  }

  if (confirm("선택한 쇼핑몰을 모두 삭제하시겠습니까?")) {
    try {
      await use$Fetch("/admin/shoppingmalls/bulk-update-state", {
        method: "PATCH",
        body: Array.from(selectedItems.value),
      });

      alert("선택한 쇼핑몰이 성공적으로 삭제되었습니다.");
      selectedItems.value.clear();
      selectAll.value = false;
      await fetchshoppingmalls();
    } catch (error) {
      console.error("Error deleting shoppingmalls:", error);
      alert(error.message || "쇼핑몰 일괄 삭제에 실패했습니다.");
    }
  }
};

// active 토글 핸들러
const handleActiveChange = async (shoppingmall) => {
  await updateActive(shoppingmall.id, shoppingmall.active);
};

// 페이지네이션 핸들러
const pageChange = (newPage) => {
  if (newPage < 1 || newPage > totalPages.value) return;
  currentPage.value = newPage;
  fetchshoppingmalls();
};

const pageSizeChange = (event) => {
  pageSize.value = parseInt(event.target.value);
  currentPage.value = 1;
  fetchshoppingmalls();
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
    formData.value.startDate = startDatePicker.value.formatDate(yesterday, "Y-m-d");
    formData.value.endDate = endDatePicker.value.formatDate(yesterday, "Y-m-d");
  }
};

const setToday = () => {
  const today = new Date();
  if (startDatePicker.value && endDatePicker.value) {
    startDatePicker.value.setDate(today);
    endDatePicker.value.setDate(today);
    formData.value.startDate = startDatePicker.value.formatDate(today, "Y-m-d");
    formData.value.endDate = endDatePicker.value.formatDate(today, "Y-m-d");
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
    formData.value.startDate = startDatePicker.value.formatDate(firstDayOfWeek, "Y-m-d");
    formData.value.endDate = endDatePicker.value.formatDate(lastDayOfWeek, "Y-m-d");
  }
};

const setThisMonth = () => {
  const today = new Date();
  const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
  const lastDayOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);

  if (startDatePicker.value && endDatePicker.value) {
    startDatePicker.value.setDate(firstDayOfMonth);
    endDatePicker.value.setDate(lastDayOfMonth);
    formData.value.startDate = startDatePicker.value.formatDate(firstDayOfMonth, "Y-m-d");
    formData.value.endDate = endDatePicker.value.formatDate(lastDayOfMonth, "Y-m-d");
  }
};

// 체크박스 핸들러
const handleSelectAll = (event) => {
  event.stopPropagation();
  event.preventDefault();
  const checked = event.target.checked;
  if (checked) {
    selectedItems.value = new Set(shoppingmalls.value.map((shoppingmall) => shoppingmall.id));
  } else {
    selectedItems.value.clear();
  }
  selectAll.value = checked;
};

const handleSelectItem = (event, shoppingmallId) => {
  event.stopPropagation();
  event.preventDefault();
  if (selectedItems.value.has(shoppingmallId)) {
    selectedItems.value.delete(shoppingmallId);
  } else {
    selectedItems.value.add(shoppingmallId);
  }
  selectAll.value = selectedItems.value.size === shoppingmalls.value.length;
};

const handleSearch = (event) => {
  event.preventDefault();
  currentPage.value = 1; // 검색 시 첫 페이지로 리셋
  fetchshoppingmalls();
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

// 숫자 포맷팅 함수
const formatNumber = (number) => {
  return number?.toLocaleString("ko-KR") || "0";
};

// 초기화
onMounted(() => {
  fetchshoppingmalls();
  waitForFlatpickr(() => {
    initializeFlatpickr();
  });
});

</script>

<template>
  <main>
    <h1 class="title">쇼핑몰 목록</h1>
		<ul class="breadcrumbs">
			<li><a href="#">FUPTO</a></li>
			<li class="divider">/</li>
      <li><a href="#">쇼핑몰</a></li>
			<li class="divider">/</li>
			<li><a href="#" class="active">쇼핑몰 목록</a></li>
		</ul>

    <div class="card">
      <div class="card-body">
        <form ref="searchForm" @submit="handleSearch">
          <table class="table">
            <tbody>
              <tr>
                <th>쇼핑몰명</th>
                <td>
                  <select v-model="formData.nameType" class="select">
                    <option value="default_name">전체</option>
                    <option value="korName">한글명</option>
                    <option value="engName">영어명</option>
                  </select>
                  <input v-model="formData.name" type="text"  class="input-text" />
                </td>
              </tr>
              <tr>
                <th>사용여부</th>
                <td>
                  <select v-model="formData.active" class="select">
                    <option value="" selected>전체</option>
                    <option value="true">노출함</option>
                    <option value="false">노출안함</option>
                  </select>
                </td>
              </tr>
              <tr>
                <th>날짜</th>
                <td>
                  <!-- 날짜 필터 선택 -->
                  <select v-model="formData.dateType" class="select">
                    <option value="reg" selected>등록일</option>
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
            </tbody>
          </table>
          <div class="text-center">
            <button type="submit" class="btn btn-primary">검 색</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 상품 목록 테이블 -->
    <div class="card">
      <div class="card-body">
        <div class="d-flex">
          <div>
            <button class="btn btn-outline-danger" @click="handleBulkDelete">일괄 삭제</button>
            <select class="select ml-2" v-model="pageSize" @change="pageSizeChange">
              <option :value="10">10</option>
              <option :value="20">20</option>
              <option :value="30">30</option>
              <option :value="50">50</option>
            </select>
          </div>
          <div>
            <router-link to="/admin/shoppingmalls/reg" class="btn btn-primary">+ Add shoppingmall</router-link>
          </div>
        </div>
        <div>
          <table class="table shoppingmall-list-table">
            <thead>
              <tr class="text-md">
                <th>
                  <input type="checkbox" id="selectAll" :checked="selectAll" @change="handleSelectAll" class="pl-checkbox" />
                </th>
                <th>번호</th>
                <th>쇼핑몰이미지</th>
                <th>쇼핑몰한글명</th>
                <th>쇼핑몰영어명</th>
                <th>URL</th>
                <th>배송비</th>
                <th>관부가세</th>
                <th>등록일</th>
                <th>수정일</th>
                <th>사용여부</th>
                <th>비고</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="noDataMessage">
                <td colspan="12" class="text-center py-4 text-gray-500">
                  {{ noDataMessage }}
                </td>
              </tr>
              <template v-else>
                <!-- 첫 번째 대표 상품 -->
                <tr v-for="sm in shoppingmalls" :key="sm.id">
                <td>
                  <input
                    type="checkbox"
                    :id="'shoppingmall' + sm.id"
                    :checked="selectedItems.has(sm.id)"
                    @change="(e) => handleSelectItem(e, sm.id)"
                    class="pl-checkbox"
                  />
                </td>
                <td>{{ sm.id }}</td>
                <td class="shoppingmall-cell">
                  <div class="d-flex align-items-center">
                    <img :src="'http://localhost:8085/api/v1/' + sm.img || 'https://via.placeholder.com/70'" :alt="sm.korName" class="shoppingmall-img" />
                  </div>
                </td>
                <td class="text-md">{{ sm.korName }}</td>
                <td class="text-md">{{ sm.engName }}</td>
                <td>
                  <button class="btn btn-outline-primary btn-sm"><a :href="sm.url" target="_blank">URL 이동</a></button>
                </td>
                <td class="text-md">￦{{ formatNumber(sm.deliveryfee) }}</td>
                <td class="text-md">{{ formatNumber(sm.taxes) }}%</td>
                <td class="text-md">{{ formatDate(sm.createDate)[0] }}<br>{{ formatDate(sm.createDate)[1] }}</td>
                <td class="text-md">{{ formatDate(sm.updateDate)[0] }}<br>{{ formatDate(sm.updateDate)[1] }}</td>
                <td>
                  <label class="pl-switch">
                    <input type="checkbox" :id="'active' + sm.id" v-model="sm.active" @change="() => handleActiveChange(sm)"/>
                    <span class="pl-slider round"></span>
                  </label>
                </td>
                <td>
                  <button class="btn btn-outline-secondary btn-sm toggle-shoppingmalls" @click="openModal(sm)">
                    <i class="mdi mdi-chevron-down"></i>
                  </button>
                  <NuxtLink :to="`/admin/shoppingmalls/${sm.id}/edit`" class="btn btn-outline-secondary btn-sm">
                    <i class="bx bxs-pencil"></i>
                  </NuxtLink>
                  <button class="btn btn-outline-danger btn-sm" @click="confirmDelete(sm.id)">
                    <i class="bx bx-trash"></i>
                  </button>
                </td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>
        <!-- 페이징 네이션 추가 -->
        <div class="pagination-container">
          <nav aria-label="Page navigation">
            <ul class="pagination justify-content-center">
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <a class="page-link" href="#" @click.prevent="pageChange(currentPage - 1)">Previous</a>
              </li>
              <li class="page-item" v-for="page in visiblePages" :key="page" :class="{ active: currentPage === page }">
                <a class="page-link" href="#" @click.prevent="pageChange(page)">{{ page }}</a>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                <a class="page-link" href="#" @click.prevent="pageChange(currentPage + 1)">Next</a>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
    <!-- 모달 -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h5 class="modal-title">쇼핑몰 상세 정보</h5>
          <button type="button" class="close" @click="closeModal">
            &times;
          </button>
        </div>
        
        <div class="modal-body">
          <p><strong>번호:</strong> {{ selectedshoppingmall.id }}번</p>
          <p><strong>쇼핑몰 이미지:</strong><br><img :src="'http://localhost:8085/api/v1/' + selectedshoppingmall.img || 'https://via.placeholder.com/70'" :alt="selectedshoppingmall.korName"></p>
          <p><strong>쇼핑몰 한글명:</strong> {{ selectedshoppingmall.korName }}</p>
          <p><strong>쇼핑몰 영어명:</strong> {{ selectedshoppingmall.engName }}</p>
          <p><strong>쇼핑몰 URL:</strong><a :href="selectedshoppingmall.url" target="_blank"> {{ selectedshoppingmall.url }}</a></p>
          <p><strong>배송비:</strong> ￦{{ formatNumber(selectedshoppingmall.deliveryfee) }}</p>
          <p><strong>관부가세:</strong> ￦{{ formatNumber(selectedshoppingmall.taxes) }}</p>
          <p><strong>상세설명:</strong><br>{{ selectedshoppingmall.description }}</p>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-primary" @click="closeModal">닫기</button>
        </div>
      </div>
    </div>
  </main>
</template>