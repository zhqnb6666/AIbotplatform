<script>
import {More, User} from "@element-plus/icons-vue";

export default {
  name: "UserDisplay",
  components: {User, More},
  props: {
    user: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      userRating: this.user.avgRating
    }
  },
  methods: {
    viewProfile() {
      this.$router.push(`/view-profile?userId=${this.user.id}`);
    }
  }
}
</script>

<template>
  <div class="media">
    <figure class="media-left">
      <el-avatar
          :size="128"
          :src="`http://localhost:8080/${user.avatarUrl}`"
          alt="Image"
      ></el-avatar>
    </figure>
    <div class="media-content">
      <h6 class="title is-6">{{ user.username }}</h6>
      <p>{{ user.bio.trim() === ''?'暂无评价':user.bio }}</p>
      <br>
      评分: <el-rate v-model="userRating" disabled></el-rate>
    </div>
    <div class="media-right">
      <el-dropdown>
        <el-button type="info" circle text size="large" @click.stop>
          <el-icon :size="25"><More /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click.once="viewProfile">
              查看资料<el-icon class="el-icon--right"><User /></el-icon>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<style scoped>

</style>