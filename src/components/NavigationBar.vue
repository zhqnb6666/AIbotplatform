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


      <div class="navbar-start" v-if="Object.keys(personalProfile).length === 0">
        <router-link class="navbar-item" to="/login">登录</router-link>
        <router-link class="navbar-item" to="/register">注册</router-link>
      </div>
      <div class="navbar-start" v-else>
        <router-link class="navbar-item" to="/logout">登出</router-link>
      </div>
    </div>
  </nav>

</template>



<script>
import { mapState, mapActions } from 'vuex';
export default {
  computed: {
    ...mapState(['personalProfile'])
  },
  methods: {
    ...mapActions(['updatePersonalProfile'])
  },
  name: 'NavigationBar',
}
</script>

<style scoped>

</style>