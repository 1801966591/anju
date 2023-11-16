<template>
  <div class="container">

    <el-form>
      <el-row style="margin-left: 50px">
        <el-col :span="8">
          <el-form-item label="房源名称">
            <el-input v-model="form.name" placeholder="请输入房源名称" style="width: 200px" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="房源类型">
            <el-input v-model="form.houseType" placeholder="请输入房源类型，如1室1厅" style="width: 250px" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="房屋朝向" style="margin-left: 60px">
            <el-select v-model="form.toward" placeholder="请选择朝向" style="width: 120px">
              <el-option label="东" value="1">东</el-option>
              <el-option label="南" value="2">南</el-option>
              <el-option label="西" value="3">西</el-option>
              <el-option label="北" value="4">北</el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row style="margin-left: 50px">
        <el-col :span="8">
          <el-form-item label="租赁类型">
            <el-select v-model="form.rentType" placeholder="请选择租赁类型" style="width: 150px">
              <el-option label="合租" value="1">合租</el-option>
              <el-option label="整租" value="2">整租</el-option>
              <el-option label="独栋" value="3">独栋</el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="面积">
            <el-input v-model="form.area" type="number" placeholder="请输入房源面积" style="width: 150px" clearable /><span style="margin-left: 5px;">平方米</span>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="房源装修程度" style="margin-left: 60px">
            <el-select v-model="form.decoration" placeholder="请选择房源装修程度" style="width: 170px">
              <el-option label="毛坯" value="1">毛坯</el-option>
              <el-option label="简修" value="2">简修</el-option>
              <el-option label="精修" value="3">精修</el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row style="margin-left: 50px">
        <el-col :span="18">
          <el-form-item label="地址">
            <el-cascader
                placeholder="请选择省/市/区"
                v-model="location"
                :options="options"
                @change="handleLocationChange"/>
            <el-input v-model="form.address" placeholder="请输入具体地址" clearable style="width: 500px;margin-left: 20px"/>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <transition
              enter-active-class="animate__animated animate__fadeInRight"
              leave-active-class="animate__animated animate__fadeOutRight">
            <el-form-item label="出租房间数" v-show="form.rentType === '1'">
              <el-input type="number" v-model="form.rentRoomNumber" placeholder="请输入出租房间数" clearable style="width: 500px;margin-left: 20px"/>
            </el-form-item>
          </transition>
        </el-col>
      </el-row>
      <el-row style="margin-left: 50px;">
        <transition
            enter-active-class="animate__animated animate__fadeInLeft"
            leave-active-class="animate__animated animate__fadeOutLeft">
          <el-col :span="6" v-show="form.rentType !== '3'">
            <el-form-item label="楼层">
              <el-input v-model="form.floor" placeholder="请输入房源所在楼层" style="width: 150px" clearable /><span style="margin-left: 5px;">层</span>
            </el-form-item>
          </el-col>
        </transition>
        <el-col :span="6">
          <el-form-item label="总楼层">
            <el-input v-model="form.totalFloor" placeholder="请输入房源总楼层" style="width: 150px" clearable/><span style="margin-left: 5px;">层</span>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="是否有电梯">
            <el-select v-model="form.elevator" placeholder="请选择" style="width: 140px">
              <el-option label="无" value="0"/>
              <el-option label="有" value="1"/>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="租金">
            <el-input v-model="form.rent" placeholder="请输入房源租金" style="width: 150px" clearable/><span style="margin-left: 5px;">元/月</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row style="margin-left: 50px;">
        <el-col :span="6">
          <el-form-item label="用电类型">
            <el-select v-model="form.powerType" placeholder="请选择" style="width: 140px">
              <el-option label="未知" value="0"/>
              <el-option label="商电" value="1"/>
              <el-option label="民电" value="2"/>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="用水类型">
            <el-select v-model="form.waterType" placeholder="请选择" style="width: 140px">
              <el-option label="未知" value="0"/>
              <el-option label="商水" value="1"/>
              <el-option label="民水" value="2"/>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="供暖类型">
            <el-select v-model="form.heatingType" placeholder="请选择" style="width: 140px">
              <el-option label="无供暖" value="0"/>
              <el-option label="集中供暖" value="1"/>
              <el-option label="地板辐射供暖" value="2"/>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="是否有燃气">
            <el-select v-model="form.gas" placeholder="请选择" style="width: 140px">
              <el-option label="无" value="0"/>
              <el-option label="有" value="1"/>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row style="margin-left: 50px">
        <el-col :span="6">
          <el-form-item label="入住性别要求">
            <el-select v-model="form.gender" placeholder="请选择" style="width: 140px">
              <el-option label="不限" value="0"/>
              <el-option label="男" value="1"/>
              <el-option label="女" value="2"/>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="上次维护时间">
            <el-date-picker
                v-model="form.maintenanceTime"
                type="datetime"
                placeholder="请选择时间"
                :shortcuts="shortcuts"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="房屋配置信息" style="margin-left: 20px">
            <el-select
                v-model="form.config"
                multiple
                placeholder="请选择房屋配置信息"
                style="width: 400px"
            >
              <el-option
                  v-for="item in configs"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row style="margin-left: 50px">
        <el-form-item label="看房时间">
          <el-switch
              style="margin-left: 40px"
              v-model="chooseTime"
              active-text="特定时间看房"
              inactive-text="随时看房"
          />
          <transition
              appear
              enter-active-class="animate__animated animate__fadeInRight"
              leave-active-class="animate__animated animate__fadeOutRight">
            <div style="margin-left: 50px" v-show="chooseTime">
              <el-time-picker
                  v-model="timeRange"
                  is-range
                  range-separator="至"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                  format="HH:mm:ss"
              />
            </div>
          </transition>
        </el-form-item>
      </el-row>
      <el-row style="margin-left: 50px">
        <el-form-item label="特色标签">
          <el-select
              v-model="form.tag"
              multiple
              placeholder="请选择特色标签"
              style="width: 400px"
          >
            <el-option
                v-for="item in tags"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-row>
      <el-row style="margin-left: 50px">
        <el-form-item label="房源描述">
          <DescriptionEditor/>
        </el-form-item>
      </el-row>
      <el-row style="margin-left: 50px">
        <el-col :span="20">
          <el-form-item label="房源图片">
            <el-upload
                v-model:file-list="fileList"
                :action="baseUrl + '/api/upload'"
                list-type="picture-card"
                :on-preview="handlePictureCardPreview"
                :limit="4"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>

            <el-dialog v-model="dialogVisible" align-center>
              <el-image :src="dialogImageUrl" fit="contain"/>
            </el-dialog>
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <div style="margin-top: 100px">
            <el-button type="warning" plain @click="reset"><i class="el-icon-reset"/><span class="button-text">重置</span></el-button>
            <el-button type="primary" plain @click="upload"><i class="el-icon-export"/><span class="button-text">上传</span></el-button>
          </div>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from "vue";
import {useRouter} from "vue-router";
import {dataStore} from "../../stores/dataStore.js";
import request from "../../utils/request.js";
import {Delete, Plus, ZoomIn} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import formatTimeToDateTime, {formatTimeToTime} from "../../utils/dateTimeUtils.js";
import DescriptionEditor from "./components/DescriptionEditor.vue";
const baseUrl = ref(import.meta.env.VITE_APP_BASE_URL)
const user = reactive(localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {})
const router = useRouter()
const store = dataStore();
const options = ref([])
const location = ref([])
const chooseTime = ref(false)
const timeRange = ref([])
const fileList = ref([])
const form = ref({
  name: '',
  houseType: '',
  toward: '',
  rentType: '',
  rentRoomNumber: '',
  area: '',
  decoration: '',
  provinceId: '',
  cityId: '',
  areaId: '',
  address: '',
  floor: '',
  totalFloor: '',
  elevator: '',
  rent: '',
  viewTime: '',
  startTime: '',
  endTime: '',
  waterType: '',
  powerType: '',
  heatingType: '',
  gas: '',
  gender: '',
  maintenanceTime: '',
  description: '',
  config: [],
  tag: [],
  fileList: [],
})

const shortcuts = [
  {
    text: 'Today',
    value: new Date(),
  },
  {
    text: 'Yesterday',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() - 3600 * 1000 * 24)
      return date
    },
  },
  {
    text: 'A week ago',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() - 3600 * 1000 * 24 * 7)
      return date
    },
  },
]

const getLocations = () => {
  request.get("/getLocation").then(res => {
    options.value = res.data
  })
}

const tags = ref([])
const getHouseTags = () => {
  request.get("/dicValue/getHouseTagList").then(res => {
    if (res.code === '200') {
      tags.value = res.data
    }else{
      store.errorMsg(res,router)
    }
  })
}

const configs = ref([])
const getHouseConfigs = () => {
  request.get("/dicValue/getHouseConfigList").then(res => {
    if (res.code === '200') {
      configs.value = res.data
    }else{
      store.errorMsg(res,router)
    }
  })
}

const handleLocationChange = () => {
  form.value.provinceId = location.value[0]
  form.value.cityId = location.value[1]
  form.value.areaId = location.value[2]
}

const dialogImageUrl = ref('')
const dialogVisible = ref(false)

const handlePictureCardPreview = (file) => {
  dialogImageUrl.value = file.url
  dialogVisible.value = true
}

onMounted(() => {
  store.description = ''
  getLocations()
  getHouseConfigs()
  getHouseTags()
})

const reset = () => {
  form.value = {
    name: '',
    houseType: '',
    toward: '',
    rentType: '',
    area: '',
    decoration: '',
    provinceId: '',
    cityId: '',
    areaId: '',
    address: '',
    floor: '',
    totalFloor: '',
    elevator: '',
    rent: '',
    viewTime: '',
    startTime: '',
    endTime: '',
    description: '',
    tag: [],
    fileList: [],
  }
  store.description = ''
  fileList.value = []
  location.value = []
  chooseTime.value = false
  timeRange.value = []
  getLocations()
}

const upload = () => {
  if (!chooseTime.value){
    form.value.viewTime = '随时看房'
    form.value.startTime = ''
    form.value.endTime = ''
  } else {
    form.value.startTime = formatTimeToTime(timeRange.value[0])
    form.value.endTime = formatTimeToTime(timeRange.value[1])
  }
  if (form.value.name === ''){
    ElMessage.error("请输入房源名称")
  } else if (form.value.houseType === ''){
    ElMessage.error("请输入房源类型")
  } else if (form.value.toward === ''){
    ElMessage.error("请选择房屋朝向")
  } else if (form.value.rentType === ''){
    ElMessage.error("请选择租赁类型")
  } else if (form.value.area === ''){
    ElMessage.error("请输入房源面积")
  } else if (form.value.decoration === ''){
    ElMessage.error("请选择装修程度")
  } else if (form.value.name === ''){
    ElMessage.error("请输入房源名称")
  } else if (form.value.provinceId === '' || form.value.cityId === '' || form.value.area === ''){
    ElMessage.error("请选择省/市/区")
  } else if (form.value.address === ''){
    ElMessage.error("请输入房源具体地址")
  } else if (form.value.rentType !== '3' && form.value.floor === ''){
    ElMessage.error("请输入房源所在楼层")
  }  else if (form.value.rentType === '1' && form.value.rentRoomNumber === ''){
    ElMessage.error("请输入出租房间数")
  } else if (form.value.totalFloor === ''){
    ElMessage.error("请输入房源总楼层")
  } else if (form.value.elevator === ''){
    ElMessage.error("请选择是否有电梯")
  } else if (chooseTime.value && (form.value.startTime === '' || form.value.endTime === '')){
    ElMessage.error("请选择看房时间")
  } else if (form.value.powerType === ''){
    ElMessage.error("请选择用电类型")
  } else if (form.value.waterType === ''){
    ElMessage.error("请选择用水类型")
  } else if (form.value.heatingType === ''){
    ElMessage.error("请选择供暖类型")
  } else if (form.value.gas === ''){
    ElMessage.error("请选择是否有燃气")
  } else if (form.value.gender === ''){
    ElMessage.error("请选择请选择入住性别要求")
  } else if (form.value.maintenanceTime === ''){
    ElMessage.error("请选择上次维护时间")
  } else if (fileList.value.length === 0){
    ElMessage.error("请选择房源图片")
  } else if (fileList.value.length > 4){
    ElMessage.error("只能上传4张图片")
  }  else if (fileList.value.length < 4){
    ElMessage.error("需要上传4张图片")
  } else {
    form.value.maintenanceTime = formatTimeToDateTime(form.value.maintenanceTime)
    form.value.description = store.description
    fileList.value.forEach(img => {
      form.value.fileList.push(img.response.data)
    })
    request.post("/house/upload",form.value).then(res => {
      if (res.code === '200') {
        store.successMsg(res,"房源上传成功，请等待管理员审核")
        setTimeout(() => {
          reset()
        },1000)
      }else {
        store.errorMsg(res,router)
      }
    })
  }
}


</script>

<style scoped>
  .container{
    margin-left: 5%;
    margin-top: 5px;
    width: 90%;
    font-family: 仓耳渔阳体, sans-serif;
  }
  ::v-deep input::-webkit-outer-spin-button,
  ::v-deep input::-webkit-inner-spin-button {
    -webkit-appearance: none !important;
  }

  ::v-deep input[type="number"] {
    -moz-appearance: textfield; /* 此处写不写都可以 */
  }

</style>