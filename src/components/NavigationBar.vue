<!-- src/components/NavigationBar.vue -->
<template>
  <!-- Navigation Bar -->
  <div class="navbar-container" v-if="!isLoggedIn">
    <router-link class="navbar-item" to="/login">登录</router-link>
    <router-link class="navbar-item" to="/register">注册</router-link>
  </div>
  <div class="navbar-container" v-else>
    <router-link class="navbar-item" to="/logout">登出</router-link>
    <router-link class="navbar-item" to="/reset-password">重置密码</router-link>
  </div>
</template>



<script>
import { mapGetters } from 'vuex';

export default {
  name: 'NavigationBar',
  computed: {
    ...mapGetters(['isLoggedIn'])
  }
}
</script>

<style scoped>
.navbar-container {
  display: flex;
  justify-content: center; /* 将项目放置在容器的右侧 */
  align-items: center; /* 垂直居中 */
  line-height: 60px;
  height: 60px;
}

.navbar-item {
  margin-left: 10px; /* 添加间距 */
  font-size: 16px
}
</style>