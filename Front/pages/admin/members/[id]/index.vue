<script setup>
useHead({
  link: [{ rel: "stylesheet", href: "/css/admin/report.css" }],
});

//---------state-------
const route = useRoute();
const members = ref([]);

//--------methods----------
const fetchMembers = async () =>{
  const id = route.params.id
  const response = await fetch(`http://localhost:8080/api/v1/admin/members/${id}`)
  const data = await response.json();

  members.value = data;
}

//-------lifecycle hooks--------
onMounted(() => {
  fetchMembers()
})



</script>

<template>
  <main>
    <h1 class="title">회원 정보</h1>
    <ul class="breadcrumbs">
      <li><a href="#">FUPTO</a></li>
      <li class="divider">/</li>
      <li><a href="#">고객</a></li>
      <li class="divider">/</li>
      <li><a href="#" class="active">회원정보</a></li>
    </ul>
  <div class="data" v-if="members">
    <div class="content-data">
      <div class="card">
        <div style="text-align: center">
          <h1 class="form-title" style="font-size: large">회원 정보</h1>
<!--          폰트 스타일 적용 필요-->
            <table class= "report member">
              <tbody class="tdCenter userForm">
                <tr>
                  <th>프로필</th>
                  <td><input type="image"></td>
                </tr>
                <tr>
                  <th>이름</th>
                  <td>{{ members.nickname }}</td>
                </tr>
                <tr>
                  <th>닉네임</th>
                  <td>{{members.userId}}</td>
                </tr>
                <tr>
                  <th>이메일</th>
                  <td>{{ members.email }}</td>
                </tr>
                <tr>
                  <th>나이</th>
                  <td>30</td>
                </tr>
                <tr>
                  <th>번호</th>
                  <td>{{ members.tel }}</td>
                </tr>
                <tr>
                  <th>성별</th>
                  <td>
                    <input type="radio" id="men" name="성별" checked ><label for="men">남성</label>
                    <input type="radio" id="women" name="성별"><label for="women">여성</label>
                  </td>


                </tr>
                <tr>
                  <th>소셜 로그인 #</th>
                  <td>
                    카카오톡 연동
                  </td>
                </tr>
              </tbody>
            </table>
        </div>
      </div>
    </div>
    <div class="content-data">
      <div class="card">
        <div style="text-align: center">
          <h1 class="form-title" style="font-size: large">활동 기록</h1>
          <table class="report member">
            <tbody>
              <tr>
                <th >가입일</th>
                <td>{{ members.createDate }}</td>
              </tr>
              <tr>
                <th>최근 로그인</th>
                <td> {{ members.loginDate }} </td>
              </tr>
              <tr>
                <th>접속 IP</th>
                <td>211.234.181.71</td>
              </tr>
              <tr>
               <th colspan="2">-</th>
              </tr>
              <tr>
                <th rowspan="4">작성글</th>
                <td> 게시글 :<span> 0개</span> </td>
              </tr>
              <tr>
                <td> 덧글 :<span> 0개</span> </td>
              </tr>
              <tr>
                <td> 자랑 :<span> 0개</span> </td>
              </tr>
              <tr>
                <td> 문의 :<span> 0개</span> </td>
              </tr>
              <tr>
                <th colspan="2">-</th>
              </tr>
              <tr>
                <th rowspan="5">작성글</th>
                <td> 경고 횟수 :<span> 0개</span> </td>
              </tr>
              <tr>
                <td> 제재 여부 :
                  <select name="" id="">
                    <option value="">일반</option>
                    <option value="">경고</option>
                    <option value="">제재</option>
                  </select>
                </td>
              </tr>
            </tbody>
          </table>

        </div>
      </div>
    </div>
    <div class="content-data">
      <div class="card">
        <div style="text-align: center">
          <h1 class="form-title" style="font-size: large">관심 목록</h1>
          <table class="report member">
            <tbody>
            <tr>
              <th>찜</th>
              <td>목록들 </td>
            </tr>
            <tr>
              <th>알림</th>
              <td></td>
            </tr>
            </tbody>
            </table>

        </div>
      </div>
    </div>
    </div>
  </main>
</template>

<!--<style scoped>-->
<!--  @import url("@/assets/css/admin/report.css");-->

<!--</style>-->