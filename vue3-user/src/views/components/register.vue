<template>
  <transition
      appear
      enter-active-class="animate__animated animate__bounceInLeft"
      leave-active-class="animate__animated animate__bounceOutLeft">
    <div v-show="store.registerWelcomeShow">
      <div style="margin-top: -50px;margin-left: -10px"><img src="../../assets/imgs/logo.png" style="width: 150px;height: 35px" alt=""/> </div>
      <div class="welcome1">欢迎注册安居租房</div>
      <div class="welcome2" style="margin-left: 50px">祝您早日找到心仪的住所</div>
    </div>
  </transition>

  <transition
      appear
      enter-active-class="animate__animated animate__bounceInRight"
      leave-active-class="animate__animated animate__bounceOutRight">
    <div class="loginBody" v-show="store.registerShow">
      <div style="margin-left: 340px;padding-top: 20px;cursor: pointer;font-size: 18px">
        <el-icon @click="closeRegister"><close/></el-icon>
      </div>
      <el-steps :active="step" finish-status="success" class="step" align-center>
        <el-step title="基本信息" />
        <el-step title="验证消息" />
        <el-step title="设置密码" />
      </el-steps>
      <div v-if="step===0">
        <div class="group" style="margin-top: 20px">
          <input v-model="registerForm.username" class="input short" placeholder="请输入用户名">
        </div>
        <div class="group" style="margin-top: 20px">
          <input v-model="registerForm.nickname" class="input short" placeholder="请输入昵称">
        </div>
        <div class="group" style="margin-top: 20px">
          <input v-model="registerForm.realName" class="input short" placeholder="请输入真实姓名">
        </div>
        <div class="gender">
          <label>请选择性别：</label>
          <el-radio-group v-model="registerForm.gender">
            <el-radio label="1">男</el-radio>
            <el-radio label="2">女</el-radio>
          </el-radio-group>
        </div>
        <button class="button" @click="next('basic')" style="width:200px;margin-top: 20px">下一步</button>
        <button class="button" @click="handleLogin()" style="width:200px;margin-top: 10px">已有账号？去登录</button>
      </div>
      <div  v-if="step===1">
        <!-- 验证信息表单 -->
        <el-form>
          <el-form-item>
            <div class="group" style="margin-top: 20px;">
              <input v-model="registerForm.email" class="input short" placeholder="请输入邮箱">
            </div>
          </el-form-item>
          <el-form-item>
            <div class="group">
              <input v-model="registerForm.mobile" type="number" class="input short" placeholder="请输入手机号">
            </div>
          </el-form-item>
          <el-form-item>
            <div class="group">
              <input v-model="code" class="input short" placeholder="请输入验证码" style="width: 120px">
              <img :src="captchaImage" @click="refreshCaptcha" style="width: 200px;height: 60px;margin-left:5px;border-radius: 5px;cursor: pointer" alt=""/>
            </div>
          </el-form-item>
        </el-form>
        <button class="button" @click="next('verify')" style="width:200px;">下一步</button>
        <button class="button" @click="back" style="width:200px;">上一步</button>
      </div>
      <div v-if="step===2">
        <div class="group" style="margin-top: 20px">
          <input type="password" v-model="registerForm.password" class="input short" placeholder="请输入密码">
        </div>
        <div class="group" style="margin-top: 20px">
          <input type="password" v-model="checkPass" class="input short" placeholder="请再次输入密码">
        </div>
        <button class="button" @click="back" style="width:200px;">上一步</button>
        <button class="button" @click="register" style="width:200px;margin-top: 10px">完成注册</button>
      </div>


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
const store = loginStore()
const step = ref(0)
let registerForm = ref({
  username : '',
  password : '',
  nickname:'',
  gender: null,
  realName : '',
  email : '',
  mobile : '',
  role: 'USER',
})
const handleLogin = () => {
  store.registerShow = false
  store.registerWelcomeShow = false
  step.value = 0
  registerForm.value = {
    username : '',
    password : '',
    nickname:'',
    gender: null,
    realName : '',
    email : '',
    mobile : '',
    role: 'USER',
  }
  setTimeout(()=>{
    store.loginWelcomeShow = true
    store.loginShow = true
  },800)
  store.loginAndRegisterVisible = true
}

const closeRegister = () => {
  store.registerShow = false
  store.registerWelcomeShow = false
  store.loginAndRegisterVisible = false
  step.value = 0
  registerForm.value = {
    username : '',
    password : '',
    nickname:'',
    gender: null,
    realName : '',
    email : '',
    mobile : '',
    role: 'USER',
  }
}
const code = ref('')
const trueCode = ref('')
let captchaImage = ref(import.meta.env.VITE_APP_BASE_URL + '/api/code')
const refreshCaptcha = async () => {
  const response = await axios.get(import.meta.env.VITE_APP_BASE_URL + '/api/code', {responseType: 'blob'});
  const blob = new Blob([response.data], {type: 'image/jpeg'});
  captchaImage.value = URL.createObjectURL(blob);
}
const next = (type) => {
  if (type === 'basic'){
    if (registerForm.username === ''){
      ElMessage.error("用户名不能为空")
    }else if (registerForm.nickname === ''){
      ElMessage.error("昵称不能为空")
    }else if (registerForm.realName === ''){
      ElMessage.error("真实姓名不能为空")
    }else if (registerForm.gender === null){
      ElMessage.error("请选择性别")
    } else {
      step.value = step.value + 1
    }
  } else if (type === 'verify') {
    if (registerForm.value.email === ''){
      ElMessage.error("邮箱不能为空")
    }else if(!registerForm.value.email.match(/^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/)) {
      ElMessage.error("请输入正确的邮箱")
    } else if (registerForm.value.mobile === ''){
      ElMessage.error("手机号不能为空")
    }else if (!registerForm.value.mobile.toString().match(/^1[34578]\d{9}$/)){
      ElMessage.error("请输入正确的手机号")
    }else if (code.value === ''){
      ElMessage.error("验证码不能为空")
    }else if (code.value !== ''){
      request.get("/getCode").then(res => {
        if (res.code === '200') {
          trueCode.value = res.data
          if (code.value !== trueCode.value) {
            ElMessage({
              message: '验证码输入错误',
              type: 'error',
              duration: 1500,
              onClose : () => {
                refreshCaptcha()
              }
            })
          }else {
            step.value = step.value + 1
          }
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
    }else {
      step.value = step.value + 1
    }
  }

}
const back = () => {
  step.value = step.value - 1
}
const checkPass = ref('')
const register = () => {
  if (registerForm.value.password === ''){
    ElMessage.error("请输入密码")
  } else if (checkPass.value === ''){
    ElMessage.error("请再次输入密码")
  }else if (registerForm.value.password !== checkPass.value){
    ElMessage.error("两次密码输入不一致")
  } else {
    request.post("/user/register",registerForm.value).then(res => {
      if (res.code === '200') {
        ElMessage({
          message: '注册成功，即将转到登录界面',
          type: 'success',
          duration: 1500,
          onClose : () => {
            handleLogin()
          }
        })
      }else {
        ElMessage.error(res.msg)
      }
    })
  }

}
</script>

<style scoped>
@import "../../assets/css/loginAndRegister.css";
.step{
  margin-top: 20px;
  margin-left: 20px;
  font-size: 12px;
}
.short{
  height: 32px;
  margin-left: 10px;
  font-size: 13px;
}
.gender{
  margin-top: 20px;
  margin-left:50px;
  font-family: PingFangMedium,sans-serif;
}
.avatarFont{
  margin-left: 40px;
  font-family: PingFangMedium,sans-serif;
}
::v-deep input::-webkit-outer-spin-button,
::v-deep input::-webkit-inner-spin-button {
  -webkit-appearance: none !important;
}
::v-deep input[type="number"] {
  -moz-appearance: textfield; /* 此处写不写都可以 */
}
</style>