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
  methods: {
    viewProfile() {
      this.$router.push(`/view-profile?userId=${this.user.userId}`);
    }
  }
}
</script>

<template>
  <div class="media">
    <figure class="media-left">
      <el-avatar
          :size="100"
          :src="`http://localhost:8080/${user.avatarUrl}`"
          alt="Image"
      ></el-avatar>
    </figure>
    <div class="media-content">
      <h6 class="title is-5">{{ user.username }}</h6>
      <p class="subtitle is-6">邮箱: {{ user.email }}</p>
      <p>简介：{{ user.bio.trim() === ''?'暂无简介':user.bio }}</p>
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