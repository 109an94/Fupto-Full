<script setup>
import { ref, onMounted, watch } from "vue";

useHead({
  link: [{ rel: "stylesheet", href: "/css/product-list.css" }],
});

const slides = ref([
  { image: "/imgs/main/banner1.png", link: "https://www.farfetch.com/kr/promotions/women/farfetch-event?clickref=1101lzXLEmGH&utm_source=reversible&utm_medium=affiliate&utm_campaign=PHUS&pid=performancehorizon_int&c=PHUS&clickid=1101lzXLEmGH&af_siteid=reversible&af_cost_model=CPA&af_channel=affiliate&is_retargeting=true" },
  { image: "/imgs/main/banner2.png", link: "https://us.wananluxury.com/en-kr/collections/black-friday-weeks?utm_source=rakuten&utm_medium=affiliate&utm_campaign=3748803&utm_content=10&utm_term=itnetwork&ranMID=47153&ranEAID=ewGjQBZZVAk&ranSiteID=ewGjQBZZVAk-hok0fZ62_FWSr_wwSyaERA" },
  { image: "/imgs/main/banner3.png", link: "https://www.luisaviaroma.com/en-it/shop/women/new-in-this-week?lvrid=_gw_tnewinthisw&cjdata=MXxOfDB8WXww&%243p=a_cj_affiliate&%7Eclick_id=f749fa5caa5b11ef838c01b20a18ba74&%7Esecondary_publisher=100461930&%24canonical_url=https%3A%2F%2Fwww.luisaviaroma.com%2Fen-it%2Fshop%2Fwomen%2Fnew-in-this-week%3Flvrid%3D_gw_tnewinthisw&AID=11554101&PID=100461930&SID=19073a1d7a7752-0e32e47816ba3-26001f51-1fa400-19073a1d7a7753-0--web***GCI-1978294934.1719926908&utm_source=CommissionJunction&utm_medium=affiliate&utm_content=100461930&utm_campaign=5891754&CJEVENT=f749fa5caa5b11ef838c01b20a18ba74&click_id=f749fa5caa5b11ef838c01b20a18ba74&_branch_match_id=1163717674500431091&_branch_referrer=H4sIAAAAAAAAA71STWvcMBD9NfbNXsmSLbtgynY3CQmkLbQ0uZmxLK2VlSUjyev2kt9eeQv9OJVeCgOPmTfM15sxhNm%2F2e30xeUwz7lW5rw7DefL491TpaF%2Fy18GCNA%2BPn%2F9II%2Fv6qfndU2TgpK5hY6%2FdCCl0gqCSF%2B5VvzcqaGVjDYSSg5Q9hgLWZOaI9wXCHDdA6PpqxfcmgHct25eeq38KFyLEaIVbgjaykvQuodYbnG6HbcZE7JPitto67rmelEeLgqcnSDndophYTIVIvrRzluWnYSJaMSaKZOFUflsFeKckNu4qhoScuxOaxcir8zGXrfiYKxRHPT%2F7Lu%2FP7YYlyXFCKcfN%2BfnJT5tXoMYATwwYKwsMiRIISircdUDyYoKISxLnGEJFKHsj2SSoSw275Ni%2F8PuDvcxg9VFQxtCc8xw0xRVg%2Bp0CVPn7eK4aA92mpT3ypqHxfAQ8cpOYlDL1P7SewtGFYMw4beJr1GYZlAn05Z1g1lJ08PDzZeb95%2F%2F%2Fhj%2F8EJOSOGcMqeud3b18YEOY5RFfAeIZwDA0QIAAA%3D%3D" },
]);

const currentSlide = ref(0);

const slideInterval = 5000; // 슬라이드 전환 간격 (밀리초)

const startSlideShow = () => {
  setInterval(() => {
    currentSlide.value = (currentSlide.value + 1) % slides.value.length;
  }, slideInterval);
};

const goToSlide = (index) => {
  currentSlide.value = index;
};

const route = useRoute();
const isActive = ref(false);
const products = ref([]);
const loading = ref(false);
const hasMore = ref(true);
const cursor = ref(null);
const designers = ref([]);

const fetchBrands = async () => {
  const config = useRuntimeConfig(); // 런타임 환경 설정
  try {
    const response = await use$Fetch(`/brands`, {
      params: {
        brand: route.query.brand ? route.query.brand.split(",") : undefined, // query 파라미터 전달
      },
    });

    if (response) {
      designers.value = response; // 응답 데이터를 designers에 저장
    }
  } catch (error) {
    console.error("Error fetching brands:", error);
  }
};


const { data: initialData } = await useAuthFetch("/products", {
  params: {
    cursor: null,
    limit: 100,
  },
});

if (initialData.value) {
  products.value = initialData.value.products;
  cursor.value = initialData.value.nextCursor;
  hasMore.value = initialData.value.hasMore;

  if (route.query.category && route.query.categoryName) {
    selectedFilters.value.category = [
      {
        id: route.query.category,
        name: route.query.categoryName,
      },
    ];
  }

  if (route.query.sub && route.query.subName) {
    selectedFilters.value.sub = [
      {
        id: route.query.sub,
        name: route.query.subName,
      },
    ];
  }
}

const loadProducts = async (reset = false) => {
  if (loading.value) return;
  if (!reset && !hasMore.value) return;

  loading.value = true;

  try {
    const params = {
      cursor: reset ? null : cursor.value,
      limit: 100,
    };

    const data = await use$Fetch("/products", {
      params,
    });

    if (reset) {
      products.value = [];
      cursor.value = null;
    }

    if (data?.products?.length) {
      if (reset) {
        products.value = data.products;
      } else {
        const existingIds = new Set(products.value.map((p) => p.id));
        const newProducts = data.products.filter((p) => !existingIds.has(p.id));
        products.value = [...products.value, ...newProducts];
      }
      cursor.value = data.nextCursor;
      hasMore.value = data.hasMore;
    } else {
      hasMore.value = false;
    }
  } catch (error) {
    console.error("Failed to load products:", error);
    hasMore.value = false;
  } finally {
    loading.value = false;
  }
};

const { toggleFavorite: toggleFavoriteAction } = useFavorite();

// 찜 관련 함수
const toggleFavorite = async (event, product) => {
  event.preventDefault();
  const success = await toggleFavoriteAction(product.mappingId);

  if (success) {
    product.isFavorite = !product.isFavorite;
  }
};

// 무한 스크롤
let observer;
const lastProductRef = ref(null);

const setupIntersectionObserver = () => {
  if (observer) {
    observer.disconnect();
  }

  observer = new IntersectionObserver(
    async (entries) => {
      const entry = entries[0];
      // 로딩 중이 아니고, 더 불러올 데이터가 있을 때만 실행
      if (entry.isIntersecting && !loading.value && hasMore.value) {
        await loadProducts();
      }
    },
    {
      rootMargin: "150px",
      threshold: 0.1,
    }
  );
};

const updateObserver = () => {
  if (!observer) return;

  observer.disconnect();

  const lastProduct = document.querySelector(".product-c-frame:last-child");
  if (lastProduct && hasMore.value) {
    observer.observe(lastProduct);
  }
};

watch(
  () => products.value,
  () => {
    nextTick(() => {
      if (!loading.value) {
        updateObserver();
      }
    });
  },
  { deep: true }
);

// 라우트 쿼리 변경 시 fetchBrands 호출
watch(() => route.query.brand, (newVal, oldVal) => {
  if (newVal !== oldVal) {
    fetchBrands(); // 쿼리가 변경될 때마다 호출
  }
});

const handleClickOutside = (e) => {
  if (!e.target.closest(".filter-select")) {
    isActive.value = false;
  }
};

onMounted(() => {
  startSlideShow();
  setupIntersectionObserver();
  fetchBrands();
  nextTick(() => {
    updateObserver();
  });
  document.addEventListener("click", handleClickOutside);
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
                  :ref="index === products.length - 1 ? lastProductRef : undefined"
                >
                  <nuxt-link :to="`/products/${product.id}/detail`">
                    <div class="product-img-frame">
                      <div class="product-img-container">
                        <img class="product-images primary-img" :src="product.mainImageUrl" alt="product-img" />
                        <img class="product-images secondary-img" :src="product.hoverImageUrl" alt="product-img-hover" />
                      </div>
                      <button
                        :data-favorite="product.isFavorite"
                        @click.prevent="(e) => toggleFavorite(e, product)"
                        class="favorite-btn"
                      >
                        <img
                          class="favorite"
                          :src="product.isFavorite ? '/imgs/icon/favorite-fill.svg' : '/imgs/icon/favorite.svg'"
                          :alt="product.isFavorite ? '찜' : '찜 해제'"
                        />
                      </button>
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
                  <template v-if="loading">
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
      <p>
        From luxury powerhouses to emerging designers, experience the creativity
        and diversity of over 500 brands on FUPTO.
      </p>
      <template>
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
      </template>
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
  margin: 0;
  font-size: 24px;
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
.featured-designers {
  text-align: center;
  margin: 50px 0;
  background-color: #f0f0f0;
}

.featured-designers h2 {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.featured-designers p {
  color: #666;
  font-size: 14px;
  margin-bottom: 30px;
}

.brand-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
  background-color: #f0f0f0;
}

.brand-card {
  width: 100px;
  text-align: center;
}

.brand-logo img {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  background-color: #f5f5f5;
}
</style>
