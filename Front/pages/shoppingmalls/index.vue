<script setup>
import { onMounted, ref } from "vue";

useHead({
  link: [{ rel: "stylesheet", href: "/css/product-list.css" }],
});

const route = useRoute();
const gender = ref(route.query.gender); // URL에서 gender 파라미터 가져오기

// 필터 드롭다운
const isActive = ref(false);
const selectedOption = ref("인기순");

const options = ["인기순", "최근 등록순", "낮은 가격순", "높은 가격순", "할인율 높은순"];

const toggleDropdown = () => {
  isActive.value = !isActive.value;
};

const selectOption = (option) => {
  selectedOption.value = option;
  isActive.value = false;
};

// 찜하기
const toggleFavorite = (event) => {
  event.preventDefault();
  const button = event.currentTarget;
  const isFavorite = button.getAttribute("data-favorite") === "true";
  const img = button.querySelector("img");

  if (isFavorite) {
    button.setAttribute("data-favorite", "false");
    img.src = "/imgs/icon/favorite.svg";
    img.alt = "즐겨찾기 추가";
  } else {
    button.setAttribute("data-favorite", "true");
    img.src = "/imgs/icon/favorite-fill.svg";
    img.alt = "즐겨찾기 제거";
  }
};

// 외부 클릭시 드롭다운 닫기
const handleClickOutside = (e) => {
  if (!e.target.closest(".filter-select")) {
    isActive.value = false;
  }
};

watch(
  () => route.query.gender,
  (newGender) => {
    gender.value = newGender;
  }
);

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});
</script>

<template>
  <main>
    <div class="main-content-wrapper">
      <FuptoAside :initialGender="gender" />
      <div class="product-content">
        <section class="category-frame">
          <h1 style="display: none">카테고리</h1>
          <span class="category-text"><a href="#">남성</a> &gt;</span>
        </section>

        <section class="filter-frame">
          <h1 class="d-none">필터 리스트</h1>
          <div class="filter-container">
            <div class="filter-select" tabindex="0" @click="toggleDropdown">
              <div class="filter-title" :class="{ active: isActive }">
                {{ selectedOption }}
              </div>
              <ul class="filter-options" :class="{ show: isActive }">
                <li v-for="option in options" :key="option" class="filter-option" @click.stop="selectOption(option)">
                  {{ option }}
                </li>
              </ul>
            </div>
          </div>
        </section>

        <section class="product-p-frame">
          <h1 style="display: none">상품 리스트</h1>
          <div class="user-product-list">
            <ul class="product-grid">
              <li class="product-c-frame">
                <a href="#">
                  <div class="product-img-frame">
                    <div class="product-img-container">
                      <img
                        class="product-images primary-img"
                        src="/imgs/products/eef6bd5c4710d6e0e0edbb48cc935eff8855edc332acd2ea12f6e1ccb05e3cc5.jfif"
                        alt="product-img"
                      />
                      <img
                        class="product-images secondary-img"
                        src="/imgs/products/91a6a9958cdf5e42b366914d7f0f4e725d055c518bb363d3930b6650496c855a.jfif"
                        alt="product-img-hover"
                      />
                    </div>
                    <button class="favorite-button" data-favorite="false" @click="toggleFavorite">
                      <img class="favorite" src="/imgs/icon/favorite.svg" alt="찜" />
                    </button>
                  </div>
                  <div class="product-info">
                    <span class="product-brand">Lemaire</span>
                    <span class="product-name">twisted belted work pants</span>
                    <div class="price-info">
                      <span class="product-price">1,000,000</span>
                      <span class="price-unit">원</span>
                    </div>
                  </div>
                </a>
              </li>
            </ul>
          </div>
        </section>
      </div>
    </div>
  </main>
</template>