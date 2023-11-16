import { defineStore } from 'pinia'
import {ref} from "vue";

export const loginStore = defineStore('login', {
    state: () => {
        return {
            loginAndRegisterVisible:false,
            loginShow :false,
            registerShow: false,
            loginWelcomeShow: false,
            registerWelcomeShow: false,
            forgetPasswordShow: false,
            user: JSON.parse(localStorage.getItem("user")) ? JSON.parse(localStorage.getItem("user")) : {}
        }
    },
})