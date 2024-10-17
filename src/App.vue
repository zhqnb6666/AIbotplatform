<template>

    <div v-if="showSideBar" class="common-layout">
      <el-container direction="horizontal">

        <SideBar />

        <el-container direction="vertical">
          <el-header class="title is-4">{{action}}</el-header>
          <el-main>
            <router-view @update-action="updateAction"/>
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
  watch: {
    $route(to) {
      this.showSideBar = to.path !== '/login' && to.path !== '/register';
      const actions = {
        '/market': '购买积分',
        '/createBot': '创建bot',
        '/': '探索',
        '/chat': '聊天',
        '/profile': '个人资料',
        '/contact': '联系我们',
        '/setting': '设置'
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
}
.el-container {
  margin: 0;
  padding: 0;
  height: 100%;
}
.common-layout {
  height: 100vh;
}
</style>