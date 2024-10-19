
<template>

    <div class="container">
      <div class="field">
        <label class="label">机器人类型</label>
        <div class="control">
          <div class="select">
            <select v-model="formInfo.robotType">
              <option v-for="type in robotType" :key="type">{{ type }}</option>
            </select>
          </div>
        </div>
      </div>

      <div class="field">
        <label class="label">名称</label>
        <label class="label" style="font-weight: lighter">必须唯一，并且使用4-20个字符，包括字母、数字、破折号、句号和下划线。</label>
        <input class="input" type="text" placeholder="BWDSADKJ" v-model="formInfo.name">
      </div>

      <div class="field">
        <label class="label">基础机器人</label>
        <div class="control">
          <div class="select">
            <select v-model="formInfo.model">
              <option v-for="type in basicRobot" :key="type">{{ type }}</option>
            </select>
          </div>
        </div>
      </div>

      <div class="field">
        <label class="label">提示词</label>
        <label class="label" style="font-weight: lighter">告诉您的机器人如何行事以及如何回应用户信息。尽可能明确和具体。</label>
        <div class="control">
          <textarea class="textarea" placeholder="例如，你是一名旅行助手。" v-model="formInfo.prompt"></textarea>
        </div>
      </div>

      <div class="field">
        <label class="label">机器人简介</label>
        <div class="control">
          <textarea class="textarea" placeholder="描述您机器人的功能以及可提供的使用体验" v-model="formInfo.description"></textarea>
        </div>
      </div>

      <div class="field">
        <label class="label">每token花费</label>
        <div class="control">
          <el-input-number v-model="formInfo.cost" :min="1" :max="1000" class="input"/>
        </div>
      </div>


      <div class="field is-grouped">
        <div class="control">
          <button class="button is-link" @click="onSubmit">创建机器人</button>
        </div>
        <div class="control">
          <button class="button is-link is-light" @click="cancel">取消</button>
        </div>
      </div>

    </div>

</template>
<script>
import axiosInstance from "@/axiosInstance";
export default {
  data() {
    return {
      formInfo: {
        robotType: '',
        name: '',
        model: '',
        prompt: '',
        description: '',
        cost: 0
      },
      robotType: [
        '提示词机器人',
        '图像生成',
        '视频生成',
        '角色扮演',
        '服务器机器人'
      ],
      basicRobot: [
        'GPT',
        'DALL-E',
        'CLIP',
        'VQ-VAE',
        'StyleGAN'
      ]
    }
  },
  methods: {
    async onSubmit() {
      try {
        const response = await axiosInstance.post('/bots', {
          name: this.formInfo.name,
          description: this.formInfo.description,
          model: this.formInfo.model,
          tokenCost: this.formInfo.cost,
        });
        if(response.status === 201) {
          this.$message({
            message: '创建成功',
            type: 'success'
          });
        } else {
          this.$message({
            message: '创建失败',
            type: 'error'
          });
          return;
        }
        const botId = response.data.botId;
        this.$router.push(`/chat/${botId}`);
      }catch (error) {
        this.$message({
          message: '创建失败',
          type: 'error'
        });
        console.error('Create bot error:', error);
      }
    },
    cancel() {
      this.$router.push('/');
    }
  }
}
</script>
<style scoped>
.container {
  margin-top: 20px;
  width: 70%;
}

:deep(.el-input__wrapper) {
  border: none;
  box-shadow: none;
}

:deep(.el-input__wrapper.is-focus) {
  border: none;
  box-shadow: none;
}

:deep(.el-input__wrapper:hover) {
  border: none;
  box-shadow: none;
}

:deep(.el-input-number__decrease:hover~.el-input:not(.is-disabled) .el-input__wrapper) {
  box-shadow: none;
}

:deep(.el-input-number__increase:hover~.el-input:not(.is-disabled) .el-input__wrapper) {
  box-shadow: none ;
}

:deep(.el-input__inner){
  font-size: 20px;
  font-weight: 400;
}
</style>