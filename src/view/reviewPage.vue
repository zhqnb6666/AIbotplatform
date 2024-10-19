<template>
  <div class="up">
    <el-header direction = "horizontal">
      <div class="vertical-container">
        <el-avatar style="width: 100px; height: 100px" :src="robotInfo.url" :fit="fit" />
        <p class="title is-6" style="margin-top: 4px">{{robotInfo.name}}</p>
      </div>
      <div class="vertical-container">
        <p class="title is-6">评分和评价</p>
        <p class="title is-1">{{robotInfo.rating}}</p>
        <p class="subtitle">{{robotInfo.reviewCount}}条评论</p>
      </div>
      <div class="rating-container">
        <div class="line" v-for="(percent, index) in starPercents" :key="index">
          <el-rate :model-value="5 - index" disabled />
          <el-progress :percentage="percent * 100" :show-text="false"/>
        </div>
      </div>
    </el-header>


    <div class="container">
      <p class="title is-4">用户评价</p>
      <article class="media" v-for="(review, index) in robotReviews" :key="index">
        <figure class="media-left">
          <p class="image is-64x64">
            <img class="is-rounded" :src="avatorImage" alt=""/>
          </p>
        </figure>
        <div class="media-content">
          <div class="content">
            <p>
              <strong>{{review.username}}</strong>
              <br>
              {{review.review}}
              <br>
              <el-rate :model-value="review.rating" disabled />
            </p>
          </div>
        </div>
      </article>
    </div>
  </div>
  <div class="down">
    <el-button type="primary" :icon="Edit" size="large" @click="showForm = true">撰写评价</el-button>
  </div>

  <el-dialog v-model="showForm" title="撰写评价">
    <form @submit.prevent="submitReview">
      <div class="field">
        <label class="label">评分</label>
        <el-rate v-model="form.rating" />
      </div>
      <div class="field">
        <label class="label">评价</label>
        <div class="control">
          <textarea v-model="form.review" class="textarea" placeholder="请输入评价" required></textarea>
        </div>
      </div>
      <div class="field is-grouped is-grouped-centered">
        <button class="button is-link is-light" type="submit" @click="submitReview">
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
import localImage from '@/assets/图标/main-thumb-pb-3015-200-ivodfqemfvztmvgafhdouijhknthkvmp.jpeg';
import avatorImage from '@/assets/图标/main-thumb-pb-5038493-200-kaqjjvliljatttdmmgeqyflyvlevubcb.jpeg'
import { Edit } from '@element-plus/icons-vue';

export default {
  name: "reviewPage",
  data() {
    return {
      fit: "cover",
      showForm: false,
      avatorImage: avatorImage,
      form: {
        rating: 0,
        review: ''
      },
      robotInfo: {
        name: "ChatGPT-JC买的",
        url: localImage,
        rating: 3.8,
        reviewCount: 20,
        fiveStarPercent: 0.5,
        fourStarPercent: 0.1,
        threeStarPercent: 0.3,
        twoStarPercent: 0.05,
        oneStarPercent: 0.05
      },
      robotReviews: [
        {
          username: 'user1',
          rating: 4,
          review: '江澈快爆米'
        },
        {
          username: 'user2',
          rating: 3,
          review: '江澈快爆米'
        },
        {
          username: 'user3',
          rating: 5,
          review: '江澈快爆米'
        },
        {
          username: 'user4',
          rating: 2,
          review: '江澈快爆米'
        },
        {
          username: 'user5',
          rating: 1,
          review: '江澈快爆米'
        }
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
    }
  },
  methods: {
    submitReview() {
      this.robotReviews.push({
        username: 'new user',
        rating: this.form.rating,
        review: this.form.review
      });
      this.showForm = false;
      this.form.rating = 0;
      this.form.review = '';
    }
  }
};
</script>


<style scoped>
.el-dialog {
  z-index: 2000;
  display: block !important; /* 强制显示 */
}
.el-header {
  height: 160px;
  align-items: center;
  justify-content: center;
  display: flex;
  flex-direction: row;
}
.container{
  width: 70%;
  margin-top: 30px;
}
.vertical-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
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
.el-header {
  height: 170px;
}
.title.is-6{
  margin-bottom: 0;
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
