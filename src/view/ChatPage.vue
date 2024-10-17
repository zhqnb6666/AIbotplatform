<script>
import DropUpButton from '@/components/DropUpButton.vue';
import DropDownButton from '@/components/DropDownButton.vue';
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
      messages: [],
      newMessage: '',
      isSingleTurn: true,
      files: [],
      robotName: 'GPT-3-turbo',
      isStreamingComplete: true
    };
  },
  mounted() {
    this.highlightCode();
  },
  created() {
    this.$emit('update-action', '您和' + this.robotName + '的聊天' + (this.isSingleTurn ? '[单轮模式]' : '[多轮模式]'));
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
    // 点赞
    thumbUp(index) {
      // todo: 后端调用API记录点赞
      if(this.messages[index].isThumbUp){
        this.messages[index].isThumbUp = false;
        console.log('取消点赞');
        return;
      }
      this.messages[index].isThumbUp = true;

      console.log('点赞');
    },
    // 点踩
    thumbDown(index) {
      if(this.messages[index].isThumbDown){
        this.messages[index].isThumbDown = false;
        console.log('取消点踩');
        return;
      }
      // todo: 后端调用API记录点踩
      this.messages[index].isThumbDown = true;
      console.log('点踩');
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
      this.$emit('update-action', '您和' + this.robotName + '的聊天' + (this.isSingleTurn ? '[单轮模式]' : '[多轮模式]'));
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
            <div v-if="message.sender==='bot'" class="title is-6">{{ robotName }}</div>
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
