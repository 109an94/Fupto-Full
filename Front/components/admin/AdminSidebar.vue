<script setup>
const route = useRoute();
const isCollapsed = ref(false);
const activeDropdown = ref(null);
const activeMenuItem = ref(null);

// 드롭다운 토글
const handleDropdownClick = (section, event) => {
  event.preventDefault();

  // 상위 메뉴 활성화 (파란색 배경)
  if (activeMenuItem.value === section) {
    activeMenuItem.value = null;
  } else {
    activeMenuItem.value = section;
  }

  // 드롭다운 토글
  if (activeDropdown.value === section) {
    activeDropdown.value = null;
  } else {
    activeDropdown.value = section;
  }
};

// 현재 메뉴가 활성화되어 있는지 확인
const isMenuActive = (section) => {
  return activeMenuItem.value === section;
};

// 드롭다운이 보여져야 하는지 확인
const shouldShowDropdown = (section) => {
  return activeDropdown.value === section;
};

// 하위 메뉴 아이템이 활성화되어 있는지 확인
const isSubMenuActive = (path) => {
  return route.path === path;
};

// 브랜드 로고 클릭 처리
const handleBrandClick = () => {
  activeMenuItem.value = "home";
  activeDropdown.value = null;
};

// 사이드바 마우스 이벤트 처리
const handleSidebarMouseLeave = () => {
  if (isCollapsed.value) {
    activeDropdown.value = null;
  }
};

onMounted(() => {
  if (route.path === "/admin") {
    activeMenuItem.value = "home";
  } else {
    const pathParts = route.path.split("/");
    if (pathParts.length >= 3) {
      const section = pathParts[2];
      activeMenuItem.value = section;
      activeDropdown.value = section;
    }
  }
});

watch(
  () => route.path,
  (newPath) => {
    if (newPath === "/admin") {
      activeMenuItem.value = "home";
      activeDropdown.value = null;
    } else {
      const pathParts = newPath.split("/");
      if (pathParts.length >= 3) {
        const section = pathParts[2];
        activeMenuItem.value = section;
        activeDropdown.value = section;
      }
    }
  }
);
</script>

<template>
  <section id="sidebar" :class="{ hide: isCollapsed }" @mouseleave="handleSidebarMouseLeave">
    <NuxtLink to="/admin" class="brand" @click="handleBrandClick"> <i class="bx bxs-smile icon"></i> FUPTO </NuxtLink>
    <ul class="side-menu">
      <li>
        <NuxtLink to="/admin" :class="{ active: activeMenuItem === 'home' }"> <i class="bx bxs-dashboard icon"></i> 홈 </NuxtLink>
      </li>
      <li class="divider" :data-text="isCollapsed ? '-' : 'main'">사이트 관리</li>
      <li>
        <a href="#" @click="handleDropdownClick('product', $event)" :class="{ active: isMenuActive('product') }">
          <i class="bx bxs-inbox icon"></i> 상품
          <i class="bx bx-chevron-right icon-right"></i>
        </a>
        <ul class="side-dropdown" :class="{ show: shouldShowDropdown('product') }">
          <li>
            <NuxtLink to="/admin/product/list" :class="{ 'sub-menu-active': isSubMenuActive('/admin/product/list') }">
              상품 목록
            </NuxtLink>
          </li>
          <li>
            <NuxtLink to="/admin/product/reg" :class="{ 'sub-menu-active': isSubMenuActive('/admin/product/reg') }">
              상품 등록
            </NuxtLink>
          </li>
          <li>
            <NuxtLink to="/admin/product/map" :class="{ 'sub-menu-active': isSubMenuActive('/admin/product/map') }">
              상품 매핑
            </NuxtLink>
          </li>
        </ul>
      </li>
      <li>
        <a href="#" @click="handleDropdownClick('brand', $event)" :class="{ active: isMenuActive('brand') }">
          <i class="bx bxs-purchase-tag-alt icon"></i> 브랜드
          <i class="bx bx-chevron-right icon-right"></i>
        </a>
        <ul class="side-dropdown" :class="{ show: shouldShowDropdown('brand') }">
          <li>
            <NuxtLink to="/admin/brand/list" :class="{ 'sub-menu-active': isSubMenuActive('/admin/brand/list') }">
              브랜드 목록
            </NuxtLink>
          </li>
          <li>
            <NuxtLink to="/admin/brand/reg" :class="{ 'sub-menu-active': isSubMenuActive('/admin/brand/reg') }">
              브랜드 등록
            </NuxtLink>
          </li>
        </ul>
      </li>
      <li>
        <a href="#" @click="handleDropdownClick('shoppingmall', $event)" :class="{ active: isMenuActive('shoppingmall') }">
          <i class="bx bxs-store icon"></i> 쇼핑몰
          <i class="bx bx-chevron-right icon-right"></i>
        </a>
        <ul class="side-dropdown" :class="{ show: shouldShowDropdown('shoppingmall') }">
          <li>
            <NuxtLink to="/admin/shoppingmall/list" :class="{ 'sub-menu-active': isSubMenuActive('/admin/shoppingmall/list') }">
              쇼핑몰 목록
            </NuxtLink>
          </li>
          <li>
            <NuxtLink to="/admin/shoppingmall/reg" :class="{ 'sub-menu-active': isSubMenuActive('/admin/shoppingmall/reg') }">
              쇼핑몰 등록
            </NuxtLink>
          </li>
        </ul>
      </li>
      <li>
        <a href="#" @click="handleDropdownClick('category', $event)" :class="{ active: isMenuActive('category') }">
          <i class="bx bxs-widget icon"></i> 카테고리
          <i class="bx bx-chevron-right icon-right"></i>
        </a>
        <ul class="side-dropdown" :class="{ show: shouldShowDropdown('category') }">
          <li>
            <NuxtLink to="/admin/category/list" :class="{ 'sub-menu-active': isSubMenuActive('/admin/category/list') }">
              카테고리 관리
            </NuxtLink>
          </li>
        </ul>
      </li>
      <li class="divider" :data-text="isCollapsed ? '-' : 'user'">사용자 관리</li>
      <li>
        <a href="#" @click="handleDropdownClick('user', $event)" :class="{ active: isMenuActive('user') }">
          <i class="bx bx-user icon"></i> 고객
          <i class="bx bx-chevron-right icon-right"></i>
        </a>
        <ul class="side-dropdown" :class="{ show: shouldShowDropdown('user') }">
          <li>
            <NuxtLink to="/admin/user/list" :class="{ 'sub-menu-active': isSubMenuActive('/admin/user/list') }">
              고객 관리
            </NuxtLink>
          </li>
          <li>
            <NuxtLink to="/admin/user/update" :class="{ 'sub-menu-active': isSubMenuActive('/admin/user/update') }">
              회원 정보#
            </NuxtLink>
          </li>
        </ul>
      </li>
      <li>
        <a href="#" @click="handleDropdownClick('notice', $event)" :class="{ active: isMenuActive('notice') }">
          <i class="bx bxs-notepad icon"></i> 게시판
          <i class="bx bx-chevron-right icon-right"></i>
        </a>
        <ul class="side-dropdown" :class="{ show: shouldShowDropdown('notice') }">
          <li>
            <NuxtLink to="/admin/notice/list" :class="{ 'sub-menu-active': isSubMenuActive('/admin/notice/list') }">
              게시판 목록
            </NuxtLink>
          </li>
          <li>
            <NuxtLink to="/admin/notice/reg" :class="{ 'sub-menu-active': isSubMenuActive('/admin/notice/reg') }">
              게시판 등록
            </NuxtLink>
          </li>
        </ul>
      </li>
      <li class="divider" :data-text="isCollapsed ? '-' : 'report'">데이터 분석</li>
      <li>
        <a href="#" @click="handleDropdownClick('report', $event)" :class="{ active: isMenuActive('report') }">
          <i class="bx bxs-chart icon"></i> 통계
          <i class="bx bx-chevron-right icon-right"></i>
        </a>
        <ul class="side-dropdown" :class="{ show: shouldShowDropdown('report') }">
          <li>
            <NuxtLink to="/admin/report/view" :class="{ 'sub-menu-active': isSubMenuActive('/admin/report/view') }">
              조회수 분석
            </NuxtLink>
          </li>
          <li>
            <a href="#">가격 분석</a>
          </li>
        </ul>
      </li>
    </ul>
  </section>
</template>
