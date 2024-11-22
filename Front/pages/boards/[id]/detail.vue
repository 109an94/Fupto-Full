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
});

const config = useRuntimeConfig();
const route = useRoute();
const router = useRouter();
const boardId = route.params.id;

const imageUrl = ref('');
const dropdownVisible = ref(false); // 드롭다운 메뉴 상태

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
  const confirmDelete = confirm("정말로 이 게시글을 삭제하시겠습니까?");
  if (confirmDelete) {
    try {
      const response = await fetch(`${config.public.apiBase}/boards/${boardId}`, {
        method: 'DELETE',
      });
      if (response.ok) {
        alert("게시글이 삭제되었습니다.");
        router.push('/boards/list'); // 게시글 목록 페이지로 이동
      } else {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
    } catch (error) {
      console.error("게시글 삭제 실패:", error);
      alert(`게시글 삭제에 실패했습니다: ${error.message}`);
    }
  }
};

onMounted(async () => {
  await loadBoardData();
});
</script>

<template>
  <main>
    <!-- 단일 board 객체를 표시 -->
    <div>
      <div class="boardcategory">
        {{ board.boardCategoryName }}
        <input type="button" value="X">
      </div>
      <section class="user">
        <div class="userimg"></div>
        <div class="userinfo">
          <span class="username">{{ board.regMemberNickName }}</span>
          <span class="date">{{ board.createdAt }}</span>
          <!-- <span class="date">{{ board.modifiedAt }}</span> -->
        </div>
        <button class="edit" @click="toggleDropdown">
          <img src="/public/imgs/icon/3dot.svg" alt="Edit Menu">
        </button>
        <!-- 드롭다운 메뉴 -->
        <div v-if="dropdownVisible" class="dropdown-menu">
          <button @click="handleEdit">수정</button>
          <button @click="handleDelete">삭제</button>
        </div>
      </section>
      <section class="body">
        <div class="title">{{ board.title }}</div>
        <div class="contents">{{ board.contents }}</div>
        <div class="img">
          <img v-if="imageUrl" :src="getImageUrl(board.img)" alt="Board image" />
        </div>
        <div class="like">like</div>
      </section>
      <hr />
      <div class="comments">comments</div>
      <input class="commentsbox" placeholder="Please leave a comment" />
    </div>
  </main>
</template>