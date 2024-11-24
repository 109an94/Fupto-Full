<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/board-detail.css" }],
});

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

const imageUrl = ref('');
const dropdownVisible = ref(false);
const showModal = ref(false);

// 날짜 형식을 'yyyy-mm-dd'로 포맷팅하는 함수
const formatDate = (date) => {
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
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
    const response = await fetch(`${config.public.apiBase}/boards/${boardId}/detail`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    board.value = {
      title: data.title,
      contents: data.contents,
      boardCategoryName: data.boardCategoryName,
      regMemberNickName: data.regMemberNickName,
      createdAt: formatDate(data.createdAt),
      modifiedAt: formatDate(data.modifiedAt),
      active: data.active,
      img: data.img, // 이미지 URL을 board 객체에 포함시킴
    };
    imageUrl.value = data.img ? (data.img.startsWith('/') ? data.img : '/' + data.img) : '';
  } catch (error) {
    console.error("Error loading board:", error);
    alert(`게시글을 불러오는데 실패했습니다: ${error.message}`);
  }
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
            <span class="post-date">{{ board.createdAt }}</span>
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
