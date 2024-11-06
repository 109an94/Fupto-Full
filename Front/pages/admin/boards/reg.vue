<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/admin/board-reg.css" }],
});

// 게시판 데이터와 이미지 URL 상태 정의
const board = ref({
  title: '',
  boardCategory: '',
  important: false,
  visible: false,
  content: '',
  fileUpload: null,
});

const imageUrl = ref('');

// 이미지 미리보기 기능
const previewImage = (event) => {
  const file = event.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      imageUrl.value = e.target.result;
    };
    reader.readAsDataURL(file);
  } else {
    imageUrl.value = '';
  }
};

// 제출 처리
const handleSubmit = () => {
  // 데이터를 서버로 전송할 수 있음 (예: fetch API 또는 axios 사용)
  alert('등록되었습니다');
  
  // 폼 초기화
  board.value = {
    title: '',
    boardCategory: '',
    important: false,
    visible: false,
    content: '',
    fileUpload: null,
  };
  imageUrl.value = '';
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
      <li><a href="#" class="active">게시판 등록</a></li>
    </ul>

    <div class="card">
      <div class="card-body">
        <form @submit.prevent="handleSubmit">
          <table class="table">
            <tbody>

              <tr>
                <th>제목</th>
                <td>
                  <input v-model="board.title" type="text" class="input-text" required />
                  <input type="checkbox" class="important" v-model="board.important" />
                </td>
              </tr>

              <tr>
                <th>게시판</th>
                <td>
                  <select v-model="board.boardCategory" class="select">
                    <option value="board_notice">공지사항</option>
                    <option value="board_comm">커뮤니티</option>
                    <option value="board_faq">FAQ</option>
                    <option value="board_cen">고객센터</option>
                  </select>
                </td>
              </tr>

              <tr>
                <th>노출</th>
                <td>
                  <input type="checkbox" id="toggle" v-model="board.visible" hidden/>
                  <label for="toggle" class="toggleSwitch">
                    <span class="toggleButton"></span>
                  </label>
                </td>
              </tr>

              <tr>
                <th>내용</th>
                <td>
                  <textarea v-model="board.content" class="content" required></textarea>
                </td>
              </tr>

              <tr>
                <th>이미지</th>
                <td>
                  <input type="file" multiple id="fileUpload" @change="previewImage" accept="image/*">
                  <div class="image-preview" v-if="imageUrl">
                    <img :src="imageUrl" alt="미리보기 이미지" />
                  </div>
                </td>
              </tr>

            </tbody>
          </table>

          <div class="text-center">
            <button type="submit" class="btn btn-primary">등록</button>
          </div>
        </form>
      </div>
    </div>
  </main>
</template>

<!-- <style scoped>
@import '@/public/css/admin/board-reg.css';

</style> -->

