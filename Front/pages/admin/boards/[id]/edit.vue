<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router'; // 라우트 정보를 가져오기 위해 사용
import { useRouter } from 'vue-router'; // 페이지 이동을 위해 사용

useHead({
  link: [{ rel: "stylesheet", href: "/css/admin/board-reg.css" }],
});

// 게시판 데이터
const board = ref({
  title: '',
  contents: '',
  regMemberId: 7,
  boardCategoryId: 1,
  active: true,
  fileUpload: null,
});

const imageUrl = ref('');
const router = useRouter();
const route = useRoute();

// 게시글 ID 가져오기 (URL 파라미터)
const boardId = route.params.id;

// 게시글 데이터 불러오기
const fetchBoardData = async () => {
  try {
    const response = await fetch(`http://localhost:8080/api/v1/admin/boards/${boardId}`);
    if (response.ok) {
      const result = await response.json();
      board.value = { ...result }; // 서버에서 받아온 데이터를 board 객체에 넣기
      imageUrl.value = result.imageUrl || ''; // 이미지 URL이 있으면 설정
    } else {
      throw new Error('게시글을 불러오는 데 실패했습니다.');
    }
  } catch (error) {
    console.error('Error:', error);
    alert('게시글을 불러오는 중 오류가 발생했습니다.');
  }
};

// 이미지 미리보기 기능
const previewImage = (event) => {
  const file = event.target.files[0];
  if (file) {
    if (file.size > 5 * 1024 * 1024) { // 5MB 제한
      alert('파일 크기는 5MB를 초과할 수 없습니다.');
      event.target.value = ''; // 파일 선택 초기화
      return;
    }
    if (!file.type.startsWith('image/')) {
      alert('이미지 파일만 업로드 가능합니다.');
      event.target.value = ''; // 파일 선택 초기화
      return;
    }
    board.value.fileUpload = file;
    const reader = new FileReader();
    reader.onload = (e) => {
      imageUrl.value = e.target.result;
    };
    reader.readAsDataURL(file);
  } else {
    imageUrl.value = '';
    board.value.fileUpload = null;
  }
};

// 이미지를 삭제
const removeImage = () => {
  imageUrl.value = '';  // 미리보기 이미지 초기화
  board.value.fileUpload = null;  // 파일 데이터 초기화
};

// 제출 처리
const handleSubmit = async () => {
  try {
    const formData = new FormData();

    formData.append('boardData', JSON.stringify({
      title: board.value.title,
      contents: board.value.contents,
      regMemberId: board.value.regMemberId,
      boardCategoryId: board.value.boardCategoryId,
      active: board.value.active,
    }));

    // 이미지 파일 추가
    if (board.value.fileUpload) {
      formData.append('file', board.value.fileUpload);
    }

    const response = await fetch(`http://localhost:8080/api/v1/admin/boards/${boardId}/update`, {
      method: 'PUT',  // PUT 메서드를 사용하여 수정 요청
      body: formData,
    });

    if (response.ok) {
      const result = await response.json();
      console.log('게시글 수정 성공:', result);
      alert('게시글이 수정되었습니다.');
      router.push('/admin/boards/list'); // 게시판 목록 페이지로 리디렉션
    } else {
      const errorData = await response.json();
      console.error('게시글 수정 실패:', errorData);
      alert('게시글 수정에 실패했습니다.');
    }
  } catch (error) {
    console.error('Error:', error);
    alert('게시글 수정 중 오류가 발생했습니다.');
  }
};

// 폼 리셋
const resetForm = () => {
  board.value = {
    title: '',
    contents: '',
    regMemberId: 7,
    boardCategoryId: 1,
    active: true,
    fileUpload: null,
  };
  imageUrl.value = '';
};

// 취소 처리
const handleCancel = () => {
  router.push('/admin/boards/list'); // 수정 취소 시 목록 페이지로 이동
};

// 페이지가 로드될 때 게시글 데이터 불러오기
onMounted(() => {
  fetchBoardData();
});
</script>

<template>
  <main>
    <h1 class="title">게시판 수정</h1>
    <ul class="breadcrumbs">
      <li><a href="#">FUPTO</a></li>
      <li class="divider">/</li>
      <li><a href="#" class="active">USER</a></li>
      <li class="divider">/</li>
      <li><a href="#" class="active">게시판</a></li>
      <li class="divider">/</li>
      <li><a href="#" class="active">게시판 수정</a></li>
    </ul>

    <div class="card">
      <div class="card-body">
        <form @submit.prevent="handleSubmit" enctype="multipart/form-data">
          <table class="table">
            <tbody>
              <tr>
                <th>제목</th>
                <td>
                  <input v-model="board.title" type="text" class="input-text" required />
                </td>
              </tr>
              
              <tr>
                <th>게시판</th>
                <td>
                  <select v-model="board.boardCategoryId" class="select">
                    <option :value="1">공지사항</option>
                    <option :value="2">커뮤니티</option>
                    <option :value="3">FAQ</option>
                    <option :value="4">고객센터</option>
                  </select>
                </td>
              </tr>

              <tr>
                <th>공개</th>
                <td>
                  <select v-model="board.active" class="select">
                    <option :value="true">공개</option>
                    <option :value="false">비공개</option>
                  </select>
                </td>
              </tr>

              <tr>
                <th>내용</th>
                <td>
                  <textarea v-model="board.contents" class="content" required></textarea>
                </td>
              </tr>

              <tr>
                <th>이미지</th>
                <td>
                  <input type="file" id="fileUpload" @change="previewImage" accept="image/*">
                  <div class="image-preview" style="position: relative;">
                    <img v-if="imageUrl" :src="imageUrl" alt="미리보기 이미지" />
                    <button v-if="imageUrl" type="button" @click="removeImage" class="remove-img-btn">X</button>
                  </div>
                </td>
              </tr>

            </tbody>
          </table>

          <div class="text-center">
            <button type="submit" class="btn btn-primary">수정</button>
            <button type="button" @click="handleCancel" class="btn btn-secondary">취소</button>
          </div>
        </form>
      </div>
    </div>
  </main>
</template>
