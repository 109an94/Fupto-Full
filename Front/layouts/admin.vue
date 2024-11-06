<script setup>
useHead({
  link: [
    { rel: "stylesheet", href: "https://unpkg.com/boxicons@2.0.9/css/boxicons.min.css" },
    { rel: "stylesheet", href: "https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css" },
    { rel: "stylesheet", href: "/css/admin/admin-style.css" },
  ],
  script: [
    { src: "https://unpkg.com/boxicons@2.0.9/dist/boxicons.js", body: true },
    { src: "https://cdn.jsdelivr.net/npm/flatpickr", body: true },
    // { src: "/js/admin/script.js", body: true },
  ],
});
</script>

<template>
  <div>
    <AdminSidebar />
    <section id="content">
      <AdminNavbar />
      <slot />
    </section>
  </div>
</template>
