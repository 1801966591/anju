<template>
  <el-row style="margin-top: 20px">
    <span class="search-span" style="margin-left: 0">操作名称：</span>
    <el-input placeholder="请输入操作名称" v-model="param.name" clearable style="width: 200px"/>
    <el-button plain type="primary" style="margin-left:20px;width: 76px;" :icon="Search" @click="findBySearch"><span class="button-text">搜索</span></el-button>
    <el-button plain type="warning" style="margin-left:20px;width: 76px;" @click="resetParam"><i class="el-icon-reset"/><span class="button-text">重置</span></el-button>
  </el-row>

  <div style="margin-top: 30px">
    <el-table ref="multipleTable" :data="tableData" height="439px" tooltip-effect="dark" border style="width: 100%;">
      <el-table-column align="center" prop="id" label="ID" width="80px"></el-table-column>
      <el-table-column align="center" prop="name" label="操作名称"></el-table-column>
      <el-table-column align="center" prop="username" label="操作人"></el-table-column>
      <el-table-column align="center" prop="ip" label="操作人ip"></el-table-column>
      <el-table-column align="center" prop="time" label="操作时间"></el-table-column>
    </el-table>
  </div>
  <div style="margin-top: 20px">
    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="param.currentPage"
        :page-sizes="[10, 15, 20, 25]"
        :page-size="param.pageSize"
        layout="total, sizes, prev, pager, next"
        :total="total">
    </el-pagination>
  </div>

</template>

<script setup>
import {Search} from "@element-plus/icons-vue";
import {onMounted, reactive, ref} from "vue";
import {useRouter} from "vue-router";
import request from "../../utils/request.js";
import {ElMessage} from "element-plus";
import {dataStore} from "../../stores/dataStore.js";
const param = reactive({
  name: '',
  currentPage: 1,
  pageSize: 10
})
let tableData = ref([])
const total = ref(0)
const router = useRouter()
const store = dataStore()

const findBySearch = () => {
  request.get("/log/search",{
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
  param.pageSize = 10
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

onMounted(() => {
  findBySearch()
})

</script>

<style scoped>

</style>