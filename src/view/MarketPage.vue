<script>
import {mapActions, mapState} from "vuex";
import axiosInstance from "@/axiosInstance";

export default {
  name: "MarketPage",
  computed: {
    ...mapState(['personalProfile']),
    paymentMethodImage() {
      switch (this.paymentMethod) {
        case 'alipay':
          return 'path/to/alipay.png';
        case 'wechat':
          return 'path/to/wechat.png';
        case 'bankcard':
          return 'path/to/bankcard.png';
        default:
          return 'path/to/default.png';
      }
    }
  },
  data() {
    return {
      products: [
        { credits: 6, bonusCredits: 0, price: 6 },
        { credits: 30, bonusCredits: 3, price: 30 },
        { credits: 98, bonusCredits: 8, price: 98 },
        { credits: 128, bonusCredits: 12, price: 128 },
        { credits: 198, bonusCredits: 28, price: 198 },
        { credits: 328, bonusCredits: 98, price: 328 },
        { credits: 648, bonusCredits: 200, price: 648 }
      ],
      dialogVisible: false,
      selectedProduct: null,
      paymentMethod: '',
      convertToTokenVisible: false,
      convertedCredits: 0
    };
  },
  methods: {
    ...mapActions(['updatePersonalProfile']),
    openDialog(product) {
      this.selectedProduct = product;
      this.dialogVisible = true;
    },
    submitPayment() {
      const now = new Date();
      const description =
      `${this.personalProfile.username}在${now.toLocaleString()}通过${this.paymentMethod}充值${this.selectedProduct.price}元，获得${this.selectedProduct.credits}积分`;
      const payload = {
        amount: this.selectedProduct.credits,
        paymentMethod: this.paymentMethod,
        description: description
      };
      axiosInstance.post('/market/recharge', payload)
          .then((response) => {
            if (response.status === 201) {
              this.$message.success('支付成功');
              this.updatePersonalProfile(this.personalProfile);
              this.dialogVisible = false;
            } else {
              this.$message.error('支付失败');
              console.error('Unexpected status code:', response.status);
            }
          })
          .catch(error => {
            this.$message.error('支付失败');
            console.error(error);
          });
    },
    submitConvertToToken() {
      const now = new Date();
      const description =
      `${this.personalProfile.username}在${now.toLocaleString()}将${this.convertedCredits}积分兑换为${this.convertedCredits * 100}个token`;
      axiosInstance.post('/market/convert-to-token', {credits: 100, tokenAmount: this.convertedCredits * 100, description: description})
          .then((response) => {
            if (response.status === 201) {
              this.$message.success('兑换成功');
              this.updatePersonalProfile(this.personalProfile);
              this.convertToTokenVisible = false;
            } else {
              this.$message.error('兑换失败');
              console.error('Unexpected status code:', response.status);
            }
          })
          .catch(error => {
            this.$message.error('兑换失败');
            console.error(error);
          });
    }
  }
}
</script>

<template>
  <div class="container">
    <div id="head_account" class="title is-4 header-style">
      账号详情
    </div>
    <el-descriptions>
      <el-descriptions-item label="用户名">{{personalProfile.username}}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{personalProfile.email}}</el-descriptions-item>
      <br><el-descriptions-item label="用户等级">
        <el-tag size="small">{{personalProfile.role === 'USER' ? '普通用户' : '管理员'}}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="剩余积分">
        {{personalProfile.credits}}
      </el-descriptions-item>
    </el-descriptions>

    <div id="head_purchase" class="title is-4 header-style">
      购买积分
    </div>
    <el-row :gutter="20">
      <el-col :span="6" v-for="(product, index) in products" :key="index">
        <el-card style="text-align: center; margin-top: 20px">
          <el-image :src="`MarketPageImage/charge-${index + 1}.png`"></el-image>
          <div class="title is-4">
            {{ product.credits }}积分
          </div>
          <div class="subtitle is-6">
            额外赠送{{ product.bonusCredits }}积分
          </div>
          <template #footer>
            <div style="text-align: center">
              <el-button type="primary" @click="openDialog(product)" style="font-size: 25px; width: 80%; height: 48px">
                ¥ {{ product.price }}
              </el-button>
            </div>
          </template>
        </el-card>
      </el-col>
    </el-row>

    <div id="head_exchange" class="title is-4 is-spaced header-style">
      积分兑换token
    </div>
    <el-col :span="6">
      <el-card style="text-align: center; margin-top: 20px">
        <el-image :src="`MarketPageImage/tokenImage.png`"></el-image>
        <div class="title is-4">
          100个token
        </div>
        <template #footer>
          <div style="text-align: center">
            <el-button type="primary" @click="convertToTokenVisible = true" style="font-size: 25px; width: 80%; height: 48px">
              1积分
            </el-button>
          </div>
        </template>
      </el-card>
    </el-col>


    <!-- Payment Dialog -->
    <el-dialog draggable center v-model="dialogVisible" title="支付页面"  width="30%">
      <p style="font-size: 20px; margin-bottom: 10px">
        购买积分:<strong>{{ selectedProduct?.credits }}</strong>
      </p>
      <div style="text-align: center">
        <el-select v-model="paymentMethod" placeholder="选择支付方式">
          <el-option label="支付宝" value="alipay"></el-option>
          <el-option label="微信支付" value="wechat"></el-option>
          <el-option label="银行卡" value="bankcard"></el-option>
        </el-select>
        <el-image :src="paymentMethodImage" style="margin-top: 20px;width: 400px; height: 400px"></el-image>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPayment">提交</el-button>
      </template>
    </el-dialog>

    <!-- Convert Credit to Token Dialog -->
    <el-dialog v-model="convertToTokenVisible" title="积分兑换Token" center draggable>
      <el-form-item style="font-size: 20px;" label="被兑换的积分数量:">
        <el-input-number v-model="convertedCredits" :min="0" :max="personalProfile.credits" label="被兑换的积分数量"></el-input-number>
      </el-form-item>
      <p style="font-size: 20px; margin-top: 5px">
        兑换token: <strong>{{convertedCredits * 100}}</strong>
      </p>
      <template #footer>
        <el-button @click="convertToTokenVisible = false">取消</el-button>
        <el-button type="primary" @click="submitConvertToToken">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<style scoped>
.container {
  width: 80%;
  height: 100vh;
}
.header-style {
  border-left: 4px solid #409EFF;
  margin-top: 20px;
  padding-left: 10px;
}

</style>