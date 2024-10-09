<script>
export default {
  name: "LoginPage",
  methods: {
    login() {
      this.$router.push('/');
    }
  }
}
</script>

<template>

<section class="hero is-fullheight">
  <div class="hero-body has-text-centered">
    <div class="login">
      <form>
        <div class="field">
          <div class="control has-icons-left">
            <input class="input is-medium is-rounded" type="email" placeholder="hello@gmail.com" autocomplete="username" required />
            <span class="icon is-medium is-left">
              <i class="fas fa-envelope"></i>
            </span>
          </div>
        </div>
        <div class="field">
          <div class="control has-icons-left">
            <input class="input is-medium is-rounded" type="password" placeholder="**********" autocomplete="current-password" required />
            <span class="icon is-medium is-left">
              <i class="fas fa-key"></i>
            </span>
          </div>
        </div>
        <br />
        <button class="button is-block is-fullwidth is-link is-medium is-rounded" type="submit" @click="login">
          登录
        </button>
      </form>
      <br>
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