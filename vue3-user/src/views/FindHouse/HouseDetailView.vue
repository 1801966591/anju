<template>
  <div style="margin-left: 110px;margin-top: 20px">
    <el-row>
      <el-col :span="12">
        <el-carousel height="562px" style="width: 794px;border-radius: 5px">
          <el-carousel-item v-for="(img,index) in house.imgs" :key="index">
            <el-image
                style="width: 794px;height: 562px"
                :src="baseUrl + '/api/files/'+img"
                fit="cover"/>
          </el-carousel-item>
        </el-carousel>
      </el-col>
      <el-col :span="12">
        <div class="left font1">
          <span v-if="house.rentType === 1">合租</span>
          <span v-if="house.rentType === 2">整租</span>
          <span v-if="house.rentType === 3">独栋</span>
          · {{house.name}} {{house.houseType}}
        </div>
        <div class="left font2">￥<span style="font-size: 32px;color: rgba(236,8,17,0.87)">{{house.rent}}</span>元/月</div>
        <div class="left" style="margin-top: 20px" v-if="house.tags.length > 0">
          <el-tag style="margin-left: 20px" type="info" v-for="(tag,index) in house.tags" :key="index">{{tag}}</el-tag>
        </div>
        <div class="left" style="width: 450px">
          <el-divider/>
        </div>
        <el-row style="width: 450px;margin-left: 160px;">
          <el-col :span="6">
            <div class="font3">
              <span v-if="house.toward === 1">东</span>
              <span v-if="house.toward === 2">南</span>
              <span v-if="house.toward === 3">西</span>
              <span v-if="house.toward === 4">北</span>
            </div>
            <div class="font4" style="margin-left: -5px">朝向</div>
          </el-col>
          <el-col :span="6">
            <div class="font3"><span style="font-weight: bold;font-family: PingFangMedium,sans-serif">{{house.houseArea}}</span>㎡</div>
            <div class="font4" style="margin-left: 5px">面积</div>
          </el-col>
          <el-col :span="6">
            <div class="font3">
              <span v-if="house.decoration === 1">毛坯</span>
              <span v-if="house.decoration === 2">简装</span>
              <span v-if="house.decoration === 3">精装</span>
            </div>
            <div class="font4" style="margin-left: 5px">装修</div>
          </el-col>
          <el-col :span="6">
            <div class="font3">
              <span v-if="house.viewStatus === 0">随时看房</span>
              <span v-else>{{house.startTime}}-{{house.endTime}}</span>
            </div>
            <div class="font4" style="margin-left: 5px">看房时间</div>
          </el-col>
        </el-row>
        <div class="left" style="width: 450px">
          <el-divider/>
        </div>
        <el-row class="left" style="width: 450px;">
          <el-col :span="4">
            <span class="font4">位置</span>
          </el-col>
          <el-col :span="20">
            <span class="font5">{{house.province}}·{{house.city}}·{{house.area}} {{house.address}}</span>
          </el-col>
        </el-row>
        <div class="left" style="width: 450px">
          <el-divider/>
        </div>
        <el-row class="left" style="width: 450px;">
          <el-col :span="4">
            <span class="font4">楼层</span>
          </el-col>
          <el-col :span="20">
              <span class="font5">
                <span v-if="house.rentType !== 3">{{house.floor}}/{{house.totalFloor}}层</span>
                 <span v-else>{{house.floor}}</span>
              </span>
          </el-col>
        </el-row>
        <div class="left" style="width: 450px">
          <el-divider/>
        </div>
        <el-row class="left" style="width: 450px;">
          <el-col :span="4">
            <span class="font4">电梯</span>
          </el-col>
          <el-col :span="20">
              <span class="font5">
                <span v-if="house.elevator === 0">无电梯</span>
                <span v-else>有电梯</span>
              </span>
          </el-col>
        </el-row>
        <div class="left" style="width: 450px">
          <el-divider/>
        </div>
        <div class="left" style="margin-top: 20px">
          <el-row>
            <el-col :span="8">
              <el-button type="success" class="button1 font6" @click="chatVisible = true">在线咨询</el-button>
            </el-col>
            <el-col :span="8">
              <el-tag plain class="button1 font6" v-if="house.reserveStatus === 0">已预约看房</el-tag>
              <el-button plain type="primary" class="button1 font6" @click="openReserve" v-else>预约看房</el-button>
            </el-col>
            <el-col :span="8">
              <el-button type="primary" class="button1 font6" @click="openRent">租赁房源</el-button>
            </el-col>
          </el-row>
        </div>
      </el-col>
    </el-row>
  </div>
  <div class="houseInfo1">
    <el-row style="margin-top: 60px">
      <el-col :span="16">
        <el-affix :offset="62" target=".houseInfo1">
          <el-menu
              style="margin-left: 110px;font-family: 'PingFang SC',serif"
              :default-active="activeName"
              mode="horizontal"
          >
            <el-menu-item index="description" style="font-size:18px;" @click="scrollToElement('description')">房源介绍</el-menu-item>
            <el-menu-item index="basic" style="font-size:18px;" @click="scrollToElement('basic')">基本信息</el-menu-item>
            <el-menu-item index="config" style="font-size:18px;" @click="scrollToElement('config')">房屋配置</el-menu-item>
            <el-menu-item index="map" style="font-size:18px;" @click="scrollToElement('map')">位置周边</el-menu-item>
          </el-menu>
        </el-affix>
        <div style="margin-top: 20px;margin-left: 110px;">
          <div class="houseInfo2" style="width: 824px">
            <div>
              <div class="font7" id="description">房源介绍</div>
              <div style="margin-top: 20px;font-family: 'PingFang SC',sans-serif;font-size: 16px">
                <span v-html="house.description"/>
              </div>
            </div>
            <div style="margin-top: 40px;">
              <div class="font7" id="basic">基本信息</div>
              <el-row style="margin-top: 20px;font-family: PingFangMedium,sans-serif;font-size: 16px">
                <el-col :span="8"><span style="color: #777777">户型</span> <span style="padding-left: 30px">{{house.houseType}}</span></el-col>
                <el-col :span="8"><span style="color: #777777">楼层</span> <span style="padding-left: 30px">
                <span v-if="house.rentType !== 3">{{house.floor}}/{{house.totalFloor}}层</span>
                 <span v-else>{{house.floor}}</span>
              </span></el-col>
                <el-col :span="8">
                  <span style="color: #777777">用水</span>
                  <span style="padding-left: 30px">
                    <span v-if="house.powerType === 0">未知</span>
                    <span v-if="house.powerType === 1">商水</span>
                    <span v-if="house.powerType === 2">民水</span>
                  </span>
                </el-col>
              </el-row>
              <el-row style="margin-top: 20px;font-family: PingFangMedium,sans-serif;font-size: 16px">
                <el-col :span="8">
                  <span style="color: #777777">用电</span>
                  <span style="padding-left: 30px">
                    <span v-if="house.powerType === 0">未知</span>
                    <span v-if="house.powerType === 1">商电</span>
                    <span v-if="house.powerType === 2">民电</span>
                  </span>
                </el-col>
                <el-col :span="8">
                  <span style="color: #777777">燃气</span>
                  <span style="padding-left: 30px">
                    <span v-if="house.gas === 0">无燃气</span>
                    <span v-else>有燃气</span>
                  </span>
                </el-col>
                <el-col :span="8">
                  <span style="color: #777777">供暖</span>
                  <span style="padding-left: 30px">
                    <span v-if="house.heatingType === 0">无供暖</span>
                    <span v-if="house.heatingType === 1">集中供暖</span>
                    <span v-if="house.heatingType === 2">地板辐射供暖</span>
                  </span>
                </el-col>
              </el-row>
              <el-row style="margin-top: 20px;font-family: PingFangMedium,sans-serif;font-size: 16px">
                <el-col :span="8">
                  <span style="color: #777777">电梯</span>
                  <span style="padding-left: 30px">
                    <span v-if="house.elevator === 0">无电梯</span>
                    <span v-else>有电梯</span>
                  </span>
                </el-col>
                <el-col :span="8">
                  <span style="color: #777777">入住性别要求</span>
                  <span style="padding-left: 30px">
                    <span v-if="house.gender === 0">不限</span>
                    <span v-if="house.gender === 1">男</span>
                    <span v-if="house.gender === 2">女</span>
                  </span>
                </el-col>
                <el-col :span="8">
                  <span style="color: #777777">维护时间</span>
                  <span style="padding-left: 30px">{{house.maintenanceTime}}</span>
                </el-col>
              </el-row>
              <el-row style="margin-top: 20px;font-family: PingFangMedium,sans-serif;font-size: 16px">
                <el-col :span="8" v-if="house.rentType === 1">
                  <span style="color: #777777">出租房间数</span>
                  <span style="padding-left: 30px">{{house.rentRoomNumber}}间</span>
                </el-col>
                <el-col :span="8"></el-col>
                <el-col :span="8"></el-col>
              </el-row>
            </div>
            <div style="margin-top: 40px;">
              <div class="font7" id="config">房屋配置</div>
              <el-row style="margin-top: 20px;margin-left: 30px" v-if="house.configs.length > 0">
                <el-col v-for="(config, index) in house.configs" :key="index" :span="3">
                  <div>
                    <i :class="config.value" style="font-size: 42px"/>
                  </div>
                  <div :style="config.name.length === 1 ? 'margin-left: 13px' : (config.name.length === 2 ? 'margin-left: 5px;' : '')"
                       style=" font-family: 'PingFang SC', sans-serif; margin-top: 5px; font-size: 16px">
                    {{ config.name }}
                  </div>
                  <el-row v-if="(index + 1) % 8 === 0" style="margin-top: 20px;margin-left: 30px"/>
                </el-col>
              </el-row>
              <div class="config" v-else>
                <span style="padding-top:25px">房屋配置管家正在上传中...</span>
              </div>
            </div>
          </div>
          <div style="margin-top: 100px;">
            <div class="font7" id="map">位置周边</div>
            <div id="container"></div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <el-affix :offset="82" target=".houseInfo2">
          <el-card class="author" style="margin-top: 20px">
            <el-row>
              <span class="font1" style="font-size: 20px">
                <span v-if="house.rentType === 1">合租</span>
                <span v-if="house.rentType === 2">整租</span>
                <span v-if="house.rentType === 3">独栋</span>
            · {{house.name}} {{house.houseType}}
              </span>
            </el-row>
            <div class="font2">￥<span style="font-size: 32px;color: rgba(236,8,17,0.87)">{{house.rent}}</span>元/月</div>
            <el-row>
              <el-col :span="6">
                <img :src="baseUrl + '/api/files/'+house.avatar" alt="" class="avatar"/>
              </el-col>
              <el-col :span="18">
                <el-row style="margin-top: 10px">
                  <span class="font8">{{house.author}}</span>
                  <el-icon @click="chatVisible = true" style="font-size: 16px;margin-top: 3px;margin-left: 20px;cursor: pointer"><ChatDotRound /></el-icon>
                </el-row>
                <el-row style="margin-top: 10px">
                  <span style="font-size: 15px;font-family: PingFangMedium,sans-serif">我清楚该房源特色，有问题欢迎咨询我</span>
                </el-row>
              </el-col>
            </el-row>
            <el-row>
              <el-tag plain class="button2 font6" v-if="house.reserveStatus === 0">已预约看房</el-tag>
              <el-button plain type="primary" class="button2 font6" @click="openReserve" v-else>预约看房</el-button>
            </el-row>
            <el-row>
              <el-button type="primary" class="button2 font6" @click="openRent">租赁房源</el-button>
            </el-row>
          </el-card>
        </el-affix>
      </el-col>
    </el-row>
  </div>


  <el-drawer
      title="在线咨询"
      v-model="chatVisible"
      direction="rtl">
    <div class="chat-container">
      <div class="chat-title">
        <img :src="baseUrl + '/api/files/' + friend.avatar" class="chat-avatar" alt=""/>
        <div class="chat-name">{{ friend.nickname }}</div>
      </div>
      <div class="chat-main" ref="scrollView">
        <div class="message-list" ref="messageList">
          <div v-if="history.loading" class="history-loading">
            <img src="../../assets/imgs/pending.gif" alt=""/>
          </div>
          <div v-else :class="history.allLoaded ? 'history-loaded':'load'" @click="loadHistoryMessage(false)">
            {{ history.allLoaded ? '已经没有更多的历史消息' : '获取历史消息' }}
          </div>
          <div v-for="(message, index) in history.messages" :key="index">
            <div class="time-tips">{{ renderMessageDate(message, index) }}</div>
            <div class="message-recalled" v-if="message.recalled">
              <div v-if="message.recaller.id === currentUser.id" class="message-recalled-self">
                <div>你撤回了一条消息</div>
                <span v-if="message.type === 'text' && Date.now()-message.timestamp< 60 * 1000 "
                      @click="editRecalledMessage(message.payload.text)">重新编辑</span>
              </div>
              <div v-else>{{ message.recaller.data.nickname }}撤回了一条消息</div>
            </div>
            <div class="message-item" v-else>
              <div class="message-item-checkbox" v-if="messageSelector.visible && message.status !== 'sending'">
                <input class="input-checkbox" type="checkbox" :value="message.messageId" v-model="messageSelector.ids"
                       @click="selectMessages">
              </div>
              <div class="message-item-content" :class="{ self: Number(message.senderId) === currentUser.id }">
                <div class="sender-info">
                  <img v-if="currentUser.id === Number(message.senderId)" :src="baseUrl + '/api/files/'+currentUser.avatar" class="sender-avatar" alt=""/>
                  <img v-else :src="baseUrl + '/api/files/'+friend.avatar" class="sender-avatar" alt=""/>
                </div>
                <div class="message-content" @click.right="showActionPopup(message)">
                  <div class="message-payload">
                    <div class="pending" v-if="message.status === 'sending'"></div>
                    <div class="send-fail" v-if="message.status === 'fail'"></div>
                    <div v-if="message.type === 'text'" class="content-text"
                         v-html="emoji.decoder.decode(message.payload.text)"></div>
                  </div>
                  <div v-if="currentUser.id === Number(message.senderId)" :class="message.read ?'message-read':'message-unread'">
                    <div v-if="Number(message.senderId) === currentUser.id">{{ message.read ? '已读' : '未读' }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="chat-footer">
        <div class="action-delete" v-if="messageSelector.visible">
          <img class="delete-btn" src="../../assets/imgs/delete.png" @click="deleteMultipleMessages" alt=""/>
          <div>删除</div>
        </div>
        <div class="action-box" v-else>
          <div class="action-bar">
            <!-- 表情 -->
            <div class="action-item">
              <div v-if="emoji.visible" class="emoji-box">
                <img
                    v-for="(emojiItem, emojiKey, index) in emoji.map"
                    class="emoji-item"
                    :key="index"
                    :src="emoji.url + emojiItem"
                    @click="chooseEmoji(emojiKey)"
                 alt=""/>
              </div>
              <i class="iconfont icon-smile" title="表情" @click="showEmojiBox"></i>
            </div>
          </div>

          <!-- GoEasyIM最大支持3k的文本消息，如需发送长文本，需调整输入框maxlength值 -->
          <div class="input-box">
            <textarea ref="input" @focus="onInputFocus" @keyup.enter="sendTextMessage" v-model="text" maxlength="700" autocomplete="off" class="input-content"></textarea>
          </div>
          <div class="send-box">
            <el-button type="primary" class="send-button" @click="sendTextMessage">发送</el-button>
          </div>
        </div>
      </div>
      <!-- 消息删除撤回弹窗 -->
      <div class="action-popup" v-if="actionPopup.visible" @click="actionPopup.visible = false">
        <div class="action-popup-main">
          <div class="action-item" @click="deleteSingleMessage">删除</div>
          <div class="action-item" v-if="actionPopup.recallable" @click="recallMessage">撤回</div>
          <div class="action-item" @click="showCheckBox">多选</div>
          <div class="action-item" @click="actionPopup.visible = false">取消</div>
        </div>
      </div>
    </div>
  </el-drawer>

  <el-dialog title="预约看房（每次看房时长为1小时）" v-model="reserveVisible" align-center width="520px" :close-on-click-modal="false" :before-close="closeForm">
    <el-form>
      <el-form-item label="姓名">
        <el-input clearable class="reserveInput" style="margin-left: 28px" v-model="reserveForm.name" placeholder="请输入姓名"/>
      </el-form-item>
      <el-form-item label="手机号">
        <el-input clearable class="reserveInput" style="margin-left: 14px" type="number" v-model="reserveForm.mobile" placeholder="请输入手机号"/>
      </el-form-item>
      <el-form-item label="看房时间">
        <el-date-picker
            clearable
            style="height: 40px;width: 450px"
            v-model="reserveForm.time"
            type="datetime"
            placeholder="选择看房时间"
        />
      </el-form-item>
      <el-form-item label="备注">
        <el-input clearable type="textarea" style="margin-left: 28px;width: 450px" v-model="reserveForm.remark" placeholder="请输入备注"/>
      </el-form-item>
    </el-form>
    <el-footer>
      <template #default>
        <el-row>
          <el-button type="primary" class="reserveButton" @click="reserve">
            <span style="font-family: 'PingFang SC',sans-serif;font-size: 16px">立即预约</span>
          </el-button>
        </el-row>
        <el-row style="margin-top: 10px">
          <span style="margin-left: 10px;font-size: 16px;font-family: 'PingFang SC',sans-serif">房东将在24小时内确认，如果未确认，将自动为您取消预约</span>
        </el-row>
      </template>
    </el-footer>
  </el-dialog>

  <el-dialog title="租赁申请（租期最少为1个月）" v-model="rentVisible" align-center width="520px" :close-on-click-modal="false" :before-close="closeForm">
    <el-form>
      <el-form-item label="姓名">
        <el-input clearable class="reserveInput" style="margin-left: 28px" v-model="rentForm.name" placeholder="请输入姓名"/>
      </el-form-item>
      <el-form-item label="手机号">
        <el-input clearable class="reserveInput" style="margin-left: 14px" type="number" v-model="rentForm.mobile" placeholder="请输入手机号"/>
      </el-form-item>
      <el-form-item label="租期">
        <el-input clearable class="reserveInput" style="margin-left: 28px" type="number" v-model="rentForm.term" placeholder="请输入租期(月)"/>
      </el-form-item>
      <el-form-item label="备注">
        <el-input clearable type="textarea" style="margin-left: 28px;width: 450px" v-model="rentForm.remark" placeholder="请输入备注"/>
      </el-form-item>
    </el-form>
    <el-footer>
      <template #default>
        <el-row>
          <el-button type="primary" class="reserveButton" @click="apply">
            <span style="font-family: 'PingFang SC',sans-serif;font-size: 16px">申请租赁</span>
          </el-button>
        </el-row>
        <el-row style="margin-top: 10px">
          <span style="margin-left: 10px;font-size: 16px;font-family: 'PingFang SC',sans-serif">房东将在24小时内确认，如果未确认，将自动为您取消申请</span>
        </el-row>
      </template>
    </el-footer>
  </el-dialog>


</template>

<script setup>

import {inject, nextTick, onBeforeUnmount, onMounted, reactive, ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import request from "../../utils/request.js";
import {loginStore} from "../../stores/loginStore.js";
import {ElMessage} from "element-plus";
import {ChatDotRound} from "@element-plus/icons-vue";
import {goEasyStore} from "../../stores/goEasyStore.js";
import {formatDate} from "../../utils/utils.js";
import EmojiDecoder from "../../utils/EmojiDecoder.js";
import AMapLoader from "@amap/amap-jsapi-loader";
import formatTimeToDateTime from "../../utils/dateTimeUtils.js";
import {dataStore} from "../../stores/dataStore.js";

const currentUser = ref(JSON.parse(localStorage.getItem("user")))
const store = loginStore()
const activeName = ref('description')
const route = useRoute()
const house = ref({
  configs: [],
  tags: []
})

let map = null
let marker = null
const detail = () => {
  const id = route.params.id
  request.get("/house/detail/"+id).then(res => {
    house.value = res.data
    house.value.maintenanceTime = classifyTime(res.data.maintenanceTime)

    friend.value = {
      id: house.value.authorId,
      nickname: house.value.nickname,
      avatar: house.value.avatar,
    }
    to.value = {
      type: GoEasy.IM_SCENE.PRIVATE,
      id: friend.value.id,
      data: {nickname: friend.value.nickname, avatar: friend.value.avatar},
    };
    loadHistoryMessage(true);

    AMapLoader.load({
      key: "31871c5a4c8852f16c75f95f28034e43",
      version: "2.0",
      plugins: [],
    }).then((AMap) => {
      const address = house.value.province + house.value.city + house.value.area + house.value.address
      request.get('/getLonAndLatByAddress/'+ address).then(res => {
        const coordinatesArray = res.data.split(',');
        map = new AMap.Map("container", {
          viewMode: "3D",
          zoom: 15,
          center: coordinatesArray,
        });

        // 创建一个标记
        marker = new AMap.Marker({
          position: coordinatesArray, // 标记的位置坐标
          map: map, // 所属的地图实例
        });
        AMap.plugin(["AMap.ToolBar", "AMap.Scale","AMap.ControlBar"], function () {
          const toolbar = new AMap.ToolBar({
            position: {
              top: '20px',
              right: '10px'
            }
          });
          map.addControl(toolbar);
          const Scale = new AMap.Scale();
          map.addControl(Scale);
        });
        // 监听地图双击事件
        map.on('dblclick', function (e) {
          // 获取当前地图缩放级别
          const currentZoom = map.getZoom();

          // 放大地图
          map.setZoom(currentZoom + 1);
        });

      })
    }).catch((e) => {
      console.log(e);
    });
  })
}

const classifyTime = (inputTime) => {
  // 将输入的字符串时间转化为Date对象
  let time = new Date(inputTime);

  // 获取当前时间
  let now = new Date();

  // 获取今天0点的时间
  let todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate());

  // 获取昨天0点的时间
  let yesterdayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 1);

  // 判断时间是否在今天
  if (time >= todayStart && time <= now) {
    return "今天";
  }
  // 判断时间是否在昨天
  else if (time >= yesterdayStart && time < todayStart) {
    return "昨天";
  }
  // 如果都不是，那么就是几天前
  else {
    // 计算时间差异（以毫秒为单位），然后将其转换为天数
    let diffInMilliseconds = Math.abs(now - time);
    // 返回天数
    return Math.ceil(diffInMilliseconds / (1000 * 60 * 60 * 24)) + "天前";
  }
}

const user = JSON.parse(localStorage.getItem("user"))

let reserveForm = reactive({
  name:'',
  mobile: '',
  time: '',
  remark: '',
  houseId: '',
})
const reserveVisible = ref(false)
const openReserve = () => {
  reserveForm.houseId = house.value.id
  reserveForm.userId = user.id
  reserveForm.name = user.realName
  reserveForm.mobile = user.mobile
  reserveVisible.value = true
}

const closeForm = () => {
  if (reserveVisible.value === true){
    reserveForm.time = ''
    reserveForm.remark = ''
    reserveVisible.value = false
  }
  if (rentVisible.value === true){
    rentForm.term = ''
    rentForm.remark = ''
    rentVisible.value = false
  }
}

const data = dataStore()
const router = useRouter()
const reserve = () => {
  if (reserveForm.name === '') {
    ElMessage.error("请输入姓名")
  } else if (reserveForm.mobile === '') {
    ElMessage.error("请输入手机号")
  } else if (!reserveForm.mobile.match(/^1[34578]\d{9}$/)){
    ElMessage.error("请输入正确的手机号")
  } else if (reserveForm.time === '') {
    ElMessage.error("请选择看房时间")
  } else if (reserveForm.time < Date.now()) {
    ElMessage.error("看房时间不能早于当前时间")
  } else if (reserveForm.time - Date.now() < 48 * 60 * 60 * 1000) {
    // 如果预约时间在当前时间之后，但是距离当前时间不足48小时
    ElMessage.error("请提前至少两天进行预约");
  } else {
    reserveForm.time = formatTimeToDateTime(reserveForm.time)
    request.post("/reserve/save",reserveForm).then(res => {
      if (res.code === '200'){
        data.successMsg(res,"提交预约看房成功，请等待房东确认")
        setTimeout(()=>{closeForm()},1000)
      } else {
        data.errorMsg(res,router)
        if (res.code === '401'){
          closeForm()
        }
      }
    })
  }
}

const rentForm = reactive({
  name:'',
  mobile: '',
  term: '',
  remark: '',
  houseId: '',
})
const rentVisible = ref(false)
const openRent = () => {
  rentForm.houseId = house.value.id
  rentForm.mobile = user.mobile
  rentForm.name= user.realName
  rentVisible.value = true
}

const apply = () => {
  if (rentForm.name === '') {
    ElMessage.error("请输入姓名")
  } else if (rentForm.mobile === '') {
    ElMessage.error("请输入手机号")
  } else if (!rentForm.mobile.match(/^1[34578]\d{9}$/)){
    ElMessage.error("请输入正确的手机号")
  } else if (rentForm.term <= 0){
    ElMessage.error("租期至少为1个月")
  }  else if (!Number.isInteger(Number(rentForm.term))){
    ElMessage.error("请输入整数")
  } else {
    request.post("/rentAudit/apply",rentForm).then(res => {
      if (res.code === '200'){
        data.successMsg(res,"提交租赁申请成功，请等待房东确认，即将跳转到我的租赁申请页面")
        setTimeout(()=>{
          router.push("/myRent/myRentApply")
        },1000)
      } else {
        data.errorMsg(res,router)
        if (res.code === '401'){
          closeForm()
        }
      }
    })
  }
}

const scrollToElement = (id) => {
  const element = document.getElementById(id);

  if (element) {
    // 使用 smooth scroll behavior 滚动到元素位置
    window.scrollTo({
      top: element.offsetTop + 570,
      behavior: 'smooth',
    });
  }
}

const handleScroll = () => {
  const descriptionElement = document.getElementById("description");
  const basicElement = document.getElementById("basic");
  const configElement = document.getElementById("config");
  const mapElement = document.getElementById("map");

  if (descriptionElement && window.scrollY < basicElement.offsetTop + 530) {
    activeName.value = "description";
  } else if (basicElement && window.scrollY >= basicElement.offsetTop + 530 && window.scrollY < configElement.offsetTop + 530) {
    activeName.value = "basic";
  } else if (configElement && window.scrollY >= configElement.offsetTop + 530 && window.scrollY < mapElement.offsetTop + 530) {
    activeName.value = "config";
  } else if (mapElement && window.scrollY >= mapElement.offsetTop + 530) {
    activeName.value = "map";
  }
};

const emojiUrl = 'https://imgcache.qq.com/open/qcloud/tim/assets/emoji/';
const emojiMap = {
  '[NO]': 'emoji_0@2x.png',
  '[OK]': 'emoji_1@2x.png',
  '[下雨]': 'emoji_2@2x.png',
  '[么么哒]': 'emoji_3@2x.png',
  '[乒乓]': 'emoji_4@2x.png',
  '[便便]': 'emoji_5@2x.png',
  '[信封]': 'emoji_6@2x.png',
  '[偷笑]': 'emoji_7@2x.png',
  '[傲慢]': 'emoji_8@2x.png',
  '[再见]': 'emoji_9@2x.png',
  '[冷汗]': 'emoji_10@2x.png',
  '[凋谢]': 'emoji_11@2x.png',
  '[刀]': 'emoji_12@2x.png',
  '[删除]': 'emoji_13@2x.png',
  '[勾引]': 'emoji_14@2x.png',
  '[发呆]': 'emoji_15@2x.png',
  '[发抖]': 'emoji_16@2x.png',
  '[可怜]': 'emoji_17@2x.png',
  '[可爱]': 'emoji_18@2x.png',
  '[右哼哼]': 'emoji_19@2x.png',
  '[右太极]': 'emoji_20@2x.png',
  '[右车头]': 'emoji_21@2x.png',
  '[吐]': 'emoji_22@2x.png',
  '[吓]': 'emoji_23@2x.png',
  '[咒骂]': 'emoji_24@2x.png',
  '[咖啡]': 'emoji_25@2x.png',
  '[啤酒]': 'emoji_26@2x.png',
  '[嘘]': 'emoji_27@2x.png',
  '[回头]': 'emoji_28@2x.png',
  '[困]': 'emoji_29@2x.png',
  '[坏笑]': 'emoji_30@2x.png',
  '[多云]': 'emoji_31@2x.png',
  '[大兵]': 'emoji_32@2x.png',
  '[大哭]': 'emoji_33@2x.png',
  '[太阳]': 'emoji_34@2x.png',
  '[奋斗]': 'emoji_35@2x.png',
  '[奶瓶]': 'emoji_36@2x.png',
  '[委屈]': 'emoji_37@2x.png',
  '[害羞]': 'emoji_38@2x.png',
  '[尴尬]': 'emoji_39@2x.png',
  '[左哼哼]': 'emoji_40@2x.png',
  '[左太极]': 'emoji_41@2x.png',
  '[左车头]': 'emoji_42@2x.png',
  '[差劲]': 'emoji_43@2x.png',
  '[弱]': 'emoji_44@2x.png',
  '[强]': 'emoji_45@2x.png',
  '[彩带]': 'emoji_46@2x.png',
  '[彩球]': 'emoji_47@2x.png',
  '[得意]': 'emoji_48@2x.png',
  '[微笑]': 'emoji_49@2x.png',
  '[心碎了]': 'emoji_50@2x.png',
  '[快哭了]': 'emoji_51@2x.png',
  '[怄火]': 'emoji_52@2x.png',
  '[怒]': 'emoji_53@2x.png',
  '[惊恐]': 'emoji_54@2x.png',
  '[惊讶]': 'emoji_55@2x.png',
  '[憨笑]': 'emoji_56@2x.png',
  '[手枪]': 'emoji_57@2x.png',
  '[打哈欠]': 'emoji_58@2x.png',
  '[抓狂]': 'emoji_59@2x.png',
  '[折磨]': 'emoji_60@2x.png',
  '[抠鼻]': 'emoji_61@2x.png',
  '[抱抱]': 'emoji_62@2x.png',
  '[抱拳]': 'emoji_63@2x.png',
  '[拳头]': 'emoji_64@2x.png',
  '[挥手]': 'emoji_65@2x.png',
  '[握手]': 'emoji_66@2x.png',
  '[撇嘴]': 'emoji_67@2x.png',
  '[擦汗]': 'emoji_68@2x.png',
  '[敲打]': 'emoji_69@2x.png',
  '[晕]': 'emoji_70@2x.png',
  '[月亮]': 'emoji_71@2x.png',
  '[棒棒糖]': 'emoji_72@2x.png',
  '[汽车]': 'emoji_73@2x.png',
  '[沙发]': 'emoji_74@2x.png',
  '[流汗]': 'emoji_75@2x.png',
  '[流泪]': 'emoji_76@2x.png',
  '[激动]': 'emoji_77@2x.png',
  '[灯泡]': 'emoji_78@2x.png',
  '[炸弹]': 'emoji_79@2x.png',
  '[熊猫]': 'emoji_80@2x.png',
  '[爆筋]': 'emoji_81@2x.png',
  '[爱你]': 'emoji_82@2x.png',
  '[爱心]': 'emoji_83@2x.png',
  '[爱情]': 'emoji_84@2x.png',
  '[猪头]': 'emoji_85@2x.png',
  '[猫咪]': 'emoji_86@2x.png',
  '[献吻]': 'emoji_87@2x.png',
  '[玫瑰]': 'emoji_88@2x.png',
  '[瓢虫]': 'emoji_89@2x.png',
  '[疑问]': 'emoji_90@2x.png',
  '[白眼]': 'emoji_91@2x.png',
  '[皮球]': 'emoji_92@2x.png',
  '[睡觉]': 'emoji_93@2x.png',
  '[磕头]': 'emoji_94@2x.png',
  '[示爱]': 'emoji_95@2x.png',
  '[礼品袋]': 'emoji_96@2x.png',
  '[礼物]': 'emoji_97@2x.png',
  '[篮球]': 'emoji_98@2x.png',
  '[米饭]': 'emoji_99@2x.png',
  '[糗大了]': 'emoji_100@2x.png',
  '[红双喜]': 'emoji_101@2x.png',
  '[红灯笼]': 'emoji_102@2x.png',
  '[纸巾]': 'emoji_103@2x.png',
  '[胜利]': 'emoji_104@2x.png',
  '[色]': 'emoji_105@2x.png',
  '[药]': 'emoji_106@2x.png',
  '[菜刀]': 'emoji_107@2x.png',
  '[蛋糕]': 'emoji_108@2x.png',
  '[蜡烛]': 'emoji_109@2x.png',
  '[街舞]': 'emoji_110@2x.png',
  '[衰]': 'emoji_111@2x.png',
  '[西瓜]': 'emoji_112@2x.png',
  '[调皮]': 'emoji_113@2x.png',
  '[象棋]': 'emoji_114@2x.png',
  '[跳绳]': 'emoji_115@2x.png',
  '[跳跳]': 'emoji_116@2x.png',
  '[车厢]': 'emoji_117@2x.png',
  '[转圈]': 'emoji_118@2x.png',
  '[鄙视]': 'emoji_119@2x.png',
  '[酷]': 'emoji_120@2x.png',
  '[钞票]': 'emoji_121@2x.png',
  '[钻戒]': 'emoji_122@2x.png',
  '[闪电]': 'emoji_123@2x.png',
  '[闭嘴]': 'emoji_124@2x.png',
  '[闹钟]': 'emoji_125@2x.png',
  '[阴险]': 'emoji_126@2x.png',
  '[难过]': 'emoji_127@2x.png',
  '[雨伞]': 'emoji_128@2x.png',
  '[青蛙]': 'emoji_129@2x.png',
  '[面条]': 'emoji_130@2x.png',
  '[鞭炮]': 'emoji_131@2x.png',
  '[风车]': 'emoji_132@2x.png',
  '[飞吻]': 'emoji_133@2x.png',
  '[飞机]': 'emoji_134@2x.png',
  '[饥饿]': 'emoji_135@2x.png',
  '[香蕉]': 'emoji_136@2x.png',
  '[骷髅]': 'emoji_137@2x.png',
  '[麦克风]': 'emoji_138@2x.png',
  '[麻将]': 'emoji_139@2x.png',
  '[鼓掌]': 'emoji_140@2x.png',
  '[龇牙]': 'emoji_141@2x.png'
}

const chatVisible = ref(false)
const to = ref({
  type: 'private',
  id: '',
  data: {
    nickname: '',
    avatar: '',
  },
})
const history = ref({
  messages: [],
  allLoaded: false,
  loading: true
})
const text = ref('')

//定义表情列表
let emoji = reactive({
  url: emojiUrl,
  map: emojiMap,
  visible: false,
  decoder: new EmojiDecoder(emojiUrl, emojiMap),
})

// 展示消息删除弹出框
const actionPopup = ref({
  visible: false,
  message: null,
  recallable: false,
})
// 消息选择
const messageSelector = ref({
  visible: false,
  ids: []
})

const friend = ref({
  id: '',
  nickname: '',
  avatar: ''
})

const goEasy = inject("goEasy")
const GoEasy = inject("GoEasy")

const onReceivedPrivateMessage = (message) => {
  if (Number(message.senderId) === friend.value.id) {
    history.value.messages.push(message);
    markPrivateMessageAsRead();
    console.log(history.value.messages)
  }
  scrollToBottom();
}

const sendTextMessage = () => {
  if (!text.value.trim()) {
    ElMessage.error("请输入消息内容")
    return
  }
  goEasy.im.createTextMessage({
    text: text.value,
    to: to.value,
    onSuccess: (message) => {
      sendMessage(message);
      text.value = '';
    },
    onFailed: (err) => {
      console.log("创建消息err:", err);
    }
  });
}

const onInputFocus = () => {
  emoji.visible = false;
}
const showEmojiBox = () => {
  emoji.visible = !emoji.visible;
}
const chooseEmoji = (emojiKey) => {
  text.value += emojiKey;
  emoji.visible = false;
}
const sendMessage = (message) => {
  let messageRef = ref()
  messageRef.value = message
  history.value.messages.push(messageRef.value);
  scrollToBottom();
  goEasy.im.sendMessage({
    message: messageRef.value,
    onSuccess: (message) => {
      console.log('发送成功', message);
      console.log(message.type === 'text')
    },
    onFailed: function (error) {
      console.log('发送失败:', error);
    }
  });
}

const showActionPopup = (message) => {
  const MAX_RECALLABLE_TIME = 3 * 60 * 1000; //3分钟以内的消息才可以撤回
  messageSelector.value.ids = [message.messageId];
  actionPopup.value.recallable = (Date.now() - message.timestamp) < MAX_RECALLABLE_TIME && Number(message.senderId) === currentUser.id && message.status === 'success';
  actionPopup.value.visible = true;
}
const deleteSingleMessage = () => {
  actionPopup.value.visible = false;
  deleteMessage();
}
const deleteMultipleMessages = () => {
  if (messageSelector.value.ids.length > 0) {
    messageSelector.value.visible = false;
    deleteMessage();
  }
}
const deleteMessage = () => {
  let conf = confirm("确认删除？");
  if (conf === true) {
    let selectedMessages = [];
    history.value.messages.forEach((message) => {
      if (messageSelector.value.ids.includes(message.messageId)) {
        selectedMessages.push(message);
      }
    });
    goEasy.im.deleteMessage({
      messages: selectedMessages,
      onSuccess: () => {
        selectedMessages.forEach((message) => {
          let index = history.value.messages.indexOf(message);
          if (index > -1) {
            history.value.messages.splice(index, 1);
          }
        });
        messageSelector.value.ids = [];
      },
      onFailed: (error) => {
        console.log('error:', error);
      },
    });
  } else {
    messageSelector.value.ids = [];
  }
}
const recallMessage = () => {
  let selectedMessages = [];
  history.value.messages.forEach((message) => {
    if (messageSelector.value.ids.includes(message.messageId)) {
      selectedMessages.push(message);
    }
  });
  actionPopup.value.visible = false;
  goEasy.im.recallMessage({
    messages: selectedMessages,
    onSuccess: () => {
      console.log('撤回成功');
    },
    onFailed: (error) => {
      console.log('撤回失败,error:', error);
    }
  });
}

const editRecalledMessage = (text) => {
  text.value = text;
}
const showCheckBox = () => {
  messageSelector.value.ids = [];
  messageSelector.value.visible = true;
  actionPopup.value.visible = false;
}
const selectMessages = (e) => {
  if (e.target.checked) {
    messageSelector.value.ids.push(e.target.value)
  } else {
    let index = messageSelector.value.ids.indexOf(e.target.value);
    if (index > -1) {
      messageSelector.value.ids.splice(index, 1);
    }
  }
}
const loadHistoryMessage = (isScrollToBottom) => {
  history.value.loading = true;
  //历史消息
  let lastMessageTimeStamp;
  let lastMessage = history.value.messages[0];
  if (lastMessage) {
    lastMessageTimeStamp = lastMessage.timestamp;
  }
  goEasy.im.history({
    userId: friend.value.id,
    lastTimestamp: lastMessageTimeStamp,
    limit: 10,
    onSuccess: (result) => {
      history.value.loading = false;
      let messages = reactive([]);
      messages.push(...result.content);
      if (messages.length === 0) {
        history.value.allLoaded = true;
      } else {
        if (lastMessageTimeStamp) {
          messages.push(...history.value.messages)
          history.value.messages = messages;
        } else {
          history.value.messages = messages;
        }
        if (messages.length < 10) {
          history.value.allLoaded = true;
        }
        if (isScrollToBottom) {
          scrollToBottom();
          //收到的消息设置为已读
          markPrivateMessageAsRead();
        }
      }
    },
    onFailed: (error) => {
      //获取失败
      history.value.loading = false;
      console.log('获取历史消息失败, code:' + error.code + ',错误信息:' + error.content);
    },
  });
}
const markPrivateMessageAsRead = () => {
  goEasy.im.markMessageAsRead({
    id: to.value.id,
    type: to.value.type,
    onSuccess: function () {
      console.log('标记私聊已读成功');
    },
    onFailed: function (error) {
      console.log('标记私聊已读失败', error);
    },
  });
}
let scrollView = ref();
let messageList = ref();
const scrollToBottom = () => {
  nextTick(() => {
    scrollView.value.scrollTop = messageList.value.scrollHeight;
  });
}
const renderMessageDate = (message, index) => {
  if (index === 0) {
    return formatDate(message.timestamp);
  } else {
    if (message.timestamp - history.value.messages[index - 1].timestamp > 5 * 60 * 1000) {
      return formatDate(message.timestamp);
    }
  }
  return '';
}

onMounted(() => {
  const store2 = goEasyStore()
  store2.connectGoEasy()
  detail();
  goEasy.im.on(GoEasy.IM_EVENT.PRIVATE_MESSAGE_RECEIVED, onReceivedPrivateMessage); //监听消息
  window.addEventListener("scroll", handleScroll);

});

// 在组件销毁时移除滚动事件监听
onBeforeUnmount(() => {
  window.removeEventListener("scroll", handleScroll);
});

const baseUrl = ref(import.meta.env.VITE_APP_BASE_URL)

</script>

<style scoped>
  .left{
    margin-left: 130px;
  }
  .font1{
    font-family: PingFangMedium,sans-serif;
    font-size: 24px;
    font-weight: bold;
  }
  .font2 {
    margin-top: 20px;
    font-family: PingFangMedium, sans-serif;
    font-size: 14px;
    font-weight: bold;
    color: rgba(236,71,49,0.87);
  }
  .font3{
    font-size: 18px;
    font-weight: bold;
    font-family: PingFang,sans-serif;
  }
  .font4{
    color: #777;
    font-family: PingFangMedium, sans-serif;
    margin-top: 5px;
    font-size: 15px;
  }
  .font5{
    font-family: PingFang, sans-serif;
    font-weight: bold;
    font-size: 16px;
  }
  .el-carousel__item h3 {
    color: #475669;
    font-size: 14px;
    opacity: 0.75;
    line-height: 150px;
    margin: 0;
  }

  .el-carousel__item:nth-child(2n) {
    background-color: #99a9bf;
  }

  .el-carousel__item:nth-child(2n+1) {
    background-color: #d3dce6;
  }
  .button1{
    width: 120px;border-radius: 5px;height: 40px
  }
  .font6{
    font-weight: bold;
    font-family: PingFang,sans-serif;
    font-size: 16px;
  }
  .font7{
    font-family: PingFang,sans-serif;
    font-size: 24px;

  }
  .config{
    margin-top: 20px;
    width: 100%;
    height: 80px;
    background:#f5f6fa;
    font-size: 18px;
    color: #777777;
    font-family: PingFang,sans-serif;
    display: flex;
    justify-content: center;
    border-radius: 5px;
  }
  /deep/.el-tabs__item {
    font-family: PingFang,sans-serif;
    font-size: 18px;
  }
  .author{
    width: 426px;
    height: 340px;
    margin-left: 60px;
    border-radius: 10px;
  }
  .el-menu-item:hover {
    background-color: #fff !important; /* 设置悬停时的背景颜色 */
    color: #303133 !important; /* 设置悬停时的文字颜色 */
  }
  .el-menu-item.is-active {
    background-color: #fff !important; /* 设置点击时的背景颜色 */
    color: #409EFF !important; /* 设置点击时的文字颜色 */
  }
  .avatar{
    height: 62px;
    width: 62px;
    border-radius: 50%;
    margin-top: 10px;
    margin-left: 10px
  }
  .font8{
    font-size: 16px;
    font-family: 'PingFang SC',sans-serif;
    font-weight: bold
  }
  .button2{
    width: 90%;
    height: 40px;
    border-radius: 5px;
    margin-top: 20px;
    margin-left: 20px;
    margin-right: 20px;
  }
  .chat-container {
    width: 100%;
    height:600px;
    display: flex;
    flex-direction: column;
    position: relative;
  }

  .chat-title {
    height: 40px;
    padding: 0 15px;
    display: flex;
    align-items: center;
    font-size: 18px;
  }

  .chat-avatar {
    width: 35px;
    height: 35px;
  }

  .chat-name {
    width: 450px;
    font-size: 15px;
    margin-left: 10px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    word-break: break-all;
  }

  .chat-main {
    display: flex;
    flex-direction: column;
    overflow-y: auto;
    flex: 1;
    scrollbar-width: thin;
  }

  .chat-main::-webkit-scrollbar {
    width: 0;
  }

  .chat-main .history-loaded {
    text-align: center;
    font-size: 12px;
    color: #cccccc;
    line-height: 20px;
  }

  .chat-main .load {
    text-align: center;
    font-size: 12px;
    color: #d02129;
    line-height: 20px;
    cursor: pointer;
  }

  .history-loading {
    width: 100%;
    text-align: center;
  }

  .time-tips {
    color: #999;
    text-align: center;
    font-size: 12px;
  }

  .message-list {
    padding: 0 10px;
  }

  .message-item {
    display: flex;
  }

  .message-item-checkbox {
    height: 50px;
    margin-right: 15px;
    display: flex;
    align-items: center;
  }

  .input-checkbox {
    position: relative;
  }

  .message-item-checkbox input[type="checkbox"]::before, .message-item-checkbox input[type="checkbox"]:checked::before {
    content: "";
    position: absolute;
    top: -3px;
    left: -3px;
    background: #FFFFFF;
    width: 18px;
    height: 18px;
    border: 1px solid #cccccc;
    border-radius: 50%;
  }

  .message-item-checkbox input[type="checkbox"]:checked::before {
    content: "\2713";
    background-color: #d02129;
    width: 18px;
    color: #FFFFFF;
    text-align: center;
    font-weight: bold;
  }

  .message-item-content {
    flex: 1;
    margin: 5px 0;
    overflow: hidden;
    display: flex;
  }

  .sender-info {
    margin: 0 5px;
  }

  .sender-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
  }

  .message-content {
    max-width: calc(100% - 100px);
  }

  .message-payload {
    display: flex;
    flex-direction: row;
    justify-content: flex-end;
    align-items: center;
  }

  .pending {
    background: url("../../assets/imgs/pending.gif") no-repeat center;
    background-size: 13px;
    width: 15px;
    height: 15px;
  }

  .send-fail {
    background: url("../../assets/imgs/failed.png") no-repeat center;
    background-size: 15px;
    width: 18px;
    height: 18px;
    margin-right: 3px;
  }

  .message-read {
    color: rgb(128, 128, 128);
    font-size: 12px;
    text-align: end;
    height: 16px;
  }

  .message-unread {
    color: #d02129;
    font-size: 12px;
    text-align: end;
    height: 16px;
  }

  .content-text {
    display: flex;
    align-items: center;
    text-align: left;
    background: #eeeeee;
    font-size: 14px;
    font-weight: 500;
    padding: 6px 8px;
    margin: 3px 0;
    line-height: 25px;
    white-space: pre-line;
    overflow-wrap: anywhere;
    border-radius: 8px;
    word-break: break-all;
  }

  .content-image {
    display: block;
    cursor: pointer;
  }

  .content-image img {
    max-height: 200px;
  }

  .content-audio {
    -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
  }

  .content-audio .audio-facade {
    min-width: 12px;
    background: #eeeeee;
    border-radius: 7px;
    display: flex;
    font-size: 14px;
    padding: 8px;
    margin: 5px 0;
    line-height: 25px;
    cursor: pointer;
  }

  .content-audio .audio-facade-bg {
    background-size: 15px;
    width: 20px;
  }
  

  .content-order {
    border-radius: 5px;
    border: 1px solid #eeeeee;
    padding: 8px;
    display: flex;
    flex-direction: column;
  }

  .content-order .order-id {
    font-size: 12px;
    color: #666666;
    margin-bottom: 5px;
  }

  .content-order .order-body {
    display: flex;
    font-size: 13px;
    padding: 5px;
  }

  .content-order .order-img {
    width: 70px;
    height: 70px;
    border-radius: 5px;
  }

  .content-order .order-name {
    margin-left: 10px;
    width: 135px;
    color: #606164;
  }

  .content-order .order-count {
    margin-top: 5px;
    margin-left: 5px;
    font-size: 12px;
    color: #666666;
    flex: 1;
  }

  .content-file {
    width: 240px;
    height: 65px;
    font-size: 15px;
    background: #FFFFFF;
    border: 1px solid #eeeeee;
    display: flex;
    align-items: center;
    padding: 10px;
    border-radius: 5px;
    cursor: pointer;
  }

  .content-file:hover {
    background: #f1f1f1;
  }

  .file-info {
    width: 194px;
    text-align: left;
  }

  .file-name {
    text-overflow: ellipsis;
    overflow: hidden;
    display: -webkit-box;
    word-break: break-all;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  .file-size {
    font-size: 12px;
    color: #ccc;
  }

  .file-img {
    width: 50px;
    height: 50px;
  }

  .message-item .self {
    overflow: hidden;
    display: flex;
    justify-content: flex-start;
    flex-direction: row-reverse;
  }

  .message-item .self .audio-facade {
    flex-direction: row-reverse;
  }

  .message-item .self .audio-facade-bg {
    background-size: 15px;
    width: 20px;
    -moz-transform: rotate(180deg);
    -webkit-transform: rotate(180deg);
    -o-transform: rotate(180deg);
    transform: rotate(180deg);
  }

  .message-recalled {
    display: flex;
    align-items: center;
    justify-content: center;
    line-height: 28px;
    font-size: 13px;
    text-align: center;
    color: grey;
    margin-top: 10px;
  }

  .message-recalled-self {
    display: flex;
  }

  .message-recalled-self span {
    margin-left: 5px;
    color: #D02129;
    cursor: pointer;
  }

  .chat-footer {
    border-top: 1px solid #dcdfe6;
    width: 100%;
    height: 140px;
    background: #FFFFFF;
  }

  .action-delete {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    background-color: #FFFFFF;
  }

  .delete-btn {
    width: 25px;
    height: 25px;
    padding: 10px;
    background: #f5f5f5;
    border-radius: 50%;
    cursor: pointer;
    margin-bottom: 10px;
  }

  .action-box {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  .action-bar {
    display: flex;
    flex-direction: row;
    padding: 0 10px;
  }

  .action-bar .action-item {
    text-align: left;
    padding: 10px 0;
    position: relative;
  }

  .action-bar .action-item .iconfont {
    font-size: 22px;
    margin: 0 10px;
    z-index: 3;
    color: #606266;
    cursor: pointer;
  }

  .action-bar .action-item .iconfont:focus {
    outline: none;
  }

  .action-bar .action-item .iconfont:hover {
    color: #d02129;
  }

  .emoji-box {
    width: 450px;
    margin-top: -230px;
    position: absolute;
    top: -111px;
    left: -11px;
    z-index: 2007;
    background: #fff;
    border: 1px solid #ebeef5;
    padding: 12px;
    text-align: justify;
    font-size: 14px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    word-break: break-all;
    border-radius: 4px;
  }

  .emoji-item {
    width: 25px;
    height: 25px;
    margin: 0 2px;
  }

  .input-box {
    padding: 0 10px;
    flex: 1;
  }

  .input-content {
    border: none;
    resize: none;
    display: block;
    padding: 5px 15px;
    box-sizing: border-box;
    width: 100%;
    color: #606266;
    outline: none;
    background: #FFFFFF;
    word-break: break-all;
  }

  .send-box {
    text-align: right;
  }

  .send-button {
    margin-right: 15px;
    width: 70px;
    height: 28px;
    font-size: 15px;
    border-radius: 5px;
  }

  .action-popup {
    width: 848px;
    height: 100%;
    position: absolute;
    top: 0;
    left: -281px;
    background: rgba(51, 51, 51, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .action-popup-main {
    width: 150px;
    height: 120px;
    background: #ffffff;
    z-index: 100;
    border-radius: 10px;
    overflow: hidden;
  }

  .action-popup-main .action-item {
    text-align: center;
    line-height: 40px;
    font-size: 15px;
    color: #262628;
    border-bottom: 1px solid #efefef;
    cursor: pointer;
  }
  #container {
    margin-top: 10px;
    width: 1250px;
    height: 500px;
  }
  ::v-deep input::-webkit-outer-spin-button,
  ::v-deep input::-webkit-inner-spin-button {
    -webkit-appearance: none !important;
  }
  ::v-deep input[type="number"] {
    -moz-appearance: textfield; /* 此处写不写都可以 */
  }
  .reserveInput{
    height: 40px;
    width: 450px
  }
  .reserveButton{
    width: 450px;
    margin: 10px 0 0 20px;
    height: 40px;
    border-radius: 10px;
  }

</style>