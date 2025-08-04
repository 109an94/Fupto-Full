<script setup>
import { ref, computed } from 'vue';
import { use$Fetch } from "~/composables/use$Fetch";

useHead({
    link: [{ rel: "stylesheet", href: "/css/admin/shoppingmall-reg.css"}],
});

const shoppingmall = ref({
    korName: '',
    engName: '',
    url: '',
    active: true,
    description: '',
    deliveryfee: '',
    taxes: '',
    fileUpload: null,
});

const imageUrl = ref('');
const errors = ref({});

const validateKorName = () => {
    if (!shoppingmall.value.korName) {
        errors.value.korName = '한글명을 입력하세요.';
    } else {
        delete errors.value.korName;
    }
};

const validateEngName = () => {
    if (!shoppingmall.value.engName) {
        errors.value.engName = '영문명을 입력하세요.';
    } else {
        delete errors.value.engName;
    }
};

const validateUrl = () => {
    const urlPattern = /^https?:\/\/.+$/;
    if (!shoppingmall.value.url || !urlPattern.test(shoppingmall.value.url)) {
        errors.value.url = '유효한 URL을 입력하세요.';
    } else {
        delete errors.value.url;
    }
};

const validateDeliveryFee = () => {
    if (!shoppingmall.value.deliveryfee || isNaN(Number(unformatNumber(shoppingmall.value.deliveryfee)))) {
        errors.value.deliveryfee = '배송비를 숫자로 입력하세요.';
    } else {
        delete errors.value.deliveryfee;
    }
};

const validateTaxes = () => {
    if (!shoppingmall.value.taxes || isNaN(Number(unformatNumber(shoppingmall.value.taxes)))) {
        errors.value.taxes = '관부가세를 숫자로 입력하세요.';
    } else {
        delete errors.value.taxes;
    }
};

const validateFileUpload = () => {
    if (!shoppingmall.value.fileUpload) {
        errors.value.fileUpload = '이미지를 선택하세요.';
    } else {
        delete errors.value.fileUpload;
    }
};

const validateForm = () => {
    validateKorName();
    validateEngName();
    validateUrl();
    validateDeliveryFee();
    validateTaxes();
    validateFileUpload();

    return Object.keys(errors.value).length === 0;
};

const previewImage = (event) => {
    const file = event.target.files[0];
    errors.value.fileUpload = ''; 
    if (file) {
        if (file.size > 5 * 1024 * 1024) {
            errors.value.fileUpload = '파일 크기는 5MB를 초과할 수 없습니다.';
            event.target.value = '';
            return;
        }
        if (!file.type.startsWith('image/')) {
            errors.value.fileUpload = '이미지 파일만 업로드 가능합니다.';
            event.target.value = '';
            return;
        }
        shoppingmall.value.fileUpload = file;
        const reader = new FileReader();
        reader.onload = (e) => {
            imageUrl.value = e.target.result;
        };
        reader.readAsDataURL(file);
    } else {
        imageUrl.value = '';
        shoppingmall.value.fileUpload = null;
    }
};

// Form submission handler
// const handleSubmit = async () => {
//     if (!validateForm()) {
//         alert('필수항목을 입력하세요.');
//         return;
//     }

//     try {
//         const formData = new FormData();
//         formData.append('shoppingmallData', JSON.stringify({
//             korName: shoppingmall.value.korName,
//             engName: shoppingmall.value.engName,
//             url: shoppingmall.value.url,
//             active: shoppingmall.value.active,
//             description: shoppingmall.value.description,
//             deliveryfee: shoppingmall.value.deliveryfee,
//             taxes: shoppingmall.value.taxes
//         }));

//         if (shoppingmall.value.fileUpload) {
//             formData.append('file', shoppingmall.value.fileUpload);
//         }

//         const response = await fetch(`http://localhost:8080/api/v1/admin/shoppingmalls/reg`, {
//             method: 'POST',
//             body: formData,
//         });

//         if (response.ok) {
//             const result = await response.json();
//             console.log('쇼핑몰 등록 성공:', result);
//             alert('쇼핑몰이 성공적으로 등록되었습니다!');
//             resetForm();

//             // 등록 후 리다이렉트
//             window.location.href = 'http://localhost:3000/admin/Shoppingmalls/list';
//         } else {
//             const errorData = await response.json();
//             throw new Error(errorData.message || '쇼핑몰 등록에 실패했습니다.');
//         }
//     } catch (error) {
//         console.error('Error:', error);
//         alert(`쇼핑몰 등록 중 오류가 발생했습니다: ${error.message}`);
//     }
// };

const handleSubmit = async () => {
    if (!validateForm()) {
        alert('필수항목을 입력하세요.');
        return;
    }

    try {
        const formData = new FormData();

        formData.append('shoppingmallData', JSON.stringify({
            korName: shoppingmall.value.korName,
            engName: shoppingmall.value.engName,
            url: shoppingmall.value.url,
            active: shoppingmall.value.active,
            description: shoppingmall.value.description,
            deliveryfee: shoppingmall.value.deliveryfee,
            taxes: shoppingmall.value.taxes
        }));

        if (shoppingmall.value.fileUpload) {
            formData.append('file', shoppingmall.value.fileUpload);
        }

        const { data, error } = await use$Fetch('/admin/shoppingmalls/reg', {
            method: 'POST',
            body: formData,
        });

        if (error) {
            throw new Error(error.message || '쇼핑몰 등록에 실패했습니다.');
        }

        console.log('쇼핑몰 등록 성공:', data);
        alert('쇼핑몰이 성공적으로 등록되었습니다!');
        resetForm();

        window.location.href = 'http://localhost:3000/admin/shoppingmalls/list';
    } catch (error) {
        console.error('Error:', error);
        alert(`쇼핑몰 등록 중 오류가 발생했습니다: ${error.message}`);
    }
};


const resetForm = () => {
    shoppingmall.value = {
        korName: '',
        engName: '',
        url: '',
        active: '',
        description: '',
        deliveryfee: '',
        taxes: '',
        fileUpload: null,
    };
    imageUrl.value = '';
    errors.value = {};
};

const handleCancel = () => {
    resetForm();
    // 취소 버튼을 클릭했을 때 리다이렉트
    window.location.href = 'http://localhost:3000/admin/shoppingmalls/list';
};
const formatNumber = (value) => {
  if (!value) return "";
  return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

const unformatNumber = (value) => {
  if (!value) return "";
  return value.toString().replace(/,/g, "");
};
</script>

<template>
    <main>
        <h1 class="title">쇼핑몰 등록</h1>
		<ul class="breadcrumbs">
			<li><a href="#">FUPTO</a></li>
			<li class="divider">/</li>
			<li><a href="#">쇼핑몰</a></li>
			<li class="divider">/</li>
			<li><a href="#" class="active">쇼핑몰 등록</a></li>
		</ul>

        <div class="container">
            <div class="card">
                <div class="card-body">
                    <form @submit.prevent="handleSubmit" enctype="multipart/form-data">
                        <div>
                            <label for="korName">
                                <span>*한글명</span>
                                <input type="text" id="korName" v-model="shoppingmall.korName" @blur="validateKorName" placeholder="한글명 입력">
                                <span v-if="errors.korName" class="error">{{ errors.korName }}</span>
                            </label>
                        </div>
                        <div>
                            <label for="engName">
                                <span>*영문명</span>
                                <input type="text" id="engName" v-model="shoppingmall.engName" @blur="validateEngName" placeholder="영문명 입력">
                                <span v-if="errors.engName" class="error">{{ errors.engName }}</span>
                            </label>
                        </div>
                        <div>
                            <label for="url">
                                <span>*URL</span>
                                <input type="url" id="url" v-model="shoppingmall.url" @blur="validateUrl" placeholder="URL 입력">
                                <span v-if="errors.url" class="error">{{ errors.url }}</span>
                            </label>
                        </div>
                        <div>
                            <label for="deliveryfee">
                                <span>*배송비</span>
                                <input
                                    type="text"
                                    id="deliveryfee"
                                    :value="formatNumber(shoppingmall.deliveryfee)"
                                    @blur="validateDeliveryFee"
                                    @input="shoppingmall.deliveryfee = unformatNumber($event.target.value)"
                                    placeholder="배송비 입력"
                                    >
                                <span v-if="errors.deliveryfee" class="error">{{ errors.deliveryfee }}</span>
                            </label>
                        </div>
                        <div>
                            <label for="taxes">
                                <span>*관부가세</span>
                                <input
                                    type="text"
                                    id="taxes"
                                    :value="formatNumber(shoppingmall.taxes)"
                                    @blur="validateTaxes"
                                    @input="shoppingmall.taxes = unformatNumber($event.target.value)"
                                    placeholder="관부가세 입력"
                                    >
                                <span v-if="errors.taxes" class="error">{{ errors.taxes }}</span>
                            </label>
                        </div>
                        <div>
                            <label for="active">
                                <span>사용여부</span>
                                <select id="active" v-model="shoppingmall.active">
                                    <option :value="true" selected>노출함</option>
                                    <option :value="false">노출안함</option>
                                </select>
                            </label>
                        </div>
                        <div>
                            <label for="description">
                                <span>설명</span>
                                <textarea rows="5" id="description" v-model="shoppingmall.description" placeholder="설명 입력"></textarea>
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
