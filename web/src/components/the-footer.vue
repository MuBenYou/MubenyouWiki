<template>
  <a-layout-footer style="text-align: center">
    MuBenYou 电子书  ©2018 Created by Ant UED
    <br/>
    <span v-show="user.id">感谢您选择我们 {{user.name}} 先生(女士)</span>
  </a-layout-footer>
</template>

<script lang="ts">
import { defineComponent,computed } from 'vue';
import store from "@/store";

export default defineComponent({
  name: 'the-footer',
  setup(){
    const user = computed(() => store.state.user);

    return{
      user,
    }
  }
});
</script>