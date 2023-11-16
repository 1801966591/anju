<template>
  <el-row style="margin-top: 20px">
    <span class="search-span" style="margin-left: 0">角色名称：</span>
    <el-input placeholder="请输入角色名称" v-model="param.name" clearable style="width: 200px"/>
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
        style="width: 100%;"
        @selection-change="handleSelectionChange">
      <el-table-column
          @selection-change="handleSelectionChange"
          type="selection"
          width="55">
      </el-table-column>
      <el-table-column align="center" prop="id" label="ID" width="80px"/>
      <el-table-column align="center" prop="name" label="角色名称" width="120px"/>
      <el-table-column align="center" prop="code" label="角色编码" width="120px"/>
      <el-table-column align="center" prop="createTime" label="创建时间" width="180px"/>
      <el-table-column align="center" prop="updateTime" label="修改时间" width="180px">
        <template #default="scope">
          <div v-if="scope.row.updateTime !== null">{{scope.row.updateTime}}</div>
          <div v-else>暂未修改</div>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" >
        <template #default="scope" >
          <el-button
              v-if="scope.row.code !== 'USER'"
              plain
              type="success"
              @click="openSetMenu(scope.row)"><i class="el-icon-baseline-menu"/> <span class="button-text">分配菜单</span></el-button>
          <el-button
              plain
              type="warning"
              :icon="Edit"
              @click="edit(scope.row)"><span class="button-text">编辑</span></el-button>
          <el-button
              plain
              type="danger"
              :icon="Delete"
              @click="del(scope.row.id)"><span class="button-text">删除</span></el-button>
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

  <el-dialog title="新增角色信息" v-model="saveVisible" width="500px" align-center :before-close="closeSaveForm" :close-on-click-modal="false">
    <el-form :model="saveForm">
      <el-form-item label="角色名称" label-width="35%">
        <el-input v-model="saveForm.name" autocomplete="off" style="width: 200px"></el-input>
      </el-form-item>
      <el-form-item label="角色编码" label-width="35%">
        <el-input v-model="saveForm.code" autocomplete="off" style="width: 200px"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="closeSaveForm">取 消</el-button>
      <el-button type="primary" @click="save">确 定</el-button>
    </template>
  </el-dialog>

  <el-dialog title="编辑角色信息" v-model="editVisible" width="500px" align-center :close-on-click-modal="false">
    <el-form :model="editForm">
      <el-form-item label="角色名称" label-width="35%">
        <el-input v-model="editForm.name" autocomplete="off" style="width: 200px"></el-input>
      </el-form-item>
      <el-form-item label="角色编码" label-width="35%">
        <el-input v-model="editForm.code" autocomplete="off" style="width: 200px"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="editVisible = false">取 消</el-button>
      <el-button type="primary" @click="update">确 定</el-button>
    </template>
  </el-dialog>

  <el-dialog title="分配菜单" v-model="setMenuVisible" width="700px" align-center :close-on-click-modal="false">
    <el-form>
      <el-tree
          :props="props"
          :data="menuData"
          show-checkbox
          node-key="id"
          ref="tree"
          :default-expanded-keys="expends"
          :default-checked-keys="checks">
            <template #default="{ node, data }">
              <span><i :class="data.icon"/> {{ data.name }}</span>
            </template>
      </el-tree>
    </el-form>
    <template #footer>
      <el-button @click="setMenuVisible = false;findBySearch()">取 消</el-button>
      <el-button type="primary" @click="saveMenu()">确 定</el-button>
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
  name: '',
  currentPage: 1,
  pageSize: 5
})
let tableData = ref([])
const total = ref(0)
const router = useRouter()
const store = dataStore()

const findBySearch = () => {
  request.get("/role/search",{
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
onMounted(() => {
  findBySearch()
})

const saveVisible = ref(false)
const saveForm = ref({
  name: '',
  code: '',
})

const closeSaveForm = () => {
  saveForm.value.name = ''
  saveForm.value.code = ''
  findBySearch()
  saveVisible.value = false
}

const save = () => {
  if (saveForm.value.name === ''){
    ElMessage.error("角色名称不能为空")
  } else if (saveForm.value.code === ''){
    ElMessage.error("角色编码不能为空")
  } else {
    request.post("/role/save",saveForm.value).then(res => {
      store.returnMsg(res,'新增角色成功',router)
      setTimeout(() => {closeSaveForm()},1000)
    })
  }
}

const editVisible = ref(false)
const editForm = ref({
  id: '',
  name: '',
  code: '',
})

const edit = (role) => {
  editForm.value = role
  editVisible.value = true
}

const update = () => {
  if (editForm.value.name === ''){
    ElMessage.error("角色名称不能为空")
  } else if (editForm.value.code === ''){
    ElMessage.error("角色编码不能为空")
  } else {
    request.put("/role/update",editForm.value).then(res => {
      store.returnMsg(res,'修改角色信息成功',router)
      setTimeout(() => {editVisible.value = false},1000)
    })
  }
}

const del = (id) => {
  ElMessageBox.confirm('确定要删除这个角色吗', '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '我再想想',
    type: 'warning'
  }).then(() => {
    request.delete("/role/deleteById/" + id).then(res => {
      store.returnMsg(res,'删除角色成功',router)
      setTimeout(() => {findBySearch()},1000)
    })
  }).catch(() => {})
}

const ids = ref([])

//获取选中的角色信息的id
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.id)
}

const delByIds = () => {
  if (ids.value.length === 0){
    ElMessage.warning("请选择要删除的角色");
  } else {
    let title = ''
    if (ids.value.length === 1){
      title = '确定要删除这个角色吗'
    }else {
      title = '确定要删除这些角色吗'
    }
    ElMessageBox.confirm(title, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '我再想想',
      type: 'warning'
    }).then(() => {
      request.delete("/role/deleteByIds/"+ids.value).then(res => {
        store.returnMsg(res,'批量删除角色成功',router)
        setTimeout(() => {findBySearch()},1000)
      })
    }).catch(()=>{})
  }
}
const setMenuVisible = ref(false)
const menuData = ref([])
const props = ref({
  label: 'name',
})
const setMenus = ref({
  id : '',
  role : '',
  menuIds : [],
})
const expends = ref([])
const checks = ref([])
const tree = ref(null);
const env = ref(import.meta.env.VITE_APP_ENV)
const openSetMenu = (data) => {
  if (env.value === 'production'){
    ElMessage.error("当前环境不能进行此操作，请在开发环境操作")
  }else {
    const roleId = data.id
    setMenus.value.id = roleId
    setMenus.value.role = data.code

    //获取所有菜单
    request.get("/menu/list").then(res =>{
      menuData.value = res.data

      expends.value = menuData.value.map(v => v.id)
    })

    //获取当前角色的菜单
    request.get("/role/getRoleMenu/"+roleId).then(res =>{
      checks.value = res.data

      //获取所有菜单的id
      request.get("/menu/ids").then(r => {
        const ids = r.data
        ids.forEach(id => {
          if (!checks.value.includes(id)) {
            const treeComponent = tree.value;
            treeComponent.setChecked(id,false)
          }
        })
      })
    })
    setMenuVisible.value = true
  }
}

const saveMenu = () => {
  if (env.value === 'production'){
    ElMessage.error("当前环境不能进行此操作，请在开发环境操作")
  }else {
    const treeComponent = tree.value;
    setMenus.value.menuIds = treeComponent.getCheckedKeys().concat(treeComponent.getHalfCheckedKeys());

    request.post("/role/setMenu", setMenus.value).then(res => {
      store.returnMsg(res,'绑定成功',router)
      setTimeout(() => {
        findBySearch();
        setMenuVisible.value = false;
        // 如果操作的用户是管理员的话，需要重新登录
        if (setMenus.value.role === 'ADMIN') {
          ElMessage({
            message: '请重新登录',
            type: "warning",
            showClose: false,
            duration: 1500,
            onClose: () => {
              router.push("/user/login");
            }
          });
        }
      },1000)
    });
  }
};


</script>

<style scoped>

</style>