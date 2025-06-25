<script setup>
import { ref, onMounted, computed } from "vue";

const props = defineProps({
  category: String,
});

const allExpenses = ref([]);
const nameRef = ref();
const amountRef = ref();
const dateRef = ref();

const fetchExpenses = () => {
  fetch("http://localhost:8080/expenses", {
    method: "GET",
  })
    .then((response) => response.json())
    .then((data) => (allExpenses.value = data))
    .catch((err) => console.error("Error fetching expenses:", err));
};

onMounted(() => {
  fetchExpenses();
});

const expensesOfCategory = computed(() => {
  return allExpenses.value.filter((expense) => expense.categoryFK === props.category);
});

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString(undefined, {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
};

const handleSubmit = (e) => {
  e.preventDefault();
  if (!category) {
    return;
  }

  const newExpense = {
    name: nameRef.current.value,
    amount: parseFloat(amountRef.current.value),
    date: new Date(dateRef.current.value).toISOString(),
    categoryFK: category,
  };

  fetch("http://localhost:8080/expenses", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(newExpense),
  })
    .then((res) => res.json())
    .then(() => {
      fetchExpenses();
      nameRef.value = "";
      amountRef.value = "";
      dateRef.value = "";
    })
    .catch((err) => console.error("Error posting expense:", err));
};
</script>

<template>
  <div>
    <form @submit.prevent="handleSubmit" class="formRow">
      <input type="text" v-model="nameRef" placeholder="Expense" required />
      <input type="number" v-model="amountRef" placeholder="Cost (€)" required />
      <input type="date" v-model="dateRef" required />
      <button type="submit" class="addButton">Add Expense</button>
    </form>

    <ul>
      <li v-for="(expense, index) in expensesOfCategory" :key="index" class="expenseBubble">
        {{ expense.name }}, cost: {{ expense.amount }} € ({{ formatDate(expense.date) }})
      </li>
    </ul>
  </div>
</template>
