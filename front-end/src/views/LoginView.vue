<template>
  <div id="body">
    <div id="SignUpTitle">회원가입</div>
    <div id="SignUpBody">
      <div id="EmailInputBody">
        <label for="email">이메일</label>
        <div id="EmailInput">
          <input type="email" id="email" v-model="email" />
          <button id="emailBtn" @click="showEmailCheck = true">인증</button>
        </div>
      </div>

      <!-- ✅ v-show에 ref 값 바인딩 -->
      <div id="EmailCheckInputBody" v-show="showEmailCheck">
        <label for="emailCheck">이메일 확인</label>
        <div id="EmailInputCheck">
          <input type="email" id="emailCheck" v-model="emailCheck" />
          <button id="emailCheckBtn">확인</button>
        </div>
      </div>

      <div id="PasswordInputBody">
        <label for="password">비밀번호</label>
        <input type="password" id="password" v-model="password" />
      </div>

      <div id="NicknameInputBody">
        <label for="nickname">닉네임</label>
        <input type="text" id="nickname" v-model="nickname" />
      </div>

      <button id="SignUpBtn">Sign Up</button>

      <div id="information">
        <span>Already have an account?</span>
        <a>Sign in</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
const showEmailCheck = ref(false);
const email = ref('');
const emailCheck = ref('');
const password = ref('');
const nickname = ref('');
</script>

<style scoped>
  #body {
    display : flex;
    flex-direction: column;
    width: 100%;
    max-width: 400px;
    margin: 0 auto;
    font-family: sans-serif;
    gap : 80px;
  }

  #SignUpBody > div {
    margin-bottom: 16px;
  }

  #SignUpTitle{
    text-align: center;
    font-size: 40px;
    padding-top: 80px;
  }

  label {
    display: block;
    margin-bottom: 4px;
    font-weight: bold;
  }

  /* input 전체 폭 맞추기 */
  input[type="email"],
  input[type="emailCheck"],
  input[type="password"],
  input[type="text"] {
    width: 100%;
    padding: 8px;
    box-sizing: border-box;
    font-size: 14px;
  }

  /* 이메일 input + 인증 버튼 정렬 */
  #EmailInputBody > div, #EmailCheckInputBody > div {
    display: flex;
    gap: 8px;
  }

  #EmailInput, #EmailInputCheck{
    display: flex;
    gap: 8px;
  }

  #email {
    flex: 1;
  }

  #emailBtn, #emailCheckBtn {
    padding: 8px 12px;
    font-size: 14px;
    white-space: nowrap;
  }

  #SignUpBtn {
    width: 100%;
    padding: 10px;
    font-size: 16px;
    background-color: #007BFF;
    color: white;
    border: none;
    cursor: pointer;
    margin-top: 16px;
  }

  #SignUpBtn:hover {
    background-color: #0056b3;
  }

  #information{
    text-align: center;
    display: flex;
    justify-content: center;
    align-items: center;
    gap : 10px;
  }

  #EmailInputContainer{
    display : flex;
    flex-direction: column;

  }
</style>
