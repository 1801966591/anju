import { defineStore } from 'pinia'
import {useRouter} from "vue-router";
import {ElMessage} from "element-plus";
import {ref} from "vue";


export const dataStore = defineStore('data', ()=> {
    //查询返回信息处理
    const searchReturnMsg = (res,router) => {
        let data = {
            tableData: [],
            total: 0
        }
        if (res.code === '200') {
            res.data.records.forEach(record => {
                record.gender = record.gender === 1 ? '男' : '女'
            });
            data.tableData = res.data.records;
            data.total = res.data.total
            return data
        }else {
            errorMsg(res,router)
        }

    }
    //增、删、改操作返回信息处理
    const returnMsg = (res,msg,router) => {
        if (res.code === '200') {
            successMsg(res,msg)
        } else {
            errorMsg(res,router)
        }
    }

    const successMsg = (res,msg) => {
        ElMessage({
            message: msg,
            type: "success",
            showClose: false,
            duration: 1000
        });
    }

    const errorMsg = (res,router) => {
        if(res.code === '401'){
            ElMessage({
                message: res.msg,
                type: "warning",
                showClose: false,
                duration: 2000,
                onClose: () => {
                    localStorage.removeItem("user")
                    localStorage.removeItem("menus")
                    localStorage.removeItem("allMenus")
                    router.push("/user/login")
                }
            });
        }else{
            ElMessage({
                message: res.msg,
                type: "warning",
                showClose: false,
                duration: 1000
            });
        }
    }

    const DetailVisible = ref(false)
    const houseDetail = ref({})
    const description = ref('')

    return {searchReturnMsg,returnMsg,errorMsg,successMsg,DetailVisible,houseDetail,description}
})