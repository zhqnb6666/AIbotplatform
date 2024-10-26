<template>
  <div>
    <el-table :data="chatHistories" style="width: 100%" fit :table-layout="'fixed'" empty-text="目前暂无数据">
      <el-table-column prop="conversationId" label="聊天ID"></el-table-column>
      <el-table-column prop="title" label="标题"></el-table-column>
      <el-table-column label="操作一">
        <template #default="scope">
          <el-button @click="continueChat(scope.row)" type="primary" size="default">继续聊天</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作二">
        <template #default="scope">
          <el-button @click="deleteChat(scope.row)" type="danger" size="default">删除聊天</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import axiosInstance from "@/service/axiosInstance";
export default {
  name: 'ChatHistory',
  created() {
    axiosInstance.get('/conversations').then((response) => {
      this.chatHistories = response.data;
    }).catch((error) => {
      console.error('Failed to fetch chat histories:', error);
      this.$message.error('获取聊天历史失败');
    });
  },
  data() {
    return {
      chatHistories: [
      ],
    };
  },
  methods: {
    continueChat(chat) {
      // 例如，跳转到聊天页面并传递聊天历史ID
      const queryParameters = `botId=${chat.botId}&conversationId=${chat.conversationId}&title=${chat.title}`;
      this.$router.push('/chat?' + queryParameters);
    },
    deleteChat(chat) {
      axiosInstance.delete(`/conversations/${chat.conversationId}`).then(() => {
        this.chatHistories = this.chatHistories.filter((item) => item.conversationId !== chat.conversationId);
        this.$message.success('删除聊天成功');
      }).catch((error) => {
        console.error('Failed to delete chat:', error);
        this.$message.error('删除聊天失败');
      });
    },
  },
};
</script>