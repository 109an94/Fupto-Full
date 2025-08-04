<script setup>
import { ref } from 'vue';

useHead({
    link: [{ rel: "stylesheet", href: "/css/admin/brand-reg.css"}],
});

const brand = ref({
    korName: '',
    engName: '',
    url: '',
    active: true,
    description: '',
    fileUpload: null,
});

const config = useRuntimeConfig();
const route = useRoute();
const router = useRouter();
const brandId = route.params.id;

const imageUrl = ref('');
const errors = ref({});

const validateKorName = () => {
    if (!brand.value.korName) {
        errors.value.korName = '한글명을 입력하세요.';
    } else {
        delete errors.value.korName;
    }
};

const validateEngName = () => {
    if (!brand.value.engName) {
        errors.value.engName = '영문명을 입력하세요.';
    } else {
        delete errors.value.engName;
    }
};

const validateUrl = () => {
    const urlPattern = /^https?:\/\/.+$/;
    if (!brand.value.url || !urlPattern.test(brand.value.url)) {
        errors.value.url = '유효한 URL을 입력하세요.';
    } else {
        delete errors.value.url;
    }
};

const validateFileUpload = () => {
    if (!brand.value.fileUpload && !imageUrl.value) { // 이미지가 없고 미리보기 이미지도 없는 경우
        errors.value.fileUpload = '이미지를 선택하세요.';
    } else {
        delete errors.value.fileUpload; // 이미지가 선택된 경우 오류 메시지 삭제
    }
};

const validateForm = () => {
    validateKorName();
    validateEngName();
    validateUrl();
    validateFileUpload();

    return Object.keys(errors.value).length === 0;
};

const previewImage = (event) => {
    const file = event.target.files[0];
    errors.value.fileUpload = ''; 
    if (file) {
        if (file.size > 5 * 1024 * 1024) { // 5MB 제한
            alert('파일 크기는 5MB를 초과할 수 없습니다.');
            event.target.value = ''; // 파일 선택 초기화
            return;
        }
        if (!file.type.startsWith('image/')) {
            alert('이미지 파일만 업로드 가능합니다.');
            event.target.value = ''; // 파일 선택 초기화
            return;
        }
        brand.value.fileUpload = file;
        const reader = new FileReader();
        reader.onload = (e) => {
            imageUrl.value = e.target.result;
        };
        reader.readAsDataURL(file);
    } else {
        // 파일이 선택되지 않았을 때 기존 이미지 URL 유지
        imageUrl.value = brand.value.img ? `${config.public.apiBase}${brand.value.img}` : '';
        brand.value.fileUpload = null;
    }
};

const getImageUrl = (url) => {
  if (!url) return '';
  if (url.startsWith('data:')) {
    // 새로 업로드된 이미지의 경우 (data URL)
    return url;
  }
  // 서버에서 가져온 이미지 URL의 경우
  return `${config.public.apiBase}${url}`;
};

// const loadBrandData = async () => {
//   try {
//     const response = await use$Fetch(`/admin/brands/${brandId}/edit`);
//     if (!response.ok) {
//       throw new Error(`HTTP error! status: ${response.status}`);
//     }
//     const data = await response.json();
//     brand.value = {
//       korName: data.korName,
//       engName: data.engName,
//       url: data.url,
//       active: data.active,
//       description: data.description,
//     };
//      // 이미지 URL 수정
//     imageUrl.value = data.img ? (data.img.startsWith('/') ? data.img : '/' + data.img) : '';

//   } catch (error) {
//     console.error("Error loading brand:", error);
//     alert(`브랜드 정보를 불러오는데 실패했습니다: ${error.message}`);
//   }
// };

const loadBrandData = async () => {
  try {
    const data = await use$Fetch(`/admin/brands/${brandId}/edit`);
    
    console.log("Received data:", data); // 디버깅을 위해 받은 데이터 로깅

    if (!data) {
      throw new Error("브랜드 데이터를 받지 못했습니다.");
    }

    brand.value = {
      korName: data.korName,
      engName: data.engName,
      url: data.url,
      active: data.active,
      description: data.description,
    };

    // 이미지 URL 수정
    imageUrl.value = data.img ? (data.img.startsWith('/') ? data.img : '/' + data.img) : '';

  } catch (error) {
    console.error("Error loading brand:", error);
    alert(`브랜드 정보를 불러오는데 실패했습니다: ${error.message}`);
  }
};

// const handleSubmit = async () => {
//   if (!validateForm()) {
//     alert('필수항목을 입력하세요.');
//     return;
//   }
//   try {
//     const formData = new FormData();
//     formData.append('brandData', JSON.stringify({
//       korName: brand.value.korName,
//       engName: brand.value.engName,
//       url: brand.value.url,
//       active: brand.value.active,
//       description: brand.value.description
//     }));

//     if (brand.value.fileUpload) {
//       formData.append('file', brand.value.fileUpload);
//     }

//     const response = await use$Fetch(`/admin/brands/${brandId}`, {
//       method: 'PATCH',
//       body: formData,
//     });

//     if (response.ok) {
//       const result = await response.json();
//       console.log('브랜드 수정 성공:', result);
//       alert('브랜드가 성공적으로 수정되었습니다!');
//       router.push('/admin/brands/list');
//     } else {
//       const errorData = await response.json();
//       throw new Error(errorData.message || '브랜드 수정에 실패했습니다.');
//     }
//   } catch (error) {
//     console.error('Error:', error);
//     alert(`브랜드 수정 중 오류가 발생했습니다: ${error.message}`);
//   }
// };

const handleSubmit = async () => {
  if (!validateForm()) {
    alert('필수항목을 입력하세요.');
    return;
  }

  try {
    const formData = new FormData();
    formData.append('brandData', JSON.stringify({
      korName: brand.value.korName,
      engName: brand.value.engName,
      url: brand.value.url,
      active: brand.value.active,
      description: brand.value.description
    }));

    if (brand.value.fileUpload) {
      formData.append('file', brand.value.fileUpload);
    }

    await use$Fetch(`/admin/brands/${brandId}`, {
      method: 'PATCH',
      body: formData,
    });

    console.log('브랜드 수정 성공');
    alert('브랜드가 성공적으로 수정되었습니다!');
    router.push('/admin/brands/list');
  } catch (error) {
    console.error('Error:', error);
    alert(`브랜드 수정 중 오류가 발생했습니다: ${error.message}`);
  }
};

const handleCancel = () => {
  router.push('/admin/brands/list');
};

onMounted(async () => {
  await loadBrandData();
});
</script>

<template>
    <main>
        <h1 class="title">브랜드 수정</h1>
		<ul class="breadcrumbs">
			<li><a href="#">FUPTO</a></li>
			<li class="divider">/</li>
			<li><a href="#">브랜드</a></li>
			<li class="divider">/</li>
			<li><a href="#" class="active">브랜드 수정</a></li>
		</ul>

        <div class="container">
            <div class="card">
                <div class="card-body">
                    <form @submit.prevent="handleSubmit" enctype="multipart/form-data">
                            <div>
                                <label for="korName">
                                    <span>*한글명</span>
                                    <input type="text" id="korName" v-model="brand.korName" @blur="validateKorName" placeholder="한글명 입력">
                                    <span v-if="errors.korName" class="error">{{ errors.korName }}</span>
                                </label>
                            </div>
                            <div>
                                <label for="engName">
                                    <span>*영문명</span>
                                    <input type="text" id="engName" v-model="brand.engName" @blur="validateEngName" placeholder="영문명 입력">
                                    <span v-if="errors.engName" class="error">{{ errors.engName }}</span>
                                </label>
                            </div>
                            <div>
                                <label for="url">
                                    <span>*URL</span>
                                    <input type="url" id="url" v-model="brand.url" @blur="validateUrl" placeholder="url 입력">
                                    <span v-if="errors.url" class="error">{{ errors.url }}</span>
                                </label>
                            </div>
                            <div>
                                <label for="active">
                                    <span>사용여부</span>
                                    <select id="active" v-model="brand.active">
                                        <option :value="true" selected>노출함</option>
                                        <option :value="false">노출안함</option>
                                    </select>
                                </label>
                            </div>
                            <div>
                                <label for="description">
                                    <span>설명</span>
                                    <textarea rows="5" id="description" v-model="brand.description" placeholder="설명 입력"></textarea>
                                </label>
                            </div>
                            <div class="file-upload">
                                <label for="fileUpload">*이미지 추가</label>
                                <input type="file" id="fileUpload" @change="previewImage" @blur="validateFileUpload" accept="image/*">
                                <span v-if="errors.fileUpload" class="error">{{ errors.fileUpload }}</span>
                            </div>
                            <div class="image-preview" id="imagePreview">
                                <img v-if="imageUrl" :src="getImageUrl(imageUrl)" alt="미리보기 이미지">
                                <p v-else>이미지가 없습니다</p>
                            </div>

                            <div class="button-group">
                                <button type="submit" class="submit-btn">수정</button>
                                <button type="button" class="cancel-btn" @click="handleCancel">취소</button>
                            </div>
                    </form>
                </div>
            </div>
        </div>
    </main>
</template>