<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '80px'  }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-button type="primary" @click="handleQuery()">
              查询
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">
              新增
            </a-button>
          </a-form-item>
        </a-form>
<!--        <a-button type="primary" @click="add()" size="large">添加</a-button>-->
      </p>
      <!--列,key id,数据category,分页,等待框,分页执行方法-->
      <a-table
          :columns="columns"
          :row-key="record=>record.id"
          :data-source="level1"
          :loading="loading"
          :pagination="false"
      >
        <template #cover="{text:cover}">
          <img class="img-wh" v-if="cover" :src="cover" alt="avatar"/> <!--渲染图片-->
        </template>
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                title="内容删除后不可恢复，确认删除？"
                ok-text="是"
                cancel-text="否"
                @confirm="handleDelete(record.id)"
            >
              <a-button type="primary" >
                删除
              </a-button>
            </a-popconfirm>

          </a-space>
        </template>
      </a-table>
    </a-layout-content>

  </a-layout>
  <a-modal
      title="分类表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      ok-text="我测,说藏话了"
      @ok="handleModalOk"
  >
    <!--弹出表单-->
    <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">

      <a-form-item label="名称">
        <a-input v-model:value="category.name"/>
      </a-form-item>
      <a-form-item label="父分类">
        <a-select
            ref="select"
            v-model:value="category.parent"
        >
          <a-select-option value="0">
            无
          </a-select-option>
          <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="category.id === c.id" >
            {{ c.name }}
          </a-select-option>

        </a-select>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="category.sort"/>
      </a-form-item>


    </a-form>
  </a-modal>

</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';//写上onMounted VUE3.0 setup集成了 导入ref 做响应式数据
import axios from 'axios';
import {message} from "ant-design-vue";
import {Tool} from "@/util/tool";

export default defineComponent({
  name: 'AdminCategory',
  setup() {
    const param = ref();
    param.value = {};
    const categorys = ref();//响应式数据 获取的书籍实时反馈到页面上


    const loading = ref(false);

    const columns = [//页面的响应变量 不是数据的响应变量 代表就是这个表格里面有多少个数据 下面数据我们自己定义的

      {
        title: '名称',
        dataIndex: 'name',
      },
      {
        title: '父分类',
        key:'parent',
        dataIndex:'parent',
      },

      {
        title: '顺序',
        dataIndex: 'sort',
      },

      {
        title: 'Action',
        key: 'action',
        slots: {customRender: 'action'}
      }
    ];
    const level1 = ref(); //一级分类树，children属性就是二级分类

    /**
     * 数据查询
     **/
    const handleQuery = () => {
      loading.value = true;
      axios.get("/category/all").then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success){
          categorys.value = data.content;
          console.log("原始数据:",categorys.value);

          level1.value=[];
          level1.value = Tool.array2Tree(categorys.value,0);
          console.log("树形结构:",level1);
        }else {
          message.error(data.message);
        }
      });
    };


    /**表单*/
    const category=ref({});
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const handleModalOk = () => {
      modalLoading.value = true;
      axios.post("/category/save",category.value).then((response) => {
        modalLoading.value = false;   //只要返回了，就去掉loging效果
        const data = response.data;  //commonResp
        if(data.success){
          modalVisible.value = false;
          //重新加载列表
          handleQuery();
        }else {
          message.error(data.message);
        }
      });
    };
    /**
     * 编辑
     */
    const edit = ( record:any ) =>{
      modalVisible .value = true;
      category.value = Tool.copy(record);//把列表的值先复制出来，就不影响record
    };
    /**
     * 添加
     */
    const add = () =>{
      modalVisible .value = true;
      category.value={};
    };


    /**
     * 删除
     */
    const handleDelete = ( id:number ) =>{

      axios.delete("/category/delete/"+id).then((response)=>{
        const data = response.data;  //data = commonResp
        if(data.success){
          //重新加载列表
          handleQuery();
        }
      })
    };


    onMounted(() => {
      handleQuery();

    });

    return {
      param,
      //categorys,//表格
      level1,
      columns,
      loading,
      handleQuery,


      edit,
      add,
      handleDelete,

      category,
      modalVisible,
      modalLoading,
      handleModalOk,

    }
  }
});
</script>

<!-- #scoped表示当前组件才有用 -->
<style scoped>
.img-wh {
  width: 50px;
  height: 50px;
  line-height: 50px;
  border-radius: 8%;
  margin: 5px 0;
}

</style>