<template>
  <div class="home">
    <div class="home-container">
      <div class="home-main">
        <router-view/>
      </div>
    </div>
  </div>
</template>

<script setup>
import {goEasyStore} from "../../stores/goEasyStore.js";
import {inject, onBeforeMount, ref} from "vue";

const goEasy = inject("goEasy")
const GoEasy = inject("GoEasy")
const unreadAmount = ref(null)

const setUnreadNumber = (content) => {
  unreadAmount.value = content.unreadTotal
}

const store = goEasyStore()
onBeforeMount(() => {
  store.connectGoEasy()
  goEasy.im.on(GoEasy.IM_EVENT.CONVERSATIONS_UPDATED, setUnreadNumber);
})
</script>

<style scoped>
@media screen and (max-width:1000px) {
  .home-container {
    width: 850px;
    height: 430px;
  }
}
@media screen and (min-width:1000px) {
  .home-container {
    width: 950px;
    height: 600px;
  }
}

.home {

  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.home-container {
  background: #FFFFFF;
  display: flex;
  position: relative;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.home-menu {
  width: 60px;
  background-color: #F7F7F7;
  border-right: 1px solid #eeeeee;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.menu-header {
  margin: 20px auto;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  cursor: pointer;
}

.user-avatar:hover + .user-profile {
  display: block;
}

.user-profile {
  display: none;
  color: #FFFFFF;
  position: absolute;
  top: 0;
  left: 70px;
  width: 250px;
  height: 200px;
  background: #FFFFFF;
  z-index: 999;
}

.user-profile-main {
  border: 1px solid #EBEEF5;
  background-color: #FFF;
  color: #303133;
  border-radius: 4px;
}

.user-profile-header {
  padding: 18px 20px;
  border-bottom: 1px solid #EBEEF5;
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 15px;
  font-weight: bold;
}

.user-profile-header img {
  width: 45px;
  height: 45px;
}

.user-profile-info {
  display: flex;
  padding: 10px 20px;
  font-size: 14px;
  color: #666666;
  line-height: 28px;
}

.user-profile-info-title {
  width: 70px;
}

.menu-box {
  padding: 40px 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
}

.menu-list {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.menu-item {
  color: #303133;
  cursor: pointer;
  height: 56px;
  position: relative;
}

.menu-unread {
  position: absolute;
  top: -5px;
  right: 5px;
  width: 18px;
  height: 18px;
  line-height: 18px;
  text-align: center;
  border-radius: 50%;
  background-color: #d02129;
  color: #ffffff;
}

.router-link-active i {
  color: #d02129 !important;
}

.iconfont {
  padding: 15px;
  font-size: 28px;
  color: #606266;
  cursor: pointer;
}

.iconfont:active {
  color: #d02129;
}

.home-main {
  padding: 0;
  flex: 1;
}

</style>
