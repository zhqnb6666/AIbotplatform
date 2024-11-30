<script>
import DropDownButton from '@/components/DropDownButton.vue';
import MarkdownIt from 'markdown-it';
import 'highlight.js/styles/github.css';
import 'github-markdown-css';
import {mapActions, mapState} from "vuex";
// src/view/ChatPage.vue
import ChatService from "@/service/ChatService";
import hljs from "highlight.js";
import {Delete, Edit, Right, Switch, Upload} from "@element-plus/icons-vue";

const md = new MarkdownIt();
export default {
  name: 'ChatPage',
  components: {
    Upload,
    Switch,
    Delete,
    Edit,
    Right,
    DropDownButton
  },
  computed: {
    ...mapState(['personalProfile']),
  },
  data() {
    return {
      conversationBasicInfo: {
        conversationId: this.$route.query.conversationId,
        botId: this.$route.query.botId,
        title: this.$route.query.title
      },
      robotInfo: {
        botId: this.$route.query.botId,
        name: '江澈',
        tokenCost: 0
      },
      messages: [],
      files: [],
      newMessage: '',
      suggestionButtonWidth: '100px',
      isSingleTurn: false,
      isStreamingComplete: true,
      isFeedbackDialogVisible: false,
      isRateDialogVisible: false,
      loading: false,
      feedbackDialogInfo: {
        messageId: 0,
        content: '',
        type: ''
      },
      rateDialogInfo: {
        botId: this.$route.query.botId,
        rating: 0,
        content: ''
      },
      followUpSuggestions:[],
    };
  },

  created() {
    const isNewConversation = !this.conversationBasicInfo.conversationId;
    ChatService.getBotInfo(this.conversationBasicInfo.botId).then(res => {
      this.robotInfo = res.data;
      if (isNewConversation) {
        this.createNewConversation();
        this.loadGreeting();
      } else {
        this.loadConversationHistory();
      }
    }).catch(err => {
      console.log(err);
      this.$message.error('获取该机器人信息失败');
    });
  },
  mounted() {
    this.highlightCode();
  },
  methods: {
    ...mapActions(['updatePersonalProfile']),
    // 高亮代码
    highlightCode() {
      this.$nextTick(() => {
        document.querySelectorAll('pre code').forEach((block) => {
          hljs.highlightElement(block);
        });
      });
    },
    // 动态设置按钮宽度
    setButtonWidth() {
      this.$nextTick(() => {
        const messageContents = this.$refs.messageContents;
        const suggestionButtons = this.$refs.suggestionButtons;
        if (messageContents && suggestionButtons) {
          let textWidth = 0;
          suggestionButtons.forEach((button, index) => {
            const text = this.followUpSuggestions[index];
            const canvas = document.createElement('canvas');
            const context = canvas.getContext('2d');
            context.font = window.getComputedStyle(button.ref).font;
            textWidth = Math.max(context.measureText(text).width + 80, textWidth);
          });
          const lastMessageContent = messageContents[messageContents.length - 1];
          const computedStyle = window.getComputedStyle(lastMessageContent);
          this.suggestionButtonWidth = Math.max(textWidth, parseInt(computedStyle.width)) + 'px';
        }
      });
    },
    // 创建新对话，加载问候语
    createNewConversation() {
      this.conversationBasicInfo.title = '您和' + this.robotInfo.name + '的聊天';
      this.updateTitle();
      ChatService.createNewConversation(this.conversationBasicInfo).then(res => {
        this.conversationBasicInfo.conversationId = res.data.conversationId;
      }).catch(err => {
        console.error(err);
        this.$message.error('创建对话失败');
      });
    },
    loadGreeting() {
      ChatService.getGreeting(this.conversationBasicInfo.botId).then(res => {
        if(!res.data) return;
        this.messages.push({
          content: md.render(res.data),
          senderType: 'BOT'
        });
        //this.highlightCode();
      }).catch(err => {
        console.error(err);
        this.$message.error('获取问候语失败');
      });
    },
    updateTitle(){
      this.$emit('update-action', this.conversationBasicInfo.title + (this.isSingleTurn ? '[单轮模式]' : '[多轮模式]'));
    },
    // 加载对话历史,如果是新对话则加载问候语
    loadConversationHistory() {
      ChatService.loadConversationHistory(this.conversationBasicInfo.conversationId).then(res => {
        this.updateTitle();
        this.messages = res.data;
        this.messages.forEach((message) => {
          if (message.senderType === 'BOT') {
            message.content = md.render(message.content);
          } else {
            message.content = JSON.parse(message.content).content;
          }
        });
        this.highlightCode();
      }).catch(err => {
        console.error(err);
        this.$message.error('获取对话失败');
      });
    },
    async sendMessage() {
      if (!this.isStreamingComplete || this.newMessage.trim() === '') return;
      if (this.personalProfile.token < this.robotInfo.tokenCost) {
        this.$message.error('token余额不足');
        return;
      }
      this.followUpSuggestions = [];
      this.loading = true;
      this.isStreamingComplete = false;
      const messageUser = {
        senderType: 'USER',
        content: this.newMessage
      };
      this.messages.push(messageUser);
      if(this.messages.length === 3){
        await ChatService.predictTitle(this.newMessage).then(res => {
          this.conversationBasicInfo.title = res.data;
          this.updateTitle();
        }).catch(err => {
          console.error(err);
          this.$message.error('获取标题失败');
          this.loading = false;
          this.isStreamingComplete = true;
        })
      }
      if(this.isSingleTurn){
        this.clearMessages();
      }
      try {
        //先发送消息
        const response = await Promise.all([ChatService.sendMessage(this.conversationBasicInfo.conversationId, this.newMessage),
            ChatService.predictNext(this.newMessage)]);
        const { botId, messageId } = response[0].data;
        this.followUpSuggestions = response[1].data;
        this.startSse(botId, messageId);
        this.newMessage = '';
        this.personalProfile.token -= this.robotInfo.tokenCost;
        this.updatePersonalProfile(this.personalProfile);
      } catch (err) {
        console.error(err);
        if (err.response && err.response.status === 400 && err.response.data.code === 'INSUFFICIENT_TOKENS') {
          this.$message.error('token余额不足');
        }
        this.$message.error('发送失败');
        this.loading = false;
        this.isStreamingComplete = true;
      }
    },
    //被服务器使用SSE通信单方面推送
    startSse(botId, messageId) {
      this.messages.push({
        content: '',
        senderType: 'BOT',
        isThumbUp: false,
        isThumbDown: false
      });
      this.loading = false;
      let streamContent = '';
      const eventSource = new EventSource(
          `http://localhost:8080/api/conversations/${botId}/messages/${messageId}/stream`
      );
      // 服务器端推送消息
      eventSource.addEventListener('message', (event) => {
        const decodedData = atob(event.data); // Base64 decode
        const utf8Data = new TextDecoder('utf-8').decode(new Uint8Array([...decodedData].map(char => char.charCodeAt(0)))); // Convert to UTF-8
        streamContent += utf8Data;
        this.messages[this.messages.length - 1].content += utf8Data;
      });
      // 服务器端推送错误
      eventSource.addEventListener('error', (event) => {
        if (!this.isStreamingComplete) {
          this.$message.error('连接失败');
          console.error('EventSource error:', event);
        }
        eventSource.close();
        this.isStreamingComplete = true;
      });
      // 结束SSe
      eventSource.addEventListener('complete', async () => {
        this.isStreamingComplete = true;
        this.messages[this.messages.length - 1].content = md.render(streamContent);
        try {
          this.setButtonWidth();
          ChatService.saveResponse(this.conversationBasicInfo.conversationId, messageId, botId, streamContent);
        } catch (err) {
          console.error('Failed to save response:', err);
          this.$message.error('保存失败');
        }
        this.$nextTick(() => {
          this.highlightCode();
        });
        eventSource.close();
      });
    },
    handleFollowUpClick(suggestion) {
      this.newMessage = suggestion;
      this.sendMessage();
    },
    async submitReview() {
      const response = await ChatService.submitFeedback(this.feedbackDialogInfo);
      if (response.status === 201) {
        this.$message.success('提交成功');
      } else {
        this.$message.error('提交失败');
        return;
      }
      this.isFeedbackDialogVisible = false;
      this.feedbackDialogInfo.content = '';
      this.feedbackDialogInfo.messageId = 0;
      this.feedbackDialogInfo.type = '';
    },
    async submitRating() {
      const response = await ChatService.submitRating(this.rateDialogInfo);
      if (response.status === 201) {
        this.$message.success('提交成功');
      } else {
        this.$message.error('提交失败');
      }
      this.isRateDialogVisible = false;
      this.rateDialogInfo.rating = 0;
      this.rateDialogInfo.content = '';
    },
    thumbUp(index) {
      this.isFeedbackDialogVisible = true;
      this.feedbackDialogInfo.messageId = index;
      this.feedbackDialogInfo.type = 'LIKE';
      this.messages[index].isThumbUp = true;
    },
    // 点踩
    thumbDown(index) {
      this.isFeedbackDialogVisible = true;
      this.feedbackDialogInfo.messageId = index;
      this.feedbackDialogInfo.type = 'DISLIKE';
      this.messages[index].isThumbDown = true;
    },
    // 清空消息
    clearMessages() {
      this.createNewConversation();
      this.messages = [];
      this.followUpSuggestions = [];
    },
    // 切换模式
    toggleMode() {
      this.clearMessages();
      this.isSingleTurn = !this.isSingleTurn;
      this.updateTitle();
    },
    // 触发文件上传
    triggerFileUpload() {
      this.$refs.imageInput.click();
    },
    // 处理文件上传
    handleFileUpload(event) {
      if (!this.isStreamingComplete) return;
      const file = event.target.files[0];
      if (file) {
        // Handle the uploaded image file
        this.files.push(file);
        console.log('Image uploaded:', file);
      }
    },
  }
};

</script>

<template>
  <el-container>
    <el-main>
    <!-- 聊天框内容 -->
      <div class="scrollable-content" v-loading="loading">
        <div v-for="(message, index) in messages" :key="index" >
          <div v-if="message.senderType==='BOT'" class="title is-6">{{ robotInfo.name }}</div>
          <div :class="['message', message.senderType]">
            <article ref="messageContents" class="message-content markdown-body" v-html="message.content"></article>
          </div>
          <div class="field is-grouped" v-if="message.senderType==='BOT'">
            <DropDownButton
                iconClass="fa fa-thumbs-up"
                text="点赞"
                :handleClick="() => thumbUp(index)"
                :isThumbUp="message.isThumbUp"
                :isThumbDown="message.isThumbDown"
                :ThumbType="true"
            />
            <DropDownButton
                iconClass="fa fa-thumbs-down"
                text="点踩"
                :handleClick="() => thumbDown(index)"
                :isThumbUp="message.isThumbUp"
                :isThumbDown="message.isThumbDown"
                :ThumbType="false"
            />
          </div>
        </div>
        <div v-show="followUpSuggestions.length > 0 && isStreamingComplete"
             class="buttons" :style="{ width: suggestionButtonWidth}">
            <el-button ref="suggestionButtons" v-for="(suggestion, index) in followUpSuggestions"
                       :key="index" @click="handleFollowUpClick(suggestion)"
                       size="large" round
                       style="width: 100%; font-size: 16px">
              <!--因为使用了space-between的排版方式，因此需要一个占位元素，让文本位于中间，图标位于最右边-->
              <p/>
              <p>{{ suggestion }}</p>
              <el-icon class="el-icon--right" :size="22"><Right /></el-icon>
            </el-button>
        </div>
      </div>
    </el-main>
    <!-- 聊天框底部 -->
    <el-footer>

      <div class="field is-grouped is-grouped-centered">

        <el-tooltip content="给机器人评分" placement="top">
          <el-button :disabled="!isStreamingComplete" text size="large" @click="isRateDialogVisible = true">
            <el-icon :size="30"><Edit /></el-icon>
          </el-button>
        </el-tooltip>


        <el-tooltip content="清空对话" placement="top">
          <el-button :disabled="!isStreamingComplete" text size="large" @click="clearMessages">
            <el-icon :size="30"><Delete /></el-icon>
          </el-button>
        </el-tooltip>

        <el-tooltip content="切换模式" placement="top">
          <el-button :disabled="!isStreamingComplete" text size="large" @click="toggleMode">
            <el-icon :size="30"><Switch/></el-icon>
          </el-button>
        </el-tooltip>

        <div class="field has-addons">
          <p class="control">
            <input
                v-model="newMessage"
                class="input"
                style="width: 500px;"
                type="text"
                placeholder="输入信息"
                @keyup.enter="sendMessage"
                :disabled="!isStreamingComplete"
            />
          </p>
          <div class="control">
            <button :disabled="!isStreamingComplete" class="button is-link is-light" @click="sendMessage">发送</button>
          </div>
        </div>
        <el-tooltip content="上传文件" placement="top">
          <el-button :disabled="!isStreamingComplete" text icon="Upload" size="large" @click="triggerFileUpload">
            <el-icon :size="30"><Upload /></el-icon>
          </el-button>
        </el-tooltip>
      </div>
      <!-- 将文件输入设置为隐藏 -->
      <input type="file" ref="imageInput" style="display: none;" @change="handleFileUpload"/>
    </el-footer>

    <el-dialog draggable center v-model="isFeedbackDialogVisible">
      <template #header>
        <span class="title is-6">请填写您对此条信息{{feedbackDialogInfo.type==='LIKE'?'满意':'不满意'}}的原因</span>
      </template>
      <el-input type="textarea" v-model="feedbackDialogInfo.content" placeholder="暂无内容" rows = 6></el-input>
      <template #footer>
        <el-button @click="isFeedbackDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="isRateDialogVisible" title="对bot撰写评价">
      <el-form :model="rateDialogInfo">
        <el-form-item label="评分">
          <el-rate v-model="rateDialogInfo.rating"></el-rate>
        </el-form-item>
        <el-form-item label="评价">
          <el-input type="textarea" v-model="rateDialogInfo.content" placeholder="暂无内容"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="isRateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRating">提交</el-button>
      </template>
    </el-dialog>

  </el-container>
</template>

<style scoped>

.el-footer {
  border-top: none;
  height: 60px;
}

.scrollable-content {
  height: 75vh;
  overflow-y: auto;
}

.title.is-6{
  margin-left: auto;
}

.message {
  margin-bottom: 10px;
  display: flex;
  width: 100%;
}

.message.USER .message-content {
  background: var(--bulma-link-bold-invert);
  color: white;
  padding: 10px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-left: auto;
  max-width: 70%;
}

.message.BOT .message-content {
  background: #f7f7f7;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-right: auto;
  max-width: 70%;
}

:deep(.el-button+.el-button) {
  margin-left: 0;
}
:deep(.el-button span) {
  width: 100%;
  display: flex;
  justify-content: space-between;
  white-space: nowrap; /* 防止文本换行 */
  overflow: hidden; /* 隐藏溢出文本 */
  text-overflow: ellipsis; /* 使用省略号表示溢出文本 */
}
.buttons {
  margin-top: 0.75em;
  flex-direction: column;
}
</style>
