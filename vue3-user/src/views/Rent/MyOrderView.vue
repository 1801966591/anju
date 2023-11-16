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
        <el-table-column align="center" prop="no" label="订单号" width="220px"/>
        <el-table-column align="center" prop="houseName" label="房源名称(点击可查看详情)" width="200px">
          <template #default="scope">
            <el-link :underline="false" @click="houseDetail(scope.row.houseId)">{{scope.row.houseName}}</el-link>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="orderType" label="订单类型" width="140px">
          <template #default="scope">
            <el-tag v-if="scope.row.orderType === 1">首次租赁</el-tag>
            <el-tag v-if="scope.row.orderType === 2" type="success">续租</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="payStatus" label="订单状态" width="140px">
          <template #default="scope">
            <el-tag v-if="scope.row.payStatus === 0" type="info">等待租户付款</el-tag>
            <el-tag v-if="scope.row.payStatus === 1" type="success">已付款</el-tag>
            <el-tag v-if="scope.row.payStatus === 2" type="warning">超时未付款</el-tag>
            <el-tag v-if="scope.row.payStatus === 3" type="danger">已退款</el-tag>
            <el-tag v-if="scope.row.payStatus === 4" type="danger">已取消订单</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="totalAmount" label="实付款" width="100px"/>
        <el-table-column align="center" prop="dueTime" label="租赁到期时间" width="180px">
          <template #default="scope">
            <el-tag v-if="scope.row.payStatus === 0" type="info">等待租户付款</el-tag>
            <div v-if="scope.row.payStatus === 1" type="success">{{scope.row.dueTime}}</div>
            <el-tag v-if="scope.row.payStatus === 2" type="warning">超时未付款</el-tag>
            <el-tag v-if="scope.row.payStatus === 3" type="danger">已退款</el-tag>
            <el-tag v-if="scope.row.payStatus === 4" type="danger">已取消订单</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="createTime" label="订单创建时间" width="180px"/>
        <el-table-column align="center" prop="paymentTime" label="订单支付时间" width="180px">
          <template #default="scope">
            <div v-if="scope.row.paymentTime !== null">
              {{scope.row.paymentTime}}
            </div>
            <el-tag v-else type="info">暂未支付</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="returnMoney" label="订单退款金额" width="140px">
          <template #default="scope">
            <div v-if="scope.row.returnMoney !== null">
              {{scope.row.returnMoney}}
            </div>
            <el-tag v-else type="info">暂未退款</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="returnTime" label="订单退款时间" width="180px">
          <template #default="scope">
            <div v-if="scope.row.returnTime !== null">
              {{scope.row.returnTime}}
            </div>
            <el-tag v-else type="info">暂未退款</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作" fixed="right"  width="300px">
          <template #default="scope" >
            <el-button
                v-if="scope.row.payStatus === 0"
                type="primary"
                @click="pay(scope.row)"><span class="button-text">立即付款</span></el-button>
            <div v-if="scope.row.payStatus === 0">
              <el-link :underline="false" @click="cancelOrder(scope.row.id)"><span style="font-size: 8px;font-family: 'PingFang SC',sans-serif">取消订单</span></el-link>
            </div>
            <el-tag type="info" v-if="scope.row.payStatus === 1">如需退款，请到租赁管理页面进行退租操作</el-tag>
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
  request.get("/order/search",{
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

const pay = (data) => {
  window.open(`${import.meta.env.VITE_APP_BASE_URL}/api/alipay/pay?subject=${"房源【" + data.houseName + "】租金"}&traceNo=${data.no}&totalAmount=${data.totalAmount}`)
}

const cancelOrder = (id) => {
  ElMessageBox.confirm('确定要取消该订单吗', '取消订单', {
    confirmButtonText: '确定',
    cancelButtonText: '我再想想',
    type: 'warning'   }).then(() => {
    request.delete("/order/cancelOrder/"+id).then(res =>{
      if (res.code === '200'){
        store.successMsg(res,"订单取消成功")
        setTimeout(()=>{findBySearch()},1000)
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