<template>
  <div class="column is-3 ">
    <aside class="menu is-hidden-mobile">
      <p class="menu-label">
        功能
      </p>
      <ul class="menu-list">
        <li><a>探索</a></li>
        <li><a>购买积分</a></li>
        <li><a>创建bot</a></li>
      </ul>
      <p class="menu-label">
        个人信息
      </p>
      <ul class="menu-list">
        <router-link to="/chat">所有聊天历史</router-link>
        <router-link to="/profile">个人资料</router-link>
      </ul>
      <p class="menu-label">
        更多
      </p>
      <ul class="menu-list">
        <li><a>设置</a></li>
        <li><a>反馈</a></li>
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