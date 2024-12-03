<!-- src/components/RobotCard.vue -->
<template>
  <div class="media" style="align-items: center" @click="chat(robot.botId)">
    <!-- If showRightButton is false, show the number -->
    <div class="media-left" v-if="showIndex" style="width: 100px">
      <span class="title" :style="{ color: numberColor }">{{ index }}.</span>
    </div>
    <figure class="media-left">
      <el-avatar :size="64" :src="avatarURl" alt="Placeholder image" />
    </figure>
    <div class="media-content">
      <div class="content">
        <p>
          <strong class="title is-6">{{ robot.name }}</strong>
          <br>{{ robot.description }}
          <span style="display: flex; align-items: center; font-size: 15px; font-weight: 500">
            每条消息花费token: <strong>{{ robot.tokenCost }}</strong>
          </span>
        </p>
      </div>
    </div>
    <!-- If showRightButton is true, show the dropdown button -->
    <div class="media-right" v-if="showRightButton">
      <el-dropdown>
        <el-button type="info" circle text size="large" @click.stop>
          <el-icon :size="25"><More /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item v-if="showReviewButton" @click="review(robot.botId)">
              撰写评价<el-icon><EditPen /></el-icon>
            </el-dropdown-item>
            <el-dropdown-item v-if="showDeleteButton" @click="deleteRobot(robot.botId)">
              删除<el-icon><Delete /></el-icon>
            </el-dropdown-item>
            <el-dropdown-item v-if="showChatButton" @click="chat(robot.botId)">
              与之聊天<el-icon><ChatDotRound /></el-icon>
            </el-dropdown-item>
            <el-dropdown-item v-if="showRewardButton" @click="dialogVisible=true">
              打赏<el-icon><Coin /></el-icon>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
    <!-- This is for the rewarding el-dialog -->
    <el-dialog title="打赏" v-model="dialogVisible" @click.stop draggable>
      <el-form :model="rewardForm" :rules="rewardRules" ref="rewardForm" label-width="80px">
        <el-form-item label="打赏的token数" prop="rewardAmount" label-position="top">
          <el-input v-model="rewardForm.rewardAmount" placeholder="请输入打赏的token数"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="rewardSubmit">打赏</el-button>
          <el-button @click="dialogVisible = false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import {ChatDotRound, Coin, Delete, EditPen, More} from "@element-plus/icons-vue";
import {AVATAR_MAP, DEFAULT_AVATAR} from "@/util/constants";
import axiosInstance from "@/service/axiosInstance";
export default {
  name: 'RobotDisplay',
  components: {Coin, More, Delete, ChatDotRound, EditPen },
  props: {
    robot: {
      type: Object,
      required: true
    },
    index : {
      type: Number,
      default: 0
    },
    showIndex: {
      type: Boolean,
      default: false
    },
    showRightButton: {
      type: Boolean,
      default: true
    },
    showReviewButton: {
      type: Boolean,
      default: true
    },
    showDeleteButton: {
      type: Boolean,
      default: false
    },
    showChatButton: {
      type: Boolean,
      default: false
    },
    showRewardButton: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      dialogVisible: false,
      rewardForm: {
        rewardAmount: ''
      },
      rewardRules: {
        rewardAmount: [
          { required: true, message: '请输入打赏的token数', trigger: 'blur' },
          { validator: this.validateNumber, trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    avatarURl() {
      return AVATAR_MAP[this.robot.name] || DEFAULT_AVATAR;
    },
    numberColor() {
      const colors = ['rgb(237.5, 189.9, 118.5)', 'rgb(250, 181.5, 181.5)', 'rgb(121.3, 187.1, 255)', '#606266'];
      return colors[this.index <= 3 ? this.index - 1 : 3];
    }
  },
  methods: {
    review(botId) {
      this.$router.push(`/review/${botId}`);
    },
    chat(botId) {
      this.$router.push(`/chat?botId=${botId}`);
    },
    deleteRobot(botId) {
      this.$emit('delete', botId);
    },
    rewardSubmit() {
      this.$refs.rewardForm.validate((valid) => {
        if (valid) {
          axiosInstance.post(`/admin/reward?userId=${this.robot.userId}&token=${this.rewardForm.rewardAmount}`).then(() => {
            this.$message.success('打赏成功');
            this.dialogVisible = false;
          }).catch(() => {
            this.$message.error('打赏失败');
          });
        }
      });
    },
    validateNumber(rule, value, callback) {
      if (Number(value) < 0 || Number(value) > 1000) {
        callback(new Error('打赏的token数必须在0-1000之间'));
      }else if (!Number.isNaN(Number(value))) {
        callback();
      } else {
        callback(new Error('打赏必须为数字值'));
      }
    },
  }
}
</script>

<style scoped>
.media:hover {
  background-color: rgb(243.9, 244.2, 244.8);
}
.media{
  margin: 0;
  padding: var(--bulma-media-spacing);
}
</style>