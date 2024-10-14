<template>
  <div class="column is-3" style="height: 100%">
    <aside class="menu is-hidden-mobile">
      <p class="menu-label">
        功能
      </p>
      <ul class="menu-list">
        <!-- 使用插槽做跳转逻辑 -->
        <router-link to="/market">
          <template v-slot:default="{ navigate }">
            <a @click="redirectIfNotLoggedIn(navigate, $event)">购买积分</a>
          </template>
        </router-link>

        <router-link to="/createBot">
          <template v-slot:default="{ navigate }">
            <a @click="redirectIfNotLoggedIn(navigate, $event)">创建bot</a>
          </template>
        </router-link>

        <router-link to="/">
          <template v-slot:default="{ navigate }">
            <a @click="redirectIfNotLoggedIn(navigate, $event)">探索</a>
          </template>
        </router-link>



      </ul>
      <p class="menu-label">
        个人信息
      </p>
      <ul class="menu-list">

        <router-link to="/chat">
          <template v-slot:default="{ navigate }">
            <a @click="redirectIfNotLoggedIn(navigate, $event)">所有聊天历史</a>
          </template>
        </router-link>

        <router-link to="/profile">
          <template v-slot:default="{ navigate }">
            <a @click="redirectIfNotLoggedIn(navigate, $event)">个人资料</a>
          </template>
        </router-link>

      </ul>
      <p class="menu-label">
        更多
      </p>
      <ul class="menu-list">

        <router-link to="/contact">
          <template v-slot:default="{ navigate }">
            <a @click="redirectIfNotLoggedIn(navigate, $event)">联系我们</a>
          </template>
        </router-link>

        <router-link to="/setting">
          <template v-slot:default="{ navigate }">
            <a @click="redirectIfNotLoggedIn(navigate, $event)">设置</a>
          </template>
        </router-link>

      </ul>
    </aside>
  </div>
</template>

<script>
import {mapGetters} from "vuex";

export default {
  name: 'SideBar',
  computed: {
    ...mapGetters(['isLoggedIn'])
  },
  methods: {
    redirectIfNotLoggedIn(navigate, event) {
      event.preventDefault(); // 阻止导航默认行为
      const isLoggedIn = this.isLoggedIn;
      if (!isLoggedIn) {
        console.log('Not logged in, redirecting to login page');
        this.$router.push('/login');
      } else {
        navigate();
      }
    }
  }
}
</script>

<style scoped></style>