<template>
  <div style="margin-left: 150px;width: 1300px;margin-top: 50px;font-family: 'PingFang SC',sans-serif">
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
        <el-table-column align="center" prop="id" label="ID" width="80px"></el-table-column>
        <el-table-column align="center" prop="houseName" label="房源名称(点击可查看详情)" width="200px">
          <template #default="scope">
            <el-link :underline="false" @click="houseDetail(scope.row.houseId)">{{scope.row.houseName}}</el-link>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="remark" label="备注" width="300px"/>
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
                v-if="scope.row.auditStatus === 0 || scope.row.auditStatus === 2"
                plain
                type="warning"
                @click="edit(scope.row)"> <el-icon><Edit /></el-icon><span class="button-text">修改</span></el-button>
            <el-button
                v-if="scope.row.auditStatus === 0 || scope.row.auditStatus === 2"
                plain
                type="danger"
                @click="cancelApply(scope.row.id)"><i class="el-icon-disable"/> <span class="button-text">取消申请</span></el-button>
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
  </div>

  <el-dialog title="修改租赁申请（租期最少为1个月）" v-model="editVisible" align-center width="520px" :close-on-click-modal="false">
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
          <el-button type="primary" class="reserveButton" @click="update">
            <span style="font-family: 'PingFang SC',sans-serif;font-size: 16px">修改</span>
          </el-button>
        </el-row>
        <el-row style="margin-top: 10px">
          <span style="margin-left: 10px;font-size: 16px;font-family: 'PingFang SC',sans-serif">房东将在24小时内确认，如果未确认，将自动为您取消申请</span>
        </el-row>
      </template>
    </el-footer>
  </el-dialog>

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
const total  = ref(0)
const router = useRouter()
const store = dataStore();

const findBySearch = () => {
  param.userId = user.id
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


const editVisible = ref(false)
const edit = (rent) => {
  rentForm.id = rent.id
  rentForm.name = rent.name
  rentForm.mobile = rent.mobile
  rentForm.term = rent.term
  rentForm.remark = rent.remark
  editVisible.value = true
}

const update = () => {
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
    request.put("/rentAudit/update",rentForm).then(res => {
      if (res.code === '200'){
        store.successMsg(res,"提交租赁申请成功，请等待房东确认")
        setTimeout(()=>{
          findBySearch()
          editVisible.value = false
        },1000)
      } else {
        store.errorMsg(res,router)
      }
    })
  }
}

const cancelApply = (id) => {
  ElMessageBox.confirm('确定要取消申请吗', '确认取消', {
    confirmButtonText: '确定',
    cancelButtonText: '我再想想',
    type: 'warning'
  }).then(() => {
    request.delete("/rentAudit/cancelApply/"+id).then(res => {
      store.returnMsg(res,'取消成功',router)
      setTimeout(() => {findBySearch()},1000)
    })
  }).catch(() => {})
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