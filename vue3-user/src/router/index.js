import { createRouter, createWebHistory } from 'vue-router';
import {loginStore} from "../stores/loginStore.js";
import {ElMessage} from "element-plus";
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
NProgress.configure({
    easing: 'ease', // 动画方式
    speed: 500, // 递增进度条的速度
    showSpinner: false, // 是否显示加载ico
    trickleSpeed: 200, // 自动递增间隔
    minimum: 0.3,// 初始化时的最小百分比,
})
const routes = [
    {
        path: '/',
        name : 'user',
        redirect:'/home',
        component: ()=> import('../views/Layout.vue'),
        children: [
            {
                path: 'home',
                name : 'userHome',
                component: ()=> import('../views/Home/HomeView.vue'),
            },
            {
                path: 'findHouse',
                name : 'findHouse',
                component: ()=> import('../views/FindHouse/FindHouseView.vue'),
            },
            {
                path: 'houseDetail/:id',
                name: 'houseDetail',
                component: () => import('../views/FindHouse/HouseDetailView.vue')
            },
            {
                path: 'myReserve',
                component: () => import('../views/Reserve/MyReserveView.vue')
            },
            {
                path: 'myRent',
                children: [
                    {
                        path: 'myRentApply',
                        component: () => import('../views/Rent/MyRentApplyView.vue')
                    },
                    {
                        path: 'myOrder',
                        component: () => import('../views/Rent/MyOrderView.vue')
                    },
                    {
                        path: 'myRent',
                        component: () => import('../views/Rent/MyRentView.vue')
                    },
                    {
                        path: 'myRepair',
                        component: () => import('../views/Rent/MyRepairView.vue')
                    },
                ]
            }
        ]
    },
    {
        path: '/404',
        name: '404',
        component: () => import("../views/ErrorPage/404.vue")

    },
];


const router = createRouter({
    history: createWebHistory(),
    routes,
});

// 路由守卫
router.beforeEach((to, from, next) => {
    window.scroll(0,0)
    const routePath = []
    const path = ['/home','/findHouse']
    const houseDetail = /houseDetail/;
    const user = JSON.parse(localStorage.getItem("user"))
    const store = loginStore()
    routes.forEach(r => {
        routePath.push(r.path.replace("/",""))
        if (r.children){
            r.children.forEach(c => {
                routePath.push(c.path.replace("/",""))
                if (c.children){
                    c.children.forEach(d => {
                        routePath.push(c.path + "/" + d.path)
                    })
                }
            })
        }
    })
    if (!user && (!path.includes(to.path) || houseDetail.test(to.path))) {
        ElMessage({
            message: '请先登录',
            type: 'error',
            duration: 1500,
            onClose : () => {
                store.loginAndRegisterVisible = true
                store.loginShow = true
                store.loginWelcomeShow = true
            }
        })
    } else if(!user && houseDetail.test(to.path) && !houseDetail.test(from.path)) {
        next()
        store.loginAndRegisterVisible = true
        store.loginShow = true
        store.loginWelcomeShow = true
    }else if (user && houseDetail.test(to.path)){
        next()
    }else if (to.path === '/404'){
        next()
    }else if (!routePath.includes(to.path.replace("/",""))){
        next("/404")
    }else {
        next()
    }
})

export default router;
