<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/admin/product-map.css" }],
});
</script>
<template>
  <main>
    <h1 class="title">상품</h1>
    <ul class="breadcrumbs">
      <li><a href="#">FUPTO</a></li>
      <li class="divider">/</li>
      <li><a href="#" class="active">상품 매핑</a></li>
    </ul>
    <div class="page-container">
      <div class="form-container">
        <h3>매핑관리</h3>
        <div class="mt-2">
          <button class="btn">매핑등록</button>
          <button class="btn">매핑수정</button>
          <button class="btn">대량매핑</button>
        </div>
        <form>
          <div class="form-group">
            <label>카테고리</label>
            <select class="form-control">
              <option value="" selected>1차 분류</option>
            </select>
            <select class="form-control">
              <option value="" selected>2차 분류</option>
            </select>
            <select class="form-control">
              <option value="" selected>3차 분류</option>
            </select>
          </div>
          <div class="form-group">
            <label>브랜드</label>
            <select class="form-control">
              <option value="" selected>브랜드 명</option>
            </select>
            <label class="ml-2">등록일</label>
            <input type="date" class="form-control" />
            <span>~</span>
            <input type="date" class="form-control" />
          </div>
          <div class="form-group">
            <label>쇼핑몰</label>
            <select class="form-control">
              <option value="" selected>쇼핑몰 명</option>
            </select>
            <button class="btn ml-2">미연동 단품 상품</button>
            <button class="btn">매핑 0개 상품</button>
          </div>
          <div class="form-group">
            <label>검색</label>
            <input type="text" class="form-control" placeholder="검색어를 입력하세요" />
            <button class="btn btn-primary">Search</button>
          </div>
        </form>
      </div>

      <div class="products-container">
        <div class="product-form">
          <h4>대표 상품</h4>
          <table class="mt-2">
            <thead>
              <tr>
                <th><input type="checkbox" /></th>
                <th>상품 코드 & 상품 명</th>
                <th>브랜드 & 쇼핑몰</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td><input type="checkbox" /></td>
                <td>코드 : 1028 대표 : null <br />상품 명 : twisted cropped pants</td>
                <td>
                  Lemiare <br />
                  YOOX
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="product-form">
          <h4>단품 상품</h4>
          <table class="mt-2">
            <thead>
              <tr>
                <th><input type="checkbox" /></th>
                <th>상품 코드 & 상품 명</th>
                <th>브랜드 & 쇼핑몰</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td><input type="checkbox" /></td>
                <td>코드 : 1028 대표 : null <br />상품 명 : twisted cropped pants</td>
                <td>
                  Lemiare <br />
                  YOOX
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div class="action-buttons">
        <button class="btn">대표상품으로 전환</button>
        <button class="btn btn-primary">수정</button>
      </div>
    </div>
  </main>
</template>
