<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";

const props = defineProps({
  options: {
    type: Array,
    required: true,
  },
  modelValue: {
    type: [String, Number],
    required: true,
  },
  placeholder: {
    type: String,
    default: "선택하세요",
  },
  required: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["update:modelValue"]);

const isOpen = ref(false);
const searchQuery = ref("");
const selectedOption = ref(null);

const updateSearchQuery = (e) => {
  searchQuery.value = e.target.value;
};

const filteredOptions = computed(() => {
  if (!searchQuery.value) return props.options;

  const searchValue = searchQuery.value;

  return props.options.filter((option) => {
    const name = option.name;
    if (/[a-zA-Z]/.test(searchValue)) {
      // 영문인 경우 대소문자 구분 없이
      return name.toLowerCase().includes(searchValue.toLowerCase());
    }
    // 한글 등 기타 문자는 그대로 비교
    return name.includes(searchValue);
  });
});

const toggleDropdown = () => {
  isOpen.value = !isOpen.value;
  if (isOpen.value) {
    searchQuery.value = "";
  }
};

const selectOption = (option) => {
  selectedOption.value = option;
  emit("update:modelValue", option.id);
  isOpen.value = false;
};

const handleClickOutside = (event) => {
  if (!event.target.closest(".searchable-select")) {
    isOpen.value = false;
  }
};

const displayValue = computed(() => {
  if (props.modelValue) {
    const option = props.options.find((opt) => opt.id === props.modelValue);
    return option ? option.name : props.placeholder;
  }
  return props.placeholder;
});

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});
</script>

<template>
  <div class="searchable-select" :class="{ 'is-open': isOpen }">
    <div class="select-trigger" @click="toggleDropdown" :class="{ required: required }">
      {{ displayValue }}
      <span class="arrow" :class="{ up: isOpen }">▼</span>
    </div>

    <div v-if="isOpen" class="select-dropdown">
      <div class="search-container">
        <input
          type="text"
          :value="searchQuery"
          placeholder="검색어를 입력하세요"
          @input="updateSearchQuery"
          @click.stop
          class="search-input"
        />
      </div>

      <div class="options-container">
        <div
          v-for="option in filteredOptions"
          :key="option.id"
          class="option"
          :class="{ selected: option.id === modelValue }"
          @click="selectOption(option)"
        >
          {{ option.name }}
        </div>
        <div v-if="filteredOptions.length === 0" class="no-results">검색 결과가 없습니다</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.searchable-select {
  position: relative;
  width: 100%;
  user-select: none;
  flex: 1;
}

.select-trigger {
  padding: 8px 12px;
  border: 1px solid #dadce5;
  border-radius: 4px;
  background: white;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 38px;
  width: 100%;
}

.select-trigger.required {
  background-color: #fff;
}

.arrow {
  font-size: 12px;
  transition: transform 0.2s ease;
}

.arrow.up {
  transform: rotate(180deg);
}

.select-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 4px;
  background: white;
  border: 1px solid #dadce5;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  max-height: 300px;
  display: flex;
  flex-direction: column;
  width: 100%;
}

.search-container {
  padding: 8px;
  border-bottom: 1px solid #dadce5;
}

.search-input {
  width: 100%;
  padding: 6px 8px;
  border: 1px solid #dadce5;
  border-radius: 4px;
  outline: none;
}

.search-input:focus {
  border-color: #3c91e6;
}

.options-container {
  overflow-y: auto;
  max-height: 250px;
}

.option {
  padding: 8px 12px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.option:hover {
  background-color: #f5f5f5;
}

.option.selected {
  background-color: #e6f3ff;
  color: #3c91e6;
}

.no-results {
  padding: 12px;
  text-align: center;
  color: #666;
}
</style>
