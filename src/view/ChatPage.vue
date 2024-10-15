<script>
import DropdownButton from '@/components/DropdownButton.vue';
import MarkdownIt from 'markdown-it';
import hljs from 'highlight.js';
import 'highlight.js/styles/github.css';
import 'github-markdown-css';

const md = new MarkdownIt();

export default {
  name: 'ChatPage',
  components: {
    DropdownButton
  },
  data() {
    return {
      messages: [],
      newMessage: '',
      isSingleTurn: true,
      files: []
    };
  },
  mounted() {
    this.highlightCode();
  },
  created() {
    this.fetchMessages();
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
    fetchMessages() {
      // todo: 从后端获取消息
    },
    // 发送消息
    sendMessage() {
      if (this.newMessage.trim() !== '') {
        this.messages.push({ text: this.newMessage, sender: 'user' });
        this.newMessage = '';
        // todo: 后端调用API生成回复，并将回复存储到数据库，前端获取回复
        // Simulate bot response
        setTimeout(() => {
          if (this.isSingleTurn) {
            // 清空之前的所有消息
            this.clearMessages();
          }
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
        }
      }, 10);
      this.messages.push({ text: '', sender });
    },

    // 清空消息
    clearMessages() {
      this.messages = [];
    },
    // 切换模式
    toggleMode() {
      this.clearMessages();
      this.isSingleTurn = !this.isSingleTurn;
    },
    // 触发文件上传
    triggerFileUpload() {
      this.$refs.imageInput.click();
    },
    // 处理文件上传
    handleFileUpload(event) {
      const file = event.target.files[0];
      if (file) {
        // Handle the uploaded image file
        this.files.push(file);
        console.log('Image uploaded:', file);
      }
    }
  }
};
</script>

<template>
  <div class="column is-9">
    <div class="card">
      <!-- 聊天框头部 -->
      <div class="card-header">
        <p class="card-header-title">你与GPT的聊天（{{ isSingleTurn ? "单轮" : "多轮" }}模式）</p>
        <button class="card-header-icon" aria-label="more options">
        <span class="icon">
          <i class="fas fa-angle-down" aria-hidden="true"></i>
        </span>
        </button>
      </div>

      <!-- 聊天框内容 -->
      <div class="card-content scrollable-content">
        <div class="messages">
          <div v-for="(message, index) in messages" :key="index" :class="['message', message.sender]">
            <div class="message-content markdown-body" v-html="message.text"></div>
          </div>
        </div>
      </div>

      <!-- 聊天框底部 -->
      <div class="card-footer">

        <div class="field is-grouped is-grouped-centered">
          <DropdownButton
              iconClass="fas fa-brush fa-2x"
              text="清空聊天记录"
              :handleClick="clearMessages"
          />
          <DropdownButton
              iconClass="fas fa-file-import fa-2x"
              text="文件上传"
              :handleClick="triggerFileUpload"
          />
          <DropdownButton
              iconClass="fas fa-sync-alt fa-2x"
              text="切换模式"
              :handleClick="toggleMode"
          />
          <div class="field has-addons">
            <p class="control is-expanded">
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
              <button class="button is-link" @click="sendMessage">发送</button>
            </div>
          </div>
        </div>
        <!-- 将文件输入设置为隐藏 -->
        <input type="file" ref="imageInput" style="display: none;" @change="handleFileUpload"/>

      </div>

    </div>
  </div>
</template>

<style scoped>
.field.is-grouped.is-grouped-centered {
  padding-top: 0;
  width: 100%;
}

.card-footer {
  border-top: none;
  height: 50px;
}

.scrollable-content {
  height: 600px;
  overflow-y: auto;
}

.messages {
  overflow-y: auto;
  margin-bottom: 20px;
}

.message {
  margin-bottom: 10px;
  display: flex;
  width: 100%;
}

.message.user .message-content {
  background: linear-gradient(to right, hsl(217, 71%, 53%), hsl(217, 71%, 73%));
  color: white;
  padding: 10px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-left: auto;
  max-width: 70%;
}

.message.bot .message-content {
  background: linear-gradient(135deg, #ffffff 0%, #a9a9a9 100%);
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-right: auto;
  max-width: 70%;
}

</style>
