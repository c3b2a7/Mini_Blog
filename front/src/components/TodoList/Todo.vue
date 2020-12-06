<template>
  <div>
    <div>
      <input :checked="todo.done" type="checkbox" />
      <label v-text="todo.text" @dblclick="editing = true" />
      <button @click="deleteTodo(todo)" />
    </div>
    <input
      v-show="editing"
      :value="todo.text"
      @keyup.esc="cancelEdit"
      @keyup.enter="doneEdit"
      @blur="doneEdit"
    />
  </div>
</template>

<script>
export default {
  name: "Todo",
  props: {
    todo: {
      type: Object,
      default: function() {
        return {};
      }
    }
  },
  data() {
    return {
      editing: false
    };
  },
  methods: {
    deleteTodo(todo) {
      this.$emit("deleteTodo", todo);
    },
    editTodo({ todo, value }) {
      this.$emit("editTodo", { todo, value });
    },
    toggleTodo(todo) {
      this.$emit("toggleTodo", todo);
    },
    doneEdit(e) {
      const value = e.target.value.trim();
      const { todo } = this;
      if (!value) {
        this.deleteTodo({
          todo
        });
      } else if (this.editing) {
        this.editTodo({
          todo,
          value
        });
        this.editing = false;
      }
    },
    cancelEdit(e) {
      e.target.value = this.todo.text;
      this.editing = false;
    }
  }
};
</script>

<style></style>