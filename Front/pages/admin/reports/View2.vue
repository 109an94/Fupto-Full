<!-- <script setup>
import { ref } from 'vue';

const buttons = ['일간', '주간', '월간', '분기'];
const activeButton = ref(null);

const setActiveButton = (index) => {
activeButton.value = index;
};
</script> -->

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
                        <div class="filter-btn"><button class="btn-url btn-main">검색</button></div>
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

            <div class="data">
                <div class="info-data">
                    <div class="card">
                        <div class="head">
                            <div>
                                <h2>1500</h2>
                                <p>Traffic</p>
                            </div>
                            <i class='bx bx-trending-up icon' ></i>
                        </div>
                        <span class="progress" data-value="40%"></span>
                        <span class="label">40%</span>
                    </div>
                </div>
            </div>

			<div class="data">
				<div class="content-data">
					<div class="head">
						<h3>Sales Report</h3>
						<div class="menu">
							<i class='bx bx-dots-horizontal-rounded icon'></i>
							<ul class="menu-link">
								<li><a href="#">Edit</a></li>
								<li><a href="#">Save</a></li>
								<li><a href="#">Remove</a></li>
							</ul>
						</div>
					</div>
					<form action="#">
						<div class="form-group">
							<input type="text" placeholder="Type...">
							<button type="submit" class="btn-send"><i class='bx bxs-send' ></i></button>
						</div>
					</form>
				</div>
			</div>
		</main>
    </template>
<!-- <script src="https://cdn.jsdelivr.net/npm/apexcharts"></script> -->
<style scoped>
    //@import '@/assets/css/admin/report.css';
</style>
<script setup lang="ts">
</script>