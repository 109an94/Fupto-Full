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
  <div class="layout-container">
    <FuptoHeader />
    <div class="main-content">
      <slot />
    </div>
    <FuptoFooter />
    <FuptoNavbar />
  </div>
</template>
