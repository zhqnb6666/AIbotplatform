<!-- src/components/NavigationBar.vue -->
<template>
  <!-- Navigation Bar -->
  <nav class="navbar is-white">
    <div class="container">
      <div class="navbar-brand">
        <router-link class="navbar-item brand-text" to="/">主页</router-link>
        <div class="navbar-burger burger" data-target="navMenu">
          <span></span>
          <span></span>
          <span></span>
        </div>
      </div>
      <div class="navbar-start" v-if="!isLoggedIn">
        <router-link class="navbar-item" to="/login">登录</router-link>
        <router-link class="navbar-item" to="/register">注册</router-link>
      </div>
      <div class="navbar-start" v-else>
        <router-link class="navbar-item" to="/logout">登出</router-link>
        <router-link class="navbar-item" to="/reset-password">重置密码</router-link>
      </div>
    </div>
  </nav>

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

</style>