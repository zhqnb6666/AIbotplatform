
<template>
    <div class="container" style="width: 60%">
      <div class="content" style="margin-top: 20px">
        <div class="control has-icons-left has-icons-right">
          <input class="input is-medium is-rounded"
                 type="text" placeholder="搜索机器人或者用户"
                 v-model="searchKeyword" @keyup.enter="sendKeyword(searchKeyword)"/>
          <span class="icon is-medium is-left">
            <i class="fa fa-search"></i>
          </span>
          <span class="icon is-medium is-right">
            <i class="fa fa-check"></i>
          </span>
        </div>
      </div>
      <div class="tags">
        <span
            class="tag is-hoverable is-medium"
            v-for="tag in allTags"
            :key="tag"
            :class="{ 'is-link is-light': activeTag === tag }"
            @click="handleClickTags(tag)"
        >
        {{ tag }}
        </span>
      </div>

      <div v-if="activeTag === '机器人'">
        <div v-if="robots.length !== 0">
          <RobotDisplay
              v-for="robot in robots"
              :key="robot.id"
              :robot="robot"
              :show-chat-button="true"
          />
        </div>
        <div v-else>
          暂无内容
        </div>
      </div>

      <div v-if="activeTag === '用户'">
        <div v-if="users.length !== 0">
          <UserDisplay
              v-for="user in users"
              :key="user.id"
              :user="user"
          />
        </div>
        <div v-else>
          暂无内容
        </div>
      </div>

      <div v-if="activeTag === '为您推荐'">
        <div v-if="recommendedRobots.length !== 0">
          <RobotDisplay
            v-for="robot in recommendedRobots"
            :key="robot.id"
            :robot="robot"
            :show-chat-button="true"
          />
        </div>
        <div v-else>
          暂无内容
        </div>
      </div>

    </div>
</template>

<script>
import RobotDisplay from "@/components/RobotDisplay.vue";
import axiosInstance from "@/service/axiosInstance";
import {mapState} from "vuex";
import UserDisplay from "@/components/UserDisplay.vue";

export default {
  name: 'HomePage',
  components: {UserDisplay, RobotDisplay},
  data() {
    return {
      robots: [],
      users: [],
      recommendedRobots: [],
      allTags: ['为您推荐', '用户', '机器人'],
      activeTag: '机器人',
      searchKeyword: ''
    }
  },
  computed: {
    ...mapState(['isLoggedIn'])
  },
  created() {
    this.sendKeyword('all');
    if (this.isLoggedIn) {
      this.getRecommendedRobots();
    }
  },
  methods: {
    handleClickTags(tag) {
      if (tag === '为您推荐' && !this.isLoggedIn) {
        this.$router.push('/login');
        return;
      }
      this.activeTag = tag;
    },
    sendKeyword(keyword) {
      if (keyword.trim() === '') {
        this.$message.error('请输入搜索关键字');
        return;
      }
      const requestBot = axiosInstance.get(`/search/bot/${keyword}`);
      const requestUser = axiosInstance.get(`/search/user/${keyword}`);
      Promise.all([requestBot, requestUser]).then(([resBot, resUser]) => {
        if (keyword.trim() === 'all') {
          this.robots = resBot.data.slice(0, 10);
          this.users = resUser.data.slice(0, 10);
        } else {
          this.robots = resBot.data;
          this.users = resUser.data;
        }
      }).catch(err => {
        console.log(err);
        this.$message.error('搜索失败');
      })
    },
    getRecommendedRobots() {
      axiosInstance.get('/bots/recommend').then((response) => {
        this.recommendedRobots = response.data;
      }).catch(err => {
        console.log(err);
        this.$message.error('获取推荐机器人失败');
      })
    },
  }
}
</script>

<style scoped>
@import "@/assets/css/HomePage.css";
</style>