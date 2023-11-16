<template>
  <div id="app">
    <el-container class="layout">
       <el-header class="header">
         <div style="margin-left: 240px;">
           <router-link to="/home">
             <img style="height: 35px;width: 35px;" src="../assets/imgs/zufang.png" alt="">
             <img style="height: 35px;width: 150px;margin-left: 5px" src="../assets/imgs/logo.png" alt="">
           </router-link>
         </div>
         <div style="margin-left: 300px;font-family: PingFangMedium,serif;width: 500px">
           <el-menu router :default-active="$route.path" mode="horizontal" style="">
             <el-menu-item style="font-size: 15px" index="/home">首页</el-menu-item>
             <el-menu-item style="font-size: 15px" index="/findHouse">找房</el-menu-item>
             <el-menu-item style="font-size: 15px" index="/myReserve" v-if="Object.keys(user).length !== 0">我的预约</el-menu-item>
             <el-sub-menu index="5" v-if="Object.keys(user).length !== 0">
               <template #title><span style="font-size: 15px">我的租赁</span></template>
               <el-menu-item style="font-size: 15px;font-family: 'PingFang SC',sans-serif" index="/myRent/myRentApply">我的租赁申请</el-menu-item>
               <el-menu-item style="font-size: 15px;font-family: 'PingFang SC',sans-serif" index="/myRent/myOrder">我的订单</el-menu-item>
               <el-menu-item style="font-size: 15px;font-family: 'PingFang SC',sans-serif" index="/myRent/myRent">租赁管理</el-menu-item>
               <el-menu-item style="font-size: 15px;font-family: 'PingFang SC',sans-serif" index="/myRent/myRepair">报修管理</el-menu-item>
             </el-sub-menu>
           </el-menu>
         </div>
         <div style="margin-top: 5px;margin-left: 80px" v-if="Object.keys(user).length !== 0">
           <el-badge :value="unReadAmount" :hidden="unReadAmount<=0" style="margin-top: 15px">
             <div style="height: 33px;width: 33px" >
               <i class="el-icon-message" style="font-size: 22px;cursor: pointer;margin-left: 5px" @click="showMessage"/>
             </div>
           </el-badge>
         </div>
         <div style="margin-left: 30px">
           <el-dropdown placement="bottom" v-if="Object.keys(user).length !== 0">
             <div style="display: flex; align-items: center;">
               <img :src="baseUrl + '/api/files/'+user.avatar" alt="" style="width: 40px; height: 40px; margin: 0 5px;border-radius: 50%" v-if="user.avatar !== ''">
               <el-avatar :src="'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" style="width: 40px; height: 40px; margin: 0 5px;" v-else/>
               <span>{{user.nickname}}</span>
             </div>
             <template #dropdown>
               <el-dropdown-menu slot="dropdown">
                 <el-dropdown-item @click="detailVisible = true">个人信息</el-dropdown-item>
                 <el-dropdown-item @click="editPasswordVisible = true">修改密码</el-dropdown-item>
                 <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
               </el-dropdown-menu>
             </template>
           </el-dropdown>
           <div v-else>
             <el-link class="button1" :underline="false" @click="handleLogin">登录</el-link>
             <el-link style="margin-left: 20px" class="button1" :underline="false" @click="handleRegister">注册</el-link>
           </div>
           <el-dialog
               v-model="store.loginAndRegisterVisible"
               :close-on-click-modal="false"
               :show-close="false"
               style="width: 700px;height: 500px;background:linear-gradient(to bottom,skyblue 20%,lightskyblue 60%);border-radius: 12px">
             <login/>
             <register/>
           </el-dialog>
         </div>
       </el-header>
      <el-main style="margin-top: 62px;width: 100%;overflow: clip">
          <router-view/>
      </el-main>
      <el-footer>
        <div class="footer" :style="margin()">
          <el-row style="height: 150px">
            <el-col :span="8">
              <div>
                <div class="footerTitle1 footerFont1">安居租房·海量真实房源</div>
                <div class="footerTitle2 footerFont2">海量房源任你选，找房更快更方便</div>
                <div style="padding-top: 10px" class="footerTitle2 footerFont2">
                  让您所到之处都能找到归属，是我最开心的事
                  <el-tag effect="dark" style="margin-left: 10px">长租</el-tag>
                  <el-tag effect="dark" style="margin-left: 10px" color="#38C77DFF">短租</el-tag>
                </div>
              </div>
            </el-col>
            <el-col :span="16">
              <div style="margin-left: 48%;margin-top: 35px">
                <img src="../assets/imgs/zufang.png" style="width: 84px;height: 84px" alt=""/>
                <img src="../assets/imgs/logo.png" alt=""/>
              </div>
            </el-col>
          </el-row>
          <el-divider/>
          <div class="copyright">
            ©2023·安居租房 版权所有
          </div>
        </div>
      </el-footer>
      <el-backtop :right="100" :bottom="100" >
        <el-icon><ArrowUp /></el-icon>
      </el-backtop>
    </el-container>
  </div>

  <el-dialog title="用户信息" v-model="detailVisible" width="600px" :close-on-click-modal="false" align-center>
    <el-row>
      <el-col :span="8">
        <div v-if="user.avatar !== ''">
          <img :src="baseUrl + '/api/files/'+user.avatar" style="width: 100px;height: 90px;margin-left: 60px;border-radius: 50%" alt="">
        </div>
        <div v-else>
          <el-avatar :src="'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" style="width: 100px;height: 90px;margin-left: 60px;"/>
        </div>
      </el-col>
      <el-col :span="16">
        <el-form label-position="left" style="margin-left:50px">
          <el-form-item label="用户名:" class="formItem">
            <span>{{user.username}}</span>
          </el-form-item>
          <el-form-item label="昵称:" class="formItem">
            <span>{{user.nickname}}</span>
          </el-form-item>
          <el-form-item label="性别:" class="formItem">
            <span>{{user.gender === 1 ? '男' : '女'}}</span>
          </el-form-item>
          <el-form-item label="手机号码:" class="formItem">
            <span>{{user.mobile}}</span>
          </el-form-item>
          <el-form-item label="邮箱:" class="formItem">
            <span>{{user.email}}</span>
          </el-form-item>
          <el-form-item label="创建时间:" class="formItem">
            <span>{{user.createTime}}</span>
          </el-form-item>
          <el-form-item label="修改时间:" class="formItem">
            <span>{{user.updateTime !== null ? user.updateTime : '暂未修改'}}</span>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
    <template #footer>
      <el-button type="primary" @click="edit">修改</el-button>
    </template>
  </el-dialog>

  <el-dialog title="修改用户信息" v-model="editVisible" :close-on-click-modal="false" width="800px" align-center>
    <el-form label-position="left" :model="editForm">
      <el-row>
        <el-col :span="12">
          <el-form-item label="用户名:" class="formItem" prop="username">
            <el-input style="width: 200px" v-model="editForm.username" disabled/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="昵称:" class="formItem" prop="nickname">
            <el-input style="width: 200px"  v-model="editForm.nickname"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="姓名:" class="formItem" prop="realName">
            <el-input style="width: 200px"  v-model="editForm.realName" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="性别:" class="formItem" prop="gender">
            <el-radio-group v-model="editForm.gender">
              <el-radio label="1">男</el-radio>
              <el-radio label="2">女</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="手机号码:" class="formItem" prop="mobile">
            <el-input style="width: 200px"  v-model="editForm.mobile" ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="邮箱:" class="formItem" prop="email">
            <el-input style="width: 200px"  v-model="editForm.email" ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="10">
          <el-switch
              v-model="isUpdateAvatar"
              class="mb-2"
              active-text="修改头像"
              inactive-text="不修改头像"
          />
        </el-col>
        <el-col :span="14">
          <el-form-item class="formItem" label="头像上传" v-if="isUpdateAvatar">
            <el-upload
                style="margin-left: 20px"
                class="upload-demo"
                :action="baseUrl + '/api/upload'"
                :on-success="handleSuccess"
                :file-list="fileList"
                :before-upload="beforeUpload"
                :limit="1"
                list-type="picture">
              <el-button size="small" type="primary">点击上传</el-button>
              <template #tip>
                <div class="el-upload__tip">只能上传jpg/png文件，且不超过1MB</div>
              </template>
            </el-upload>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="editVisible = false">取 消</el-button>
      <el-button type="primary" @click="update">确 定</el-button>
    </template>
  </el-dialog>

  <el-dialog title="修改密码" v-model="editPasswordVisible" :close-on-click-modal="false" :before-close="closeForm" width="400px" align-center>
    <el-form label-position="left" :model="editPasswordForm" style="margin-left: 50px">
      <el-form-item label="旧密码">
        <el-input style="width: 200px" v-model="editPasswordForm.oldPassword" show-password clearable password/>
      </el-form-item>
      <el-form-item label="新密码">
        <el-input style="width: 200px" v-model="editPasswordForm.newPassword" show-password clearable password/>
      </el-form-item>
      <el-form-item label="确认密码">
        <el-input style="width: 200px" v-model="editPasswordForm.checkPass" show-password clearable password/>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="closeForm">取 消</el-button>
      <el-button type="primary" @click="updatePassword">确 定</el-button>
    </template>
  </el-dialog>

  <el-drawer
      v-model="messageVisible"
      title="消息列表"
      direction="rtl"
      :close-on-click-modal="false"
      :before-close="handleClose"
      style="font-family: 'PingFang SC', sans-serif;">
    <div>
      <el-button round style="margin-left: 75%;margin-top: -20px" @click="readAll" :disabled="ifUnRead(messages)"><span class="button-text">一键已读</span></el-button>
    </div>
    <div>
      <div v-for="message in messages" :key="message.id">
        <el-row>
          <el-col :span="17"><h3>{{message.title}}</h3></el-col>
          <el-col :span="7" style="margin-top: 18px;text-align: right;">{{message.createTime}}</el-col>
        </el-row>
        <el-row>
          <el-col :span="22">{{message.content}}</el-col>
          <el-col :span="2">
            <div style="margin-left: 10px">
              <span style="color: #dc6565;font-size: 10px" v-if="message.readStatus===0">未读</span>
              <span style="color: #777;font-size: 10px" v-else>已读</span>
            </div>
          </el-col>
        </el-row>
        <el-row style="margin-top: 10px">
          <el-button style="height: 25px;margin-left: 76%" v-if="message.readStatus===0" @click="read(message.id)"><span class="button-text">已读</span></el-button>
        </el-row>
      </div>
    </div>
  </el-drawer>

</template>


<script setup>
import {ArrowUp, Close} from "@element-plus/icons-vue";
import {onMounted, reactive, ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import {ElMessage, ElMessageBox, ElNotification} from "element-plus";
import {loginStore} from "../stores/loginStore.js";
import Login from "./components/login.vue";
import Register from "./components/register.vue";
import request from "../utils/request.js";
import {dataStore} from "../stores/dataStore.js";

const baseUrl = ref(import.meta.env.VITE_APP_BASE_URL)

const router = useRouter()
const route = useRoute()

const store = loginStore()
let user = reactive(localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {})

const handleLogin = () => {
  store.registerShow = false
  store.registerWelcomeShow = false
  store.loginWelcomeShow = true
  store.loginShow = true
  store.loginAndRegisterVisible = true
}
const handleRegister = () => {
  store.loginShow = false
  store.loginWelcomeShow = false
  store.registerWelcomeShow = true
  store.registerShow = true
  store.loginAndRegisterVisible = true
}

const detailVisible = ref(false)

const editVisible = ref(false)
const editForm = ref({
  id: '',
  username : '',
  nickname : '',
  realName : '',
  avatar : '',
  gender : '',
  mobile : '',
  email : '',
})

const edit = () => {
  editForm.value = {
    id: user.id,
    username : user.username,
    nickname : user.nickname,
    realName : user.realName,
    avatar : user.avatar,
    gender : user.gender.toString(),
    mobile : user.mobile,
    email : user.email,
  }
  editVisible.value = true
}

const isUpdateAvatar = ref(false)
let fileList = ref([])
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
  editForm.value.avatar = res.data;
}

const store2 = dataStore()
const update = () => {
  if (editForm.value.username === ''){
    ElMessage.error("请输入用户名")
  }else if (editForm.value.nickname === ''){
    ElMessage.error("请输入昵称")
  }else if (editForm.value.gender === ''){
    ElMessage.error("请选择性别")
  }else if (editForm.value.realName === ''){
    ElMessage.error("请输入真实姓名")
  }else if (editForm.value.email === ''){
    ElMessage.error("邮箱不能为空")
  }else if(!editForm.value.email.match(/^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/)) {
    ElMessage.error("请输入正确的邮箱")
  } else if (editForm.value.mobile === ''){
    ElMessage.error("手机号不能为空")
  }else if (!editForm.value.mobile.match(/^1[34578]\d{9}$/)){
    ElMessage.error("请输入正确的手机号")
  }else {
    request.put("/user/update",editForm.value).then(res => {
      if (res.code === '200') {
        store2.returnMsg(res,'修改用户信息成功，请重新登录',router)
        setTimeout(() => {
          localStorage.removeItem("user")
          user = JSON.parse(localStorage.getItem("user")) ? JSON.parse(localStorage.getItem("user")) : {}
          if (route.path !== '/home') {
            router.push('/home')
          } else {
            router.go(0)
          }
        },1000)
      } else {
        store2.errorMsg(res,router)
      }
    })
  }
}

const editPasswordVisible = ref(false)
const editPasswordForm = ref({
  oldPassword: '',
  newPassword: '',
  checkPass: ''
})

const closeForm = () => {
  editPasswordForm.value = {
    oldPassword: '',
    newPassword: '',
    checkPass: ''
  }
  editPasswordVisible.value = false
}

const updatePassword = () => {
  if (editPasswordForm.value.oldPassword === '') {
    ElMessage.error("请输入旧密码")
  } else if (editPasswordForm.value.newPassword === '') {
    ElMessage.error("请输入新密码")
  } else if (editPasswordForm.value.newPassword !== editPasswordForm.value.checkPass) {
    ElMessage.error("两次新密码输入不一致")
  } else {
    request.put('/user/updatePassword',editPasswordForm.value).then(res => {
      if (res.code === '200') {
        store2.returnMsg(res,'密码修改成功，请重新登录',router)
        setTimeout(() => {
          localStorage.removeItem("user")
          user = JSON.parse(localStorage.getItem("user")) ? JSON.parse(localStorage.getItem("user")) : {}
          if (route.path !== '/home') {
            router.push('/home')
          } else {
            router.go(0)
          }
        },1000)
      } else {
        store2.errorMsg(res,router)
      }
    })
  }
}

const unReadAmount = ref(0)
const getUnReadAmount = () => {
  request.get("/message/getUnReadAmount/"+user.id).then(res => {
    if (res.code === '200'){
      unReadAmount.value = res.data
      if (unReadAmount.value > 0){
        ElNotification({
          title: '提示',
          message: `您有${unReadAmount.value}条未读消息`,
          type: 'info',
          duration: 2000,
        })
      }
    }else {
      store2.errorMsg(res,router)
    }
  })
}

const messageVisible = ref(false)
const messages = ref([])
const showMessage = () => {
  request.get("/message/getMessage/"+user.id).then(res => {
    if (res.code === '200'){
      res.data.forEach(r => {
        r.createTime = classifyTime(r.createTime)
      })
      messages.value = res.data
      messageVisible.value = true
    }else {
      store2.errorMsg(res,router)
    }
  })
}

const classifyTime = (inputTime) => {
  // 将字符串时间转化为Date对象
  let time = new Date(inputTime);

  // 获取当前时间
  let now = new Date();

  // 获取今天0点的时间
  let todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate());

  // 获取昨天0点的时间
  let yesterdayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 1);

  let minute = time.getMinutes();
  if (time.getMinutes() < 10){
    minute = "0"+time.getMinutes()
  }
  // 判断时间是否在今天,如果是，返回时间
  if (time >= todayStart && time <= now) {
    return time.getHours() + ":" + minute;
  }
  // 判断时间是否在昨天，如果是，返回昨天+时间
  else if (time >= yesterdayStart && time < todayStart) {
    return "昨天 " + time.getHours() + ":" + minute;
  }
  // 判断是否在今年，如果是，返回日期
  else if (time.getFullYear() === now.getFullYear()){
    return (time.getMonth() + 1) + "月" + time.getDate() + "日";
  }else {
    //如果不在今年，返回年月日
    return time.getFullYear() + "年" + (time.getMonth() + 1) + "月" + time.getDate() + "日";
  }
}

const handleClose = () => {
  getUnReadAmount()
  messageVisible.value = false
}

const read = (id) => {
  ElMessageBox.confirm('确定要将该消息设置为已读吗', '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '我再想想',
    type: 'warning'
  }).then(()=>{
    request.put("/message/read/"+id).then(res => {
      if (res.code === '200'){
        store2.successMsg(res,"已读成功")
        setTimeout(()=>{showMessage()},1000)
      }else {
        store2.errorMsg(res,router)
      }
    })
  }).catch(()=>{})
}

const ifUnRead = (messages) => {
  let flag = true
  messages.forEach(message => {
    if (message.readStatus === 0){
      flag = false
    }
  })
  return flag
}

const readAll = () => {
  ElMessageBox.confirm('确定要将全部消息设置为已读吗', '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '我再想想',
    type: 'warning'
  }).then(()=>{
    request.put("/message/readAll").then(res => {
      if (res.code === '200'){
        store2.successMsg(res,"全部已读成功")
        setTimeout(()=>{showMessage()},1000)
      }else {
        store2.errorMsg(res,router)
      }
    })
  }).catch(()=>{})
}


const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗', '退出登录', {
    confirmButtonText: '确定',
    cancelButtonText: '我再想想',
    type: 'warning'   }).then(() => {
      localStorage.removeItem("user")
      user = JSON.parse(localStorage.getItem("user")) ? JSON.parse(localStorage.getItem("user")) : {}
      if (route.path !== '/home') {
        ElMessage.success({
          message: '退出登录成功，即将返回到首页',
          duration: 1500,
          onClose: () => {
           router.push('/home')
         }
        })
      } else {
        ElMessage.success({
          message: '退出登录成功',
          duration: 1500,
          onClose: () => {
            router.go(0)
          }
        })
      }
    }).catch()
}

const margin = () => {
  if (route.path === '/myReserve' || route.path === '/myRent/myRentApply') {
    return 'margin-top:100px'
  }else if (route.path === '/myRent/myOrder' || route.path === '/myRent/myRepair') {
    return 'margin-top:108px'
  }else if (route.path === '/myRent/myRent') {
    return 'margin-top:89px'
  }

}

onMounted(()=>{
  if (Object.keys(user).length !== 0){
    getUnReadAmount()
  }
})

</script>

<style scoped>
.header{
  height: 62px;
  position: fixed;
  top: 0;
  width: 100%; /* 确保宽度占满整个屏幕 */
  z-index: 1000; /* 设置 z-index 来控制元素的层叠顺序，根据需要调整 */
  background: #fff;
  border-bottom: 1px solid #ccc; /* 添加底边框 */
  display: flex;
  align-items: center;
  box-shadow: 0 2px 6px rgba(0,21,41,.35);
}
.button1{
  font-family: PingFangMedium,serif;
  color: #777777;
  font-size: 15px;
}
.el-card__body, .el-main,.el-footer {
  padding: 0;
}
:focus-visible {
  outline: none;
}
.footer{
  width: 100%;
  height: 250px;
  margin-top: 50px;
  background: #212221;
  color: #fff;
}
.footerTitle1{
  padding-left: 70px;
  padding-top: 20px;
}
.footerFont1{
  font-size: 38px;
  font-family: PingFang,sans-serif;
  font-weight: bold;
}
.footerTitle2{
  padding-top: 20px;
  padding-left: 70px;
}
.footerFont2{
  font-family: PingFangMedium,sans-serif;
  font-size: 14px;
}
.copyright{
  display: flex;
  align-content: center;
  justify-content: center;
  font-size: 15px;
  font-family: PingFangMedium,sans-serif
}

</style>