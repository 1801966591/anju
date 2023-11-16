<template>
  <el-config-provider :locale="zhCn">
    <router-view/>
  </el-config-provider>
</template>

<script setup>
import {useRouter} from "vue-router";
import NProgress from "nprogress";
import zhCn from 'element-plus/es/locale/lang/zh-cn'

NProgress.configure({
  easing: 'ease', // 动画方式
  speed: 300, // 递增进度条的速度
  showSpinner: false, // 是否显示加载ico
  trickleSpeed: 200, // 自动递增间隔
  minimum: 0.3,// 初始化时的最小百分比,
})
const router = useRouter();
router.beforeEach((to, from, next) => {
  NProgress.start();
  next();
});
router.afterEach(() => {
  NProgress.done();
});
</script>

<style>
::-webkit-scrollbar {
  width: 4px; /*滚动条宽度*/
  height: 6px; /*滚动条高度*/
  background-color: #ebeef5; /*滚动区域边缘的背景颜色*/
  padding-right: 10px;
}
::-webkit-scrollbar-thumb {
  box-shadow: inset 0 0 6px rgba(0, 0, 0, .3); /*滚动条的内阴影*/
  -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, .3);
  background-color: skyblue;  /*滚动条的颜色*/
  border-radius: 70px; /*滚动条的圆角*/
}
::-webkit-scrollbar-track{
  box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2); /*滚动条的内阴影*/
  border-radius: 70px; /*滚动条的背景区域的圆角*/
  background: rgba(255, 255, 255, 1); /*滚动条的背景颜色*/
}
.button-text{
  font-size: 14px;
  font-family: 仓耳渔阳体,sans-serif;
  margin-top: 3px
}
.search-span{
  margin-left: 20px;
  margin-top: 5px;
}

</style>
