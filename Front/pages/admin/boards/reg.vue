<script setup>
import{ ref } from 'vue';

useHead({
  link: [{ rel: "stylesheet", href: "/css/admin/board-reg.css" }],
});


// 게시판 데이터
const board = ref({
  title: '',
  boardCategoryName: '공지사항',
  contents: '',
  fileUpload: null,
});

const imageUrl = ref('');

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

// 제출 처리
const handleSubmit = async() => {
  try{

  const formData = new FormData();

  formData.append('title', board.value.title);
  formData.append('contents', board.value.contents);
  formData.append('boardCategoryName', board.value.boardCategoryName);

  // formData.append('boardData', JSON.stringify({
  //   tilte: board.value.title,
  //   contents: board.value.contents,
  //   boardCategoryName: board.value.boardCategoryName
  // }));
  
  // 이미지 파일도 추가 (파일이 있으면)
  if (board.value.fileUpload) {
    formData.append('file', board.value.fileUpload);
  }

  const response = await fetch('http://localhost:8080/api/v1/admin/boards/post',{
    method: 'POST',
    body: formData
  });

  if(response.ok){
    const result = await response.json();
    console.log('게시글 등록 성공:',result);
    alert('게시글이 등록되었습니다.');
    resetForm();
  } else {
    const errorData = await response.json();
    console.error('게시글 등록 실패:', errorData);
    throw new Error(errorData.message || '게시글 등록에 실패했습니다.');
    }
  } catch(error) {
    console.error('Error:',error);
    alert('게시글 등록 중 오류가 발생했습니다.');
  }
  };

  const resetForm = () => {
    board.value = {
      title: '',
      boardCategoryName: '',
      contents: '',
      fileUpload: null,
    };
  };

  const handleCancel = resetForm;

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
                  <select v-model="board.boardCategoryName" class="select">
                    <option value="공지사항">공지사항</option>
                    <option value="커뮤니티">커뮤니티</option>
                    <option value="FAQ">FAQ</option>
                    <option value="고객센터">고객센터</option>
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
                  <div class="image-preview">
                    <img v-if="imageUrl" :src="imageUrl" alt="미리보기 이미지"/>
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

