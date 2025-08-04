<script setup>
import {useRouter} from "vue-router";
import {ref, computed, watch, onMounted} from 'vue';
import {useAuthFetch} from "~/composables/useAuthFetch.js";

useHead({
  link: [{ rel: "stylesheet", href: "/css/admin/report.css" }],
});


//----------------state--------------
const router = useRouter()
const members = ref([]);
const isLoading = ref(false);
const totalPages = ref(0);
const totalCount = ref(0);
const currentPage = ref(1);
const page = ref(10);
const size = ref(10);
const filterData = ref({
  // date
  // login
  // membertype
  memberType:"",
  // gender
  gender:"",
  // search
  searchType: "userId",
  searchKeyWord :"",
  // date
  dateType : "regDate",
  startDate : "",
  endDate : ""
})
//---------------methods-------------
const fetchMembers = async (page = 1) => {
  try {
    isLoading.value = true;
    const queryParams = {
      page: page,
      size: size.value,
      ...(filterData.value.memberType && {memberType: filterData.value.memberType}),
      ...(filterData.value.gender && {gender: filterData.value.gender}),
      ...(filterData.value.searchType && {searchType: filterData.value.searchType}),
      ...(filterData.value.searchKeyWord && {searchKeyWord: filterData.value.searchKeyWord}),
      ...(filterData.value.dateType && {dateType: filterData.value.dateType}),
      ...(filterData.value.startDate && {startDate: filterData.value.startDate}),
      ...(filterData.value.endDate && {endDate: filterData.value.endDate})
    };
    const response = await use$Fetch('/admin/members/search', {
      method: 'GET',
      params: queryParams
    });

    if (!response || !response.members) {
      throw new Error('유효한 데이터가 없습니다.');
    }

    console.log(response);
    members.value = response.members;
    totalPages.value = response.totalPages;
    currentPage.value = page;
    totalCount.value = response.totalCount;
  } catch (error) {
    console.error("데이터 조회 오류:", error);
    // 오류 처리 로직 (예: 사용자에게 오류 메시지 표시)
  } finally {
    isLoading.value = false;
  }
}

// 페이지네이션 관련 함수
const pageChange = (newPage) => {
  fetchMembers(newPage);
}

const getPageNumbers = computed(() => {
  const pageCount = Math.min(10, totalPages.value);
  let start = Math.max(1, currentPage.value - Math.floor(pageCount / 2));
  let end = Math.min(start + pageCount - 1, totalPages.value);

  if (end - start + 1 < pageCount) {
    start = Math.max(1, end - pageCount + 1);
  }

  return Array.from({ length: end - start + 1 }, (_, i) => start + i);
});



//나이 계산하는 매서드
const calculateAge = (birthDate) => {
  const today = new Date();
  const birthDateObj = new Date(birthDate);
  let age = today.getFullYear() - birthDateObj.getFullYear();
  const monthDiff = today.getMonth() - birthDateObj.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate()
      < birthDateObj.getDate())) {
    age--;
  }
  return age;
}

//Instant 뒷자리 일자 남기는 매서드
const delectTime = (createDate) => {
  const date = new Date(createDate);
  return date.toISOString().split('T')[0];
}


//--------------lifecycle hooks
onMounted(() => {
  fetchMembers();
})

//------------event handler

const searchHandler = (event) => {
  event.preventDefault();
  currentPage.value = 0;
  fetchMembers();
}

const urlHandler = (id) => {
  router.push(`/admin/members/${id}`)
}


//----------------

// 각 사용자의 선택 상태가 변경될 때마다 updateSelectAll 함수 호출
members.value.forEach(members => {
  watch(() => members.selected, updateSelectAll);
});
</script>

<template>
  <main>
    <h1 class="title">고객 목록</h1>
    <ul class="breadcrumbs">
      <li><a href="#">FUPTO</a></li>
      <li class="divider">/</li>
      <li><a href="#">고객</a></li>
      <li class="divider">/</li>
      <li><a href="#" class="active">고객 목록</a></li>
    </ul>
    <div class="data">
      <div class="content-data">
        <form @submit="searchHandler">
          <div class="card">
            <div class="">
              <!--                          class에 head 제거함-->
              <div class="filter">
                <table class="report">
                  <tbody>
                  <tr>
                    <th>날짜</th>
                    <td colspan="3">
                      <select v-model="filterData.dateType" class="">
                        <option value="regDate">가입일</option>
                        <option value="updateDate">수정일</option>
                        <option value="loginDate">접속일</option>
                      </select>
                      <input type="date" v-model="filterData.startDate">
                      <span>~</span>
                      <input type="date" v-model="filterData.endDate">
                      <button class="btn-url active">오늘</button>
                      <button class="btn-url">주간</button>
                      <button class="btn-url">월간</button>
                      <button class="btn-url">분기</button>
                      <button class="btn-url">반기</button>
                      <button class="btn-url">년간</button>

                    </td>
                  </tr>
                  <tr>
                    <th>소셜 로그인</th>
                    <td colspan="3">
                      <select class="">
                        <option>기본</option>
                        <option>구글</option>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <th>회원 유형</th>
                    <td>
                      <select v-model="filterData.memberType" class="">
                        <option value="">전체</option>
                        <option value="ROLE_USER">일반</option>
                        <option value="ROLE_ADMIN">관리자</option>
                      </select>
                    </td>
                    <th>성별</th>
                    <td>
                      <input type="radio" id="men" name="성별" v-model="filterData.gender" value="" checked><label
                        for="men">전체</label>
                      <input type="radio" id="men" name="성별" v-model="filterData.gender" value="남성"><label
                        for="men">남성</label>
                      <input type="radio" id="women" name="성별" v-model="filterData.gender" value="여성"><label
                        for="women">여성</label>
                    </td>
                  </tr>
                  <tr>
                    <th>검색명</th>
                    <td colspan="3">
                      <select v-model="filterData.searchType" class="">
                        <option value="userId">아이디</option>
                        <option value="nickname">이름</option>
                        <option value="email">이메일</option>
                      </select>
                      <input type="text" class="" v-model="filterData.searchKeyWord">
                    </td>
                  </tr>
                  </tbody>
                </table>
              </div>
            </div>
            <div class="filter-btn">
              <button class="btn-url btn-main">검색</button>
            </div>
          </div>
        </form>
      </div>
    </div>
    <div class="data">
      <div class="content-data">
        <div class="card">
          <table class="report f-list">
            <tbody>
            <tr>
              <th><input type="checkbox"></th>
              <th>이름</th>
              <th>아이디</th>
              <th>이메일</th>
              <th>가입일</th>
              <th>나이</th>
              <th>성별</th>
              <th>회원 유형</th>
            </tr>
            <tr v-for="m in members" :key="m.id" @click="urlHandler(m.id)">
              <td><input type="checkbox" v-model="m.selected"></td>
              <td>{{ m.nickname }}</td>
              <td>{{ m.userId }}</td>
              <td>{{ m.email }}</td>
              <td>{{ delectTime(m.createDate) }}</td>
              <td>{{ calculateAge(m.birthDate) }}</td>
              <td>{{ m.gender }}</td>
              <td>{{ m.memberType || '일반' }}</td>
            </tr>
            </tbody>
          </table>
          <!-- 페이지네이션 -->

          <div class="pagination-container mt-4">
            <ul class="pagination justify-content-center">
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <button class="page-link" @click="pageChange(1)">&lt;&lt;</button>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <button class="page-link" @click="pageChange(currentPage - 1)">이전</button>
              </li>
              <li v-for="pageNum in getPageNumbers" :key="pageNum" class="page-item" :class="{ active: pageNum === currentPage }">
                <button class="page-link" @click="pageChange(pageNum)">
                  {{ pageNum }}
                </button>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                <button class="page-link" @click="pageChange(currentPage + 1)">다음</button>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                <button class="page-link" @click="pageChange(totalPages)">&gt;&gt;</button>
              </li>
            </ul>
          </div>
      </div>
    </div>
    </div>
  </main>
</template>

<style scoped>
.sub-product {
  background-color: #f0f0f0;
}

/*페이지네이션*/
.pagination-container {
  margin-top: 1rem;
}

.pagination {
  display: flex;
  justify-content: center;
  list-style: none;
  padding: 0;
}

.page-item {
  margin: 0 0.25rem;
}

.page-link {
  padding: 0.5rem 1rem;
  border: 1px solid #dee2e6;
  background-color: #fff;
  color: #007bff;
  cursor: pointer;
}

.page-item.active .page-link {
  background-color: #007bff;
  border-color: #007bff;
  color: #fff;
}

.page-item.disabled .page-link {
  color: #6c757d;
  pointer-events: none;
  background-color: #fff;
  border-color: #dee2e6;
}

.date-cell {
  text-align: center;
  vertical-align: middle;
  padding: 8px;
}

.pl-switch {
  position: relative;
  display: inline-block;
  width: 35px;
  height: 20px;
}

.pl-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.pl-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.4s;
  border-radius: 34px;
}

.pl-slider:before {
  position: absolute;
  content: "";
  height: 14px;
  width: 14px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.4s;
  border-radius: 50%;
}
.pl-slider.round {
  border-radius: 34px;
}

.pl-slider.round:before {
  border-radius: 50%;
}

input:checked + .pl-slider {
  background-color: #2196f3;
}

input:checked + .pl-slider:before {
  transform: translateX(15px);
}

</style>