<script>


export default {
  name: 'ProfilePage',
  data() {
    return {
      robots: [
        {name: 'ChatGPT', description: 'A conversational agent that can generate human-like responses.'},
        {name: 'Claude', description: 'A robot that can help you with your daily tasks.'},
      ],
      isEditingProfile: false,
      personalProfile: {
        name:'tom',
        email:'tom@gmail.com',
        introduction:'我是'
      }
    }
  },
  methods: {
    editProfile() {
      this.isEditingProfile = true;
    },
    closeForm() {
      this.isEditingProfile = false;
    },
    submit() {
      // Save the personal profile
      this.isEditingProfile = false;
    },
    cancel() {
      this.isEditingProfile = false;
    }
  }
}
</script>

<template>
  <!-- Main container -->
  <div class="column is-9" v-if="!isEditingProfile">
    <!-- Profile card -->
    <div class="level">
      <div class="level-left">
        <div class="level-item">
          <figure class="image is-128x128">
            <img class="is-rounded"
                 src="https://bulma.io/assets/images/placeholders/128x128.png"
                 alt="Placeholder image"
            />
          </figure>
        </div>
        <div class="level-item">
          <div class="control">
            <p class="title is-4">江澈</p>
            <p class="subtitle is-6">@钓鱼佬</p>
          </div>
        </div>
      </div>
      <div class="level-right">
        <button class="button is-medium is-link" @click="editProfile">编辑个人资料</button>
      </div>
    </div>


    <div class="tabs">
      <ul>
        <li class="is-active"><a>{{robots.length}}个机器人</a></li>
        <li><a>0个帖子</a></li>
        <li><a>0个已关注的机器人</a></li>
      </ul>
    </div>

    <div class="control">
      <div class="media" v-for="robot in robots" :key="robot.name">
        <figure class="media-left">
          <p class="image is-64x64">
            <img :src="`https://bulma.io/assets/images/placeholders/64x64.png`" alt="Placeholder image" />
          </p>
        </figure>
        <div class="media-content">
          <div class="content">
            <p>
              <strong>{{robot.name}}</strong>
              <br>
              {{robot.description}}
            </p>
          </div>
        </div>
        <div class="media-right">
          <button class="button is-link">查看</button>
        </div>
      </div>
    </div>
  </div>
  <div class="column is-9" v-else>
    <div class="level">
      <div class="level-left">
        <div class="level-item label is-medium">
          头像:
        </div>
        <div class="level-item">
          <figure class="image is-128x128">
            <img class="is-rounded"
                 src="https://bulma.io/assets/images/placeholders/128x128.png"
                 alt="Placeholder image"
            />
          </figure>
        </div>
      </div>

    </div>
    <div class="file has-name">
      <label class="file-label">
        <input class="file-input" type="file" name="resume" />
        <span class="file-cta">
      <span class="file-icon">
        <i class="fas fa-upload"></i>
      </span>
      <span class="file-label"> 上传图片 </span>
    </span>
        <span class="file-name"> Screen Shot 2017-07-29 at 15.54.25.png </span>
      </label>
    </div>
    <div class="field">
      <label class="label is-medium">用户名:</label>
      <input class="input is-medium" type="text" :placeholder="personalProfile.name"/>
    </div>
    <div class="field">
      <label class="label is-medium">邮箱:</label>
      <input class="input is-medium" type="text" :placeholder="personalProfile.email" disabled />
    </div>
    <div class="field">
      <label class="label is-medium">个人简介</label>
      <div class="control">
        <textarea class="textarea is-medium" :placeholder="personalProfile.introduction"></textarea>
      </div>
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

</template>

<style scoped>

</style>