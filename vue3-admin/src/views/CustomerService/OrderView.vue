<template>
  <el-row style="margin-top: 20px">
    <span class="search-span" style="margin-top: 8px">房源名称：</span>
    <el-input placeholder="请输入房源名称" v-model="param.name" clearable style="width: 200px"/>
    <span class="search-span" style="margin-left: 20px;margin-top: 8px;">租赁人姓名：</span>
    <el-input placeholder="请输入租赁人姓名" v-model="param.realName" clearable style="width: 200px"/>
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
      <el-table-column align="center" prop="id" label="ID" width="80px"/>
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
      <el-table-column align="center" prop="dueTime" label="租赁到期时间" width="180px"/>
      <el-table-column align="center" prop="createTime" label="订单创建时间" width="180px"/>
      <el-table-column align="center" prop="paymentTime" label="订单支付时间" width="180px">
        <template #default="scope">
          <div v-if="scope.row.paymentTime !== null">
            {{scope.row.paymentTime}}
          </div>
          <div v-else>暂未支付</div>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" fixed="right"  width="200px">
        <template #default="scope" >
          <el-button
              plain
              type="primary"
              @click="detail(scope.row.id)"> <el-icon><View /></el-icon><span class="button-text">查看详情</span></el-button>
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

  <el-dialog :title="title" v-model="detailVisible" align-center width="600px" :close-on-click-modal="false">
    <el-form>
      <el-row>
        <el-col :span="12">
          <el-form-item label="订单号：">
            {{order.no}}
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="租赁人姓名：">
            {{order.name}}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="手机号：">
            {{order.mobile}}
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="租期：">
            {{order.term}} 个月
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="到期时间：">
            {{order.dueTime}}
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="备注：">
            <span v-if="order.remark !== ''">{{order.remark}}</span>
            <span v-else>无备注</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="实付款：">
            {{order.totalAmount}}
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="订单类型：">
            <el-tag v-if="order.orderType === 1">首次租赁</el-tag>
            <el-tag v-if="order.orderType === 2" type="success">续租</el-tag>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="支付状态：">
            <el-tag v-if="order.payStatus === 0" type="info">等待租户付款</el-tag>
            <el-tag v-if="order.payStatus === 1" type="success">已付款</el-tag>
            <el-tag v-if="order.payStatus === 2" type="warning">超时未付款</el-tag>
            <el-tag v-if="order.payStatus === 3" type="danger">已退款</el-tag>
            <el-tag v-if="order.payStatus === 4" type="danger">已取消订单</el-tag>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="支付时间：" v-if="order.paymentTime !== ''">
            {{order.paymentTime}}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="支付宝交易编号：" v-if="order.alipayNo !== ''">
            {{order.alipayNo}}
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="订单创建时间：">
            {{order.createTime}}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="退款金额：">
            <span v-if="order.returnMoney !== null">{{order.returnMoney}}</span>
            <el-tag v-else type="info">暂未退款</el-tag>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="退款时间：">
            <span v-if="order.returnTime !== null">{{order.returnTime}}</span>
            <el-tag v-else type="info">暂未退款</el-tag>
          </el-form-item>
        </el-col>
      </el-row>
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

const detailVisible = ref(false)
const order = ref({})
const title = ref("")
const detail = (id) => {
  request.get("/order/detail/"+id).then(res => {
    if (res.code === '200') {
      title.value = `房源【${res.data.houseName}】的租赁订单详情`
      order.value = res.data
      detailVisible.value = true
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