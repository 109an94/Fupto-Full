
<!-- script -->
<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/headers.css" }],
  link: [{ rel: "stylesheet", href: "/css/myLayout.css" }],
  link: [{ rel: "stylesheet", href: "/css/myFavorite.css" }]
});
const favorites = ref([
  {
    id: 1,
    brand: 'Thom Browne',
    name: '4-Bar cotton cropped trousers',
    price: 370000,
    alertPrice: '',
    showAlert: false,
    image: 'https://img.ssensemedia.com/images/b_white,c_lpad,g_south,h_1086,w_724/c_scale,h_480,f_auto,q_auto/222495F070000_1/navy-stand-collar-jumpsuit.jpg'
  },
  {
    id: 2,
    brand: 'Thom Browne',
    name: '4-Bar cotton cropped trousers 만약 이름이 길어지면?',
    price: 370000,
    alertPrice: '',
    showAlert: false,
    image: 'https://img.ssensemedia.com/images/b_white,c_lpad,g_south,h_1086,w_724/c_scale,h_480,f_auto,q_auto/222495F070000_1/navy-stand-collar-jumpsuit.jpg'
  },
  {
    id: 3,
    brand: 'Thom Browne',
    name: '4-Bar cotton cropped trousers',
    price: 370000,
    alertPrice: '',
    showAlert: false,
    image: 'https://img.ssensemedia.com/images/b_white,c_lpad,g_south,h_1086,w_724/c_scale,h_480,f_auto,q_auto/222495F070000_1/navy-stand-collar-jumpsuit.jpg'
  },
  {
    id: 4,
    brand: 'Thom Browne',
    name: '4-Bar cotton cropped trousers',
    price: 370000,
    alertPrice: '',
    showAlert: false,
    image: 'https://img.ssensemedia.com/images/b_white,c_lpad,g_south,h_1086,w_724/c_scale,h_480,f_auto,q_auto/222495F070000_1/navy-stand-collar-jumpsuit.jpg'
  }
  // ... more items
])

const toggleAlert = (id) => {
  const item = favorites.value.find(item => item.id === id)
  if (item) {
    item.showAlert = !item.showAlert
  }
}
</script>

<!-- templates -->
<template>
  <div class="favorites-container">
    <div class="product-item" v-for="item in favorites" :key="item.id">
      <!-- Product Info Section -->
      <div class="product-info" >

<!--        이미지-->
        <div class="product-image">
          <img :src="item.image" :alt="item.name">
        </div>
<!--        디테일-->
        <div class="product-details">
          <h3>{{ item.brand }}</h3>
            <p>{{ item.name }}</p>
          <div class="price-info">
            <span class="price-icon">*</span>
            <span>{{ item.price }}₩</span>
          </div>
          <div class="web-only">
            <input type="number"
                   v-model="item.alertPrice"
                   placeholder="알림 설정 가격">
          </div>

        </div>
        <div class="btn-box">
          <button class="save-btn">이동</button>
          <button class="save-btn web-only">변경</button>
        </div>
      </div>
      <!-- 모바일 알림 설정 -->
      <div class="alert-section mobile-only" v-show="item.showAlert">
        <div class="alert-input mobile-only">
          <div class="width80px"></div>
          <div>알림</div>
          <input type="number"
                 v-model="item.alertPrice"
                 placeholder="알림 받을 가격 입력">
          <button class="save-btn">변경</button>
        </div>
      </div>
<!--      드롭 다운 버튼-->
      <div class="dropdown">
        <button class="dropdown-btn mobile-only"
                :class="{ 'is-active': item.showAlert }"
                @click="toggleAlert(item.id)">
          <img src="public/imgs/icon/arrow-forward.svg" alt="toggle">
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>