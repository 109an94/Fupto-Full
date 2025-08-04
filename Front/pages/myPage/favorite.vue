<script setup>
import {use$Fetch} from "~/composables/use$Fetch.js";
useHead({
  link: [{ rel: "stylesheet", href: "/css/myFavorite.css" }]
});

const userDetails = useUserDetails()
const products = ref([]);
const fetchMember = async () => {
  try {
    const id = userDetails.id.value;
    const response = await use$Fetch(`/user/member/${id}/fav`, {
      method: 'GET'
    });
    if (!response) {
      throw new Error('데이터 조회 실패');
    }
    console.log(response)
    // 각 제품마다 단일 이미지를 가지도록 매핑
    products.value = await Promise.all(
        response.map(async (product) => {
          const imageResponse = await use$Fetch(`/user/member/${product.productId}/favimg`, {
            method: 'GET',
            responseType: 'blob'
          });
          const imgObjectUrl = URL.createObjectURL(imageResponse);
          return {
            ...product,
            showAlert: false,
            image: imgObjectUrl
          };
        })
    );
  } catch (error) {
    console.error("데이터 조회 오류:", error);
  }
};

const updateAlertPrice = async (memberId, productId, alertPrice) => {
  try {
     await use$Fetch(`/user/member/${memberId}/fav/${productId}/alertPrice`, {
      method: 'POST',
      body: {
        alertPrice: alertPrice
      }
    })

    // 데이터 다시 불러오기
    await fetchMember();

    // 토글 닫기
    const item = products.value.find(item => item.productId === productId);
    if (item) {
      item.showAlert = false;
    }


    console.log('알림 가격이 업데이트 되었습니다.')
    alert('알림 가격이 업데이트 되었습니다.')
  } catch (error) {
    console.error('업데이트 실패 : ' + error);
  }
}

const toggleAlert = (id) => {
  const item = products.value.find(item => item.id === id)
  if (item) {
    item.showAlert = !item.showAlert
  }
}

const getTrendingIcon = (item) => {
  return item.price < item.alertPrice
      ? '/imgs/icon/fallchart.svg'
      : '/imgs/icon/basicchart.svg'
}
//-------lifecycle hooks--------
onMounted(() => {
  fetchMember()
});
</script>

<!-- templates -->
<template>
  <div class="favorites-container">
    <div class="product-item" v-for="item in products" :key="item.id">
      <!-- Product Info Section -->
      <div class="product-info" >

<!--        이미지-->
        <div class="product-image">
          <img :src="item.image" :alt="item.productName">
        </div>
<!--        디테일-->
        <div class="product-details">
          <h3>{{ item.productBrandName }}</h3>
            <p>{{ item.productName }}</p>
          <div class="price-info">
            <span class="price-icon">
              <img :src="getTrendingIcon(item)" alt="price trend">
            </span>
            <span>{{ item.productPrice }} ₩</span>
          </div>
          <div class="web-only">
            <input type="number"
                   v-model="item.alertPrice"
                   :alt = "item.alertPrice"
                   :placeholder="item.alertPrice ? item.alertPrice.toString() : '알림 받을 가격'">
          </div>

        </div>
        <div class="btn-box">
          <nuxt-link :to="`/products/${item.productId}/detail`">
            <button class="save-btn">이동</button>
          </nuxt-link>
          <button class="save-btn web-only" @click="updateAlertPrice(userDetails.id.value, item.productId, item.alertPrice)">
            변경</button>
        </div>
      </div>
      <!-- 모바일 알림 설정 -->
      <div class="alert-section mobile-only" v-show="item.showAlert">
        <div class="alert-input mobile-only">
          <div class="width80px">
            <div>알림</div>
          </div>
          <input type="number"
                 v-model="item.alertPrice"
                 :alt = "item.alertPrice"
                 :placeholder="item.alertPrice ? item.alertPrice.toString() : '알림 받을 가격'">
          <button class="save-btn"
          @click="updateAlertPrice(userDetails.id.value,
          item.productId,
          item.alertPrice)">변경</button>
        </div>
      </div>
<!--      드롭 다운 버튼-->
      <div class="dropdown">
        <button class="dropdown-btn mobile-only"
                :class="{ 'is-active': item.showAlert }"
                @click="toggleAlert(item.id)">
          <img src="/imgs/icon/arrow-forward.svg" alt="toggle">
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>