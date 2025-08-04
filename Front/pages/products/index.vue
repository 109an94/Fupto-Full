<script setup>
import { ref, watch, onMounted, onUnmounted } from "vue";

useHead({
  link: [{ rel: "stylesheet", href: "/css/product-list.css" }],
});

const route = useRoute();
const router = useRouter();
const gender = ref(route.query.gender);
const asideRef = ref(null);
const selectedFilters = ref({
  category: [],
  sub: [],
  brand: [],
});

const isActive = ref(false);
const selectedSort = ref(route.query.sort || "popular");
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
////////////////////////////////////////////////////////////////////////////////
const { data: initialData } = await useAuthFetch("/products", {
  params: {
    gender: route.query.gender,
    category: route.query.category ? route.query.category.split(",") : undefined,
    sub: route.query.sub ? route.query.sub.split(",") : undefined,
    brand: route.query.brand ? route.query.brand.split(",") : undefined,
    min: route.query.min || undefined,
    max: route.query.max || undefined,
    sort: route.query.sort || "popular",
    cursor: null,
    limit: 100,
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
  if (loading.value) return;
  if (!reset && !hasMore.value) return;

  loading.value = true;

  try {
    const params = {
      gender: route.query.gender,
      category: route.query.category ? route.query.category.split(",") : undefined,
      sub: route.query.sub ? route.query.sub.split(",") : undefined,
      brand: route.query.brand ? route.query.brand.split(",") : undefined,
      min: route.query.min || undefined,
      max: route.query.max || undefined,
      sort: selectedSort.value,
      cursor: reset ? null : cursor.value,
      limit: 100,
    };

    const data = await use$Fetch("/products", {
      params,
    });

    if (reset) {
      products.value = [];
      cursor.value = null;
    }

    if (data?.products?.length) {
      if (reset) {
        products.value = data.products;
      } else {
        const existingIds = new Set(products.value.map((p) => p.id));
        const newProducts = data.products.filter((p) => !existingIds.has(p.id));
        products.value = [...products.value, ...newProducts];
      }
      cursor.value = data.nextCursor;
      hasMore.value = data.hasMore;
    } else {
      hasMore.value = false;
    }
  } catch (error) {
    console.error("Failed to load products:", error);
    hasMore.value = false;
  } finally {
    loading.value = false;
  }
};

const { toggleFavorite: toggleFavoriteAction } = useFavorite();

// 찜 관련 함수
const toggleFavorite = async (event, product) => {
  event.preventDefault();
  const success = await toggleFavoriteAction(product.mappingId);

  if (success) {
    product.favorite = !product.favorite;
  }
};

const toggleDropdown = () => {
  isActive.value = !isActive.value;
};

// Sort 선택시
const selectOption = async (option) => {
  selectedSort.value = option.value;
  isActive.value = false;

  try {
    const response = await use$Fetch("/products", {
      params: {
        gender: route.query.gender,
        category: route.query.category ? route.query.category.split(",") : undefined,
        sub: route.query.sub ? route.query.sub.split(",") : undefined,
        brand: route.query.brand ? route.query.brand.split(",") : undefined,
        min: route.query.min || undefined,
        max: route.query.max || undefined,
        sort: option.value,
        cursor: null,
        limit: 100,
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

const handleFilterChange = (filterData) => {
  selectedFilters.value = {
    category: filterData.category || [],
    sub: filterData.sub || [],
    brand: filterData.brand || [],
  };
  updateQueryParams(filterData);
};

const removeFilter = async (type, id) => {
  if (type === "category") {
    selectedFilters.value.category = selectedFilters.value.category.filter((item) => item.id !== id);
    selectedFilters.value.sub = [];
  } else {
    selectedFilters.value[type] = selectedFilters.value[type].filter((item) => item.id !== id);
  }

  await asideRef.value?.updateFilterState({
    type: type,
    id: id,
    checked: false,
  });

  // URL 쿼리 파라미터 업데이트
  const updatedQuery = { ...route.query };

  if (type === "category") {
    delete updatedQuery.category;
    delete updatedQuery.categoryName;
    delete updatedQuery.sub;
    delete updatedQuery.subName;
  } else if (type === "sub") {
    delete updatedQuery.sub;
    delete updatedQuery.subName;
  } else if (type === "brand") {
    if (selectedFilters.value.brand.length === 0) {
      delete updatedQuery.brand;
    } else {
      updatedQuery.brand = selectedFilters.value.brand.map((b) => b.id).join(",");
    }
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
  if (observer) {
    observer.disconnect();
  }

  observer = new IntersectionObserver(
    async (entries) => {
      const entry = entries[0];
      // 로딩 중이 아니고, 더 불러올 데이터가 있을 때만 실행
      if (entry.isIntersecting && !loading.value && hasMore.value) {
        await loadProducts();
      }
    },
    {
      rootMargin: "150px",
      threshold: 0.1,
    }
  );
};

const updateObserver = () => {
  if (!observer) return;

  observer.disconnect();

  const lastProduct = document.querySelector(".product-c-frame:last-child");
  if (lastProduct && hasMore.value) {
    observer.observe(lastProduct);
  }
};

watch(
  () => products.value,
  () => {
    nextTick(() => {
      if (!loading.value) {
        updateObserver();
      }
    });
  },
  { deep: true }
);

// URL 파라미터 변경 감지
watch(
  () => route.query.gender,
  async (newGender, oldGender) => {
    if (newGender && newGender !== oldGender) {
      gender.value = newGender;
      selectedSort.value = "popular";

      await router.replace({
        query: {
          ...route.query,
          sort: undefined,
        },
      });

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

onMounted(async () => {
  setupIntersectionObserver();
  nextTick(() => {
    updateObserver();
  });
  document.addEventListener("click", handleClickOutside);

  // 새로 고침시 찜 정보 업데이트
  try {
    const data = await use$Fetch("/products", {
      params: {
        gender: route.query.gender,
        category: route.query.category ? route.query.category.split(",") : undefined,
        sub: route.query.sub ? route.query.sub.split(",") : undefined,
        brand: route.query.brand ? route.query.brand.split(",") : undefined,
        min: route.query.min || undefined,
        max: route.query.max || undefined,
        sort: route.query.sort || "popular",
        cursor: null,
        limit: 100,
      },
    });

    if (data?.products) {
      products.value = products.value.map((product) => {
        const updatedProduct = data.products.find((p) => p.id === product.id);
        if (updatedProduct) {
          return {
            ...product,
            favorite: updatedProduct.favorite,
          };
        }
        return product;
      });
    }
  } catch (error) {
    console.error("Failed to load products with favorites:", error);
  }
});

onUnmounted(() => {
  if (observer) {
    observer.disconnect();
    observer = null;
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
                  :ref="index === products.length - 1 ? lastProductRef : undefined"
                >
                  <nuxt-link :to="`/products/${product.id}/detail`">
                    <div class="product-img-frame">
                      <div class="product-img-container">
                        <img class="product-images primary-img" :src="product.mainImageUrl" alt="product-img" />
                        <img class="product-images secondary-img" :src="product.hoverImageUrl" alt="product-img-hover" />
                      </div>
                      <button
                        :data-favorite="product.favorite"
                        @click.prevent="(e) => toggleFavorite(e, product)"
                        class="favorite-btn"
                      >
                        <img
                          class="favorite"
                          :src="product.favorite ? '/imgs/icon/favorite-fill.svg' : '/imgs/icon/favorite.svg'"
                          :alt="product.favorite ? '찜' : '찜 해제'"
                        />
                      </button>
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
