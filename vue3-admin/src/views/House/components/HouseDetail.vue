<template>
  <el-drawer :title="title" v-model="store.DetailVisible" direction="btt" :before-close="closeForm" size="70vh" :close-on-click-modal="false">
    <el-row>
      <el-col :span="6">
        <el-form-item label="房源名称：">
          {{houseDetail.name}}
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="房源类型：">
          {{houseDetail.houseType}}
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="朝向：">
          <span v-if="houseDetail.toward === 1">东</span>
          <span v-if="houseDetail.toward === 2">南</span>
          <span v-if="houseDetail.toward === 3">西</span>
          <span v-if="houseDetail.toward === 4">北</span>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="租赁类型：">
          <span v-if="houseDetail.rentType === 1">合租</span>
          <span v-if="houseDetail.rentType === 2">整租</span>
          <span v-if="houseDetail.rentType === 3">独栋</span>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="6">
        <el-form-item label="面积：">
          <span>{{houseDetail.houseArea}}㎡</span>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="房源装修程度：">
          <span v-if="houseDetail.decoration === 1">毛坯</span>
          <span v-if="houseDetail.decoration === 2">简修</span>
          <span v-if="houseDetail.decoration === 3">精修</span>
        </el-form-item>
      </el-col>
      <el-col :span="6" v-show="houseDetail.rentType !== '3'">
        <el-form-item label="楼层：">
          <span>{{houseDetail.floor}}层</span>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="总楼层：">
          <span>{{houseDetail.totalFloor}}层</span>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="12">
        <el-form-item label="地址：">
          <span>{{houseDetail.province}} {{houseDetail.city}} {{houseDetail.area}} {{houseDetail.address}}</span>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="出租房间数：" v-if="houseDetail.rentType===1">
          <span>{{houseDetail.rentRoomNumber}}</span>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="6">
        <el-form-item label="是否有电梯：">
          <span v-if="houseDetail.elevator === 0">无</span>
          <span v-if="houseDetail.elevator === 1">有</span>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="租金：">
          <span>{{houseDetail.rent}}元</span>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="入住性别要求：">
          <span v-if="houseDetail.gender === 0">不限</span>
          <span v-if="houseDetail.gender === 1">男</span>
          <span v-if="houseDetail.gender === 2">女</span>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="上次维护时间：">
          <span>{{houseDetail.maintenanceTime}}</span>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="6">
        <el-form-item label="用电类型：">
          <span v-if="houseDetail.powerType === 0">未知</span>
          <span v-if="houseDetail.powerType === 1">商电</span>
          <span v-if="houseDetail.powerType === 2">民电</span>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="用水类型：">
          <span v-if="houseDetail.waterType === 0">未知</span>
          <span v-if="houseDetail.waterType === 1">商水</span>
          <span v-if="houseDetail.waterType === 2">民水</span>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="供暖类型：">
          <span v-if="houseDetail.heatingType === 0">无供暖</span>
          <span v-if="houseDetail.heatingType === 1">集中供暖</span>
          <span v-if="houseDetail.heatingType === 2">地板辐射供暖</span>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="是否有燃气：">
          <span v-if="houseDetail.gas === 0">无</span>
          <span v-if="houseDetail.gas === 1">有</span>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="6">
        <el-form-item label="看房时间：">
          <span v-if="houseDetail.viewStatus === 0">随时看房</span>
          <span v-else>{{houseDetail.startTime}}-{{houseDetail.endTime}}</span>
        </el-form-item>
      </el-col>
      <el-col :span="18" v-show="houseDetail.tags.length > 0">
        <el-form-item label="特色标签：">
          <div v-for="(tag,index) in houseDetail.tags" :key="index">
            <el-tag :style="index > 0 ? 'margin-left:20px':'margin-left:5px'" type="info">{{tag}}</el-tag>
          </div>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-show="houseDetail.configs.length > 0">
      <el-form-item label="房屋配置：">
        <el-row>
          <div v-for="(config, index) in houseDetail.configs" :key="index">
            <div style="margin-left: 40px;margin-top: 10px">
              <div>
                <i :class="config.value" style="font-size: 25px"/>
              </div>
              <div :style="config.name.length === 1 ? 'margin-left:6px' : (config.name.length === 2 ? '' : 'margin-left:-8px')"
                   style=" font-family: 'PingFang SC', sans-serif; margin-top: 5px; font-size: 16px">
                {{ config.name }}
              </div>
            </div>
          </div>
        </el-row>
      </el-form-item>
    </el-row>
    <el-row>
      <el-form-item label="房源描述：">
        <span v-html="houseDetail.description"></span>
      </el-form-item>
    </el-row>
    <el-row>
      <el-form-item label="房源图片：">
        <div v-for="(img,index) in houseDetail.imgs" :key="index">
          <el-image
              style="width: 150px;height: 100px"
              :style="index > 0 ? 'margin-left:20px':'margin-left:5px'"
              :src="baseUrl + '/api/files/' + img"
              fit="fill"
              :preview-src-list="[baseUrl + '/api/files/' + img]"/>
        </div>
      </el-form-item>
    </el-row>
  </el-drawer>
</template>

<script setup>
 import {dataStore} from "../../../stores/dataStore.js";
 import request from "../../../utils/request.js";
 import {onMounted, ref, watch} from "vue";
 const baseUrl = ref(import.meta.env.VITE_APP_BASE_URL)
 const props = defineProps(['id'])
 const store = dataStore()
 const title = ref('')
 const houseDetail = ref({
   configs: [],
   tags: [],
 })

 const closeForm = () => {
   houseDetail.value = {
     configs: [],
     tags: [],
   }
   store.DetailVisible = false
 }

 onMounted(() => {
   // Use a watcher to fetch data when DetailVisible changes to true
   watch(
       () => store.DetailVisible,
       async (newVal) => {
         if (newVal === true) {
           request.get("/house/detail/"+props.id).then(res => {
             if (res.code === "200") {
               houseDetail.value = res.data;
               title.value = `查看【${res.data.name}】详情信息`;
             } else {
               store.errorMsg();
             }
           })
         }
       }
   );
 });


</script>

<style scoped>

</style>