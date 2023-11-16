<template>

  <el-row style="margin-top: 20px">
    <span class="search-span" style="margin-left: 0">房源名称：</span>
    <el-input placeholder="请输入房源名称" v-model="param.name" clearable style="width: 200px"/>
    <span class="search-span" style="margin-left: 20px">预约人姓名：</span>
    <el-input placeholder="请输入预约人姓名" v-model="param.realName" clearable style="width: 200px"/>
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
      <el-table-column align="center" prop="houseName" label="房源名称(点击可查看详情)" width="200px">
        <template #default="scope">
          <el-link :underline="false" @click="houseDetail(scope.row.houseId)">{{scope.row.houseName}}</el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="name" label="预约人姓名" width="120px"></el-table-column>
      <el-table-column align="center" prop="mobile" label="预约人电话" width="120px"></el-table-column>
      <el-table-column align="center" prop="time" label="预约看房时间" width="180px"></el-table-column>
      <el-table-column align="center" prop="reserveStatus" label="预约状态" width="180px">
        <template #default="scope">
          <el-tag v-if="scope.row.reserveStatus === 0" type="info">未到预约时间</el-tag>
          <el-tag v-if="scope.row.reserveStatus === 1" type="success">已过预约时间</el-tag>
          <el-tag v-if="scope.row.reserveStatus === 2" type="warning">用户已取消预约</el-tag>
          <el-tag v-if="scope.row.reserveStatus === 3" type="warning">房东超时未确认，已取消预约</el-tag>
          <el-tag v-if="scope.row.reserveStatus === 4" type="success">已完成</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="remark" label="备注" width="400px"></el-table-column>
      <el-table-column align="center" prop="createTime" label="预约创建时间" width="180px"></el-table-column>
      <el-table-column align="center" prop="editTime" label="预约修改时间" width="180px">
        <template #default="scope">
          <div v-if="scope.row.updateTime!==null">{{scope.row.updateTime}}</div>
          <div v-else>暂未修改</div>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="status" label="审核状态" width="140px">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="info">待审核</el-tag>
          <el-tag v-if="scope.row.status === 1" type="success">同意</el-tag>
          <el-tag v-if="scope.row.status === 2" type="warning">拒绝</el-tag>
          <el-tag v-if="scope.row.status === 3" type="warning">超时未确认</el-tag>
          <el-tag v-if="scope.row.status === 4" type="warning">用户已取消预约</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="content" label="拒绝原因" width="400px">
        <template #default="scope">
          <div v-if="scope.row.status === 2">{{scope.row.content}}</div>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" fixed="right"  width="150px">
        <template #default="scope" >
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
          <el-radio label="1">同意</el-radio>
          <el-radio label="2">拒绝</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="拒绝原因" label-width="35%" v-show="form.status === '2'">
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
import HouseDetail from "../House/components/HouseDetail.vue";

const user = reactive(localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {})
const param = reactive({
  name: '',
  realName: '',
  currentPage: 1,
  pageSize: 5
})
let tableData = ref([])
const total  = ref(0)
const router = useRouter()
const store = dataStore();

const findBySearch = () => {
  request.get("/reserveAudit/search",{
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
  param.realName = ''
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
  status: 0,
  content: '',
})

const houseId = ref('')
const houseDetail = (id) => {
  houseId.value = id
  store.DetailVisible = true
}

const auditVisible = ref(false)
const title = ref('')
const openAudit = (data) => {
  title.value = `审核房源【${data.houseName}】的预约`
  form.value.id = data.id
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
    request.put("/reserveAudit/audit",form.value).then(res => {
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