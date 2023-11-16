<template>
  <el-container>
    <el-aside :width="asideWidth" style="overflow:hidden">
      <div style="height: 60px;color:white;display: flex;align-items: center;justify-content: center;">
        <router-link to="/">
          <img style="width: 40px;height: 40px" src="../assets/imgs/rentHouse.png" alt="">
          <img v-show="!isCollapse" style="height: 40px;width: 150px;margin-left: 5px" src="../assets/imgs/logo.png" alt="">
        </router-link>
      </div>
      <el-menu
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          style="border: none"
          background-color="#001529"
          text-color="rgba(255,255,255,0.65)"
          active-text-color="#fff"
          :default-active="route.path">
        <div v-if="env==='development'">
          <div v-for="item in menus" :key="item.id">
            <div v-if="item.path">
              <el-menu-item :index="item.path">
                <i :style="!isCollapse ? 'margin-left:-9px' : 'margin-left:0;margin-right:0'" :class="item.icon"/>
                <span :style="!isCollapse ? '' : 'margin-left:25px'">{{ item.name }}</span>
              </el-menu-item>
            </div>
            <div v-if="!item.path">
              <el-sub-menu :index="item.id + 'a'" :collapse="isCollapse">
                <template #title>
                  <i :style="!isCollapse ? '' : 'margin-left: 7px'" :class="item.icon"/>
                  <span :style="!isCollapse ? 'margin-left: 9px' : 'margin-left: 21px'">{{ item.name }}</span>
                </template>
                <div v-for="sub in item.children" :key="sub.id">
                  <el-menu-item :index="sub.path">
                    <template #title>
                      <i :class="sub.icon"/>
                      <span>{{ sub.name }}</span>
                    </template>
                  </el-menu-item>
                </div>
              </el-sub-menu>
            </div>
          </div>
        </div>
        <div v-if="env==='production'">
          <el-menu-item index="/home">
            <i :style="!isCollapse ? 'margin-left:-9px' : 'margin-left:0;margin-right:0'" class="el-icon-home"/>
            <span :style="!isCollapse ? '' : 'margin-left:25px'">首页</span>
          </el-menu-item>
          <el-sub-menu index="1" :collapse="isCollapse" v-if="user.role === 'ADMIN'">
            <template #title>
              <i :style="!isCollapse ? '' : 'margin-left: 7px'" class="el-icon-system"/>
              <span :style="!isCollapse ? 'margin-left: 9px' : 'margin-left: 21px'">系统管理</span>
            </template>
            <el-menu-item index="/user/list">
              <template #title>
                <i class="el-icon-userInfo"/>
                <span>用户管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/role/list">
              <template #title>
                <i class="el-icon-role"/>
                <span>角色管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/dict/list">
              <template #title>
                <i class="el-icon-dict"/>
                <span>字典管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/menu/list">
              <template #title>
                <i class="el-icon-baseline-menu"/>
                <span>菜单管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/log/list">
              <template #title>
                <i class="el-icon-log"/>
                <span>操作日志管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/notice/list">
              <template #title>
                <i class="el-icon-notice"/>
                <span>公告管理</span>
              </template>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="2" :collapse="isCollapse">
            <template #title>
              <i :style="!isCollapse ? '' : 'margin-left: 7px'" class="el-icon-house-resource"/>
              <span :style="!isCollapse ? 'margin-left: 9px' : 'margin-left: 21px'">房源管理</span>
            </template>
            <el-menu-item index="/house/upload" v-if="user.role==='LANDLORD'">
              <template #title>
                <i class="el-icon-apply"/>
                <span>上传房源</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/house/myHouse" v-if="user.role==='LANDLORD'">
              <template #title>
                <i class="el-icon-my-apply"/>
                <span>我的房源</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/house/audit" v-if="user.role==='ADMIN'">
              <template #title>
                <i class="el-icon-house-audit"/>
                <span>审核房源</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/house/list" v-if="user.role==='ADMIN'">
              <template #title>
                <i class="el-icon-house"/>
                <span>房源列表</span>
              </template>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="3" :collapse="isCollapse" v-if="user.role === 'LANDLORD'">
            <template #title>
              <i :style="!isCollapse ? '' : 'margin-left: 7px'" class="el-icon-customer-service"/>
              <span :style="!isCollapse ? 'margin-left: 9px' : 'margin-left: 21px'">客户服务</span>
            </template>
            <el-menu-item index="/CustomerService/chatOnline">
              <template #title>
                <i class="el-icon-chat"/>
                <span>在线咨询</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/customerService/reserve">
              <template #title>
                <i class="el-icon-ic_reserve"/>
                <span>预约看房审核</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/customerService/rentAudit">
              <template #title>
                <i class="el-icon-audit"/>
                <span>租赁审核</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/customerService/order">
              <template #title>
                <i class="el-icon-order"/>
                <span>订单管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/customerService/rent">
              <template #title>
                <i class="el-icon-rent"/>
                <span>租赁管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/customerService/repair">
              <template #title>
                <i class="el-icon-repair-report"/>
                <span>报修管理</span>
              </template>
            </el-menu-item>
          </el-sub-menu>
        </div>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <el-icon style="font-size: 26px;cursor: pointer" @click="handleCollapse" v-if="!isCollapse"><Fold /></el-icon>
        <el-icon style="font-size: 26px;cursor: pointer" @click="handleCollapse" v-else><Expand /></el-icon>
        <el-breadcrumb :separator-icon="ArrowRight" style="margin-left: 20px;font-size: 18px">
          <el-breadcrumb-item v-if="route.path !== '/home'" :to="{ path: '/' }">首页</el-breadcrumb-item>
          <template v-for="(item, index) in breadList">
            <el-breadcrumb-item
                v-if="item.name"
                :key="index"
                :to="item.path">
              {{ item.meta.title }}</el-breadcrumb-item>
          </template>
        </el-breadcrumb>
        <div style="flex: 1;display: flex;align-items: center;justify-content: flex-end">
          <div v-if="route.path !== '/home'" style="margin-right: 100px"><i class="el-icon-time"/><span>当前时间：{{nowTime}}</span></div>
          <i class="el-icon-fullScreen" v-if="!isFullscreen" style="font-size: 26px;margin-right: 20px;" @click="handleFull"/>
          <div style="margin-right: 20px;margin-top: 5px">
            <el-badge :value="unReadAmount" :hidden="unReadAmount<=0">
              <i class="el-icon-message" style="font-size: 26px;cursor: pointer;" @click="showMessage"/>
            </el-badge>
          </div>
          <i class="el-icon-cancel-fullScreen" v-if="isFullscreen" style="font-size: 26px;margin-right: 20px;" @click="exitFullscreen"></i>
          <el-dropdown placement="bottom">
            <div style="display: flex; align-items: center;">
              <img :src="baseUrl + '/api/files/'+user.avatar" alt="" style="width: 40px; height: 40px; margin: 0 5px;border-radius: 5px" >
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
        </div>
      </el-header>
      <el-main id="app">
        <router-view/>
      </el-main>
    </el-container>
  </el-container>

  <el-dialog title="用户信息" v-model="detailVisible" width="600px" :close-on-click-modal="false" align-center>
    <el-row>
      <el-col :span="8">
        <div v-if="user.avatar !== ''">
          <img :src="baseUrl + '/api/files/'+user.avatar" style="width: 100px;height: 90px;margin-left: 60px;border-radius: 5px" alt="">
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
          <el-form-item label="姓名:" class="formItem">
            <span>{{user.realName}}</span>
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
          <el-form-item label="角色:" class="formItem">
            <span>{{user.role}}</span>
          </el-form-item>
          <el-form-item label="状态:" class="formItem">
            <span>{{user.status === 1 ? '正常' : '禁用'}}</span>
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

  <el-dialog title="修改用户信息" v-model="editVisible" :close-on-click-modal="false" width="600px" align-center>
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
      style="font-family: 仓耳渔阳体, sans-serif;">
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
import {ArrowRight, Expand, Fold, HomeFilled, Menu, User} from "@element-plus/icons-vue";
import {onMounted, reactive, ref, watchEffect} from "vue";
import {useRoute, useRouter} from "vue-router";
import {ElMessage, ElMessageBox, ElNotification} from "element-plus";
import {resetRoutes} from "../router/index.js";
import {dataStore} from "../stores/dataStore.js";
import request from "../utils/request.js";
const baseUrl = ref(import.meta.env.VITE_APP_BASE_URL)
const env = ref(import.meta.env.VITE_APP_ENV)
const asideWidth = ref('200px')
const isCollapse = ref(false)
const isFullscreen = ref(false)
let breadList = reactive([])
let user = reactive(JSON.parse(localStorage.getItem("user")) ? JSON.parse(localStorage.getItem("user")) : {})
const menus = reactive(JSON.parse(localStorage.getItem("menus")) ? JSON.parse(localStorage.getItem("menus")) : {})
const router = useRouter()

const handleCollapse = () => {
  isCollapse.value = !isCollapse.value
  asideWidth.value = isCollapse.value ? '64px' : '200px'
}

const nowTime = ref('')
const getTime = () => {
  setInterval(()=>{
    nowTime.value = new Date().toLocaleTimeString('chinese',{hour12:false})
  }, 1000);
}

const handleFull = () => {
  isFullscreen.value = !isFullscreen.value
  document.documentElement.requestFullscreen()
}

const exitFullscreen = () => {
  isFullscreen.value = !isFullscreen.value
  if (document.exitFullscreen) {
    document.exitFullscreen()
  } else if (document.msExitFullscreen) {
    document.msExitFullscreen()
  } else if (document.mozCancelFullScreen) {
    document.mozCancelFullScreen()
  } else if (document.webkitCancelFullScreen) {
    document.webkitCancelFullScreen()
  }
}

const route = useRoute()
const getMatched = () => {
  breadList = route.matched;
}

watchEffect(() => {
  getMatched();
});

getMatched();

const store = dataStore()
const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗', '退出登录', {
    confirmButtonText: '确定',
    cancelButtonText: '我再想想',
    type: 'warning'   }).then(() => {
    ElMessage.success({
      message: '退出登录成功',
      duration: 1500,
      onClose: () => {
        localStorage.removeItem("user")
        localStorage.removeItem("menus")
        localStorage.removeItem("allMenus")
        router.push("/user/login")
      }
    })
  }).catch()
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
        store.returnMsg(res,'修改用户信息成功，请重新登录',router)
        setTimeout(() => {
          localStorage.removeItem("user")
          localStorage.removeItem("menus")
          localStorage.removeItem("allMenus")
          router.push("/user/login")
        },1000)
      } else {
        store.errorMsg(res,router)
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
        store.returnMsg(res,'密码修改成功，请重新登录',router)
        setTimeout(() => {
          localStorage.removeItem("user")
          localStorage.removeItem("menus")
          localStorage.removeItem("allMenus")
          router.push("/user/login")
        },1000)
      } else {
        store.errorMsg(res,router)
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
      store.errorMsg(res,router)
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
      store.errorMsg(res,router)
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

  let minute = time.getMinutes()
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
        store.successMsg(res,"已读成功")
        setTimeout(()=>{showMessage()},1000)
      }else {
        store.errorMsg(res,router)
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
        store.successMsg(res,"全部已读成功")
        setTimeout(()=>{showMessage()},1000)
      }else {
        store.errorMsg(res,router)
      }
    })
  }).catch(()=>{})
}

onMounted(() => {
  getTime()
  getUnReadAmount()
})

</script>

<style scoped>
.el-menu--inline{
  background-color: #000c17 !important
}
.el-menu--inline .el-menu-item{
  background-color: #000c17 !important;
  padding-left: 49px !important;
}
.el-menu-item:hover, .el-sub-menu__title:hover{
  color: #fff !important;
}
.el-menu-item:hover i,.el-sub-menu__title:hover i{
  color: #fff !important;
}
.el-menu-item.is-active{
  width: calc(100% - 8px) !important;
  background-color: #1890ff !important;
  border-radius: 5px !important;
  margin-left: 4px !important;
}
.el-menu-item.is-active i ,.el-menu--collapse>.el-menu-item.is-active i {
  margin-left: -4px;
}
.el-menu-item{

  height: 40px !important;
  line-height: 40px !important;

}
.el-sub-menu__title{
  height: 40px !important;
  line-height: 40px !important;
}
.el-sub-menu .el-menu-item{
  min-width: 0 !important;
}
.el-menu--inline .el-menu-item.is-active{
  padding-left: 45px !important;
}
.el-aside{
  transition: width .3s;
  box-shadow: 2px 0 6px rgba(0,21,41,.35);
  height: 100vh;
  background-color: #001529;
  font-family: 仓耳渔阳体,sans-serif;
}
.logo-title{
  margin-left: 5px;
  font-size: 20px;
  transition: all .3s;
}
.el-header{
  box-shadow: 2px 0 6px rgba(0,21,41,.35);
  display: flex;
  align-items: center;
  font-family: 仓耳渔阳体,sans-serif;
}

#app{
  font-family: 仓耳渔阳体,sans-serif;
  height: calc(100vh - 62px);
}
.bold-text {
  font-weight: bold;
}
:focus {outline: 0;}

.isCollapse .el-icon svg {
  height: 0;
  width: 0;
}
.el-drawer__title{
  font-family: 仓耳渔阳体, sans-serif !important;
}
</style>