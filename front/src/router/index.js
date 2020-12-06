import Vue from "vue";
import VueRouter from "vue-router";
import TodoList from "@/components/TodoList";
import ViewBody from "@/components/ViewBody";

Vue.use(VueRouter);

const routes = [
  {
    name: "View",
    path: "/view",
    component: ViewBody
  },
  {
    name: "Question",
    path: "/question",
    component: () => import("@/components/Question/AskQuestion")
  },
  {
    name: "Todo",
    path: "/todo",
    component: TodoList
  },
  {
    path: "*",
    component: ViewBody
  }
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes
});

export default router;
