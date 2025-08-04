<script setup>
import {use$Fetch} from "~/composables/use$Fetch.js";

useHead({
  link: [{ rel: "stylesheet", href: "/css/myBoard.css" }]
});

const userDetails = useUserDetails()
const boards = ref([])
const fetchBoards = async () => {
  try {
    const id = userDetails.id.value
    const response = await use$Fetch(`/user/member/${id}/boards`, {
      method: 'GET'
    })
    if (!response) {
      throw new Error('데이터 조회 실패')
    }
    console.log(response)
    // 보드 데이터를 먼저 설정
    boards.value = response.map(board => ({
      ...board,
      showAlert: false,
      image: null // 초기 이미지 값을 null로 설정
    }));

    // 이미지 데이터를 별도로 가져오기
    await Promise.all(
        boards.value.map(async (board) => {
          if (board.img) {
            try {
              const imageResponse = await use$Fetch(`/user/member/${board.id}/boardimg`, {
                method: 'GET',
                responseType: 'blob'
              });
              board.image = URL.createObjectURL(imageResponse);
            } catch (imageError) {
              console.error(`이미지 로드 실패 (ID: ${board.id}):`, imageError);
              board.image = null; // 오류 발생 시 이미지를 null로 유지
            }
          }
        })
    );
console.log(boards)
  } catch (error) {
    console.error(error);
  }
}

  console.log(boards.value);
await fetchBoards();
onMounted(()=>{
  fetchBoards()
})

</script>

<template>
  <div class="board-container">
    <div class="board-list">
      <div class="board-item" v-for="item in boards" :key="item.id">
        <!-- 좌측 번호 영역 -->
        <div class="item-number">{{ item.id }}</div>

        <!-- 중앙 컨텐츠 영역 -->
        <div class="item-content">
          <div class="content-main">
            <nuxt-link :to="`/boards/${item.id}/detail`">
              <h3 class="title">{{item.title}}</h3>
            </nuxt-link>
            <div class="content-info">
              <span class="writer">{{ item.regMemberNickName}}</span>
              <span class="date">{{ item.createdAt}}</span>
<!--              <span class="views">조회</span>-->
            </div>
          </div>
        </div>

        <!-- 우측 이미지 영역 -->
        <div class="item-image" v-if="item.img">
          <img :src="item.image || 'https://via.placeholder.com/70'" alt="게시글 이미지" />
        </div>

        <!-- 댓글 카운트 영역 -->
<!--        <div class="item-comments">-->
<!--          <span class="comment-count">댓글 22</span>-->
<!--        </div>-->
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>