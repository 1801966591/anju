<template>
  <el-row style="margin-top: 20px">
    <span class="search-span" style="margin-left: 0">字典类型名称：</span>
    <el-input placeholder="请输入字典类型名称" v-model="param.name" clearable style="width: 200px"/>
    <el-button plain type="primary" style="margin-left:20px;width: 76px;" :icon="Search" @click="findBySearch"><span class="button-text">搜索</span></el-button>
    <el-button plain type="warning" style="margin-left:20px;width: 76px;" @click="resetParam"><i class="el-icon-reset"/><span class="button-text">重置</span></el-button>
  </el-row>

  <el-row style="margin-top: 30px;">
    <el-col>
      <el-button type="primary" plain :icon="Plus" @click="saveVisible = true"><span class="button-text">新增</span></el-button>
      <el-button type="danger" plain @click="delByCodes" :icon="Delete"><span class="button-text">批量删除</span></el-button>
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
      <el-table-column align="center" prop="name" label="字典类型名称" width="220px"/>
      <el-table-column align="center" prop="code" label="字典类型编码" width="220px"/>
      <el-table-column align="center" prop="description" label="字典类型描述" width="400px"/>
      <el-table-column align="center" label="操作" >
        <template #default="scope" >
          <el-button
              v-if="scope.row.code !== 'USER'"
              plain
              type="success"
              @click="showDicValue(scope.row)"><i class="el-icon-dict-value"/> <span class="button-text">查看字典值</span></el-button>
          <el-button
              plain
              type="warning"
              :icon="Edit"
              @click="edit(scope.row)"><span class="button-text">编辑</span></el-button>
          <el-button
              plain
              type="danger"
              :icon="Delete"
              @click="delByCode(scope.row.code)"><span class="button-text">删除</span></el-button>
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

  <el-dialog title="新增字典类型" v-model="saveVisible" width="500px" align-center :before-close="closeForm" :close-on-click-modal="false">
    <el-form :model="form">
      <el-form-item label="字典类型名称" label-width="35%">
        <el-input v-model="form.name" autocomplete="off" style="width: 200px"/>
      </el-form-item>
      <el-form-item label="字典类型编码" label-width="35%">
        <el-input v-model="form.code" autocomplete="off" style="width: 200px"/>
      </el-form-item>
      <el-form-item label="字典类型描述" label-width="35%">
        <el-input v-model="form.description" autocomplete="off" style="width: 200px"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="closeForm">取 消</el-button>
      <el-button type="primary" @click="save">确 定</el-button>
    </template>
  </el-dialog>

  <el-dialog title="修改字典类型" v-model="editVisible" width="500px" align-center :close-on-click-modal="false">
    <el-form :model="form">
      <el-form-item label="字典类型名称" label-width="35%">
        <el-input v-model="form.name" autocomplete="off" style="width: 200px"/>
      </el-form-item>
      <el-form-item label="字典类型编码" label-width="35%">
        <el-input v-model="form.code" autocomplete="off" style="width: 200px" disabled/>
      </el-form-item>
      <el-form-item label="字典类型描述" label-width="35%">
        <el-input v-model="form.description" autocomplete="off" style="width: 200px"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="editVisible = false">取 消</el-button>
      <el-button type="primary" @click="update">确 定</el-button>
    </template>
  </el-dialog>

  <el-dialog :title="title" v-model="valueVisible" width="90%" align-center :close-on-click-modal="false">
    <el-card>
      <DicValue :code="code" v-if="valueVisible"/>
    </el-card>
  </el-dialog>

</template>

<script setup>
import {Delete, Edit, Plus, Search} from "@element-plus/icons-vue";
import {onMounted, reactive, ref} from "vue";
import {useRouter} from "vue-router";
import request from "../../utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import DicValue from "./component/DicValue.vue";
import {dataStore} from "../../stores/dataStore.js";

const user = reactive(localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {})
const param = reactive({
  name: '',
  currentPage: 1,
  pageSize: 5
})
let tableData = ref([])
const total = ref(0)
const router = useRouter()
const store = dataStore()

const findBySearch = () => {
  request.get("/dicType/search",{
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

const saveVisible = ref(false)
const form = ref({
  name: '',
  code: '',
  description: ''
})

const closeForm = () => {
  form.value.name = ''
  form.value.code = ''
  form.value.description = ''
  findBySearch()
  saveVisible.value = false
  editVisible.value = false
}

const save = () => {
  if (form.value.name === '') {
    ElMessage.error("字典类型名称不能为空")
  } else if (form.value.code === '') {
    ElMessage.error("字典类型编码不能为空")
  } else {
    request.post("/dicType/save",form.value).then(res => {
      store.returnMsg(res,'新增成功',router)
      setTimeout(() => {closeForm()},1000)
    })
  }
}

const editVisible = ref(false)

const edit = (type) => {
  form.value.name = type.name
  form.value.code = type.code
  form.value.description = type.description
  editVisible.value = true
}

const update = () => {
  if (form.value.name === '') {
    ElMessage.error("字典类型名称不能为空")
  } else {
    request.put("/dicType/update",form.value).then(res => {
      store.returnMsg(res,'修改成功',router)
      setTimeout(() => {closeForm()},1000)
    })
  }
}

const delByCode = (code) => {
  ElMessageBox.confirm('确定要删除这个字典类型吗', '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '我再想想',
    type: 'warning'
  }).then(() => {
    request.delete("/dicType/deleteByCode/"+code).then(res => {
      store.returnMsg(res,'删除成功',router)
      setTimeout(() => {findBySearch()},1000)
    })
  }).catch(() => {})
}

const codes = ref([])

//获取选中的字典类型信息的code
const handleSelectionChange = (selection) => {
  codes.value = selection.map(item => item.code)
}

const delByCodes = () => {
  if (codes.value.length === 0){
    ElMessage.warning("请选择要删除的字典类型");
  } else {
    let title = ''
    if (codes.value.length === 1){
      title = '确定要删除这个字典类型吗'
    }else {
      title = '确定要删除这些字典类型吗'
    }
    ElMessageBox.confirm(title, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '我再想想',
      type: 'warning'
    }).then(() => {
      request.delete("/dicType/deleteByCodes/"+codes.value).then(res => {
        store.returnMsg(res,'批量删除成功',router)
        setTimeout(() => {findBySearch()},1000)
      })
    }).catch(()=>{})
  }
}

const code = ref('')
const title = ref('')
const valueVisible = ref(false)
const showDicValue = (type) => {
  title.value = `【${type.name}】字典数据`
  code.value = type.code
  valueVisible.value = true
}

onMounted(() => {
  findBySearch()
})

</script>

<style scoped>

</style>