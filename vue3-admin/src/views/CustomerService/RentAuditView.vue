<template>
  <el-row style="margin-top: 20px">
    <span class="search-span" style="margin-top: 8px">房源名称：</span>
    <el-input placeholder="请输入房源名称" v-model="param.name" clearable style="width: 200px"/>
    <span class="search-span" style="margin-left: 20px;margin-top: 8px;">申请人姓名：</span>
    <el-input placeholder="请输入申请人姓名" v-model="param.realName" clearable style="width: 200px"/>
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
      <el-table-column align="center" prop="id" label="ID" width="80px"></el-table-column>
      <el-table-column align="center" prop="houseName" label="房源名称(点击可查看详情)" width="200px">
        <template #default="scope">
          <el-link :underline="false" @click="houseDetail(scope.row.houseId)">{{scope.row.houseName}}</el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="remark" label="备注" width="400px"></el-table-column>
      <el-table-column align="center" prop="applyType" label="申请类型" width="140px">
        <template #default="scope">
          <el-tag v-if="scope.row.applyType === 1">首次租赁</el-tag>
          <el-tag v-if="scope.row.applyType === 2" type="success">续租</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="auditStatus" label="审核状态" width="140px">
        <template #default="scope">
          <el-tag v-if="scope.row.auditStatus === 0" type="info">待审核</el-tag>
          <el-tag v-if="scope.row.auditStatus === 1" type="success">同意</el-tag>
          <el-tag v-if="scope.row.auditStatus === 2" type="warning">拒绝</el-tag>
          <el-tag v-if="scope.row.auditStatus === 3" type="warning">超时未确认</el-tag>
          <el-tag v-if="scope.row.auditStatus === 4" type="warning">用户已取消申请</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="content" label="拒绝原因" width="400px">
        <template #default="scope">
          <div v-if="scope.row.status === 2">{{scope.row.content}}</div>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="applyTime" label="租赁申请时间" width="180px"></el-table-column>
      <el-table-column align="center" prop="updateTime" label="修改时间" width="180px">
        <template #default="scope">
          <div v-if="scope.row.updateTime!==null">{{scope.row.updateTime}}</div>
          <div v-else>暂未修改</div>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" fixed="right"  width="350px">
        <template #default="scope" >
          <el-button
              v-if="scope.row.auditStatus !== 3 && scope.row.auditStatus !== 4"
              plain
              type="primary"
              @click="detail(scope.row)"> <el-icon><View /></el-icon><span class="button-text">查看详情</span></el-button>
          <el-button
              v-if="scope.row.auditStatus === 0"
              plain
              type="success"
              @click="openAudit(scope.row)"><i class="el-icon-audit"/><span class="button-text">审核</span></el-button>
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

  <el-dialog title="申请详情" v-model="detailVisible" align-center width="400px" :close-on-click-modal="false">
    <el-form style="margin-left: 100px">
      <el-form-item label="姓名：">
        {{rentForm.name}}
      </el-form-item>
      <el-form-item label="手机号：">
        {{rentForm.mobile}}
      </el-form-item>
      <el-form-item label="租期：">
        {{rentForm.term}} 个月
      </el-form-item>
      <el-form-item label="备注：">
        {{rentForm.remark}}
      </el-form-item>
    </el-form>
  </el-dialog>

  <el-dialog :title="title" v-model="auditVisible" width="500px" align-center :before-close="closeForm" :close-on-click-modal="false">
    <el-form :model="form">
      <el-form-item label="审核状态" label-width="35%">
        <el-radio-group v-model="form.auditStatus">
          <el-radio label="1">同意</el-radio>
          <el-radio label="2">拒绝</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="拒绝原因" label-width="35%" v-show="form.auditStatus === '2'">
        <el-input type="textarea" :rows="3" v-model="form.reason" autocomplete="off" style="width: 250px"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="closeForm">取 消</el-button>
      <el-button type="primary" @click="audit">确 定</el-button>
    </template>
  </el-dialog>

</template>

<script setup>
import {Edit, Search, View} from "@element-plus/icons-vue";
import {onMounted, reactive, ref} from "vue";
import {useRouter} from "vue-router";
import request from "../../utils/request.js";
import {dataStore} from "../../stores/dataStore.js";
import {ElMessage, ElMessageBox} from "element-plus";
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
  request.get("/rentAudit/search",{
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

const houseId = ref('')
const houseDetail = (id) => {
  houseId.value = id
  store.DetailVisible = true
}

const rentForm = reactive({
  id: '',
  name: '',
  mobile: '',
  term: '',
  remark: '',
  reserveStatus: '',
})

const detailVisible = ref(false)
const detail = (rent) => {
  rentForm.name = rent.name
  rentForm.mobile = rent.mobile
  rentForm.term = rent.term
  rentForm.remark = rent.remark
  detailVisible.value = true
}

const form = ref({
  id: '',
  auditStatus: 0,
  reason: '',
})
const auditVisible = ref(false)
const title = ref('')
const openAudit = (data) => {
  title.value = `审核【${data.name}】的租赁申请`
  form.value.id = data.id
  auditVisible.value = true
}

const closeForm = () => {
  form.value.status = ''
  form.value.content = ''
  findBySearch()
  auditVisible.value = false
}

const audit = () => {
  if (form.value.status === ''){
    ElMessage.error("请选择审核状态")
  } else if (form.value.status === '2' && form.value.content === ''){
    ElMessage.error("请输入审核意见")
  } else {
    request.put("/rentAudit/audit",form.value).then(res => {
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
::v-deep input::-webkit-outer-spin-button,
::v-deep input::-webkit-inner-spin-button {
  -webkit-appearance: none !important;
}
::v-deep input[type="number"] {
  -moz-appearance: textfield; /* 此处写不写都可以 */
}
</style>