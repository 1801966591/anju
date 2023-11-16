<template>
  <el-row style="margin-top: 20px">
    <span class="search-span" style="margin-left: 0">用户名：</span>
    <el-input placeholder="请输入用户名" v-model="userParams.username" clearable style="width: 200px"/>
    <span class="search-span">昵称：</span>
    <el-input placeholder="请输入昵称" v-model="userParams.nickname" clearable style="width: 200px;"/>
    <span class="search-span">真实姓名：</span>
    <el-input placeholder="请输入真实姓名" v-model="userParams.realName" clearable style="width: 200px"/>
    <el-button plain type="primary" style="margin-left:20px;width: 76px;" :icon="Search" @click="findBySearch"><span class="button-text">搜索</span></el-button>
    <el-button plain type="warning" style="margin-left:20px;width: 76px;" @click="resetParam"><i class="el-icon-reset"/><span class="button-text">重置</span></el-button>
  </el-row>

  <el-row style="margin-top: 30px;">
    <el-col>
      <el-button type="danger" plain @click="confirmDeleteMultipleUser"><i class="el-icon-disable"/><span class="button-text">批量禁用</span></el-button>
    </el-col>
  </el-row>

  <div style="margin-top: 30px">
    <el-table
        ref="multipleTable"
        :data="tableData"
        tooltip-effect="dark"
        border
        style="width: 100%;"
        @selection-change="handleSelectionChange">
      <el-table-column
          @selection-change="handleSelectionChange"
          type="selection"
          width="55">
      </el-table-column>
      <el-table-column align="center" prop="id" label="ID" width="53"/>
      <el-table-column align="center" prop="username" label="用户名" width="120"/>
      <el-table-column align="center" prop="nickname" label="昵称" width="120"/>
      <el-table-column align="center" prop="realName" label="真实姓名" width="100"/>
      <el-table-column align="center" prop="gender" label="性别" width="60"/>
      <el-table-column align="center" prop="role" label="角色" width="120"/>
      <el-table-column align="center" prop="status" label="状态" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 1" type="success">正常</el-tag>
          <el-tag v-else type="warning">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="createTime" label="创建日期" width="180"/>
      <el-table-column align="center" prop="updateTime" label="修改日期" width="180">
        <template #default="scope">
          <div v-if="scope.row.updateTime !== null">{{scope.row.updateTime}}</div>
          <div v-else>暂未修改</div>
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="200">
        <template #default="scope">
          <el-button plain @click="showDetail(scope.row.id)"  size="small" type="primary">查看</el-button>
          <el-button
              plain
              v-if="scope.row.status === 1"
              type="danger"
              size="small"
              :disabled="scope.row.id === user.id"
              @click="confirmDisableUser(scope.row.id,scope.row.status)">
            禁用</el-button>
          <el-button
              plain
              type="success"
              v-else
              size="small"
              @click="confirmDisableUser(scope.row.id,scope.row.status)">
            启用</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <div style="margin-top: 20px">
    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="userParams.currentPage"
        :page-sizes="[5, 10, 15, 20]"
        :page-size="userParams.pageSize"
        layout="total, sizes, prev, pager, next"
        :total="total">
    </el-pagination>
  </div>



  <el-dialog title="用户信息" v-model="detailVisible" width="600px" :close-on-click-modal="false" align-center>
    <el-row>
      <el-col span="8">
        <div v-if="detail.avatar !== ''">
          <img :src="baseUrl + '/api/files/'+detail.avatar" style="width: 100px;height: 90px;margin-left: 60px;border-radius: 5px" alt="">
        </div>
        <div v-else>
          <el-avatar :src="'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" style="width: 100px;height: 90px;margin-left: 60px;"/>
        </div>
      </el-col>
      <el-col span="16">
        <el-form label-position="left" style="margin-left:50px">
          <el-form-item label="用户名:" class="formItem">
            <span>{{detail.username}}</span>
          </el-form-item>
          <el-form-item label="昵称:" class="formItem">
            <span>{{detail.nickname}}</span>
          </el-form-item>
          <el-form-item label="姓名:" class="formItem">
            <span>{{detail.realName}}</span>
          </el-form-item>
          <el-form-item label="性别:" class="formItem">
            <span>{{detail.gender === 1 ? '男' : '女'}}</span>
          </el-form-item>
          <el-form-item label="手机号码:" class="formItem">
            <span>{{detail.mobile}}</span>
          </el-form-item>
          <el-form-item label="邮箱:" class="formItem">
            <span>{{detail.email}}</span>
          </el-form-item>
          <el-form-item label="角色:" class="formItem">
            <span>{{detail.role}}</span>
          </el-form-item>
          <el-form-item label="状态:" class="formItem">
            <span>{{detail.status === 1 ? '正常' : '禁用'}}</span>
          </el-form-item>
          <el-form-item label="创建时间:" class="formItem">
            <span>{{detail.createTime}}</span>
          </el-form-item>
          <el-form-item label="修改时间:" class="formItem">
            <span>{{detail.updateTime !== null ? detail.updateTime : '暂未修改'}}</span>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </el-dialog>

</template>

<script setup>
import {onMounted, reactive, ref} from "vue";
import {Plus, Search} from "@element-plus/icons-vue";
  import request from "../../utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {useRouter} from "vue-router";
import {dataStore} from "../../stores/dataStore.js";
const baseUrl = ref(import.meta.env.VITE_APP_BASE_URL)
const user = reactive(localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {})
let userParams = reactive({
  username : '',
  nickname : '',
  realName : '',
  currentPage: 1,
  pageSize: 5
})
let tableData = ref([])
const total = ref(0)
const router = useRouter()
const store = dataStore()

const findBySearch = () => {
  request.get("/user/search",{
    params: userParams
  }).then(res => {
    const data = store.searchReturnMsg(res,router)
    if (data) {
      tableData.value = data.tableData
      total.value = data.total
    }
  })
}
const resetParam = () => {
  userParams.username = ''
  userParams.nickname = ''
  userParams.realName = ''
  userParams.currentPage = 1
  userParams.pageSize = 5
  findBySearch()
}
//修改展示条数
const handleSizeChange = (pageSize) => {
  userParams.pageSize = pageSize;
  findBySearch();
}
//修改当前页数
const handleCurrentChange = (currentPage) => {
  userParams.currentPage = currentPage;
  findBySearch();
}
onMounted(() => {
  findBySearch()
})


let detail = ref({
  username : '',
  nickname : '',
  realName : '',
  avatar : '',
  gender : '',
  role : '',
  status : '',
  mobile : '',
  email : '',
  createTime : '',
  updateTime : '',
})
const detailVisible = ref(false)
const showDetail = (id) => {
  request.get("/user/detail/" + id).then(res => {
    if (res.code === '200') {
      detail.value = res.data
      detailVisible.value = true
    } else {
      store.errorMsg(res,router)
    }
  })
}

//禁用用户或启用用户
const confirmDisableUser = (id,status) => {
  let tip = status === 1 ? '禁用' : '启用'
  ElMessageBox.confirm('确定要' + tip + '该用户吗？', '确认' + tip, {
    confirmButtonText: '确定',
    cancelButtonText: '我再想想',
    type: 'warning'
  }).then(() => {
    request.delete("/user/disableUserById/"+id).then(res => {
      if (res.code === '200') {
        let message = res.data === 0 ? '禁用成功' : '启用成功'
        ElMessage({
          message: message,
          type: "success",
          showClose: false,
          duration: 1000,
          onClose : () => {
            findBySearch()
          }
        });
      }else {
        store.errorMsg(res,router)
      }
    })
  }).catch(() => {});
}

const ids = ref([])

//获取选中的用户信息的id
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.id)
}

//批量禁用选中用户
const confirmDeleteMultipleUser = () => {
  if (ids.value.length === 0){
    ElMessage.warning("请选择要禁用的用户");
  }else if(ids.value.includes(user.id)){
    ElMessage.warning("不能禁用当前登录的用户");
  }else {
    let title = ''
    if(ids.value.length === 1){
      title = '确定要禁用这个用户吗？'
    }else {
      title = '确定要禁用这些用户吗？'
    }
    ElMessageBox.confirm(title, '确认禁用', {
      confirmButtonText: '确定',
      cancelButtonText: '我再想想',
      type: 'warning'
    }).then(() => {
      // User clicked '确定', perform delete operation here
      request.delete("/user/disableAdminByIds/"+ids.value).then(res => {
        store.returnMsg(res,'批量禁用成功',router)
        setTimeout(() => {findBySearch()},1000)
      })
    }).catch(() => {
      // User clicked '取消', do nothing or handle as needed
    });
  }
}

</script>

<style scoped>
.search-span{
  margin-left: 20px;
  margin-top: 5px;
}
</style>