<script setup>
import {useAuthFetch} from "~/composables/useAuthFetch.js";
import {use$Fetch} from "~/composables/use$Fetch.js";

useHead({
  link: [{ rel: "stylesheet", href: "/css/admin/report.css" }],
});

//---------state-------
const route = useRoute();
const member = ref([]);
const gender = computed(()=>(member.value.gender));
const favorites = ref([]);
const favimgs = ref([]);

//--------methods----------
const fetchMember = async () => {
  try {
    const id = route.params.id
    console.log(id);
    const response = await use$Fetch(`/admin/members/${id}`, {
      method: 'GET'
    })
    if (!response) {
      throw new Error('데이터 조회 실패');
    }

    console.log(response);

    member.value = response;
    favorites.value = response.favoriteList || [];

    if (favorites.value != null && favorites.value.length > 0) {
      console.log('이미지 조회시작')
      console.log(favorites.value)
      await fetchFavImgs();
    }
    console.log(favorites)
  } catch (error) {
    console.error("데이터 조회 오류:", error);
  }
}

const fetchFavImgs = async () =>{
  const imagePromies = favorites.value.map(async (favorites) => {
    const imageUrl = `/admin/members/fav/${favorites.productId}/image`
    console.log(imageUrl)
    try {
      const response = await use$Fetch(imageUrl, {
        method : 'GET'
      })
      const blob = new Blob([response],{
        type: 'image/jpeg'
      })
      const imgObjectUrl = URL.createObjectURL(blob);
      return {...favorites, imageUrl: imgObjectUrl};
    } catch (error) {
      console.error(`이미지 패치 실패 ${favorites.productId}:`,error)
      return favorites;
    }
  })
  const favoritesWithImages = await Promise.all(imagePromies);
  favorites.value = favoritesWithImages;
}

//나이 계산하는 매서드
const calculateAge = (birthDate) => {
  const today = new Date();
  const birthDateObj = new Date(birthDate);
  let age = today.getFullYear() - birthDateObj.getFullYear();
  const monthDiff = today.getMonth() - birthDateObj.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate()
      < birthDateObj.getDate())) {
    age--;
  }
  return age;
}

//Instant 뒷자리 일자 남기는 매서드
const delectTime = (createDate) => {
  const date = new Date(createDate);
  return date.toISOString().split('T')[0];
}


//radio 셀렉터 -> 엥 안써도 작동하는데?
// const isChecked = computed(()=>{
//   return member.value.gender;
// })

//-------lifecycle hooks--------
onMounted(() => {
  fetchMember()
});
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
  <div class="data" v-if="member">
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
                  <td>{{ member.nickname }}</td>
                </tr>
                <tr>
                  <th>닉네임</th>
                  <td>{{member.userId}}</td>
                </tr>
                <tr>
                  <th>이메일</th>
                  <td>{{ member.email }}</td>
                </tr>
                <tr>
                  <th>나이</th>
                  <td v-if="member.birthDate">{{ calculateAge(member.birthDate) }}</td>
                </tr>
                <tr>
                  <th>번호</th>
                  <td>{{ member.tel }}</td>
                </tr>
                <tr>
                  <th>성별</th>
                  <td>
                    <input type="radio" id="men" name="성별"
                           v-model="gender" :checked="gender === '남성'" disabled ><label for="남성">남성</label>
                    <input type="radio" id="women" name="성별"
                           v-model="gender" :checked="gender === '여성'" disabled ><label for="여성">여성</label>
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
                <td v-if="member.createDate">{{ delectTime(member.createDate) }}</td>
              </tr>
              <tr>
                <th>최근 로그인</th>
                <td v-if="member.loginDate"> {{ delectTime(member.loginDate) }} </td>
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
                <td> 게시글 :<span>&#160;{{ member.boardCount }}개</span> </td>
              </tr>
              <tr>
                <td> 덧글 :<span> 0개</span> </td>
              </tr>
              <tr>
                <td> 문의 :<span> 0개</span> </td>
              </tr>
              <tr>
                <td> 찜 :<span>&#160; {{member.favoriteCount}}</span> </td>
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
          <table class="report fav">
            <tbody>
            <tr>
              <th>이미지</th>
              <th colspan="3">상품명</th>
              <th>등록일</th>
            </tr>
            <tr v-for="f in favorites" :key="f.id">
              <td><img class="image-preview"
                       :src="f.imageUrl || 'https://via.placeholder.com/80'"
                       :alt="f.productName"
                       style="width: 80px; height: 80px; object-fit: cover;"> </td>
              <td colspan="3">{{f.productBrandName }} <br> {{ f.productName }}</td>
              <td>{{ delectTime(f.createDate) }}</td>
            </tr>
            </tbody>
            </table>
        </div>
      </div>
    </div>
    </div>
  </main>
</template>

<style scoped>


</style>