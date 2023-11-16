<template>
  <div class="chat-container">
    <div class="chat-title">
      <img :src="baseUrl + '/api/files/' + friend.avatar" class="chat-avatar"/>
      <div class="chat-name">{{ friend.nickname }}</div>
    </div>
    <div class="chat-main" ref="scrollView">
      <div class="message-list" ref="messageList">
        <div v-if="history.loading" class="history-loading">
          <img src="../../../assets/imgs/pending.gif"/>
        </div>
        <div v-else :class="history.allLoaded ? 'history-loaded':'load'" @click="loadHistoryMessage(false)">
          {{ history.allLoaded ? '已经没有更多的历史消息' : '获取历史消息' }}
        </div>
        <div v-for="(message, index) in history.messages" :key="index">
          <div class="time-tips">{{ renderMessageDate(message, index) }}</div>
          <div class="message-recalled" v-if="message.recalled">
            <div v-if="message.recaller.id === currentUser.id" class="message-recalled-self">
              <div>你撤回了一条消息</div>
              <span v-if="message.type === 'text' && Date.now()-message.timestamp< 60 * 1000 "
                    @click="editRecalledMessage(message.payload.text)">重新编辑</span>
            </div>
            <div v-else>{{ message.recaller.data.nickname }}撤回了一条消息</div>
          </div>
          <div class="message-item" v-else>
            <div class="message-item-checkbox" v-if="messageSelector.visible && message.status !== 'sending'">
              <input class="input-checkbox" type="checkbox" :value="message.messageId" v-model="messageSelector.ids"
                     @click="selectMessages">
            </div>
            <div class="message-item-content" :class="{ self: Number(message.senderId) === currentUser.id }">
              <div class="sender-info">
                <img v-if="currentUser.id === Number(message.senderId)" :src="baseUrl + '/api/files/'+currentUser.avatar" class="sender-avatar"/>
                <img v-else :src="baseUrl + '/api/files/'+friend.avatar" class="sender-avatar"/>
              </div>
              <div class="message-content" @click.right="showActionPopup(message)">
                <div class="message-payload">
                  <div class="pending" v-if="message.status === 'sending'"></div>
                  <div class="send-fail" v-if="message.status === 'fail'"></div>
                  <div v-if="message.type === 'text'" class="content-text"
                       v-html="emoji.decoder.decode(message.payload.text)"></div>
                </div>
                <div v-if="currentUser.id === Number(message.senderId)" :class="message.read ?'message-read':'message-unread'">
                  <div v-if="Number(message.senderId) === currentUser.id">{{ message.read ? '已读' : '未读' }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="chat-footer">
      <div class="action-delete" v-if="messageSelector.visible">
        <img class="delete-btn" src="../../../assets/imgs/delete.png" @click="deleteMultipleMessages" alt=""/>
        <div>删除</div>
      </div>
      <div class="action-box" v-else>
        <div class="action-bar">
          <!-- 表情 -->
          <div class="action-item">
            <div v-if="emoji.visible" class="emoji-box">
              <img
                v-for="(emojiItem, emojiKey, index) in emoji.map"
                class="emoji-item"
                :key="index"
                :src="emoji.url + emojiItem"
                @click="chooseEmoji(emojiKey)"
               alt=""/>
            </div>
            <i class="iconfont icon-smile" title="表情" @click="showEmojiBox"></i>
          </div>
        </div>

        <!-- GoEasyIM最大支持3k的文本消息，如需发送长文本，需调整输入框maxlength值 -->
        <div class="input-box">
          <textarea ref="input" @focus="onInputFocus" @keyup.enter="sendTextMessage" v-model="text" maxlength="700" autocomplete="off" class="input-content"></textarea>
        </div>
        <div class="send-box">
          <el-button type="primary" class="send-button" @click="sendTextMessage">发送</el-button>
        </div>
      </div>
    </div>
    <!-- 消息删除撤回弹窗 -->
    <div class="action-popup" v-if="actionPopup.visible" @click="actionPopup.visible = false">
      <div class="action-popup-main">
        <div class="action-item" @click="deleteSingleMessage">删除</div>
        <div class="action-item" v-if="actionPopup.recallable" @click="recallMessage">撤回</div>
        <div class="action-item" @click="showCheckBox">多选</div>
        <div class="action-item" @click="actionPopup.visible = false">取消</div>
      </div>
    </div>
  </div>
</template>

<script setup>
  import {formatDate} from '../../../utils/utils.js'
  import EmojiDecoder from '../../../utils/EmojiDecoder';
  import {inject, nextTick, onBeforeUnmount, onMounted, reactive, ref} from "vue";
  import {ElMessage} from "element-plus";
  import {useRoute} from "vue-router";

  const baseUrl = ref(import.meta.env.VITE_APP_BASE_URL)
  const emojiUrl = 'https://imgcache.qq.com/open/qcloud/tim/assets/emoji/';
  const emojiMap = {
    '[NO]': 'emoji_0@2x.png',
    '[OK]': 'emoji_1@2x.png',
    '[下雨]': 'emoji_2@2x.png',
    '[么么哒]': 'emoji_3@2x.png',
    '[乒乓]': 'emoji_4@2x.png',
    '[便便]': 'emoji_5@2x.png',
    '[信封]': 'emoji_6@2x.png',
    '[偷笑]': 'emoji_7@2x.png',
    '[傲慢]': 'emoji_8@2x.png',
    '[再见]': 'emoji_9@2x.png',
    '[冷汗]': 'emoji_10@2x.png',
    '[凋谢]': 'emoji_11@2x.png',
    '[刀]': 'emoji_12@2x.png',
    '[删除]': 'emoji_13@2x.png',
    '[勾引]': 'emoji_14@2x.png',
    '[发呆]': 'emoji_15@2x.png',
    '[发抖]': 'emoji_16@2x.png',
    '[可怜]': 'emoji_17@2x.png',
    '[可爱]': 'emoji_18@2x.png',
    '[右哼哼]': 'emoji_19@2x.png',
    '[右太极]': 'emoji_20@2x.png',
    '[右车头]': 'emoji_21@2x.png',
    '[吐]': 'emoji_22@2x.png',
    '[吓]': 'emoji_23@2x.png',
    '[咒骂]': 'emoji_24@2x.png',
    '[咖啡]': 'emoji_25@2x.png',
    '[啤酒]': 'emoji_26@2x.png',
    '[嘘]': 'emoji_27@2x.png',
    '[回头]': 'emoji_28@2x.png',
    '[困]': 'emoji_29@2x.png',
    '[坏笑]': 'emoji_30@2x.png',
    '[多云]': 'emoji_31@2x.png',
    '[大兵]': 'emoji_32@2x.png',
    '[大哭]': 'emoji_33@2x.png',
    '[太阳]': 'emoji_34@2x.png',
    '[奋斗]': 'emoji_35@2x.png',
    '[奶瓶]': 'emoji_36@2x.png',
    '[委屈]': 'emoji_37@2x.png',
    '[害羞]': 'emoji_38@2x.png',
    '[尴尬]': 'emoji_39@2x.png',
    '[左哼哼]': 'emoji_40@2x.png',
    '[左太极]': 'emoji_41@2x.png',
    '[左车头]': 'emoji_42@2x.png',
    '[差劲]': 'emoji_43@2x.png',
    '[弱]': 'emoji_44@2x.png',
    '[强]': 'emoji_45@2x.png',
    '[彩带]': 'emoji_46@2x.png',
    '[彩球]': 'emoji_47@2x.png',
    '[得意]': 'emoji_48@2x.png',
    '[微笑]': 'emoji_49@2x.png',
    '[心碎了]': 'emoji_50@2x.png',
    '[快哭了]': 'emoji_51@2x.png',
    '[怄火]': 'emoji_52@2x.png',
    '[怒]': 'emoji_53@2x.png',
    '[惊恐]': 'emoji_54@2x.png',
    '[惊讶]': 'emoji_55@2x.png',
    '[憨笑]': 'emoji_56@2x.png',
    '[手枪]': 'emoji_57@2x.png',
    '[打哈欠]': 'emoji_58@2x.png',
    '[抓狂]': 'emoji_59@2x.png',
    '[折磨]': 'emoji_60@2x.png',
    '[抠鼻]': 'emoji_61@2x.png',
    '[抱抱]': 'emoji_62@2x.png',
    '[抱拳]': 'emoji_63@2x.png',
    '[拳头]': 'emoji_64@2x.png',
    '[挥手]': 'emoji_65@2x.png',
    '[握手]': 'emoji_66@2x.png',
    '[撇嘴]': 'emoji_67@2x.png',
    '[擦汗]': 'emoji_68@2x.png',
    '[敲打]': 'emoji_69@2x.png',
    '[晕]': 'emoji_70@2x.png',
    '[月亮]': 'emoji_71@2x.png',
    '[棒棒糖]': 'emoji_72@2x.png',
    '[汽车]': 'emoji_73@2x.png',
    '[沙发]': 'emoji_74@2x.png',
    '[流汗]': 'emoji_75@2x.png',
    '[流泪]': 'emoji_76@2x.png',
    '[激动]': 'emoji_77@2x.png',
    '[灯泡]': 'emoji_78@2x.png',
    '[炸弹]': 'emoji_79@2x.png',
    '[熊猫]': 'emoji_80@2x.png',
    '[爆筋]': 'emoji_81@2x.png',
    '[爱你]': 'emoji_82@2x.png',
    '[爱心]': 'emoji_83@2x.png',
    '[爱情]': 'emoji_84@2x.png',
    '[猪头]': 'emoji_85@2x.png',
    '[猫咪]': 'emoji_86@2x.png',
    '[献吻]': 'emoji_87@2x.png',
    '[玫瑰]': 'emoji_88@2x.png',
    '[瓢虫]': 'emoji_89@2x.png',
    '[疑问]': 'emoji_90@2x.png',
    '[白眼]': 'emoji_91@2x.png',
    '[皮球]': 'emoji_92@2x.png',
    '[睡觉]': 'emoji_93@2x.png',
    '[磕头]': 'emoji_94@2x.png',
    '[示爱]': 'emoji_95@2x.png',
    '[礼品袋]': 'emoji_96@2x.png',
    '[礼物]': 'emoji_97@2x.png',
    '[篮球]': 'emoji_98@2x.png',
    '[米饭]': 'emoji_99@2x.png',
    '[糗大了]': 'emoji_100@2x.png',
    '[红双喜]': 'emoji_101@2x.png',
    '[红灯笼]': 'emoji_102@2x.png',
    '[纸巾]': 'emoji_103@2x.png',
    '[胜利]': 'emoji_104@2x.png',
    '[色]': 'emoji_105@2x.png',
    '[药]': 'emoji_106@2x.png',
    '[菜刀]': 'emoji_107@2x.png',
    '[蛋糕]': 'emoji_108@2x.png',
    '[蜡烛]': 'emoji_109@2x.png',
    '[街舞]': 'emoji_110@2x.png',
    '[衰]': 'emoji_111@2x.png',
    '[西瓜]': 'emoji_112@2x.png',
    '[调皮]': 'emoji_113@2x.png',
    '[象棋]': 'emoji_114@2x.png',
    '[跳绳]': 'emoji_115@2x.png',
    '[跳跳]': 'emoji_116@2x.png',
    '[车厢]': 'emoji_117@2x.png',
    '[转圈]': 'emoji_118@2x.png',
    '[鄙视]': 'emoji_119@2x.png',
    '[酷]': 'emoji_120@2x.png',
    '[钞票]': 'emoji_121@2x.png',
    '[钻戒]': 'emoji_122@2x.png',
    '[闪电]': 'emoji_123@2x.png',
    '[闭嘴]': 'emoji_124@2x.png',
    '[闹钟]': 'emoji_125@2x.png',
    '[阴险]': 'emoji_126@2x.png',
    '[难过]': 'emoji_127@2x.png',
    '[雨伞]': 'emoji_128@2x.png',
    '[青蛙]': 'emoji_129@2x.png',
    '[面条]': 'emoji_130@2x.png',
    '[鞭炮]': 'emoji_131@2x.png',
    '[风车]': 'emoji_132@2x.png',
    '[飞吻]': 'emoji_133@2x.png',
    '[飞机]': 'emoji_134@2x.png',
    '[饥饿]': 'emoji_135@2x.png',
    '[香蕉]': 'emoji_136@2x.png',
    '[骷髅]': 'emoji_137@2x.png',
    '[麦克风]': 'emoji_138@2x.png',
    '[麻将]': 'emoji_139@2x.png',
    '[鼓掌]': 'emoji_140@2x.png',
    '[龇牙]': 'emoji_141@2x.png'
  }
  const currentUser = reactive(JSON.parse(localStorage.getItem("user")))
  const friend = ref({})
  const history = ref({
    messages: [],
    allLoaded: false,
    loading: true
  })
  const onReceivedPrivateMessage = (message) => {
    if (message.senderId === friend.value.id) {
      history.value.messages.push(message);
      markPrivateMessageAsRead();
    }
    scrollToBottom();
  }

  const text = ref('')
  const to = ref({})
  const goEasy = inject("goEasy")
  const sendTextMessage = () => {
    if (!text.value.trim()) {
      ElMessage.error("请输入消息内容")
      return
    }
    goEasy.im.createTextMessage({
      text: text.value,
      to: to.value,
      onSuccess: (message) => {
        sendMessage(message);
        text.value = '';
      },
      onFailed: (err) => {
        console.log("创建消息err:", err);
      }
    });
  }

  //定义表情列表
  const emoji = ref({
    url: emojiUrl,
    map: emojiMap,
    visible: false,
    decoder: new EmojiDecoder(emojiUrl, emojiMap),
  })
  const onInputFocus = () => {
    emoji.value.visible = false;
  }
  const showEmojiBox = () => {
    emoji.value.visible = !emoji.value.visible;
  }
  const chooseEmoji = (emojiKey) => {
    text.value += emojiKey;
    emoji.value.visible = false;
  }
  const sendMessage = (message) => {
    let messageRef = ref()
    messageRef.value = message
    history.value.messages.push(messageRef.value);
    scrollToBottom();
    goEasy.im.sendMessage({
      message: messageRef.value,
      onSuccess: (message) => {
        console.log('发送成功', message);
      },
      onFailed: function (error) {
        console.log('发送失败:', error);
      }
    });
  }

  // 展示消息删除弹出框
  const actionPopup = ref({
        visible: false,
        message: null,
        recallable: false,
  })
  // 消息选择
  const messageSelector = ref({
    visible: false,
    ids: []
  })
  const showActionPopup = (message) => {
    const MAX_RECALLABLE_TIME = 3 * 60 * 1000; //3分钟以内的消息才可以撤回
    messageSelector.value.ids = [message.messageId];
    actionPopup.value.recallable = (Date.now() - message.timestamp) < MAX_RECALLABLE_TIME && Number(message.senderId) === currentUser.id && message.status === 'success';
    actionPopup.value.visible = true;
  }
  const deleteSingleMessage = () => {
    actionPopup.value.visible = false;
    deleteMessage();
  }
  const deleteMultipleMessages = () => {
    if (messageSelector.value.ids.length > 0) {
      messageSelector.value.visible = false;
      deleteMessage();
    }
  }
  const deleteMessage = () => {
    let conf = confirm("确认删除？");
    if (conf === true) {
      let selectedMessages = [];
      history.value.messages.forEach((message) => {
        if (messageSelector.value.ids.includes(message.messageId)) {
          selectedMessages.push(message);
        }
      });
      goEasy.im.deleteMessage({
        messages: selectedMessages,
        onSuccess: () => {
          selectedMessages.forEach((message) => {
            let index = history.value.messages.indexOf(message);
            if (index > -1) {
              history.value.messages.splice(index, 1);
            }
          });
          messageSelector.value.ids = [];
        },
        onFailed: (error) => {
          console.log('error:', error);
        },
      });
    } else {
      messageSelector.value.ids = [];
    }
  }
  const recallMessage = () => {
    let selectedMessages = [];
    history.value.messages.forEach((message) => {
      if (messageSelector.value.ids.includes(message.messageId)) {
        selectedMessages.push(message);
      }
    });
    actionPopup.value.visible = false;
    goEasy.im.recallMessage({
      messages: selectedMessages,
      onSuccess: () => {
        console.log('撤回成功');
      },
      onFailed: (error) => {
        console.log('撤回失败,error:', error);
      }
    });
  }

  const editRecalledMessage = (text) => {
    text.value = text;
  }
  const showCheckBox = () => {
    messageSelector.value.ids = [];
    messageSelector.value.visible = true;
    actionPopup.value.visible = false;
  }
  const selectMessages = (e) => {
    if (e.target.checked) {
      messageSelector.value.ids.push(e.target.value)
    } else {
      let index = messageSelector.value.ids.indexOf(e.target.value);
      if (index > -1) {
        messageSelector.value.ids.splice(index, 1);
      }
    }
  }
  const loadHistoryMessage = (isScrollToBottom) => {
    history.value.loading = true;
    //历史消息
    let lastMessageTimeStamp = null;
    let lastMessage = history.value.messages[0];
    if (lastMessage) {
      lastMessageTimeStamp = lastMessage.timestamp;
    }
    goEasy.im.history({
      userId: friend.value.id,
      lastTimestamp: lastMessageTimeStamp,
      limit: 10,
      onSuccess: (result) => {
        history.value.loading = false;
        let messages = result.content;
        if (messages.length === 0) {
          history.value.allLoaded = true;
        } else {
          if (lastMessageTimeStamp) {
            history.value.messages = messages.concat(history.value.messages);
          } else {
            history.value.messages = messages;
          }
          if (messages.length < 10) {
            history.value.allLoaded = true;
          }
          if (isScrollToBottom) {
            scrollToBottom();
            //收到的消息设置为已读
            markPrivateMessageAsRead();
          }
        }
      },
      onFailed: (error) => {
        //获取失败
        history.value.loading = false;
        console.log('获取历史消息失败, code:' + error.code + ',错误信息:' + error.content);
      },
    });
  }
  const markPrivateMessageAsRead = () => {
    goEasy.im.markMessageAsRead({
      id: to.value.id,
      type: to.value.type,
      onSuccess: function () {
        console.log('标记私聊已读成功');
      },
      onFailed: function (error) {
        console.log('标记私聊已读失败', error);
      },
    });
  }
  let scrollView = ref();
  let messageList = ref();
  const scrollToBottom = () => {
    nextTick(() => {
      scrollView.value.scrollTop = messageList.value.scrollHeight;
    });
  }
  const renderMessageDate = (message, index) => {
    if (index === 0) {
      return formatDate(message.timestamp);
    } else {
      if (message.timestamp - history.value.messages[index - 1].timestamp > 5 * 60 * 1000) {
        return formatDate(message.timestamp);
      }
    }
    return '';
  }

  const route = useRoute()
  const GoEasy = inject("GoEasy")
  onMounted(()=>{
    friend.value = {
      id: route.params.id,
      nickname: route.query.nickname,
      avatar: route.query.avatar,
    };
    to.value = {
      type: GoEasy.IM_SCENE.PRIVATE,
      id: friend.value.id,
      data: {nickname: friend.value.nickname, avatar: friend.value.avatar},
    };
    loadHistoryMessage(true);

    goEasy.im.on(GoEasy.IM_EVENT.PRIVATE_MESSAGE_RECEIVED, onReceivedPrivateMessage);
  })

  onBeforeUnmount(() => {
    goEasy.im.off(GoEasy.IM_EVENT.PRIVATE_MESSAGE_RECEIVED, onReceivedPrivateMessage);
  })
</script>

<style scoped>
  .chat-container {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    position: relative;
  }

  .chat-title {
    height: 40px;
    padding: 0 15px;
    display: flex;
    align-items: center;
    font-size: 18px;
  }

  .chat-avatar {
    width: 35px;
    height: 35px;
  }

  .chat-name {
    width: 400px;
    font-size: 15px;
    margin-left: 10px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    word-break: break-all;
  }

  .chat-main {
    display: flex;
    flex-direction: column;
    overflow-y: auto;
    flex: 1;
    scrollbar-width: thin;
  }

  .chat-main::-webkit-scrollbar {
    width: 0;
  }

  .chat-main .history-loaded {
    text-align: center;
    font-size: 12px;
    color: #cccccc;
    line-height: 20px;
  }

  .chat-main .load {
    text-align: center;
    font-size: 12px;
    color: #d02129;
    line-height: 20px;
    cursor: pointer;
  }

  .history-loading {
    width: 100%;
    text-align: center;
  }

  .time-tips {
    color: #999;
    text-align: center;
    font-size: 12px;
  }

  .message-list {
    padding: 0 10px;
  }

  .message-item {
    display: flex;
  }

  .message-item-checkbox {
    height: 50px;
    margin-right: 15px;
    display: flex;
    align-items: center;
  }

  .input-checkbox {
    position: relative;
  }

  .message-item-checkbox input[type="checkbox"]::before, .message-item-checkbox input[type="checkbox"]:checked::before {
    content: "";
    position: absolute;
    top: -3px;
    left: -3px;
    background: #FFFFFF;
    width: 18px;
    height: 18px;
    border: 1px solid #cccccc;
    border-radius: 50%;
  }

  .message-item-checkbox input[type="checkbox"]:checked::before {
    content: "\2713";
    background-color: #d02129;
    width: 18px;
    color: #FFFFFF;
    text-align: center;
    font-weight: bold;
  }

  .message-item-content {
    flex: 1;
    margin: 5px 0;
    overflow: hidden;
    display: flex;
  }

  .sender-info {
    margin: 0 5px;
  }

  .sender-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
  }

  .message-content {
    max-width: calc(100% - 100px);
  }

  .message-payload {
    display: flex;
    flex-direction: row;
    justify-content: flex-end;
    align-items: center;
  }

  .pending {
    background: url("../../../assets/imgs/pending.gif") no-repeat center;
    background-size: 13px;
    width: 15px;
    height: 15px;
  }

  .send-fail {
    background: url("../../../assets/imgs/failed.png") no-repeat center;
    background-size: 15px;
    width: 18px;
    height: 18px;
    margin-right: 3px;
  }

  .message-read {
    color: rgb(128, 128, 128);
    font-size: 12px;
    text-align: end;
    height: 16px;
  }

  .message-unread {
    color: #d02129;
    font-size: 12px;
    text-align: end;
    height: 16px;
  }

  .content-text {
    display: flex;
    align-items: center;
    text-align: left;
    background: #eeeeee;
    font-size: 14px;
    font-weight: 500;
    padding: 6px 8px;
    margin: 3px 0;
    line-height: 25px;
    white-space: pre-line;
    overflow-wrap: anywhere;
    border-radius: 8px;
    word-break: break-all;
  }

  .content-image {
    display: block;
    cursor: pointer;
  }

  .content-image img {
    max-height: 200px;
  }

  .content-audio {
    -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
  }

  .content-audio .audio-facade {
    min-width: 12px;
    background: #eeeeee;
    border-radius: 7px;
    display: flex;
    font-size: 14px;
    padding: 8px;
    margin: 5px 0;
    line-height: 25px;
    cursor: pointer;
  }

  .content-order {
    border-radius: 5px;
    border: 1px solid #eeeeee;
    padding: 8px;
    display: flex;
    flex-direction: column;
  }

  .content-order .order-id {
    font-size: 12px;
    color: #666666;
    margin-bottom: 5px;
  }

  .content-order .order-body {
    display: flex;
    font-size: 13px;
    padding: 5px;
  }

  .content-order .order-img {
    width: 70px;
    height: 70px;
    border-radius: 5px;
  }

  .content-order .order-name {
    margin-left: 10px;
    width: 135px;
    color: #606164;
  }

  .content-order .order-count {
    font-size: 12px;
    color: #666666;
    flex: 1;
  }

  .content-file {
    width: 240px;
    height: 65px;
    font-size: 15px;
    background: #FFFFFF;
    border: 1px solid #eeeeee;
    display: flex;
    align-items: center;
    padding: 10px;
    border-radius: 5px;
    cursor: pointer;
  }

  .content-file:hover {
    background: #f1f1f1;
  }

  .file-info {
    width: 194px;
    text-align: left;
  }

  .file-name {
    text-overflow: ellipsis;
    overflow: hidden;
    display: -webkit-box;
    word-break: break-all;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  .file-size {
    font-size: 12px;
    color: #ccc;
  }

  .file-img {
    width: 50px;
    height: 50px;
  }

  .message-item .self {
    overflow: hidden;
    display: flex;
    justify-content: flex-start;
    flex-direction: row-reverse;
  }

  .message-item .self .audio-facade {
    flex-direction: row-reverse;
  }

  .message-recalled {
    display: flex;
    align-items: center;
    justify-content: center;
    line-height: 28px;
    font-size: 13px;
    text-align: center;
    color: grey;
    margin-top: 10px;
  }

  .message-recalled-self {
    display: flex;
  }

  .message-recalled-self span {
    margin-left: 5px;
    color: #D02129;
    cursor: pointer;
  }

  .chat-footer {
    border-top: 1px solid #dcdfe6;
    width: 100%;
    height: 140px;
    background: #FFFFFF;
  }

  .action-delete {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    background-color: #FFFFFF;
  }

  .delete-btn {
    width: 25px;
    height: 25px;
    padding: 10px;
    background: #f5f5f5;
    border-radius: 50%;
    cursor: pointer;
    margin-bottom: 10px;
  }

  .action-box {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  .action-bar {
    display: flex;
    flex-direction: row;
    padding: 0 10px;
  }

  .action-bar .action-item {
    text-align: left;
    padding: 10px 0;
    position: relative;
  }

  .action-bar .action-item .iconfont {
    font-size: 22px;
    margin: 0 10px;
    z-index: 3;
    color: #606266;
    cursor: pointer;
  }

  .action-bar .action-item .iconfont:focus {
    outline: none;
  }

  .action-bar .action-item .iconfont:hover {
    color: #d02129;
  }

  .emoji-box {
    width: 600px;
    margin-top: -150px;
    position: absolute;
    top: -111px;
    left: -11px;
    z-index: 2007;
    background: #fff;
    border: 1px solid #ebeef5;
    padding: 12px;
    text-align: justify;
    font-size: 14px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    word-break: break-all;
    border-radius: 4px;
  }

  .emoji-item {
    width: 25px;
    height: 25px;
    margin: 0 2px;
  }

  .input-box {
    padding: 0 10px;
    flex: 1;
  }

  .input-content {
    border: none;
    resize: none;
    display: block;
    padding: 5px 15px;
    box-sizing: border-box;
    width: 100%;
    color: #606266;
    outline: none;
    background: #FFFFFF;
    word-break: break-all;
  }

  .send-box {
    padding: 5px 10px;
    text-align: right;
  }

  .send-button {
    margin-right: 15px;
    width: 70px;
    height: 28px;
    font-size: 15px;
    border-radius: 5px;
  }

  .action-popup {
    width: 848px;
    height: 100%;
    position: absolute;
    top: 0;
    left: -281px;
    background: rgba(51, 51, 51, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .action-popup-main {
    width: 150px;
    height: 120px;
    background: #ffffff;
    z-index: 100;
    border-radius: 10px;
    overflow: hidden;
  }

  .action-popup-main .action-item {
    text-align: center;
    line-height: 40px;
    font-size: 15px;
    color: #262628;
    border-bottom: 1px solid #efefef;
    cursor: pointer;
  }

  .image-preview {
    max-width: 750px;
    max-height: 500px;
    background: rgba(0, 0, 0, 0.8);
    display: flex;
    align-items: center;
    justify-content: center;
    position: fixed;
    margin: auto;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    z-index: 9998;
  }

  .image-preview img {
    max-width: 750px;
    max-height: 500px;
  }

  .image-preview .close {
    font-size: 50px;
    line-height: 24px;
    cursor: pointer;
    color: #FFFFFF;
    position: absolute;
    top: 10px;
    right: 5px;
    z-index: 1002;
  }

  .order-box {
    width: 848px;
    position: absolute;
    left: -281px;
    right: 0;
    top: 0;
    bottom: 0;
    z-index: 2007;
    font-size: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(33, 33, 33, 0.7);
  }

  .order-list {
    width: 300px;
    background: #F1F1F1;
    border-radius: 5px;
  }

  .order-list .title {
    font-weight: 600;
    font-size: 15px;
    color: #000000;
    margin-left: 10px;
    margin-right: 10px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .order-list .title span {
    font-size: 28px;
    font-weight: 400;
    cursor: pointer;
  }

  .order-list .order-item {
    padding: 10px;
    background: #FFFFFF;
    margin: 10px;
    border-radius: 5px;
    cursor: pointer;
  }

  .order-list .order-id {
    font-size: 12px;
    color: #666666;
    margin-bottom: 5px;
  }

  .order-list .order-body {
    display: flex;
    font-size: 13px;
    justify-content: space-between;
  }

  .order-list .order-img {
    width: 50px;
    height: 50px;
    border-radius: 5px;
  }

  .order-list .order-name {
    width: 160px;
  }

  .order-list .order-count {
    font-size: 12px;
    color: #666666;
    flex: 1;
  }

</style>
