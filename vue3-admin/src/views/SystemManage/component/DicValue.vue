<template>
  <el-row style="margin-top: 20px">
    <span class="search-span" style="margin-left: 0">字典值名称：</span>
    <el-input placeholder="请输入字典值名称" v-model="param.name" clearable style="width: 200px"/>
    <el-button plain type="primary" style="margin-left:20px;width: 76px;" :icon="Search" @click="findBySearch"><span class="button-text">搜索</span></el-button>
    <el-button plain type="warning" style="margin-left:20px;width: 76px;" @click="resetParam"><i class="el-icon-reset"/><span class="button-text">重置</span></el-button>
  </el-row>

  <el-row style="margin-top: 30px;">
    <el-col>
      <el-button type="primary" plain :icon="Plus" @click="saveVisible = true"><span class="button-text">新增</span></el-button>
      <el-button type="danger" plain @click="delByIds" :icon="Delete"><span class="button-text">批量删除</span></el-button>
    </el-col>
  </el-row>

  <div style="margin-top: 30px">
    <el-table
        ref="multipleTable"
        :data="tableData"
        tooltip-effect="dark"
        border
        height="284"
        style="width: 100%;"
        @selection-change="handleSelectionChange">
      <el-table-column
          @selection-change="handleSelectionChange"
          type="selection"
          width="55">
      </el-table-column>
      <el-table-column align="center" prop="id" label="ID" width="60px"/>
      <el-table-column align="center" prop="name" label="字典名称" width="220px"/>
      <el-table-column align="center" prop="value" label="字典值" width="220px">
        <template #default="scope">
          <div v-if="/icon/.test(props.code)">
            <i :class="scope.row.value" style="font-size: 24px"/>
          </div>
          <div v-else>{{scope.row.value}}</div>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="code" label="字典类型编码" width="220px"/>
      <el-table-column align="center" label="操作" >
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

  <el-dialog title="新增字典数据" v-model="saveVisible" width="500px" align-center :before-close="closeForm" :close-on-click-modal="false">
    <el-form :model="form">
      <el-form-item label="字典名称" label-width="35%">
        <el-input v-model="form.name" autocomplete="off" style="width: 200px"/>
      </el-form-item>
      <el-form-item label="字典值" label-width="35%">
        <el-input v-model="form.value" autocomplete="off" style="width: 200px"/>
      </el-form-item>
      <el-form-item label="字典类型编码" label-width="35%">
        <el-input v-model="form.code" autocomplete="off" style="width: 200px" disabled/>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="closeForm">取 消</el-button>
      <el-button type="primary" @click="save">确 定</el-button>
    </template>
  </el-dialog>

  <el-dialog title="修改字典数据" v-model="editVisible" width="500px" align-center :close-on-click-modal="false">
    <el-form :model="form">
      <el-form-item label="字典名称" label-width="35%">
        <el-input v-model="form.name" autocomplete="off" style="width: 200px"/>
      </el-form-item>
      <el-form-item label="字典值" label-width="35%">
        <el-input v-model="form.value" autocomplete="off" style="width: 200px"/>
      </el-form-item>
      <el-form-item label="字典类型编码" label-width="35%">
        <el-input v-model="form.code" autocomplete="off" style="width: 200px" disabled/>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="editVisible = false">取 消</el-button>
      <el-button type="primary" @click="update">确 定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import {Delete, Edit, Plus, Search} from "@element-plus/icons-vue";
import {onMounted, reactive, ref} from "vue";
import {useRouter} from "vue-router";
import request from "../../../utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {dataStore} from "../../../stores/dataStore.js";

const props = defineProps(['code'])
const user = reactive(localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {})
const param = reactive({
  name: '',
  code: props.code,
  currentPage: 1,
  pageSize: 5
})
let tableData = ref([])
const total = ref(0)
const router = useRouter()
const store = dataStore()


const findBySearch = () => {
  request.get("/dicValue/search",{
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
  id: '',
  name: '',
  value: '',
  code: props.code,
  
})

const closeForm = () => {
  form.value.name = ''
  form.value.value = ''
  findBySearch()
  saveVisible.value = false
  editVisible.value = false
}

const save = () => {
  if (form.value.name === '') {
    ElMessage.error("字典名称不能为空")
  } else if (form.value.value === '') {
    ElMessage.error("字典值不能为空")
  } else {
    request.post("/dicValue/save",form.value).then(res => {
      store.returnMsg(res,'新增成功',router)
      setTimeout(() => {closeForm()},1000)
    })
  }
}

const editVisible = ref(false)

const edit = (value) => {
  form.value.id = value.id
  form.value.name = value.name
  form.value.value = value.value
  editVisible.value = true
}

const update = () => {
  if (form.value.name === '') {
    ElMessage.error("字典名称不能为空")
  } else if (form.value.value === '') {
    ElMessage.error("字典值不能为空")
  } else {
    request.put("/dicValue/update",form.value).then(res => {
      store.returnMsg(res,'修改成功',router)
      setTimeout(() => {closeForm()},1000)
    })
  }
}

const delById = (id) => {
  ElMessageBox.confirm('确定要删除这个字典数据吗', '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '我再想想',
    type: 'warning'
  }).then(() => {
    request.delete("/dicValue/deleteById/"+id).then(res => {
      store.returnMsg(res,'删除成功',router)
      setTimeout(() => {findBySearch()},1000)
    })
  }).catch(() => {})
}

const ids = ref([])

//获取选中的字典数据的code
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.id)
}

const delByIds = () => {
  if (ids.value.length === 0){
    ElMessage.warning("请选择要删除的字典类型");
  } else {
    let title = ''
    if (ids.value.length === 1){
      title = '确定要删除这个字典数据吗'
    }else {
      title = '确定要删除这些字典数据吗'
    }
    ElMessageBox.confirm(title, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '我再想想',
      type: 'warning'
    }).then(() => {
      request.delete("/dicValue/deleteByIds/"+ids.value).then(res => {
        store.returnMsg(res,'批量删除成功',router)
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