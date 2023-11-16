<template>

  <el-row style="margin-top: 20px">
    <span class="search-span" style="margin-left: 0">公告标题：</span>
    <el-input placeholder="请输入公告标题" v-model="param.title" clearable style="width: 200px"/>
    <el-button plain type="primary" style="margin-left:20px;width: 76px;" :icon="Search" @click="findBySearch"><span class="button-text">搜索</span></el-button>
    <el-button plain type="warning" style="margin-left:20px;width: 76px;" @click="resetParam"><i class="el-icon-reset"/><span class="button-text">重置</span></el-button>
  </el-row>

  <el-row style="margin-top: 30px;">
    <el-col>
      <el-button type="primary" plain :icon="Plus" @click="add"><span class="button-text">新增</span></el-button>
      <el-button type="danger" plain @click="delByIds" :icon="Delete"><span class="button-text">批量删除</span></el-button>
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
      <el-table-column align="center" prop="id" label="ID" width="80px"></el-table-column>
      <el-table-column align="center" prop="title" label="公告标题"></el-table-column>
      <el-table-column align="center" prop="content" label="公告内容" width="405px"></el-table-column>
      <el-table-column align="center" prop="time" label="更新时间" width="200px"></el-table-column>
      <el-table-column align="center" label="操作"  width="200px">
        <template #default="scope" >
          <el-button
              plain
              type="warning"
              :icon="Edit"
              @click="edit(scope.row)"><span class="button-text">编辑</span></el-button>
          <el-button
              plain
              type="danger"
              :icon="Delete"
              @click="delById(scope.row.id)"><span class="button-text">删除</span></el-button>
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

  <el-dialog :title="title" v-model="formVisible" width="500px" align-center :before-close="closeForm" :close-on-click-modal="false">
    <el-form :model="form">
      <el-form-item label="公告标题" label-width="35%">
        <el-input v-model="form.title" autocomplete="off" style="width: 200px"/>
      </el-form-item>
      <el-form-item label="公告内容" label-width="35%">
        <el-input type="textarea" :rows="3" v-model="form.content" autocomplete="off" style="width: 250px"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="closeForm">取 消</el-button>
      <el-button type="primary" @click="submit">确 定</el-button>
    </template>
  </el-dialog>

</template>

<script setup>
import {Delete, Edit, Plus, Search} from "@element-plus/icons-vue";
import {onMounted, reactive, ref} from "vue";
import {useRouter} from "vue-router";
import request from "../../utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {dataStore} from "../../stores/dataStore.js";

const user = reactive(localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {})
const param = reactive({
  title: '',
  currentPage: 1,
  pageSize: 5
})
let tableData = ref([])
const total  = ref(0)
const router = useRouter()
const store = dataStore();

const findBySearch = () => {
  request.get("/notice/search",{
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
  param.title = ''
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
  title: '',
  content: '',
})
const title = ref('')
const formVisible = ref(false)

const closeForm = () => {
  form.value.title = ''
  form.value.content = ''
  findBySearch()
  formVisible.value = false
}

const add = () => {
  title.value = '新增公告信息'
  formVisible.value = true
}

const edit = (notice) => {
  form.value.id = notice.id
  form.value.title = notice.title
  form.value.content = notice.content
  title.value = '修改公告信息'
  formVisible.value = true
}
const submit = () => {
  if (form.value.title === ''){
    ElMessage.error("公告标题不能为空")
  } else if (form.value.content === ''){
    ElMessage.error("公告内容不能为空")
  } else {
    if (form.value.id === ''){
      //新增
      request.post("/notice/save",form.value).then(res => {
        store.returnMsg(res,'新增公告信息成功',router)
        setTimeout(() => {
          closeForm()
        },1000)
      })
    } else {
      //修改
      request.put("/notice/update",form.value).then(res => {
        store.returnMsg(res,'修改公告信息成功',router)
        setTimeout(() => {
          closeForm()
        },1000)
      })
    }
  }
}

const delById = (id) => {
  ElMessageBox.confirm('确定要删除这条公告吗', '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '我再想想',
    type: 'warning'
  }).then(() => {
    request.delete("/notice/deleteById/"+id).then(res => {
      store.returnMsg(res,'删除公告信息成功',router)
      setTimeout(() => {findBySearch()},1000)
    })
  }).catch(() => {})
}

const ids = ref([])

//获取选中的菜单信息的code
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.id)
}

const delByIds = () => {
  if (ids.value.length === 0){
    ElMessage.warning("请选择要删除的公告");
  } else {
    let title = ''
    if (ids.value.length === 1){
      title = '确定要删除这条公告吗'
    }else {
      title = '确定要删除这些公告吗'
    }
    ElMessageBox.confirm(title, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '我再想想',
      type: 'warning'
    }).then(() => {
      request.delete("/notice/deleteByIds/"+ids.value).then(res => {
        store.returnMsg(res,'批量删除公告成功',router)
        setTimeout(() => {findBySearch()},1000)
      })
    }).catch(()=>{})
  }
}

onMounted(() => {
  findBySearch()
})

</script>


<style scoped>

</style>