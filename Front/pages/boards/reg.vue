<script setup>
import{ ref, onMounted } from 'vue';
import { use$Fetch } from "~/composables/use$Fetch";

useHead({
  link: [{ rel: "stylesheet", href: "/css/board-reg.css" }],
});


// 게시판 데이터
const board = ref({
  title: '',
  contents: '',
  regMemberId: '',
  boardCategoryId: 1,
  active: true,
  fileUpload: null,
});

const imageUrl = ref('');
const userDetails= useUserDetails();

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
const handleSubmit = async() => {
  try{
    const formData = new FormData();

    formData.append('boardData', JSON.stringify({
    title: board.value.title,
    contents: board.value.contents,
    regMemberId: userDetails.id.value,
    boardCategoryId: board.value.boardCategoryId,
    active: board.value.active,
  }));
  
  // 이미지 파일 추가
  if (board.value.fileUpload) {
    formData.append('file', board.value.fileUpload);
  }

  const {data, error} = await use$Fetch('/boards/post', {
        method: 'POST',
        body: formData,
      });

      if (error) {
            throw new Error(error.message || '게시글 등록에 실패했습니다.');
        }

        console.log('게시글 등록 성공:',data);
        alert('게시글이 등록되었습니다.');
        resetForm();
        window.location.href = 'http://localhost:3000/boards/list';
        
      } catch(error) {
        console.error('Error:',error);
        alert('게시글 등록 중 오류가 발생했습니다.');
      }
    };

  const handleCancel = () => {
    window.location.href = 'http://localhost:3000/boards/list';
};

  const resetForm = () => {
    board.value = {
      title: '',
      contents: '',
      regMemberId: '',
      boardCategoryId: 1,
      active: true,
      fileUpload: null,
    };
    imageUrl.value = '';
  };

</script>


<template>
  <main>

    <div class="board-container">
      <div class="board-detail">
        <form @submit.prevent="handleSubmit" enctype="multipart/form-data">
          <table class="table">
            <tbody>
              <tr>
                <th>제목</th>
                <td>
                  <input v-model="board.title" type="text" class="input-text" required />
                  <!-- <input type="checkbox" class="important" v-model="board.important" /> -->
                </td>
              </tr>
              
              <tr>
                <th>게시판</th>
                <td>
                  <select v-model="board.boardCategoryId" class="select">
                    <option :value="1" selected>공지사항</option>
                    <option :value="2">커뮤니티</option>
                    <option :value="3">FAQ</option>
                    <option :value="4">고객센터</option>
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
            <button type="submit" class="btn btn-primary">등록</button>
            <button type="button" class="btn btn-secondary" @click="handleCancel">취소</button>
          </div>
        </form>
      </div>
    </div>
  </main>
</template>

