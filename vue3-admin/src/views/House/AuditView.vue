<template>

  <el-row style="margin-top: 20px">
    <span class="search-span" style="margin-left: 0">房源名称：</span>
    <el-input placeholder="请输入房源名称" v-model="param.name" clearable style="width: 200px"/>
    <el-button plain type="primary" style="margin-left:20px;width: 76px;" :icon="Search" @click="findBySearch"><span class="button-text">搜索</span></el-button>
    <el-button plain type="warning" style="margin-left:20px;width: 76px;" @click="resetParam"><i class="el-icon-reset"/><span class="button-text">重置</span></el-button>
  </el-row>

  <div style="margin-top: 30px">
    <el-table
        ref="multipleTable"
        :data="tableData"
        tooltip-effect="dark"
        border
        style="width: 100%;">
      <el-table-column align="center" prop="id" label="ID" width="80px"></el-table-column>
      <el-table-column align="center" prop="name" label="房源名称" width="300px"></el-table-column>
      <el-table-column align="center" prop="author" label="发布人" width="120px"></el-table-column>
      <el-table-column align="center" prop="status" label="审核状态" width="140px">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="info">待审核</el-tag>
          <el-tag v-if="scope.row.status === 1" type="success">已通过</el-tag>
          <el-tag v-if="scope.row.status === 2" type="warning">未通过</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="content" label="审核意见" width="400px">
        <template #default="scope">
          <div v-if="scope.row.status === 0">待审核</div>
          <div v-if="scope.row.status === 1">
            <div v-if="scope.row.content === ''">
              无意见
            </div>
            <div v-else>
              {{scope.row.content}}
            </div>
          </div>
          <div v-if="scope.row.status === 2">{{scope.row.content}}</div>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作"  width="256px">
        <template #default="scope" >
          <el-button
              plain
              type="primary"
              :icon="View"
              @click="detail(scope.row.id)"><span class="button-text">查看详情</span></el-button>
          <el-button
              v-if="scope.row.status === 0"
              plain
              type="success"
              @click="openAudit(scope.row)"><i class="el-icon-audit"/> <span class="button-text">审核</span></el-button>
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

  <el-dialog :title="title" v-model="auditVisible" width="500px" align-center :before-close="closeForm" :close-on-click-modal="false">
    <el-form :model="form">
      <el-form-item label="审核状态" label-width="35%">
        <el-radio-group v-model="form.status">
          <el-radio label="1">通过</el-radio>
          <el-radio label="2">不通过</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="审核意见" label-width="35%">
        <el-input type="textarea" :rows="3" v-model="form.content" autocomplete="off" style="width: 250px"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="closeForm">取 消</el-button>
      <el-button type="primary" @click="update">确 定</el-button>
    </template>
  </el-dialog>

  <HouseDetail :id="houseId"/>

</template>

<script setup>
import {Search, View} from "@element-plus/icons-vue";
import {onMounted, reactive, ref} from "vue";
import {useRouter} from "vue-router";
import request from "../../utils/request.js";
import {dataStore} from "../../stores/dataStore.js";
import {ElMessage} from "element-plus";
import HouseDetail from "./components/HouseDetail.vue";

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
  request.get("/houseAudit/search",{
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
  id: '',
  houseId: '',
  status: '',
  content: '',
})

const houseId = ref('')
const detail = (id) => {
  houseId.value = id
  store.DetailVisible = true
}

const auditVisible = ref(false)
const title = ref('')
const openAudit = (data) => {
  title.value = `审核房源【${data.name}】`
  form.value.id = data.id
  form.value.houseId = data.houseId
  auditVisible.value = true
}

const closeForm = () => {
  form.value.status = ''
  form.value.content = ''
  findBySearch()
  auditVisible.value = false
}

const update = () => {
  if (form.value.status === ''){
    ElMessage.error("请选择审核状态")
  } else if (form.value.status === '2' && form.value.content === ''){
    ElMessage.error("请输入审核意见")
  } else {
    request.put("/houseAudit/audit",form.value).then(res => {
      if (res.code === '200') {
        store.successMsg(res,"审核成功")
        setTimeout(() => {
          findBySearch()
          closeForm()
        },1000)
      }else{
        store.errorMsg(res,router)
      }
    })
  }
}

onMounted(() => {
  findBySearch()
})

</script>


<style scoped>

</style>