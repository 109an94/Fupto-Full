<script setup>
const { isAnonymous, logout } = useUserDetails()
const route = useRoute();

const logoutHandler = async () => {
  await logout()
  // 로그아웃 후 필요한 추가 작업 수행 (예: 홈페이지로 리다이렉트)
  await navigateTo('/')
}
const isActiveLink = (path, gender) => {
  if (path === "products") {
    return route.path === `/${path}` && route.query.gender === gender;
  }
  return route.path.startsWith(`/${path}`);
};

const redirect = () => {
  window.location.href = "http://localhost:3000/boards/list";
  // router.go(-1);
}

</script>

<template>
  <header class="main-header">
    <nav class="main-nav">
      <h1 class="main-nav_title"><NuxtLink to="/">FUPTO</NuxtLink></h1>
      <ul class="main-nav-list">
        <li class="main-nav_link">
          <NuxtLink to="/products?gender=2" :class="{ 'active-link': isActiveLink('products', '2') }">여성</NuxtLink>
          <NuxtLink to="/products?gender=1" :class="{ 'active-link': isActiveLink('products', '1') }">남성</NuxtLink>
        </li>
        <li class="main-nav_link">
          <NuxtLink to="/brands" :class="{ 'active-link': isActiveLink('brands') }">브랜드</NuxtLink>
          <NuxtLink to="/shoppingmalls" :class="{ 'active-link': isActiveLink('shoppingmalls') }">쇼핑몰</NuxtLink>
        </li>
        <li class="main-nav_link">
          <NuxtLink to="/boards/list" :class="{ 'active-link': isActiveLink('boards') }" @click="redirect">게시글</NuxtLink>
        </li>
        <li class="utility-nav-list">
          <nuxt-link v-if="isAnonymous()==false" to="/myPage"
                     :class="{ 'active-link': isActiveLink('myPage') }">my</nuxt-link>
          <NuxtLink v-if="isAnonymous()" to="/user/signin">로그인</NuxtLink>
          <nuxt-link v-else @click="logoutHandler" to="/user/signin">로그아웃</nuxt-link>
        </li>
      </ul>
    </nav>
  </header>
</template>
