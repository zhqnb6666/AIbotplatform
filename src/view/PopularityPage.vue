<script>
import PopularityService from "@/service/PopularityService";
import RobotDisplay from "@/components/RobotDisplay.vue";
import {mapState} from "vuex";
export default {
  name: "PopularityPage",
  components: {RobotDisplay},
  created() {
    this.getPopularity();
  },
  computed: {
    ...mapState(['personalProfile'])
  },
  data() {
    return {
      activeTab: 0,
      robots:[]
    }
  },
  methods: {
    getPopularity() {
      PopularityService.getPopularity(10).then((response) => {
        this.robots = response.data;
      }).catch((error) => {
        console.error(error);
        this.$message.error("获取流行榜失败");
      });
    },
    getLatest() {
      PopularityService.getLatest(10).then((response) => {
        this.robots = response.data;
      }).catch((error) => {
        console.error(error);
        this.$message.error("获取最新失败");
      });
    },
    getMonthlyBest() {
      PopularityService.getBestMonthly(10).then((response) => {
        this.robots = response.data;
      }).catch((error) => {
        console.error(error);
        this.$message.error("获取月度最佳失败");
      });
    },
    getHistoricalBest() {
      PopularityService.getBestHistorical(10).then((response) => {
        this.robots = response.data;
      }).catch((error) => {
        console.error(error);
        this.$message.error("获取历史最佳失败");
      });
    },
    switchTab(tabIndex) {
      this.activeTab = tabIndex;
      switch(tabIndex) {
        case 0:
          this.getPopularity();
          break;
        case 1:
          this.getLatest();
          break;
        case 2:
          this.getMonthlyBest();
          break;
        case 3:
          this.getHistoricalBest();
          break;
      }
    }
  }
}
</script>

<template>
  <div class="tabs is-fullwidth">
    <ul>
      <li :class="{ 'is-active': activeTab === 0 }" @click="switchTab(0)"><a>流行榜</a></li>
      <li :class="{ 'is-active': activeTab === 1 }" @click="switchTab(1)"><a>最新</a></li>
      <li :class="{ 'is-active': activeTab === 2 }" @click="switchTab(2)"><a>月度最佳</a></li>
      <li :class="{ 'is-active': activeTab === 3 }" @click="switchTab(3)"><a>历史最佳</a></li>
    </ul>
  </div>
  <RobotDisplay v-for="(robot, index) in robots" :key="robot.id"
                :robot="robot"
                :show-right-button="true"
                :index="index + 1"
                :show-reward-button="this.personalProfile.role === 'ADMIN'"
                :show-index="true"

  />
</template>

<style scoped>
</style>