
<template>
    <div class="container" style="width: 60%">
      <div class="content" style="margin-top: 20px">
        <div class="control has-icons-left has-icons-right">
          <input class="input is-medium is-rounded" type="text" placeholder="搜索机器人或者用户">
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
            :class="{ 'is-link is-light': activeTag.includes(tag) }"
            @click="handleClickTags(tag)"
        >
        {{ tag }}
        </span>
      </div>

      <div class="media" v-for="robot in robots" :key="robot.name">
        <figure class="media-left">
          <p class="image is-64x64">
            <img :src="`https://bulma.io/assets/images/placeholders/64x64.png`" alt="Placeholder image" />
          </p>
        </figure>
        <div class="media-content">
          <div class="content">
            <p>
              <strong class="title is-6">{{robot.name}}</strong>
              <br>{{robot.description}}
              <span style="display: flex; align-items: center; font-size: 15px; font-weight: 500">
                每条消息花费token: <strong>{{robot.tokenCost}}</strong>
              </span>
            </p>
          </div>
        </div>
        <div class="media-right">
          <el-button text size="large" round @click="review(robot.botId)">撰写评价<el-icon><EditPen /></el-icon></el-button>
        </div>
      </div>
    </div>



</template>

<script>
import axiosInstance from "@/axiosInstance";
import {EditPen} from "@element-plus/icons-vue";
export default {
  name: 'HomePage',
  components: {EditPen},
  data() {
    return {
      robots: [

      ],
      allTags: ['官方', '简体中文', '热门', '用户', '机器人'],
      activeTag: []
    }
  },
  created() {
    axiosInstance.get('/bots').then(res => {
      this.robots = res.data.slice(0, 10);
    }).catch(err => {
      console.log(err);
      this.$message.error('获取机器人列表失败');
    })
  },
  methods: {
    handleClickTags(tag) {
      if(this.activeTag.includes(tag)) {
        this.activeTag = this.activeTag.filter(t => t !== tag)
      } else {
        this.activeTag.push(tag)
        console.log('click tags')
      }
    },
    //跳转到评分界面
    review(botId) {
      this.$router.push(`/review/${botId}`);
    }
  }
}
</script>

<style scoped>
@import "@/assets/css/HomePage.css";

</style>