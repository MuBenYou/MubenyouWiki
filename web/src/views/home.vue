<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
          @click="handleClick"
      >
        <a-menu-item key="welcome">
          <router-link to="'/'">
            <MailOutlined />
            <span>欢迎</span>
          </router-link>
        </a-menu-item>
          <a-sub-menu v-for="item in level1" :key="item.id">
            <template v-slot:title>
              <span><user-outlined />{{item.name}}</span>
            </template>
            <a-menu-item v-for="child in item.children" :key="child.id">
              <MailOutlined /><span>{{child.name}}</span>
          </a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <a-list item-layout="vertical" size="large" :grid="{gutter : 20,column : 3}"  :data-source="ebooks">
        <template #renderItem="{ item }">
          <a-list-item key="item.title">
            <template #actions>
          <span v-for="{ type, text } in actions" :key="type">
            <component v-bind:is="type" style="margin-right: 8px" />
            {{ text }}
          </span>
            </template>

            <a-list-item-meta :description="item.description">
              <template #title>
                <a :href="item.href">{{ item.name }}</a>
              </template>
              <template #avatar><a-avatar :src="item.cover" /></template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </a-layout-content>
  </a-layout>>
</template>

<script lang="ts">
import { defineComponent ,onMounted,ref} from 'vue';
import axios from "axios";
import { StarOutlined, LikeOutlined, MessageOutlined } from '@ant-design/icons-vue';
import {message} from "ant-design-vue";
import {Tool} from "@/util/tool";

const listData:any = [];

for (let i = 0; i < 23; i++) {
  listData.push({
    href: 'https://www.antdv.com/',
    title: `ant design vue part ${i}`,
    avatar: 'https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png',
    description:
        'Ant Design, a design language for background applications, is refined by Ant UED Team.',
    content:
        'We supply a series of design principles, practical patterns and high quality design resources (Sketch and Axure), to help people create their product prototypes beautifully and efficiently.',
  });
}

export default defineComponent({
  name: 'Home',
  setup(){//初始化函数

    const ebooks=ref();//实现响应式数据 方式1

    const level1 = ref(); //一级分类树，children属性就是二级分类
    let categorys: any;
    /**
     * 查询所有类
     **/
    const handleQueryCategory = () => {
      axios.get("/category/all").then((response) => {
        const data = response.data;
        if (data.success){
          categorys = data.content;
          console.log("原始数据:",categorys);

          level1.value=[];
          level1.value = Tool.array2Tree(categorys,0);
          console.log("树形结构:",level1.value);
        }else {
          message.error(data.message);
        }
      });
    };

    const handleClick = ()=>{
      console.log("menu click")
    };

    onMounted(()=>{//页面加载完后的才执行的生命周期函数
      handleQueryCategory();
      axios.get( "/ebook/list",{
        params:{
          page:1,
          size:1000,
        }
      }).then((response)=>{ //默认会有个参数 这个参数名是自个起的
        const data=response.data;//后端的commonResp的数据
        ebooks.value=data.content.list;
      });
    });

    const pagination = {
      onChange: (page:any) => {
        console.log(page);
      },
      pageSize: 3,
    };
    const actions:any = [
      { type: 'StarOutlined', text: '156' },
      { type: 'LikeOutlined', text: '156' },
      { type: 'MessageOutlined', text: '2' },
    ];

    return{
      ebooks,
      handleClick,
      level1,

      listData,
      pagination,
      actions,
    }
  },

  components: {
    StarOutlined,
    LikeOutlined,
    MessageOutlined,
  },
});
</script>
<style scoped>
.ant-avatar {
  width: 50px;
  height: 50px;
  line-height: 50px;
  border-radius: 8%;
  margin: 5px 0;
}
</style>
