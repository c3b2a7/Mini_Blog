<template>
  <div>
    <p>
      Ask a yes/no question:
      <input v-model="question" />
    </p>
    <p>{{ answer }}</p>
  </div>
</template>

<script>
export default {
  name: "AskQuestion",
  data() {
    return {
      question: "",
      answer: "I cannot give you an answer until you ask a question!"
    };
  },
  watch: {
    question: function() {
      this.answer = "Waiting for you to stop typing...";
      this.debouncedGetAnswer();
    }
  },
  created() {
    this.$$.get("/kd/query?type=shentong&postid=11111").then(response => {
      console.log(response.data);
    });
    this.debouncedGetAnswer = this.$_.debounce(this.getAnswer, 500);
  },
  methods: {
    getAnswer() {
      if (this.question.length === 0) {
        this.answer = "I cannot give you an answer until you ask a question!";
        return;
      }
      if (this.question.indexOf("?") === -1) {
        this.answer = "Questions usually contain a question mark. :-)";
        return;
      }
      this.answer = "Thinking...";
      this.$$.get("/fakeapi/api")
        .then(response => {
          this.answer = this.$_.capitalize(response.data.answer);
        })
        .catch(error => {
          this.answer = "Error! Could not reach the API. " + error;
        });
    }
  }
};
</script>

<style scoped></style>