<script setup>
import { ref } from 'vue';

useHead({
    link: [{ rel: "stylesheet", href: "/css/admin/brand-reg.css"}],
});

const brand = ref({
    korName: '',
    engName: '',
    url: '',
    active: '',
    description: '',
    fileUpload: null,
});

const imageUrl = ref('');

const previewImage = (event) => {
    const file = event.target.files[0];
    brand.value.fileUpload = file; // 파일을 brand.fileUpload에 저장
    if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            imageUrl.value = e.target.result;
        };
        reader.readAsDataURL(file);
    } else {
        imageUrl.value = '';
    }
};

const handleSubmit = async () => {
    try {
        const formData = new FormData();
         // brandData라는 이름으로 객체를 추가
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

        const response = await fetch(`http://localhost:8080/api/v1/admin/brands/reg`, {
            method: 'POST',
            body: formData, // FormData 사용
        });

        if (response.ok) {
            const result = await response.json();
            console.log('브랜드 등록 성공:', result);
            alert('브랜드가 성공적으로 등록되었습니다!');
            resetForm();
        } else {
            const errorData = await response.json();
            throw new Error(errorData.message || '브랜드 등록에 실패했습니다.');
        }
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
};

const handleCancel = resetForm;
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
                    <form @submit.prevent="handleSubmit">
                            <div>
                                <label for="korName">
                                    <span>한글명</span>
                                    <input type="text" id="korName" v-model="brand.korName" placeholder="한글명 입력" required>
                                </label>
                            </div>
                            <div>
                                <label for="engName">
                                    <span>영문명</span>
                                    <input type="text" id="engName" v-model="brand.engName" placeholder="영문명 입력" required>
                                </label>
                            </div>
                            <div>
                                <label for="url">
                                    <span>URL</span>
                                    <input type="url" id="url" v-model="brand.url" placeholder="url 입력" required>
                                </label>
                            </div>
                            <div>
                                <label for="active">
                                    <span>사용여부</span>
                                    <select id="active" v-model="brand.active" required>
                                        <option value="" disabled selected>선택하세요</option>
                                        <option value="visible">노출함</option>
                                        <option value="hidden">노출안함</option>
                                    </select>
                                </label>
                            </div>
                            <div>
                                <label for="description">
                                    <span>설명</span>
                                    <textarea id="description" v-model="brand.description" placeholder="설명 입력" required></textarea>
                                </label>
                            </div>
                            <div class="file-upload">
                                <label for="fileUpload">이미지 추가</label>
                                <input type="file" id="fileUpload" @change="previewImage" accept="image/*">
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