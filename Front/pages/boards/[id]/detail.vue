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
const handleDelete = async (boardId, active) => {
  const confirmDeactivate = confirm("이 게시글을 비활성화하시겠습니까?");
  if (confirmDeactivate) {
    try {
      const response = await fetch(`${config.public.apiBase}/admin/boards/${boardId}/active`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ active: false })
      });
      
      if (response.ok) {
        alert("게시글이 비활성화되었습니다.");
        await loadBoardData(); // 게시글 데이터를 다시 불러와 화면을 갱신합니다.
        // 또는 router.push('/boards/list'); // 게시글 목록 페이지로 이동
      } else {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
    } catch (error) {
      console.error("게시글 비활성화 실패:", error);
      alert(`게시글 비활성화에 실패했습니다: ${error.message}`);
    }
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
        
      <hr>

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
