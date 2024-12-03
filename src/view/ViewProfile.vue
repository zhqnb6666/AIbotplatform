<template>
  <div class="container head">
    <!-- 个人资料页面 -->
    <div class="level" style="margin-top: 1rem">
      <div class="level-left">
        <div class="level-item">
          <el-avatar :size="128" :src="viewedProfile.avatarUrl" />
        </div>
        <div class="level-item">
          <div class="control">
            <p class="title is-4">{{ viewedProfile.username }}</p>
            <p class="subtitle is-6">@{{ viewedProfile.email.split('@')[0] }}</p>
          </div>
        </div>
      </div>
    </div>

    <div class="columns" style="height: 20%">
      <div class="column is-one-third" >
        <div class="box" style="height: 100%">
          <p class="title is-5 is-spaced">用户身份</p>
          <p class="subtitle is-4">{{ viewedProfile.role === 'USER' ? '普通用户' : '管理员' }}</p>
        </div>
      </div>
      <div class="column is-one-third" >
        <div class="box" style="height: 100%">
          <p class="title is-5">平均评分</p>
          <el-rate v-model="viewedProfile.avgRating"
                   show-score
                   text-color="#ff9900"
                   :score-template="`${viewedProfile.avgRating !== 0 ? `${viewedProfile.avgRating} 分` : '暂无评分'}`"
                   disabled/>
        </div>
      </div>
    </div>

    <div class="tabs">
      <ul>
        <li :class="{'is-active':tab_index === 0}" @click="changeTab(0)"><a>{{ robots.length }}个机器人</a></li>
        <li :class="{'is-active':tab_index === 2}" @click="changeTab(2)"><a>{{ feedbackList.length }}条他人评论</a></li>
      </ul>
    </div>

    <div v-if="tab_index === 0">
      <div v-if="robots.length !== 0">
        <RobotDisplay
            v-for="robot in robots"
            :key="robot.id"
            :robot="robot"
            :show-review-button="false"
            :show-chat-button="true"
            :show-delete-button="false"
        />
      </div>
      <div class="message" v-else>
        暂无数据
      </div>
    </div>

    <div v-if="tab_index === 2">
      <div v-if="feedbackList.length !== 0">
        <FeedbackDisplay
            v-for="feedback in feedbackList"
            :key="feedback.name"
            :feedback="feedback"
        />
      </div>
      <div class="message" v-else>
        暂无数据
      </div>
    </div>
  </div>
  <div class="foot">
    <el-button type="primary" size="large" @click="dialogFormVisible = true">
      撰写评价<el-icon class="el-icon--right"><Edit/></el-icon>
    </el-button>
  </div>
  <el-dialog v-model="dialogFormVisible" title="填写对此名用户的评价" draggable center>
    <el-form :model="feedbackForm">
      <el-form-item label="评价">
        <el-input type="textarea" :rows="6" v-model="feedbackForm.content" />
      </el-form-item>
      <el-form-item label="评分">
        <el-rate v-model="feedbackForm.rating" show-score/>
      </el-form-item>
    </el-form>
    <el-form-item>
      <el-button type="primary" @click="onSubmit">提交</el-button>
      <el-button>取消</el-button>
    </el-form-item>
  </el-dialog>
</template>

<script>
import RobotDisplay from "@/components/RobotDisplay.vue";
import ProfileService from "@/service/ProfileService";
import FeedbackDisplay from "@/components/FeedbackDisplay.vue";
import {Edit} from "@element-plus/icons-vue";
import {mapState} from "vuex";

export default {
  name: 'ProfilePage',
  components: {Edit, FeedbackDisplay, RobotDisplay},
  computed: {
    ...mapState(['personalProfile'])
  },
  data() {
    return {
      userId: this.$route.query.userId,
      robots: [],
      tab_index: 0,
      feedbackList: [],
      viewedProfile: {
        username: 'username',
        email: 'hello@gmail',
        role: 'USER',
        avatarUrl: 'https://picture-973460_960_720.png',
        bio: 'Hello, I am a new user',
        avgRating: 0,
      },
      dialogFormVisible: false,
      feedbackForm: {
        "userId": this.$route.query.userId,
        "content": "",
        "rating": 0
      }
    };
  },
  created() {
    ProfileService.getProfile(this.userId).then((response) => {
      let { username, email, role, avatarUrl, bio, avgRating} = response.data;
      avatarUrl = `http://localhost:8080/${avatarUrl}`;
      this.viewedProfile = { username, email, role, avatarUrl, bio, avgRating};
      this.robots = response.data.userBotList;
      this.feedbackList = response.data.userFeedbackList;
    }).catch((error) => {
      this.$message.error('获取个人资料失败');
      console.error(error);
    });
  },
  methods: {
    changeTab(index) {
      this.tab_index = index;
    },
    onSubmit() {
      ProfileService.postFeedback(this.feedbackForm).then(() => {
        this.$message.success('评价成功');
        this.feedbackList.push({
          commenter: this.personalProfile.username,
          commenterAvatarUrl: this.personalProfile.avatarUrl.replace('http://localhost:8080/', ''),
          content: this.feedbackForm.content,
          rating: this.feedbackForm.rating
        });
      }).catch((error) => {
        this.$message.error('评价失败');
        console.error(error);
      });
      this.dialogFormVisible = false;
    }
  }
};
</script>

<style scoped>
.container {
  width: 80%;
  overflow-y: auto;
}
.head {
  height: 90%;
}
.foot {
  height: 10%;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>