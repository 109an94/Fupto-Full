<script setup>
import { ref, onMounted, onUnmounted } from "vue";

const { isAnonymous } = useUserDetails();
const { isAuthModalOpen, checkVendorAccess, closeAuthModal, navigateToSignup, navigateToSignin } = useVendorAuth();

const currentSlide = ref(0);
const isVendorListOpen = ref(true);
const isDescriptionOpen = ref(true);
const isFooterVisible = ref(false);
const isModalOpen = ref(false);
const selectedImage = ref(null);
const modalCurrentSlide = ref(0);

const route = useRoute();
const router = useRouter();
const config = useRuntimeConfig();
const productId = route.params.id;

useHead(() => ({
  link: [{ rel: "stylesheet", href: "/css/product-detail.css" }],
  bodyAttrs: {
    class: isModalOpen.value ? "modal-open" : "",
  },
}));

const { data: product } = await useAuthFetch(`/products/${productId}`, {
  key: `product-${productId}`,
});

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

const { toggleFavorite: toggleFavoriteAction } = useFavorite();

//찜 관련 함수
const toggleFavorite = async (event) => {
  event.preventDefault();
  const success = await toggleFavoriteAction(product.value.mappingId);

  if (success) {
    product.value.favorite = !product.value.favorite;
  }
};

const toggleVendorList = () => {
  if (!checkVendorAccess()) return;
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

const getQueryString = (clickedIndex) => {
  if (!product.value?.categories) return {};

  const query = {};

  if (clickedIndex >= 0) {
    query.gender = product.value.categories[0].id;
  }

  if (clickedIndex >= 1) {
    query.category = product.value.categories[1].id;
  }

  if (clickedIndex >= 2) {
    query.sub = product.value.categories[2].id;
  }

  return query;
};

watch(currentSlide, (newSlide) => {
  if (isModalOpen.value && product.value?.images) {
    selectedImage.value = product.value.images[newSlide];
  }
});

onMounted(() => {
  window.addEventListener("scroll", handleScroll);
  if (isAnonymous()) {
    checkVendorAccess();
  }
});

onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
});
</script>

<template>
  <main class="product product-detail-page">
    <nav class="categoty-list">
      <ol>
        <li v-for="(category, index) in product?.categories" :key="category.id">
          <nuxt-link
            :to="{
              path: '/products',
              query: getQueryString(index),
            }"
          >
            {{ category.name }}
          </nuxt-link>
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

        <section class="description-section">
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
        </section>
      </div>

      <div class="product-info">
        <section class="header">
          <p class="brand">{{ product?.brandEngName }}</p>
          <div class="title-container">
            <h1 class="title">{{ product?.productName }}</h1>
            <button :data-favorite="product?.favorite" @click="toggleFavorite" class="favorite-btn">
              <img
                class="favorite"
                :src="product?.favorite ? '/imgs/icon/favorite-fill.svg' : '/imgs/icon/favorite.svg'"
                :alt="product?.favorite ? '찜' : '찜 해제'"
              />
            </button>
          </div>
        </section>

        <section class="price-info">
          <p class="retail-price">{{ product?.priceInfo.retailPrice?.toLocaleString() }} ￦</p>
          <p>
            <span class="discount">{{ product?.priceInfo.discountRate }}%</span>
            <span class="sale-price">{{ product?.priceInfo.salePrice?.toLocaleString() }} ￦</span>
          </p>
        </section>

        <section class="vendor-section">
          <div :class="{ 'blur-section': isAnonymous() }">
            <div class="section-header" @click="toggleVendorList">
              <span>모든 할인 상품 둘러보기</span>
              <svg class="arrow" :class="{ up: isVendorListOpen }" viewBox="0 0 24 24" fill="none">
                <path d="M7 10L12 15L17 10" stroke="currentColor" stroke-width="2" />
              </svg>
            </div>
            <div class="section-content" :class="{ open: isVendorListOpen }">
              <ul class="vendor-list">
                <li v-for="shop in product?.shops" :key="shop.id" class="vendor-card">
                  <div class="background-area" @click="navigateToRoute(`/products/${shop.productId}/single`)"></div>
                  <div class="vendor-logo" @click.stop>
                    <img :src="`${config.public.apiBase}/${shop.logoUrl}`" :alt="shop.shopName" />
                  </div>
                  <div class="name-wrapper" @click.stop>
                    <nuxt-link :to="`/shoppingmalls/${shop.id}/detail`" class="vendor-name">{{ shop.shopName }}</nuxt-link>
                  </div>
                  <div class="vendor-price">
                    <p>{{ shop.price?.toLocaleString() }} ￦</p>
                    <a :href="shop.productUrl" class="link" target="_blank" @click.stop>이동</a>
                  </div>
                </li>
              </ul>
            </div>
          </div>

          <div v-if="isAnonymous() && isAuthModalOpen" class="vendor-modal-overlay">
            <div class="popup flex flex-col items-center gap-lg">
              <div class="flex gap-lg justify-end w-full pr">
                <button class="icon-sm" @click="closeAuthModal">×</button>
              </div>
              <h2 class="text-default text-bold text-lg">쇼핑몰 정보가 궁금하시다면?</h2>
              <button type="button" class="btn btn-fupto flex items-center justify-center" @click="navigateToSignup">
                <span class="text-white text-bold text-md">FUPTO로 가입하기</span>
              </button>
              <div class="flex items-center gap-sm">
                <span class="text-default text-regular text-sm">이미 회원이신가요?</span>
                <a href="#" class="text-default text-bold text-md" @click.prevent="navigateToSignin">로그인하기 ></a>
              </div>
            </div>
          </div>
        </section>

        <section class="description-section">
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
        </section>
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
