<script setup>

useHead({
  link: [{ rel: "stylesheet", href: "/css/admin/board-list.css" }],
});

import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

// 게시판 목록 상태
const boards = ref([]);
const isLoading = ref(true);

// 페이지 이동을 위한 라우터
const router = useRouter();

// 게시판 데이터 가져오기
const fetchBoards = async () => {
  try {
    const response = await fetch(`http://localhost:8080/api/v1/admin/boards/list`);
    const data = await response.json();
    boards.value = data;
  } catch (error) {
    console.error('게시판 데이터를 가져오는 중 오류 발생:', error);
  } finally {
    isLoading.value = false;
  }
};

// 컴포넌트 마운트 시 데이터 가져오기
onMounted(() => {
  fetchBoards();
});

// 글쓰기 페이지로 이동
const navigateToCreate = () => {
  router.push('/admin/board/reg');
};
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
        <form>
          <table class="table">
            <tbody>
              <tr>
                <th>등록일</th>
                <td>
                  <input type="text" id="good_sdate" name="good_sdate" class="input-text date" readonly />
                  <button type="button" id="icon_sdate" class="btn-calendar">&#x1F4C5;</button>
                  ~
                  <input type="text" id="good_edate" name="good_edate" class="input-text date" readonly />
                  <button type="button" id="icon_edate" class="btn-calendar mr-2">&#x1F4C5;</button>
                  <button type="button" id="date_term1" class="btn btn-primary mr-2">어제</button>
                  <button type="button" id="date_term2" class="btn btn-primary mr-2">오늘</button>
                  <button type="button" id="date_term3" class="btn btn-primary mr-2">이번 주</button>
                  <button type="button" id="date_term4" class="btn btn-primary">이번 달</button>
                </td>
              </tr>

              <tr>
                <th>게시판</th>
                <td>
                  <select name="st" class="select">
                    <option value="notice_all">전체</option>
                    <option value="notice_comm">커뮤니티</option>
                    <option value="notice_info">공지사항</option>
                    <option value="notice_faq">FAQ</option>
                    <option value="notice_cen">고객센터</option>
                  </select>
                </td>
              </tr>

              <tr>
                <th>게시글</th>
                <td>
                  <select name="sc" class="select">
                    <option value="notice_all">전체</option>
                    <option value="notice_title">제목</option>
                    <option value="notice_writer">작성자</option>
                    <option value="notice_content">내용</option>
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
            <select class="select mr-2">
              <option>10</option>
              <option>25</option>
              <option>50</option>
              <option>100</option>
            </select>
            <button @click="navigateToCreate" class="btn btn-primary">글쓰기</button>
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
                <th>Hits</th>
                <th>ACTIVE</th>
                <th>DATE</th>
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
                <td class="text-md">5</td>
                <td>
                  <div class="custom-control custom-switch active-toggle">
                    <input type="checkbox" class="custom-control-input" v-model="board.active" :id="'active' + board.id" />
                    <label class="custom-control-label" :for="'active' + board.id"></label>
                  </div>
                </td>
                <td class="text-md">{{ board.createDate }}</td>
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

