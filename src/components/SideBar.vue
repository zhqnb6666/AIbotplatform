<template>
  <el-aside width="200px">
    <NavigationBar />
    <el-menu router>

      <el-sub-menu index="1">
        <template #title>
          <span>功能</span>
        </template>
        <el-menu-item index="/market">
          购买积分
        </el-menu-item>
        <el-menu-item index="/createBot">
         创建bot
        </el-menu-item>
        <el-menu-item index="/">
          探索
        </el-menu-item>
        <el-menu-item index="/chat">
          聊天
        </el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="2">
        <template #title>
          <span>个人信息</span>
        </template>
        <el-menu-item index="/profile">
          个人资料
        </el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="3">
        <template #title>
          <span>更多</span>
        </template>
        <el-menu-item index="/contact">
          联系我们
        </el-menu-item>
        <el-menu-item index="/setting">
          设置
        </el-menu-item>
      </el-sub-menu>
    </el-menu>

  </el-aside>
</template>

<script>
import NavigationBar from "@/components/NavigationBar.vue";
export default {
  name: 'SideBar',
  components: {
    NavigationBar
  }
}
</script>

<style scoped>
.el-aside {
  color: var(--el-text-color-primary);
  height: 100%;
  border-right: 1px solid var(--el-border-color);
}

</style>