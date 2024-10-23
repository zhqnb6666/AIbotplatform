<script>
import { mapState, mapActions } from 'vuex';
import axiosInstance from "@/axiosInstance";
import {Coin} from "@element-plus/icons-vue";

export default {
  name: 'ProfilePage',
  components: {Coin},
  computed: {
    ...mapState(['personalProfile']),

  },
  data() {
    return {
      robots: [
        {name: 'ChatGPT', description: 'A conversational agent that can generate human-like responses.'},
        {name: 'Claude', description: 'A robot that can help you with your daily tasks.'},
      ],
      isEditingProfile: false,
      selectedFile: null,
      tab_index: 0,
    }
  },
  created() {
    axiosInstance.get('/profile').then((response) => {
      this.robots = response.data.userBotList;
    }).catch((error) => {
      this.$message.error('获取个人资料失败');
      console.error(error);
    });
  },
  methods: {
    ...mapActions(['updatePersonalProfile']),
    editProfile() {
      this.isEditingProfile = true;
    },
    async submit() {
      // Update avatar
      if (this.selectedFile) {
        const formData = new FormData();
        formData.append('file', this.selectedFile);
        try {
          const response = await axiosInstance.put('/profile/change-avatar', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          });
          if (!response || response.status !== 200) {
            this.$message.error('修改失败');
            return;
          }
        } catch (error) {
          console.error(error);
          this.$message.error('修改失败');
          return;
        }
      }
      // Update bio
      try {
        const response = await axiosInstance.put('/profile/change-bio', {
          newBio: this.personalProfile.bio
        }, {
          headers: {
            'Content-Type': 'application/json'
          }
        });
        if(!response || response.status !== 200) {
          this.$message.error('修改失败');
          return;
        }
      } catch (error) {
        console.error(error);
        this.$message.error('修改失败');
        return;
      }
      this.$message.success('修改成功');
      await this.updatePersonalProfile(this.personalProfile);
      this.isEditingProfile = false;
    },
    cancel() {
      this.isEditingProfile = false;
    },
    changeTab(index) {
      this.tab_index = index;
      console.log('change tab');
    },
    isImageFile(file) {
      const imageExtensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'tiff'];
      const fileExtension = file.name.split('.').pop().toLowerCase();
      return imageExtensions.includes(fileExtension);
    },
    async handleImageUpload(event) {
      const file = event.target.files[0];
      const maxSize = 10 * 1024 * 1024;  // 10MB, adjust as needed
      if (this.isImageFile(file)) {
        this.$message.success('头像符合要求');
      } else {
        this.$message.error('请选择图片文件');
        return;
      }
      if (file && file.size <= maxSize) {
        this.selectedFile = file;
        this.personalProfile.avatarUrl = URL.createObjectURL(file);
        this.personalProfile.imageName = file.name;
      } else {
        this.$message.error('文件大小超过限制，请选择较小的文件');
      }
    }
  }
}
</script>

<template>
  <div class="container" v-if="!isEditingProfile">
    <!-- 个人资料页面 -->
    <div class="level">
      <div class="level-left">
        <div class="level-item">
          <el-avatar :size="128" :src="personalProfile.avatarUrl" />
        </div>
        <div class="level-item">
          <div class="control">
            <p class="title is-4">{{ personalProfile.username }}</p>
            <p class="subtitle is-6">@{{ personalProfile.email.split('@')[0] }}</p>
          </div>
        </div>
      </div>
      <div class="level-right">
        <button class="button is-medium is-white" @click="editProfile">编辑个人资料</button>
      </div>
    </div>
    <el-row class="subtitle is-5" align="middle">
      您是<strong>{{ personalProfile.role === 'USER'?'普通用户':'管理员'}}</strong>，目前拥有<strong>{{ personalProfile.credits }}</strong>
      <el-icon><Coin/></el-icon>
    </el-row>
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
          <el-avatar :size="64" :src="robot.avatarUrl" />
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
          <el-button size="large" text>查看</el-button>
        </div>
      </div>
    </div>
  </div>

  <div class="container" v-else>
    <!-- 资料修改页面 -->
    <div class="level">
      <div class="level-left">
        <div class="level-item label is-medium">
          头像:
        </div>
        <div class="level-item">
          <el-avatar :size="128" :src="personalProfile.avatarUrl" />
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
        <span class="file-name"> {{personalProfile.imageName || '原来的头像.jpeg'}} </span>
      </label>
    </div>

    <div class="field">
      <label class="label is-medium">用户名:</label>
      <input class="input is-medium" type="text" :placeholder="personalProfile.username" disabled/>
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
.container {
  width: 70%;
  margin-top: 24px;
}
</style>