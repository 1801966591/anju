<template>
  <div class="dashboard-container">
    <el-row>
      <el-col :span="12">
        <el-card class="w-full" style="height: 300px;width: 760px;" shadow="hover">
          <el-row style="margin-top: 30px">
            <el-col :span="12">
              <el-image :src="baseUrl + '/api/files/'+user.avatar" style="width: 200px;height: 200px;border-radius: 10px;" fit="fill"/>
            </el-col>
            <el-col :span="12">
              <div style="margin:10px 0 0 -130px;font-size: 16px">
                <div>昵称：{{user.nickname}}</div>
                <div style="margin-top: 35px">角色：{{user.role === 'ADMIN' ? '系统管理员' : '房东'}}</div>
                <div style="margin-top: 35px">上次登录时间：{{lastTime}}</div>
                <div style="margin-top: 35px">{{greetings}}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="smallCard leftCard" shadow="hover">
          <div style="display: flex">
            <div style="flex: 1;margin-top: 15px;">
              <el-icon style="font-size: 45px;"><Clock style="color:#409EFF;"/></el-icon>
            </div>
            <div style="flex: 2;margin-left: 20px;margin-top: 5px">
              <span class="title">当前时间</span>
              <div style="margin-top: 10px;margin-left: 10px">
                <span style="font-size: 17px;font-weight: bold">{{nowTime}}</span>
              </div>
            </div>
          </div>
        </el-card>
        <el-card class="smallCard leftCard" shadow="hover">
          <div style="display: flex">
            <div style="flex: 1;margin-top: 15px;">
              <i class="el-icon-order" style="font-size: 45px;color:#409EFF;" v-if="user.role === 'LANDLORD'"/>
              <i class="el-icon-user" style="font-size: 50px;color:#409EFF;" v-else/>
            </div>
            <div style="flex: 2;margin-left: 20px;margin-top: 5px">
              <span class="title">
                <span v-if="user.role === 'LANDLORD'">订单数</span>
                <span v-else>用户数</span>
              </span>
              <div style="margin-top: 10px;margin-left: 10px">
                <span style="font-size: 17px;font-weight: bold">
                  <span v-if="user.role === 'LANDLORD'">{{order}} 笔</span>
                  <span v-else>{{userCount}} 条</span>
                </span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="smallCard rightCard" shadow="hover">
          <div style="display: flex">
            <div style="flex: 1;margin-top: 20px;">
              <el-icon style="font-size: 45px;margin-top: -5px"><Message style="color:#409EFF;"/></el-icon>
            </div>
            <div style="flex: 2;margin-left: 20px;margin-top: 5px">
              <span class="title">消息数</span>
              <div style="margin-top: 10px;margin-left: 10px">
                <span style="font-size: 17px;font-weight: bold">{{message}} 条</span>
              </div>
            </div>
          </div>
        </el-card>
        <el-card class="smallCard rightCard" v-if="user.role === 'LANDLORD'" shadow="hover">
          <div style="display: flex">
            <div style="flex: 1;margin-top: 15px;">
              <i class="el-icon-money" style="font-size: 45px;color:#409EFF;"/>
            </div>
            <div style="flex: 2;margin-left: 20px;margin-top: 5px">
              <span class="title">当前收入</span>
              <div style="margin-top: 10px;margin-left: 10px">
                <span style="font-size: 17px;font-weight: bold">{{money}} 元</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row style="margin-top: 30px">
      <el-col :span="12">
        <el-card style="width: 600px" shadow="hover">
          <div slot="header" class="clearfix">
            <span style="font-size: 18px;line-height: 2rem;font-weight: bold;">系统公告</span>
          </div>
          <el-collapse v-model="activeName" accordion>
            <el-collapse-item v-for="notice in notices" :key="notice.id" :title="notice.i+'.'+notice.title" :name="notice.id">
              {{notice.content}}
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card style="margin-left: 30px;width: 600px;height: 400px" shadow="hover">
          <div id="main" style="width: 600px;height:400px;" v-show="data.length > 0"></div>
          <div v-show="data.length === 0" style="display: flex;justify-content: center;margin-top: 30%;font-size: 18px">暂无正在租赁的房源</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {computed, onBeforeMount, onMounted, reactive, ref} from "vue";
import {Clock, Message, Money} from "@element-plus/icons-vue";
import request from "../../utils/request.js";
import {dataStore} from "../../stores/dataStore.js";
import {useRouter} from "vue-router";
import * as echarts from 'echarts';
const baseUrl = ref(import.meta.env.VITE_APP_BASE_URL)
const user = reactive(JSON.parse(localStorage.getItem("user")))
const greetings =  computed(() => {
  let date = new Date();
  const hours = date.getHours();
  if (hours >= 6 && hours < 8) {
    return "晨起披衣出草堂，轩窗已自喜微凉🌅！";
  } else if (hours >= 8 && hours < 12) {
    return "愿你的日程充满光明和活力。上午好🌞！";
  } else if (hours >= 12 && hours < 18) {
    return "午后时光静美如诗，愿心情如春花般绽放。下午好☕！";
  } else if (hours >= 18 && hours < 23) {
    return "海上升明月，天涯共此时。晚上好🌃！";
  } else if (hours >= 23 || (hours >= 0 && hours < 6)) {
    return "偷偷向银河要了一把碎星，只等你闭上眼睛撒入你的梦中，晚安🌛！";
  }else {
    return "未获取到时间"
  }
})

const nowTime = ref('')
const getTime = () => {
  setInterval(()=>{
    nowTime.value = new Date().toLocaleTimeString('chinese',{hour12:false})
  }, 1000);
}

const activeName = ref(1)
const notices = ref([])
const store = dataStore()
const router = useRouter()
const getNotice = () => {
  request.get("/notice/getFiveNotice").then(res => {
    if (res.code === '200'){
      activeName.value = res.data[0].id
      let i = 1
      res.data.forEach(notice => {
        notice.i = i
        notices.value.push(notice)
        i++
      })
    }else {
      store.errorMsg(res,router)
    }
  })
}

const lastTime = ref("")
const getLastLoginTime = () => {
  request.get("/log/getLastLoginTime").then(res => {
    if (res.code === '200'){
      lastTime.value = res.data.time
    }else {
      store.errorMsg(res,router)
    }
  })
}

const message = ref(0)
const getMessageCount = () => {
  request.get("/message/getTotal").then(res => {
    if (res.code === '200'){
      message.value = res.data
    }else {
      store.errorMsg(res,router)
    }
  })
}

const order = ref(0)
const getOrderCount = () => {
  request.get("/order/search",{
    params: {
      currentPage:1,pageSize:5
    }
  }).then(res => {
    if (res.code === '200'){
      order.value = res.data.total
    }else {
      store.errorMsg(res,router)
    }
  })
}

const money = ref(0)
const getOrderMoney = () => {
  request.get("/order/getMoney").then(res => {
    if (res.code === '200'){
      money.value = res.data
    }else {
      store.errorMsg(res,router)
    }
  })
}

const userCount = ref(0)
const getUserCount = () => {
  request.get("/user/getTotalExistAdmin").then(res => {
    if (res.code === '200') {
      userCount.value = res.data
    }else {
      store.errorMsg(res,router)
    }
  })
}

const data = ref([])
const getECharts = () => {
  const chartBox = echarts.init(document.getElementById("main"));

  let title = ''
  let name = ''

  if (user.role === 'ADMIN') {
    title = '注册用户分布'
    name = '角色名称'
  } else {
    title = '正在租赁的房源'
    name = '房源名称'
  }

  const option = {
    title: {
      text: title,
      left: 'center',
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)',
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      padding:[50,0,0,0],
    },
    series: [
      {
        name: name,
        type: 'pie',
        radius: '50%',
        center: ['50%', '50%'],
        data: data.value,
        itemStyle: {
          normal: {
            color: function(colors) {
              const colorList = [
                '#45C2E0', '#C1EBDD', '#FFC851','#5A5476','#1869A0','#FF9393'
              ];
              return colorList[colors.dataIndex]
            }
          }
        },
      },
    ],
  };

  if (user.role === 'ADMIN'){
    request.get("/userRole/getCount").then(res => {
      if (res.code === '200'){
        res.data.forEach(r => {
          data.value.push({name:r.name,value:r.value})
        })
        chartBox.setOption(option);
      }else{
        store.errorMsg(res,router)
      }

    })
  }else {
    request.get("/rent/getCount").then(res => {
      if (res.code === '200'){
        res.data.forEach(r => {
          data.value.push({name:r.name,value:r.value})
        })
        chartBox.setOption(option);
      }else{
        store.errorMsg(res,router)
      }

    })
  }

  // 根据页面大小自动响应图表大小
  window.addEventListener("resize", function () {
    chartBox.resize();
  });
}

onMounted(() => {
  getTime()
  request.get("/user/loginStatus").then(res => {
    if (res.code === '200'){
      getNotice()
      getLastLoginTime()
      getMessageCount()
      getOrderCount()
      getOrderMoney()
      getUserCount()
      getECharts()
    }else {
      store.errorMsg(res,router)
    }
  })
})

</script>

<style scoped>
  .smallCard{
    height: 120px;
    width: 240px;
    margin-top: 20px
  }
  .leftCard{
    margin-left: 140px;
  }
  .rightCard{
    margin-left: 80px;
  }
  .title{
    font-size: 23px;
    font-weight: bold;
    font-family: 'PingFang SC',sans-serif
  }
</style>