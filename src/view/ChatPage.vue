
<script>
import DropdownButton from '@/components/DropdownButton.vue';
export default {
  name: 'ChatPage',
  components: {
    DropdownButton
  },
  data() {
    return {
      messages: [
        { text: 'Hello! How can I help you today?', sender: 'bot' },
        { text: 'I need some information about your services.', sender: 'user' }
      ],
      newMessage: '',
      isSingleTurn: true,
      files: []
    };
  },
  methods: {
    sendMessage() {
      if (this.newMessage.trim() !== '') {
        this.messages.push({ text: this.newMessage, sender: 'user' });
        this.newMessage = '';
        // Simulate bot response
        setTimeout(() => {
          const response = this.isSingleTurn
              ? 'This is a single-turn response.'
              : 'This is a multi-turn response based on history.';
          this.messages.push({ text: response, sender: 'bot' });
        }, 1000);
      }
    },
    clearMessages() {
      this.messages = [];
    },
    toggleMode() {
      this.isSingleTurn = !this.isSingleTurn;
    },
    triggerFileUpload() {
      this.$refs.imageInput.click();
    },
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
        <p class="card-header-title">你与GPT的聊天</p>
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
            <div class="message-content">{{ message.text }}</div>
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
        <input type="file" ref="imageInput" style="display: none;" @change="handleFileUpload" />

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
  background: linear-gradient(135deg, #6b73ff 0%, #000dff 100%);
  color: white;
  padding: 10px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-left: auto;
  max-width: 70%;
}

.message.bot .message-content {
  background: #e0e0e0;
  padding: 10px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-right: auto;
  max-width: 70%;
}
</style>