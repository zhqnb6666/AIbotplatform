<!-- src/components/RobotCard.vue -->
<template>
  <div class="media" style="align-items: center" @click="chat(robot.botId)">
    <figure class="media-left">
      <el-avatar :size="64" :src="robot.avatarURl" alt="Placeholder image" />
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
    <div class="media-right">
      <el-dropdown>
        <el-button type="info" circle text size="large" @click.stop>
          <el-icon class="el-icon--right" :size="25"><More /></el-icon>
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
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import {ChatDotRound, Delete, EditPen, More} from "@element-plus/icons-vue";

export default {
  name: 'RobotDisplay',
  components: {More, Delete, ChatDotRound, EditPen },
  props: {
    robot: {
      type: Object,
      required: true
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
    }
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