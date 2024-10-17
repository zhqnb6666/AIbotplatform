
<template>

    <div class="container" style="width: 70%;">
      <div class="field">
        <label class="label">机器人类型</label>
        <div class="control">
          <div class="select">
            <select>
              <option v-for="type in robotType" :key="type">{{ type }}</option>
            </select>
          </div>
        </div>
      </div>

      <div class="field">
        <label class="label">名称</label>
        <label class="label" style="font-weight: lighter">必须唯一，并且使用4-20个字符，包括字母、数字、破折号、句号和下划线。</label>
        <input class="input" type="text" placeholder="BWDSADKJ">
      </div>

      <div class="field">
        <label class="label">基础机器人</label>
        <div class="control">
          <div class="select">
            <select>
              <option v-for="type in basicRobot" :key="type">{{ type }}</option>
            </select>
          </div>
        </div>
      </div>

      <div class="field">
        <label class="label">提示词</label>
        <label class="label" style="font-weight: lighter">告诉您的机器人如何行事以及如何回应用户信息。尽可能明确和具体。</label>
        <div class="control">
          <textarea class="textarea" placeholder="例如，你是一名旅行助手。"></textarea>
        </div>
      </div>

      <div class="field">
        <label class="label">机器人简介</label>
        <div class="control">
          <textarea class="textarea" placeholder="描述您机器人的功能以及可提供的使用体验"></textarea>
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
export default {
  data() {
    return {
      form: {
        name: '',
        region: '',
        date1: '',
        date2: '',
        delivery: false,
        type: [],
        resource: '',
        desc: ''
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
    onSubmit() {
      // todo: 将表单数据提交到后端数据库
      this.$router.push('/chat');
    },
    cancel() {
      this.$router.push('/');
    }
  }
}
</script>