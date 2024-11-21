<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import {useAuthFetch} from "~/composables/useAuthFetch.js";

const router = useRouter();

const email = ref('');
const name = ref('');
const userId = ref('');
const phone = ref('');
const password = ref('');
const confirmPassword = ref('');
const gender = ref('');
const agreement = ref(false);
const birthDate = ref('');

const errors = reactive({
  email: '',
  name: '',
  userId: '',
  phone: '',
  password: '',
  confirmPassword: '',
  gender: '',
  agreement: ''
});


const validateEmail = () => {
  if (email.value.length < 5 || email.value.length > 20) {
    errors.email = '이메일은 5-20자 사이여야 합니다.';
  } else {
    errors.email = '';
  }
};
const validateName = () => {
  if (name.value.length < 2 || name.value.length > 16) {
    errors.name = '이름은 2자 이상 16자 이하로 입력해주세요.';
  } else {
    errors.name = '';
  }
};
const validateUserId = () => {
  if (userId.value.length < 2 || userId.value.length > 16) {
    errors.userId = '닉네임은 2자 이상 16자 이하로 입력해주세요.';
  } else {
    errors.userId = '';
  }
};
const validatePhone = () => {
  if (!phone.value.match(/^\d{10,11}$/)) {
    errors.phone = '유효한 전화번호를 입력해주세요.';
  } else {
    errors.phone = '';
  }
};
const validatePassword = () => {
  if (password.value.length < 8 || password.value.length > 16) {
    errors.password = '비밀번호는 8-16자 사이여야 합니다.';
  } else {
    errors.password = '';
  }
};
const validateConfirmPassword = () => {
  if (password.value !== confirmPassword.value) {
    errors.confirmPassword = '비밀번호가 일치하지 않습니다.';
  } else {
    errors.confirmPassword = '';
  }
};

const handleSubmit = async () => {
  validateEmail();
  validateName();
  validateUserId();
  validatePhone();
  validatePassword();
  validateConfirmPassword();

  if (!gender.value) {
    errors.gender = '성별을 선택해주세요.';
  } else {
    errors.gender = '';
  }

  if (!agreement.value) {
    errors.agreement = '이용약관에 동의해주세요.';
  } else {
    errors.agreement = '';
  }

  if (
      !errors.email &&
      !errors.name &&
      !errors.userId &&
      !errors.phone &&
      !errors.password &&
      !errors.confirmPassword &&
      !errors.gender &&
      !errors.agreement
  ) {
    try {
      const requestBody = {
        userId : userId.value,
        nickname : name.value,
        password : password.value,
        birthDate : birthDate.value,
        gender : gender.value === 'male' ?'남성':'여성',
        tel : phone.value,
        email : email.value
      }

      const {data, error} = await useAuthFetch('/auth/signup', {
        method: 'POST',
        body: requestBody
      })
      if (error.value) {
        console.error('Registration error:', error.value);
        alert(`회원가입 실패: ${error.value.message || '알 수 없는 오류가 발생했습니다.'}`);
      } else {
        console.log('Response data:', data.value);
        alert('회원가입이 완료되었습니다!');
        router.push('/user/signin');
      }
    } catch (error) {
      console.error('Unexpected error:', error);
      alert('회원가입 중 예기치 않은 오류가 발생했습니다. 다시 시도해주세요.');
    }
  }
};
</script>

<template>
  <div class="join-container">
    <h1 class="join-title">회원 가입을 위해 정보를 입력해주세요.</h1>
    <form class="join-form" @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="userId">*아이디</label>
        <input
            type="text"
            id="userId"
            v-model="userId"
            @input="validateUserId"
            placeholder="2자 이상 16자 이하, 영어 또는 한글로 구성해주세요."
        />
        <p class="validation-error" v-if="errors.userId">{{ errors.userId }}</p>
      </div>

      <div class="form-group">
        <label for="name">*이름</label>
        <input
            type="text"
            id="name"
            v-model="name"
            @input="validateName"
            placeholder="2자 이상 16자 이하, 영어나 한글로 구성해주세요."
        />
        <p class="validation-error" v-if="errors.name">{{ errors.name }}</p>
      </div>

      <div class="form-group">
        <label for="email">*이메일</label>
        <input
            type="email"
            id="email"
            v-model="email"
            @input="validateEmail"
            placeholder="5-20자의 영문 대소문자, 숫자와 특수기호로 구성해주세요."
        />
        <p class="validation-error" v-if="errors.email">{{ errors.email }}</p>
      </div>

      <div class="form-group phone-group">
        <label for="phone">*전화번호</label>
        <div class="phone-input-group">
          <input
              type="tel"
              id="phone"
              v-model="phone"
              @input="validatePhone"
              placeholder="전화번호를 입력해주세요."
          />
          <button type="button" class="phone-certify-button">인증</button>
        </div>
        <p class="validation-error" v-if="errors.phone">{{ errors.phone }}</p>
      </div>

      <div class="form-group">
        <label for="password">*비밀번호</label>
        <input
            type="password"
            id="password"
            v-model="password"
            @input="validatePassword"
            placeholder="8-16자의 영문 대/소문자, 숫자, 특수문자로 구성해주세요."
        />
        <p class="validation-error" v-if="errors.password">{{ errors.password }}</p>
      </div>

      <div class="form-group">
        <label for="confirm-password">*비밀번호 확인</label>
        <input
            type="password"
            id="confirm-password"
            v-model="confirmPassword"
            @input="validateConfirmPassword"
            placeholder="비밀번호를 다시 확인해주세요."
        />
        <p class="validation-error" v-if="errors.confirmPassword">{{ errors.confirmPassword }}</p>
      </div>

      <div class="form-group">
        <label>*성별</label>
        <div class="gender-group">
          <input type="radio" id="male" name="gender" value="male" v-model="gender" />
          <label for="male" class="gender-label male">남자</label>

          <input type="radio" id="female" name="gender" value="female" v-model="gender" />
          <label for="female" class="gender-label female">여자</label>
        </div>
        <p class="validation-error" v-if="errors.gender">{{ errors.gender }}</p>
      </div>

      <div class="form-group">
        <input type="checkbox" id="agreement" v-model="agreement" />
        <label for="agreement">이용약관 개인정보 수집 및 이용, 마케팅 활용 선택에 모두 동의합니다.</label>
        <p class="validation-error" v-if="errors.agreement">{{ errors.agreement }}</p>
      </div>

      <button type="submit" class="submit-button">가입하기</button>
    </form>
  </div>
</template>

<style scoped>
@import url("@/public/css/user-join.css");
</style>