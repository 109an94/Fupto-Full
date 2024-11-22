<script setup>
const route = useRoute();

const isActiveLink = (path, gender) => {
  if (path === "products") {
    return route.path === `/${path}` && route.query.gender === gender;
  }
  return route.path.startsWith(`/${path}`);
};
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
<<<<<<< HEAD
          <NuxtLink to="/boards/list" >게시글</NuxtLink>
=======
          <NuxtLink to="/boards" :class="{ 'active-link': isActiveLink('boards') }">게시글</NuxtLink>
>>>>>>> ae32e5ada96cdd9e87fafe5f15fcb5406547692f
        </li>
      </ul>
    </nav>
  </header>
</template>
