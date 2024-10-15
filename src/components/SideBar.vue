<template>
  <div class="column is-3" style="height: 100%">
    <aside class="menu is-hidden-mobile">
      <p class="menu-label">
        功能
      </p>
      <ul class="menu-list">
        <router-link to="/market">
          购买积分
        </router-link>

        <router-link to="/createBot">
          创建bot
        </router-link>

        <router-link to="/">
          探索
        </router-link>
      </ul>
      <p class="menu-label">
        个人信息
      </p>
      <ul class="menu-list">
        <router-link to="/chat">
          所有聊天历史
        </router-link>

        <router-link to="/profile">
          个人资料
        </router-link>
      </ul>
      <p class="menu-label">
        更多
      </p>
      <ul class="menu-list">
        <router-link to="/contact">
          联系我们
        </router-link>

        <router-link to="/setting">
          设置
        </router-link>
      </ul>
    </aside>
  </div>
</template>

<script>
export default {
  name: 'SideBar'
}
</script>

<style scoped></style>