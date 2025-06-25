<script setup>
import { ref } from "vue";

const selected = ref("");
const categories = ref([]);
const categoryInput = ref([]);

const addCategory = () => {
  if (!categoryInput || categoryInput.length > 30) return;
  fetch("http://localhost:8080/categories", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ name: categoryInput }),
  })
    .then((response) => response.json())
    .then((newCategory) => {
      setCategories((prev) => [...prev, newCategory]);
      setCategoryInput("");
      setSelected(newCategory.name);
    });
};

const deleteCategory = () => {
  if (!selected) return;
  fetch(`http://localhost:8080/categories/${encodeURIComponent(selected)}`, {
    method: "DELETE",
  })
    .then(() => {
      setCategories((prev) => prev.filter((category) => category.name !== selected));
    })
    .catch((error) => {
      console.error("Error deleting category:", error);
    });
};
</script>

<template>
  <div>
    <div class="horizontalInputs">
      <input @change="setCategoryInput(e.target.value)" type="text" placeholder="Category name" />
      <button @click="addCategory" class="addButton">Add category</button>
      <button @click="deleteCategory" :disabled="!selected" class="deleteButton">Add category</button>
    </div>

    <ul class="style">
      <p>Categories:</p>
    </ul>
    <section class="expenseList">
      <p>
        Expenses of category <strong>{{ selected }}</strong
        >:
      </p>
    </section>
  </div>
</template>

<style scoped>
.style {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  background-color: #dedede;
  border-radius: 5px;
  padding: 5px;
  width: 300px;
  margin: 25px auto;
}
</style>
