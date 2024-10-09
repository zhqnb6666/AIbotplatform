<template>
<div class="hero hero-body">
  <div class="container" style="width: 30%">
    <div class="field">
      <label class="label is-medium">用户名</label>
      <div class="control has-icons-left">
        <input v-model="userInfo.username" :class="`input is-medium ${userInfoCheck.username ? 'is-success' : 'is-danger'}`" type="text" placeholder="请输入用户名" required/>
        <span class="icon is-small is-left">
          <i class="fas fa-user"></i>
        </span>
      </div>
      <p class="help" :class="{'is-success': userInfoCheck.username, 'is-danger': !userInfoCheck.username}">
        {{ userInfoCheck.username ? '用户名可用' : '用户名不可用（用户名长度需要在1到14之间）' }}
      </p>
    </div>
    <div class="field">
      <label class="label is-medium">邮箱</label>
      <div class="control has-icons-left">
        <input v-model="userInfo.email" :class="`input is-medium ${userInfoCheck.email ? 'is-success' : 'is-danger'}`" type="text" placeholder="请输入邮箱" required/>
        <span class="icon is-small is-left">
          <i class="fas fa-envelope"></i>
        </span>
      </div>
      <p class="help" :class="{'is-success': userInfoCheck.email, 'is-danger': !userInfoCheck.email}">
        {{ userInfoCheck.email ? '邮箱可用' : '邮箱不可用' }}
      </p>
    </div>

    <div class="field">
      <label class="label is-medium">验证码</label>
      <div class="control columns">
        <div class="column is-9" style="margin-left: -0.8rem">
          <div class="control has-icons-left">
            <input v-model="userInfo.verificationCode" :class="`input is-medium ${userInfoCheck.verificationCode ? 'is-success' : 'is-danger'}`" type="text" placeholder="请输入验证码" required/>
            <span class="icon is-small is-left">
              <i class="fas fa-key"> </i>
            </span>
          </div>
          <p class="help" :class="{'is-success': userInfoCheck.verificationCode, 'is-danger': !userInfoCheck.verificationCode}">
            {{ userInfoCheck.verificationCode ? '验证码正确' : '验证码错误' }}
          </p>
        </div>
        <div class="column" style="margin-left: -1rem">
          <button class="button is-medium" @click="sendVerificationCode">发送验证码</button>
        </div>
      </div>
    </div>
    <div class="field">
      <label class="label is-medium">密码</label>
      <div class="control has-icons-left">
        <input v-model="userInfo.password" :class="`input is-medium ${userInfoCheck.password ? 'is-success' : 'is-danger'}`" type="password" placeholder="请输入密码" required/>
        <span class="icon is-small is-left">
          <i class="fas fa-key"></i>
        </span>
      </div>
      <p class="help" :class="{'is-success': userInfoCheck.password, 'is-danger': !userInfoCheck.password}">
        {{ userInfoCheck.password ? '密码可用' : '密码不可用（密码需同时包含数字和大小写字母）' }}
      </p>
    </div>

    <div class="field">
      <label class="label is-medium">确认密码</label>
      <div class="control has-icons-left">
        <input v-model="userInfo.confirmPassword" :class="`input is-medium ${userInfoCheck.confirmPassword ? 'is-success' : 'is-danger'}`" type="password" placeholder="请再次输入密码" required/>
        <span class="icon is-small is-left">
          <i class="fas fa-key"></i>
        </span>
      </div>
      <p class="help" :class="{'is-success': userInfoCheck.confirmPassword, 'is-danger': !userInfoCheck.confirmPassword}">
        {{ userInfoCheck.confirmPassword ? '密码一致' : '密码不一致' }}
      </p>
    </div>


    <div class="field is-grouped">
      <div class="control">
        <button class="button is-link is-medium" @click = 'submit'>提交</button>
      </div>
      <div class="control">
        <button class="button is-link is-light is-medium" @click = 'cancel'>取消</button>
      </div>
    </div>
  </div>
</div>


</template>
<script>
export default {
  name: 'RegisterPage',
  data() {
    return {
      userInfo: {
        username: '',
        email: '',
        verificationCode: '',
        password: '',
        confirmPassword: '',
        personalProfile: ''
      },
      userInfoCheck: {
        username: false,
        email: false,
        verificationCode: false,
        password: false,
        confirmPassword: false,
      },
    }
  },
  watch: {
    'userInfo.username'(newVal) {
      this.userInfoCheck.username = newVal.length > 0 && newVal.length < 15;
    },
    'userInfo.email'(newVal) {
      this.userInfoCheck.email = /^[a-zA-Z0-9._%+-]+@gmail\.com$/.test(newVal);
    },
    'userInfo.password'(newVal) {
      this.userInfoCheck.password = this.validatePassword(newVal);
    },
    'userInfo.confirmPassword'(newVal) {
      this.userInfoCheck.confirmPassword = newVal === this.userInfo.password && this.userInfoCheck.password;
    }
  },
  methods: {
    validatePassword(password) {
      const hasUpperCase = /[A-Z]/.test(password);
      const hasLowerCase = /[a-z]/.test(password);
      const hasNumber = /\d/.test(password);
      const isLongEnough = password.length > 8;
      return hasUpperCase && hasLowerCase && hasNumber && isLongEnough;
    },
    submit() {
      if (Object.values(this.userInfoCheck).every(value => value)) {
        // 路由跳转到首页
        this.$router.push('/');
      } else {
        alert('请检查输入');
      }
    },
    cancel() {
      // 路由跳转到登录页
      this.$router.push('/login');
    },
    sendVerificationCode() {
      // todo:发送验证码
    }

  }
}
</script>

<style scoped>

</style>