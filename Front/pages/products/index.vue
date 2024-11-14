<script setup>
import { ref, watch, onMounted, onUnmounted } from "vue";

useHead({
  link: [{ rel: "stylesheet", href: "/css/product-list.css" }],
});

const route = useRoute();
const router = useRouter();
const gender = ref(route.query.gender);
const asideRef = ref(null);

// 정렬 관련
const isActive = ref(false);
const sortOptions = [
  { label: "인기순", value: "popular" },
  { label: "최근 등록순", value: "recent" },
  { label: "낮은 가격순", value: "priceAsc" },
  { label: "높은 가격순", value: "priceDesc" },
  { label: "할인율 높은순", value: "discountDesc" },
];
const selectedSort = ref("popular");

const toggleDropdown = () => {
  isActive.value = !isActive.value;
};

const selectOption = (option) => {
  selectedSort.value = option.value;
  isActive.value = false;
  loadProducts(true);
};

// 필터 태그 관련
const selectedFilters = ref({
  cat: [],
  brand: [],
});

// URL 쿼리 파라미터 업데이트
const updateQueryParams = async (newParams) => {
  // async 추가
  const updatedQuery = { ...route.query };

  if (newParams.gender) {
    updatedQuery.gender = newParams.gender;
  }

  // 카테고리 처리
  if (newParams.cat?.length) {
    updatedQuery.cat = newParams.cat.map((c) => c.id).join(",");
  } else {
    delete updatedQuery.cat;
  }

  // 브랜드 처리
  if (newParams.brand?.length) {
    updatedQuery.brand = newParams.brand.map((b) => b.id).join(",");
  } else {
    delete updatedQuery.brand;
  }

  // 가격 범위 처리
  if (newParams.min) updatedQuery.min = newParams.min;
  else delete updatedQuery.min;

  if (newParams.max) updatedQuery.max = newParams.max;
  else delete updatedQuery.max;

  await router.replace({
    // await 추가
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

  // aside 컴포넌트 상태 업데이트
  await asideRef.value?.updateFilterState({ type, id, checked: false });

  // 현재 남아있는 필터 상태로 검색 실행
  const filterData = {
    gender: route.query.gender, // gender 유지
    cat: selectedFilters.value.cat, // 현재 선택된 카테고리
    brand: selectedFilters.value.brand, // 현재 선택된 브랜드
    min: route.query.min, // 가격 범위 유지
    max: route.query.max,
  };

  // 쿼리 파라미터 직접 업데이트
  const updatedQuery = { ...route.query }; // 현재 쿼리 복사

  // 카테고리 처리
  if (filterData.cat?.length) {
    updatedQuery.cat = filterData.cat.map((c) => c.id).join(",");
  } else {
    delete updatedQuery.cat;
  }

  // 브랜드 처리
  if (filterData.brand?.length) {
    updatedQuery.brand = filterData.brand.map((b) => b.id).join(",");
  } else {
    delete updatedQuery.brand;
  }

  // 라우터 업데이트
  await router.replace({
    path: route.path,
    query: updatedQuery,
  });

  // 상품 검색 실행
  loadProducts(true);
};

// 상품 목록 관련
const products = ref([]);
const loading = ref(false);
const hasMore = ref(true);
const cursor = ref(null);

// 상품 로드
const loadProducts = async (reset = false) => {
  if (loading.value || (!hasMore.value && !reset)) return;
  loading.value = true;

  try {
    const config = useRuntimeConfig();
    const { data } = await useFetch("/products", {
      baseURL: config.public.apiBase,
      params: {
        gender: route.query.gender,
        cat: route.query.cat ? route.query.cat.split(",") : undefined,
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

    if (data.value) {
      products.value.push(...data.value.products);
      cursor.value = data.value.nextCursor;
      hasMore.value = data.value.hasMore;
    }
  } catch (error) {
    console.error("Failed to load products:", error);
  } finally {
    loading.value = false;
  }
};

// 검색 버튼 클릭 처리
const handleSearch = async (searchParams) => {
  // async 추가
  // 선택된 필터 상태 업데이트
  selectedFilters.value = {
    cat: searchParams.cat || [],
    brand: searchParams.brand || [],
  };

  // 가격도 selectedFilters에 포함
  if (searchParams.min || searchParams.max) {
    selectedFilters.value.min = searchParams.min;
    selectedFilters.value.max = searchParams.max;
  }

  // 쿼리 파라미터 업데이트 및 상품 검색 - 한번에 처리
  const queryParams = {
    ...searchParams,
    cat: selectedFilters.value.cat,
    brand: selectedFilters.value.brand,
    min: searchParams.min || undefined,
    max: searchParams.max || undefined,
  };

  // 쿼리 파라미터 업데이트를 기다린 후 상품 검색 실행
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
  () => route.query.gender, // route.query 전체가 아닌 gender만 감시
  (newGender) => {
    if (newGender) {
      gender.value = newGender;
      loadProducts(true);
    }
  },
  { immediate: true } // deep: true는 필요 없음
);

// 외부 클릭 이벤트 핸들러 유지
const handleClickOutside = (e) => {
  if (!e.target.closest(".filter-select")) {
    isActive.value = false;
  }
};

// mounted, unmounted 이벤트 핸들러 유지
onMounted(() => {
  setupIntersectionObserver();
  document.addEventListener("click", handleClickOutside);
});

onUnmounted(() => {
  if (observer) {
    observer.disconnect();
  }
  document.removeEventListener("click", handleClickOutside);
});
</script>

<template>
  <main>
    <div class="main-content-wrapper">
      <FuptoAside ref="asideRef" :initialGender="gender" @filter-change="handleFilterChange" @search="handleSearch" />
      <div class="product-content">
        <!-- 필터 태그 -->
        <section v-if="selectedFilters.cat.length || selectedFilters.brand.length" class="filter-tags">
          <div v-for="cat in selectedFilters.cat" :key="`cat-${cat.id}`" class="filter-tag">
            {{ cat.name }}
            <button @click="removeFilter('cat', cat.id)">×</button>
          </div>
          <div v-for="brand in selectedFilters.brand" :key="`brand-${brand.id}`" class="filter-tag">
            {{ brand.name }}
            <button @click="removeFilter('brand', brand.id)">×</button>
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
                  <a href="#">
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
                  </a>
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
/* 필터 태그 영역 */
.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 16px 0;
  margin-bottom: 16px;
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

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  font-size: 14px;
  color: #666;
}

.loading::after {
  content: "";
  width: 24px;
  height: 24px;
  margin-left: 8px;
  border: 2px solid #ddd;
  border-top-color: #333;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.empty-list {
  grid-column: 1 / -1;
  min-height: 200px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #ffffff;
  border-radius: 8px;
  font-size: 16px;
  color: #666;
}
</style>
