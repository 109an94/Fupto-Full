<script setup>
import { ref } from 'vue';

useHead({
    link: [{ rel: "stylesheet", href: "/css/admin/brand-reg.css"}],
});

const brand = ref({
    hanName: '',
    engName: '',
    url: '',
    price: '',
    isUse: '',
    description: '',
    fileUpload: null,
});

const imageUrl = ref('');

const previewImage = (event) => {
    const file = event.target.files[0];
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

const handleSubmit = () => {
    console.log('브랜드 등록:', brand.value);
    alert('브랜드가 성공적으로 등록되었습니다!');
    // 폼 리셋
    brand.value = {
        hanName: '',
        engName: '',
        url: '',
        price: '',
        isUse: '',
        description: '',
        fileUpload: null,
    };
    imageUrl.value = '';
};

const handleCancel = () => {
    brand.value = {
        hanName: '',
        engName: '',
        url: '',
        price: '',
        isUse: '',
        description: '',
        fileUpload: null,
    };
    imageUrl.value = '';
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
                    <form @submit.prevent="handleSubmit">
                            <div>
                                <label for="hanName">
                                    <span>한글명</span>
                                    <input type="text" id="hanName" v-model="brand.hanName" placeholder="한글명 입력" required>
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
                                <label for="is-use">
                                    <span>사용여부</span>
                                    <select id="is-use" v-model="brand.isUse" required>
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
                                <input type="file" id="fileUpload" @change="previewImage" accept="image/*" required>
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