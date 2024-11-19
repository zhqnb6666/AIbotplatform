<script>
import axiosInstance from "@/service/axiosInstance";
export default {
  name: "SettingPage",
  data() {
    return {
      panelBlocks: [
        {
          title: "关于",
          link: "/about",
        },
        {
          title: "使用指南",
          link: "/guide",
        },
        {
          title: "联系我们",
          link: "/contact",
        },
      ],
    };
  },
  methods: {
    handleLogout() {
      //清除axiosInstance的jwt请求头
      axiosInstance.defaults.headers.common["Authorization"] = "";
      //清除localStorage中的jwt
      localStorage.removeItem("jwt");
      this.$router.push("/login");
    },
    handleResetPassword() {
      this.$router.push("/reset-password");
    },
  },
}
</script>

<template>
<div class="container">
  <div class="button-container">
    <el-button type="danger" text @click="handleLogout">登出</el-button>
    <el-button type="danger" text @click="handleResetPassword">重置密码</el-button>
  </div>
</div>
</template>

<style scoped>
.container {
  width: 50%;
  margin-top: 20px;
}
.button-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 10px;
}
.el-button {
  width: 150px;
  height: 50px;
  font-size: 18px;
}
:deep(.el-button+.el-button) {
  margin-left: 0;
}
</style>