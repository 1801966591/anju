import { defineStore } from 'pinia'
import {useRouter} from "vue-router";
import {ElMessage} from "element-plus";
import {inject, ref} from "vue";
import GoEasy from "goeasy";


export const goEasyStore = defineStore('GoEasy', ()=> {
    const connectGoEasy = () => {
        const goEasy = GoEasy.getInstance({
            host:"hangzhou.goeasy.io",  //若是新加坡区域：singapore.goeasy.io
            appkey:"BC-96619573e9df4c758a704cc1dabfe665",
            modules:['im']//根据需要，传入'im’或'pubusub'，或数组方式同时传入
        });
        const user = JSON.parse(localStorage.getItem("user"))
        if (goEasy.getConnectionStatus() === 'disconnected'){
            goEasy.connect({
                id: user.id,
                data: {nickname: user.nickname, avatar: user.avatar},
                onSuccess: function () {  //连接成功
                    console.log("连接成功") //连接成功
                },
                onFailed: function (error) { //连接失败
                    console.log("连接失败, 错误代码:" + error.code + ",错误:" + error.content);
                },
                onProgress: function (attempts) { //连接或自动重连中
                    console.log("连接或自动重连中...", attempts);
                }
            });
        }
    }
    return {connectGoEasy}
})