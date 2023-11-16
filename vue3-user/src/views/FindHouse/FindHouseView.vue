<template>
 <div>
   <el-row style="margin-top: 50px;margin-left: 240px">
     <el-input placeholder="请输入房源名称" v-model="param.name" style="height:42px;width: 800px;font-size: 15px" clearable>
       <template #append>
         <el-button style="background:#409EFF;height: 42px;width: 100px;color: #fff" type="primary" @click="findBySearch">开始找房</el-button>
       </template>
     </el-input>
   </el-row>
   <div class="searchType">
     <el-row>
       <el-col :span="2">
         <div style="margin-top: 10px">位置</div>
       </el-col>
       <el-col :span="22">
         <el-cascader
             placeholder="请选择省/市/区"
             v-model="location"
             :options="options"
             @change="handleLocationChange"/>
       </el-col>
     </el-row>
     <el-row style="margin-top: 20px">
       <el-col :span="2">类型</el-col>
       <el-col :span="22">
         <el-radio-group v-model="param.rentType">
           <el-radio style="margin-left: 20px" label="">不限</el-radio>
           <el-radio style="margin-left: 20px" label="1">合租</el-radio>
           <el-radio style="margin-left: 20px" label="2">整租</el-radio>
           <el-radio style="margin-left: 20px" label="3">独栋</el-radio>
         </el-radio-group>
       </el-col>
     </el-row>
     <el-row style="margin-top: 20px">
       <el-col :span="2">
         <div>租金区间</div>
       </el-col>
       <el-col :span="22">
         <el-input v-model="param.minRent" style="width: 70px;height:28px;"/> - <el-input v-model="param.maxRent" style="width: 70px;height:28px;"/> 元
       </el-col>
     </el-row>
     <el-row style="margin-top: 20px">
       <el-col :span="2">
         <div>面积</div>
       </el-col>
       <el-col :span="22">
         <el-input v-model="param.minArea" style="width: 70px;height:28px;"/> - <el-input v-model="param.maxArea" style="width: 70px;height:28px;"/> 平方米
       </el-col>
     </el-row>
   </div>
   <div style="margin-left: 240px">
     <el-divider/>
   </div>

   <el-row style="margin-top: 30px;font-family: PingFangMedium,sans-serif;font-size: 20px;margin-left: 240px">
     <el-col :span="12">
       <div>
       </div>
     </el-col>
     <el-col :span="9">
       <el-menu mode="horizontal"  style="margin-top: -15px;" default-active="default"  text-color="#929292" active-text-color="#409EFF">
         <el-menu-item style="font-size: 16px" index="default" @click="defaultOrder">综合排序</el-menu-item>
         <el-menu-item style="font-size: 16px;margin-left: 40px" index="latest" @click="listOrder('DESC')">最新上架</el-menu-item>
         <el-sub-menu index="price" style="margin-left: 40px;">
           <template #title>
             <span style="font-size: 16px">价格</span>
           </template>
           <el-menu-item style="font-size: 15px;" index="priceUp" @click="rentOrder('ASC')">价格升序</el-menu-item>
           <el-menu-item style="font-size: 15px;" index="priceDown" @click="rentOrder('DESC')">价格降序</el-menu-item>
         </el-sub-menu>
         <el-sub-menu index="area" style="margin-left: 40px;font-size: 16px">
           <template #title>
             <span style="font-size: 16px">面积</span>
           </template>
           <el-menu-item style="font-size: 15px;" index="areaUp" @click="areaOrder('ASC')">面积升序</el-menu-item>
           <el-menu-item style="font-size: 15px;" index="areaDown" @click="areaOrder('DESC')">面积降序</el-menu-item>
         </el-sub-menu>
       </el-menu>
     </el-col>
     <el-col :span="3">
       <el-link :underline="false" style="margin-top: -5px;margin-left: 50px;font-family: PingFangMedium,sans-serif;font-size: 16px">
         <span v-if="mode==='largeImage'" @click="changeMode('list')"><i class="el-icon-tabulation"/>切换列表</span>
         <span v-if="mode==='list'" @click="changeMode('largeImage')"><i class="el-icon-large-image"/>切换大图</span>
       </el-link>
     </el-col>
   </el-row>
   <div style="margin-top: -25px;margin-left: 240px">
     <el-divider/>
   </div>
   <div v-if="houseData.length>0">
     <div style="margin-top: 20px;margin-left: 240px" v-if="mode==='list'">
       <div style="margin-top: 20px" v-for="house in houseData" :key="house.id">
         <div @click="handleClick(house.id)" style="cursor: pointer">
           <el-row>
             <el-col :span="5">
               <el-image
                   style="width: 210px; height: 140px;border-radius: 10px;"
                   :src="baseUrl + '/api/files/'+house.img1"
                   fit="fill"/>
             </el-col>
             <el-col :span="15" style="margin-left: 20px">
               <div style="font-size: 20px;font-family: PingFangMedium,sans-serif;font-weight: bold">
                 <span v-if="house.rentType === 1">合租</span>
                 <span v-if="house.rentType === 2">整租</span>
                 <span v-if="house.rentType === 3">独栋</span>
                 · {{house.name}}
                 <span style="padding-left: 20px">{{house.houseType}}</span>
               </div>
               <div style="font-size: 15px;font-family: PingFang,sans-serif;margin-top: 15px">
                 <span>{{house.province}} · {{house.city}} · {{house.area}}</span>
                 <span style="padding-left: 20px">{{house.houseArea}}㎡</span>
                 <span style="padding-left: 20px">
                 <span v-if="house.rentType !== 3">{{house.floor}}/{{house.totalFloor}}层</span>
                 <span v-else>{{house.floor}}</span>
               </span>
                 <span style="padding-left: 20px">
                 <span v-if="house.toward === 1">东</span>
                 <span v-if="house.toward === 2">南</span>
                 <span v-if="house.toward === 3">西</span>
                 <span v-if="house.toward === 4">北</span>
               </span>
               </div>
               <div style="margin-top: 15px;" v-if="house.tags.length > 0">
                 <el-tag type="info" style="margin-left: 5px" v-for="(tag,index) in house.tags" :key="index">{{tag}}</el-tag>
               </div>
               <div style="margin-top: 15px;font-family: PingFang,sans-serif;font-size: 12px"><i class="el-icon-time"/>{{house.listTime}} </div>
             </el-col>
             <el-col :span="4" style="margin-left: -300px;font-family: PingFangMedium,sans-serif;color: red">
               ￥<span style="font-size: 20px;font-weight: bold">{{house.rent}}</span>元/月
             </el-col>
           </el-row>
         </div>
       </div>
     </div>
     <div style="margin-top: 20px;margin-left: 240px" v-if="mode==='largeImage'">
       <el-row>
         <el-col v-for="(house, index) in houseData" :key="index" :span="8">
           <div style="border-radius: 6px;width: 370px;height: 420px;cursor: pointer;border:1px solid #ddd;margin-bottom: 40px" @click="handleClick(house.id)">
             <img :src="baseUrl + '/api/files/'+house.img1" alt="" style="border-radius: 6px 6px 0 0;padding-right:1px;width: 370px;height: 246px;">
             <div style="height: 150px;margin-left: 10px">
               <el-row style="margin-top: 15px">
                 <div style="font-size: 18px;font-family: 'PingFang SC',sans-serif;font-weight: bold">
                   <span v-if="house.rentType === 1">合租</span>
                   <span v-if="house.rentType === 2">整租</span>
                   <span v-if="house.rentType === 3">独栋</span>
                   · {{house.name}}
                   <span style="padding-left: 10px">{{house.houseType}}</span>
                 </div>
               </el-row>
               <el-row style="margin-top: 15px">
                 <el-col :span="17">
                 <span style="font-family: 'PingFang SC',sans-serif;">
                   <span>{{house.houseArea}}㎡</span>
                   <span style="padding-left: 20px">
                     <span v-if="house.rentType !== 3">{{house.floor}}/{{house.totalFloor}}层</span>
                     <span v-else>{{house.floor}}</span>
                   </span>
                   <span style="padding-left: 20px">
                     <span v-if="house.toward === 1">东</span>
                     <span v-if="house.toward === 2">南</span>
                     <span v-if="house.toward === 3">西</span>
                     <span v-if="house.toward === 4">北</span>
                   </span>
                 </span>
                 </el-col>
                 <el-col :span="7" style="margin-top: -5px;">
                 <span style="font-family: 'PingFang SC',sans-serif;color: red;">
                   ￥<span style="font-size: 20px;font-weight: bold">{{house.rent}}</span>元/月
                 </span>
                 </el-col>
               </el-row>
               <el-row v-if="house.tags.length > 0" style="margin-top: 10px;">
                 <el-tag type="info" :style="index > 0 ? 'margin-left:5px':''" v-for="(tag,index) in house.tags" :key="index">{{tag}}</el-tag>
               </el-row>
               <el-row style="margin-top: 10px;font-family: 'PingFang SC',sans-serif">
                 <span>{{house.province}} · {{house.city}} · {{house.area}}</span>
               </el-row>
             </div>

           </div>
           <el-row v-if="(index + 1) % 3 === 0" style="margin-top: 20px;margin-left: 30px"/>
         </el-col>
       </el-row>
     </div>
     <div style="padding-left: 240px;margin-top: 30px" v-if="total-param.pageSize > 0">
       <el-button class="loadMore" @click="loadMore">查看更多</el-button>
     </div>
   </div>
   <div v-else>
     <div style="display: flex;justify-content: center;font-size: 18px;font-family: 'PingFang SC',sans-serif;margin-top: 50px">
       暂无符合条件的房源信息
     </div>
   </div>
 </div>
</template>

<script setup>
import {computed, ref, onMounted} from "vue";
import {useRoute, useRouter} from "vue-router";
import request from "../../utils/request";
const baseUrl = ref(import.meta.env.VITE_APP_BASE_URL)
const param = ref({
  name: '',
  rentType: '',
  provinceId: '',
  cityId: '',
  areaId: '',
  minRent: '',
  maxRent: '',
  minArea: '',
  maxArea: '',
  orderBy: '',
  order: '',
  currentPage: 1,
  pageSize: 20,
})
const houseData = ref([])
const options = ref([])
const mode = ref("list")


const router = useRouter();


const findBySearch = () => {
  request.get("/house/userSearch",{
    params: param.value
  }).then(res => {
    houseData.value = res.data.records
  })
}

const defaultOrder = () => {
  param.value.orderBy = ''
  param.value.order = ''
  findBySearch()
}

const listOrder = () => {
  param.value.orderBy = 'listTime'
  param.value.order = 'DESC'
  findBySearch()
}

const areaOrder = (order) => {
  param.value.orderBy = 'area'
  param.value.order = order
  findBySearch()
}

const rentOrder = (order) => {
  param.value.orderBy = 'rent'
  param.value.order = order
  findBySearch()
}

const loadMore = () => {
  mode.value === 'list' ? param.value.pageSize += 20 : param.value.pageSize += 30
  findBySearch()
}

const changeMode = (displayMode) => {
  mode.value = displayMode;
  displayMode === 'list' ? param.value.pageSize = 20 : param.value.pageSize = 30
  findBySearch()
}

const getLocations = () => {
  request.get("/getLocation").then(res => {
    options.value = res.data
  })
}

const location = ref([])
const handleLocationChange = () => {
  param.value.provinceId = location.value[0]
  param.value.cityId = location.value[1]
  param.value.areaId = location.value[2]
}

onMounted(() => {
  const route = useRoute()
  const rentType = route.query.rentType
  const name = route.query.name
  if (rentType !== '') {
    param.value.rentType = rentType
  }
  if (name !== '') {
    param.value.name = name
  }
  findBySearch()
  getLocations()
});

const handleClick = (id) => {
  router.push(`/houseDetail/${id}`)
}

</script>

<style scoped>
  .searchType{
    margin-left: 240px;
    margin-top: 20px;
    font-size: 16px;
    font-family: PingFangMedium,sans-serif;
    font-weight: 500;
  }
  .loadMore{
    width: 1100px;
    height: 50px;
    border-radius: 5px;
    text-align: center;
    font-family: "PingFang SC",sans-serif;
    font-size: 18px;
  }

</style>