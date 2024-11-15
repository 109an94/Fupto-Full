<script setup>
useHead({
    link: [{ rel: "stylesheet", href: "/css/product-present-detail.css"}],
});
</script>
<template>
    <main>
       <section>
           <h1 hidden>카테고리</h1>
           <ol class="category">
               <li><a href="">남성</a></li>
               <li><a href="">하의</a></li>
               <li><a href="">캐주얼 팬츠</a></li>
           </ol>
       </section>

       <section>
           <h1 hidden>상품정보</h1>
           <div>
               <div class="name-container">
                   <div class="name-wrap">
                       <p class="brand-name"><a href="">lemaire</a></p>
                       <p class="product-name">twisted belted work pants</p>
                   </div>
                   <div class="icon:favorite">
                       <span>찜하기</span>
                   </div>
               </div>
               <div class="imgs-align">
                   <div class="imgs-container">
                       <div class="imgs-wrap">
                           <img src="" alt="이미지">
                           <img src="" alt="이미지2">
                           <img src="" alt="이미지3">
                           <img src="" alt="이미지4">
                           <img src="" alt="이미지5">
                       </div>
                   </div>
               </div>
               <div class="price-container">
                   <ul class="price-wrap">
                       <li class="price-retail"><del>10,000,000 원</del></li>
                       <!-- 판매가 -->
                       <li class="price-per">90%</li>
                       <!-- 할인 비율 -->
                       <li class="price-sale">1,000,000 원</li>
                       <!-- 할인가 -->
                       <li>
                           <ul class="seller-cost-container">
                           <!-- 쇼핑몰 배송 정보 -->
                               <li>+ 관세 8%</li>
                               <li>+ 부가세 10%</li>
                               <li>+ 배송비 ~27$</li>
                           </ul>
                       </li>
                   </ul>
               </div>
           </div>
       </section>

       <section>
    <!-- 모든 할인 상품 둘러보기 -->
    <h1 hidden>모든 할인 상품 둘러보기</h1>
    <div class="card-container">
        <div class="card-header">
            <div class="logo-container">
                <img src="" alt="Logo" class="logo">
            </div>
            <p class="shop-name"><a href="#">르메르 쇼핑몰</a></p>
        </div>
        <div class="item-details">
            <p class="brand-name">SSENSE</p>
            <p class="item-price">1,000,000 원</p>
        </div>
        <div class="link-container">
            <a href="#" class="link-button">링크</a>
        </div>
    </div>
</section>


       <section>
           <h1 hidden>상품 정보 고시</h1>
           <div>
               <div class="card-container">
                   <div class="card-header">상품 설명</div>
                   <div class="icon:more"><a href="">더보기</a></div>
               </div>
               <div class="dcp-wrap">
                   <p>
                       Lemaire reimagines classic workwear with this white button-up shirt, cut to an oversized fit. Made from heavy cotton poplin, it's accented with sleeve panels that create the illusion of a layered look.<br>
                       Made in Italy<br>
                       ㆍCare instructions: machine wash at 30 degrees<br>
                       ㆍButtoned cuffs<br>
                       ㆍClosure: buttoned front<br>
                       ㆍMaterial: 100% cotton<br>
                       ㆍItem number: P00695115
                   </p>
               </div> 
           </div>
       </section>

   </main>
</template>