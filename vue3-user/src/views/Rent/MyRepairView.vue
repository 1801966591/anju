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
        <el-table-column align="center" prop="houseName" label="房源信息(点击可查看详情)" width="250px">
          <template #default="scope">
            <el-link :underline="false" @click="houseDetail(scope.row.houseId)">{{scope.row.houseName}}</el-link>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="repairInfo" label="报修信息" width="400px"/>
        <el-table-column align="center" prop="createTime" label="报修时间" width="180px"/>
        <el-table-column align="center" prop="repairStatus" label="报修状态" width="140px">
          <template #default="scope">
            <el-tag v-if="scope.row.repairStatus === 1" type="info">待处理</el-tag>
            <el-tag v-if="scope.row.repairStatus === 2">已处理</el-tag>
            <el-tag v-if="scope.row.repairStatus === 3" type="success">已完成</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="repairTime" label="处理时间" width="180px">
          <template #default="scope">
            <div v-if="scope.row.repairTime !== null">
              {{scope.row.repairTime}}
            </div>
            <el-tag v-else>暂未处理</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作" width="250px">
          <template #default="scope" >
            <div v-if="scope.row.repairStatus === 2">
              <el-button
                  type="success"
                  @click="complete(scope.row.id)"><i class="el-icon-complete"/><span class="button-text">完成</span></el-button>
              <el-button
                  type="primary"
                  @click="openRepair(scope.row)"><i class="el-icon-repair"/><span class="button-text">再次报修</span></el-button>
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
  request.get("/repair/search",{
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

const repairForm = ref({})
const repairVisible = ref(false)
const openRepair = (data) => {
  repairForm.value = data
  repairVisible.value = true
}

const closeForm = () => {
  repairVisible.value = false
  findBySearch()
}

const repair = () => {
  if (repairForm.value.repairInfo === ''){
    ElMessage.error("请输入报修信息")
  }else {
    request.put("/repair/reApply",repairForm.value).then(res => {
      if (res.code === '200') {
        store.successMsg(res,"再次报修成功，请等待房东处理");
        setTimeout(()=>{closeForm()},1000)
      }else {
        store.errorMsg(res,router)
      }
    })
  }
}

const complete = (id) => {
  request.put("/repair/complete/"+id).then(res => {
    if (res.code === '200') {
      store.successMsg(res,"本次报修已完成,祝您生活愉快");
      setTimeout(()=>{findBySearch()},1000)
    }else{
      store.errorMsg(res,router)
    }
  })
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