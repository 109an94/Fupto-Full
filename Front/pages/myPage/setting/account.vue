<script setup>
import { ref } from 'vue'
import { useAuthFetch } from '@/composables/useAuthFetch'
import { use$Fetch } from '@/composables/use$Fetch'
import  useUserDetails  from '~/composables/useUserDetails'

useHead({
  link: [{ rel: "stylesheet", href: "/css/myAccount.css" }]
});

const userDetails = useUserDetails()
const username = computed(() => userDetails.username.value)
const id = computed(()=>userDetails.id.value)
const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const nickname = ref('')
const email = ref('')
const birthDate = ref('')


// 알림 설정 토글
// const newsLetter = ref(false)
// const sms = ref(false)
// const emailNotif = ref(false)
// const promotionEmail = ref(false)
// const promotionSms = ref(false)
const getMember = async ()=>{
  try {
    if (id.value){
      const response = await use$Fetch(`/user/member/${id.value}`, {
        method: 'GET'
      })
      const data = response;
      const formatBirthDate = data.birthDate.split('T')[0];
      if (data){
        nickname.value = data.nickname
        email.value = data.email
        birthDate.value = formatBirthDate
      }
    }else{
      console.error('id를 찾을 수 없습니다.')
    }
  }catch (error){
    console.error("Failed to load user:", error);
  }
}

const updateMember = async () => {
  try {
    const response = await use$Fetch('/user/member/edit', {
      method: 'PUT',
      body: {
        password: currentPassword.value,
        newPassword: newPassword.value,
        confirmPassword: confirmPassword.value,
        nickname: nickname.value,
        email: email.value,
        birthDate: birthDate.value
      }
    })
    const data = response;
    if (data.value) {
      alert('회원정보가 수정되었습니다.')
    }
  } catch (error) {
    if (error.response?.status === 401) {
      alert('현재 비밀번호가 일치하지 않습니다.')
    } else {
      alert('회원정보 수정에 실패했습니다.')
    }
  }
}
onMounted(() => {
  // userDetails.loadUserFromStorage()
  getMember()
})
</script>

<template>
  <div class="edit-member-container">
    <div class="member-info">
      <h2>회원 정보</h2>
      <div class="info-row">
        <client-only>
          <span class="userid">{{username}}</span>
        </client-only>
      </div>

      <div class="password-section">
        <div class="input-group">
          <label>*비밀번호</label>
          <input
              type="password"
              v-model="currentPassword"
              placeholder="현재 비밀번호를 입력하세요"
          />
        </div>

        <div class="input-group">
          <label>새로운 비밀번호</label>
          <input
              type="password"
              v-model="newPassword"
              placeholder="새 비밀번호"
          />
        </div>

        <div class="input-group">
          <label>새로운 비밀번호 확인</label>
          <input
              type="password"
              v-model="confirmPassword"
              placeholder="새 비밀번호 확인"
          />
        </div>
      </div>

      <div class="personal-info">
        <div class="input-group">
          <label>닉네임</label>
          <input type="text" v-model="nickname" />
        </div>

        <div class="input-group">
          <label>이메일</label>
          <input type="email" v-model="email" />
        </div>

        <div class="input-group">
          <label>생년월일</label>
          <input type="date" v-model="birthDate" />
        </div>
      </div>

      <!--      <div class="notification-settings">-->
      <!--        <h3>알림 설정</h3>-->
      <!--        <div class="toggle-group">-->
      <!--          <label>뉴스레터</label>-->
      <!--          <toggle-switch v-model="newsLetter" />-->
      <!--        </div>-->

      <!--        <div class="toggle-group">-->
      <!--          <label>문자</label>-->
      <!--          <toggle-switch v-model="sms" />-->
      <!--        </div>-->

      <!--        <div class="toggle-group">-->
      <!--          <label>앱푸시</label>-->
      <!--          <toggle-switch v-model="emailNotif" />-->
      <!--        </div>-->

      <!--        <div class="toggle-group">-->
      <!--          <label>제고 리마인드 알림</label>-->
      <!--          <toggle-switch v-model="promotionEmail" />-->
      <!--        </div>-->

      <!--        <div class="toggle-group">-->
      <!--          <label>가격 리마인드 알림</label>-->
      <!--          <toggle-switch v-model="promotionSms" />-->
      <!--        </div>-->
      <!--      </div>-->

      <div class="button-group">
        <button @click="updateMember" class="update-btn">
          정보 수정
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>