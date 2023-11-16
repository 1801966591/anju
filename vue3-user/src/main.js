import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import './assets/css/global.css'
import './assets/fonts/font.css'
import './assets/css/iconfont/iconfont.css'
import './assets/iconfont/iconfont.css'
import { createPinia } from 'pinia'
import router from './router'; // 导入路由配置
import 'animate.css';
import GoEasy from "goeasy";

//引入elementPlus推荐的light模式的样式文件
import 'element-plus/dist/index.css'

//  elementPlus实现的dark模式的样式文件
import 'element-plus/theme-chalk/dark/css-vars.css'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

const goEasy = GoEasy.getInstance({
    host: 'hangzhou.goeasy.io', //应用所在的区域地址: [hangzhou.goeasy.io, 新加坡暂不支持IM，敬请期待]
    appkey: 'BC-96619573e9df4c758a704cc1dabfe665', // common key,
    modules: ['im'],
});
app.provide('GoEasy', GoEasy);
app.provide('goEasy', goEasy);

app.use(ElementPlus)
app.use(router); // 使用路由配置
app.use(createPinia())
app.mount('#app')
