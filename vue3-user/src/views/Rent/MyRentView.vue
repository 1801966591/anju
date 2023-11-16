<template>
  <div style="margin-left: 100px;width: 1400px;margin-top: 50px;font-family: 'PingFang SC',sans-serif">
    <el-row style="margin-top: 20px">
      <span class="search-span" style="margin-left: 0">房源名称：</span>
      <el-input placeholder="请输入房源名称" v-model="param.name" clearable style="width: 200px"/>
      <el-button plain type="primary" style="margin-left:20px;width: 76px;" :icon="Search" @click="findBySearch"><span class="button-text">搜索</span></el-button>
      <el-button plain type="warning" style="margin-left:20px;width: 76px;" @click="resetParam"><i class="el-icon-reset"/><span class="button-text">重置</span></el-button>
    </el-row>

    <div style="margin-top: 50px">
      <el-table
          ref="multipleTable"
          :data="tableData"
          tooltip-effect="dark"
          border
          style="width: 100%;">
        <el-table-column align="center" prop="houseName" label="房源信息(点击可查看详情)" width="330px">
          <template #default="scope">
            <el-row>
              <el-col :span="12">
                <el-image :src="baseUrl + '/api/files/'+scope.row.img"/>
              </el-col>
              <el-col :span="12">
                <el-link :underline="false" @click="houseDetail(scope.row.houseId)" style="margin-top: 40px">{{scope.row.houseName}}</el-link>
              </el-col>
            </el-row>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="rentStatus" label="租赁状态" width="110px">
          <template #default="scope">
            <el-tag v-if="scope.row.rentStatus === 1" type="info">租赁进行中</el-tag>
            <el-tag v-if="scope.row.rentStatus === 2" type="success">已完成</el-tag>
            <el-tag v-if="scope.row.rentStatus === 3" type="warning">已退租</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="term" label="租期(月)" width="90px">
          <template #default="scope">
            <el-tag v-if="scope.row.rentStatus === 3" type="warning">已退租</el-tag>
            <div v-else>{{scope.row.term}}</div>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="dueTime" label="租赁到期时间" width="180px">
          <template #default="scope">
            <el-tag v-if="scope.row.rentStatus === 3" type="warning">已退租</el-tag>
            <div v-else>{{scope.row.dueTime}}</div>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="rentTime" label="租赁时间" width="180px"/>
        <el-table-column align="center" prop="returnTime" label="退租时间" width="180px">
          <template #default="scope">
            <div v-if="scope.row.returnTime !== null">
              {{scope.row.returnTime}}
            </div>
            <el-tag v-else>暂未退租</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作">
          <template #default="scope" >
            <div v-if="scope.row.rentStatus === 1">
              <el-button
                  type="primary"
                  @click="openRepair(scope.row)"><i class="el-icon-repair"/><span class="button-text">报修</span></el-button>
              <el-button
                  type="success"
                  @click="openApply(scope.row,'relet')"><i class="el-icon-relet"/><span class="button-text">续租</span></el-button>
              <el-button
                  type="warning"
                  @click="rentOut(scope.row.id)"><i class="el-icon-renting-out"/><span class="button-text">退租</span></el-button>
            </div>
            <div v-else>
              <el-button
                  type="success"
                  @click="openApply(scope.row,'apply')"><i class="el-icon-apply"/><span class="button-text">再次申请</span></el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div style="margin-top: 30px">
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
    <HouseDetail :id="houseId"/>

    <el-dialog title="报修申请" v-model="repairVisible" align-center width="520px" :close-on-click-modal="false" :before-close="closeForm">
      <el-form>
        <el-form-item label="报修信息">
          <el-input clearable type="textarea" rows="5" v-model="repairForm.repairInfo" placeholder="请输入报修信息"/>
        </el-form-item>
      </el-form>
      <el-footer>
        <template #default>
          <el-row>
            <el-button type="primary" class="reserveButton" @click="repair">
              <span style="font-family: 'PingFang SC',sans-serif;font-size: 16px">报修</span>
            </el-button>
          </el-row>
          <el-row style="margin-top: 10px">
            <span style="margin-left: 80px;font-size: 16px;font-family: 'PingFang SC',sans-serif">房东将尽快处理报修信息，请耐心等待</span>
          </el-row>
        </template>
      </el-footer>
    </el-dialog>

    <el-dialog :title="title" v-model="rentVisible" align-center width="520px" :close-on-click-modal="false" :before-close="closeForm">
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
            <el-button type="primary" class="reserveButton" @click="apply()" v-if="applyType === 'relet'">
              <span style="font-family: 'PingFang SC',sans-serif;font-size: 16px">申请续租</span>
            </el-button>
            <el-button type="primary" class="reserveButton" @click="apply()" v-else-if="applyType === 'apply'">
              <span style="font-family: 'PingFang SC',sans-serif;font-size: 16px">申请租赁</span>
            </el-button>
          </el-row>
          <el-row style="margin-top: 10px">
            <span style="margin-left: 10px;font-size: 16px;font-family: 'PingFang SC',sans-serif">房东将在24小时内确认，如果未确认，将自动为您取消申请</span>
          </el-row>
        </template>
      </el-footer>
    </el-dialog>

  </div>

</template>

<script setup>
import {Edit, Search, View} from "@element-plus/icons-vue";
import {onMounted, reactive, ref} from "vue";
import {useRouter} from "vue-router";
import request from "../../utils/request.js";
import {dataStore} from "../../stores/dataStore.js";
import {ElMessage, ElMessageBox} from "element-plus";
import HouseDetail from "../Reserve/components/HouseDetail.vue";
import formatTimeToDateTime from "../../utils/dateTimeUtils.js";

const baseUrl = ref(import.meta.env.VITE_APP_BASE_URL)
const user = reactive(localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {})
const param = reactive({
  userId: '',
  name: '',
  realName: '',
  currentPage: 1,
  pageSize: 5
})
let tableData = ref([])
const total  = ref(1)
const router = useRouter()
const store = dataStore();

const findBySearch = () => {
  param.userId = user.id
  request.get("/rent/search",{
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

const houseId = ref('')
const houseDetail = (id) => {
  houseId.value = id
  store.DetailVisible = true
}

const repairForm = reactive({
  houseId: '',
  userId: '',
  rentId: '',
  repairInfo: ''
})
const repairVisible = ref(false)
const openRepair = (data) => {
  repairForm.houseId = data.houseId
  repairForm.userId = data.userId
  repairForm.rentId = data.id
  repairVisible.value = true
}

const closeForm = () => {
  repairForm.repairInfo = ''
  repairVisible.value = false
  rentForm.term = ''
  rentForm.remark = ''
  rentVisible.value = false
  findBySearch()
}

const repair = () => {
  if (repairForm.repairInfo === ''){
    ElMessage.error("请输入报修信息")
  }else {
    request.post("/repair/apply",repairForm).then(res => {
      if (res.code === '200') {
        store.successMsg(res,"报修成功，请等待房东处理");
        setTimeout(()=>{closeForm()},1000)
      }else {
        store.errorMsg(res,router)
      }
    })
  }
}

const rentForm = reactive({
  rentId: '',
  name:'',
  mobile: '',
  term: '',
  remark: '',
  houseId: '',
})
const rentVisible = ref(false)
const applyType = ref("")
const title = ref("")
const openApply = (data,type) => {
  rentForm.rentId = data.id
  rentForm.name = user.realName
  rentForm.houseId = data.houseId
  rentForm.mobile = user.mobile
  applyType.value = type
  if (type === 'apply'){
    title.value = '租赁申请（租期最少为1个月）'
  }else {
    title.value = '续租申请（租期最少为1个月）'
  }
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
    if (applyType.value === 'apply'){
      request.post("/rentAudit/apply",rentForm).then(res => {
        if (res.code === '200'){
          store.successMsg(res,"提交租赁申请成功，请等待房东确认，即将跳转到我的租赁申请页面")
          setTimeout(()=>{
            router.push("/myRent/myRentApply")
          },1000)
        } else {
          store.errorMsg(res,router)
          if (res.code === '401'){
            closeForm()
          }
        }
      })
    }else {
      request.post("/rentAudit/relet",rentForm).then(res => {
        if (res.code === '200'){
          store.successMsg(res,"提交续租申请成功，请等待房东确认")
          setTimeout(()=>{closeForm()},1000)
        } else {
          store.errorMsg(res,router)
          if (res.code === '401'){
            closeForm()
          }
        }
      })
    }
  }
}

const rentOut = (id) => {
  ElMessageBox.confirm('确定要退租吗', '退租', {
    confirmButtonText: '确定',
    cancelButtonText: '我再想想',
    type: 'warning'   }).then(() => {
      request.get("/rent/rentOut/"+id).then(res =>{
        if (res.code === '200'){
          store.successMsg(res,"退租成功，剩余租金已退款")
          setTimeout(()=>{closeForm()},1000)
        }else {
          store.errorMsg(res,router)
        }
      })
  }).catch()
}

onMounted(() => {
  findBySearch()
})

</script>


<style scoped>
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
::v-deep input::-webkit-outer-spin-button,
::v-deep input::-webkit-inner-spin-button {
  -webkit-appearance: none !important;
}
::v-deep input[type="number"] {
  -moz-appearance: textfield; /* 此处写不写都可以 */
}
</style>