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
const searchInputRef = ref(null);
const highlightedIndex = ref(-1);

const updateSearchQuery = (e) => {
  searchQuery.value = e.target.value;
  highlightedIndex.value = -1;
};

const filteredOptions = computed(() => {
  if (!searchQuery.value) return props.options;

  const searchValue = searchQuery.value;

  return props.options.filter((option) => {
    const name = option.name;
    if (/[a-zA-Z]/.test(searchValue)) {
      return name.toLowerCase().includes(searchValue.toLowerCase());
    }
    return name.includes(searchValue);
  });
});

const handleKeyDown = (e) => {
  if (!isOpen.value) return;

  switch (e.key) {
    case "ArrowDown":
      e.preventDefault();
      if (highlightedIndex.value < filteredOptions.value.length - 1) {
        highlightedIndex.value++;
      }
      break;
    case "ArrowUp":
      e.preventDefault();
      if (highlightedIndex.value > 0) {
        highlightedIndex.value--;
      }
      break;
    case "Enter":
      e.preventDefault();
      if (highlightedIndex.value >= 0 && filteredOptions.value[highlightedIndex.value]) {
        selectOption(filteredOptions.value[highlightedIndex.value]);
      }
      break;
  }
};

const toggleDropdown = () => {
  if (!isOpen.value) {
    document.dispatchEvent(
      new CustomEvent("closeOtherSelects", {
        detail: { currentSelect: searchInputRef.value },
      })
    );
  }
  isOpen.value = !isOpen.value;
  if (isOpen.value) {
    searchQuery.value = "";
    highlightedIndex.value = -1;
  }
};

const selectOption = (option) => {
  selectedOption.value = option;
  setTimeout(() => {
    emit("update:modelValue", option.id);
    isOpen.value = false;
  }, 0);
};

const closeOtherSelectHandler = (e) => {
  if (e.detail.currentSelect !== searchInputRef.value) {
    isOpen.value = false;
  }
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
  document.addEventListener("closeOtherSelects", closeOtherSelectHandler);
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
  document.removeEventListener("closeOtherSelects", closeOtherSelectHandler);
});
</script>

<template>
  <div class="searchable-select" :class="{ 'is-open': isOpen }" @keydown="handleKeyDown">
    <div class="select-trigger" @click="toggleDropdown" :class="{ required: required }">
      {{ displayValue }}
      <span class="arrow" :class="{ up: isOpen }">▼</span>
    </div>

    <div v-if="isOpen" class="select-dropdown">
      <div class="search-container">
        <input
          ref="searchInputRef"
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
          v-for="(option, index) in filteredOptions"
          :key="option.id"
          class="option"
          :class="{
            selected: option.id === modelValue,
            highlighted: index === highlightedIndex,
          }"
          @click="selectOption(option)"
          @mouseover="highlightedIndex = index"
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
  font-size: 10px;
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
  background-color: #e0e0e0;
}

.option.selected {
  background-color: #e6f3ff;
  color: #328be4;
}

.option.highlighted {
  background-color: #e0e0e0;
}

.no-results {
  padding: 12px;
  text-align: center;
  color: #666;
}
</style>
