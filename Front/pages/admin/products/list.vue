<script setup>
import { ref, onMounted } from "vue";
import { useAuthFetch } from "~/composables/useAuthFetch";
import { use$Fetch } from "~/composables/use$Fetch";

useHead({
  link: [{ rel: "stylesheet", href: "/css/admin/product-list.css" }],
});

// 상태 관리
const products = ref([]);
const totalElements = ref(0);
const totalPages = ref(0);
const currentPage = ref(0);
const pageSize = ref(10);
const sortField = ref("id");

const showMappingModal = ref(false);
const showCompleteModal = ref(false);
const mappingMainProduct = ref(null);

const mappedProducts = ref({});
const expandedRows = ref(new Set());
const categories = ref({
  level1: [],
  level2: [],
  level3: [],
});

// Template refs
const startDatePicker = ref(null);
const endDatePicker = ref(null);
const startDateInput = ref(null);
const endDateInput = ref(null);
const searchForm = ref(null);

// 폼 데이터
const formData = ref({
  category1: "",
  category2: "",
  category3: "",
  searchType: "product_name",
  searchKeyword: "",
  priceMin: "",
  priceMax: "",
  active: "",
  dateType: "create_date",
  startDate: "",
  endDate: "",
});

// 체크박스 상태
const selectAll = ref(false);
const selectedItems = ref(new Set());

// 전체 리스트 또는 검색조건에 맞는 리스트 API호출
const fetchProducts = async () => {
  try {
    const params = new URLSearchParams({
      page: currentPage.value.toString(),
      size: pageSize.value.toString(),
      sort: sortField.value,
      presentId: "1",
    });

    if (formData.value.category1) params.append("category1", formData.value.category1);
    if (formData.value.category2) params.append("category2", formData.value.category2);
    if (formData.value.category3) params.append("category3", formData.value.category3);
    if (formData.value.searchKeyword && formData.value.searchType) {
      params.append("searchType", formData.value.searchType);
      params.append("searchKeyword", formData.value.searchKeyword);
    }
    if (formData.value.priceMin) params.append("minPrice", formData.value.priceMin);
    if (formData.value.priceMax) params.append("maxPrice", formData.value.priceMax);
    if (formData.value.active) params.append("active", formData.value.active === "1");
    if (formData.value.startDate) {
      params.append("dateType", formData.value.dateType);
      const date = new Date(formData.value.startDate);
      params.append("startDate", date.toISOString());
    }
    if (formData.value.endDate) {
      const date = new Date(formData.value.endDate);
      params.append("endDate", date.toISOString());
    }

    console.log("Search Parameters:", params.toString());

    const data = await use$Fetch(`/admin/products?${params.toString()}`);

    products.value = data.products;
    totalElements.value = data.totalElements;
    totalPages.value = data.totalPages;
    currentPage.value = data.currentPage;
  } catch (error) {
    console.error("Error fetching products:", error);
  }
};

// 매핑 모달 관련
const getProductName = (id) => {
  const mainProduct = products.value.find((p) => p.id === id);
  if (mainProduct) return mainProduct.productName;

  for (const mappedList of Object.values(mappedProducts.value)) {
    const mappedProduct = mappedList.find((p) => p.id === id);
    if (mappedProduct) return mappedProduct.productName;
  }
  return "";
};

const getProductDetails = (id) => {
  const mainProduct = products.value.find((p) => p.id === id);
  if (mainProduct) {
    return `(NO.${mainProduct.id}) ${mainProduct.brandEngName} / ${mainProduct.shoppingMallEngName} / ￦${formatNumber(
      mainProduct.salePrice
    )}`;
  }

  for (const mappedList of Object.values(mappedProducts.value)) {
    const mappedProduct = mappedList.find((p) => p.id === id);
    if (mappedProduct) {
      return `(NO.${mappedProduct.id}) ${mappedProduct.brandEngName} / ${mappedProduct.shoppingMallEngName} / ￦${formatNumber(
        mappedProduct.salePrice
      )}`;
    }
  }
  return "";
};

const handleMappingManagement = () => {
  if (selectedItems.value.size !== 2) {
    alert("매핑할 상품을 2개 선택해주세요.");
    return;
  }
  showMappingModal.value = true;
};

const handleMapping = async () => {
  try {
    const request = {
      mainProductId: mappingMainProduct.value,
      mappingProductIds: Array.from(selectedItems.value).filter((id) => id !== mappingMainProduct.value),
    };

    await use$Fetch(`/admin/products/mapping`, {
      method: "POST",
      body: request,
    });

    showMappingModal.value = false;
    showCompleteModal.value = true;
  } catch (error) {
    console.error("매핑 처리 중 오류:", error);
    alert("매핑 처리 중 오류가 발생했습니다.");
  }
};

const handleCloseCompleteModal = async () => {
  showCompleteModal.value = false;
  selectedItems.value.clear();
  await fetchProducts();

  for (const productId of expandedRows.value) {
    await fetchMappingProducts(productId);
  }
};

// 매핑된 상품만 가져오기
const fetchMappingProducts = async (productId) => {
  try {
    const data = await use$Fetch(`/admin/products/${productId}/mapping`);
    mappedProducts.value[productId] = data;
  } catch (error) {
    console.error("Error fetching mapping products:", error);
  }
};

//검색창 카테고리 가져오기
const fetchCategories = async (level, parentId = null) => {
  try {
    const params = new URLSearchParams();
    if (parentId) params.append("parentId", parentId);
    params.append("level", level);

    const data = await use$Fetch(`/admin/products/categories?${params.toString()}`);

    if (level === 1) {
      categories.value.level1 = data.value;
    } else if (level === 2) {
      categories.value.level2 = data.value;
    } else if (level === 3) {
      categories.value.level3 = data.value;
    }
  } catch (error) {
    console.error("Error fetching categories:", error);
  }
};

// active 상태 변경
const updateActive = async (productId, active) => {
  try {
    console.log(`Updating active status: productId=${productId}, active=${active}`);
    await use$Fetch(`/admin/products/${productId}/active`, {
      method: "PATCH",
      query: { active },
    });
  } catch (error) {
    console.error("Error updating active status:", error);
    throw new Error("Active 상태 변경에 실패했습니다.");
  }
};

// active 토글 핸들러
const handleActiveChange = async (product) => {
  await updateActive(product.id, product.active);
};

// state 변경
const updateState = async (productId, isMainProduct = false) => {
  try {
    if (isMainProduct) {
      await use$Fetch(`/admin/products/${productId}/promote`, {
        method: "PATCH",
      });
      fetchProducts();
    } else {
      await use$Fetch(`/admin/products/${productId}/state`, {
        method: "PATCH",
      });

      const parentProduct = products.value.find((p) => mappedProducts.value[p.id]?.some((mp) => mp.id === productId));

      if (parentProduct) {
        await fetchMappingProducts(parentProduct.id);
      }
    }
  } catch (error) {
    console.error("Error updating state:", error);
    alert(error.message || "상품 삭제 처리 중 오류가 발생했습니다.");
  }
};

// 삭제 버튼 클릭 핸들러
const handleDelete = async (productId, isMainProduct = false) => {
  const message = isMainProduct
    ? "대표상품을 삭제하면 첫 번째 매핑상품이 대표상품으로 변경됩니다. 계속하시겠습니까?"
    : "선택한 상품을 삭제하시겠습니까?";

  if (confirm(message)) {
    await updateState(productId, isMainProduct);
  }
};

// 카테고리 선택 핸들러
const handleCategory1Change = async () => {
  formData.value.category2 = "";
  formData.value.category3 = "";
  categories.value.level2 = [];
  categories.value.level3 = [];

  if (formData.value.category1) {
    await fetchCategories(2, formData.value.category1);
  }
};

const handleCategory2Change = async () => {
  formData.value.category3 = "";
  categories.value.level3 = [];

  if (formData.value.category2) {
    await fetchCategories(3, formData.value.category2);
  }
};

// 페이지네이션 핸들러
const pageChange = (newPage) => {
  currentPage.value = newPage;
  fetchProducts();
};

const pageSizeChange = (event) => {
  pageSize.value = parseInt(event.target.value);
  currentPage.value = 0;
  fetchProducts();
};

const getPageNumbers = () => {
  const pagePerGroup = 10; // 한 그룹당 보여줄 페이지 수
  const currentGroup = Math.floor(currentPage.value / pagePerGroup);
  const start = currentGroup * pagePerGroup + 1;
  const end = Math.min(start + pagePerGroup - 1, totalPages.value);

  return Array.from({ length: end - start + 1 }, (_, i) => start + i);
};

// Flatpickr 로딩 체크
const waitForFlatpickr = (callback) => {
  if (window.flatpickr) {
    callback();
  } else {
    setTimeout(() => waitForFlatpickr(callback), 100);
  }
};

// Flatpickr 초기화
const initializeFlatpickr = () => {
  const config = {
    dateFormat: "Y-m-d",
    showMonths: 1,
    static: true,
    monthSelectorType: "static",
    onChange: (selectedDates, dateStr, instance) => {
      if (instance.element === startDateInput.value) {
        formData.value.startDate = dateStr;
      } else if (instance.element === endDateInput.value) {
        formData.value.endDate = dateStr;
      }
    },
  };

  if (startDateInput.value && endDateInput.value) {
    startDatePicker.value = flatpickr(startDateInput.value, config);
    endDatePicker.value = flatpickr(endDateInput.value, config);
  }
};

// 날짜 설정 함수들
const setYesterday = () => {
  const yesterday = new Date();
  yesterday.setDate(yesterday.getDate() - 1);
  if (startDatePicker.value && endDatePicker.value) {
    startDatePicker.value.setDate(yesterday);
    endDatePicker.value.setDate(yesterday);
    formData.value.startDate = startDatePicker.value.formatDate(yesterday, "Y-m-d");
    formData.value.endDate = endDatePicker.value.formatDate(yesterday, "Y-m-d");
  }
};

const setToday = () => {
  const today = new Date();
  if (startDatePicker.value && endDatePicker.value) {
    startDatePicker.value.setDate(today);
    endDatePicker.value.setDate(today);
    formData.value.startDate = startDatePicker.value.formatDate(today, "Y-m-d");
    formData.value.endDate = endDatePicker.value.formatDate(today, "Y-m-d");
  }
};

const setThisWeek = () => {
  const today = new Date();
  const firstDayOfWeek = new Date(today);
  firstDayOfWeek.setDate(today.getDate() - today.getDay());
  const lastDayOfWeek = new Date(firstDayOfWeek);
  lastDayOfWeek.setDate(firstDayOfWeek.getDate() + 6);

  if (startDatePicker.value && endDatePicker.value) {
    startDatePicker.value.setDate(firstDayOfWeek);
    endDatePicker.value.setDate(lastDayOfWeek);
    formData.value.startDate = startDatePicker.value.formatDate(firstDayOfWeek, "Y-m-d");
    formData.value.endDate = endDatePicker.value.formatDate(lastDayOfWeek, "Y-m-d");
  }
};

const setThisMonth = () => {
  const today = new Date();
  const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
  const lastDayOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);

  if (startDatePicker.value && endDatePicker.value) {
    startDatePicker.value.setDate(firstDayOfMonth);
    endDatePicker.value.setDate(lastDayOfMonth);
    formData.value.startDate = startDatePicker.value.formatDate(firstDayOfMonth, "Y-m-d");
    formData.value.endDate = endDatePicker.value.formatDate(lastDayOfMonth, "Y-m-d");
  }
};

// 체크박스 핸들러
const handleSelectAll = (event) => {
  event.stopPropagation();
  event.preventDefault();
  const checked = event.target.checked;
  if (checked) {
    selectedItems.value = new Set(products.value.map((product) => product.id));
  } else {
    selectedItems.value.clear();
  }
  selectAll.value = checked;
};

const handleSelectItem = (event, productId) => {
  event.stopPropagation();
  event.preventDefault();
  if (selectedItems.value.has(productId)) {
    selectedItems.value.delete(productId);
  } else {
    selectedItems.value.add(productId);
  }
  selectAll.value = selectedItems.value.size === products.value.length;
};

// 토글 핸들러
const toggleProductRows = async (productId) => {
  if (!mappedProducts.value[productId]) {
    await fetchMappingProducts(productId);
  }

  if (expandedRows.value.has(productId)) {
    expandedRows.value.delete(productId);
  } else {
    expandedRows.value.add(productId);
  }
};
const isExpanded = (productId) => expandedRows.value.has(productId);

// 검색 핸들러
const handleSearch = (event) => {
  event.preventDefault();
  currentPage.value = 0;
  fetchProducts();
};

// 날짜 포맷팅 함수
const formatDate = (dateString) => {
  if (!dateString) return "";

  // UTC 시간을 그대로 사용 (이미 KST로 저장되어 있으므로)
  const date = new Date(dateString);

  const ymd = `${date.getUTCFullYear()}-${String(date.getUTCMonth() + 1).padStart(2, "0")}-${String(date.getUTCDate()).padStart(
    2,
    "0"
  )}`;

  const time = `${String(date.getUTCHours()).padStart(2, "0")}:${String(date.getUTCMinutes()).padStart(2, "0")}:${String(
    date.getUTCSeconds()
  ).padStart(2, "0")}`;

  return [ymd, time];
};

// 숫자 포맷팅 함수
const formatNumber = (number) => {
  return number?.toLocaleString("ko-KR") || "0";
};

onMounted(() => {
  fetchCategories(1);
  fetchProducts();
  waitForFlatpickr(() => {
    initializeFlatpickr();
  });
});
</script>

<template>
  <main>
    <!-- 매핑 선택 모달 -->
    <div v-if="showMappingModal" class="mapping-modal">
      <div class="modal-content">
        <h3>매핑 관리</h3>
        <br />
        <div class="selected-products">
          <div v-for="id in selectedItems" :key="id" class="product-item">
            <input type="radio" :value="id" v-model="mappingMainProduct" name="mainProduct" :id="`product-${id}`" />
            <label :for="`product-${id}`" class="product-info">
              <div class="product-name">상품명 : {{ getProductName(id) }}</div>
              <div class="product-details">
                {{ getProductDetails(id) }}
              </div>
            </label>
          </div>
        </div>
        <p class="info-text">
          <strong><br />* 선택한 상품 중 하나를 대표 상품으로 지정해주세요.</strong><br />
        </p>
        <div class="modal-actions">
          <button class="btn btn-primary" @click="handleMapping" :disabled="!mappingMainProduct">매핑 처리</button>
          <button class="btn btn-secondary" @click="showMappingModal = false">취소</button>
        </div>
      </div>
    </div>

    <!-- 완료 알림 모달 -->
    <div v-if="showCompleteModal" class="complete-modal">
      <div class="modal-content">
        <p>매핑이 완료되었습니다.</p>
        <div class="modal-actions">
          <button class="btn btn-primary" @click="handleCloseCompleteModal">확인</button>
        </div>
      </div>
    </div>

    <h1 class="title">상품</h1>
    <ul class="breadcrumbs">
      <li><a href="#">FUPTO</a></li>
      <li class="divider">/</li>
      <li><a href="#" class="active">상품 목록</a></li>
    </ul>

    <!-- 검색 폼 -->
    <div class="card">
      <div class="card-body">
        <form ref="searchForm" @submit="handleSearch">
          <table class="table">
            <tbody>
              <tr>
                <th>카테고리</th>
                <td>
                  <select v-model="formData.category1" class="select" @change="handleCategory1Change">
                    <option value="">1차 카테고리</option>
                    <option v-for="cat in categories.level1" :key="cat.id" :value="cat.id">
                      {{ cat.name }}
                    </option>
                  </select>
                  <select v-model="formData.category2" class="select" @change="handleCategory2Change">
                    <option value="">2차 카테고리</option>
                    <option v-for="cat in categories.level2" :key="cat.id" :value="cat.id">
                      {{ cat.name }}
                    </option>
                  </select>
                  <select v-model="formData.category3" class="select">
                    <option value="">3차 카테고리</option>
                    <option v-for="cat in categories.level3" :key="cat.id" :value="cat.id">
                      {{ cat.name }}
                    </option>
                  </select>
                </td>
              </tr>
              <tr>
                <th>검색어</th>
                <td>
                  <select v-model="formData.searchType" class="select">
                    <option value="product_name">상품명</option>
                    <option value="brand_name">브랜드</option>
                    <option value="shop_mall">쇼핑몰</option>
                  </select>
                  <input type="text" v-model="formData.searchKeyword" class="input-text" />
                </td>
              </tr>
              <tr>
                <th>판매가</th>
                <td>
                  <input type="number" v-model="formData.priceMin" class="input-text price" /> 원 ~
                  <input type="number" v-model="formData.priceMax" class="input-text price" /> 원
                </td>
              </tr>
              <tr>
                <th>공개여부</th>
                <td>
                  <select v-model="formData.active" class="select">
                    <option value="">전체</option>
                    <option value="0">비공개</option>
                    <option value="1">공개</option>
                  </select>
                </td>
              </tr>
              <tr>
                <th>날 짜</th>
                <td>
                  <select v-model="formData.dateType" class="select">
                    <option value="create_date">등록일</option>
                    <option value="update_date">수정일</option>
                  </select>
                  <input ref="startDateInput" type="text" class="input-text date" readonly v-model="formData.startDate" />
                  <button type="button" class="btn-calendar" @click="() => startDatePicker?.open()">&#x1F4C5;</button>
                  ~
                  <input ref="endDateInput" type="text" class="input-text date" readonly v-model="formData.endDate" />
                  <button type="button" class="btn-calendar mr-2" @click="() => endDatePicker?.open()">&#x1F4C5;</button>
                  <button type="button" class="btn btn-primary mr-2" @click="setYesterday">어제</button>
                  <button type="button" class="btn btn-primary mr-2" @click="setToday">오늘</button>
                  <button type="button" class="btn btn-primary mr-2" @click="setThisWeek">이번 주</button>
                  <button type="button" class="btn btn-primary" @click="setThisMonth">이번 달</button>
                </td>
              </tr>
            </tbody>
          </table>
          <div class="text-center">
            <button type="submit" class="btn btn-primary btn-modify">검&nbsp;&nbsp;&nbsp;색</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 상품 목록 -->
    <div class="card">
      <div class="card-body">
        <div class="d-flex">
          <select class="select mr-2" v-model="pageSize" @change="pageSizeChange">
            <option :value="2">2</option>
            <option :value="10">10</option>
            <option :value="25">25</option>
            <option :value="50">50</option>
            <option :value="100">100</option>
          </select>
          <button class="btn btn-primary mr-2" @click="handleMappingManagement" :disabled="selectedItems.size < 2">
            매핑 관리
          </button>
          <router-link to="/admin/products/reg" class="btn btn-primary">+ Add Product</router-link>
        </div>

        <table class="table product-list-table">
          <thead>
            <tr class="text-md">
              <th>
                <input type="checkbox" id="selectAll" :checked="selectAll" @change="handleSelectAll" class="pl-checkbox" />
              </th>
              <th>NO.</th>
              <th>PRODUCT</th>
              <th>SHOP MALL</th>
              <th>CATEGORY</th>
              <th>RETAIL PRICE</th>
              <th>SALE PRICE</th>
              <th>URL</th>
              <th>ACT</th>
              <th>DATE</th>
              <th>EDIT</th>
            </tr>
          </thead>
          <tbody>
            <template v-for="p in products" :key="p.id">
              <!-- 메인 상품 행 -->
              <tr>
                <td>
                  <input
                    type="checkbox"
                    :id="'product' + p.id"
                    :checked="selectedItems.has(p.id)"
                    @change="(e) => handleSelectItem(e, p.id)"
                    class="pl-checkbox"
                  />
                </td>
                <td>{{ p.id }}</td>
                <td class="product-cell">
                  <div class="d-flex align-items-center">
                    <img :src="p.imagePath" :alt="p.productName" class="product-img" />
                    <div class="product-info ml-2">
                      <span class="text-md">{{ p.productName }}</span>
                      <small class="d-block text-muted text-sm">{{ p.brandEngName }}</small>
                    </div>
                  </div>
                </td>
                <td class="text-md">{{ p.shoppingMallEngName }}</td>
                <td>
                  <span class="badge-pill badge-light text-md">{{ p.categoryName }}</span>
                </td>
                <td class="text-md">￦{{ formatNumber(p.retailPrice) }}</td>
                <td class="text-md">￦{{ formatNumber(p.salePrice) }}</td>
                <td>
                  <button class="btn btn-outline-primary btn-sm">
                    <a :href="p.url" target="_blank">판매처<br />이동</a>
                  </button>
                </td>
                <td>
                  <label class="pl-switch">
                    <input type="checkbox" :id="'active' + p.id" v-model="p.active" @change="() => handleActiveChange(p)" />
                    <span class="pl-slider round"></span>
                  </label>
                </td>
                <td class="date-cell">
                  <div>{{ formatDate(p.updateDate)[0] }}</div>
                  <div>{{ formatDate(p.updateDate)[1] }}</div>
                </td>
                <td>
                  <button class="btn btn-outline-secondary btn-sm" @click="() => toggleProductRows(p.id)">
                    <i class="bx" :class="isExpanded(p.id) ? 'bx-chevron-up' : 'bx-chevron-down'"></i>
                  </button>
                  <NuxtLink :to="`/admin/products/${p.id}/edit`" class="btn btn-outline-secondary btn-sm">
                    <i class="bx bxs-pencil"></i>
                  </NuxtLink>
                  <button class="btn btn-outline-danger btn-sm" @click="() => handleDelete(p.id, true)">
                    <i class="bx bx-trash"></i>
                  </button>
                </td>
              </tr>

              <!-- 매핑된 상품 행 -->
              <tr v-if="isExpanded(p.id)" class="mapping-products">
                <td colspan="11">
                  <template v-if="mappedProducts[p.id] && mappedProducts[p.id].length > 0">
                    <table class="table">
                      <tbody>
                        <tr v-for="mp in mappedProducts[p.id]" :key="mp.id">
                          <td>
                            <input
                              type="checkbox"
                              :id="'product' + mp.id"
                              :checked="selectedItems.has(mp.id)"
                              @change="(e) => handleSelectItem(e, mp.id)"
                              class="pl-checkbox"
                            />
                          </td>
                          <td>{{ mp.id }}</td>
                          <td class="product-cell">
                            <div class="d-flex align-items-center">
                              <img
                                :src="mp.imagePath || 'https://via.placeholder.com/70'"
                                :alt="mp.productName"
                                class="product-img"
                              />
                              <div class="product-info ml-2">
                                <span class="text-md">{{ mp.productName }}</span>
                                <small class="d-block text-muted text-sm">{{ mp.brandEngName }}</small>
                              </div>
                            </div>
                          </td>
                          <td class="text-md">{{ mp.shoppingMallEngName }}</td>
                          <td>
                            <span class="badge-pill badge-light text-md">{{ mp.categoryName }}</span>
                          </td>
                          <td class="text-md">￦{{ formatNumber(mp.retailPrice) }}</td>
                          <td class="text-md">￦{{ formatNumber(mp.salePrice) }}</td>
                          <td>
                            <button class="btn btn-outline-primary btn-sm">
                              <a :href="mp.url" target="_blank">판매처<br />이동</a>
                            </button>
                          </td>
                          <td>
                            <label class="pl-switch">
                              <input
                                type="checkbox"
                                :id="'active' + mp.id"
                                v-model="mp.active"
                                @change="() => handleActiveChange(mp)"
                              />
                              <span class="pl-slider round"></span>
                            </label>
                          </td>
                          <td class="date-cell">
                            <div>{{ formatDate(mp.updateDate)[0] }}</div>
                            <div>{{ formatDate(mp.updateDate)[1] }}</div>
                          </td>
                          <td>
                            <NuxtLink :to="`/admin/products/${mp.id}/edit`" class="btn btn-outline-secondary btn-sm">
                              <i class="bx bxs-pencil"></i>
                            </NuxtLink>
                            <button class="btn btn-outline-danger btn-sm" @click="() => handleDelete(mp.id, false)">
                              <i class="bx bx-trash"></i>
                            </button>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </template>
                  <template v-else>
                    <div class="no-mapping-message">현재 이 상품은 매핑 상품이 존재하지 않습니다.</div>
                  </template>
                </td>
              </tr>
            </template>
          </tbody>
        </table>

        <!-- 페이지네이션 -->
        <div class="pagination-container mt-4">
          <ul class="pagination justify-content-center">
            <li class="page-item" :class="{ disabled: currentPage === 0 }">
              <button class="page-link" @click="pageChange(0)">&lt;&lt;</button>
            </li>
            <li class="page-item" :class="{ disabled: currentPage === 0 }">
              <button class="page-link" @click="pageChange(currentPage - 1)">이전</button>
            </li>
            <li
              v-for="pageNum in getPageNumbers()"
              :key="pageNum"
              class="page-item"
              :class="{ active: pageNum - 1 === currentPage }"
            >
              <button class="page-link" @click="pageChange(pageNum - 1)">
                {{ pageNum }}
              </button>
            </li>
            <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
              <button class="page-link" @click="pageChange(currentPage + 1)">다음</button>
            </li>
            <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
              <button class="page-link" @click="pageChange(totalPages - 1)">&gt;&gt;</button>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </main>
</template>

<style>
/*페이지네이션*/
.pagination-container {
  margin-top: 1rem;
}

.pagination {
  display: flex;
  justify-content: center;
  list-style: none;
  padding: 0;
}

.page-item {
  margin: 0 0.25rem;
}

.page-link {
  padding: 0.5rem 1rem;
  border: 1px solid #dee2e6;
  background-color: #fff;
  color: #007bff;
  cursor: pointer;
}

.page-item.active .page-link {
  background-color: #007bff;
  border-color: #007bff;
  color: #fff;
}

.page-item.disabled .page-link {
  color: #6c757d;
  pointer-events: none;
  background-color: #fff;
  border-color: #dee2e6;
}

.date-cell {
  text-align: center;
  vertical-align: middle;
  padding: 8px;
}

.pl-switch {
  position: relative;
  display: inline-block;
  width: 35px;
  height: 20px;
}

.pl-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.pl-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.4s;
  border-radius: 34px;
}

.pl-slider:before {
  position: absolute;
  content: "";
  height: 14px;
  width: 14px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.4s;
  border-radius: 50%;
}
.pl-slider.round {
  border-radius: 34px;
}

.pl-slider.round:before {
  border-radius: 50%;
}

input:checked + .pl-slider {
  background-color: #2196f3;
}

input:checked + .pl-slider:before {
  transform: translateX(15px);
}

/*매핑 모달*/
.confirm-modal,
.mapping-modal,
.complete-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 24px;
  border-radius: 8px;
  width: fit-content;
  min-width: 400px;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 20px;
}

.product-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.product-item input[type="radio"] {
  margin-right: 8px;
  transform: scale(1.5);
}

.product-info {
  display: flex;
  flex-direction: column;
}
</style>
