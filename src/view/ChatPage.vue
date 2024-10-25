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
      robotInfo: {
        botId: this.$route.params.botId,
        name: '江澈',
      },
      messages: [],
      files: [],
      newMessage: '',
      isSingleTurn: true,
      isStreamingComplete: true,
      isFeedbackDialogVisible: false,
      isRateDialogVisible: false,
      feedbackDialogInfo: {
        messageId: 0,
        content: '',
        type: ''
      },
      rateDialogInfo: {
        botId: this.$route.params.botId,
        rating: 0,
        content: ''
      }
    };
  },
  mounted() {
    this.highlightCode();
  },
  created() {
    axiosInstance.get(`/bots/${this.robotInfo.botId}`).then(res => {
      this.robotInfo = res.data;
      this.$emit('update-action', '您和' + this.robotInfo.name + '的聊天' + (this.isSingleTurn ? '[单轮模式]' : '[多轮模式]'));
      this.fetchMessages(this.robotInfo.botId);
    }).catch(err => {
      console.log(err);
      this.$message.error('获取该机器人信息失败');
    });
  },
  methods: {
    // 高亮代码块
    highlightCode() {
      this.$nextTick(() => {
        document.querySelectorAll('pre code').forEach((block) => {
          hljs.highlightElement(block);
        });
      });
    },
    // 获取消息
    // eslint-disable-next-line no-unused-vars
    fetchMessages(botId) {
      // todo: 从后端获取消息（应由江澈完成）
      // 如果后端没有返回历史记录，则新建一个对话，否则获取历史记录
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
    sendMessage() {
      if (!this.isStreamingComplete) return;
      if (this.newMessage.trim() !== '') {
        this.messages.push({ text: this.newMessage, sender: 'user'});
        this.newMessage = '';
        if (this.isSingleTurn) {
          // 清空之前的所有消息
          this.clearMessages();
        }
        // todo: 后端调用API生成回复，并将回复存储到数据库，前端获取回复
        setTimeout(() => {
          const response = this.isSingleTurn
              ? `
# 单轮对话
这是一个段落，其中包含**加粗**和*斜体*文本。
- 这是一个列表项
- 这是另一个列表项
- 这是第三个列表项

[这是一个链接](https://www.example.com)

\`\`\`javascript
// 这是一个 JavaScript 代码块
function greet() {
   console.log("single");
}
greet();
\`\`\`
              `
              :
              `
# 多轮对话
这是一个段落，其中包含**加粗**和*斜体*文本。
- 这是一个列表项
- 这是另一个列表项
- 这是第三个列表项
[这是一个链接](https://www.example.com)
\`\`\`javascript
// 这是一个 JavaScript 代码块
function greet() {
    console.log("multi");
}
greet();
\`\`\`
              `;
          this.streamMessage(response, 'bot');
        }, 1000);
      }

    },

    // 流式消息, 逐字显示
    streamMessage(text, sender) {
      let index = 0;
      this.isStreamingComplete = false;
      const interval = setInterval(() => {
        if (index < text.length) {
          this.messages[this.messages.length - 1].text = text.substring(0, index + 1);
          index++;
        } else {
          clearInterval(interval);
          this.messages[this.messages.length - 1].text = md.render(text);
          this.$nextTick(() => {
            this.highlightCode();
          });
          this.isStreamingComplete = true;
        }
      }, 10);
      this.messages.push({ text: '', sender, isThumbUp: false, isThumbDown: false});
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
      this.$emit('update-action', '您和' + this.robotInfo.name + '的聊天' + (this.isSingleTurn ? '[单轮模式]' : '[多轮模式]'));
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
          <div v-if="message.sender==='bot'" class="title is-6">{{ robotInfo.name }}</div>
          <div :class="['message', message.sender]">
            <article class="message-content markdown-body" v-html="message.text"></article>
          </div>
          <div class="field is-grouped" v-if="message.sender==='bot'">
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
      </div>
    </el-main>
    <!-- 聊天框底部 -->
    <el-footer>

      <div class="field is-grouped is-grouped-centered">

        <DropUpButton
            iconClass="fa fa-edit fa-2x"
            text="Rate the Bot"
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

.message.user .message-content {
  background: var(--bulma-link-bold-invert);
  color: white;
  padding: 10px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-left: auto;
  max-width: 70%;
}

.message.bot .message-content {
  background: #f7f7f7;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-right: auto;
  max-width: 70%;
}

</style>
