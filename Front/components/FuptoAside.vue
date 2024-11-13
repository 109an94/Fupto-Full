<script setup>
import { ref, computed, watch } from "vue";

useHead({
  link: [{ rel: "stylesheet", href: "/css/aside.css" }],
});

// props로 초기 성별 받기
const props = defineProps({
  initialGender: {
    type: String,
    default: null,
  },
});

/*성별 관련*/
const selectedGender = ref(props.initialGender);

const toggleGender = async (gender) => {
  selectedGender.value = gender;
  const genderId = gender === "male" ? "1" : "2";
  await loadSecondCategories(genderId);
  await loadBrands();
};
/*카테고리 관련*/
const categories = ref([]);

// 2차 카테고리 로드
const loadSecondCategories = async (genderId) => {
  if (!genderId) return;

  const config = useRuntimeConfig();
  const { data } = await useFetch("/products/categories", {
    baseURL: config.public.apiBase,
    params: { parentId: genderId },
  });

  if (data.value) {
    categories.value = data.value.map((category) => ({
      id: category.id,
      name: category.name,
      checked: false,
      isExpanded: false,
      subCategories: [{ id: `${category.id}-all`, name: "All", checked: true }],
    }));
  }
};

// 3차 카테고리 로드 (2차 카테고리 클릭시)
const loadThirdCategories = async (category) => {
  const config = useRuntimeConfig();
  const { data } = await useFetch("/products/categories", {
    baseURL: config.public.apiBase,
    params: { parentId: category.id },
  });

  if (data.value) {
    category.subCategories = [
      { id: `${category.id}-all`, name: "All", checked: true },
      ...data.value.map((subCat) => ({
        id: subCat.id,
        name: subCat.name,
        checked: false,
      })),
    ];
  }
};

// 기존 toggleCategory 함수 수정
const toggleCategory = async (category) => {
  category.isExpanded = category.checked;
  if (category.checked && category.subCategories.length === 1) {
    await loadThirdCategories(category);
  }
};

// handleSubCategoryChange는 기존 그대로 유지
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

// clearAll도 기존 그대로 유지
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
const brands = ref([]);

// 브랜드 로드
const loadBrands = async () => {
  const config = useRuntimeConfig();
  const { data } = await useFetch("/products/brands", {
    baseURL: config.public.apiBase,
  });

  if (data.value) {
    brands.value = data.value.map((brand) => ({
      id: brand.id,
      name: `${brand.engName}(${brand.korName})`,
      checked: false,
    }));
  }
};

// filteredBrands는 기존 그대로 유지
const filteredBrands = computed(() => {
  return brands.value.filter((brand) => brand.name.toLowerCase().includes(searchQuery.value.toLowerCase()));
});

// clearBrands도 기존 그대로 유지
const clearBrands = () => {
  brands.value.forEach((brand) => (brand.checked = false));
  searchQuery.value = "";
};

/*가격 관련 - 기존 그대로 유지*/
const minPrice = ref("");
const maxPrice = ref("");

const clearPriceRange = () => {
  minPrice.value = "";
  maxPrice.value = "";
};

// URL의 gender 파라미터 감지하여 성별 설정
watch(
  () => props.initialGender,
  (newGender) => {
    if (newGender) {
      selectedGender.value = newGender === "1" ? "male" : "female";
      loadSecondCategories(newGender);
      loadBrands();
    }
  },
  { immediate: true }
);

// 초기 데이터 로드
onMounted(async () => {
  if (props.initialGender) {
    await loadSecondCategories(props.initialGender);
    await loadBrands();
  }
});
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
