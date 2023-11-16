<template>
<div class="content">
  <el-row class="bgImg">
    <div style="height: 100px">
      <div class="bigTitle">寻找理想的家，从这里开始。</div>
      <div class="smallTitle">海量房源任你选，找房更快</div>
    </div>
    <div style="margin-left: 180px">
      <el-row>
        <el-radio-group v-model="searchType" style="font-family: PingFangMedium,sans-serif">
          <el-radio-button label="">
            <span class="searchType">全部</span>
          </el-radio-button>
          <el-radio-button label="1">
            <span class="searchType">合租房</span>
          </el-radio-button>
          <el-radio-button label="2">
            <span class="searchType">整租房</span>
          </el-radio-button>
          <el-radio-button label="3">
            <span class="searchType">独栋公寓</span>
          </el-radio-button>
        </el-radio-group>
      </el-row>
      <el-input placeholder="请输入房源名称开始找房" v-model="name" style="height:50px;width: 800px;font-size: 15px" clearable>
        <template #append>
          <el-button style="height: 50px;width: 100px;" type="primary" @click="searchHouse('')">开始找房</el-button>
        </template>
      </el-input>

    </div>
  </el-row>

  <el-row :gutter="10" style="margin-top: 64px;margin-left: 190px">
    <el-col :span="8">
      <div class="type together" @click="searchHouse('1')">
        <div class="BigTitle">合租房</div>
        <div class="SmallTitle">温馨合租房，室友同分享</div>
      </div>
    </el-col>
    <el-col :span="8">
      <div class="type alone" @click="searchHouse('2')">
        <div class="BigTitle">整租房</div>
        <div class="SmallTitle">品质生活我做主</div>
      </div>
    </el-col>
    <el-col :span="8">
      <div class="type single" @click="searchHouse('3')">
        <div class="BigTitle">独栋公寓</div>
        <div class="SmallTitle">独栋公寓，尊享品质</div>
      </div>
    </el-col>
  </el-row>

  <div style="margin-top: 100px;">
    <div class="title1 font1">安心住</div>
    <div class="title1 font2">/满足个性化的租房需求/</div>
  </div>

  <el-card shadow="hover" class="bgImg2">
    <div class="title2 font3">安心</div>
    <div class="title3 font4">实景拍摄，真房源细心悉，满足您多方位需求</div>
    <div class="title4 font3">舒心</div>
    <div class="title3 font4">租前租后服务一如仪，更有专属管家，让您畅享时光</div>
    <div class="title4 font3">放心呼吸</div>
    <div class="title3 font4">干净明亮，空气清新，让您生活如诗，精心呵护您的健康</div>
    <div>
      <el-button type="primary" class="button1" @click="toFindHouse">立即找房</el-button>
    </div>
  </el-card>

  <div style="margin-top: 100px;">
    <div class="title1 font1">业务体系</div>
    <div class="title1 font2">/专业租房体系，租后服务一体化/</div>
  </div>

  <el-row :gutter="10" style="margin-top: 64px;margin-left: 95px">
    <el-col :span="8">
      <div class="business resource">
        <div class="title5 font5">品质真房源</div>
        <div class="title6 font6">所见即所得，免去货不对版的担忧</div>
      </div>
    </el-col>
    <el-col :span="8">
      <div class="business smart">
        <div style="color: #333333" class="title5 font5">智能家居</div>
        <div style="color: #333333" class="title6 font6">安全、便捷，智能生活，节约能源</div>
      </div>
    </el-col>
    <el-col :span="8">
      <div class="business service">
        <div class="title5 font5">租后服务</div>
        <div class="title6 font6">在线报修、快速处理服务</div>
      </div>
    </el-col>
  </el-row>

  <div style="margin-top: 100px;">
    <div class="title1 font1">服务理念</div>
    <div class="title1 font2">/让所有漂泊的游子都能租到满意的房子/</div>
  </div>

  <el-row :gutter="10" style="padding: 64px 60px 0 60px;margin-left: 200px">
    <el-col :span="8">
      <div class="idea easy">
        <div class="title7 font7">简单</div>
      </div>
      <div class="title8 font8">真实优质房源，无中介，无虚假信息</div>
      <div style="margin-top:5px;padding-left: 50px" class="title8 font8">让租房变得简单</div>
    </el-col>
    <el-col :span="8">
      <div class="idea credible">
        <div class="title7 font7">可信</div>
      </div>
      <div class="title8 font8">给用户提供真实舒适的优质好房源</div>
      <div style="margin-top:5px;padding-left: 50px" class="title8 font8">做值得信赖的品牌</div>
    </el-col>
    <el-col :span="8">
      <div class="idea userFirst">
        <div class="title7 font7">用户至上</div>
      </div>
      <div class="title8 font8">致力于提供个性化、优质的服务</div>
      <div style="margin-top:5px;padding-left: 30px" class="title8 font8">努力满足每个用户的需求</div>
    </el-col>
  </el-row>
</div>
</template>

<script setup>

  import {ref,} from "vue";
  import {useRouter} from "vue-router";

  const name = ref('')
  const searchType = ref("")

  const router = useRouter();

  const toFindHouse = () => {
    router.push("/findHouse")
  }

  const searchHouse = (type) => {
    router.push({
      path:"/findHouse",
      query:{
        rentType: type,
        name: name.value
      }
    })
  }
</script>

<style scoped>

  .bgImg{
    width: 100%;
    height: 550px;
    background-image: url("../../assets/imgs/bg.png");
    background-size: 100% 550px;
  }
  .bigTitle{
    font-family:'仓耳渔阳体',sans-serif;
    font-size: 63px;
    color: #fff;
    padding: 60px 0 0 180px;
  }
  .smallTitle{
    font-family:'仓耳渔阳体',sans-serif;
    font-size: 28px;
    color: #fff;
    padding: 15px 0 0 180px;
  }
  .searchType{
    font-size: 16px
  }
  .type{
    width:254px;
    height:100px;
    background-size: 100% 100px;
    border-radius: 10px;
    transition: transform 0.3s;
  }
  .type:hover{
    transform: scale(1.2); /* 鼠标悬停时放大到1.2倍 */
    cursor: pointer; /* 添加指针样式，以表示可交互 */
  }
  .together{
    background-image: url('../../assets/imgs/together.png');
  }
  .alone{
    background-image: url('../../assets/imgs/alone.png');
  }
  .single{
    background-image: url("../../assets/imgs/single.png");
  }
  .BigTitle{
    font-size: 22px;
    font-family:PingFangMedium,sans-serif;
    font-weight: bold;
    color: #fff;
    padding: 22px 0 0 90px;
  }
  .SmallTitle{
    font-family:PingFang,sans-serif;
    font-size: 13px;
    color: #fff;
    padding: 5px 0 0 90px;
  }
  .title1{
    display: flex;
    align-content: center;
    justify-content: center;
  }
  .font1{
    font-family:PingFangMedium,sans-serif;
    font-weight: bold;
    font-size: 36px;
  }
  .font2{
    font-family:PingFang,sans-serif;
    font-size: 24px;
    color: #777;
  }
  .bgImg2{
    width: 1200px;
    height: 564px;
    background-image: url("../../assets/imgs/live.33d8dc33.png");
    background-size: cover;
    margin-top: 50px;
    margin-left: 160px;
    border-radius: 10px;
  }
  .title2{
    padding-left: 50px;
    padding-top: 90px;
  }
  .font3{
    font-size: 30px;
    font-family: 仓耳渔阳体,sans-serif;
    font-weight: 600;
  }
  .title3{
    padding-left: 50px;
    padding-top: 15px;
  }
  .font4{
    font-size: 18px;
    font-family: PingFangMedium,sans-serif;
    color: #777;
  }
  .title4{
    padding-left: 50px;
    padding-top: 30px;
  }
  .button1{
    width: 168px;
    height: 44px;
    text-align: center;
    border-radius: 40px;
    margin-top: 50px;
    margin-left: 120px;
    font-family: 仓耳渔阳体,sans-serif;
    font-size: 18px;
    font-weight: bold;
  }
  .business{
    width: 360px;
    height: 360px;
    text-align: center;
    border-radius: 10px;
    background-size: cover;
    transition: transform 0.3s;

  }
  .business:hover{
    transform: scale(1.05);
  }
  .resource{
    background-image: url("../../assets/imgs/resource.png");
  }
  .smart{
    background-image: url("../../assets/imgs/smart.png");
  }
  .service{
    background-image: url("../../assets/imgs/service.png");
  }
  .title5{
    padding-top: 124px;
  }
  .title6{
    padding-top: 10px;
  }
  .font5{
    font-family: PingFangMedium,sans-serif;
    font-size: 24px;
    font-weight: bold;
    color: #fff
  }
  .font6{
    font-family: PingFangMedium,sans-serif;
    font-size: 16px;
    font-weight: bold;
    color: #fff;
  }
  .idea{
    border-radius: 50%;
    width: 210px;
    height: 210px;
    text-align: center;
    background-size: cover;
    transition: transform 0.3s;
  }
  .easy{
    background-image: url("../../assets/imgs/easy.png");
  }
  .credible{
    background-image: url("../../assets/imgs/credible.png");
  }
  .userFirst{
    background-image: url("../../assets/imgs/userFirst.png");
  }
  .title7{
    padding-top: 80px;
  }
  .font7{
    font-family: PingFangMedium,sans-serif;
    font-size: 24px;
    font-weight: bold;
    color: #fff;
  }
  .title8{
    margin-top: 20px;
    margin-right: 80px;
  }
  .font8{
    font-family: PingFangMedium,sans-serif;
    font-size: 14px;
  }
  /*/deep/ .el-input__inner{
    height: 50px;
  }*/

</style>