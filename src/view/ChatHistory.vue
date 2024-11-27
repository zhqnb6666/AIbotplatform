<template>
  <div class="container" style="width: 80%;">
    <el-table
        :data="filteredChatHistories"
        style="width: 100%"
        height="700px"
        empty-text="目前暂无数据"
        row-key="conversationId"
        lazy
        virtual-scroll
        :default-sort="{ prop: 'conversationId', order: 'ascending' }"
        highlight-current-row
    >
      <el-table-column prop="conversationId" label="聊天ID"></el-table-column>
      <el-table-column prop="title" label="标题"></el-table-column>
      <el-table-column align="center">
        <template #header>
          <el-input v-model="search" placeholder="通过标题或者聊天ID进行搜索" />
        </template>
        <template #default="scope">
          <el-button @click="continueChat(scope.row)">
            继续聊天<el-icon class="el-icon--right"><ChatDotRound /></el-icon>
          </el-button>
          <el-button type="danger" @click="deleteChat(scope.row)">
            删除<el-icon class="el-icon--right"><Delete /></el-icon>
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import axiosInstance from "@/service/axiosInstance";
import {ChatDotRound, Delete} from "@element-plus/icons-vue";
export default {
  name: 'ChatHistory',
  components: {ChatDotRound, Delete},
  created() {
    axiosInstance.get('/conversations').then((response) => {
      this.chatHistories = response.data;
    }).catch((error) => {
      console.error('Failed to fetch chat histories:', error);
      this.$message.error('获取聊天历史失败');
    });
  },
  computed: {
    filteredChatHistories() {
      return this.chatHistories.filter((chat) => {
        return chat.title.includes(this.search) || chat.conversationId === parseInt(this.search);
      });
    },
  },
  data() {
    return {
      chatHistories: [
      ],
      search: '',
    };
  },
  methods: {
    continueChat(chat) {
      //跳转到聊天页面并传递聊天历史ID和标题
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