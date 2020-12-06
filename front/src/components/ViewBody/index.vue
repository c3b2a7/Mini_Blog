<template>
  <el-main>
    <el-table :data="tableData">
      <el-table-column label="日期" prop="date"></el-table-column>
      <el-table-column label="姓名" prop="name"></el-table-column>
      <el-table-column label="地址" prop="address"></el-table-column>
    </el-table>
  </el-main>
</template>

<script>
export default {
  name: "Main",
  data: function() {
    const item = {
      date: "2016-05-02",
      name: "王小虎",
      address: "上海市普陀区金沙江路 1518 弄"
    };
    return {
      tableData: Array(15).fill(item),
      color: "red"
    };
  },
  watch: {
    color: function(newVal, oldVal) {
      console.log("old val: %s\nnew val: %s", oldVal, newVal);
    }
  },
  directive: {
    // color:{
    //     bind: function(){},
    //     inserted: function(){},
    //     updata: function(){}
    // }
    color: function(el, binding) {
      el.style.color = binding.value;
      console.log(`old value: ${binding.oldValue} new value: ${binding.value}`);
    }
  }
};
</script>

<style scoped></style>
