
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
                <br>{{robot.description}}
                <br class="tags"><span v-for="tag in robot.tags" :key="tag" class="tag is-hoverable">{{tag}}</span>
              </p>
            </div>
          </div>
          <div class="media-right">
            <button class="button is-white" @click="review">评分</button>
          </div>
        </div>
      </div>

    </div>

</template>

<script>

export default {
  name: 'HomePage',
  data() {
    return {
      robots: [
        {
          name: 'Robot1',
          description: 'This is a robot',
          tags: ['Official', 'GPT']
        },
        {
          name: 'Robot2',
          description: 'This is a robot',
          tags: ['tag1', 'tag2']
        },
        {
          name: 'Robot3',
          description: 'This is a robot',
          tags: ['tag1', 'tag2']
        }
      ],
      allTags: ['Official', 'GPT', 'tag1', 'tag2', 'tag3'],
      activeTag: []
    }
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
    review() {
      this.$router.push('/review')
    }
  }
}
</script>

<style scoped>
@import "@/assets/css/HomePage.css";

</style>