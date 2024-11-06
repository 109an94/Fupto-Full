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
        <a href="#" @click="handleDropdownClick('products', $event)" :class="{ active: isMenuActive('products') }">
          <i class="bx bxs-inbox icon"></i> 상품
          <i class="bx bx-chevron-right icon-right"></i>
        </a>
        <ul class="side-dropdown" :class="{ show: shouldShowDropdown('products') }">
          <li>
            <NuxtLink to="/admin/products/list" :class="{ 'sub-menu-active': isSubMenuActive('/admin/products/list') }">
              상품 목록
            </NuxtLink>
          </li>
          <li>
            <NuxtLink to="/admin/products/reg" :class="{ 'sub-menu-active': isSubMenuActive('/admin/products/reg') }">
              상품 등록
            </NuxtLink>
          </li>
          <li>
            <NuxtLink to="/admin/products/map" :class="{ 'sub-menu-active': isSubMenuActive('/admin/products/map') }">
              상품 매핑
            </NuxtLink>
          </li>
        </ul>
      </li>
      <li>
        <a href="#" @click="handleDropdownClick('brands', $event)" :class="{ active: isMenuActive('brands') }">
          <i class="bx bxs-purchase-tag-alt icon"></i> 브랜드
          <i class="bx bx-chevron-right icon-right"></i>
        </a>
        <ul class="side-dropdown" :class="{ show: shouldShowDropdown('brands') }">
          <li>
            <NuxtLink to="/admin/brands/list" :class="{ 'sub-menu-active': isSubMenuActive('/admin/brands/list') }">
              브랜드 목록
            </NuxtLink>
          </li>
          <li>
            <NuxtLink to="/admin/brands/reg" :class="{ 'sub-menu-active': isSubMenuActive('/admin/brands/reg') }">
              브랜드 등록
            </NuxtLink>
          </li>
        </ul>
      </li>
      <li>
        <a href="#" @click="handleDropdownClick('shoppingmalls', $event)" :class="{ active: isMenuActive('shoppingmalls') }">
          <i class="bx bxs-store icon"></i> 쇼핑몰
          <i class="bx bx-chevron-right icon-right"></i>
        </a>
        <ul class="side-dropdown" :class="{ show: shouldShowDropdown('shoppingmalls') }">
          <li>
            <NuxtLink to="/admin/shoppingmalls/list" :class="{ 'sub-menu-active': isSubMenuActive('/admin/shoppingmalls/list') }">
              쇼핑몰 목록
            </NuxtLink>
          </li>
          <li>
            <NuxtLink to="/admin/shoppingmalls/reg" :class="{ 'sub-menu-active': isSubMenuActive('/admin/shoppingmalls/reg') }">
              쇼핑몰 등록
            </NuxtLink>
          </li>
        </ul>
      </li>
      <li>
        <a href="#" @click="handleDropdownClick('categories', $event)" :class="{ active: isMenuActive('categories') }">
          <i class="bx bxs-widget icon"></i> 카테고리
          <i class="bx bx-chevron-right icon-right"></i>
        </a>
        <ul class="side-dropdown" :class="{ show: shouldShowDropdown('categories') }">
          <li>
            <NuxtLink to="/admin/categories/list" :class="{ 'sub-menu-active': isSubMenuActive('/admin/categories/list') }">
              카테고리 관리
            </NuxtLink>
          </li>
        </ul>
      </li>
      <li class="divider" :data-text="isCollapsed ? '-' : 'user'">사용자 관리</li>
      <li>
        <a href="#" @click="handleDropdownClick('members', $event)" :class="{ active: isMenuActive('members') }">
          <i class="bx bx-user icon"></i> 고객
          <i class="bx bx-chevron-right icon-right"></i>
        </a>
        <ul class="side-dropdown" :class="{ show: shouldShowDropdown('members') }">
          <li>
            <NuxtLink to="/admin/members/list" :class="{ 'sub-menu-active': isSubMenuActive('/admin/members/list') }">
              고객 관리
            </NuxtLink>
          </li>
          <li>
            <NuxtLink to="/admin/users/update" :class="{ 'sub-menu-active': isSubMenuActive('/admin/users/update') }">
              회원 정보#
            </NuxtLink>
          </li>
        </ul>
      </li>
      <li>
        <a href="#" @click="handleDropdownClick('notices', $event)" :class="{ active: isMenuActive('notices') }">
          <i class="bx bxs-notepad icon"></i> 게시판
          <i class="bx bx-chevron-right icon-right"></i>
        </a>
        <ul class="side-dropdown" :class="{ show: shouldShowDropdown('notices') }">
          <li>
            <NuxtLink to="/admin/notices/list" :class="{ 'sub-menu-active': isSubMenuActive('/admin/notices/list') }">
              게시판 목록
            </NuxtLink>
          </li>
          <li>
            <NuxtLink to="/admin/notices/reg" :class="{ 'sub-menu-active': isSubMenuActive('/admin/notices/reg') }">
              게시판 등록
            </NuxtLink>
          </li>
        </ul>
      </li>
      <li class="divider" :data-text="isCollapsed ? '-' : 'report'">데이터 분석</li>
      <li>
        <a href="#" @click="handleDropdownClick('reports', $event)" :class="{ active: isMenuActive('reports') }">
          <i class="bx bxs-chart icon"></i> 통계
          <i class="bx bx-chevron-right icon-right"></i>
        </a>
        <ul class="side-dropdown" :class="{ show: shouldShowDropdown('reports') }">
          <li>
            <NuxtLink to="/admin/reports/view" :class="{ 'sub-menu-active': isSubMenuActive('/admin/reports/view') }">
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