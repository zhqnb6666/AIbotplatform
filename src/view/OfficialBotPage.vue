<script>
import axiosInstance from "@/service/axiosInstance";
import {AVATAR_MAP, DEFAULT_AVATAR, BASIC_ROBOTS} from "@/util/constants";
import {Delete, Download, Edit, Plus} from "@element-plus/icons-vue";
export default {
  name: "OfficialBotPage",
  components: {Download, Delete, Edit, Plus},
  computed: {
    filterTableData() {
      return this.officialBots.filter((data) => {
        return (
          data.name.toLowerCase().includes(this.search.toLowerCase()) ||
          data.description.toLowerCase().includes(this.search.toLowerCase())
        );
      });
    },
    BASIC_ROBOTS() {
      return BASIC_ROBOTS
    },
    infoDialogVisible() {
      return this.infoDialogState !== 0;
    }
  },
  created() {
    axiosInstance.get('/bots').then((response) => {
      this.officialBots = response.data;
      this.officialBots.forEach((bot) => {
        bot.avatar = this.avatarURl(bot.name);
      });
    }).catch((error) => {
      console.error('Failed to fetch official bots:', error);
      this.$message.error('获取官方机器人失败');
    });
  },

  data() {
    return {
      officialBots:[
      {
        botId: 0,
        avatar: "string",
        name: "string",
        description: "This is a description",
        model: "string",
        tokenCost: 0
      }
      ],
      search: "",
      infoDialogState: 0,
      currentBot: {
        botId: 0,
        avatar: "",
        name: "",
        description: "",
        model: "",
        tokenCost: 0,
        promptTemplate: '',
        greetingMessage: '',
        temperature: 0,
        accessibility: "PUBLIC"
      },
    };
  },
  methods: {
    avatarURl(robotName) {
      return AVATAR_MAP[robotName] || DEFAULT_AVATAR;
    },
    openEditDialog(bot) {
      this.currentBot = { ...bot };
      this.infoDialogState = 1;
    },
    openAddDialog() {
      this.currentBot = {
        botId: 0,
        avatar: "",
        name: "",
        description: "",
        model: "",
        tokenCost: 0,
        promptTemplate: '',
        greetingMessage: '',
        temperature: 0,
        accessibility: "PUBLIC"
      };
      this.infoDialogState = 2;
    },
    handleUpdate() {
      axiosInstance.put(`/admin/bot`, this.currentBot).then(() => {
        const index = this.officialBots.findIndex(bot => bot.botId === this.currentBot.botId);
        if (index !== -1) {
          this.officialBots.splice(index, 1, this.currentBot);
        }
        this.$message.success('更新成功');
        this.infoDialogState = 0;
      }).catch((error) => {
        console.error('Failed to update official bot:', error);
        this.$message.error('更新失败');
      });
    },
    handleAdd() {
      axiosInstance.post(`/admin/bot`, this.currentBot).then((response) => {
        this.officialBots.push(response.data);
        this.$message.success('添加成功');
        this.infoDialogState = 0;
      }).catch((error) => {
        console.error('Failed to add official bot:', error);
        this.$message.error('添加失败');
      });
    },
    handleBotInfo() {
      if(this.infoDialogState === 1) {
        this.handleUpdate();
      } else if(this.infoDialogState === 2) {
        this.handleAdd();
      }
    },
    handleDelete(botId) {
      axiosInstance.delete(`/admin/bot/${botId}`).then(() => {
        this.officialBots = this.officialBots.filter((bot) => bot.botId !== botId);
        this.$message.success('删除成功');
      }).catch((error) => {
        console.error('Failed to delete official bot:', error);
        this.$message.error('删除失败');
      });
    },
    handleExport() {
      axiosInstance.get('/admin/export').then((response) => {
        const excel_url = `http://localhost:8080/${response.data.split(' ')[5]}`;
        this.downloadFile(excel_url);
      }).catch((error) => {
        console.error('Failed to export official bots:', error);
        this.$message.error('导出失败');
      });
    },
    downloadFile(url) {
      const link = document.createElement('a');
      link.href = url;
      link.download = 'total_info.xlsx';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  }
}
</script>

<template>
<el-table :data="filterTableData"
          style="width: 100%"
          height="700px"
          lazy row-key="botId"
          highlight-current-row
          :default-sort="{ prop: 'botId', order: 'ascending' }">
  <el-table-column type="expand">
    <template #default="scope">
      <el-descriptions title="机器人详情" border direction="vertical" column="5">
        <el-descriptions-item
            :rowspan="2"
            :width="140"
            label="机器人图片"
            align="center"
        >
          <el-image
              style="width: 100px; height: 100px"
              :src="scope.row.avatar"
          />
        </el-descriptions-item>
        <el-descriptions-item label="机器人ID">{{scope.row.botId}}</el-descriptions-item>
        <el-descriptions-item label="名称">{{scope.row.name}}</el-descriptions-item>
        <el-descriptions-item label="模型">{{scope.row.model}}</el-descriptions-item>
        <el-descriptions-item label="token消耗">{{scope.row.tokenCost}}</el-descriptions-item>
        <el-descriptions-item label="描述">
          {{scope.row.description}}
        </el-descriptions-item>
      </el-descriptions>
    </template>
  </el-table-column>
  <el-table-column label="机器人ID" prop="botId" />
  <el-table-column label="名称" prop="name" />
  <el-table-column align="center">
    <template #header>
      <el-input v-model="search" placeholder="通过机器人或者描述进行搜索" />
    </template>
    <template #default="scope">
      <el-button @click="openEditDialog(scope.row)">
        编辑<el-icon class="el-icon--right"><Edit /></el-icon>
      </el-button>
      <el-button
          type="danger"
          @click="handleDelete(scope.row.botId)"
      >
        删除<el-icon class="el-icon--right"><Delete /></el-icon>
      </el-button>
    </template>
  </el-table-column>
</el-table>
<el-button class="mt-4" @click="openAddDialog">
  添加官方机器人<el-icon class="el-icon--right"><Plus /></el-icon>
</el-button>
<el-button class="mt-4" @click="handleExport">
  导出数据<el-icon class="el-icon--right"><Download /></el-icon>
</el-button>
<!-- 编辑或更新机器人对话框 -->
<el-dialog v-model="infoDialogVisible" draggable>
  <template #header>
    <div class="subtitle is-4" style="width: 100%;justify-content: center;display: flex">
      {{infoDialogState === 1 ? '更新机器人' : '添加机器人'}}
    </div>
  </template>
  <el-form v-model="currentBot" label-width="auto">
    <el-form-item label="名称">
      <el-input v-model="currentBot.name"></el-input>
    </el-form-item>
    <el-form-item label="模型">
      <el-select v-model="currentBot.model" placeholder="">
        <el-option
            v-for="item in BASIC_ROBOTS"
            :key="item"
            :label="item"
            :value="item"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="描述">
      <el-input type="textarea" v-model="currentBot.description"></el-input>
    </el-form-item>
    <el-form-item label="提示词">
      <el-input type="textarea" v-model="currentBot.promptTemplate"></el-input>
    </el-form-item>
    <el-form-item label="欢迎语">
      <el-input type="textarea" v-model="currentBot.greetingMessage"></el-input>
    </el-form-item>
    <el-form-item label="token消耗">
      <el-input-number v-model="currentBot.tokenCost" :min="1" :max="10000"></el-input-number>
    </el-form-item>
    <el-form-item label="temperature">
      <el-input-number v-model="currentBot.temperature" :min="0" :max="1" :step="0.01"></el-input-number>
    </el-form-item>
    <el-form-item label="可见性">
      <el-radio-group v-model="currentBot.accessibility">
        <el-radio label="PUBLIC">公开</el-radio>
        <el-radio label="PRIVATE">私有</el-radio>
      </el-radio-group>
    </el-form-item>
  </el-form>
  <template #footer>
    <el-button @click="infoDialogState = 0">取消</el-button>
    <el-button type="primary" @click="handleBotInfo()">{{infoDialogState === 1 ? '更新' : '添加'}}</el-button>
  </template>
</el-dialog>

</template>

<style scoped>
</style>
