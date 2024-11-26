<script setup>
import {jwtDecode} from "jwt-decode";
import {useRouter} from "vue-router";

const userDetails = useUserDetails()
const route = useRoute()
const returnURL = route.query.returnURL || "/";
const router = useRouter()

const username = ref("")
const password = ref("")


const localLoginHandler = async () => {
  try {
    let {data} = await useAuthFetch("/auth/signin", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: {
        username: username.value,
        password: password.value,
      }
    });
    const response = data.value;

    let userInfo = jwtDecode(response.token);
    userDetails.setAuthentication({
      id: response.userId,
      username: userInfo.username,
      email: userInfo.email,
      roles: userInfo.roles.map((role) => role.authority),
      token: response.token
    });
    userInfo.roles.map(role => {
      console.log(role, role.authority)
    });
    // 로그인 처리 로직 (api호출시)
    console.log('User info:', userInfo);
    console.log('User details:', userDetails);

    // 로그인 성공 후 리다이렉트
    console.log("returnURL", returnURL)
    return navigateTo(returnURL);
  } catch (error) {
    console.error('Login failed:', error);
    alert("계정이 일치하지 않습니다.")
  }
}

</script>

<template>
  <main>
    <div class="login-container">
      <div class="login-box">
        <h1>로그인</h1>
        <form @submit.prevent="localLoginHandler">
          <div class="input-group">
            <label for="username">아이디</label>
            <input class="textbox" v-model="username" type="text" id="username" name="username" required>
          </div>
          <div class="input-group">
            <label for="password">비밀번호</label>
            <input class="textbox" v-model="password" type="password" id="password" name="password" required>
          </div>
          <button type="submit" class="login-button">로그인</button>
        </form>
        <div class="login-links">
          <a href="#">아이디/비밀번호 찾기</a>
          <nuxt-link to="signup">회원가입</nuxt-link>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
@import url("@/public/css/admin/admin-login.css");
</style>