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
        <el-table-column align="center" prop="updateTime" label="预约修改时间" width="180px">
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
        <el-table-column align="center" label="操作" fixed="right"  width="250px">
          <template #default="scope" >
            <el-button
                v-if="scope.row.reserveStatus === 0"
                plain
                type="warning"
                @click="edit(scope.row)"> <el-icon><Edit /></el-icon><span class="button-text">修改</span></el-button>
            <el-button
                v-if="scope.row.reserveStatus === 0"
                plain
                type="danger"
                @click="cancelReserve(scope.row.id)"><i class="el-icon-disable"/> <span class="button-text">取消预约</span></el-button>
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

  <el-dialog title="修改预约信息" v-model="editVisible" align-center width="520px" :close-on-click-modal="false">
    <el-form>
      <el-form-item label="姓名">
        <el-input clearable class="reserveInput" style="margin-left: 28px" v-model="reserveForm.name" placeholder="请输入姓名"/>
      </el-form-item>
      <el-form-item label="手机号">
        <el-input clearable class="reserveInput" style="margin-left: 14px" type="number" v-model="reserveForm.mobile" placeholder="请输入手机号"/>
      </el-form-item>
      <el-form-item label="看房时间">
        <el-date-picker
            clearable
            style="height: 40px;width: 450px"
            v-model="reserveForm.time"
            type="datetime"
            placeholder="选择看房时间"
        />
      </el-form-item>
      <el-form-item label="备注">
        <el-input clearable type="textarea" style="margin-left: 28px;width: 450px" v-model="reserveForm.remark" placeholder="请输入备注"/>
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
          <span style="margin-left: 10px;font-size: 16px;font-family: 'PingFang SC',sans-serif">房东将在24小时内确认，如果未确认，将自动为您取消预约</span>
        </el-row>
      </template>
    </el-footer>
  </el-dialog>

</template>

<script setup>
import {Delete, Edit, Search, View} from "@element-plus/icons-vue";
import {onMounted, reactive, ref} from "vue";
import {useRouter} from "vue-router";
import request from "../../utils/request.js";
import {dataStore} from "../../stores/dataStore.js";
import {ElMessage, ElMessageBox} from "element-plus";
import HouseDetail from "./components/HouseDetail.vue";
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

const reserveForm = reactive({
  id: '',
  name: '',
  mobile: '',
  time: '',
  remark: '',
  reserveStatus: '',
})
const editVisible = ref(false)
const edit = (reserve) => {
  reserveForm.id = reserve.id
  reserveForm.name = reserve.name
  reserveForm.mobile = reserve.mobile
  reserveForm.time = reserve.time
  reserveForm.remark = reserve.remark
  reserveForm.reserveStatus = reserve.reserveStatus
  editVisible.value = true
}

const update = () => {
  if (reserveForm.name === '') {
    ElMessage.error("请输入姓名")
  } else if (reserveForm.mobile === '') {
    ElMessage.error("请输入手机号")
  } else if (!reserveForm.mobile.match(/^1[34578]\d{9}$/)){
    ElMessage.error("请输入正确的手机号")
  } else if (reserveForm.time === '') {
    ElMessage.error("请选择看房时间")
  } else if (reserveForm.time < Date.now()) {
    ElMessage.error("看房时间不能早于当前时间")
  } else if (reserveForm.time - Date.now() < 48 * 60 * 60 * 1000) {
    // 如果预约时间在当前时间之后，但是距离当前时间不足48小时
    ElMessage.error("请提前至少两天进行预约");
  } else {
    reserveForm.time = formatTimeToDateTime(reserveForm.time)
    request.put("/reserve/update",reserveForm).then(res => {
      if (res.code === '200'){
        store.successMsg(res,"修改预约看房成功，请等待房东确认")
        setTimeout(()=>{editVisible.value = false},1000)
      } else {
        store.errorMsg(res,router)
        if (res.code === '401'){
          editVisible.value = false
        }
      }
    })
  }
}

const cancelReserve = (id) => {
  ElMessageBox.confirm('确定要取消预约吗', '确认取消', {
    confirmButtonText: '确定',
    cancelButtonText: '我再想想',
    type: 'warning'
  }).then(() => {
    request.delete("/reserve/cancelReserve/"+id).then(res => {
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