<script setup>
import { computed } from "vue";

const props = defineProps({
  categoryName: String,
  isSelected: Boolean,
});

const emit = defineEmits(["handleButtonClick"]);

const handleButtonClick = () => {
  emit("handleButtonClick", props.categoryName);
};

const buttonStyle = computed(() => ({
  backgroundColor: props.isSelected ? "#ffa925" : "white",
  color: "black",
  border: "2px solid black",
  margin: "3px",
  width: "250px",
}));
</script>

<template>
  <div>
    <button @click="handleButtonClick" :style="buttonStyle">
      {{ props.categoryName }}
    </button>
  </div>
</template>

<style scoped></style>
