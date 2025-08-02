<template>
  <main>
    <h1 class="title">홈</h1>
    <ul class="breadcrumbs">
      <li><a href="#">FUPTO</a></li>
      <li class="divider">/</li>
      <li><a href="#" class="active">홈</a></li>
    </ul>
    <div class="info-data">
      <div class="card">
        <div class="head">
          <div>
            <h2>1500</h2>
            <p>Traffic</p>
          </div>
          <i class="bx bx-trending-up icon"></i>
        </div>
        <span class="progress" data-value="40%"></span>
        <span class="label">40%</span>
      </div>
    </div>
    <div class="data">
      <div class="content-data">
        <div class="head">
          <h3>Sales Report</h3>
          <div class="menu">
            <i class="bx bx-dots-horizontal-rounded icon"></i>
            <ul class="menu-link">
              <li><a href="#">Edit</a></li>
              <li><a href="#">Save</a></li>
              <li><a href="#">Remove</a></li>
            </ul>
          </div>
        </div>
        <form action="#">
          <div class="form-group">
            <input type="text" placeholder="Type..." />
            <button type="submit" class="btn-send"><i class="bx bxs-send"></i></button>
          </div>
        </form>
      </div>
    </div>
  </main>
</template>
