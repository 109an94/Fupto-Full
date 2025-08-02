<script setup>
useHead({
  link: [
    {
      rel: "stylesheet",
      href: "/css/style.css",
    },
  ],
});
</script>

<template>
  <div class="layout-container default-layout">
    <FuptoHeader />
    <div class="main-content">
      <slot />
    </div>
    <FuptoFooter />
    <FuptoNavbar v-if="!$route.path.match(/\/products\/.*\/detail/)" />
  </div>
</template>
