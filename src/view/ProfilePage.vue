<script>
import { mapState, mapActions } from 'vuex';

export default {
  name: 'ProfilePage',
  computed: {
    ...mapState(['personalProfile'])
  },
  data() {
    return {
      robots: [
        {name: 'ChatGPT', description: 'A conversational agent that can generate human-like responses.'},
        {name: 'Claude', description: 'A robot that can help you with your daily tasks.'},
      ],
      isEditingProfile: false,
      tab_index: 0,
    }
  },
  methods: {
    editProfile() {
      this.isEditingProfile = true;
    },
    submit() {
      this.updatePersonalProfile(this.personalProfile);
      this.isEditingProfile = false;
    },
    cancel() {
      this.isEditingProfile = false;
    },
    changeTab(index) {
      this.tab_index = index;
      console.log('change tab');
    },
    handleImageUpload(event) {
      const file = event.target.files[0];
      if (file) {
        this.personalProfile.imageName = file.name;
        const reader = new FileReader();
        reader.onload = (e) => {
          this.updatePersonalProfile({...this.personalProfile, image: e.target.result});
        };
        reader.readAsDataURL(file);
      }
    },
    ...mapActions(['updatePersonalProfile'])
  }
}
</script>

<template>
  <div class="column is-9" v-if="!isEditingProfile">
    <!-- 个人资料页面 -->
    <div class="level">
      <div class="level-left">
        <div class="level-item">
          <figure class="image is-128x128">
            <img class="is-rounded"
                 :src="personalProfile.image"
                 alt="Placeholder image"
            />
          </figure>
        </div>
        <div class="level-item">
          <div class="control">
            <p class="title is-4">{{ personalProfile.name }}</p>
            <p class="subtitle is-6">@{{ personalProfile.email.split('@')[0] }}</p>
          </div>
        </div>
      </div>
      <div class="level-right">
        <button class="button is-medium is-white" @click="editProfile">编辑个人资料</button>
      </div>
    </div>

    <div class="tabs">
      <ul>
        <li :class="{'is-active':tab_index === 0}" @click="changeTab(0)"><a>{{ robots.length }}个机器人</a></li>
        <li :class="{'is-active':tab_index === 1}" @click="changeTab(1)"><a>使用情况</a></li>
        <li :class="{'is-active':tab_index === 2}" @click="changeTab(2)"><a>他人评论</a></li>
      </ul>
    </div>
    <div class="control">
      <div class="media" v-for="robot in robots" :key="robot.name">
        <figure class="media-left">
          <p class="image is-64x64">
            <img :src="`https://bulma.io/assets/images/placeholders/64x64.png`" alt="Placeholder image"/>
          </p>
        </figure>
        <div class="media-content">
          <div class="content">
            <p>
              <strong>{{ robot.name }}</strong>
              <br>
              {{ robot.description }}
            </p>
          </div>
        </div>
        <div class="media-right">
          <button class="button is-white">查看</button>
        </div>
      </div>
    </div>
  </div>

  <div class="column is-9" v-else>
    <!-- 资料修改页面 -->
    <div class="level">
      <div class="level-left">
        <div class="level-item label is-medium">
          头像:
        </div>
        <div class="level-item">
          <figure class="image is-128x128">
            <img class="is-rounded"
                 :src="personalProfile.image"
                 alt="Placeholder image"
            />
          </figure>
        </div>
      </div>
    </div>

    <div class="file has-name">
      <label class="file-label">
        <input class="file-input" type="file" name="resume" @change="handleImageUpload"/>
        <span class="file-cta">
          <span class="file-icon">
            <i class="fas fa-upload"></i>
          </span>
          <span class="file-label"> 上传图片 </span>
        </span>
        <span class="file-name"> {{ personalProfile.imageName }} </span>
      </label>
    </div>

    <div class="field">
      <label class="label is-medium">用户名:</label>
      <input class="input is-medium" type="text" v-model="personalProfile.name"/>
    </div>
    <div class="field">
      <label class="label is-medium">邮箱:</label>
      <input class="input is-medium" type="text" :placeholder="personalProfile.email" disabled/>
    </div>
    <div class="field">
      <label class="label is-medium">个人简介</label>
      <div class="control">
        <textarea class="textarea is-medium" v-model="personalProfile.bio"></textarea>
      </div>
    </div>
    <div class="field is-grouped">
      <div class="control">
        <button class="button is-link is-medium" @click='submit'>提交</button>
      </div>
      <div class="control">
        <button class="button is-link is-light is-medium" @click='cancel'>取消</button>
      </div>
    </div>
  </div>

</template>

<style scoped>

</style>