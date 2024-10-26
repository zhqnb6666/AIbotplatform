<script>
import DropUpButton from '@/components/DropUpButton.vue';
import DropDownButton from '@/components/DropDownButton.vue';
import axiosInstance from "@/service/axiosInstance";
import hljs from 'highlight.js';
import MarkdownIt from 'markdown-it';
import 'highlight.js/styles/github.css';
import 'github-markdown-css';


const md = new MarkdownIt();

export default {
  name: 'ChatPage',
  components: {
    DropUpButton,
    DropDownButton
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
      },
      messages: [],
      files: [],
      newMessage: '',
      streamContent: '',
      isSingleTurn: false,
      isStreamingComplete: true,
      isFeedbackDialogVisible: false,
      isRateDialogVisible: false,
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
    axiosInstance.get(`/bots/${this.conversationBasicInfo.botId}`).then(res => {
      this.robotInfo = res.data;
      //是新聊天的情况
      if (isNewConversation) {
        this.createNewConversation();
      //用历史记录的情况
      } else {
        this.loadConversationHistory();
      }
    }).catch(err => {
      console.log(err);
      this.$message.error('获取该机器人信息失败');
    });
  },
  methods: {
    createNewConversation() {
      this.conversationBasicInfo.title = '您和' + this.robotInfo.name + '的聊天';
      this.$emit('update-action', this.conversationBasicInfo.title + (this.isSingleTurn ? '[单轮模式]' : '[多轮模式]'));
      axiosInstance.post(`/conversations`, this.conversationBasicInfo).then(res => {
        this.conversationBasicInfo.conversationId = res.data.conversationId;
      }).catch(err => {
        console.error(err);
        this.$message.error('创建对话失败');
      });
    },
    loadConversationHistory() {
      axiosInstance.get(`/conversations/${this.conversationBasicInfo.conversationId}/chatHistory`).then(res => {
        this.$emit('update-action', this.conversationBasicInfo.title + (this.isSingleTurn ? '[单轮模式]' : '[多轮模式]'));
        this.messages = res.data;
        this.messages.forEach((message) => {
          if (message.senderType === 'BOT') {
            //重新渲染markdown
            message.content = md.render(message.content);
          } else {
            //用户消息的存储格式为"{\"content\":\"写一段归并排序代码\"}"，所以需要解析
            message.content = JSON.parse(message.content).content;
          }
        });
        this.highlightCode();
      }).catch(err => {
        console.error(err);
        this.$message.error('获取对话失败');
      });
    },
    // 高亮代码块
    highlightCode() {
      this.$nextTick(() => {
        document.querySelectorAll('pre code').forEach((block) => {
          hljs.highlightElement(block);
        });
      });
    },
    // 点赞
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
    // 发送消息
    async sendMessage() {
      if (!this.isStreamingComplete || this.newMessage.trim() === '') return;

      this.isStreamingComplete = false;

      // 添加用户消息到列表
      const messageUser = {
        senderType: 'USER',
        content: this.newMessage
      };
      this.messages.push(messageUser);

      try {
        // 1. 发送消息获取messageId和botId
        const response = await axiosInstance.post(`/conversations/${this.conversationBasicInfo.conversationId}/messages`, {
          content: this.newMessage
        });

        const { botId, messageId } = response.data;

        // 2. 开始SSE流式传输
        this.startSse(botId, messageId);
        this.newMessage = '';

      } catch (err) {
        console.error(err);
        this.$message.error('发送失败');
        this.isStreamingComplete = true;
      }
    },

    // SSE传输
    startSse(botId, messageId) {
      // 添加机器人空消息占位
      this.messages.push({
        content: '',
        senderType: 'BOT',
        isThumbUp: false,
        isThumbDown: false
      });
      this.streamContent = ''; // 重置流式内容

      const eventSource = new EventSource(
          `http://localhost:8080/api/conversations/${botId}/messages/${messageId}/stream`
      );

      eventSource.addEventListener('message', (event) => {
        // 累加流式内容到临时存储
        this.streamContent += event.data;
        // 更新显示的消息内容
        this.messages[this.messages.length - 1].content += event.data;
      });

      eventSource.addEventListener('error', (event) => {
        console.error('EventSource error:', event);
        eventSource.close();
        this.isStreamingComplete = true;
      });

      eventSource.addEventListener('complete', async () => {
        // 渲染markdown
        this.messages[this.messages.length - 1].content = md.render(this.streamContent);
        // 保存完整的响应内容到后端
        try {
          await axiosInstance.post(`/conversations/${this.conversationBasicInfo.conversationId}/saveResponse`, {
            messageId: messageId,
            senderType: 'BOT',
            botId: botId,
            content: this.streamContent
          });
        } catch (err) {
          console.error('Failed to save response:', err);
          this.$message.error('保存失败');
        }

        // 高亮代码
        this.$nextTick(() => {
          this.highlightCode();
        });
        this.isStreamingComplete = true;
        eventSource.close();

      });
    },
    // 清空消息
    clearMessages() {
      if (!this.isStreamingComplete) return;
      this.messages = [];
    },
    // 切换模式
    toggleMode() {
      if (!this.isStreamingComplete) return;
      this.clearMessages();
      this.isSingleTurn = !this.isSingleTurn;
      this.$emit('update-action', this.conversationBasicInfo.title + (this.isSingleTurn ? '[单轮模式]' : '[多轮模式]'));
    },
    // 触发文件上传
    triggerFileUpload() {
      if (!this.isStreamingComplete) return;
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
    // 提交对某条消息的评价
    async submitReview() {
      const response = await axiosInstance.post(`/conversations/feedback`, this.feedbackDialogInfo);
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
    // 提交对机器人的评分
    async submitRating() {
      const response = await axiosInstance.post(`/bots/ratings`, {
        botId: this.rateDialogInfo.botId,
        rating: this.rateDialogInfo.rating,
      });
      if (response.status === 201) {
        this.$message.success('提交成功');
      } else {
        this.$message.error('提交失败');
      }
      this.isRateDialogVisible = false;
      this.rateDialogInfo.rating = 0;
      this.rateDialogInfo.content = '';
    }
  }
};

</script>

<template>
  <el-container>
    <el-main>
    <!-- 聊天框内容 -->
      <div class="scrollable-content">
        <div v-for="(message, index) in messages" :key="index" >
          <div v-if="message.senderType==='BOT'" class="title is-6">{{ robotInfo.name }}</div>
          <div :class="['message', message.senderType]">
            <article class="message-content markdown-body" v-html="message.content"></article>
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
        <div v-if="followUpSuggestions.length > 0" class="follow-up-suggestions">
          <h3>Follow-up Suggestions:</h3>
          <ul>
            <li v-for="(suggestion, index) in followUpSuggestions" :key="index">{{ suggestion }}</li>
          </ul>
        </div>
      </div>
    </el-main>
    <!-- 聊天框底部 -->
    <el-footer>

      <div class="field is-grouped is-grouped-centered">

        <DropUpButton
            iconClass="fa fa-edit fa-2x"
            text="给机器人评分"
            :handleClick="() => { isRateDialogVisible = true; }"
        />

        <DropUpButton
            iconClass="fa fa-trash-alt fa-2x"
            text="清空聊天记录"
            :handleClick="clearMessages"
        />

        <DropUpButton
            iconClass="fa fa-sync-alt fa-2x"
            text="切换模式"
            :handleClick="toggleMode"
        />
        <div class="field has-addons">
          <p class="control">
            <input
                v-model="newMessage"
                class="input"
                style="width: 500px;"
                type="text"
                placeholder="输入信息"
                @keyup.enter="sendMessage"
            />
          </p>
          <div class="control">
            <button class="button is-link is-light" @click="sendMessage">发送</button>
          </div>
        </div>
        <DropUpButton
            iconClass="fa fa-plus fa-2x"
            text="文件上传"
            :handleClick="triggerFileUpload"
        />
      </div>
      <!-- 将文件输入设置为隐藏 -->
      <input type="file" ref="imageInput" style="display: none;" @change="handleFileUpload"/>
    </el-footer>

    <el-dialog draggable center v-model="isFeedbackDialogVisible">
      <template #header>
        <span class="title is-6">请填写您对此条信息{{feedbackDialogInfo.type==='LIKE'?'满意':'不满意'}}的原因</span>
      </template>
      <el-input type="textarea" v-model="feedbackDialogInfo.content" placeholder="关注taffy谢谢喵" rows = 6></el-input>
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
          <el-input type="textarea" v-model="rateDialogInfo.content" placeholder="关注taffy谢谢喵"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="isRateDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitRating">Submit</el-button>
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
.follow-up-suggestions {
  margin-top: 20px;
}

.follow-up-suggestions h3 {
  font-size: 1.2em;
  margin-bottom: 10px;
}

.follow-up-suggestions ul {
  list-style-type: none;
  padding: 0;
}

.follow-up-suggestions li {
  background: #f7f7f7;
  padding: 10px;
  border-radius: 5px;
  margin-bottom: 5px;
}
</style>
