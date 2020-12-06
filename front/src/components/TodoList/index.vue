<template>
  <div>
    <input auto-complete="off" placeholder="new todo" @keyup.enter="addTodo" />
    <todo
      v-for="(todo, index) in todos"
      :todo="todo"
      :key="index"
      @toggleTodo="toggleTodo"
      @editTodo="editTodo"
      @deleteTodo="deleteTodo"
    />
  </div>
</template>

<script>
import Todo from "./Todo";

const defalutList = [
  { text: "star this repository", done: false },
  { text: "fork this repository", done: false },
  { text: "follow author", done: false },
  { text: "vue-element-admin", done: true },
  { text: "vue", done: true },
  { text: "element-ui", done: true },
  { text: "axios", done: true },
  { text: "webpack", done: true }
];
const STORAGE_KEY = "todos";
export default {
  components: { Todo },
  data() {
    return {
      todos: defalutList
    };
  },
  methods: {
    setLocalStorage() {
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(this.todos));
    },
    addTodo(e) {
      const text = e.target.value;
      if (text.trim()) {
        this.todos.push({
          text,
          done: false
        });
        this.setLocalStorage();
      }
      e.target.value = "";
    },
    toggleTodo(val) {
      val.done = !val.done;
      this.setLocalStorage();
    },
    deleteTodo(todo) {
      this.todos.splice(this.todos.indexOf(todo), 1);
      this.setLocalStorage();
    },
    editTodo({ todo, value }) {
      todo.text = value;
      this.setLocalStorage();
    },
    clearCompleted() {
      this.todos = this.todos.filter(todo => !todo.done);
      this.setLocalStorage();
    },
    toggleAll({ done }) {
      this.todos.forEach(todo => {
        todo.done = done;
        this.setLocalStorage();
      });
    }
  }
};
</script>

<style></style>