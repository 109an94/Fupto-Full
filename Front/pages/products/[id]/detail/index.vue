<script setup>
import { ref } from "vue";

const currentSlide = ref(0);
const isVendorListOpen = ref(true);
const isDescriptionOpen = ref(true);

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
</script>
<template>
  <main class="product">
    <nav class="breadcrumb">
      <ol>
        <li><a href="#">남성</a></li>
        <li><a href="#">하의</a></li>
        <li><a href="#">캐주얼 팬츠</a></li>
      </ol>
    </nav>

    <div class="product-container">
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
              <article v-for="n in 3" :key="n" class="vendor-card">
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
            Lemaire reimagines classic workwear with this white button-up shirt, cut to an oversized fit. Made from heavy cotton
            poplin, it's accented with sleeve panels that create the illusion of a layered look.
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

    <footer class="mobile-footer">
      <button class="share-btn">공유하기</button>
      <button class="buy-btn">최저가 이동</button>
    </footer>
  </main>
</template>

<style scoped>
.product {
  max-width: 1200px;
  margin: 0 auto;
}

.breadcrumb {
  position: fixed;
  top: 110px;
  width: 100%;
  background: white;
  padding: 10px;
  z-index: 100;
}

.breadcrumb ol {
  display: flex;
  gap: 8px;
}

.breadcrumb a {
  color: #2d2f33;
  font-size: 14px;
}

.breadcrumb li:not(:last-child)::after {
  content: ">";
  margin-left: 8px;
}

.product-container {
  display: block;
  margin-top: 4.5rem;
}

.product-info {
  transform: translateY(-25px);
}

.product-images {
  position: relative;
  height: 500px;
  overflow: hidden;
}

.slider-container {
  display: flex;
  transition: transform 0.3s ease;
  height: 100%;
}

.slide {
  min-width: 100%;
  height: 100%;
  background: #d9d9d9;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1d70f5;
}

.slider-nav button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  border: none;
  padding: 15px;
  cursor: pointer;
  font-size: 24px;
  color: rgba(255, 255, 255, 0.8);
  opacity: 0;
  transition: opacity 0.3s ease;
  background: rgba(255, 255, 255, 0.15);
}

.product-images:hover .slider-nav button {
  opacity: 1;
}

.slider-nav button:hover {
  background: rgba(255, 255, 255, 0.4);
}

.prev {
  left: 10px;
}

.next {
  right: 10px;
}

.slider-dots {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
}

.dot.active {
  background: white;
}

.header {
  padding: 20px;
}

.brand {
  color: #60646b;
  font-size: 16px;
  font-weight: bold;
}

.title {
  font-size: 24px;
  margin-top: 8px;
}

.price-info {
  padding: 20px;
  text-align: right;
}

.original-price {
  color: #989ca2;
  font-size: 18px;
  text-decoration: line-through;
}

.discount {
  color: #f74957;
  font-size: 24px;
  margin-right: 10px;
}

.final-price {
  color: #0f0f10;
  font-size: 32px;
}

.additional-costs {
  color: #60646b;
  font-size: 12px;
}

.additional-costs span {
  margin-left: 10px;
}

.section-header {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.arrow {
  width: 24px;
  height: 24px;
  transition: transform 0.3s;
}

.arrow.up {
  transform: rotate(180deg);
}

.section-content {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s;
}

.section-content.open {
  max-height: 1000px;
}

.vendor-list {
  padding: 20px;
}

.vendor-card {
  border: 1px solid #2d2f33;
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.vendor-logo {
  width: 60px;
  height: 60px;
  background: #d9d9d9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.vendor-name {
  flex: 1;
}

.vendor-price {
  text-align: right;
}

.link {
  display: inline-block;
  background: #1d70f5;
  color: white;
  padding: 2px 20px;
  border-radius: 10px;
  text-decoration: none;
  margin-top: 5px;
}

.description {
  padding: 20px;
  color: #60646b;
  font-size: 14px;
}

.description ul {
  margin-top: 10px;
  padding-left: 20px;
  list-style-type: disc;
}

.mobile-footer {
  position: fixed;
  bottom: -80px;
  width: 100%;
  max-width: 430px;
  background: white;
  padding: 10px 20px;
  display: flex;
  gap: 10px;
  transition: bottom 0.3s;
}

.share-btn {
  background: #d8e4f2;
  color: #1d70f5;
  border: none;
  border-radius: 10px;
  padding: 20px;
  width: 138px;
  font-weight: 500;
}

.buy-btn {
  background: #1d70f5;
  color: white;
  border: none;
  border-radius: 10px;
  padding: 20px;
  flex-grow: 1;
  font-weight: 700;
}

@media (min-width: 1024px) {
  .product-container {
    display: grid;
    grid-template-columns: 600px 1fr;
    gap: 2rem;
  }

  .product-images {
    height: 700px;
    width: 600px;
    margin-left: auto;
  }

  .vendor-list {
    margin-right: auto;
    padding-left: 15px;
  }

  .vendor-card {
    height: 80px;
  }

  .link {
    margin-top: 20px;
  }

  .description-section {
    max-width: 600px; /* 이미지 프레임과 동일한 너비 */
    justify-content: center;
  }

  .mobile-footer {
    display: none;
  }
}
</style>
