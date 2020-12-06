import Vue from "vue";
import Element from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
import lodash from "lodash";
import axios from "axios";
import router from "@/router";
import App from "@/App";

Vue.use(Element);

Object.defineProperty(Vue.prototype, "$$", { value: axios });
Object.defineProperty(Vue.prototype, "$_", { value: lodash });

Vue.config.productionTip = false;

new Vue({
  router,
  render: h => h(App)
}).$mount("#app");
