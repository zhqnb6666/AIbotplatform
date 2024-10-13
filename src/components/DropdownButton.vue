<!-- DropdownButton.vue -->
<template>
  <div class="dropdown is-hoverable">
    <div class="dropdown-trigger">
      <button class="button is-white" @click="handleClick">
        <span class="icon is-large">
          <i :class="iconClass" aria-hidden="true"></i>
        </span>
      </button>
    </div>
    <div class="dropdown-menu">
      <div class="dropdown-content">
        <div class="dropdown-item">
          <p>{{ text }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DropdownButton',
  props: {
    iconClass: {
      type: String,
      required: true
    },
    text: {
      type: String,
      required: true
    },
    handleClick: {
      type: Function,
      required: true
    }
  }
};
</script>

<style scoped>
.icon {
  color: #404040;
}
</style>