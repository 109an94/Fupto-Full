<script setup>
import { ref, computed, onMounted } from "vue";
import SearchableSelect from "~/components/admin/SearchableSelect.vue";

useHead({
  link: [{ rel: "stylesheet", href: "/css/admin/product-reg.css" }],
});

const route = useRoute();
const config = useRuntimeConfig();
const productId = route.params.id;

const products = ref([]);

const categories = ref({
  level1: [],
  level2: [],
  level3: [],
});

const brands = ref([]);
const shops = ref([]);

const loadProductData = async () => {
  try {
    const data = await use$Fetch(`/admin/products/${productId}/edit`);

    await fetchCategories(1);
    if (data.category1Id) {
      await fetchCategories(2, data.category1Id);
    }
    if (data.category2Id) {
      await fetchCategories(3, data.category2Id);
    }

    products.value = [
      {
        id: data.id,
        presentId: data.presentId,
        active: data.active,
        category1: data.category1Id,
        category2: data.category2Id,
        category3: data.category3Id,
        brandId: data.brandId,
        shoppingMallId: data.shoppingMallId,
        url: data.url,
        productName: data.productName,
        retailPrice: data.retailPrice,
        salePrice: data.salePrice,
        description: data.description,
        images: data.productImages.map((img) => `${config.public.apiBase}/admin/products/${data.id}/image/${img.displayOrder}`),
        imageNames: data.productImages.map((img) => img.originalName),
        fileList: [], // 새로 추가될 파일들
        existingImages: data.productImages, // 기존 이미지 정보 보존
      },
    ];
  } catch (error) {
    console.error("Error loading product:", error);
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

// 브랜드, 쇼핑몰 데이터 가져오기
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

const handleActiveChange = (product) => {
  console.log(`Product ${product.id} active status: ${product.active}`);
};

// 가격 포맷팅
const formatNumber = (value) => {
  if (!value) return "";
  return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

const unformatNumber = (value) => {
  if (!value) return "";
  return value.toString().replace(/,/g, "");
};

// 파일 업로드 관련 함수들
const triggerFileUpload = (id) => {
  const fileInput = document.querySelector(`#product-form-${id} input[type="file"]`);
  if (fileInput) fileInput.click();
};

const handleFileUpload = (event, id) => {
  const files = event.target.files;
  const product = products.value[0];

  if (!files || files.length === 0) {
    return;
  }

  if (product) {
    if (!product.fileList) {
      product.fileList = [];
    }

    product.fileList = [...product.fileList, ...Array.from(files)];

    const newImageUrls = Array.from(files).map((file) => URL.createObjectURL(file));
    const newImageNames = Array.from(files).map((file) => file.name);

    product.images = [...product.images, ...newImageUrls];

    product.imageNames = [
      ...product.existingImages.map((img) => img.originalName),
      ...product.imageNames.slice(product.existingImages.length),
      ...newImageNames,
    ];
  }
};

const removeImage = (productId, imageIndex) => {
  const product = products.value[0];
  if (!product) return;

  // 기존 이미지인 경우
  if (imageIndex < product.existingImages.length) {
    product.existingImages.splice(imageIndex, 1);
  } else {
    // 새로 추가된 이미지인 경우
    const newImageIndex = imageIndex - product.existingImages.length;
    URL.revokeObjectURL(product.images[imageIndex]);
    product.fileList.splice(newImageIndex, 1);
  }

  product.images.splice(imageIndex, 1);
  product.imageNames.splice(imageIndex, 1);
};

const getImageNamesString = computed(() => (productId) => {
  const product = products.value.find((p) => p.id === productId);
  return product ? product.imageNames.join(", ") : "";
});

// 수정된 submit 함수
const submitForm = async () => {
  try {
    const product = products.value[0];

    const updateData = {
      id: product.id,
      active: product.active,
      presentId: product.presentId,
      category3Id: product.category3,
      brandId: product.brandId,
      shoppingMallId: product.shoppingMallId,
      url: product.url,
      productName: product.productName,
      retailPrice: parseInt(product.retailPrice),
      salePrice: parseInt(product.salePrice),
      description: product.description,
      existingImageIds: product.existingImages.map((img) => img.id),
      imageOrders: [
        ...product.existingImages.map((img, index) => ({
          id: img.id,
          isNew: false,
          displayOrder: index + 1,
        })),
        ...Array(product.fileList.length)
          .fill()
          .map((_, index) => ({
            isNew: true,
            displayOrder: product.existingImages.length + index + 1,
          })),
      ],
    };

    const formData = new FormData();
    formData.append(
      "data",
      new Blob([JSON.stringify(updateData)], {
        type: "application/json",
      })
    );

    // 새로운 파일들 추가
    if (product.fileList.length > 0) {
      product.fileList.forEach((file) => {
        formData.append("files", file);
      });
    }

    await use$Fetch(`/admin/products/${productId}`, {
      method: "PATCH",
      body: formData,
    });

    alert("상품 수정이 완료되었습니다.");
    await navigateTo("/admin/products/list");
  } catch (error) {
    console.error("Error updating product:", error);
    alert("상품 수정 중 오류가 발생했습니다.");
  }
};

onMounted(async () => {
  await Promise.all([fetchBrands(), fetchShops()]);
  await loadProductData();
});
</script>

<template>
  <div>
    <main>
      <h1 class="title">상품 수정</h1>
      <ul class="breadcrumbs">
        <li><a href="#">FUPTO</a></li>
        <li class="divider">/</li>
        <li><a href="#" class="active">상품 수정</a></li>
      </ul>
      <div class="product-form-container">
        <section>
          <form @submit.prevent="submitForm" enctype="multipart/form-data" class="n-form">
            <div id="product-forms-container">
              <div v-if="products[0]" :id="`product-form-${products[0].id}`" class="product-form">
                <h3 class="n-heading:6">
                  상품 정보
                  <div class="form-header-controls">
                    <div class="switch-container">
                      <label class="pl-switch">
                        <input
                          type="checkbox"
                          :id="'active' + products[0].id"
                          v-model="products[0].active"
                          @change="() => handleActiveChange(products[0])"
                        />
                        <span class="pl-slider round"></span>
                      </label>
                      <span class="switch-label mr-2">공개 여부</span>
                    </div>
                    <label class="representative-product">
                      <input type="checkbox" v-model="products[0].presentId" />
                      <span class="switch-label ml-2">대표 상품</span>
                    </label>
                  </div>
                </h3>

                <div class="form-row">
                  <label>카테고리 :</label>
                  <select v-model="products[0].category1" required @change="() => handleCategory1Change(products[0])">
                    <option value="">1차 카테고리</option>
                    <option v-for="cat in categories.level1" :key="cat.id" :value="cat.id">
                      {{ cat.name }}
                    </option>
                  </select>
                  <select
                    v-model="products[0].category2"
                    required
                    class="ml:2"
                    @change="() => handleCategory2Change(products[0])"
                  >
                    <option value="">2차 카테고리</option>
                    <option v-for="cat in categories.level2" :key="cat.id" :value="cat.id">
                      {{ cat.name }}
                    </option>
                  </select>
                  <select v-model="products[0].category3" required class="ml:2">
                    <option value="">3차 카테고리</option>
                    <option v-for="cat in categories.level3" :key="cat.id" :value="cat.id">
                      {{ cat.name }}
                    </option>
                  </select>
                </div>

                <div class="form-row">
                  <label>브랜드 :</label>
                  <SearchableSelect v-model="products[0].brandId" :options="brands" placeholder="브랜드 선택" required />
                </div>
                <div class="form-row">
                  <label>쇼핑몰 :</label>
                  <SearchableSelect v-model="products[0].shoppingMallId" :options="shops" placeholder="쇼핑몰 선택" required />
                </div>

                <div class="form-row">
                  <label>URL:</label>
                  <input type="url" v-model="products[0].url" required />
                </div>
                <div class="form-row">
                  <label>상품이름 :</label>
                  <input type="text" v-model="products[0].productName" required />
                </div>
                <div class="form-row">
                  <label>소비자가 :</label>
                  <input
                    type="text"
                    :value="formatNumber(products[0].retailPrice)"
                    @input="(e) => (products[0].retailPrice = unformatNumber(e.target.value))"
                    required
                  />
                </div>
                <div class="form-row">
                  <label>할인가 :</label>
                  <input
                    type="text"
                    :value="formatNumber(products[0].salePrice)"
                    @input="(e) => (products[0].salePrice = unformatNumber(e.target.value))"
                    required
                  />
                </div>
                <div class="form-row">
                  <label>상세설명 :</label>
                  <textarea v-model="products[0].description" required rows="8" style="white-space: pre-wrap"></textarea>
                </div>
                <div class="form-row">
                  <label>사진추가 :</label>
                  <div class="input-group">
                    <input
                      type="text"
                      class="form-control file-upload-info"
                      disabled
                      :value="getImageNamesString(products[0].id)"
                      placeholder="    파일을 선택하세요"
                    />
                    <div class="input-group-append">
                      <button
                        class="file-upload-browse n-btn n-btn-color:main"
                        type="button"
                        @click="triggerFileUpload(products[0].id)"
                      >
                        파일 선택
                      </button>
                    </div>
                    <input
                      type="file"
                      :ref="`fileUpload${products[0].id}`"
                      @change="handleFileUpload($event, products[0].id)"
                      multiple
                      style="display: none"
                    />
                  </div>
                </div>
                <div :id="`imagePreview${products[0].id}`" class="image-preview">
                  <div v-for="(image, imageIndex) in products[0].images" :key="imageIndex" class="image-preview-item">
                    <img :src="image" />
                    <button type="button" class="delete-btn" @click="removeImage(products[0].id, imageIndex)">X</button>
                  </div>
                </div>
              </div>
            </div>
            <div class="form-actions">
              <button type="submit" class="n-btn n-btn-color:main">수 정</button>
              <nuxt-link to="/admin/products/list" class="n-btn n-btn-color:danger">취 소</nuxt-link>
            </div>
          </form>
        </section>
      </div>
    </main>
  </div>
</template>
