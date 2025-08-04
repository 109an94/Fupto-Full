<template>
  <div class="designer-list">
    <h1>Designers</h1>
    <div class="search-container">
      <input 
        type="text" 
        placeholder="Search Designers" 
        v-model="searchQuery"
        @input="selectedLetter = null"
      />
    </div>
    <div class="alphabet-links">
      <span 
        v-for="letter in alphabet" 
        :key="letter"
        @click="toggleLetter(letter)"
        :class="{ 'selected': selectedLetter === letter }"
      >
        {{ letter }}
      </span>
    </div>
    <div v-if="Object.keys(filteredDesigners).length === 0" class="no-results">
      검색 결과가 없습니다.
    </div>
    <div v-else class="designer-groups">
      <div 
        v-for="(designers, letter) in filteredDesigners" 
        :key="letter" 
        class="designer-group"
        :class="{ 'faded': selectedLetter && selectedLetter !== letter }"
      >
        <h2>{{ letter }}</h2>
        <ul>
          <li v-for="designer in designers" :key="designer.id">
            <NuxtLink :to="`/brands/${designer.id}/detail`">
              <span class="star">☆</span>
              {{ designer.engName }}
            </NuxtLink>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const searchQuery = ref("");
const alphabet = ref(Array.from("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
const selectedLetter = ref(null);
const designers = ref({});
const route = useRoute();
const router = useRouter();

const fetchBrands = async () => {
  const config = useRuntimeConfig();
  try {
    const response = await use$Fetch(`/brands`, {
      params: {
        brand: route.query.brand ? route.query.brand.split(",") : undefined,
      },
    });

    designers.value = groupDesignersByFirstLetter(response);
  } catch (error) {
    console.error("Error fetching brands:", error);
  }
};

const groupDesignersByFirstLetter = (brands) => {
  return brands.reduce((acc, brand) => {
    const firstLetter = brand.engName.charAt(0).toUpperCase();
    if (!acc[firstLetter]) acc[firstLetter] = [];
    acc[firstLetter].push(brand);
    return acc;
  }, {});
};

const filteredDesigners = computed(() => {
  let filtered = { ...designers.value };

  // 검색어 필터링
  if (searchQuery.value) {
    const search = searchQuery.value.toLowerCase();
    Object.keys(filtered).forEach(letter => {
      filtered[letter] = filtered[letter].filter(designer => 
        designer.engName.toLowerCase().includes(search)
      );
    });
  }

  // 선택된 알파벳 필터링
  if (selectedLetter.value) {
    filtered = {
      [selectedLetter.value]: filtered[selectedLetter.value] || []
    };
  }

  // 빈 배열을 가진 키 제거
  return Object.fromEntries(
    Object.entries(filtered).filter(([_, names]) => names.length > 0)
  );
});

const toggleLetter = (letter) => {
  if (selectedLetter.value === letter) {
    selectedLetter.value = null;
  } else {
    selectedLetter.value = letter;
  }
  // 알파벳 선택 시 검색어 초기화
  searchQuery.value = "";
};

onMounted(() => {
  fetchBrands();
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
  flex-wrap: wrap;
  justify-content: center;
  gap: 1.5rem;
  margin: 1rem 0;
  font-size: 1.2rem;
  color: var(--color-text-unselected);
}

.alphabet-links span {
  cursor: pointer;
  padding: 0.5rem;
}

.alphabet-links span.selected {
  color: var(--color-text-title);
  font-weight: bold;
}

.no-results {
  text-align: center;
  padding: 2rem;
  font-size: 1.2rem;
  color: var(--color-text-default);
}

.designer-groups {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  justify-content: space-between;
  padding: 10px;
}

.designer-group {
  margin: 0;
  min-width: 0;
  width: 100%;
}

/* 반응형 디자인을 위한 미디어 쿼리 */
@media (max-width: 1200px) {
  .designer-groups {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 900px) {
  .designer-groups {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .designer-groups {
    grid-template-columns: 1fr;
  }
}


.designer-group.faded {
  opacity: 0.5;
}

.designer-group h2 {
  font-size: 1.5rem;
  color: var(--color-text-title);
  text-align: left;
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
  color: var(--color-sub-1);
}
</style>