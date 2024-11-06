<script setup>
import { ref, computed } from "vue";

useHead({
  link: [{ rel: "stylesheet", href: "/css/aside.css" }],
});

/*성별 관련*/
const selectedGender = ref("male");
const toggleGender = (gender) => {
  selectedGender.value = gender;
};

/*카테고리 관련*/
const categories = ref([
  {
    id: "category-tops",
    name: "상의",
    checked: false,
    isExpanded: false,
    subCategories: [
      { id: "tops-all", name: "All", checked: true },
      { id: "tops-tshirts", name: "T-Shirts", checked: false },
      { id: "tops-shirts", name: "Shirts", checked: false },
      { id: "tops-sweaters", name: "Sweaters", checked: false },
      { id: "tops-hoodies", name: "Hoodies", checked: false },
    ],
  },
  {
    id: "category-bottoms",
    name: "하의",
    checked: false,
    isExpanded: false,
    subCategories: [
      { id: "bottoms-all", name: "All", checked: true },
      { id: "bottoms-pants", name: "Pants", checked: false },
      { id: "bottoms-jeans", name: "Jeans", checked: false },
      { id: "bottoms-shorts", name: "Shorts", checked: false },
      { id: "bottoms-skirts", name: "Skirts", checked: false },
    ],
  },
  {
    id: "category-shoes",
    name: "신발",
    checked: false,
    isExpanded: false,
    subCategories: [
      { id: "shoes-all", name: "All", checked: true },
      { id: "shoes-sneakers", name: "Sneakers", checked: false },
      { id: "shoes-boots", name: "Boots", checked: false },
      { id: "shoes-sandals", name: "Sandals", checked: false },
      { id: "shoes-formal", name: "Formal", checked: false },
    ],
  },
  {
    id: "category-accessories",
    name: "액세서리",
    checked: false,
    isExpanded: false,
    subCategories: [
      { id: "accessories-all", name: "All", checked: true },
      { id: "acc-belts", name: "Belts", checked: false },
      { id: "acc-hats", name: "Hats", checked: false },
      { id: "acc-jewelry", name: "Jewelry", checked: false },
      { id: "acc-watches", name: "Watches", checked: false },
    ],
  },
  {
    id: "category-bags",
    name: "가방",
    checked: false,
    isExpanded: false,
    subCategories: [
      { id: "bags-all", name: "All", checked: true },
      { id: "bags-backpacks", name: "Backpacks", checked: false },
      { id: "bags-shoulder", name: "Shoulder Bags", checked: false },
      { id: "bags-tote", name: "Tote Bags", checked: false },
      { id: "bags-clutch", name: "Clutch", checked: false },
    ],
  },
]);

const toggleCategory = (category) => {
  category.isExpanded = category.checked;
};

const handleSubCategoryChange = (category, subCategory) => {
  if (subCategory.id.endsWith("-all")) {
    if (subCategory.checked) {
      category.subCategories.forEach((sub) => {
        if (sub.id !== subCategory.id) {
          sub.checked = false;
        }
      });
    }
  } else {
    const allButton = category.subCategories.find((sub) => sub.id.endsWith("-all"));
    if (allButton) {
      allButton.checked = false;
    }
  }
};

const clearAll = () => {
  categories.value.forEach((category) => {
    category.checked = false;
    category.isExpanded = false;
    category.subCategories.forEach((sub, index) => {
      sub.checked = index === 0;
    });
  });
};

/*브랜드 관련*/
const searchQuery = ref("");

const brands = ref([
  { id: "division", name: "(DI)VISION", checked: false },
  { id: "032c", name: "032C", checked: false },
  { id: "alyx", name: "1017 ALYX 9SM", checked: false },
  { id: "1989studio", name: "1989 studio", checked: false },
  { id: "44label", name: "44 LABEL GROUP", checked: false },
  { id: "87avril90", name: "87 AVRIL 90", checked: false },
  { id: "acoldwall", name: "A-COLD-WALL", checked: false },
  { id: "apc", name: "A.P.C.", checked: false },
]);

const filteredBrands = computed(() => {
  return brands.value.filter((brand) => brand.name.toLowerCase().includes(searchQuery.value.toLowerCase()));
});

const clearBrands = () => {
  brands.value.forEach((brand) => (brand.checked = false));
  searchQuery.value = "";
};

/*가격 관련*/
const minPrice = ref("");
const maxPrice = ref("");

const clearPriceRange = () => {
  minPrice.value = "";
  maxPrice.value = "";
};
</script>

<template>
  <aside class="filter-sidebar">
    <h1 style="display: none">사이드바</h1>
    <div>
      <section>
        <h1 class="filter-list h1-style">성별</h1>
        <div class="gender-buttons">
          <button
            class="gender-button"
            :class="{ 'gender-button-active': selectedGender === 'female' }"
            @click="toggleGender('female')"
          >
            여성
          </button>
          <button
            class="gender-button"
            :class="{ 'gender-button-active': selectedGender === 'male' }"
            @click="toggleGender('male')"
          >
            남성
          </button>
        </div>
      </section>

      <section>
        <header class="filter-list">
          <h1 class="h1-style">카테고리</h1>
          <span class="clear-button" @click="clearAll">Clear</span>
        </header>
        <ul class="category-list">
          <li v-for="category in categories" :key="category.id" class="category-item">
            <div class="category-item-content">
              <input type="checkbox" :id="category.id" v-model="category.checked" @change="toggleCategory(category)" />
              <label :for="category.id">{{ category.name }}</label>
              <img class="icon-down" :src="category.isExpanded ? '/imgs/icon/up.svg' : '/imgs/icon/down.svg'" alt="direc-icon" />
            </div>
            <div v-if="category.isExpanded" class="category-items">
              <div class="aside-category-list">
                <template v-for="subCategory in category.subCategories" :key="subCategory.id">
                  <input
                    type="checkbox"
                    name="category"
                    :id="subCategory.id"
                    class="aside-category-input"
                    v-model="subCategory.checked"
                    @change="handleSubCategoryChange(category, subCategory)"
                  />
                  <label :for="subCategory.id" class="aside-category-label">
                    {{ subCategory.name }}
                  </label>
                </template>
              </div>
            </div>
          </li>
        </ul>
      </section>

      <section>
        <header class="filter-list">
          <h1 class="h1-style">브랜드</h1>
          <span class="clear-button" @click="clearBrands">Clear</span>
        </header>
        <div>
          <input type="text" v-model="searchQuery" placeholder="브랜드 검색" class="aside-brand-search-input" />
        </div>
        <div class="aside-brand-list">
          <div v-for="brand in filteredBrands" :key="brand.id" class="aside-brand-item">
            <input type="checkbox" :id="brand.id" v-model="brand.checked" />
            <label :for="brand.id">{{ brand.name }}</label>
          </div>
        </div>
      </section>

      <section>
        <header class="filter-list">
          <h1 class="h1-style">가격</h1>
          <span class="clear-button" @click="clearPriceRange">Clear</span>
        </header>
        <div class="price-range">
          <div class="price-input">
            <span class="currency">₩</span>
            <input
              type="number"
              v-model="minPrice"
              placeholder="최소"
              max="99999999"
              oninput="javascript: if (this.value.length > 8) this.value = this.value.slice(0, 8);"
            />
          </div>
          <span class="price-separator">-</span>
          <div class="price-input">
            <span class="currency">₩</span>
            <input
              type="number"
              v-model="maxPrice"
              placeholder="최대"
              max="99999999"
              oninput="javascript: if (this.value.length > 8) this.value = this.value.slice(0, 8);"
            />
          </div>
        </div>
      </section>

      <button class="search-button">검 색</button>
    </div>
  </aside>
</template>
