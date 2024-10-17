<!-- DropDownButton.vue -->
<template>
  <div class="dropdown is-hoverable is-up"  v-if="(!isThumbUp && !ThumbType) || (!isThumbDown && ThumbType)">
    <div class="dropdown-trigger">
      <button
          class="button is-small"
          @click="handleClick"
          :class="{ 'is-active': isThumbUp || isThumbDown }"
      >
        <span class="icon">
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
    },
    isThumbUp: {
      type: Boolean,
      default: false
    },
    isThumbDown: {
      type: Boolean,
      default: false
    },
    ThumbType: {
      type: Boolean,
      default: true
    }
  }
};
</script>

<style scoped>
.icon {
  color: #2E2E2E;
}
</style>