<script setup>
import { ref } from 'vue';
import { use$Fetch } from "~/composables/use$Fetch";

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
    if (!brand.value.fileUpload) {
        errors.value.fileUpload = '이미지를 선택하세요.';
    } else {
        delete errors.value.fileUpload;
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
        imageUrl.value = '';
        brand.value.fileUpload = null;
    }
};

// const handleSubmit = async () => {
//     if (!validateForm()) {
//         alert('필수항목을 입력하세요.');
//         return;
//     }

//     try {

//         const formData = new FormData();

//         // brandData 객체를 FormData에 JSON 문자열로 추가
//         formData.append('brandData', JSON.stringify({
//             korName: brand.value.korName,
//             engName: brand.value.engName,
//             url: brand.value.url,
//             active: brand.value.active,
//             description: brand.value.description
//         }));

//         // formData.append("korName", brand.value.korName);
//         // formData.append("engName", brand.value.engName);
//         // formData.append("active", brand.value.active);
//         // formData.append("url", brand.value.url);
//         // formData.append("description", brand.value.description);

//         // 파일이 있는 경우 FormData에 추가
//         if (brand.value.fileUpload) {
//             formData.append('file', brand.value.fileUpload);
//         }

//         // fetch API로 FormData 전송
//         const response = await use$Fetch(`/admin/brands/reg`, {
//             method: 'POST',
//             body: formData, // FormData 사용
//         });

//         // 응답 상태 코드 확인
//         if (response.ok) {
//             const result = await response.json();
//             console.log('브랜드 등록 성공:', result);
//             alert('브랜드가 성공적으로 등록되었습니다!');
//             resetForm(); // 폼 초기화

//             // 등록 후 리다이렉트
//             window.location.href = 'http://localhost:8085/admin/brands/list';
//         } else {
//             const errorData = await response.json();
//             console.error('브랜드 등록 실패:', errorData);
//             throw new Error(errorData.message || '브랜드 등록에 실패했습니다.');
//         }
//     } catch (error) {
//         // 네트워크 오류나 JSON 파싱 오류 처리
//         console.error('Error:', error);
//         alert(`브랜드 등록 중 오류가 발생했습니다: ${error.message}`);
//     }
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

        const { data, error } = await use$Fetch('/admin/brands/reg', {
            method: 'POST',
            body: formData,
        });

        if (error) {
            throw new Error(error.message || '브랜드 등록에 실패했습니다.');
        }

        console.log('브랜드 등록 성공:', data);
        alert('브랜드가 성공적으로 등록되었습니다!');
        resetForm();

        window.location.href = 'http://localhost:3000/admin/brands/list';
    } catch (error) {
        console.error('Error:', error);
        alert(`브랜드 등록 중 오류가 발생했습니다: ${error.message}`);
    }
};

const resetForm = () => {
    brand.value = {
        korName: '',
        engName: '',
        url: '',
        active: '',
        description: '',
        fileUpload: null,
    };
    imageUrl.value = '';
    errors.value = {};
};

const handleCancel = () => {
    resetForm();
    // 취소 버튼을 클릭했을 때 리다이렉트
    window.location.href = 'http://localhost:3000/admin/brands/list';
};
</script>

<template>
    <main>
        <h1 class="title">브랜드 등록</h1>
		<ul class="breadcrumbs">
			<li><a href="#">FUPTO</a></li>
			<li class="divider">/</li>
			<li><a href="#">브랜드</a></li>
			<li class="divider">/</li>
			<li><a href="#" class="active">브랜드 등록</a></li>
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
                                <img v-if="imageUrl" :src="imageUrl" alt="미리보기 이미지">
                            </div>

                            <div class="button-group">
                                <button type="submit" class="submit-btn">등록</button>
                                <button type="button" class="cancel-btn" @click="handleCancel">취소</button>
                            </div>
                    </form>
                </div>
            </div>
        </div>
    </main>
</template>