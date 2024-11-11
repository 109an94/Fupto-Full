<script setup lang="ts">
import {useRouter} from "vue-router";
import {ref, computed, watch, onMounted} from 'vue';

useHead({
  link: [{ rel: "stylesheet", href: "/css/admin/report.css" }],
});


//----------------state--------------
const router = useRouter()
const members = ref([]);
const isLoading = ref(false);
const totalPages = ref(0);
const currentPage = ref(0);
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
//---------------methods
const fetchMembers = async (page = 1) => {
  try{
    const params = new URLSearchParams({
      page : page,
      size : size.value.toString()
    });
    // date
    // login
    // membertype
    if (filterData.value.memberType) params.append("memberType", filterData.value.memberType);
    // gender
    if (filterData.value.gender) params.append("gender", filterData.value.gender);
    // search
    if (filterData.value.searchType) params.append("searchType", filterData.value.searchType);
    if (filterData.value.searchKeyWord) params.append("searchKeyWord", filterData.value.searchKeyWord);

    if (filterData.value.dateType) params.append("dateType",filterData.value.dateType);
    if (filterData.value.startDate) params.append("startDate",filterData.value.startDate);
    if (filterData.value.endDate) params.append("endDate",filterData.value.endDate);

    const response = await fetch(`http://localhost:8080/api/v1/admin/members/search?${params.toString()}`);
    const data = await response.json();
    members.value = data.members;
    console.log(params.toString());
    console.log(data.members);
    // members.value = data; //아직 data 자체가 배열이여서 직접 할당

  } catch (error) {
    console.log("데이터 입력 오류 : ",error);
  } finally{
    isLoading.value = false;
  }
}

const calculateAge = (createDate : string) =>{
  const birthDate = new Date(createDate);
  const today = new Date();
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();
  if (monthDiff < 0||(monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }
  return age;
}

const regDate = (creatDate) => {
  const date = new Date(creatDate);
  return date.toISOString().split('T')[0];
}

//--------------lifecycle hooks
onMounted(()=>{
  fetchMembers();
})

//------------event handler

const searchHandler = (event) =>{
  event.preventDefault();
  currentPage.value = 0;
  fetchMembers();
}

const urlHandler = (id) =>{
  router.push(`/admin/members/${id}`)
}



//----------------
interface User {
  id: string;
  name: string;
  email: string;
  joinDate: string;
  age: number;
  gender: string;
  memberType: string;
  selected: boolean;
}

const users = ref<User[]>([
  { id: 'fupto1', name: '최원석', email: 'fupto@fupto.com', joinDate: '2024.10.18', age: 31, gender: '남성', memberType: '일반', selected: false },
  { id: 'fupto2', name: '이건주', email: 'fupto@fupto.com', joinDate: '2024.10.18', age: 31, gender: '남성', memberType: '일반', selected: false },
  { id: 'fupto3', name: '손우재', email: 'fupto@fupto.com', joinDate: '2024.10.18', age: 31, gender: '남성', memberType: '일반', selected: false },
  { id: 'fupto4', name: '박형민', email: 'fupto@fupto.com', joinDate: '2024.10.18', age: 31, gender: '남성', memberType: '일반', selected: false },
]);

const sortColumn = ref('');
const sortOrder = ref('asc');
const selectAll = ref(false);

const sort = (column: string) => {
  if (sortColumn.value === column) {
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc';
  } else {
    sortColumn.value = column;
    sortOrder.value = 'asc';
  }
};

const sortedUsers = computed(() => {
  return [...members.value].sort((a, b) => {
    const aValue = a[sortColumn.value as keyof members];
    const bValue = b[sortColumn.value as keyof members];

    if (aValue < bValue) return sortOrder.value === 'asc' ? -1 : 1;
    if (aValue > bValue) return sortOrder.value === 'asc' ? 1 : -1;
    return 0;
  });
});

const toggleAll = () => {
  members.value.forEach(members => members.selected = selectAll.value);
};

// selectAll 값을 모든 사용자의 선택 상태에 따라 업데이트
const updateSelectAll = () => {
  selectAll.value = members.value.every(members => members.selected);
};

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
                    <input type="date"  v-model="filterData.endDate">
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
                    <input type="radio" id="men" name="성별" v-model="filterData.gender" value="" checked><label for="men">전체</label>
                    <input type="radio" id="men" name="성별" v-model="filterData.gender" value="남성"><label for="men">남성</label>
                    <input type="radio" id="women" name="성별" v-model="filterData.gender" value="여성"><label for="women">여성</label>
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
                    <input type="text" class=""  v-model="filterData.searchKeyWord">
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div class="filter-btn"><button class="btn-url btn-main">검색</button></div>
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
<!--                <th><input type="checkbox"></th>-->
<!--                <th>이름</th>-->
<!--                <th>아이디</th>-->
<!--                <th>이메일</th>-->
<!--                <th>가입일</th>-->
<!--                <th>나이</th>-->
<!--                <th>성별</th>-->
<!--                <th>회원 유형</th>-->
                <th><input type="checkbox" v-model="selectAll" @change="toggleAll"></th>
                <th @click="sort('nickname')">이름 <span v-if="sortColumn === 'nickname'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span></th>
                <th @click="sort('userId')">아이디 <span v-if="sortColumn === 'userId'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span></th>
                <th @click="sort('email')">이메일 <span v-if="sortColumn === 'email'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span></th>
                <th @click="sort('createDate')">가입일 <span v-if="sortColumn === 'createDate'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span></th>
                <th @click="sort('age')">나이 <span v-if="sortColumn === 'age'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span></th>
                <th @click="sort('gender')">성별 <span v-if="sortColumn === 'gender'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span></th>
                <th @click="sort('memberType')">회원 유형 <span v-if="sortColumn === 'memberType'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span></th>
              </tr>
                <tr v-for="m in members" :key="m.id" @click="urlHandler(m.id)">
                  <td><input type="checkbox" v-model="m.selected"></td>
                  <td>{{ m.nickname }}</td>
                  <td>{{ m.userId }}</td>
                  <td>{{ m.email }}</td>
                  <td>{{ regDate(m.createDate) }}</td>
                  <td>{{ calculateAge(m.createDate)}}</td>
                  <td>{{ m.gender }}</td>
                  <td>{{ m.memberType || '일반'}}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </main>
</template>

<!--<style scoped>-->
<!--@import '@/public/css/admin/report.css';-->
<!--.sub-product {-->
<!--  background-color: #f0f0f0;-->
<!--}-->
<!--</style>-->