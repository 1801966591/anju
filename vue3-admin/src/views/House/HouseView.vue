<template>

  <el-row style="margin-top: 20px">
    <span class="search-span" style="margin-left: 0">房源名称：</span>
    <el-input placeholder="请输入房源名称" v-model="param.name" clearable style="width: 200px"/>
    <el-button plain type="primary" style="margin-left:20px;width: 76px;" :icon="Search" @click="findBySearch"><span class="button-text">搜索</span></el-button>
    <el-button plain type="warning" style="margin-left:20px;width: 76px;" @click="resetParam"><i class="el-icon-reset"/><span class="button-text">重置</span></el-button>
  </el-row>

  <el-row style="margin-top: 30px;" v-if="user.role === 'LANDLORD'">
    <el-col>
      <el-button type="danger" plain @click="delByIds" :icon="Delete"><span class="button-text">批量删除</span></el-button>
    </el-col>
  </el-row>

  <div style="margin-top: 30px">
    <el-table
        ref="multipleTable"
        :data="tableData"
        tooltip-effect="dark"
        border
        style="width: 100%;"
        @selection-change="handleSelectionChange">
      <el-table-column
          @selection-change="handleSelectionChange"
          type="selection"
          width="55">
      </el-table-column>
      <el-table-column align="center" prop="id" label="ID" width="80px"></el-table-column>
      <el-table-column align="center" prop="name" label="房源名称(点击可查看详情)" width="196px">
        <template #default="scope">
          <el-link :underline="false" @click="detail(scope.row.id)">{{scope.row.name}}</el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="author" label="发布人" width="117px" v-if="user.role === 'ADMIN'" />
      <el-table-column align="center" prop="houseType" label="房屋类型" width="117px"/>
      <el-table-column align="center" prop="rentType" label="租赁类型" width="117px">
        <template #default="scope">
          <div v-if="scope.row.rentType===1">合租</div>
          <div v-if="scope.row.rentType===2">整租</div>
          <div v-if="scope.row.rentType===3">独栋</div>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="area" label="房源面积" width="100px">
        <template #default="scope">
          {{scope.row.area}}㎡
        </template>
      </el-table-column>
      <el-table-column align="center" prop="decoration" label="装修程度" width="117px">
        <template #default="scope">
          <div v-if="scope.row.decoration===1">毛坯</div>
          <div v-if="scope.row.decoration===2">简装</div>
          <div v-if="scope.row.decoration===3">精装</div>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="rent" label="租金" width="100px">
        <template #default="scope">
          {{scope.row.rent}}元/月
        </template>
      </el-table-column>
      <el-table-column align="center" prop="auditStatus" label="审核状态" width="100px" v-if="user.role === 'LANDLORD'">
        <template #default="scope">
          <div v-if="scope.row.auditStatus===0"><el-tag type="info">待审核</el-tag></div>
          <div v-if="scope.row.auditStatus===1"><el-tag type="success">已通过</el-tag></div>
          <div v-if="scope.row.auditStatus===2"><el-tag type="warning">未通过</el-tag></div>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="content" label="审核意见" width="200px" v-if="user.role === 'LANDLORD'">
        <template #default="scope">
          <div v-if="scope.row.auditStatus===0">暂未审核</div>
          <div v-if="scope.row.auditStatus===1">
            <div v-if="scope.row.content === ''">
              无意见
            </div>
            <div v-else>
              {{scope.row.content}}
            </div>
          </div>
          <div v-if="scope.row.auditStatus===2">{{scope.row.content}}</div>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="listingStatus" label="上架状态" width="100px">
        <template #default="scope">
          <div v-if="scope.row.listingStatus===0"><el-tag type="info">未上架</el-tag></div>
          <div v-if="scope.row.listingStatus===1"><el-tag type="success">已上架</el-tag></div>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="rentStatus" label="出租状态" width="100px">
        <template #default="scope">
          <div v-if="scope.row.rentStatus===0"><el-tag type="info">未出租</el-tag></div>
          <div v-if="scope.row.rentStatus===1"><el-tag type="success">已出租</el-tag></div>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="createTime" label="发布时间" width="200px"/>
      <el-table-column align="center" prop="updateTime" label="修改时间" width="200px">
        <template #default="scope">
          <div v-if="scope.row.updateTime!==null">{{scope.row.updateTime}}</div>
          <div v-else>暂未修改</div>
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作"  width="400px" v-if="user.role === 'LANDLORD'">
        <template #default="scope" >
          <el-button
              v-if="scope.row.auditStatus === 1 && scope.row.rentStatus === 0 && scope.row.listingStatus === 0"
              plain
              type="success"
              @click="list(scope.row.id)"><i class="el-icon-listing"/> <span class="button-text">上架</span></el-button>
          <el-button
              plain
              type="success"
              @click="maintenance(scope.row.id)"><i class="el-icon-maintenance"/> <span class="button-text">维护</span></el-button>
          <el-button
              v-if="scope.row.auditStatus === 1 && scope.row.rentStatus === 0 && scope.row.listingStatus === 1"
              plain
              type="primary"
              @click="list(scope.row.id)"><i class="el-icon-listing"/> <span class="button-text">下架</span></el-button>
          <el-button
              v-if="scope.row.rentStatus === 0 && scope.row.listingStatus === 0"
              plain
              type="warning"
              :icon="Edit"
              @click="edit(scope.row.id)"><span class="button-text">编辑</span></el-button>
          <el-button
              v-if="scope.row.rentStatus === 0 && scope.row.listingStatus === 0"
              plain
              type="danger"
              :icon="Delete"
              @click="delById(scope.row.id)"><span class="button-text">删除</span></el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <div style="margin-top: 20px">
    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="param.currentPage"
        :page-sizes="[5, 10, 15, 20]"
        :page-size="param.pageSize"
        layout="total, sizes, prev, pager, next"
        :total="total">
    </el-pagination>
  </div>

  <el-drawer :title="title" v-model="editVisible" direction="rtl" :before-close="closeForm" size="1300px" :close-on-click-modal="false">
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
        <el-form-item label="地址">
          <el-cascader
              placeholder="请选择省/市/区"
              v-model="location"
              :options="options"
              @change="handleLocationChange"/>
          <el-input v-model="form.address" placeholder="请输入具体地址" clearable style="width: 500px;margin-left: 20px"/>
        </el-form-item>
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
        <el-col :span="18">
          <el-form-item label="房屋配置信息" style="margin-left: 20px">
            <el-select
                v-model="form.config"
                multiple
                placeholder="请选择房屋配置信息"
                style="width: 700px"
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
      </el-row>      <el-row style="margin-left: 50px">
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
            <el-button plain type="warning" @click="closeForm"><span class="button-text">取消</span></el-button>
            <el-button plain type="primary" @click="update"><span class="button-text">修改</span></el-button>
          </div>
        </el-col>
      </el-row>
    </el-form>
  </el-drawer>

  <HouseDetail :id="houseId"/>

</template>

<script setup>
import {Delete, Edit, Plus, Search} from "@element-plus/icons-vue";
import {onMounted, reactive, ref} from "vue";
import {useRouter} from "vue-router";
import request from "../../utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {dataStore} from "../../stores/dataStore.js";
import HouseDetail from "./components/HouseDetail.vue"
import formatTimeToDateTime, {formatTimeToTime} from "../../utils/dateTimeUtils.js";
import DescriptionEditor from "./components/DescriptionEditor.vue";
const baseUrl = ref(import.meta.env.VITE_APP_BASE_URL)
const user = reactive(localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {})
const param = reactive({
  name: '',
  currentPage: 1,
  pageSize: 5
})
let tableData = ref([])
const total  = ref(0)
const router = useRouter()
const store = dataStore();

const findBySearch = () => {
  request.get("/house/search",{
    params: param
  }).then(res => {
    const data = store.searchReturnMsg(res,router)
    if (data) {
      tableData.value = data.tableData
      total.value = data.total
    }
  })
}

const resetParam = () => {
  param.name = ''
  param.currentPage = 1
  param.pageSize = 5
  findBySearch()
}
//修改展示条数
const handleSizeChange = (pageSize) => {
  param.pageSize = pageSize;
  findBySearch();
}
//修改当前页数
const handleCurrentChange = (currentPage) => {
  param.currentPage = currentPage;
  findBySearch();
}

const form = ref({
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
  imgs: [],
})
const title = ref('')

const houseId = ref('')
const detail = (id) => {
  houseId.value = id
  store.DetailVisible = true
}

const closeForm = () => {
  form.value = {}
  location.value = []
  fileList.value = []
  findBySearch()
  editVisible.value = false
}

const editVisible = ref(false)
const chooseTime = ref(false)
const timeRange = ref([])
const fileList = ref([])
const location = ref([])
const edit = (id) => {
  request.get("/house/detail/"+id).then(res => {
    if (res.code === '200'){
      form.value = res.data
      form.value.fileList = []
      title.value = `修改【${form.value.name}】信息`
      form.value.area = res.data.houseArea
      form.value.toward = form.value.toward.toString()
      form.value.rentType = form.value.rentType.toString()
      form.value.decoration = form.value.decoration.toString()
      form.value.elevator = form.value.elevator.toString()
      form.value.powerType = form.value.powerType.toString()
      form.value.waterType = form.value.waterType.toString()
      form.value.heatingType = form.value.heatingType.toString()
      form.value.gas = form.value.gas.toString()
      form.value.gender = form.value.gender.toString()
      form.value.tag = res.data.tagIds
      form.value.config = res.data.configIds
      form.value.description = res.data.description
      store.description = res.data.description
      location.value.push(form.value.provinceId.toString())
      location.value.push(form.value.cityId.toString())
      location.value.push(form.value.areaId.toString())
      form.value.imgs.forEach(img => {
        fileList.value.push({
          name: img,
          url: import.meta.env.VITE_APP_BASE_URL + '/api/files/'+img,
          response: {
            data: img
          }
        })
      })
      console.log(fileList.value)
      editVisible.value = true
    }else {
      store.errorMsg(res,router)
    }
  })

}

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
const options = ref([])
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

const update = () => {
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
    if (!chooseTime.value){
      form.value.viewTime = '随时看房'
      form.value.startTime = ''
      form.value.endTime = ''
    } else {
      form.value.startTime = formatTimeToTime(timeRange.value[0])
      form.value.endTime = formatTimeToTime(timeRange.value[1])
    }
    form.value.maintenanceTime = formatTimeToDateTime(form.value.maintenanceTime)
    fileList.value.forEach(img => {
      form.value.fileList.push(img.response.data)
    })
    request.put("/house/update",form.value).then(res => {
      if (res.code === '200') {
        store.successMsg(res,"房源修改成功，请等待管理员审核")
        setTimeout(() => {
          closeForm()
        },1000)
      }else {
        store.errorMsg(res,router)
      }
    })
  }
}

const delById = (id) => {
  ElMessageBox.confirm('确定要删除这个房源吗', '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '我再想想',
    type: 'warning'
  }).then(() => {
    request.delete("/house/deleteById/"+id).then(res => {
      store.returnMsg(res,'删除房源信息成功',router)
      setTimeout(() => {findBySearch()},1000)
    })
  }).catch(() => {})
}

const ids = ref([])


const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.id)
}

const delByIds = () => {
  if (ids.value.length === 0){
    ElMessage.warning("请选择要删除的房源");
  } else {
    let title = ''
    if (ids.value.length === 1){
      title = '确定要删除这个房源吗'
    }else {
      title = '确定要删除这些房源吗'
    }
    ElMessageBox.confirm(title, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '我再想想',
      type: 'warning'
    }).then(() => {
      request.delete("/house/deleteByIds/"+ids.value).then(res => {
        store.returnMsg(res,'批量删除房源成功',router)
        setTimeout(() => {findBySearch()},1000)
      })
    }).catch(()=>{})
  }
}

const list = (id) => {
  request.post("/house/sale/"+id).then(res => {
    if (res.code === '200'){
      let msg = ''
      if (res.data === 1){
        msg = '上架成功'
      } else {
        msg = '下架成功'
      }
      store.successMsg(res,msg)
      setTimeout(() => {
        findBySearch()
      },1000)
    }
  })
}

const maintenance = (id) => {
  request.post("/house/maintenance/"+id).then(res => {
    if (res.code === '200'){
      store.successMsg(res,"维护成功")
      setTimeout(()=>{findBySearch()},1000)
    }else {
      store.errorMsg(res,router)
    }
  })
}

onMounted(() => {
  findBySearch()
  getHouseConfigs()
  getHouseTags()
  getLocations()
})



</script>


<style scoped>
::v-deep input::-webkit-outer-spin-button,
::v-deep input::-webkit-inner-spin-button {
  -webkit-appearance: none !important;
}

::v-deep input[type="number"] {
  -moz-appearance: textfield; /* 此处写不写都可以 */
}
</style>