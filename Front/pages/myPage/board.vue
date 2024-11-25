<script setup>
import {use$Fetch} from "~/composables/use$Fetch.js";

useHead({
  link: [{ rel: "stylesheet", href: "/css/myBoard.css" }]
});

const userDetails = useUserDetails()
const boards = ref([])
const fetchBoards = async () => {
  const id = userDetails.id.value
  try {
    const response = await use$Fetch(`/user/member/${id}/boards`, {
      method: 'GET'
    })
    if (!response) {
      throw new Error('데이터 조회 실패')
    }

    console.log(response)

    boards.value = await Promise.all(
        response.map(async (board) => {
          const imageResponse = await use$Fetch(`/user/member/${board.id}/boardimg`, {
            method: 'GET',
            responseType: 'blob'
          });
          const imgObjectUrl = URL.createObjectURL(imageResponse);
          return {
            ...boards,
            showAlert: false,
            image: imgObjectUrl
          };
        })
    );
  } catch (error) {
    console.error(error)
  }


}
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
            <h3 class="title">{{item.title}}</h3>
            <div class="content-info">
              <span class="writer">{{ item.regMemberNickName}}</span>
              <span class="date">{{ item.createdAt}}</span>
<!--              <span class="views">조회</span>-->
            </div>
          </div>
        </div>

        <!-- 우측 이미지 영역 -->
        <div class="item-image" v-if="item.img">
          <img :src="item.img || 'https://via.placeholder.com/70'" alt="게시글 이미지" />
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