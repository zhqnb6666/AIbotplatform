<script setup>
import { ref } from 'vue';

const messages = ref([]);
const newMessage = ref('');

const sendMessage = () => {
  if (newMessage.value.trim() !== '') {
    messages.value.push({
      text: newMessage.value,
      sender: 'user',
      timestamp: new Date().toLocaleTimeString(),
    });
    newMessage.value = '';

    // 模拟 ChatGPT 回复
    setTimeout(() => {
      messages.value.push({
        text: '这是一个自动回复。',
        sender: 'bot',
        timestamp: new Date().toLocaleTimeString(),
      });
    }, 1000);
  }
};
</script>

<template>
  <div class = "column is-9">


      <article class="messages">
        <div v-for="(message, index) in messages" :key="index" :class="['message', message.sender]">
          <div class="message-content box">
            <p>{{ message.text }}</p>
            <span class="timestamp">{{ message.timestamp }}</span>
          </div>
        </div>
      </article>
      <div class="field has-addons input-container">
        <div class="control is-expanded">
          <input class="input" v-model="newMessage" @keyup.enter="sendMessage" placeholder="输入消息..." />
        </div>
        <div class="control">
          <button class="button is-link" @click="sendMessage">发送</button>
        </div>
      </div>

  </div>

</template>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 600px;
  margin: 0 auto;
  overflow: hidden;
}

.messages {
  flex: 1;
  padding: 10px;
  overflow-y: auto;

}

.message {
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
}

.message.user .message-content {
  align-self: flex-end;
  background-color: #95ecff;
}

.message.bot .message-content {
  align-self: flex-start;
  background-color: #fff;
}

.message-content {
  padding: 10px;
  border-radius: 8px;
  max-width: 80%;
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
}

.timestamp {
  font-size: 0.8em;
  color: #999;
  margin-top: 5px;
  text-align: right;
}

.input-container {
  padding: 10px;
  border-top: 1px solid #ccc;
  background-color: #fff;
}
</style>