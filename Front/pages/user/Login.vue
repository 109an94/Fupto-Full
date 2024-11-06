<template>
    <main>
        <div class="login-container">
            <div class="login-box">
                <h1>로그인</h1>
                <form action="/login" method="POST">
                    <div class="input-group">
                        <label for="username">아이디</label>
                        <input type="text" id="username" name="username" required>
                    </div>
                    <div class="input-group">
                        <label for="password">비밀번호</label>
                        <input type="password" id="password" name="password" required>
                    </div>
                    <button type="submit" class="login-button">로그인</button>
                </form>
                <div class="login-links">
                    <a href="#">아이디/비밀번호 찾기</a>
                    <a href="#">회원가입</a>
                </div>
            </div>
        </div>
    </main>
</template>

<style scoped>
@import url("@/assets/css/user-login.css");
</style>