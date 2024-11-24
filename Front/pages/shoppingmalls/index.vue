<template>
  <div class="shoppingmall-list">
    <h1>ShoppingMalls</h1>
    <div class="search-container">
      <input 
        type="text" 
        placeholder="Search ShoppingMalls" 
        v-model="searchQuery"
        @input="selectedLetter = null"
      />
    </div>
    <div class="alphabet-links">
      <span 
        v-for="letter in alphabetWithNumbers" 
        :key="letter"
        @click="toggleLetter(letter)"
        :class="{ 'selected': selectedLetter === letter }"
      >
        {{ letter }}
      </span>
    </div>
    <div v-if="Object.keys(filteredShoppingmalls).length === 0" class="no-results">
      검색 결과가 없습니다.
    </div>
    <div v-else class="shoppingmall-groups">
      <div 
        v-for="(shoppingmalls, letter) in sortedFilteredShoppingmalls" 
        :key="letter" 
        class="shoppingmall-group"
        :class="{ 'faded': selectedLetter && selectedLetter !== letter }"
      >
        <h2>{{ letter }}</h2>
        <ul>
          <li v-for="shoppingmall in shoppingmalls" :key="shoppingmall.id">
            <NuxtLink :to="`/shoppingmalls/${shoppingmall.id}/detail`">
              <span class="star">☆</span>
              {{ shoppingmall.engName }}
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
const alphabetWithNumbers = ref([...alphabet.value, "0~9"]);
const selectedLetter = ref(null);
const shoppingmalls = ref({});
const route = useRoute();
const router = useRouter();

// const fetchShoppingmalls = async () => {
//   const config = useRuntimeConfig();
//   try {
//     const response = await $fetch(`${config.public.apiBase}/shoppingmalls`, {
//       params: {
//         shoppingmall: route.query.shoppingmall ? route.query.shoppingmall.split(",") : undefined,
//       },
//     });

//     shoppingmalls.value = groupShoppingmallsByFirstLetter(response);
//   } catch (error) {
//     console.error("Error fetching shoppingmalls:", error);
//   }
// };

const fetchShoppingmalls = async () => {
  const config = useRuntimeConfig();
  try {
    const response = await use$Fetch(`/shoppingmalls`, {
      params: {
        shoppingmall: route.query.shoppingmall ? route.query.shoppingmall.split(",") : undefined,
      },
    });

    shoppingmalls.value = groupShoppingmallsByFirstLetter(response);
  } catch (error) {
    console.error("Error fetching shoppingmalls:", error);
  }
};

const groupShoppingmallsByFirstLetter = (shoppingmalls) => {
  return shoppingmalls.reduce((acc, shoppingmall) => {
    const firstLetter = shoppingmall.engName.charAt(0).toUpperCase();
    if (/[0-9]/.test(firstLetter)) {
      // 숫자는 "0~9" 그룹으로
      if (!acc["0~9"]) acc["0~9"] = [];
      acc["0~9"].push(shoppingmall);
    } else {
      // 알파벳 그룹
      if (!acc[firstLetter]) acc[firstLetter] = [];
      acc[firstLetter].push(shoppingmall);
    }
    return acc;
  }, {});
};

const filteredShoppingmalls = computed(() => {
  let filtered = { ...shoppingmalls.value };

  // 검색어 필터링
  if (searchQuery.value) {
    const search = searchQuery.value.toLowerCase();
    Object.keys(filtered).forEach(letter => {
      filtered[letter] = filtered[letter].filter(shoppingmall => 
        shoppingmall.engName.toLowerCase().includes(search)
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

// 정렬: 0~9 그룹을 마지막으로
const sortedFilteredShoppingmalls = computed(() => {
  const entries = Object.entries(filteredShoppingmalls.value);
  const alphabetEntries = entries.filter(([key]) => key !== "0~9").sort(([a], [b]) => a.localeCompare(b));
  const numberEntries = entries.filter(([key]) => key === "0~9");
  return Object.fromEntries([...alphabetEntries, ...numberEntries]);
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
  fetchShoppingmalls();
});
</script>

<style scoped>
.shoppingmall-list {
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
  gap: 1.3rem;
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

.shoppingmall-groups {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  justify-content: space-between;
  padding: 10px;
}

.shoppingmall-group {
  margin: 0;
  min-width: 0;
  width: 100%;
}

/* 반응형 디자인을 위한 미디어 쿼리 */
@media (max-width: 1200px) {
  .shoppingmall-groups {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 900px) {
  .shoppingmall-groups {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .shoppingmall-groups {
    grid-template-columns: 1fr;
  }
}


.shoppingmall-group.faded {
  opacity: 0.5;
}

.shoppingmall-group h2 {
  font-size: 1.5rem;
  color: var(--color-text-title);
  text-align: left;
}

.shoppingmall-group ul {
  list-style: none;
  padding: 0;
}

.shoppingmall-group li {
  font-size: 1rem;
  color: var(--color-text-default);
  display: flex;
  align-items: center;
}

.shoppingmall-group .star {
  margin-right: 0.5rem;
  color: var(--color-sub-1);
}
</style>