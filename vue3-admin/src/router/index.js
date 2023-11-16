import { createRouter, createWebHistory } from 'vue-router';
import {ref} from "vue";
const env = import.meta.env.VITE_APP_ENV
let routes
if (env === 'production') {
    routes = [
        {
            path: '/user',
            component: () => import('../views/Login/LoginView.vue'),
            children: [
                {
                    path: 'login',
                    component: () => import('../views/Login/component/LoginForm.vue')
                },
                {
                    path: 'register',
                    component: () => import('../views/Login/component/RegisterForm.vue')
                },
            ]
        },
        {
            path: '/',
            component: () => import('../views/Layout.vue'),
            redirect: '/home',
            children: [
                {
                    path: 'home',
                    name : '首页',
                    component: ()=> import('../views/Home/HomeView.vue'),
                    meta: {
                        title: '首页'
                    }
                },
                {
                    path: 'user/list',
                    name : '用户管理',
                    component: ()=> import('../views/SystemManage/UserView.vue'),
                    meta: {
                        title: '用户管理'
                    }
                },
                {
                    path: 'role/list',
                    name : '角色管理',
                    component: ()=> import('../views/SystemManage/RoleView.vue'),
                    meta: {
                        title: '角色管理'
                    }
                },
                {
                    path: 'dict/list',
                    name : '字典管理',
                    component: ()=> import('../views/SystemManage/DictionaryView.vue'),
                    meta: {
                        title: '字典管理'
                    }
                },
                {
                    path: 'menu/list',
                    name : '菜单管理',
                    component: ()=> import('../views/SystemManage/MenuView.vue'),
                    meta: {
                        title: '菜单管理'
                    }
                },
                {
                    path: 'log/list',
                    name : '操作日志管理',
                    component: ()=> import('../views/SystemManage/LogView.vue'),
                    meta: {
                        title: '操作日志管理'
                    }
                },
                {
                    path: 'notice/list',
                    name : '公告管理',
                    component: ()=> import('../views/SystemManage/NoticeView.vue'),
                    meta: {
                        title: '公告管理'
                    }
                },
                {
                    path: 'house/upload',
                    name : '上传房源',
                    component: ()=> import('../views/House/UploadHouseView.vue'),
                    meta: {
                        title: '上传房源'
                    }
                },
                {
                    path: 'house/myHouse',
                    name : '我的房源',
                    component: ()=> import('../views/House/HouseView.vue'),
                    meta: {
                        title: '我的房源'
                    }
                },
                {
                    path: 'house/audit',
                    name : '审核房源',
                    component: ()=> import('../views/House/AuditView.vue'),
                    meta: {
                        title: '审核房源'
                    }
                },
                {
                    path: 'house/list',
                    name : '房源列表',
                    component: ()=> import('../views/House/HouseView.vue'),
                    meta: {
                        title: '房源列表'
                    }
                },
                {
                    path: 'CustomerService/chatOnline',
                    redirect: '/CustomerService/chatOnline/conversations',
                    meta:{title:"在线咨询"},
                    component: () => import("../views/CustomerService/ChatView.vue"),
                    children: [
                        {
                            path: 'conversations',
                            component: ()=>import("../views/CustomerService/component/Conversations.vue"),
                            name : 'conversation',
                            meta:{title:"对话列表"},
                            children: [
                                {
                                    path: 'privateChat/:id',
                                    component: ()=>import("../views/CustomerService/component/PrivateChat.vue"),
                                    name : 'privateChat',
                                    meta:{title:"聊天页面"},
                                }
                            ]
                        },
                    ]
                },
                {
                    path: 'customerService/reserve',
                    name : '预约看房审核',
                    component: ()=> import('../views/CustomerService/ReserveView.vue'),
                    meta: {
                        title: '预约看房审核'
                    }
                },
                {
                    path: 'customerService/rentAudit',
                    name : '租赁审核',
                    component: ()=> import('../views/CustomerService/RentAuditView.vue'),
                    meta: {
                        title: '租赁审核'
                    }
                },
                {
                    path: 'customerService/order',
                    name : '订单管理',
                    component: ()=> import('../views/CustomerService/OrderView.vue'),
                    meta: {
                        title: '订单管理'
                    }
                },
                {
                    path: 'customerService/rent',
                    name : '租赁管理',
                    component: ()=> import('../views/CustomerService/RentView.vue'),
                    meta: {
                        title: '租赁管理'
                    }
                },
                {
                    path: 'customerService/repair',
                    name : '报修管理',
                    component: ()=> import('../views/CustomerService/RepairView.vue'),
                    meta: {
                        title: '报修管理'
                    }
                },
                {
                    path: '403',
                    name : '403',
                    component: () => import('../views/ErrorPage/Auth.vue'),
                },
            ]
        },
        {
            path: '/404',
            component: ()=>import("../views/ErrorPage/404.vue"),
            name : '404'
        }
    ]
}else {
    routes = [
        {
            path: '/user',
            component: () => import('../views/Login/LoginView.vue'),
            children: [
                {
                    path: 'login',
                    component: () => import('../views/Login/component/LoginForm.vue')
                },
                {
                    path: 'register',
                    component: () => import('../views/Login/component/RegisterForm.vue')
                },
            ]
        },
    ];
}

const router = createRouter({
    history: createWebHistory(),
    routes,
});

//重置路由
export const resetRoutes = () => {
    router.matcher = createRouter({
        history: createWebHistory(),
        routes,
    });
}

export const setMenu = () => {
    if (env === 'development'){
        const storeMenus = JSON.parse(localStorage.getItem("menus"))
        if (storeMenus) {
            //拼装动态路由
            const manageRoute = {
                path: '/',
                component: () => import("../views/Layout.vue"),
                redirect: "/home",
                children: [
                    {
                        path: '403',
                        name : '403',
                        component: () => import('../views/ErrorPage/Auth.vue'),
                    },
                    {
                        path: 'CustomerService/chatOnline',
                        redirect: '/CustomerService/chatOnline/conversations',
                        meta:{title:"在线咨询"},
                        component: () => import("../views/CustomerService/ChatView.vue"),
                        children: [
                            {
                                path: 'conversations',
                                component: ()=>import("../views/CustomerService/component/Conversations.vue"),
                                name : 'conversation',
                                meta:{title:"对话列表"},
                                children: [
                                    {
                                        path: 'privateChat/:id',
                                        component: ()=>import("../views/CustomerService/component/PrivateChat.vue"),
                                        name : 'privateChat',
                                        meta:{title:"聊天页面"},
                                    }
                                ]
                            },
                        ]
                    },

                ],
            }
            storeMenus.forEach(item => {
                if (item.path){ //当且仅当path不为空的时候才去设置路由
                    let itemMenu = {
                        path: item.path.replace("/",""),
                        name : item.name,
                        component: ()=> import('../views'+item.pagePath+'.vue'),
                        meta: {
                            title: item.name
                        }
                    }
                    manageRoute.children.push(itemMenu)
                }else if(item.children.length){
                    item.children.forEach(item => {
                        if (item.path) {
                            let itemMenu = {
                                path: item.path.replace("/", ""),
                                name: item.name,
                                component: () => import('../views'+item.pagePath+'.vue'),
                                meta: {
                                    title: item.name
                                }
                            }
                            manageRoute.children.push(itemMenu)
                        }
                    })
                }
            })
            const currentRouteNames = router.getRoutes().map(v => v.name)
            if (!currentRouteNames.includes("Layout")){
                router.addRoute(manageRoute); // 添加路由
            }
            router.addRoute({
                path: '/404',
                component: ()=>import("../views/ErrorPage/404.vue"),
                name : '404'
            })
        }
    }
}

setMenu()

// 路由守卫
router.beforeEach((to ,from, next) => {

    if (to.path ==='/user/login' || to.path ==='/user/register') {
        next();
    }
    const user = JSON.parse(localStorage.getItem("user"));

    const menus = JSON.parse(localStorage.getItem("menus"));
    const allMenus = JSON.parse(localStorage.getItem("allMenus"));

    let path = []
    let allMenusPath = []
    allMenusPath.push('/CustomerService/chatOnline/conversations')
    path.push('/CustomerService/chatOnline/conversations')
    const privateChat = /privateChat/

    if (menus){
        menus.forEach(menu => {
            if (menu.path){
                path.push(menu.path)
            }else {
                menu.children.forEach(item => {
                    path.push(item.path)
                })
            }
        })

        allMenus.forEach(menu => {
            if (menu.path){
                allMenusPath.push(menu.path)
            }else {
                menu.children.forEach(item => {
                    allMenusPath.push(item.path)
                })
            }
        })
    }

    let route = []

    routes.forEach(r => {
        route.push(r.path)
    })

    if (!user && to.path !=='/user/login' && to.path !=='/user/register') {
        return next("/user/login");
    } else if (to.path === '/404') {
        next();
    } else if (to.path === '/403') {
        next();
    } else if (privateChat.test(to.path)) {
        next();
    }else if (user && !path.includes(to.path) && allMenusPath.includes(to.path)) {
        next('/403');
    } else if (user && !allMenusPath.includes(to.path) && !route.includes(to.path)) {
        next('/404');
    } else {
        next();
    }

})

export default router;
