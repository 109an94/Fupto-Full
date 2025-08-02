<template>
  <div class="join-container">
    <h1 class="join-title">회원 가입을 위해 정보를 입력해주세요.</h1>
    <form class="join-form" @submit.prevent="handleSubmit">

      <div class="form-group">
        <label for="email">*이메일(아이디)</label>
        <input 
          type="email" 
          id="email" 
          v-model="email"
          @input="validateEmail" 
          placeholder="5-20자의 영문 대소문자, 숫자와 특수기호로 구성해주세요." 
        />
        <p class="validation-error" v-if="emailError">{{ emailError }}</p>
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
        <p class="validation-error" v-if="nameError">{{ nameError }}</p>
      </div>

      <div class="form-group">
        <label for="nickname">*닉네임</label>
        <input 
          type="text" 
          id="nickname" 
          v-model="nickname"
          @input="validateNickname"
          placeholder="2자 이상 16자 이하, 영어 또는 한글로 구성해주세요." 
        />
        <p class="validation-error" v-if="nicknameError">{{ nicknameError }}</p>
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
          <button type="button" class="phone-certify-button" @click="validatePhoneNumber">인증</button>
        </div>
        <p class="validation-error" v-if="phoneError">{{ phoneError }}</p>
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
        <p class="validation-error" v-if="passwordError">{{ passwordError }}</p>
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
        <p class="validation-error" v-if="confirmPasswordError">{{ confirmPasswordError }}</p>
      </div>

      <div class="form-group">
        <label>*성별</label>
        <div class="gender-group">
          <input type="radio" id="male" name="gender" value="male" v-model="gender" />
          <label for="male" class="gender-label male">남자</label>
          
          <input type="radio" id="female" name="gender" value="female" v-model="gender" />
          <label for="female" class="gender-label female">여자</label>
        </div>
        <p class="validation-error" v-if="genderError">{{ genderError }}</p>
      </div>

      <div class="form-group">
        <input type="checkbox" id="agreement" v-model="agreement" />
        <label for="agreement">이용약관 개인정보 수집 및 이용, 마케팅 활용 선택에 모두 동의합니다.</label>
        <p class="validation-error" v-if="agreementError">{{ agreementError }}</p>
      </div>

      <button type="submit" class="submit-button">가입하기</button>
    </form>
  </div>
</template>

<script>
export default {
  name: "Join",
  data() {
    return {
      email: '',
      name: '',
      nickname: '',
      phone: '',
      password: '',
      confirmPassword: '',
      gender: '',
      agreement: false,

      // Error messages
      emailError: '',
      nameError: '',
      nicknameError: '',
      phoneError: '',
      passwordError: '',
      confirmPasswordError: '',
      genderError: '',
      agreementError: ''
    };
  },
  methods: {
    validateEmail() {
      // Add email validation logic here
      if (this.email.length < 5 || this.email.length > 20) {
        this.emailError = '이메일은 5-20자 사이여야 합니다.';
      } else {
        this.emailError = '';
      }
    },
    validateName() {
      if (this.name.length < 2 || this.name.length > 16) {
        this.nameError = '이름은 2자 이상 16자 이하로 입력해주세요.';
      } else {
        this.nameError = '';
      }
    },
    validateNickname() {
      if (this.nickname.length < 2 || this.nickname.length > 16) {
        this.nicknameError = '닉네임은 2자 이상 16자 이하로 입력해주세요.';
      } else {
        this.nicknameError = '';
      }
    },
    validatePhone() {
      // Add phone validation logic here
      if (!this.phone.match(/^\d{10,11}$/)) {
        this.phoneError = '유효한 전화번호를 입력해주세요.';
      } else {
        this.phoneError = '';
      }
    },
    validatePassword() {
      if (this.password.length < 8 || this.password.length > 16) {
        this.passwordError = '비밀번호는 8-16자 사이여야 합니다.';
      } else {
        this.passwordError = '';
      }
    },
    validateConfirmPassword() {
      if (this.password !== this.confirmPassword) {
        this.confirmPasswordError = '비밀번호가 일치하지 않습니다.';
      } else {
        this.confirmPasswordError = '';
      }
    },
    handleSubmit() {
      this.validateEmail();
      this.validateName();
      this.validateNickname();
      this.validatePhone();
      this.validatePassword();
      this.validateConfirmPassword();

      if (!this.gender) {
        this.genderError = '성별을 선택해주세요.';
      } else {
        this.genderError = '';
      }

      if (!this.agreement) {
        this.agreementError = '이용약관에 동의해주세요.';
      } else {
        this.agreementError = '';
      }

      if (
        !this.emailError &&
        !this.nameError &&
        !this.nicknameError &&
        !this.phoneError &&
        !this.passwordError &&
        !this.confirmPasswordError &&
        !this.genderError &&
        !this.agreementError
      ) {
        // Submit form if there are no errors
        alert('회원가입이 완료되었습니다!');
      }
    }
  }
};
</script>

<style scoped>
@import url("@/assets/css/user-join.css");
</style>