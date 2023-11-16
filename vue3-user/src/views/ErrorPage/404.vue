<script>
import router from "../../router";

export default {
  name: "Auth",
  methods : {
    toHome(){

      router.push("/")


    }
  }
}
</script>

<template>
  <div>
    <div style="display: flex;justify-content: center">
      <img src="../../assets/imgs/404.png" style="width: 500px">
    </div>
    <div style="display: flex;justify-content: center;margin-top: 40px;font-size: 20px">
      找不到页面
    </div>
    <div style="display: flex;justify-content: center;margin-top: 40px">
      <el-button round plain type="primary" style="width: 150px;height: 40px;font-size: 15px" @click="toHome">返回首页</el-button>
    </div>
  </div>
</template>

<style scoped>

</style>