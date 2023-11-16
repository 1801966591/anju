<template>
  <transition
      appear
      enter-active-class="animate__animated animate__bounceInLeft"
      leave-active-class="animate__animated animate__bounceOutLeft">
    <div v-show="store.loginWelcomeShow">
      <div style="margin-top: -50px;margin-left: -10px"><img src="../../assets/imgs/logo.png" style="width: 150px;height: 35px" alt=""/> </div>
      <div class="welcome1">欢迎来到安居租房</div>
      <div class="welcome2">登录后解锁更多功能</div>
    </div>
  </transition>
  <transition
      appear
      enter-active-class="animate__animated animate__bounceInRight"
      leave-active-class="animate__animated animate__bounceOutRight">
    <div class="loginBody" v-show="store.loginShow">
      <div style="margin-left: 340px;padding-top: 20px;cursor: pointer;font-size: 18px">
        <el-icon @click="closeLogin"><close/></el-icon>
      </div>
      <el-form>
        <el-form-item>
          <div class="group" style="margin-top: 80px">
            <input v-model="loginForm.username" class="input" placeholder="请输入用户名">
          </div>
        </el-form-item>
        <el-form-item>
          <div class="group">
            <input type="password" v-model="loginForm.password" class="input" placeholder="请输入密码">
          </div>
        </el-form-item>
        <el-form-item>
          <div class="group">
            <input v-model="code" class="input" placeholder="请输入验证码" style="width: 120px">
          </div>
          <img :src="captchaImage" @click="refreshCaptcha" style="width: 200px;height: 60px;margin-left: 10px;border-radius: 5px;cursor: pointer" alt=""/>
        </el-form-item>
      </el-form>
      <el-link :underline="false" style="margin-left: 300px;font-family: PingFangMedium,sans-serif" @click="handleForgetPassword">忘记密码?</el-link>
      <button class="button" @click="login">登 录</button>
      <button class="button" @click="handleRegister" style="margin-left: 40px">注 册</button>
    </div>
  </transition>

  <transition
      appear
      enter-active-class="animate__animated animate__bounceInRight"
      leave-active-class="animate__animated animate__bounceOutRight">
    <div class="loginBody" v-show="store.forgetPasswordShow">
      <div style="margin-left: 340px;padding-top: 20px;cursor: pointer;font-size: 18px">
        <el-icon @click="closeLogin"><close/></el-icon>
      </div>
      <el-form>
        <el-form-item>
          <div class="group" style="margin-top: 20px">
            <input v-model="findBackPasswordForm.username" class="input" placeholder="请输入用户名">
          </div>
        </el-form-item>
        <el-form-item>
          <div class="group">
            <input v-model="findBackPasswordForm.email" class="input" style="width: 180px" placeholder="请输入邮箱">
            <button type="button" class="button" style="width: 270px;margin-left: 20px;margin-top: -3px;height: 35px" @click="sendEmail">发送验证码</button>
          </div>
        </el-form-item>
        <el-form-item>
          <div class="group">
            <input v-model="findBackPasswordForm.code" class="input" placeholder="请输入验证码">
          </div>
        </el-form-item>
        <el-form-item>
          <div class="group">
            <input type="password" v-model="findBackPasswordForm.password" class="input" placeholder="请输入新密码">
          </div>
        </el-form-item>
        <el-form-item>
          <div class="group">
            <input type="password" v-model="findBackPasswordForm.checkPass" class="input" placeholder="请再次输入新密码">
          </div>
        </el-form-item>
      </el-form>
      <button class="button" @click="findBack" style="width:200px;margin-top: 20px">找 回</button>
      <button class="button" @click="handleLogin()" style="width:200px;margin-top: 10px">返回登录</button>
    </div>
  </transition>
</template>

<script setup>
import {Close} from "@element-plus/icons-vue";
import {loginStore} from "../../stores/loginStore.js";
import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import request from "../../utils/request.js";
import axios from "axios";
import {useRouter} from "vue-router";
import {dataStore} from "../../stores/dataStore.js";

const store = loginStore()
let loginForm = reactive({
  username : '',
  password : '',
})
const handleForgetPassword = () => {
  store.loginShow = false
  loginForm = {
    username : '',
    password : '',
  }
  setTimeout(()=>{
    store.forgetPasswordShow = true
  },800)
  store.loginAndRegisterVisible = true
}

let findBackPasswordForm = reactive({
  username: '',
  email: '',
  code: '',
  password: '',
  checkPass: '',
})

const data = dataStore()

const verifyInput = () => {
  if (findBackPasswordForm.username === ''){
    ElMessage.error("用户名不能为空")
  }else if (findBackPasswordForm.email === ''){
    ElMessage.error("邮箱不能为空")
  }else if(!findBackPasswordForm.email.match(/^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/)) {
    ElMessage.error("请输入正确的邮箱")
  }else {
    return true
  }
}

const sendEmail = () => {
  if (verifyInput()){
    request.get("/user/sendEmail",{
      params: findBackPasswordForm
    }).then(res => {
      if (res.code === '200'){
        data.successMsg(res,"发送成功")
      }else {
        data.errorMsg(res,router)
      }
    })
  }
}

const findBack = () => {
  if (verifyInput()){
    if (findBackPasswordForm.code === ''){
      ElMessage.error("请输入验证码")
    }else if (findBackPasswordForm.password === ''){
      ElMessage.error("请输入新密码")
    }else if (findBackPasswordForm.checkPass === ''){
      ElMessage.error("请再次输入新密码")
    }else if (findBackPasswordForm.password !== findBackPasswordForm.checkPass){
      ElMessage.error("两次密码输入不一致")
    }else {
      request.put("/user/findBackPassword",findBackPasswordForm).then(res => {
        if (res.code === '200') {
          data.successMsg(res,"密码找回成功，即将转到登录页面")
          setTimeout(()=>{handleLogin()},1000)
        }else {
          data.errorMsg(res,router)
        }
      })
    }
  }
}

const handleLogin = () => {
  store.forgetPasswordShow = false
  findBackPasswordForm.username = ''
  findBackPasswordForm.email = ''
  findBackPasswordForm.code = ''
  findBackPasswordForm.password = ''
  findBackPasswordForm.checkPass = ''
  setTimeout(()=>{
    store.loginWelcomeShow = true
    store.loginShow = true
  },800)
  store.loginAndRegisterVisible = true
}

const handleRegister = () => {
  store.loginShow = false
  store.loginWelcomeShow = false
  loginForm = {
    username : '',
    password : '',
  }
  setTimeout(()=>{
    store.registerWelcomeShow = true
    store.registerShow = true
  },800)
  store.loginAndRegisterVisible = true
}
const router = useRouter();
const closeLogin = () => {
  store.loginShow = false
  store.loginWelcomeShow = false
  store.forgetPasswordShow = false
  store.loginAndRegisterVisible = false
  store.forgetPasswordShow = false
  loginForm = {
    username : '',
    password : '',
    role: 'USER',
  }
}
let captchaImage = ref(import.meta.env.VITE_APP_BASE_URL + '/api/code')
const refreshCaptcha = async () => {
  const response = await axios.get(import.meta.env.VITE_APP_BASE_URL + '/api/code', {responseType: 'blob'});
  const blob = new Blob([response.data], {type: 'image/jpeg'});
  captchaImage.value = URL.createObjectURL(blob);
}

const code = ref('')
const login = () => {
  if (loginForm.username === '') {
    ElMessage.error("用户名不能为空")
  }else if(loginForm.password === ''){
    ElMessage.error("密码不能为空")
  }else if (code === ''){
    ElMessage.error("验证码不能为空")
  } else if (code !== ''){
    request.get("/getCode").then(res => {
      if (res.code === '200') {
        if (code.value !== res.data) {
          ElMessage({
            message: '验证码输入错误',
            type: 'error',
            duration: 1500,
            onClose : () => {
              refreshCaptcha()
            }
          })
        } else {
          request.post("/user/userLogin",loginForm).then(res => {
            if (res.code === '200') {
              ElMessage({
                message: "登录成功",
                type: 'success',
                duration: 1500,
                onClose : () => {
                  localStorage.setItem("user", JSON.stringify(res.data));
                  store.user = JSON.parse(localStorage.getItem("user"))
                  router.go(0)
                }
              })
            } else {
              ElMessage({
                message: res.msg,
                type: 'error',
                duration: 1500,
                onClose : () => {
                  refreshCaptcha()
                }
              })
            }
          })
        }
      }else {
        ElMessage({
          message: res.msg,
          type: 'error',
          duration: 1500,
          onClose : () => {
            refreshCaptcha()
          }
        })
      }
    })
  }
}
</script>

<style scoped>
@import "../../assets/css/loginAndRegister.css";
</style>