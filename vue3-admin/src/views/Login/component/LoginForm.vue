<template>
  <transition
      appear
      enter-active-class="animate__animated animate__bounceInRight"
      leave-active-class="animate__animated animate__bounceOutRight">
  <div style="font-family: 'PingFang SC',sans-serif">
    <el-form>
      <div style="display:flex;align-items:center;justify-content:center;margin-top: 100px;">
        <span style="font-size: 2.25rem;line-height: 2rem;font-weight: bold;font-family: '默陌老屋手迹',sans-serif">登录</span>
      </div>
      <el-form-item class="formItem" style="margin-top: 50px;font-size: 20px" prop="username">
        <div style="width: 100%">
          <label style="font-size: 18px">用户名</label>
        </div>
        <el-input class="input" v-model="loginForm.username" clearable/>
      </el-form-item>
      <el-form-item class="formItem" style="margin-top: 20px;" prop="loginPassword">
        <div style="width: 100%">
          <label style="font-size: 18px">密码</label>
        </div>
        <el-input class="input" type="password" v-model="loginForm.password" show-password clearable/>
      </el-form-item>
      <el-form-item class="formItem" style="margin-top: 20px;" prop="code">
        <el-row>
          <el-col :span="16">
            <label style="font-size: 18px">验证码</label><br/>
            <el-input v-model="loginForm.code" clearable style="width: 250px;height: 40px"/>
          </el-col>
          <el-col :span="8">
            <div style="margin-top: 20px;margin-left: -20px">
              <img :src="captchaImage" @click="refreshCaptcha" style="cursor: pointer;" alt="">
            </div>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item>
        <el-row style="display: flex;justify-content: flex-end;margin-right: 100px">
            <div style="margin-left: 550px">
              <el-link type="primary" :underline="false" @click="findBackPassword = true">
                <span style="font-size: 17px">忘记密码</span>
              </el-link>
            </div>
        </el-row>
      </el-form-item>
      <el-form-item class="formItem">
        <el-button type="primary" :loading="loading" class="button" @click="login" >登录</el-button><br/>
        <el-button plain class="button" @click="toRegister">房东注册</el-button>
      </el-form-item>
    </el-form>

    <el-dialog title="找回密码" v-model="findBackPassword" width="700px">
      <el-form label-position="right">
        <el-form-item label="用户名：" class="formItem">
          <el-input style="width: 200px" v-model="findBackPasswordForm.username" clearable/>
        </el-form-item>
        <el-form-item label="邮箱：" class="formItem">
          <el-input style="width: 200px" v-model="findBackPasswordForm.email" clearable/>
          <el-button style="margin-left: 20px" type="primary" @click="sendEmail">发送验证码</el-button>
        </el-form-item>
        <el-collapse-transition>
          <el-form-item label="验证码：" prop="captcha" class="formItem" >
            <el-input style="width: 200px" v-model="findBackPasswordForm.code" clearable/>
          </el-form-item>
        </el-collapse-transition>
        <el-form-item label="新密码：" prop="password" class="formItem">
          <el-input style="width: 200px" v-model="findBackPasswordForm.password" show-password clearable password/>
        </el-form-item>
        <el-form-item label="确认密码：" prop="checkPass" class="formItem">
          <el-input style="width: 200px;" v-model="findBackPasswordForm.checkPass" show-password clearable password/>
        </el-form-item>
      </el-form>
      <el-button type="primary" style="margin-left: 400px" @click="findBack">找 回</el-button>
      <el-button @click="closeForm" >取消</el-button>
    </el-dialog>
  </div>
  </transition>
</template>

<script setup>
import request from "../../../utils/request";
import axios from "axios";
import {ElMessage} from "element-plus";
import {onMounted, reactive, ref} from "vue";
import {useRouter} from "vue-router";
import {setMenu} from "../../../router/index.js";
import {dataStore} from "../../../stores/dataStore.js";

const loginForm = reactive({
  username : '',
  password : '',
  code: '',
})
const loading = ref(false)

const captchaImage = ref('')

const router = useRouter()
const toRegister = () => {
  router.push("/user/register")
}

const refreshCaptcha = async () => {
  loginForm.code = ''
  const response = await axios.get(import.meta.env.VITE_APP_BASE_URL + '/api/code', {responseType: 'blob'});
  const blob = new Blob([response.data], {type: 'image/jpeg'});
  captchaImage.value = URL.createObjectURL(blob);
}

onMounted(() => {
  refreshCaptcha()
  window.addEventListener('keydown', keyDown)
})

const findBackPassword = ref(false)
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
          data.successMsg(res,"密码找回成功，请登录")
          setTimeout(()=>{closeForm()},1000)
        }else {
          data.errorMsg(res,router)
        }
      })
    }
  }
}

const closeForm = () => {
  findBackPasswordForm.username = ''
  findBackPasswordForm.email = ''
  findBackPasswordForm.code = ''
  findBackPasswordForm.password = ''
  findBackPasswordForm.checkPass = ''
  findBackPassword.value = false
}

const login = () => {
  loading.value = true
  if (loginForm.username === ''){
    ElMessage.error("请输入用户名")
    loading.value = false
  }else if (loginForm.password === '') {
    ElMessage.error("请输入密码")
    loading.value = false
  }else if (loginForm.code === '') {
    ElMessage.error("请输入验证码")
    loading.value = false
  } else if (loginForm.code !== ''){
    request.get("/getCode").then(res => {
      if (res.code === '200') {
        let trueCode = res.data
        if (loginForm.code === trueCode) {
          request.post("/user/login",loginForm).then(result => {
            if (result.code === '200') {
              ElMessage({
                message: "登录成功，即将跳转到首页",
                type: "success",
                showClose: false,
                duration: 2000,
                onClose: () => {
                  localStorage.setItem("user", JSON.stringify(result.data));
                  localStorage.setItem("menus", JSON.stringify(result.data.menu));
                  localStorage.setItem("allMenus", JSON.stringify(result.data.allMenus));
                  setMenu()
                  router.push("/")
                }
              });
            } else {
              ElMessage({
                message: result.msg,
                type: "warning",
                showClose: false,
                duration: 2000,
                onClose: () => {
                  refreshCaptcha()
                  loading.value = false
                }
              });
            }
          })
        } else {
          ElMessage({
            message: "验证码错误",
            type: "warning",
            showClose: false,
            duration: 2000,
            onClose: () => {
              refreshCaptcha()
              loading.value = false
            }
          });
        }
      }else {
        ElMessage({
          message: res.msg,
          type: "error",
          showClose: false,
          duration: 2000,
          onClose: () => {
            refreshCaptcha()
            loading.value = false
          }
        });
      }
    })
  }
}

// 监听回车键执行事件
 const keyDown = (e) => {
  if (e.keyCode === 13) {
    login()
  }
}
</script>

<style scoped>
  .formItem{
    display: flex;
    align-content: center;
    margin-left: 120px;
  }
  .input{
    height: 40px;
    width: 500px;
  }
  .button{
    margin-top: 20px;
    width: 500px;
    height: 40px;
    font-size: 16px;
    border-radius: 10px
  }
</style>