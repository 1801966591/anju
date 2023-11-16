<template>
  <transition
      appear
      enter-active-class="animate__animated animate__bounceInRight"
      leave-active-class="animate__animated animate__bounceOutRight">
  <div style="font-family: 'PingFang SC',sans-serif">
    <div style="display:flex;align-items:center;justify-content:center;margin-top: 50px">
      <span style="font-size: 2rem;line-height: 2rem;font-weight: bold;font-family: '默陌老屋手迹'">注册</span>
    </div>
    <el-steps :active="currentStep" align-center style="color:dodgerblue;margin-top: 20px;" finish-status="success">
      <el-step title="填写基本信息"></el-step>
      <el-step title="验证信息"></el-step>
      <el-step title="设置密码"></el-step>
    </el-steps>

    <div class="step-content" style="margin-top: 15px;">
      <div v-show="currentStep === 0">
        <!-- 填写基本信息表单 -->
        <el-form v-if="currentStep===0">
          <el-row>
            <el-col :span="12">
              <el-form-item class="formItem">
                <div style="width: 100%;"><label class="font">用户名</label></div>
                <el-input class="input" v-model="registerForm.username" clearable placeholder="请输入用户名"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item style="margin-left: 30px">
                <div style="width: 100%;"><label class="font">昵称</label></div>
                <el-input class="input" v-model="registerForm.nickname" clearable placeholder="请输入昵称"/>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item class="formItem">
                <div style="width: 100%;"><label class="font">请选择性别</label></div>
                <el-radio-group v-model="registerForm.gender">
                  <el-radio label="1">男</el-radio>
                  <el-radio label="2">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item style="margin-left: 30px">
                <div style="width: 100%;"><label class="font">真实姓名</label></div>
                <el-input class="input" v-model="registerForm.realName" clearable  placeholder="请输入真实姓名"/>
              </el-form-item>
            </el-col>
          </el-row>



          <el-form-item class="formItem">
            <div style="width: 100%;"><label class="font">头像上传</label></div>
            <el-upload
                class="upload-demo"
                :action="baseUrl + '/api/upload'"
                :on-success="handleSuccess"
                :file-list="fileList"
                :before-upload="beforeUpload"
                :limit="1"
                list-type="picture">
              <el-button size="small" type="primary">点击上传</el-button>
              <template #tip>
                <div class="el-upload__tip">
                  只能上传jpg/png文件，且不超过10MB
                </div>
              </template>
            </el-upload>
          </el-form-item>
        <el-button type="primary" class="button" @click="nextStep">下一步</el-button>
        <el-button plain @click="toLogin" class="button" style="margin-left: 130px;margin-top: 10px">已有账号？去登录</el-button>
        </el-form>
      </div>

      <div v-show="currentStep === 1">
        <!-- 验证信息表单 -->
        <el-form v-if="currentStep===1">
          <el-row>
            <el-col :span="12">
              <el-form-item class="formItem" style="margin-top: 40px">
                <div style="width: 100%"><label>邮箱</label></div>
                <el-input v-model="registerForm.email" class="input"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item class="formItem" style="margin-left: 30px;margin-top: 40px">
                <div style="width: 100%"><label>手机号码</label></div>
                <el-input v-model="registerForm.mobile" class="input"/>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item style="margin-top: 20px;margin-left: 180px">
            <el-row>
              <el-col :span="12">
                <div style="width: 100%"><label>验证码</label></div>
                <el-input  v-model="captcha" clearable style="width: 200px;height: 40px" placeholder="请输入验证码"/>
              </el-col>
              <el-col :span="12">
                <div style="margin-top: 30px;margin-left: 20px">
                  <img :src="captchaImage" @click="refreshCaptcha" style="cursor: pointer">
                </div>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
        <el-button type="primary" class="button" style="margin-top: 70px" @click="VerifyNextStep">下一步</el-button>
        <el-button plain class="button" style="margin-left: 130px;margin-top: 10px" @click="prevStep">上一步</el-button>
      </div>

      <div v-show="currentStep === 2">
        <!-- 设置密码表单 -->
        <el-form v-if="currentStep===2">
          <el-form-item style="margin-left: 250px">
            <div style="width: 100%"><label>密码</label></div>
            <el-input style="width: 300px;height: 40px" v-model="registerForm.password" show-password clearable password/>
          </el-form-item>
          <el-form-item style="margin-left: 250px">
            <div style="width: 100%"><label>确认密码</label></div>
            <el-input style="width: 300px;height: 40px" v-model="checkPass" show-password clearable password/>
          </el-form-item>
        </el-form>
        <el-button type="success" class="button" @click="register">完成注册</el-button>
        <el-button plain class="button" style="margin-left: 130px;margin-top: 10px" @click="prevStep">上一步</el-button>
      </div>
    </div>
  </div>
  </transition>
</template>

<script setup>
import axios from "axios";
import request from "../../../utils/request";
import {ElMessage} from "element-plus";
import {onMounted, reactive, ref} from "vue";
import {useRouter} from "vue-router";
const baseUrl = ref(import.meta.env.VITE_APP_BASE_URL)
const registerForm = reactive({
      username : '',
      password : '',
      nickname:'',
      gender: null,
      realName : '',
      email : '',
      mobile : '',
      avatar : '',
    })
const loading = ref(false)
const currentStep = ref(0)
const fileList = reactive([])
const captcha = ref('')
const captchaImage = ref('')
const checkPass = ref('')

const router = useRouter()
const toLogin = () => {
  router.push("/user/login");
}

const nextStep = () => {
  if (registerForm.username === ''){
    ElMessage.error("请输入用户名")
  }else if (registerForm.nickname === ''){
    ElMessage.error("请输入昵称")
  }else if (registerForm.gender === ''){
    ElMessage.error("请选择性别")
  }else if (registerForm.realName === ''){
    ElMessage.error("请输入真实姓名")
  }else {
    currentStep.value++
  }
}
const refreshCaptcha = async () => {
  const response = await axios.get(import.meta.env.VITE_APP_BASE_URL + '/api/code', {responseType: 'blob'});
  const blob = new Blob([response.data], {type: 'image/jpeg'});
  captchaImage.value = URL.createObjectURL(blob);
}
const VerifyNextStep = () => {
  if (registerForm.email === ''){
    ElMessage.error("邮箱不能为空")
  }else if(!registerForm.email.match(/^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/)) {
    ElMessage.error("请输入正确的邮箱")
  } else if (registerForm.mobile === ''){
    ElMessage.error("手机号不能为空")
  }else if (!registerForm.mobile.match(/^1[34578]\d{9}$/)){
    ElMessage.error("请输入正确的手机号")
  }else if (captcha.value === ''){
    ElMessage.error("验证码不能为空")
  }else if (captcha.value !== ''){
    request.get("/getCode").then(res => {
      if (res.code === '200') {
        const trueCode = res.data
        if (captcha.value !== trueCode) {
          ElMessage({
            message: '验证码输入错误',
            type: 'error',
            duration: 1500,
            onClose : () => {
              refreshCaptcha()
            }
          })
        }else {
          currentStep.value++
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
  }
}

const prevStep = () => {
  currentStep.value--;
}

const beforeUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
  if (!isJpgOrPng) {
    ElMessage.error('只能上传jpg/png文件');
  }
  const isLt10M = file.size / 1024 / 1024 < 10;
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB');
  }
  return isJpgOrPng && isLt10M;
}

const handleSuccess = (res) => {
  // 在这里处理上传成功的情况
  console.log("上传成功：", res.data);
  // 更新成功上传的图片路径和图片名
  registerForm.avatar = res.data;
}

const register = () => {
  loading.value = true
  if (registerForm.password === ''){
    ElMessage.error("请输入密码")
    loading.value = false
  } else if (checkPass === ''){
    ElMessage.error("请再次输入密码")
    loading.value = false
  }else if (registerForm.password !== checkPass.value){
    ElMessage.error("两次密码输入不一致")
    loading.value = false
  } else {
    request.post("/user/register",registerForm).then(res => {
      if (res.code === '200') {
        ElMessage({
          message: "注册成功，即将跳转到登录页面",
          type: "success",
          showClose: false,
          duration: 2000,
          onClose: () => {
            toLogin()
          }
        });
      }else{
        ElMessage({
          message: res.msg,
          type: "warning",
          showClose: false,
          duration: 1000,
          onClose: () => {
            loading.value = false
          }
        });
      }
    })
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.formItem{
  display: flex;
  align-content: center;
  margin-left: 180px;
}
.input{
  width: 200px;
  height: 40px;
}
.button{
  margin-top: 20px;
  margin-left: 130px;
  width: 500px;
  font-size: 16px;
  border-radius: 10px
}
</style>