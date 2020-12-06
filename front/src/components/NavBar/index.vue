<template>
  <el-header>
    <el-select
      size="mini"
      v-model="path"
      @change="route"
      filterable
      style="padding-right: 10px"
    >
      <el-option
        v-for="item in options"
        :key="item.lable"
        :label="item.label"
        :value="item.path"
      ></el-option>
    </el-select>
    <el-dropdown>
      <i class="el-icon-setting" style="margin-right: 15px"></i>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item>查看</el-dropdown-item>
        <el-dropdown-item>新增</el-dropdown-item>
        <el-dropdown-item>删除</el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
    <span>王小虎</span>
  </el-header>
</template>

<script>
export default {
  name: "Header",
  data: function() {
    return {
      path: "",
      options: [
        { label: "问题列表", path: "/problem/index" },
        { label: "需求列表", path: "/requirement/index" },
        { label: "环境列表", path: "/env/index" },
        { label: "工单列表", path: "/sheet/index" },
        { label: "缺陷列表", path: "/bug/index" },
        { label: "代办列表", path: "/todo" },
        { label: "名单列表", path: "/view" },
        { label: "问答", path: "/question" }
      ]
    };
  },
  methods: {
    route() {
      console.log(this.path);
      this.$router.push(this.path);
    }
  }
};
</script>

<style scoped></style>
