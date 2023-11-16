<template>
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
      <el-table-column align="center" prop="repairInfo" label="报修信息" width="305px"/>
      <el-table-column align="center" prop="createTime" label="报修时间" width="180px"/>
      <el-table-column align="center" prop="repairStatus" label="报修状态" width="130px">
        <template #default="scope">
          <el-tag v-if="scope.row.repairStatus === 1" type="info">待处理</el-tag>
          <el-tag v-if="scope.row.repairStatus === 2">已处理</el-tag>
          <el-tag v-if="scope.row.repairStatus === 3" type="success">已完成</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="repairType" label="报修类型" width="130px">
        <template #default="scope">
          <el-tag v-if="scope.row.repairType === 1" type="success">首次报修</el-tag>
          <el-tag v-if="scope.row.repairType === 2">多次报修</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="repairTimes" label="报修次数" width="100px"/>
      <el-table-column align="center" prop="repairTime" label="处理时间" width="180px">
        <template #default="scope">
          <div v-if="scope.row.repairTime !== null">
            {{scope.row.repairTime}}
          </div>
          <el-tag v-else>暂未处理</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="250px" fixed="right">
        <template #default="scope" >
          <el-button
              type="primary"
              @click="detail(scope.row.id)"><el-icon><View/></el-icon><span class="button-text">查看详情</span></el-button>
          <el-button
              v-if="scope.row.repairStatus === 1"
              type="success"
              @click="handle(scope.row.id)"><i class="el-icon-repair"/><span class="button-text">处理</span></el-button>
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

  <el-dialog title="报修详情" v-model="detailVisible" align-center width="400px" :close-on-click-modal="false">
    <el-form style="margin-left: 20px">
      <el-form-item label="报修房源名称：">
        {{repairForm.houseName}}
      </el-form-item>
      <el-form-item label="报修人姓名：">
        {{repairForm.name}}
      </el-form-item>
      <el-form-item label="手机号：">
        {{repairForm.mobile}}
      </el-form-item>
      <el-form-item label="报修信息：">
        {{repairForm.repairInfo}}
      </el-form-item>
      <el-form-item label="报修时间：">
        {{repairForm.createTime}}
      </el-form-item>
      <el-form-item label="报修状态：">
        <el-tag v-if="repairForm.repairStatus === 1" type="info">待处理</el-tag>
        <el-tag v-if="repairForm.repairStatus === 2">已处理</el-tag>
        <el-tag v-if="repairForm.repairStatus === 3" type="success">已完成</el-tag>
      </el-form-item>
    </el-form>
  </el-dialog>


</template>

<script setup>
import {Search, View} from "@element-plus/icons-vue";
import {onMounted, reactive, ref} from "vue";
import {useRouter} from "vue-router";
import request from "../../utils/request.js";
import {dataStore} from "../../stores/dataStore.js";
import HouseDetail from "../House/components/HouseDetail.vue";

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
const detailVisible = ref(false)

const detail = (id) => {
  request.get("/repair/detail/"+id).then(res => {
    if (res.code === '200'){
      repairForm.value = res.data
      detailVisible.value = true
    }else {
      store.errorMsg(res,router)
    }
  })
}

const handle = (id) => {
  request.put("/repair/handle/"+id).then(res => {
    if (res.code === '200'){
      store.successMsg(res,"处理成功，等待用户确认")
      setTimeout(()=>{findBySearch()},1000)
    }else {
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