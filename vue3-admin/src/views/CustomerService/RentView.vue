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
      <el-table-column align="center" prop="dueTime" label="租赁到期时间">
        <template #default="scope">
          <el-tag v-if="scope.row.rentStatus === 3" type="warning">已退租</el-tag>
          <div v-else>{{scope.row.dueTime}}</div>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="rentTime" label="租赁时间"/>
      <el-table-column align="center" prop="returnTime" label="退租时间">
        <template #default="scope">
          <div v-if="scope.row.returnTime !== null">
            {{scope.row.returnTime}}
          </div>
          <el-tag v-else>暂未退租</el-tag>
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
</template>

<script setup>
import {Edit, Search, View} from "@element-plus/icons-vue";
import {onMounted, reactive, ref} from "vue";
import {useRouter} from "vue-router";
import request from "../../utils/request.js";
import {dataStore} from "../../stores/dataStore.js";
import HouseDetail from "../House/components/HouseDetail.vue";
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