<script setup>
import { onMounted, ref } from "vue";

useHead({
  link: [{ rel: "stylesheet", href: "/css/product-list.css" }],
});

const route = useRoute();
const gender = ref(route.query.gender); // URL에서 gender 파라미터 가져오기

// 필터 드롭다운
const isActive = ref(false);
const selectedOption = ref("인기순");

const options = ["인기순", "최근 등록순", "낮은 가격순", "높은 가격순", "할인율 높은순"];

const toggleDropdown = () => {
  isActive.value = !isActive.value;
};

const selectOption = (option) => {
  selectedOption.value = option;
  isActive.value = false;
};

// 찜하기
const toggleFavorite = (event) => {
  event.preventDefault();
  const button = event.currentTarget;
  const isFavorite = button.getAttribute("data-favorite") === "true";
  const img = button.querySelector("img");

  if (isFavorite) {
    button.setAttribute("data-favorite", "false");
    img.src = "/imgs/icon/favorite.svg";
    img.alt = "즐겨찾기 추가";
  } else {
    button.setAttribute("data-favorite", "true");
    img.src = "/imgs/icon/favorite-fill.svg";
    img.alt = "즐겨찾기 제거";
  }
};

// 외부 클릭시 드롭다운 닫기
const handleClickOutside = (e) => {
  if (!e.target.closest(".filter-select")) {
    isActive.value = false;
  }
};

// 브랜드 프로필 탭 전환
const activeTab = ref("details");
const setActiveTab = (tab) => {
  activeTab.value = tab;
};

watch(
  () => route.query.gender,
  (newGender) => {
    gender.value = newGender;
  }
);

watch(
  () => route.query.gender,
  (newGender) => {
    gender.value = newGender;
  }
);

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});
</script>

<template>
  <main>
    <div class="main-content-wrapper">
      <FuptoAside :initialGender="gender" />
      <div class="product-content">
        <section class="category-frame">
          <h1 style="display: none">카테고리</h1>
          <span class="category-text"><a href="#">남성</a> &gt;</span>
        </section>

        <section>
          <!-- Brand Profile Section -->
          <div class="brand-profile">

            <div class="brand-background">
              <img class="brand-background-img" src="/imgs/brands/lemaire-bg.jpeg" alt="Brand Background">
            </div>

            <div class="brand-header">
              <img class="brand-image" src="" alt="Brand Image">
              <div class="brand-info">
                <h1 class="brand-name">Lemaire</h1>
                <p class="brand-subtitle">르메르</p>
                <p class="brand-stats">관심 11,126 | 스타일 11,126</p>
                <div class="buttons">
                  <button class="follow-button">팔로우</button>
                  <button class="info-button">브랜드 더 알아보기</button>
                </div>
              </div>
            </div>

            <div class="tabs">
              <span class="tab" :class="{ active: activeTab === 'details' }" @click="setActiveTab('details')">상세 설명</span>
              <span class="tab" :class="{ active: activeTab === 'stats' }" @click="setActiveTab('stats')">통계</span>
            </div>

            <div class="brand-details">
              <div v-if="activeTab === 'details'">
                <!-- 시즌 상품 콘텐츠 -->
                <p class="detail-type"><strong>Type :</strong> 편집샵</p>
                <p class="about-title">About</p>
                <p class="about-description">
                  SSENSE(발음은 [es-uhns])는 문화, 커뮤니티, 상거래의 교차점에서 운영되는 글로벌 기술 플랫폼입니다.
                  스트리트웨어 변화를 주도하며 여성복, 남성복, 아동복, Everything Else™에 걸쳐 기존 및 신생 럭셔리 브랜드를 포함하여 제공합니다.
                  SSENSE는 전자상거래 에디토리얼 콘텐츠 제작자로 비평가들의 찬사를 받았으며, 월 평균 1억 페이지 뷰를 생성합니다.
                  대상 고객의 약 80%는 18-40세입니다.
                </p>
              </div>

              <div v-else-if="activeTab === 'stats'">
                <!-- 통계 콘텐츠 -->
                <p>브랜드의 통계 정보가 여기에 표시됩니다.</p>
              </div>
            </div>
            
          </div>
        </section>

        <section class="filter-frame">
          <h1 class="d-none">필터 리스트</h1>
          <div class="filter-container">
            <div class="filter-select" tabindex="0" @click="toggleDropdown">
              <div class="filter-title" :class="{ active: isActive }">
                {{ selectedOption }}
              </div>
              <ul class="filter-options" :class="{ show: isActive }">
                <li v-for="option in options" :key="option" class="filter-option" @click.stop="selectOption(option)">
                  {{ option }}
                </li>
              </ul>
            </div>
          </div>
        </section>

        <section class="product-p-frame">
          <h1 style="display: none">상품 리스트</h1>
          <div class="user-product-list">
            <ul class="product-grid">
              <li class="product-c-frame">
                <a href="#">
                  <div class="product-img-frame">
                    <div class="product-img-container">
                      <img
                        class="product-images primary-img"
                        src=""
                        alt="product-img"
                      />
                      <img
                        class="product-images secondary-img"
                        src=""
                        alt="product-img-hover"
                      />
                    </div>
                    <button class="favorite-button" data-favorite="false" @click="toggleFavorite">
                      <img class="favorite" src="/imgs/icon/favorite.svg" alt="찜" />
                    </button>
                  </div>
                  <div class="product-info">
                    <span class="product-brand">Lemaire</span>
                    <span class="product-name">twisted belted work pants</span>
                    <div class="price-info">
                      <span class="product-price">1,000,000</span>
                      <span class="price-unit">원</span>
                    </div>
                  </div>
                </a>
              </li>
            </ul>
          </div>
        </section>

      </div>
    </div>
  </main>
</template>

<style scoped>
.brand-profile {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  font-family: "Noto Sans KR", sans-serif;
  color: var(--color-text-default);
}

.brand-background {
  width: 100%;
  height: 200px;
  margin-top: 24px;
  background-color: var(--color-ui-backgroundb);
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  position: relative; /* 자식 요소의 절대 위치 기준점 */
}

.brand-background-img {
  position: absolute; /* 부모 요소 기준으로 절대 위치 */
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover; /* 이미지 비율을 유지하면서 영역 채우기 */
}

.brand-header,
.tabs {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid var(--color-ui-backgroundb);
}

.brand-image {
    width: 70px;
    height: 70px;
    border-radius: 50%;        /* 원형으로 만들기 */
    object-fit: cover;         /* 이미지가 원 안에 꽉 차도록 */
    border: 0.5px solid var(--color-text-unselected);
    background-color: var(--color-ui-white)
}

.brand-info {
  flex: 1;
  margin-left: 10px;
}

.brand-name {
  color: var(--color-ui-black);
  font-size: var(--font-bold-h3);
  font-weight: var(--font-bold);
  margin: 0;
}

.brand-subtitle,
.brand-stats,
.about-description {
  font-size: var(--font-regular-caption);
  color: var(--color-text-caption);
  margin-top: 5px;
}

.buttons {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.button {
  padding: 5px 10px;
  font-size: var(--font-regular-body);
  font-weight: var(--font-regular);
  color: var(--color-ui-black);
  border: 1px solid var(--color-ui-backgroundb);
  background-color: var(--color-ui-white);
  cursor: pointer;
}

.tabs {
  justify-content: space-around;
  font-weight: var(--font-bold);
}

.tab {
  cursor: pointer;
  padding: 10px;
}

.tab.active {
  color: var(--color-main-deep);
  border-bottom: 2px solid var(--color-main-deep);
}

.brand-details {
  padding: 20px;
}

.detail-type {
  font-size: var(--font-regular-body);
  font-weight: var(--font-medium);
  margin-bottom: 10px;
}

.about-title {
  font-size: var(--font-medium-h3);
  font-weight: var(--font-bold);
  margin-top: 20px;
}
</style>