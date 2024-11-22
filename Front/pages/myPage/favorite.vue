
<!-- script -->
<script setup>
useHead({
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
      <div class="product-info" @click="toggleAlert(item.id)">
        <div class="product-image">
          <img :src="item.image" :alt="item.name">
        </div>
        <div class="product-details">
          <h3>{{ item.brand }}</h3>
          <p>{{ item.name }}</p>
          <div class="price-info">
            <span class="price-icon">*</span>
            <span>{{ item.price }}₩</span>
          </div>
        </div>
        <button class="toggle-btn">*</button>
      </div>

      <!-- Alert Price Section -->
      <div class="alert-section" v-show="item.showAlert">
        <div class="alert-input">
          <input
              type="number"
              v-model="item.alertPrice"
              placeholder="알림 설정 가격"
          >
          <button class="save-btn">변경</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>