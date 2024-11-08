<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/admin/product-reg.css" }],
});

const products = ref([]);
const nextId = ref(1);

const addProductForm = () => {
  products.value.push({
    id: nextId.value++,
    active: true,
    isRepresentative: false,
    category1: "",
    category2: "",
    category3: "",
    brand: "",
    shop: "",
    url: "",
    name: "",
    originalPrice: "",
    discountedPrice: "",
    description: "",
    images: [],
    imageNames: [],
  });
};

const removeProductForm = (id) => {
  const index = products.value.findIndex((p) => p.id === id);
  if (index !== -1) {
    products.value.splice(index, 1);
  }
  // Renumber the remaining products
  products.value.forEach((product, index) => {
    product.id = index + 1;
  });
  nextId.value = products.value.length + 1;
};

const updateRepresentativeProduct = (id) => {
  const currentProduct = products.value.find((product) => product.id === id);
  if (currentProduct && currentProduct.isRepresentative) {
    products.value.forEach((product) => {
      product.isRepresentative = false;
    });
  } else {
    products.value.forEach((product) => {
      product.isRepresentative = product.id === id;
    });
  }
};

const handleActiveChange = (product) => {
  // 토글 버튼 상태 변경 처리
  console.log(`Product ${product.id} active status: ${product.active}`);
};

const triggerFileUpload = (id) => {
  const fileInput = document.querySelector(`#product-form-${id} input[type="file"]`);
  if (fileInput) fileInput.click();
};

const handleFileUpload = (event, id) => {
  const files = event.target.files;
  const product = products.value.find((p) => p.id === id);
  if (product) {
    product.images = [...product.images, ...Array.from(files).map((file) => URL.createObjectURL(file))];
    product.imageNames = [...product.imageNames, ...Array.from(files).map((file) => file.name)];
  }
};

const removeImage = (productId, imageIndex) => {
  const product = products.value.find((p) => p.id === productId);
  if (product) {
    product.images.splice(imageIndex, 1);
    product.imageNames.splice(imageIndex, 1);
  }
};

const submitForm = () => {
  console.log(products.value);
  // 여기에 서버로 데이터를 전송하는 로직 추가
};

onMounted(() => {
  addProductForm(); // 초기 상품 폼 추가
});

const getImageNamesString = computed(() => (productId) => {
  const product = products.value.find((p) => p.id === productId);
  return product ? product.imageNames.join(", ") : "";
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
                      <span class="switch-label">공개 여부</span>
                    </div>
                    <label class="representative-product">
                      <input
                        type="checkbox"
                        :checked="product.isRepresentative"
                        @change="updateRepresentativeProduct(product.id)"
                      />
                      <span class="switch-label ml-2">대표 상품</span>
                    </label>
                  </div>
                </h3>
                <!-- 기존 폼 필드들 유지 -->
                <div class="form-row">
                  <label>카테고리 :</label>
                  <select v-model="product.category1" required>
                    <option value="">1차 카테고리</option>
                  </select>
                  <select v-model="product.category2" required class="ml:2">
                    <option value="">2차 카테고리</option>
                  </select>
                  <select v-model="product.category3" required class="ml:2">
                    <option value="">3차 카테고리</option>
                  </select>
                </div>
                <div class="form-row">
                  <label>브랜드 :</label>
                  <select v-model="product.brand" required>
                    <option value="">브랜드 선택</option>
                  </select>
                </div>
                <div class="form-row">
                  <label>쇼핑몰 :</label>
                  <select v-model="product.shop" required>
                    <option value="">쇼핑몰 선택</option>
                  </select>
                </div>
                <div class="form-row">
                  <label>URL:</label>
                  <input type="url" v-model="product.url" required />
                </div>
                <div class="form-row">
                  <label>상품이름 :</label>
                  <input type="text" v-model="product.name" required />
                </div>
                <div class="form-row">
                  <label>소비자가 :</label>
                  <input type="number" v-model="product.originalPrice" required />
                </div>
                <div class="form-row">
                  <label>할인가 :</label>
                  <input type="number" v-model="product.discountedPrice" required />
                </div>
                <div class="form-row">
                  <label>상세설명 :</label>
                  <textarea v-model="product.description" required rows="8"></textarea>
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
              <a href="#" class="n-btn n-btn-color:danger">취 소</a>
            </div>
          </form>
        </section>
      </div>
    </main>
  </div>
</template>
