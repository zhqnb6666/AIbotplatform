<script>
export default {
  name: "FeedbackDisplay",

  props: {
    feedback: {
      type: Object,
      required: true
    },
  },
  data() {
    return {
      rating: this.feedback.rating
    };
  },
}
</script>

<template>
  <div class="media">
    <figure class="media-left">
      <el-avatar
        :size="128"
        :src="`http://localhost:8080/${feedback.commenterAvatarUrl}`"
        alt="Image"
      ></el-avatar>
    </figure>
    <div class="media-content">
      <h6 class="title is-6">{{ feedback.commenter }}</h6>
      <p>评价：{{ feedback.content }}</p>
      <br>
      评分: <el-rate v-model="rating" disabled></el-rate>
    </div>
  </div>
</template>

<style scoped>

</style>