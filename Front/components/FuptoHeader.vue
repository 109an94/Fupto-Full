<template>
  <header class="main-header">
    <nav class="main-nav">
      <h1 class="main-nav_title"><NuxtLink to="/">FUPTO</NuxtLink></h1>
      <ul class="main-nav-list">
        <li class="main-nav_link">
          <a href="#">여성</a>
          <NuxtLink to="/products/list">남성</NuxtLink>
        </li>
        <li class="main-nav_link">
          <a href="#">브랜드</a>
          <a href="#">쇼핑몰</a>
        </li>
        <li class="main-nav_link">
          <NuxtLink to="/board/notice">게시글</NuxtLink>
        </li>
      </ul>
    </nav>
  </header>
</template>
