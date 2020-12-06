<template>
  <div id="app">
    <el-container style="border: 1px solid #eee">
      <side-bar></side-bar>
      <el-container>
        <el-header>
          <nav-bar></nav-bar>
        </el-header>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>
<script>
import SideBar from "@/components/SideBar";
import NavBar from "@/components/NavBar";

export default {
  name: "app",
  components: { NavBar, SideBar }
};
</script>

<style>
#app {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;

  color: #2c3e50;
}
</style>