<script setup>
import { ref, onMounted, onUnmounted } from "vue";

useHead({
  link: [{ rel: "stylesheet", href: "/css/product-detail.css" }],
});

const currentSlide = ref(0);
const isVendorListOpen = ref(true);
const isDescriptionOpen = ref(true);
const isFooterVisible = ref(false);

const route = useRoute();
const router = useRouter();
const productId = route.params.id;

const config = useRuntimeConfig();

const { data: product } = await useFetch(`${config.public.apiBase}/products/${productId}`, {
  key: `product-${productId}`,
});

// 슬라이더 관련 함수들
const goToSlide = (index) => {
  currentSlide.value = index;
};

const prevSlide = () => {
  if (currentSlide.value > 0) {
    currentSlide.value--;
  }
};

const nextSlide = () => {
  if (currentSlide.value < (product.value?.images?.length || 0) - 1) {
    currentSlide.value++;
  }
};

// 기존 토글 함수들
const toggleVendorList = () => {
  isVendorListOpen.value = !isVendorListOpen.value;
};

const toggleDescription = () => {
  isDescriptionOpen.value = !isDescriptionOpen.value;
};

const handleScroll = () => {
  if (window.scrollY > 100) {
    isFooterVisible.value = true;
  } else {
    isFooterVisible.value = false;
  }
};

const navigateToRoute = (path) => {
  router.push(path);
};

onMounted(() => {
  window.addEventListener("scroll", handleScroll);
});

onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
});
</script>

<template>
  <main class="product product-detail-page">
    <nav class="breadcrumb">
      <ol>
        <li v-for="category in product?.categories" :key="category.id">
          <a href="#">{{ category.name }}</a>
        </li>
      </ol>
    </nav>

    <div class="product-container">
      <div class="left-column">
        <section class="product-images">
          <div class="slider-container" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
            <div v-for="image in product?.images" :key="image.id" class="slide">
              <img :src="image.imageUrl" :alt="product?.productName" />
            </div>
          </div>
          <div class="slider-nav">
            <button class="prev" @click="prevSlide">‹</button>
            <button class="next" @click="nextSlide">›</button>
          </div>
          <div class="slider-dots">
            <div
              v-for="(image, index) in product?.images"
              :key="image.id"
              class="dot"
              :class="{ active: currentSlide === index }"
              @click="goToSlide(index)"
            ></div>
          </div>
        </section>

        <div class="description-section">
          <div class="section-header" @click="toggleDescription">
            <span>상품 설명</span>
            <svg class="arrow" :class="{ up: isDescriptionOpen }" viewBox="0 0 24 24" fill="none">
              <path d="M7 10L12 15L17 10" stroke="currentColor" stroke-width="2" />
            </svg>
          </div>
          <div class="section-content" :class="{ open: isDescriptionOpen }">
            <div class="description">
              <p>{{ product?.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="product-info">
        <section class="header">
          <p class="brand">{{ product?.brandEngName }}</p>
          <h1 class="title">{{ product?.productName }}</h1>
        </section>

        <section class="price-info">
          <p class="retail-price">{{ product?.priceInfo.retailPrice?.toLocaleString() }} ￦</p>
          <p>
            <span class="discount">{{ product?.priceInfo.discountRate }}%</span>
            <span class="sale-price">{{ product?.priceInfo.salePrice?.toLocaleString() }} ￦</span>
          </p>
          <div class="additional-costs">
            <span>+ 관세 8%</span>
            <span>+ 부가세 10%</span>
            <span>+ 배송비 ~27$</span>
          </div>
        </section>

        <section class="vendor-section">
          <div class="section-header" @click="toggleVendorList">
            <span>모든 할인 상품 둘러보기</span>
            <svg class="arrow" :class="{ up: isVendorListOpen }" viewBox="0 0 24 24" fill="none">
              <path d="M7 10L12 15L17 10" stroke="currentColor" stroke-width="2" />
            </svg>
          </div>
          <div class="section-content" :class="{ open: isVendorListOpen }">
            <div class="vendor-list">
              <article v-for="shop in product?.shops" :key="shop.id" class="vendor-card">
                <div class="background-area" @click="navigateToRoute(`/products/${shop.productId}/detail`)"></div>
                <div class="vendor-logo" @click.stop>로고</div>
                <div class="name-wrapper" @click.stop>
                  <span class="vendor-name">{{ shop.shopName }}</span>
                </div>
                <div class="vendor-price">
                  <p>{{ shop.price?.toLocaleString() }} ￦</p>
                  <a :href="shop.productUrl" class="link" target="_blank" @click.stop>링크</a>
                </div>
              </article>
            </div>
          </div>
        </section>

        <!-- 모바일용 description 섹션 -->
        <div class="description-section">
          <div class="section-header" @click="toggleDescription">
            <span>상품 설명</span>
            <svg class="arrow" :class="{ up: isDescriptionOpen }" viewBox="0 0 24 24" fill="none">
              <path d="M7 10L12 15L17 10" stroke="currentColor" stroke-width="2" />
            </svg>
          </div>
          <div class="section-content" :class="{ open: isDescriptionOpen }">
            <div class="description">
              <p>{{ product?.description }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <footer class="mobile-footer" :class="{ visible: isFooterVisible }">
      <button class="share-btn">공유하기</button>
      <button class="buy-btn" @click="window.open(product?.shop[0]?.productUrl, '_blank')">최저가 이동</button>
    </footer>
  </main>
</template>
