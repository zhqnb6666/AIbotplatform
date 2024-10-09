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
      <div class="navbar-start">
        <router-link class="navbar-item" to="/login">登录</router-link>
        <router-link class="navbar-item" to="/register">注册</router-link>
      </div>
    </div>
  </nav>

</template>

<script>
export default {
  name: 'NavigationBar',
}
</script>

<style scoped>

</style>