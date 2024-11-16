<script setup>
import { ref, onMounted, onUnmounted } from "vue";

useHead({
  link: [{ rel: "stylesheet", href: "/css/product-detail.css" }],
});

const currentSlide = ref(0);
const isVendorListOpen = ref(true);
const isDescriptionOpen = ref(true);
const isFooterVisible = ref(false);

const goToSlide = (index) => {
  currentSlide.value = index;
};

const prevSlide = () => {
  if (currentSlide.value > 0) {
    currentSlide.value--;
  }
};

const nextSlide = () => {
  if (currentSlide.value < 2) {
    currentSlide.value++;
  }
};

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
        <li><a href="#">남성</a></li>
        <li><a href="#">하의</a></li>
        <li><a href="#">캐주얼 팬츠</a></li>
      </ol>
    </nav>

    <div class="product-container">
      <div class="left-column">
        <section class="product-images">
          <div class="slider-container" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
            <div v-for="n in 3" :key="n" class="slide">img {{ n }}</div>
          </div>
          <div class="slider-nav">
            <button class="prev" @click="prevSlide">‹</button>
            <button class="next" @click="nextSlide">›</button>
          </div>
          <div class="slider-dots">
            <div v-for="n in 3" :key="n" class="dot" :class="{ active: currentSlide === n - 1 }" @click="goToSlide(n - 1)"></div>
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
              <p>
                Lemaire reimagines classic workwear with this white button-up shirt, cut to an oversized fit. Made from heavy
                cotton poplin, it's accented with sleeve panels that create the illusion of a layered look.
              </p>
              <ul>
                <li>Made in Italy</li>
                <li>Care instructions: machine wash at 30 degrees</li>
                <li>Buttoned cuffs</li>
                <li>Closure: buttoned front</li>
                <li>Material: 100% cotton</li>
                <li>Item number: P00695115</li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <div class="product-info">
        <section class="header">
          <p class="brand">Lemaire</p>
          <h1 class="title">twisted belted work pants</h1>
        </section>

        <section class="price-info">
          <p class="original-price">10,000,000 ￦</p>
          <p>
            <span class="discount">90%</span>
            <span class="final-price">1,000,000 ￦</span>
          </p>
          <div class="additional-costs">
            <span>+ 관세 8%</span>
            <span>+ 부가세 10%</span>
            <span>+ 배송비 ~27$</span>
          </div>
        </section>

        <div class="vendor-section">
          <div class="section-header" @click="toggleVendorList">
            <span>모든 할인 상품 둘러보기</span>
            <svg class="arrow" :class="{ up: isVendorListOpen }" viewBox="0 0 24 24" fill="none">
              <path d="M7 10L12 15L17 10" stroke="currentColor" stroke-width="2" />
            </svg>
          </div>
          <div class="section-content" :class="{ open: isVendorListOpen }">
            <div class="vendor-list">
              <article v-for="n in 6" :key="n" class="vendor-card">
                <div class="vendor-logo">로고</div>
                <span class="vendor-name">SSENSE</span>
                <div class="vendor-price">
                  <p>1,000,000 ￦</p>
                  <a href="#" class="link">링크</a>
                </div>
              </article>
            </div>
          </div>
        </div>

        <div class="description-section">
          <div class="section-header" @click="toggleDescription">
            <span>상품 설명</span>
            <svg class="arrow" :class="{ up: isDescriptionOpen }" viewBox="0 0 24 24" fill="none">
              <path d="M7 10L12 15L17 10" stroke="currentColor" stroke-width="2" />
            </svg>
          </div>
          <div class="section-content" :class="{ open: isDescriptionOpen }">
            <div class="description">
              <p>
                Lemaire reimagines classic workwear with this white button-up shirt, cut to an oversized fit. Made from heavy
                cotton poplin, it's accented with sleeve panels that create the illusion of a layered look.
              </p>
              <ul>
                <li>Made in Italy</li>
                <li>Care instructions: machine wash at 30 degrees</li>
                <li>Buttoned cuffs</li>
                <li>Closure: buttoned front</li>
                <li>Material: 100% cotton</li>
                <li>Item number: P00695115</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>

    <footer class="mobile-footer" :class="{ visible: isFooterVisible }">
      <button class="share-btn">공유하기</button>
      <button class="buy-btn">최저가 이동</button>
    </footer>
  </main>
</template>
