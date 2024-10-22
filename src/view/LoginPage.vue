<script>
import {mapActions, mapGetters} from "vuex";
import axiosInstance from "@/axiosInstance"
export default {
  name: "LoginPage",
  computed: {
    ...mapGetters(['isLoggedIn']),
    ...mapGetters(['personalProfile'])
  },
  data() {
    return {
      usernameOrEmail: "",
      password: ""
    }
  },
  methods: {
    ...mapActions(['updateLoginState']),
    ...mapActions(['updatePersonalProfile']),
    async login() {
      try {
        const response = await axiosInstance.post("/auth/login", {
          usernameOrEmail: this.usernameOrEmail,
          password: this.password
        });
        if(response.data==="Incorrect username or password"){
          this.$message({
            message: '用户名或密码错误',
            type: 'error'
          });
          return;
        }
        const jwt = response.data.jwt;
        localStorage.setItem('token', jwt);
        axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${jwt}`;
        await this.updateLoginState(true);
        axiosInstance.get('/profile').then((response) => {
          let { username, email, role, credits, avatarUrl, bio } = response.data;
          avatarUrl = `http://localhost:8080/avatars/${avatarUrl}`;
          this.updatePersonalProfile({ username, email, role, credits, avatarUrl, bio });
        })
        this.$router.push('/');
      } catch (error) {
        console.error('Login error:', error);
        alert('登录失败');
      }
    }
  }
}
</script>

<template>

<section class="hero is-fullheight">
  <div class="hero-body has-text-centered">
    <div class="login">
      <form @submit.prevent="login">
        <div class="field">
          <div class="control has-icons-left">
            <input v-model="usernameOrEmail" class="input is-medium is-rounded" type="email" placeholder="请输入邮箱或用户名" autocomplete="username" required />
            <span class="icon is-medium is-left">
              <i class="fas fa-user"></i>
            </span>
          </div>
        </div>
        <div class="field">
          <div class="control has-icons-left">
            <input v-model="password" class="input is-medium is-rounded" type="password" placeholder="**********" autocomplete="current-password" required />
            <span class="icon is-medium is-left">
              <i class="fas fa-key"></i>
            </span>
          </div>
        </div>
        <button class="button is-block is-fullwidth is-link is-medium is-rounded" type="submit" @click="login">
          登录
        </button>

      </form>
      <br />
      <nav class="level">
        <div class="level-item has-text-centered">
          <div>
            <a href="#">忘记密码?</a>
          </div>
        </div>
        <div class="level-item has-text-centered">
          <div>
            <router-link to="/register">创建一个账户</router-link>
          </div>
        </div>
      </nav>
    </div>
  </div>
</section>

</template>

<style src="@/assets/css/LoginPage.css" scoped>

</style>