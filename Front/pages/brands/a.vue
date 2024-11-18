<template>
    <div class="designer-list">
      <h1>Designers for Women</h1>
      <div class="search-container">
        <input type="text" placeholder="Search Designers" v-model="searchQuery" />
      </div>
      <div class="alphabet-links">
        <span v-for="letter in alphabet" :key="letter">{{ letter }}</span>
      </div>
      <div class="designer-groups">
        <div v-for="(designers, letter) in filteredDesigners" :key="letter" class="designer-group">
          <h2>{{ letter }}</h2>
          <ul>
            <li v-for="designer in designers" :key="designer">
              <span class="star">☆</span> {{ designer }}
            </li>
          </ul>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed } from 'vue';
  
  const searchQuery = ref("");
  const alphabet = ref(Array.from("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
  const designers = ref({
    A: ["A BATHING APE®", "A-COLD-WALL*", "A KIND OF GUISE", "Acne Studios", "ACRONYM", "ADER error", "adidas"],
    E: ["Each x Other", "ecco.kollektive", "ECKHAUS LATTA", "EDWARD CUMING", "ELENA DAWSON", "Ellery"],
    M: ["MACH & MACH", "MACKAGE", "Mackintosh", "MAD Paris", "MAGDA BUTRYM", "Maison Kitsuné"],
    S: ["sacai", "SAINT LAURENT", "SALOMON", "Sandro", "SANDY LIANG", "Sankuanz", "Santoni"],
  });
  
  const filteredDesigners = computed(() => {
    if (!searchQuery.value) return designers.value;
    const search = searchQuery.value.toLowerCase();
    return Object.fromEntries(
      Object.entries(designers.value).map(([letter, names]) => [
        letter,
        names.filter((name) => name.toLowerCase().includes(search)),
      ])
    );
  });
  </script>
  
  <style scoped>
  .designer-list {
    font-family: "Noto Sans KR", sans-serif;
    text-align: center;
    padding: 2rem;
  }
  
  h1 {
    font-size: 2rem;
    color: var(--color-text-title);
  }
  
  .search-container {
    margin: 1rem 0;
  }
  
  .search-container input {
    padding: 0.5rem;
    font-size: 1rem;
    border: 1px solid #ccc;
    border-radius: 4px;
    width: 100%;
    max-width: 400px;
  }
  
  .alphabet-links {
    display: flex;
    justify-content: center;
    gap: 0.5rem;
    margin: 1rem 0;
    font-size: 1.2rem;
    color: var(--color-text-unselected);
  }
  
  .alphabet-links span {
    cursor: pointer;
  }
  
  .designer-groups {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-around;
  }
  
  .designer-group {
    margin: 1rem;
    min-width: 150px;
    text-align: left;
  }
  
  .designer-group h2 {
    font-size: 1.5rem;
    color: var(--color-text-title);
  }
  
  .designer-group ul {
    list-style: none;
    padding: 0;
  }
  
  .designer-group li {
    font-size: 1rem;
    color: var(--color-text-default);
    display: flex;
    align-items: center;
  }
  
  .designer-group .star {
    margin-right: 0.5rem;
    color: var(--color-sub-r);
  }
  </style>
  