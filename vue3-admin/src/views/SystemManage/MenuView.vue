<template>
  <el-row style="margin-top: 20px">
    <span class="search-span" style="margin-left: 0">菜单名称：</span>
    <el-input placeholder="请输入菜单名称" v-model="param.name" clearable style="width: 200px"/>
    <el-button plain type="primary" style="margin-left:20px;width: 76px;" :icon="Search" @click="findBySearch"><span class="button-text">搜索</span></el-button>
    <el-button plain type="warning" style="margin-left:20px;width: 76px;" @click="resetParam"><i class="el-icon-reset"/><span class="button-text">重置</span></el-button>
  </el-row>

  <el-row style="margin-top: 30px;">
    <el-col>
      <el-button type="primary" plain :icon="Plus" @click="addMenu(null)"><span class="button-text">新增</span></el-button>
      <el-button type="danger" plain @click="delByIds" :icon="Delete"><span class="button-text">批量删除</span></el-button>
    </el-col>
  </el-row>

  <div style="margin-top: 30px">
    <el-table
        ref="multipleTable"
        :data="tableData"
        tooltip-effect="dark"
        border
        default-expand-all
        height="450px"
        row-key="id"
        style="width: 100%;"
        @selection-change="handleSelectionChange">
      <el-table-column
          @selection-change="handleSelectionChange"
          type="selection"
          width="55">
      </el-table-column>
      <el-table-column align="center" prop="id" label="ID" width="80px"/>
      <el-table-column align="center" prop="name" width="100px" label="菜单名称"/>
      <el-table-column align="center" prop="path" width="140px" label="路径"/>
      <el-table-column align="center" prop="pagePath" width="260px"  label="页面路径"/>
      <el-table-column align="center" prop="icon" label="图标" width="80">
        <template #default="scope">
          <i :class="scope.row.icon" style="font-size: 25px"/>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="description" width="150px" label="描述"></el-table-column>
      <el-table-column align="center" label="操作" width="340px">
        <template #default="scope" >
          <el-button
              plain
              :icon="Plus"
              v-if="scope.row.pid === null && scope.row.path === ''"
              type="success"
              @click="addMenu(scope.row.id)"><span class="button-text">新增子菜单</span></el-button>
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

  <el-dialog :title="title" v-model="saveVisible" width="500px" align-center :before-close="closeForm" :close-on-click-modal="false">
    <el-form :model="form">
      <el-form-item label="菜单名称" label-width="35%">
        <el-input v-model="form.name" autocomplete="off" style="width: 200px"/>
      </el-form-item>
      <el-form-item label="路径" label-width="35%">
        <el-input v-model="form.path" autocomplete="off" style="width: 200px"/>
      </el-form-item>
      <el-form-item label="页面路径" label-width="35%">
        <el-input v-model="form.pagePath" autocomplete="off" style="width: 200px"/>
      </el-form-item>
      <el-form-item label="图标" label-width="35%">
        <el-select placeholder="请选择图标" v-model="form.icon" style="width: 200px">
          <template #prefix>
            <i :class="form.icon" style="font-size: 25px"/>
          </template>
          <el-option
              v-for="item in icons"
              :key="item.name"
              :label="item.name"
              :value="item.value">
            <i :class="item.value"/>  {{item.name}}
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="描述" label-width="35%">
        <el-input v-model="form.description" autocomplete="off" style="width: 200px"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="closeForm">取 消</el-button>
      <el-button type="primary" @click="saveMenu">确 定</el-button>
    </template>
  </el-dialog>

  <el-dialog title="修改菜单" v-model="editVisible" width="500px" align-center :close-on-click-modal="false">
    <el-form :model="form">
      <el-form-item label="菜单名称" label-width="35%">
        <el-input v-model="form.name" autocomplete="off" style="width: 200px"/>
      </el-form-item>
      <el-form-item label="路径" label-width="35%">
        <el-input v-model="form.path" autocomplete="off" style="width: 200px"/>
      </el-form-item>
      <el-form-item label="页面路径" label-width="35%">
        <el-input v-model="form.pagePath" autocomplete="off" style="width: 200px"/>
      </el-form-item>
      <el-form-item label="图标" label-width="35%">
        <el-select placeholder="请选择图标" v-model="form.icon" style="width: 200px">
          <template #prefix>
            <i :class="form.icon" style="font-size: 25px"/>
          </template>
          <el-option
              v-for="item in icons"
              :key="item.name"
              :label="item.name"
              :value="item.value">
            <i :class="item.value"/>  {{item.name}}
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="描述" label-width="35%">
        <el-input v-model="form.description" autocomplete="off" style="width: 200px"/>
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
import request from "../../utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {dataStore} from "../../stores/dataStore.js";
const env = ref(import.meta.env.VITE_APP_ENV)
const user = reactive(localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {})
const param = reactive({
  name: '',
  currentPage: 1,
  pageSize: 5
})
let tableData = ref([])
const router = useRouter()
const store = dataStore()

const findBySearch = () => {
  request.get("/menu/list",{
    params: param
  }).then(res => {
    if (res.code === '200') {
      tableData.value = res.data
    }else {
      store.errorMsg(res,router)
    }
  })
}

const resetParam = () => {
  param.name = ''
  param.currentPage = 1
  param.pageSize = 5
  findBySearch()
}

const saveVisible = ref(false)
const form = ref({
  id: '',
  name: '',
  path: '',
  pagePath: '',
  icon: '',
  description: '',
  pid: null
})

const closeForm = () => {
  form.value.name = ''
  form.value.path = ''
  form.value.pagePath = ''
  form.value.icon = ''
  form.value.description = ''
  form.value.pid = null
  findBySearch()
  saveVisible.value = false
  editVisible.value = false
}

const title = ref('')
const addMenu = (pid) => {
  if (env.value === 'production'){
    ElMessage.error("当前环境不能进行此操作，请在开发环境操作")
  }else {
    if (pid) {
      form.value.pid = pid
      title.value = '新增子菜单'
    } else {
      title.value = '新增菜单'
    }
    saveVisible.value = true
  }

}

const icons = ref([])
const getIcons = () => {
  request.get("/dicValue/getMenuIconList").then(res => {
    if (res.code === '200') {
      icons.value = res.data
    }else{
      store.errorMsg(res,router)
    }
  })
}

const saveMenu = () => {
  if (env.value === 'production'){
    ElMessage.error("当前环境不能进行此操作，请在开发环境操作")
  } else if (form.value.name === '') {
    ElMessage.error("菜单名称不能为空")
  } else if (form.value.path !== '' && form.value.pagePath === '') {
    ElMessage.error("页面路径不能为空")
  } else {
    request.post("/menu/save",form.value).then(res => {
      if (res.code === '200'){
        store.successMsg(res,'新增成功')
        setTimeout(() => {closeForm()},1000)
      }else {
        store.errorMsg(res,router)
      }
    })
  }
}

const editVisible = ref(false)

const edit = (menu) => {
  if (env.value === 'production'){
    ElMessage.error("当前环境不能进行此操作，请在开发环境操作")
  }else {
    form.value.id = menu.id
    form.value.name = menu.name
    form.value.path = menu.path
    form.value.pagePath = menu.pagePath
    form.value.icon = menu.icon
    form.value.description = menu.description
    form.value.pid = menu.pid
    editVisible.value = true
  }

}

const update = () => {
  if (env.value === 'production'){
    ElMessage.error("当前环境不能进行此操作，请在开发环境操作")
  } else if (form.value.name === '') {
    ElMessage.error("字典名称不能为空")
  } else if (form.value.path !== '' && form.value.pagePath === '') {
    ElMessage.error("页面路径不能为空")
  } else {
    request.put("/menu/update",form.value).then(res => {
      store.returnMsg(res,'修改成功',router)
      setTimeout(() => {closeForm()},1000)
    })
  }
}

const delById = (id) => {
  if (env.value === 'production'){
    ElMessage.error("当前环境不能进行此操作，请在开发环境操作")
  }else {
    ElMessageBox.confirm('确定要删除这个菜单吗', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '我再想想',
      type: 'warning'
    }).then(() => {
      request.delete("/menu/deleteById/"+id).then(res => {
        store.returnMsg(res,'删除成功',router)
        setTimeout(() => {findBySearch()},1000)
      })
    }).catch(() => {})
  }

}

const ids = ref([])

//获取选中的菜单信息的code
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.id)
}

const delByIds = () => {
  if (env.value === 'production'){
    ElMessage.error("当前环境不能进行此操作，请在开发环境操作")
  } else if (ids.value.length === 0){
    ElMessage.warning("请选择要删除的菜单");
  } else {
    let title = ''
    if (ids.value.length === 1){
      title = '确定要删除这个菜单吗'
    }else {
      title = '确定要删除这些菜单吗'
    }
    ElMessageBox.confirm(title, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '我再想想',
      type: 'warning'
    }).then(() => {
      request.delete("/menu/deleteByIds/"+ids.value).then(res => {
        store.returnMsg(res,'批量删除成功',router)
        setTimeout(() => {findBySearch()},1000)
      })
    }).catch(()=>{})
  }
}

onMounted(() => {
  findBySearch()
  getIcons()
})

</script>

<style scoped>

</style>