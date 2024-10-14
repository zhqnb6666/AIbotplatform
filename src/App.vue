<template>

    <div v-if="showSideBar">
      <NavigationBar />
      <div class="container">
        <div class="columns">
          <SideBar />
          <router-view />
        </div>
      </div>
    </div>
    <router-view v-else />

</template>

<script>
import NavigationBar from "@/components/NavigationBar.vue";
import SideBar from "@/components/SideBar.vue";

export default {
  name: 'App',
  components: {
    SideBar,
    NavigationBar
  },
  data() {
    return {
      showSideBar: true
    }
  },
  watch: {
    $route(to) {
      this.showSideBar = to.path !== '/login' && to.path !== '/register';
    }
  },
}
</script>
