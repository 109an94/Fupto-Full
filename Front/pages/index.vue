<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";

useHead({
  link: [{ rel: "stylesheet", href: "/css/product-list.css" }],
});

// 슬라이드 데이터 설정
const slides = ref([
  { image: "/imgs/main/banner1.png", link: "https://www.farfetch.com/kr/promotions/women/farfetch-event?clickref=1101lzXLEmGH&utm_source=reversible&utm_medium=affiliate&utm_campaign=PHUS&pid=performancehorizon_int&c=PHUS&clickid=1101lzXLEmGH&af_siteid=reversible&af_cost_model=CPA&af_channel=affiliate&is_retargeting=true" },
  { image: "/imgs/main/banner2.png", link: "https://us.wananluxury.com/en-kr/collections/black-friday-weeks?utm_source=rakuten&utm_medium=affiliate&utm_campaign=3748803&utm_content=10&utm_term=itnetwork&ranMID=47153&ranEAID=ewGjQBZZVAk&ranSiteID=ewGjQBZZVAk-hok0fZ62_FWSr_wwSyaERA" },
  { image: "/imgs/main/banner3.png", link: "https://www.luisaviaroma.com/en-it/shop/women/new-in-this-week?lvrid=_gw_tnewinthisw&cjdata=MXxOfDB8WXww&%243p=a_cj_affiliate&%7Eclick_id=f749fa5caa5b11ef838c01b20a18ba74&%7Esecondary_publisher=100461930&%24canonical_url=https%3A%2F%2Fwww.luisaviaroma.com%2Fen-it%2Fshop%2Fwomen%2Fnew-in-this-week%3Flvrid%3D_gw_tnewinthisw&AID=11554101&PID=100461930&SID=19073a1d7a7752-0e32e47816ba3-26001f51-1fa400-19073a1d7a7753-0--web***GCI-1978294934.1719926908&utm_source=CommissionJunction&utm_medium=affiliate&utm_content=100461930&utm_campaign=5891754&CJEVENT=f749fa5caa5b11ef838c01b20a18ba74&click_id=f749fa5caa5b11ef838c01b20a18ba74&_branch_match_id=1163717674500431091&_branch_referrer=H4sIAAAAAAAAA71STWvcMBD9NfbNXsmSLbtgynY3CQmkLbQ0uZmxLK2VlSUjyev2kt9eeQv9OJVeCgOPmTfM15sxhNm%2F2e30xeUwz7lW5rw7DefL491TpaF%2Fy18GCNA%2BPn%2F9II%2Fv6qfndU2TgpK5hY6%2FdCCl0gqCSF%2B5VvzcqaGVjDYSSg5Q9hgLWZOaI9wXCHDdA6PpqxfcmgHct25eeq38KFyLEaIVbgjaykvQuodYbnG6HbcZE7JPitto67rmelEeLgqcnSDndophYTIVIvrRzluWnYSJaMSaKZOFUflsFeKckNu4qhoScuxOaxcir8zGXrfiYKxRHPT%2F7Lu%2FP7YYlyXFCKcfN%2BfnJT5tXoMYATwwYKwsMiRIISircdUDyYoKISxLnGEJFKHsj2SSoSw275Ni%2F8PuDvcxg9VFQxtCc8xw0xRVg%2Bp0CVPn7eK4aA92mpT3ypqHxfAQ8cpOYlDL1P7SewtGFYMw4beJr1GYZlAn05Z1g1lJ08PDzZeb95%2F%2F%2Fhj%2F8EJOSOGcMqeud3b18YEOY5RFfAeIZwDA0QIAAA%3D%3D" },
  { image: "/imgs/main/banner4.png", link: "https://www.italist.com/us/women/?seasons%5B%5D=FW24&on_sale=1"}
]);

const currentSlide = ref(0);
const slideInterval = 5000; // 슬라이드 전환 간격 (밀리초)

// 슬라이드 쇼 시작
const startSlideShow = () => {
  setInterval(() => {
    currentSlide.value = (currentSlide.value + 1) % slides.value.length;
  }, slideInterval);
};

// 슬라이드 이동
const goToSlide = (index) => {
  currentSlide.value = index;
};

const route = useRoute();
const products = ref([]);
const designers = ref([]);
const loadingProducts = ref(false);
const loadingBrands = ref(false);

// 브랜드 및 제품 데이터 가져오기
const fetchBrandsAndProducts = async () => {
  loadingBrands.value = true;
  loadingProducts.value = true;

  try {
    // 브랜드 데이터 가져오기
    const brandResponse = await use$Fetch(`/brands`, {
      params: {
        brand: route.query.brand ? route.query.brand.split(",") : undefined,
      },
    });

    if (brandResponse) {
      designers.value = brandResponse; // 브랜드 응답 데이터 저장
    }

    // 제품 데이터 가져오기
    const { data: initialData } = await useAuthFetch("/products", {
      params: {
        cursor: null,
        limit: 100,
      },
    });

    if (initialData.value) {
      products.value = initialData.value.products; // 제품 응답 데이터 저장
    }
  } catch (error) {
    console.error("Error fetching brands or products:", error);
  } finally {
    loadingBrands.value = false; // 브랜드 로딩 종료
    loadingProducts.value = false; // 제품 로딩 종료
  }
};

// 찜 관련 함수
// const { toggleFavorite: toggleFavoriteAction } = useFavorite();

// const toggleFavorite = async (event, product) => {
//   event.preventDefault();
//   const success = await toggleFavoriteAction(product.mappingId);

//   if (success) {
//     product.isFavorite = !product.isFavorite;
//   }
// };

// 컴포넌트가 마운트될 때 데이터 가져오기
onMounted(() => {
  startSlideShow(); // 슬라이드 쇼 시작
  fetchBrandsAndProducts(); // 브랜드 및 제품 데이터 가져오기
});
</script>

<template>
  <div id="app" class="main-container">
    <!-- 배너 섹션 -->
    <div class="banner">
      <div class="banner-slides" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
        <div v-for="(slide, index) in slides" :key="index" class="banner-slide">
          <a :href="slide.link" target="_blank">
            <img :src="slide.image" :alt="`Banner ${index + 1}`">
          </a>
        </div>
      </div>
      <!-- 페이지 인디케이터 -->
      <div class="banner-indicators">
        <span
          v-for="(slide, index) in slides"
          :key="index"
          class="indicator"
          :class="{ active: index === currentSlide }"
          @click="goToSlide(index)"
        ></span>
      </div>
    </div>

    <!-- 상품 섹션 -->
    <section class="product-p-frame">
      <div class="section-header">
        <h2>WHAT'S TRENDING NOW</h2>
        <div class="user-product-list">
          <ul class="product-grids">
            <template v-if="products.length > 0">
              <li
                v-for="(product, index) in products"
                :key="product.id"
                class="product-c-frame"
              >
                <nuxt-link :to="`/products/${product.id}/detail`">
                  <div class="product-img-frame">
                    <div class="product-img-container">
                      <img class="product-images primary-img" :src="product.mainImageUrl" alt="product-img" />
                      <img class="product-images secondary-img" :src="product.hoverImageUrl" alt="product-img-hover" />
                    </div>
                    <!-- <button
                      :data-favorite="product.isFavorite"
                      @click.prevent="(e) => toggleFavorite(e, product)"
                      class="favorite-btn"
                    >
                      <img
                        class="favorite"
                        :src="product.isFavorite ? '/imgs/icon/favorite-fill.svg' : '/imgs/icon/favorite.svg'"
                        :alt="product.isFavorite ? '찜' : '찜 해제'"
                      />
                    </button> -->
                  </div>
                  <div class="product-info">
                    <span class="product-brand">{{ product.brandEngName }}</span>
                    <span class="product-name">{{ product.productName }}</span>
                    <div class="price-info">
                      <span class="product-price">{{ product.salePrice.toLocaleString() }}</span>
                      <span class="price-unit">원</span>
                    </div>
                  </div>
                </nuxt-link>
              </li>
            </template>
            <template v-else>
              <li class="empty-list">
                <template v-if="loadingProducts">
                  <div class="loading">Loading...</div>
                </template>
                <template v-else> 상품이 없습니다. </template>
              </li>
            </template>
          </ul>
        </div>
      </div>
    </section>

    <!-- FEATURED DESIGNERS 섹션 -->
    <div class="featured-designers">
      <h2>FEATURED DESIGNERS</h2>
      <p>From luxury powerhouses to emerging designers, experience the creativity and diversity of over 500 brands on FUPTO.</p>

      <!-- 브랜드 그리드 -->
      <div class="brand-grid">
        <div v-for="brand in designers" :key="brand.id" class="brand-card">
          <NuxtLink :to="`/brands/${brand.id}/detail`">
            <div class="brand-logo">
              <img :src="`http://localhost:8085/api/v1/${brand.img}`" :alt="brand.korName" />
              <p>{{ brand.engName }}</p>
            </div>
          </NuxtLink>
        </div>
      </div>

      
    </div>

  
  <div class="watching-section">
    <div class="image-container">
      <img src="/imgs/main/mytheresa.jpg" alt="Mytheresa Tag" />
    </div>
    <div class="text-container">
      <h1 class="title">GIVE YOUR CLOSET A NEW LIFE</h1>
      <p class="description">
        Refresh your wardrobe with exclusive designs from top-tier designers. 
        Let go of those deep sale purchases that no longer serve you, and discover curated luxury fashion
      </p>
      <a href="https://www.pentagram.com/work/mytheresa" target="_blank" class="cta-button">
        WATCH
      </a>
    </div>
  </div>

  </div>
</template>

<style>
/* 메인 컨테이너 */
.main-container {
  font-family: Arial, sans-serif;
  color: #333;
}

/* 배너 섹션 */
.banner {
  position: relative;
  width: 100%;
  height: 400px; /* 원하는 배너 높이 설정 */
  overflow: hidden;
}

.banner-slides {
  display: flex;
  height: 100%;
  width: 100%;
  transition: transform 0.5s ease-in-out;
}

.banner-slide {
  flex: 0 0 100%;
  height: 100%; /* 배너의 전체 높이를 차지 */
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f0f0;
}

.banner-slide img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 이미지가 영역을 꽉 채우도록 설정 */
  object-position: center;
}

/* 배너 인디케이터 */
.banner-indicators {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
}

.indicator {
  width: 10px;
  height: 10px;
  background-color: white;
  border-radius: 50%;
  opacity: 0.5;
  cursor: pointer;
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.indicator.active {
  opacity: 1;
  transform: scale(1.2);
}

/* 상품 섹션 */
.products {
  padding: 20px;
  background-color: #f9f9f9;
}

.section-header {
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 20px 0 10px;
  font-size: 28px;
  font-weight: bold;
  color: #333;
  text-align: left;
  text-transform: uppercase;
  letter-spacing: 1px;
  line-height: 1.4;
  border-bottom: 2px solid #e0e0e0;
  padding-bottom: 10px;
  transition: color 0.3s ease;
}

.tags {
  display: flex;
  gap: 10px;
}

.tag {
  padding: 5px 15px;
  border: 1px solid #ccc;
  background-color: white;
  border-radius: 15px;
  cursor: pointer;
  font-size: 14px;
}

.product-grids {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.product-card img {
  width: 100%;
  border-radius: 10px;
  transition: transform 0.3s ease;
}

.product-card img:hover {
  transform: scale(1.05);
}

/* FEATURED DESIGNERS 스타일링 */
/* FEATURED DESIGNERS 스타일링 */
.featured-designers {
  text-align: center;
  margin: 50px 0;
  background-color: #f9f9f9; /* 밝은 회색 배경 */
  border-radius: 8px; /* 모서리를 둥글게 */
  padding: 30px; /* 내부 여백 추가 */
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); /* 부드러운 그림자 효과 */
}

.featured-designers h2 {
  font-size: 28px; /* 제목 크기 조정 */
  font-weight: bold; /* 제목 두께 조정 */
  color: #333; /* 제목 색상 */
  margin-bottom: 10px; /* 제목 아래 여백 */
}

.featured-designers p {
  font-size: 16px; /* 설명 텍스트 크기 조정 */
  color: #666; /* 설명 텍스트 색상 */
  line-height: 1.5; /* 줄 높이 조정 */
  margin-bottom: 20px;
}

.brand-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); /* 브랜드 카드 그리드 설정 */
  gap: 20px; /* 카드 간격 설정 */
}

.brand-card {
  background-color: #fff; /* 카드 배경색 흰색 */
  border-radius: 5px; /* 카드 모서리 둥글게 */
  padding: 15px; /* 카드 내부 여백 */
  box-shadow: 0 2px 10px rgba(0,0,0,0.1); /* 카드 그림자 효과 */
}

.brand-logo img {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  background-color: #f5f5f5;
}

.watching-section {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.image-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.image-container img {
  max-width: 100%;
  height: auto;
  border-radius: 5px;
}

.text-container {
  flex: 1;
  padding: 0 20px;
  text-align: center;
}

.title {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 20px;
}

.description {
  font-size: 1rem;
  color: #666;
  margin-bottom: 20px;
}

.cta-button {
  background-color: #000;
  color: #fff;
  border: none;
  border-radius: 5px;
  padding: 10px 20px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: bold;
}

.cta-button:hover {
  background-color: #444;
}
</style>
