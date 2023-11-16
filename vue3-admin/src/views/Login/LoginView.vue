<template>
  <div>
    <el-row>
      <el-col :span="12">
        <div style="width: 100%;height: 100vh;background-color: #353e54">
          <transition
              appear
              enter-active-class="animate__animated animate__bounceInLeft"
              leave-active-class="animate__animated animate__bounceOutLeft">
            <div>
              <div>
                <img src="../../assets/imgs/logo.png" style="width: 200px;margin:23px 0 0 23px;">
              </div>
                <div>
                  <div style="display: flex;align-items: center;justify-content: center;margin-top: 85px">
                    <img src="../../assets/imgs/login-box-bg.png" style="width: 350px;height: 350px">
                  </div>
                  <div style="margin-left: 200px;">
                    <span style="font-size: 2rem;inline-size:2.25rem;color: #fff;font-family: '默陌老屋手迹',sans-serif">欢迎使用本系统</span>
                  </div>
                </div>
            </div>
          </transition>
        </div>

      </el-col>

        <el-col :span="12">
            <div class="right-content">
               <router-view/>
            </div>
        </el-col>

    </el-row>
  </div>
</template>

<script setup>

</script>


<style scoped>

</style>