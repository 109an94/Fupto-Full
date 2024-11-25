<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/board-detail.css" }],
});

import { use$Fetch } from "~/composables/use$Fetch";


const board = ref({
  title: '',
  contents: '',
  regMemberNickName: '',
  boardCategoryName: '',
  fileUpload: null,
  active: ''
});

const config = useRuntimeConfig();
const route = useRoute();
const router = useRouter();
const boardId = route.params.id;
const userDetails= useUserDetails();

const imageUrl = ref('');
const dropdownVisible = ref(false);
const showModal = ref(false);


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

    board.value = {
      title: data.title,
      contents: data.contents,
      boardCategoryName: data.boardCategoryName,
      regMemberNickName: data.regMemberNickName,
      createdAt: data.createdAt,
      modifiedAt:data.modifiedAt,
      active: data.active,
      img: data.img, // 이미지 URL을 board 객체에 포함시킴
    };
    console.log(board);
    imageUrl.value = data.img ? (data.img.startsWith('/') ? data.img : '/' + data.img) : '';
  } catch (error) {
    console.error("Error loading board:", error);
    alert(`게시글을 불러오는데 실패했습니다: ${error.message}`);
  }
};

// 날짜 형식을 'yyyy-mm-dd'로 포맷팅하는 함수
const formatDate = (dateString) => {
  if (!dateString) return "";

  const date = new Date(dateString);
  // UTC 날짜 포맷
  const ymd = 
    date.getUTCFullYear() + "-" + 
    String(date.getUTCMonth() + 1).padStart(2, "0") + "-" + 
    String(date.getUTCDate()).padStart(2, "0");

  // UTC 시간 포맷 (시:분:초)
  const time = 
    String(date.getUTCHours()).padStart(2, "0") +
    ":" +
    String(date.getUTCMinutes()).padStart(2, "0") +
    ":" +
    String(date.getUTCSeconds()).padStart(2, "0");

  return `${ymd} ${time}`;  // 날짜와 시간을 '시:분:초' 형식으로 반환
};

// 드롭다운 메뉴 토글
const toggleDropdown = () => {
  dropdownVisible.value = !dropdownVisible.value;
};

// 수정 버튼 클릭 시 이동
const handleEdit = () => {
  router.push(`/boards/${boardId}/edit`); // 수정 페이지로 이동
};

// 삭제 버튼 클릭 시 처리
const handleDelete = async () => {
  // 삭제 확인 메시지
  const isConfirmed = confirm('게시물을 삭제하시겠습니까?');
  
  // 사용자가 '확인'을 누른 경우에만 삭제 진행
  if (isConfirmed) {
    try {
      const formData = new FormData();
      formData.append('active', false);  // 게시글을 비활성화 (삭제처럼)

<<<<<<< HEAD
      await use$Fetch(`/boards/${boardId}/inactive`, {
        method: 'PATCH',
        body: formData,
      });
      alert('게시글이 삭제되었습니다!');
      router.push('/boards/list');
      
    } catch (error) {
    console.error('Error:', error);
    alert(`게시글 삭제에 실패했습니다: ${error.message}`);
  }

=======
      const response = await fetch(`${config.public.apiBase}/boards/${boardId}/inactive`, {
        method: 'PATCH',
        body: formData,
      });

      if (response.ok) {
        const result = await response.json();
        console.log('게시글 삭제 성공:', result);
        alert('게시글이 삭제되었습니다!');
        router.push('/boards/list');  // 목록 페이지로 이동
      } else {
        const errorData = await response.json();
        throw new Error(errorData.message || '게시글 삭제에 실패했습니다.');
      }
    } catch (error) {
      console.error('Error:', error);
      alert(`게시글 삭제 중 오류가 발생했습니다: ${error.message}`);
    }
>>>>>>> d9dac12098eab459e1abd0a41c5305389a669a97
  } else {
    // 사용자가 취소를 눌렀을 경우
    console.log('삭제가 취소되었습니다.');
  }
};


const closeModal = () => {
  showModal.value = false;
  window.location.href = "http://localhost:3000/boards/list";
  // router.go(-1);
}

onMounted(async () => {
  await loadBoardData();
});
</script>

<template>
  <main class="board-container">
    <article class="board-detail">
      
      <section class="header">
      <div class="filter-tag" @click="closeModal">
        <span>{{ board.boardCategoryName }}</span>
      </div>
      <h1 class="post-title">{{ board.title }}</h1>

      <div class="user-section">
        <div class="user-avatar"></div>
          <div class="user-meta">
            <span class="user-name">{{ board.regMemberNickName }}</span>
            <span class="post-date">{{ formatDate(board.createdAt) }}</span>
          </div>
          <div class="actions-menu">
            <button class="menu-trigger" @click="toggleDropdown">
              <img src="/public/imgs/icon/3dot.svg" alt="Menu">
            </button>
            <div v-if="dropdownVisible" class="menu-dropdown">
              <button @click="handleEdit">수정</button>
              <button @click="handleDelete">삭제</button>
            </div>
          </div>
        </div>
      </section>
        
      <section class = "body">
        
        <div class="image-preview" id="imagePreview">
          <img v-if="imageUrl" :src="getImageUrl(imageUrl)" alt="미리보기 이미지">
        </div>
        
        <div class="post-content">{{ board.contents }}</div>
        
      </section>

      <section class="comments-section">
        <h2 class="comments-title">댓글</h2>
        <div class="comments-list"></div>
        <div class="comment-form">
          <input type="text" placeholder="댓글을 입력하세요."/>
        </div>
      </section>
    </article>
  </main>
</template>
