// src/mixins/sendVerificationMixin.js
import axiosInstance from '@/service/axiosInstance';

export const sendVerificationMixin = {
    data() {
        return {
            isSendingCode: false,
            verificationCodeValidTime: 30,
            timer: null,
            verificationText: '发送验证码'
        };
    },
    methods: {
        async sendVerificationCode(email, emailCheck) {
            if (this.isSendingCode) return;
            if (!emailCheck) {
                this.$message.error('请输入正确的邮箱');
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
                const response = await axiosInstance.post(`/auth/send-verification/${email}`);
                if (response.data === "Verification code sent successfully") {
                    this.$message.success('验证码发送成功');
                } else {
                    this.$message.error('验证码发送失败，请稍后再试');
                }
            } catch (error) {
                console.error('Error during sending verification code:', error);
                if (error.response && error.response.data === "Please wait before requesting a new code") {
                    this.$message.error('发送过于频繁，请稍后再次请求验证码');
                }
                this.$message.error('验证码发送失败，请稍后再试');
            }
        }
    },
    beforeUnmount() {
        if (this.timer) {
            clearInterval(this.timer);
        }
    }
};