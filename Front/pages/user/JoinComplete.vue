<template>
    <div class="complete-container">
      <div class="branding">
        F<span class="highlight">UCK</span>UPTO
      </div>
      <div class="checkmark">
        <img src="/imgs/icon/check.svg" alt="Check Icon" class="checkmark-icon" />
      </div>
      <p class="completion-message">회원가입 완료이 완료되었습니다.</p>
      <div class="buttons">
        <button class="btn primary">로그인 하러 가기</button>
        <button class="btn secondary">메인페이지로 가기</button>
      </div>
    </div>
</template>
  
<script>
export default {
  name: 'Complete',
};
</script>

<style scoped>
@import url("@/public/css/user-joinComplete.css");
</style>