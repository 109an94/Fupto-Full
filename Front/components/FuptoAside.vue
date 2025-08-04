<script setup>
import { ref, computed, watch } from "vue";

useHead({
  link: [{ rel: "stylesheet", href: "/css/aside.css" }],
});

const route = useRoute();
const emit = defineEmits(["filter-change", "search"]);

const props = defineProps({
  initialGender: {
    type: String,
    default: null,
  },
});

//성별 관련
const selectedGender = ref(props.initialGender);

const toggleGender = async (gender) => {
  selectedGender.value = gender;
  const genderId = gender === "male" ? "1" : "2";
  clearPriceRange();
  emit("filter-change", { gender: genderId });
  await loadSecondCategories(genderId);
  await loadBrands();
};

//카테고리 관련
const categories = ref([]);

//2차 카테고리 로드
const loadSecondCategories = async (genderId) => {
  if (!genderId) return;

  try {
    const data = await use$Fetch("/products/categories", {
      params: { parentId: genderId },
    });

    if (data) {
      categories.value = data.map((category) => ({
        id: category.id,
        name: category.name,
        checked: false,
        isExpanded: false,
        subCategories: [{ id: `all-${category.id}`, name: "All", checked: true }],
      }));

      if (route.query.cat) {
        const selectedCatIds = route.query.cat.split(",");
        await restoreSelectedCategories(selectedCatIds);
      }
    }
  } catch (error) {
    console.error("Error loading second categories:", error);
  }
};

const loadThirdCategories = async (category) => {
  try {
    const data = await use$Fetch("/products/categories", {
      params: { parentId: category.id },
    });

    if (data) {
      category.subCategories = [
        { id: `all-${category.id}`, name: "All", checked: true },
        ...data.map((subCat) => ({
          id: subCat.id.toString(),
          name: subCat.name,
          checked: false,
        })),
      ];
    }
  } catch (error) {
    console.error("Error loading third categories:", error);
  }
};

const toggleCategory = async (category) => {
  category.isExpanded = category.checked;
  if (category.checked && category.subCategories.length === 1) {
    await loadThirdCategories(category);
  }
  emitFilterChange();
};

const handleSubCategoryChange = (category, subCategory) => {
  const isAll = subCategory.id.startsWith("all-");

  if (isAll) {
    // All 버튼이 체크되면 다른 모든 항목 해제
    if (subCategory.checked) {
      category.subCategories.forEach((sub) => {
        if (sub.id !== subCategory.id) {
          sub.checked = false;
        }
      });
    }
  } else {
    // 일반 항목이 체크되면 All 버튼 해제
    const allButton = category.subCategories.find((sub) => sub.id.startsWith("all-"));
    if (allButton) {
      allButton.checked = false;
    }

    // 모든 일반 항목이 해제되면 All 버튼 체크
    const hasCheckedItems = category.subCategories.some((sub) => !sub.id.startsWith("all-") && sub.checked);
    if (!hasCheckedItems) {
      allButton.checked = true;
    }
  }

  emitFilterChange();
};

const getSelectedCategories = () => {
  const selectedCategories = {
    category: [],
    sub: [],
  };

  categories.value.forEach((category) => {
    if (category.checked) {
      selectedCategories.category.push({
        id: category.id,
        name: category.name,
      });

      const allButton = category.subCategories.find((sub) => sub.id.startsWith("all-"));
      if (!allButton?.checked) {
        category.subCategories.forEach((subCat) => {
          if (subCat.checked && !subCat.id.startsWith("all-")) {
            selectedCategories.sub.push({
              id: subCat.id,
              name: subCat.name,
            });
          }
        });
      }
    }
  });

  return selectedCategories;
};

const clearAll = () => {
  categories.value.forEach((category) => {
    category.checked = false;
    category.isExpanded = false;
    category.subCategories.forEach((sub, index) => {
      sub.checked = index === 0;
    });
  });
  emitFilterChange();
};

//브랜드 관련
const searchQuery = ref("");
const brands = ref([]);

const loadBrands = async () => {
  try {
    const data = await use$Fetch("/products/brands");

    if (data) {
      brands.value = data.map((brand) => ({
        id: `brand-${brand.id}`,
        originalId: brand.id,
        name: `${brand.engName}(${brand.korName})`,
        checked: false,
      }));

      if (route.query.brand) {
        const selectedBrandIds = route.query.brand.split(",");
        brands.value.forEach((brand) => {
          brand.checked = selectedBrandIds.includes(brand.originalId.toString());
        });
      }
    }
  } catch (error) {
    console.error("Error loading brands:", error);
  }
};

const filteredBrands = computed(() => {
  if (!searchQuery.value) return brands.value;

  const searchValue = searchQuery.value;
  return brands.value.filter((brand) => {
    const name = brand.name;
    if (/[a-zA-Z]/.test(searchValue)) {
      return name.toLowerCase().includes(searchValue.toLowerCase());
    }
    return name.includes(searchValue);
  });
});

const getSelectedBrands = () => {
  return brands.value
    .filter((brand) => brand.checked)
    .map((brand) => ({
      id: brand.originalId,
      name: brand.name,
    }));
};

const clearBrands = () => {
  brands.value.forEach((brand) => (brand.checked = false));
  searchQuery.value = "";
  emitFilterChange();
};

//가격 관련
const minPrice = ref("");
const maxPrice = ref("");
const priceError = ref("");

const clearPriceRange = () => {
  minPrice.value = "";
  maxPrice.value = "";
  priceError.value = "";
};

const formattedMinPrice = computed({
  get: () => {
    return minPrice.value ? Number(minPrice.value).toLocaleString() : "";
  },
  set: (value) => {
    minPrice.value = value.replace(/,/g, "");
  },
});

const formattedMaxPrice = computed({
  get: () => {
    return maxPrice.value ? Number(maxPrice.value).toLocaleString() : "";
  },
  set: (value) => {
    maxPrice.value = value.replace(/,/g, "");
  },
});

const validatePrices = () => {
  priceError.value = "";
  return true;
};

// 필터 변경 이벤트 발생
const emitFilterChange = () => {
  const selectedCats = getSelectedCategories();
  const selectedBrands = getSelectedBrands();

  const filterData = {
    category: selectedCats.category,
    sub: selectedCats.sub,
    brand: selectedBrands,
  };

  emit("filter-change", filterData);
};

// 검색 버튼 클릭
const handleSearch = () => {
  if (minPrice.value && maxPrice.value) {
    if (Number(minPrice.value) > Number(maxPrice.value)) {
      priceError.value = "최소 금액이 최대 금액보다 큽니다.<br>다시 입력해 주세요.";
      return;
    }
  }

  const selectedCats = getSelectedCategories();
  const filterData = {
    category: selectedCats.category,
    sub: selectedCats.sub,
    brand: getSelectedBrands(),
    min: minPrice.value ? parseInt(minPrice.value) : null,
    max: maxPrice.value ? parseInt(maxPrice.value) : null,
  };

  emit("search", filterData);
};

defineExpose({
  updateFilterState(data) {
    if (data.type === "brand") {
      const brand = brands.value.find((b) => b.originalId === data.id);
      if (brand) {
        brand.checked = data.checked;
      }
    } else if (data.type === "category") {
      const category = categories.value.find((cat) => cat.id === data.id);
      if (category) {
        category.checked = data.checked;
        category.isExpanded = false;
        category.subCategories.forEach((sub, index) => {
          sub.checked = index === 0;
        });
      }
    } else if (data.type === "sub") {
      categories.value.forEach((category) => {
        const subCategory = category.subCategories.find((sub) => sub.id === data.id);
        if (subCategory) {
          subCategory.checked = data.checked;
          const hasCheckedItems = category.subCategories.some((sub) => !sub.id.startsWith("all-") && sub.checked);
          const allButton = category.subCategories.find((sub) => sub.id.startsWith("all-"));
          if (allButton) {
            allButton.checked = !hasCheckedItems;
          }
        }
      });
    }
  },
});

const restoreSelectedCategories = async (selectedCatIds) => {
  if (route.query.category) {
    const categoryIds = route.query.category.split(",");
    for (const category of categories.value) {
      if (categoryIds.includes(category.id.toString())) {
        category.checked = true;
        category.isExpanded = true;
        if (category.subCategories.length === 1) {
          await loadThirdCategories(category);
        }
      }
    }
  }

  if (route.query.sub) {
    const subIds = route.query.sub.split(",");
    for (const category of categories.value) {
      if (category.checked) {
        await loadThirdCategories(category);
        const allButton = category.subCategories.find((sub) => sub.id.startsWith("all-"));

        category.subCategories.forEach((sub) => {
          if (subIds.includes(sub.id.toString())) {
            sub.checked = true;
            if (allButton) {
              allButton.checked = false;
            }
          }
        });
      }
    }
  }
};

watch(
  [() => route.query.gender, () => props.initialGender],
  async ([queryGender, propGender]) => {
    const genderValue = queryGender || propGender;
    if (genderValue) {
      selectedGender.value = genderValue === "1" ? "male" : "female";
      clearPriceRange();
      await loadSecondCategories(genderValue);
      if (route.query.category || route.query.sub) {
        await restoreSelectedCategories();
        emitFilterChange();
      }
    }
  },
  { immediate: true }
);

onMounted(async () => {
  await loadBrands();
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

      <section v-if="selectedGender">
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
          <input
            type="text"
            :value="searchQuery"
            @input="(e) => (searchQuery = e.target.value)"
            placeholder="브랜드 검색"
            class="aside-brand-search-input"
          />
        </div>
        <div class="aside-brand-list">
          <div v-for="brand in filteredBrands" :key="brand.id" class="aside-brand-item">
            <input type="checkbox" :id="brand.id" v-model="brand.checked" @change="emitFilterChange" />
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
            <input type="text" v-model="formattedMinPrice" placeholder="최소" @input="validatePrices" maxlength="12" />
          </div>
          <span class="price-separator">-</span>
          <div class="price-input">
            <span class="currency">₩</span>
            <input type="text" v-model="formattedMaxPrice" placeholder="최대" @input="validatePrices" maxlength="12" />
          </div>
        </div>
        <div v-if="priceError" class="price-error" v-html="priceError"></div>
      </section>

      <button class="search-button" @click="handleSearch">검 색</button>
    </div>
  </aside>
</template>
