
<script setup>
import { ref } from 'vue';
import { use$Fetch } from "~/composables/use$Fetch";

useHead({
    link: [{ rel: "stylesheet", href: "/css/board-reg.css"}],
});

const board = ref({
  title: '',
  contents: '',
  regMemberId: '',
  boardCategoryId: 1,
  active: true,
  fileUpload: null,
});

const config = useRuntimeConfig();
const route = useRoute();
const router = useRouter();
const boardId = route.params.id;

const imageUrl = ref('');
const userDetails= useUserDetails();

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
        // 파일이 선택되지 않았을 때 기존 이미지 URL 유지
        imageUrl.value = board.value.img ? `${config.public.apiBase}${board.value.img}` : '';
        board.value.fileUpload = null;
        // imageUrl.value = '';
    }

};

const getImageUrl = (url) => {
  if (!url) return '';
  if (url.startsWith('data:')) {
    // 새로 업로드된 이미지의 경우 (data URL)
    return url;
  }
  // 서버에서 가져온 이미지 URL의 경우
  return `${config.public.apiBase}${url}`;
};

const loadBoardData = async () => {
  try {
    const data = await use$Fetch(`/boards/${boardId}/edit`);

    console.log("Received data:", data);

    if (!data) {
      throw new Error("게시글 데이터를 받지 못했습니다.");
    }
    // const data = await response.json();
    board.value = {
      title: data.title,
      contents: data.contents,
      boardCategoryId: data.boardCategoryId,
      active: data.active,
      regMemberId: userDetails.id.value,
    };
    imageUrl.value = data.img ? (data.img.startsWith('/') ? data.img : '/' + data.img) : '';
  } catch (error) {
    console.error("Error loading board:", error);
    alert(`게시글을 불러오는데 실패했습니다: ${error.message}`);
  }
};

const handleSubmit = async () => {
  try {
    const formData = new FormData();

    formData.append('boardData', JSON.stringify({
      title: board.value.title,
      contents: board.value.contents,
      boardCategoryId: board.value.boardCategoryId,
      active: board.value.active,
      regMemberId: userDetails.id.value,
    }));

    // 파일이 있으면 formData에 파일 추가
    if (board.value.fileUpload) {
      formData.append('file', board.value.fileUpload);
    }
    console.log(formData);

    // 요청 보내기
    await use$Fetch(`/boards/${boardId}`, {
      method: 'PATCH',
      body: formData,
    });

    console.log('게시글 수정 성공');
    alert('게시글이 수정되었습니다!');
    router.push('/boards/list');
  } catch (error) {
    console.error('Error:', error);
    alert(`게시글 수정 중 오류가 발생했습니다: ${error.message}`);
  }
};

const removeImage = () => {
  imageUrl.value = '';  // 미리보기 이미지 초기화
  board.value.fileUpload = null;  // 파일 데이터 초기화
};

const handleCancel = () => {
  // router.push('/boards/list');
  router.go(-1);
};

onMounted(async () => {
  await loadBoardData();
});
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
                <td><input v-model="board.title" type="text" class="input-text" required /></td>
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
                <td><textarea v-model="board.contents" class="content" required></textarea></td>
              </tr>
              <tr>
                <th>이미지</th>
                <td>
                  <!-- 로드된 이미지 -->
                  <input type="file" id="fileUpload" @change="previewImage" accept="image/*">
                  <div class="image-preview" id="imagePreview">
                    <img v-if="imageUrl" :src="getImageUrl(imageUrl)" alt="미리보기 이미지">
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