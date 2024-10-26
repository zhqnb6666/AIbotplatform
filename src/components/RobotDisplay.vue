<!-- src/components/RobotCard.vue -->
<template>
  <div class="media">
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
      <el-button v-if="showReviewButton" text size="default" @click="review(robot.botId)">撰写评价<el-icon><EditPen /></el-icon></el-button>
      <el-button v-if="showDeleteButton" text size="default" type="danger" @click="deleteRobot(robot.botId)">删除<el-icon><Delete /></el-icon></el-button>
      <el-button v-if="showChatButton" text size="default" @click="chat(robot.botId)">或者 与之聊天<el-icon><ChatDotRound /></el-icon></el-button>
    </div>
  </div>
</template>

<script>
import {ChatDotRound, Delete, EditPen} from "@element-plus/icons-vue";

export default {
  name: 'RobotDisplay',
  components: {Delete, ChatDotRound, EditPen },
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
      this.$emit('review', botId);
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
.media-right {
  display: flex;
  flex-direction: column;
}
</style>