<template>

  <div v-if="showSideBar" class="common-layout">
    <el-container direction="horizontal">

      <SideBar />

      <el-container direction="vertical">
        <el-header class="title is-4">{{action}}</el-header>
        <el-main>
          <router-view v-if="isChatRoute" @update-action="updateAction"/>
          <router-view v-else />
        </el-main>
      </el-container>
    </el-container>
  </div>
  <router-view v-else />

</template>

<script>

import SideBar from "@/components/SideBar.vue";
import 'bulma/css/bulma.css';
export default {
  name: 'App',
  components: {
    SideBar
  },
  methods: {
    updateAction(newAction) {
      this.action = newAction;
    }
  },
  data() {
    return {
      showSideBar: true,
      action: '探索'
    }
  },
  computed: {
    isChatRoute() {
      return this.$route.matched.some(record => record.path === '/chat');
    }
  },
  watch: {
    $route(to) {
      this.showSideBar = to.path !== '/login' && to.path !== '/register' && to.path !== '/reset-password';
      const actions = {
        '/market': '购买积分',
        '/createBot': '创建bot',
        '/': '探索',
        '/profile': '个人资料',
        '/contact': '联系我们',
        '/setting': '设置',
        '/review': '评分',
        '/chatHistory': '聊天历史',
        '/reset-password': '重置密码'
      };
      this.action = actions[to.path] || '探索';
    }
  },
}
</script>
<style scoped>
.el-main {
  padding-top: 0;
  padding-bottom: 0;
}
.el-header {
  line-height: 60px;
  text-align: center;
  border-bottom: 1px solid var(--el-border-color);
  margin-bottom: 0;
}
.el-container {
  padding: 0;
  height: 100%;
}
.common-layout {
  height: 100vh;
}
</style>