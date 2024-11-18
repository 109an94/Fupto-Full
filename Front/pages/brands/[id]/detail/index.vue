<script setup>
import { onMounted, ref } from "vue";

useHead({
  link: [{ rel: "stylesheet", href: "/css/product-list.css" }],
});

const route = useRoute();
const router = useRouter();
const config = useRuntimeConfig();

const gender = ref(route.query.gender); // URL에서 gender 파라미터 가져오기
const asideRef = ref(null);
const selectedFilters = ref({
  category: [],
  sub: [],
  brand: [],
});

const brandId = route.params.id;

// 필터 드롭다운
const isActive = ref(false);

const selectedSort = ref("popular");
const sortOptions = [
  { label: "인기순", value: "popular" },
  { label: "최근 등록순", value: "recent" },
  { label: "낮은 가격순", value: "priceAsc" },
  { label: "높은 가격순", value: "priceDesc" },
  { label: "할인율 높은순", value: "discountDesc" },
];

const products = ref([]);
const loading = ref(false);
const hasMore = ref(true);
const cursor = ref(null);

const { data: brand } = await useFetch(`${config.public.apiBase}/brands/${brandId}`, {
  key: `brand-${brandId}`,
});

const { data: initialData } = await useFetch("/products", {
  baseURL: config.public.apiBase,
  params: {
    gender: route.query.gender,
    category: route.query.category ? route.query.category.split(",") : undefined,
    sub: route.query.sub ? route.query.sub.split(",") : undefined,
    brand: route.query.brand ? route.query.brand.split(",") : undefined,
    min: route.query.min || undefined,
    max: route.query.max || undefined,
    sort: selectedSort.value,
    cursor: null,
    limit: 20,
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
////////////////////////////////////////////////////////////////////////////////
const loadProducts = async (reset = false) => {
  if (loading.value || (!hasMore.value && !reset)) return;
  loading.value = true;

  try {
    const data = await $fetch("/products", {
      baseURL: config.public.apiBase,
      params: {
        gender: route.query.gender,
        category: route.query.category ? route.query.category.split(",") : undefined,
        sub: route.query.sub ? route.query.sub.split(",") : undefined,
        brand: route.query.brand ? route.query.brand.split(",") : undefined,
        min: route.query.min || undefined,
        max: route.query.max || undefined,
        sort: selectedSort.value,
        cursor: reset ? null : cursor.value,
        limit: 20,
      },
    });

    if (reset) {
      products.value = [];
      cursor.value = null;
    }

    if (data) {
      products.value.push(...data.products);
      cursor.value = data.nextCursor;
      hasMore.value = data.hasMore;
    }
  } catch (error) {
    console.error("Failed to load products:", error);
  } finally {
    loading.value = false;
  }
};

const toggleDropdown = () => {
  isActive.value = !isActive.value;
};

const selectOption = async (option) => {
  selectedSort.value = option.value;
  isActive.value = false;

  try {
    const response = await $fetch(`${config.public.apiBase}/products`, {
      params: {
        gender: route.query.gender,
        category: route.query.category ? route.query.category.split(",") : undefined, // 수정
        sub: route.query.sub ? route.query.sub.split(",") : undefined, // 수정
        brand: route.query.brand ? route.query.brand.split(",") : undefined,
        min: route.query.min || undefined,
        max: route.query.max || undefined,
        sort: option.value,
        cursor: null,
        limit: 20,
      },
    });

    if (response) {
      products.value = response.products;
      cursor.value = response.nextCursor;
      hasMore.value = response.hasMore;
    }

    await router.replace({
      query: {
        ...route.query,
        sort: option.value,
      },
    });
  } catch (error) {
    console.error("Failed to sort products:", error);
  }
};

const isDetailsVisible = ref(true); // 상세 내용 보이기 상태

const toggleDetails = () => {
  isDetailsVisible.value = !isDetailsVisible.value;
};

// URL 쿼리 파라미터 업데이트
const updateQueryParams = async (newParams) => {
  const updatedQuery = { ...route.query };

  if (newParams.gender) {
    updatedQuery.gender = newParams.gender;
    delete updatedQuery.sort;
  }

  if (newParams.category?.length) {
    updatedQuery.category = newParams.category.map((c) => c.id).join(",");
  } else {
    delete updatedQuery.category;
  }

  if (newParams.sub?.length) {
    updatedQuery.sub = newParams.sub.map((c) => c.id).join(",");
  } else {
    delete updatedQuery.sub;
  }

  if (newParams.brand?.length) {
    updatedQuery.brand = newParams.brand.map((b) => b.id).join(",");
  } else {
    delete updatedQuery.brand;
  }

  if (newParams.min) updatedQuery.min = newParams.min;
  else delete updatedQuery.min;

  if (newParams.max) updatedQuery.max = newParams.max;
  else delete updatedQuery.max;

  await router.replace({
    path: route.path,
    query: updatedQuery,
  });
};

// FuptoAside의 필터 변경 처리
const handleFilterChange = (filterData) => {
  updateQueryParams(filterData);
};

const removeFilter = async (type, id) => {
  selectedFilters.value[type] = selectedFilters.value[type].filter((item) => item.id !== id);

  await asideRef.value?.updateFilterState({ type, id, checked: false });

  const filterData = {
    gender: route.query.gender,
    category: selectedFilters.value.category,
    sub: selectedFilters.value.sub,
    brand: selectedFilters.value.brand,
    min: route.query.min,
    max: route.query.max,
  };

  const updatedQuery = { ...route.query };

  if (filterData.category?.length) {
    updatedQuery.category = filterData.category.map((c) => c.id).join(",");
  } else {
    delete updatedQuery.category;
  }

  if (filterData.sub?.length) {
    updatedQuery.sub = filterData.sub.map((c) => c.id).join(",");
  } else {
    delete updatedQuery.sub;
  }

  if (filterData.brand?.length) {
    updatedQuery.brand = filterData.brand.map((b) => b.id).join(",");
  } else {
    delete updatedQuery.brand;
  }

  await router.replace({
    path: route.path,
    query: updatedQuery,
  });

  loadProducts(true);
};

// 검색 버튼 클릭
const handleSearch = async (searchParams) => {
  selectedFilters.value = {
    category: searchParams.category || [],
    sub: searchParams.sub || [],
    brand: searchParams.brand || [],
  };

  if (searchParams.min || searchParams.max) {
    selectedFilters.value.min = searchParams.min;
    selectedFilters.value.max = searchParams.max;
  }

  const queryParams = {
    ...searchParams,
    category: selectedFilters.value.category,
    sub: selectedFilters.value.sub,
    brand: selectedFilters.value.brand,
    min: searchParams.min || undefined,
    max: searchParams.max || undefined,
  };

  await updateQueryParams(queryParams);
  await loadProducts(true);
};

// 무한 스크롤
let observer;
const lastProductRef = ref(null);

const setupIntersectionObserver = () => {
  observer = new IntersectionObserver(
    (entries) => {
      const firstEntry = entries[0];
      if (firstEntry.isIntersecting && hasMore.value) {
        loadProducts();
      }
    },
    { threshold: 0.1 }
  );
};

const updateObserver = () => {
  if (observer && lastProductRef.value) {
    observer.disconnect();
    observer.observe(lastProductRef.value);
  }
};

watch(() => products.value.length, updateObserver);

// URL 파라미터 변경 감지
watch(
  () => route.query.gender,
  async (newGender) => {
    if (newGender) {
      gender.value = newGender;
      selectedSort.value = "popular";
      onNuxtReady(async () => {
        await loadProducts(true);
      });
    }
  }
);

const handleClickOutside = (e) => {
  if (!e.target.closest(".filter-select")) {
    isActive.value = false;
  }
};

onMounted(() => {
  setupIntersectionObserver();
  document.addEventListener("click", handleClickOutside);
});

onUnmounted(() => {
  if (observer) {
    observer.disconnect();
  }
  document.removeEventListener("click", handleClickOutside);
});;
</script>

<template>
  <main>
    <div class="main-content-wrapper">
      <FuptoAside2 ref="asideRef" :initialGender="gender" @filter-change="handleFilterChange" @search="handleSearch" />
      <div class="product-content">

        <section>
          <!-- Brand Profile Section -->
          <div class="brand-profile">

            <div class="brand-background">
              <img class="brand-background-img" src="/imgs/brands/lemaire-bg.jpeg" alt="Brand Background">
            </div>

            <div class="brand-header">
              <img class="brand-image" :src="'http://localhost:8080/api/v1/' + brand.img" :alt="brand.korName" />
              <div class="brand-info">
                <h1 class="brand-name">{{ brand.engName }}</h1>
                <p class="brand-subtitle">{{ brand.korName }}</p>
              </div>
            </div>

            <div class="tabs">
              <span
                class="tab"
                :class="{ active: isDetailsVisible }"
                @click="toggleDetails"
              >
                상세 설명
              </span>
            </div>

            <div v-if="isDetailsVisible" class="brand-details">
              <p class="about-title">About</p>
              <p class="about-description">{{ brand.description }}</p>
            </div>
            
          </div>
        </section>

         <!-- 필터 태그 -->
         <section
          v-if="selectedFilters.category.length || selectedFilters.sub.length || selectedFilters.brand.length"
          class="filter-tags"
        >
          <div v-for="category in selectedFilters.category" :key="`category-${category.id}`" class="filter-tag">
            {{ category.name }}
            <button @click="removeFilter('category', category.id)">×</button>
          </div>
          <div v-for="sub in selectedFilters.sub" :key="`sub-${sub.id}`" class="filter-tag">
            {{ sub.name }}
            <button @click="removeFilter('sub', sub.id)">×</button>
          </div>
        </section>

        <!-- 정렬 옵션 -->
        <section class="filter-frame">
          <div class="filter-container">
            <div class="filter-select" tabindex="0" @click="toggleDropdown">
              <div class="filter-title" :class="{ active: isActive }">
                {{ sortOptions.find((opt) => opt.value === selectedSort)?.label }}
              </div>
              <ul class="filter-options" :class="{ show: isActive }">
                <li v-for="option in sortOptions" :key="option.value" class="filter-option" @click.stop="selectOption(option)">
                  {{ option.label }}
                </li>
              </ul>
            </div>
          </div>
        </section>

        <!-- 상품 목록 -->
        <section class="product-p-frame">
          <div class="user-product-list">
            <ul class="product-grid">
              <template v-if="products.length > 0">
                <li
                  v-for="(product, index) in products"
                  :key="product.id"
                  class="product-c-frame"
                  :ref="
                    index === products.length - 1
                      ? (el) => {
                          lastProductRef = el;
                        }
                      : undefined
                  "
                >
                  <nuxt-link :to="`/products/${product.id}/detail`">
                    <div class="product-img-frame">
                      <div class="product-img-container">
                        <img class="product-images primary-img" :src="product.mainImageUrl" alt="product-img" />
                        <img class="product-images secondary-img" :src="product.hoverImageUrl" alt="product-img-hover" />
                      </div>
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
        </section>

      </div>
    </div>
  </main>
</template>

<style scoped>
.brand-profile {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  font-family: "Noto Sans KR", sans-serif;
  color: var(--color-text-default);
}

.brand-background {
  width: 100%;
  height: 200px;
  margin-top: 24px;
  background-color: var(--color-ui-backgroundb);
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  position: relative; /* 자식 요소의 절대 위치 기준점 */
}

.brand-background-img {
  position: absolute; /* 부모 요소 기준으로 절대 위치 */
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover; /* 이미지 비율을 유지하면서 영역 채우기 */
}

.brand-header,
.tabs {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid var(--color-ui-backgroundb);
}

.brand-image {
    width: 70px;
    height: 70px;
    border-radius: 50%;        /* 원형으로 만들기 */
    object-fit: cover;         /* 이미지가 원 안에 꽉 차도록 */
    border: 0.5px solid var(--color-text-unselected);
    background-color: var(--color-ui-white)
}

.brand-info {
  flex: 1;
  margin-left: 10px;
}

.brand-name {
  color: var(--color-ui-black);
  font-size: var(--font-bold-h3);
  font-weight: var(--font-bold);
  margin: 0;
}

.brand-subtitle,
.brand-stats,
.about-description {
  font-size: var(--font-regular-caption);
  color: var(--color-text-caption);
  margin-top: 5px;
}

.button {
  padding: 5px 10px;
  font-size: var(--font-regular-body);
  font-weight: var(--font-regular);
  color: var(--color-ui-black);
  border: 1px solid var(--color-ui-backgroundb);
  background-color: var(--color-ui-white);
  cursor: pointer;
}

.tabs {
  justify-content: space-around;
  font-weight: var(--font-bold);
}

.tab {
  cursor: pointer;
  padding: 10px;
}

.tab.active {
  color: var(--color-main-deep);
  border-bottom: 2px solid var(--color-main-deep);
}

.brand-details {
  padding: 20px;
}

.about-title {
  font-size: var(--font-medium-h3);
  font-weight: var(--font-bold);
  margin-top: 20px;
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 16px 0;
  margin-bottom: 16px;
  width: 100%;
}

.filter-tag {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  background-color: #f5f5f5;
  border-radius: 20px;
  font-size: 14px;
  color: #333;
}

.filter-tag button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-left: 8px;
  width: 18px;
  height: 18px;
  border: none;
  background: none;
  font-size: 16px;
  color: #666;
  cursor: pointer;
  padding: 0;
}

.filter-tag button:hover {
  color: #000;
}

@media (min-width: 769px) {
  .filter-tags {
    max-width: calc((130px + (100vw - 769px) * 0.14) * 3 + 20px);
  }
}

@media (min-width: 1440px) {
  .filter-tags {
    max-width: calc(224px * 4 + 30px);
  }
}

@media (max-width: 768px) {
  .filter-tags {
    max-width: calc(130px * 5 + 40px);
  }
}

@media (max-width: 650px) {
  .filter-tags {
    max-width: calc(130px * 4 + 30px);
  }
}

@media (max-width: 600px) {
  .filter-tags {
    max-width: calc(130px * 3 + 20px);
  }
}

@media (max-width: 430px) {
  .filter-tags {
    max-width: calc(130px * 2 + 10px);
  }
}

@media (max-width: 280px) {
  .filter-tags {
    max-width: 130px;
  }
}
</style>