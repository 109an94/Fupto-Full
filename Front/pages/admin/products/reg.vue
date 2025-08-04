<script setup>
import { ref, computed, onMounted } from "vue";
import SearchableSelect from "~/components/admin/SearchableSelect.vue";

useHead({
  link: [{ rel: "stylesheet", href: "/css/admin/product-reg.css" }],
});

const products = ref([]);
const nextId = ref(1);

const categories = ref({
  level1: [],
  level2: [],
  level3: [],
});

const brands = ref([]);
const shops = ref([]);
const config = useRuntimeConfig();

const addProductForm = () => {
  const lastProduct = products.value[products.value.length - 1];

  products.value.push({
    presentId: nextId.value === 1,
    id: nextId.value++,
    active: true,
    category1: lastProduct ? lastProduct.category1 : "",
    category2: lastProduct ? lastProduct.category2 : "",
    category3: lastProduct ? lastProduct.category3 : "",
    brandId: lastProduct ? lastProduct.brandId : "",
    shoppingMallId: "",
    url: "https://",
    productName: "",
    retailPrice: "",
    salePrice: "",
    description: "",
    images: [],
    imageNames: [],
    fileList: [],
  });

  if (lastProduct && lastProduct.category1) {
    fetchCategories(2, lastProduct.category1);
    if (lastProduct.category2) {
      fetchCategories(3, lastProduct.category2);
    }
  }
};

// 카테고리 데이터 가져오기
const fetchCategories = async (level, parentId = null) => {
  try {
    const params = new URLSearchParams();
    if (parentId) params.append("parentId", parentId);
    params.append("level", level);

    const data = await use$Fetch(`/admin/products/categories?${params.toString()}`);

    if (level === 1) {
      categories.value.level1 = data;
    } else if (level === 2) {
      categories.value.level2 = data;
    } else if (level === 3) {
      categories.value.level3 = data;
    }
  } catch (error) {
    console.error("Error fetching categories:", error);
  }
};

// 카테고리 선택 핸들러
const handleCategory1Change = async (product) => {
  product.category2 = "";
  product.category3 = "";
  categories.value.level2 = [];
  categories.value.level3 = [];

  if (product.category1) {
    await fetchCategories(2, product.category1);
  }
};

const handleCategory2Change = async (product) => {
  product.category3 = "";
  categories.value.level3 = [];

  if (product.category2) {
    await fetchCategories(3, product.category2);
  }
};

const fetchBrands = async () => {
  try {
    const data = await use$Fetch("/admin/products/brands");

    brands.value = data.map((brand) => ({
      id: brand.id,
      name: `${brand.engName}(${brand.korName})`,
    }));
  } catch (error) {
    console.error("Error fetching brands:", error);
  }
};

const fetchShops = async () => {
  try {
    const data = await use$Fetch("/admin/products/shopping-malls");

    shops.value = data.map((shop) => ({
      id: shop.id,
      name: `${shop.engName}(${shop.korName})`,
    }));
  } catch (error) {
    console.error("Error fetching shopping malls:", error);
  }
};

const removeProductForm = (id) => {
  const index = products.value.findIndex((p) => p.id === id);
  if (index !== -1) {
    products.value.splice(index, 1);
  }
  products.value.forEach((product, index) => {
    product.id = index + 1;
  });
  nextId.value = products.value.length + 1;
};

const updatepresentIdProduct = (id) => {
  const currentProduct = products.value.find((product) => product.id === id);

  if (!currentProduct.presentId) {
    products.value.forEach((product) => {
      product.presentId = false;
    });
  } else {
    products.value.forEach((product) => {
      product.presentId = product.id === id;
    });
  }

  console.log(
    "Updated products:",
    products.value.map((p) => ({ id: p.id, presentId: p.presentId }))
  );
};

const handleActiveChange = (product) => {
  console.log(`Product ${product.id} active status: ${product.active}`);
};

const formatNumber = (value) => {
  if (!value) return "";
  return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

const unformatNumber = (value) => {
  if (!value) return "";
  return value.toString().replace(/,/g, "");
};

const triggerFileUpload = (id) => {
  const fileInput = document.querySelector(`#product-form-${id} input[type="file"]`);
  if (fileInput) fileInput.click();
};

const handleFileUpload = (event, id) => {
  const files = event.target.files;
  const product = products.value.find((p) => p.id === id);
  if (product) {
    product.fileList = [...(product.fileList || []), ...Array.from(files)];
    product.images = product.fileList.map((file) => URL.createObjectURL(file));
    product.imageNames = product.fileList.map((file) => file.name);
  }
};

const removeImage = (productId, imageIndex) => {
  const product = products.value.find((p) => p.id === productId);
  if (product && product.fileList) {
    URL.revokeObjectURL(product.images[imageIndex]);
    product.fileList.splice(imageIndex, 1);
    product.images.splice(imageIndex, 1);
    product.imageNames.splice(imageIndex, 1);
  }
};

const submitForm = async () => {
  try {
    const productsData = products.value.map((product) => ({
      formId: product.id,
      active: product.active,
      presentId: product.presentId,
      categoryId: product.category3,
      brandId: product.brandId,
      shoppingMallId: product.shoppingMallId,
      url: product.url,
      productName: product.productName,
      retailPrice: parseInt(product.retailPrice),
      salePrice: parseInt(product.salePrice),
      description: product.description,
      imageFileNames: product.fileList?.map((file) => file.name) || [],
    }));

    console.log("productsData:", productsData);

    const formData = new FormData();
    formData.append(
      "data",
      new Blob([JSON.stringify(productsData)], {
        type: "application/json",
      })
    );

    products.value.forEach((product) => {
      if (product.fileList && product.fileList.length > 0) {
        product.fileList.forEach((file) => {
          formData.append(`files_${product.id}`, file);
        });
      }
    });

    await use$Fetch("/admin/products", {
      method: "POST",
      body: formData,
    });

    alert("상품 등록이 완료 되었습니다.");
    await navigateTo("/admin/products/list");
  } catch (error) {
    console.error("Error submitting form:", error);
    alert("상품 등록 중 오류가 발생했습니다.");
  }
};

const getImageNamesString = computed(() => (productId) => {
  const product = products.value.find((p) => p.id === productId);
  return product ? product.imageNames.join(", ") : "";
});

onMounted(async () => {
  await Promise.all([fetchCategories(1), fetchBrands(), fetchShops()]);
  addProductForm();
});
</script>
<template>
  <div>
    <main>
      <h1 class="title">상품</h1>
      <ul class="breadcrumbs">
        <li><a href="#">FUPTO</a></li>
        <li class="divider">/</li>
        <li><a href="#" class="active">상품 등록</a></li>
      </ul>
      <div class="product-form-container">
        <section>
          <form id="bulk-product-form" @submit.prevent="submitForm" enctype="multipart/form-data" class="n-form">
            <div id="product-forms-container">
              <div v-for="(product, index) in products" :key="product.id" :id="`product-form-${product.id}`" class="product-form">
                <h3 class="n-heading:6">
                  상품 {{ product.id }}
                  <div class="form-header-controls">
                    <div class="switch-container">
                      <label class="pl-switch">
                        <input
                          type="checkbox"
                          :id="'active' + product.id"
                          v-model="product.active"
                          @change="() => handleActiveChange(product)"
                        />
                        <span class="pl-slider round"></span>
                      </label>
                      <span class="switch-label mr-2">공개 여부</span>
                    </div>
                    <label class="representative-product">
                      <input type="checkbox" v-model="product.presentId" @change="updatepresentIdProduct(product.id)" />
                      <span class="switch-label ml-2">대표 상품</span>
                    </label>
                  </div>
                </h3>
                <!-- 기존 폼 필드들 -->
                <div class="form-row">
                  <label>카테고리 :</label>
                  <select v-model="product.category1" required @change="() => handleCategory1Change(product)">
                    <option value="">1차 카테고리</option>
                    <option v-for="cat in categories.level1" :key="cat.id" :value="cat.id">
                      {{ cat.name }}
                    </option>
                  </select>
                  <select v-model="product.category2" required class="ml:2" @change="() => handleCategory2Change(product)">
                    <option value="">2차 카테고리</option>
                    <option v-for="cat in categories.level2" :key="cat.id" :value="cat.id">
                      {{ cat.name }}
                    </option>
                  </select>
                  <select v-model="product.category3" required class="ml:2">
                    <option value="">3차 카테고리</option>
                    <option v-for="cat in categories.level3" :key="cat.id" :value="cat.id">
                      {{ cat.name }}
                    </option>
                  </select>
                </div>

                <div class="form-row">
                  <label>브랜드 :</label>
                  <SearchableSelect v-model="product.brandId" :options="brands" placeholder="브랜드 선택" required />
                </div>
                <div class="form-row">
                  <label>쇼핑몰 :</label>
                  <SearchableSelect v-model="product.shoppingMallId" :options="shops" placeholder="쇼핑몰 선택" required />
                </div>

                <div class="form-row">
                  <label>URL:</label>
                  <input type="url" v-model="product.url" required />
                </div>
                <div class="form-row">
                  <label>상품이름 :</label>
                  <input type="text" v-model="product.productName" required />
                </div>
                <div class="form-row">
                  <label>소비자가 :</label>
                  <input
                    type="text"
                    :value="formatNumber(product.retailPrice)"
                    @input="(e) => (product.retailPrice = unformatNumber(e.target.value))"
                    required
                  />
                </div>
                <div class="form-row">
                  <label>할인가 :</label>
                  <input
                    type="text"
                    :value="formatNumber(product.salePrice)"
                    @input="(e) => (product.salePrice = unformatNumber(e.target.value))"
                    required
                  />
                </div>
                <div class="form-row">
                  <label>상세설명 :</label>
                  <textarea v-model="product.description" required rows="8" style="white-space: pre-wrap"></textarea>
                </div>
                <div class="form-row">
                  <label>사진추가 :</label>
                  <div class="input-group">
                    <input
                      type="text"
                      class="form-control file-upload-info"
                      disabled
                      :value="getImageNamesString(product.id)"
                      placeholder="    파일을 선택하세요"
                    />
                    <div class="input-group-append">
                      <button
                        class="file-upload-browse n-btn n-btn-color:main"
                        type="button"
                        @click="triggerFileUpload(product.id)"
                      >
                        파일 선택
                      </button>
                    </div>
                    <input
                      type="file"
                      :ref="`fileUpload${product.id}`"
                      @change="handleFileUpload($event, product.id)"
                      multiple
                      style="display: none"
                    />
                  </div>
                </div>
                <div :id="`imagePreview${product.id}`" class="image-preview">
                  <div v-for="(image, imageIndex) in product.images" :key="imageIndex" class="image-preview-item">
                    <img :src="image" />
                    <button type="button" class="delete-btn" @click="removeImage(product.id, imageIndex)">X</button>
                  </div>
                </div>
                <button
                  v-if="index > 0"
                  type="button"
                  @click="removeProductForm(product.id)"
                  class="n-btn n-btn-color:danger mt-2"
                >
                  이 상품 제거
                </button>
              </div>
            </div>
            <div class="form-actions">
              <button type="button" @click="addProductForm" class="n-btn n-btn-color:main">+ 상품 추가</button>
              <button type="submit" class="n-btn n-btn-color:main">등 록</button>
              <nuxt-link to="list" class="n-btn n-btn-color:danger">취 소</nuxt-link>
            </div>
          </form>
        </section>
      </div>
    </main>
  </div>
</template>
