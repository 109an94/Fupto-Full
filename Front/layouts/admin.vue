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
  ],
});

const isCollapsed = ref(false);

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value;
};
</script>

<template>
  <div>
    <AdminSidebar :is-collapsed="isCollapsed" />
    <section id="content">
      <AdminNavbar @toggle-sidebar="toggleSidebar" />
      <slot />
    </section>
  </div>
</template>
