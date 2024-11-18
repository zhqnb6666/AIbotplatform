
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
        <label class="label" style="font-weight: lighter">使用4-20个字符，包括字母、数字、破折号、句号和下划线。</label>
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
          <textarea class="textarea" placeholder="例如，你是一名旅行助手。" v-model="formInfo.promptTemplate"></textarea>
        </div>
      </div>

      <div class="field">
        <label class="label">欢迎语</label>
        <label class="label" style="font-weight: lighter">告诉您的机器人向用户打招呼的方式</label>
        <div class="control">
          <textarea class="textarea" placeholder="你好，我是一个专门用来逗人开心的笑话机器人" v-model="formInfo.greetingMessage"></textarea>
        </div>
      </div>

      <div class="field">
        <label class="label">机器人简介</label>
        <div class="control">
          <textarea class="textarea" placeholder="描述您机器人的功能以及可提供的使用体验" v-model="formInfo.description"></textarea>
        </div>
      </div>

      <div class="field" v-if="this.personalProfile.role === 'ADMIN'">
        <label class="label">每token花费</label>
        <div class="control">
          <el-input-number v-model="formInfo.tokenCost" :min="1" :max="1000" class="input"/>
        </div>
      </div>

      <div class="field">
        <label class="label">temperature参数设置</label>
        <div class="control">
          <el-input-number v-model="formInfo.temperature" :min="0" :max="1" :step="0.01" class="input"/>
        </div>
      </div>

      <div class="field">
        <label class="label">可见性</label>
        <div class="control">
          <label class="radio">
            <input type="radio" name="accessibility" value="PUBLIC" v-model="formInfo.accessibility">
            公开
          </label>
          <label class="radio" style="margin-left: 5px">
            <input type="radio" name="accessibility" value="PRIVATE" v-model="formInfo.accessibility">
            私有
          </label>
        </div>
      </div>

      <div class="field is-grouped" style="margin-bottom: 10px">
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
import axiosInstance from "@/service/axiosInstance";
import {mapState} from "vuex";
export default {
  data() {
    return {
      formInfo: {
        robotType: '',
        name: '',
        model: '',
        promptTemplate: '',
        description: '',
        greetingMessage: '',
        tokenCost: 0,
        temperature: 0,
        accessibility: "PUBLIC"
      },
      robotType: [
        '提示词机器人',
        '图像生成',
        '视频生成',
        '角色扮演',
        '服务器机器人'
      ],
      basicRobot: [
        "GPT_3_5_TURBO",
        "GPT_4_32K",
        "GPT_4_O",
        "GPT_4_O_MINI",
        "ERNIE-Bot",
        "BLOOMZ-7B",
        "Llama-2-7b-chat",
        "Llama-2-13b-chat",
        "Llama-2-70b-chat",
        "Qianfan-Chinese-Llama-2-7B",
        "ChatGLM2-6B-32K",
        "AquilaChat-7B",
        "Stable-Diffusion-XL",
        "Calculator-Bot"
      ]
    }
  },
  computed: {
    ...mapState(['personalProfile']),
  },
  methods: {
    validateName(name) {
      const regex = /^[a-zA-Z0-9._-]{4,20}$/;
      return regex.test(name);
    },
    async onSubmit() {
      if (!this.validateName(this.formInfo.name)) {
        this.$message.error('名称无效。请使用4-20个字符，包括字母、数字、破折号、句号和下划线。');
        return;
      }
      try {
        const response = await axiosInstance.post('/bots', this.formInfo);
        if(response.status === 201) {
          this.$message.success('创建成功');
        } else {
          this.$message.error('创建失败');
          return;
        }
        const botId = response.data.botId;
        this.$router.push(`/chat?botId=${botId}`);
      }catch (error) {
        this.$message.error('创建失败');
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