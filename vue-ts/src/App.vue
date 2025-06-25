<script setup lang="ts">
import CategoryList from "./components/CategoryList.vue";
</script>

<template>
  <div class="container">
    <h2>Vue TS frontend</h2>
    <CategoryList />
  </div>
</template>

<style>
html,
body {
  height: 100%;
  margin: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.container {
  text-align: center;
}
</style>
