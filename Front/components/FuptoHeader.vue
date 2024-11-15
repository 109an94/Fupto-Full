<template>
  <header class="main-header">
    <nav class="main-nav">
      <h1 class="main-nav_title"><NuxtLink to="/">FUPTO</NuxtLink></h1>
      <ul class="main-nav-list">
        <li class="main-nav_link">
          <NuxtLink to="/products?gender=2">여성</NuxtLink>
          <NuxtLink to="/products?gender=1">남성</NuxtLink>
        </li>
        <li class="main-nav_link">
          <NuxtLink to="/brands">브랜드</NuxtLink>
          <NuxtLink to="/shoppingmalls">쇼핑몰</NuxtLink>
        </li>
        <li class="main-nav_link">
          <NuxtLink to="/board/notice">게시글</NuxtLink>
        </li>
      </ul>
    </nav>
  </header>
</template>
