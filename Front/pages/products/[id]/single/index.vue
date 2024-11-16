<script setup>
import { ref, onMounted, onUnmounted } from "vue";

useHead({
  link: [{ rel: "stylesheet", href: "/css/product-single.css" }],
});

const currentSlide = ref(0);
const isVendorListOpen = ref(true);
const isDescriptionOpen = ref(true);
const isFooterVisible = ref(false);
const isModalOpen = ref(false);
const selectedImage = ref(null);
const modalCurrentSlide = ref(0);

const route = useRoute();
const router = useRouter();
const productId = route.params.id;

const config = useRuntimeConfig();

const { data: product } = await useFetch(`${config.public.apiBase}/products/${productId}/single`, {
  key: `product-${productId}`,
});

useHead(() => ({
  bodyAttrs: {
    class: isModalOpen.value ? "modal-open" : "",
  },
}));

const prevModalSlide = () => {
  if (modalCurrentSlide.value > 0) {
    modalCurrentSlide.value--;
    selectedImage.value = product.value?.images[modalCurrentSlide.value];
  }
};

const nextModalSlide = () => {
  if (modalCurrentSlide.value < (product.value?.images?.length || 0) - 1) {
    modalCurrentSlide.value++;
    selectedImage.value = product.value?.images[modalCurrentSlide.value];
  }
};

const openModal = (image) => {
  const imageIndex = product.value?.images?.findIndex((img) => img.id === image.id);
  if (imageIndex !== -1) {
    modalCurrentSlide.value = imageIndex;
  }
  selectedImage.value = image;
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
  selectedImage.value = null;
  modalCurrentSlide.value = 0;
};

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

const lowestPriceUrl = computed(() => {
  return product.value?.shops?.[0]?.productUrl || null;
});

const openLowestPriceUrl = () => {
  if (lowestPriceUrl.value && window) {
    window.open(lowestPriceUrl.value, "_blank");
  }
};

watch(currentSlide, (newSlide) => {
  if (isModalOpen.value && product.value?.images) {
    selectedImage.value = product.value.images[newSlide];
  }
});

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
              <img :src="image.imageUrl" :alt="product?.productName" @click="openModal(image)" class="cursor-pointer" />
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
          <div class="section-content" :class="{ open: isVendorListOpen }">
            <div class="vendor-list">
              <article v-for="shop in product?.shops" :key="shop.id" class="vendor-card">
                <div class="background-area" @click="navigateToRoute(`/products/${shop.productId}/single`)"></div>
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

        <sention class="description-section">
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
        </sention>
      </div>
    </div>

    <footer class="mobile-footer" :class="{ visible: isFooterVisible }">
      <button class="share-btn">공유하기</button>
      <button class="buy-btn" @click.prevent="openLowestPriceUrl">최저가 이동</button>
    </footer>

    <div v-if="isModalOpen" class="modal" @click="closeModal">
      <div class="modal-content" @click.stop>
        <button class="modal-close" @click="closeModal">&times;</button>
        <button class="modal-nav modal-prev" @click.stop="prevModalSlide">‹</button>
        <button class="modal-nav modal-next" @click.stop="nextModalSlide">›</button>
        <img :src="selectedImage?.imageUrl" :alt="product?.productName" class="modal-image" />
      </div>
    </div>
  </main>
</template>

<style>
body.modal-open {
  overflow: hidden;
}
</style>
