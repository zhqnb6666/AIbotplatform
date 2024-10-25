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

      <RobotDisplay
          v-for="robot in robots"
          :key="robot.id"
          :robot="robot"
          :show-button="false"
      />

    </div>


  <div class="container" v-else>
    <!-- 资料修改页面 -->
    <div class="level">
      <div class="level-left">
        <div class="level-item label is-medium">
          头像:
        </div>
        <div class="level-item">
          <el-avatar :size="128" :src="localProfile.avatarUrl" />
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
        <span class="file-name"> {{this.selectedFileName || '原来的头像.jpeg'}}
        </span>
      </label>
    </div>

    <div class="field">
      <label class="label is-medium">用户名:</label>
      <input class="input is-medium" type="text" :placeholder="localProfile.username" disabled/>
    </div>
    <div class="field">
      <label class="label is-medium">邮箱:</label>
      <input class="input is-medium" type="text" :placeholder="localProfile.email" disabled/>
    </div>
    <div class="field">
      <label class="label is-medium">个人简介</label>
      <div class="control">
        <textarea class="textarea is-medium" v-model="localProfile.bio"></textarea>
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

  <!-- Modal for cropping image -->
  <el-dialog v-model="cropperVisible" width="50%">
    <div>
      <img ref="image" :src="imageUrl" alt="Source Image" style="width: 500px;height: 500px"/>
    </div>
    <template #footer>
      <el-button @click="cropperVisible = false">取消</el-button>
      <el-button type="primary" @click="cropImage">裁剪</el-button>
    </template>
  </el-dialog>
</template>

<script>
import { mapState, mapActions } from 'vuex';
import axiosInstance from "@/service/axiosInstance";
import { Coin } from "@element-plus/icons-vue";
import Cropper from 'cropperjs';
import 'cropperjs/dist/cropper.css';
import RobotDisplay from "@/components/RobotDisplay.vue";

export default {
  name: 'ProfilePage',
  components: {RobotDisplay, Coin },
  computed: {
    ...mapState(['personalProfile']),
  },
  data() {
    return {
      robots: [],
      isEditingProfile: false,
      tab_index: 0,
      localProfile: null,//对个人资料的修改
      //图片上传需要的变量
      selectedFileName: '',
      imageUrl: null,
      croppedImageUrl: null,
      cropper: null,
      cropperVisible: false,

    };
  },
  created() {
    axiosInstance.get('/profile').then((response) => {
      this.robots = response.data.userBotList;
    }).catch((error) => {
      this.$message.error('获取个人资料失败');
      console.error(error);
    });
  },
  beforeUnmount() {
    if (this.cropper) {
      this.cropper.destroy();
    }
  },
  methods: {
    ...mapActions(['updatePersonalProfile']),
    editProfile() {
      this.isEditingProfile = true;
      this.localProfile = JSON.parse(JSON.stringify(this.personalProfile)); // 创建一个本地副本，避免直接修改全局状态
    },
    async submit() {
      // Update avatar
      if (this.croppedImageUrl) {
        const blob = await fetch(this.croppedImageUrl).then(res => res.blob());
        const formData = new FormData();
        formData.append('file', blob, `avatar-${this.localProfile.email}.png`);
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
          newBio: this.localProfile.bio
        }, {
          headers: {
            'Content-Type': 'application/json'
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
      this.$message.success('修改成功');
      await this.updatePersonalProfile(this.localProfile); // Update global state
      this.isEditingProfile = false;
    },
    cancel() {
      this.localProfile = JSON.parse(JSON.stringify(this.personalProfile)); // Revert to global state
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
        this.selectedFileName = file.name;
        this.imageUrl = URL.createObjectURL(file);
        this.cropperVisible = true;
        // Destroy the old Cropper instance if it exists
        if (this.cropper) {
          this.cropper.destroy();
        }
        await this.$nextTick();
        this.cropper = new Cropper(this.$refs.image, {
          aspectRatio: 1,
          viewMode: 1,
          cropBoxResizable: false,
          dragMode: 'move',
          cropBoxMovable: false,
          background: false,
          guides: false,
          center: false,
          highlight: false,
          autoCropArea: 1,
          ready: () => {
            this.cropper.crop();
            // Apply the CSS class to make the crop box circular
            const cropBox = this.$refs.image.parentNode.querySelector('.cropper-crop-box');
            const viewBox = this.$refs.image.parentNode.querySelector('.cropper-view-box');
            cropBox.style.borderRadius = '50%';
            viewBox.style.borderRadius = '50%';
          },
        });
      } else {
        this.$message.error('文件大小超过限制，请选择较小的文件');
      }
    },
    cropImage() {
      if (this.cropper) {
        const canvas = this.cropper.getCroppedCanvas({
          width: 200,
          height: 200,
          imageSmoothingQuality: 'high',
        });
        this.croppedImageUrl = canvas.toDataURL('image/png');
        this.localProfile.avatarUrl = this.croppedImageUrl;
        this.cropperVisible = false;
      }
    },

  }
};
</script>

<style scoped>
.container {
  width: 70%;
  margin-top: 24px;
}
/* 其他样式 */
.cropper-crop-box, .cropper-view-box {
  border-radius: 50%;
}
</style>