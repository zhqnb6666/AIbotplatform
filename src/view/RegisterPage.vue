<template>
<div class="hero hero-body">
  <div class="container" style="width: 30%">
    <form @submit.prevent="submit" v-if="!isSubmitting">
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
      <div class="columns">
      <div class="column is-8" style="padding-top: 0;padding-left: 0">
        <div class="control has-icons-left">
          <input v-model="userInfo.verificationCode" class="input is-medium" type="text" placeholder="请输入验证码" required/>
          <span class="icon is-small is-left">
            <i class="fas fa-lock"> </i>
          </span>
        </div>
      </div>
      <div class="column" style="padding-top: 0;padding-right: 0">
        <button class="button is-medium fixed-width-button label" :disabled="isSendingCode" @click="sendVerificationCode">
          {{ isSendingCode ? `${verificationCodeValidTime}s` : verificationText }}
        </button>
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


    <div class="field is-grouped is-grouped-centered">
      <div class="control">
        <button class="button is-link is-medium" @click = 'submit'>提交</button>
      </div>
      <div class="control">
        <button class="button is-link is-light is-medium" @click = 'cancel'>取消</button>
      </div>
    </div>
    </form>
    <div class="skeleton-lines" v-else>
      <div v-for="n in 6" :key="n" class="skeleton-block"></div>
    </div>
  </div>
</div>


</template>
<script>
import axiosInstance from '@/axiosInstance';
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
      verificationCodeValidTime: 10,
      isSendingCode: false,
      isSubmitting: false,
      timer: null,
      verificationText: '发送验证码'
    }
  },
  watch: {
    'userInfo.username'(newVal) {
      this.userInfoCheck.username = newVal.length > 0 && newVal.length < 15;
    },
    'userInfo.email'(newVal) {
      this.userInfoCheck.email = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$/.test(newVal);
    },
    'userInfo.password'(newVal) {
      this.userInfoCheck.password = this.validatePassword(newVal);
    },
    'userInfo.confirmPassword'(newVal) {
      this.userInfoCheck.confirmPassword = newVal === this.userInfo.password && this.userInfoCheck.password;
    },
    'userInfo.verificationCode'(newVal) {
      this.userInfoCheck.verificationCode = newVal.length === 6;
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
    async submit() {
      if (Object.values(this.userInfoCheck).every(value => value)) {
        this.isSubmitting = true;
        try {
          const response = await axiosInstance.post('/auth/register', {
            username: this.userInfo.username,
            email: this.userInfo.email,
            password: this.userInfo.password,
            verificationCode: this.userInfo.verificationCode
          });
          if (response.data !== "User registered successfully") {
            this.$message({
              message: '注册失败，请稍后再试',
              type: 'error'
            });
            return;
          }
          this.$router.push('/login');
        } catch (error) {
          this.isSubmitting = false;
          console.error('Error during registration:', error);
          this.$message({
            message: '注册失败，请稍后再试',
            type: 'error'
          });
        }
      } else {
        this.$message({
          message: '请检查输入是否正确',
          type: 'error'
        });
      }
    },
    cancel() {
      // 路由跳转到登录页
      this.$router.push('/login');
    },
    async sendVerificationCode() {
      if (this.isSendingCode) return;
      if (!this.userInfoCheck.email) {
        this.$message({
          message: '请输入正确的邮箱',
          type: 'error'
        });
        return;
      }
      this.isSendingCode = true;
      this.verificationCodeValidTime = 30;
      this.timer = setInterval(() => {
        if (this.verificationCodeValidTime > 0) {
          this.verificationCodeValidTime--;
        } else {
          clearInterval(this.timer);
          this.isSendingCode = false;
          this.verificationText = '重新发送';
        }
      }, 1000);
      try {
        const response = await axiosInstance.post(`/auth/send-verification/${this.userInfo.email}`);
        if (response.data === "Verification code sent successfully") {
          this.$message({
            message: '验证码发送成功',
            type: 'success'
          });
        } else {
          this.$message({
            message: '验证码发送失败，请稍后再试',
            type: 'error'
          });

        }
      } catch (error) {
        this.$message({
          message: '验证码发送失败，请稍后再试',
          type: 'error'
        });

      }
    },
    beforeDestroy() {
      if (this.timer) {
        clearInterval(this.timer);
      }
    }
  }
}
</script>

<style scoped>
.fixed-width-button{
  width: 100%;
}
</style>