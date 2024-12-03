<template>
  <div class="up">
    <el-header direction="horizontal">
      <div class="vertical-container">
        <el-avatar style="width: 100px; height: 100px" :src="robotInfo.url" :fit="'cover'"/>
        <p class="title is-6">{{ robotInfo.name }}</p>
      </div>
      <div class="vertical-container">
        <p class="title is-6" style="margin-bottom: 0">评分和评价</p>
        <p class="title is-1">{{ robotInfo.avgRating }}</p>
        <p class="subtitle">{{ robotInfo.reviewCount }}条评价</p>
      </div>
      <div class="rating-container">
        <div class="line" v-for="(percent, index) in starPercents" :key="index">
          <el-rate :model-value="5 - index" disabled/>
          <el-progress :percentage="percent" :show-text="false"/>
        </div>
      </div>
    </el-header>


    <div class="container">
      <p class="title is-4">用户评价</p>
      <div v-if="robotReviews.length !== 0">
        <article class="media" v-for="(review, index) in robotReviews" :key="index">
          <figure class="media-left">
            <p class="image is-64x64">
              <img class="is-rounded" :src="`http://localhost:8080/${review.avatarUrl}`" alt="用户头像"/>
            </p>
          </figure>
          <div class="media-content">
            <div class="content">
              <p>
                <strong>{{ review.username }}</strong>
                <br>评分：
                <el-rate :model-value="review.rating" disabled/>
              </p>
            </div>
          </div>
        </article>
      </div>
      <div v-else>
        <p>暂无评价</p>
      </div>
    </div>
  </div>
  <div class="down">
    <el-button type="primary" :icon="Edit" size="large" @click="showForm = true">撰写评价</el-button>
  </div>

  <el-dialog v-model="showForm" title="撰写评价">
    <form @submit.prevent="submitReview">
      <div class="field">
        <label class="label">评分</label>
        <el-rate v-model="form.rating"/>
      </div>
      <div class="field">
        <label class="label">评价</label>
        <div class="control">
          <textarea v-model="form.review" class="textarea" placeholder="请输入评价" required></textarea>
        </div>
      </div>
      <div class="field is-grouped is-grouped-centered">
        <button class="button is-link is-light" type="submit">
          提交
        </button>
        <button class="button" @click="showForm = false">
          取消
        </button>
      </div>
    </form>
  </el-dialog>

</template>

<script>
import {Edit} from '@element-plus/icons-vue';
import axiosInstance from "@/service/axiosInstance";
import {DEFAULT_AVATAR, AVATAR_MAP} from "@/util/constants";
import {mapState} from "vuex";

export default {
  name: "reviewPage",
  data() {
    return {
      showForm: false,
      form: {
        rating: 0,
        review: ''
      },
      robotInfo: {
        name: "ChatGPT-JC买的",
        url: DEFAULT_AVATAR,
        avgRating: 3.8,
        reviewCount: 20,
        fiveStarPercent: 50,
        fourStarPercent: 10,
        threeStarPercent: 10,
        twoStarPercent: 10,
        oneStarPercent: 20
      },
      robotReviews: [
        {
          username: 'user1',
          avatarUrl: 'https://element-plus.gitee.io/zh-CN/element-plus/favicon.ico',
          rating: 4,
        },
      ]
    };
  },
  computed: {
    Edit() {
      return Edit;
    },
    starPercents() {
      return [
        this.robotInfo.fiveStarPercent,
        this.robotInfo.fourStarPercent,
        this.robotInfo.threeStarPercent,
        this.robotInfo.twoStarPercent,
        this.robotInfo.oneStarPercent
      ];
    },
    ...mapState(['personalProfile'])
  },
  created() {
    this.fetchRobotInfo(this.$route.params.botId);
  },

  methods: {
    submitReview() {
      const botId = this.$route.params.botId;
      axiosInstance.post(`/bots/ratings`, {
        botId: botId,
        rating: this.form.rating,
      }).then(() => {
        this.showForm = false;
        this.robotReviews.push({
          avatarUrl: this.personalProfile.avatarUrl.replace("http://localhost:8080/", ""),
          username: this.personalProfile.username,
          rating: this.form.rating
        });
        //更新评分
        this.robotInfo.avgRating = Number(((this.robotInfo.avgRating * this.robotInfo.reviewCount + this.form.rating) /
            (this.robotInfo.reviewCount + 1)).toFixed(2));
        //更新评价数
        this.robotInfo.reviewCount += 1;
        //更新评分百分比
        const totalRatings = this.robotInfo.reviewCount;
        const percentages = ['fiveStarPercent', 'fourStarPercent', 'threeStarPercent', 'twoStarPercent', 'oneStarPercent'];
        for (let i = 0; i < 5; i++) {
          this.robotInfo[percentages[i]] = Number(((this.robotInfo[percentages[i]] * (totalRatings - 1) +
              (this.form.rating === 5 - i ? 100 : 0)) / totalRatings).toFixed(2));
        }
        this.$message.success('评价成功');
      })
      .catch((error) => {
        console.error(error);
        this.$message.error('评价失败');
      });
    },
    fetchRobotInfo(botId) {
      const request1 = axiosInstance.get(`/bots/${botId}/ratings`);
      const request2 = axiosInstance.get(`/bots/${botId}`);
      const request3 = axiosInstance.get(`/bots/${botId}/ratings/detail`);
      Promise.all([request1, request2, request3])
          .then(([ratings, bot, reviews]) => {
            this.robotInfo = {
              name: bot.data.name,
              url: AVATAR_MAP[bot.data.name] || DEFAULT_AVATAR,
              avgRating: ratings.data.averageRating,
              reviewCount: ratings.data.totalRatings,
              fiveStarPercent: ratings.data.fiveStarPercentage,
              fourStarPercent: ratings.data.fourStarPercentage,
              threeStarPercent: ratings.data.threeStarPercentage,
              twoStarPercent: ratings.data.twoStarPercentage,
              oneStarPercent: ratings.data.oneStarPercentage
            };
            this.robotReviews = reviews.data;
          })
          .catch((error) => {
            console.error(error);
          });
    }
  }
};
</script>


<style scoped>
.el-header {
  height: 170px;
  align-items: center;
  justify-content: center;
  display: flex;
  flex-direction: row;
}

.container {
  width: 70%;
  margin-top: 30px;
}

.vertical-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 150px;
}

.rating-container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 500px;
  height: 100%;
}

.line {
  display: flex;
  align-items: center;
  width: 100%;
}

.el-progress {
  width: 300px;
}

.up {
  height: 90%;
  overflow-y: auto;
}

.down {
  height: 10%;
  display: flex;
  align-items: center;
  justify-content: center
}
</style>
