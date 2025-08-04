<script setup>
import { ref, reactive } from 'vue';
import vueApexCharts from 'vue3-apexcharts';

const apexchart = vueApexCharts;

  const chartOptions = reactive({
    chart:{
      id: 'vuechart'
    },
    xaxis: {
      categories: [2020,2021,2022,2023,2024]
    }
  });
  const series = ref([{
    name: 'series-1',
    data: [150,360,4200,3800,9500]
  }]);

const productsData = ref([
  {
    rank: 1,
    code: '8',
    name: '트위스트 팬츠',
    brand: '르메르',
    views: 1430,
    subProducts: [
      { code: '1', name: '서브 제품 1' },
      { code: '2', name: '서브 제품 2' }
    ],
    subProductsVisible: false
  },
  {
    rank: 2,
    code: '23',
    name: '랩 오버 셔츠',
    brand: '르메르',
    views: 800,
    subProducts: [
      { code: '3', name: '서브 제품 3' }
    ],
    subProductsVisible: false
  },
  {
    rank: 3,
    code: '888',
    name: '다른 거',
    brand: '다른 브랜드',
    views: 620,
    subProducts: [
      { code: '4', name: '서브 제품 4' },
      { code: '5', name: '서브 제품 5' }
    ],
    subProductsVisible: false
  }
]);

const toggleSubProducts = (index) => {
  productsData.value[index].subProductsVisible = !productsData.value[index].subProductsVisible;
};
</script>



<template>
<main>
			<h1 class="title">조회수 분석</h1>
			<ul class="breadcrumbs">
				<li><a href="#">FUPTO</a></li>
				<li class="divider">/</li>
				<li><a href="#">통계</a></li>
				<li class="divider">/</li>
				<li><a href="#" class="active">조회수</a></li>
			</ul>
            
            <div class="data">
                <div class="content-data">
                    <div class="card">
                        <div class="">
<!--                          class에 head 제거함-->
                          <div class="filter">
                          <table class="report">
                            <tbody>
                              <tr>
                                <th>목록</th>
                                <td>
                                  <button class="btn-url active">상품별 조회량</button>
                                  <button class="btn-url">브랜드 조회량</button>
                                  <button class="btn-url">쇼핑몰 조회량</button>
                                  <button class="btn-url">찜 횟수</button>
                                </td>
                              </tr>
                              <tr>
                                <th>기간</th>
                                <td>
                                  <button class="btn-url active">일간</button>
                                  <button class="btn-url">월간</button>
                                  <button class="btn-url">주간</button>
                                  <button class="btn-url">반기</button>
                                  <button class="btn-url">년간</button>
                                  <input type="date">
                                  <span>~</span>
                                  <input type="date">
                                </td>
                              </tr>
                            </tbody>
                          </table>
                          </div>
                        </div>

                        <div class="apexchart">
                          <apexchart width="600" height=300 type="line" :options="chartOptions" :series="series"></apexchart>
                        </div>
                        <div class="filter-btn"><button class="btn-url btn-main">검색</button></div>
                    </div>
                </div>
            </div>

<!--  테스트 구현 -->
            <div class="data">
                <div class="content-data">
                    <div class="card">
                        <div class="title center">
                            <h1>상품 조회량</h1>
                        </div>
                        <div>
                            <table class="report chart">
                                <thead>
                                <tr>
                                <th>순위</th>
                                <th>품목코드</th>
                                <th>상품명</th>
                                <th>브랜드</th>
                                <th>조회수</th>
                                </tr>
                            </thead>
                              <tbody>
                              <template v-for="(product, index) in productsData" :key="product.code">
                                <tr @click="toggleSubProducts(index)" :class="{ 'main-product': !product.isSub }">
                                  <td>{{ product.rank }}</td>
                                  <td>{{ product.code }}</td>
                                  <td>{{ product.name }}</td>
                                  <td>{{ product.brand }}</td>
                                  <td>{{ product.views }}</td>
                                </tr>
                                <template v-if="product.subProductsVisible">
                                  <tr v-for="subProduct in product.subProducts" :key="subProduct.code" class="sub-product">
                                    <td></td>
                                    <td>{{ subProduct.code }}</td>
                                    <td>{{ subProduct.name }}</td>
                                    <td></td>
                                    <td></td>
                                  </tr>
                                </template>
                              </template>
                              </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <div class="data">
    <div class="content-data">
      <div class="card">
        <div class="title center">
          <h1>상품 조회량</h1>
        </div>
        <div>
          <table class="report chart">
            <thead>
            <tr>
              <th>순위</th>
              <th>품목코드</th>
              <th>상품명</th>
              <th>브랜드</th>
              <th>조회수</th>
            </tr>
            </thead>
            <tbody>
            <tr>
              <td>1</td>
              <td>8</td>
              <td>트위스트 팬츠</td>
              <td>르메르</td>
              <td>1430</td>
            </tr>
            <tr>
              <td>2</td>
              <td>23</td>
              <td>랩 오버 셔츠</td>
              <td>르메르</td>
              <td>800</td>
            </tr>
            <tr>
              <td>3</td>
              <td>888</td>
              <td>다른 거</td>
              <td>다른 브랜드</td>
              <td>620</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>

		</main>
    </template>
<!-- <script src="https://cdn.jsdelivr.net/npm/apexcharts"></script> -->
<style scoped>
@import '@/public/css/admin/report.css';
.sub-product {
  background-color: #f0f0f0;
}
</style>

