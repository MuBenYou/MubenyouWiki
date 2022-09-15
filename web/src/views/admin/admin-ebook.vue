<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '80px'  }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.name" placeholder="名称">
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
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
      <!--列,key id,数据ebook,分页,等待框,分页执行方法-->
      <a-table
          :columns="columns"
          :row-key="record=>record.id"
          :data-source="ebooks"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #cover="{text:cover}">
          <img class="img-wh" v-if="cover" :src="cover" alt="avatar"/> <!--渲染图片-->
        </template>
        <template v-slot:category="{text ,record }">
          <span>{{getCategoryName(record.category1Id) }} / {{getCategoryName(record.category2Id)}}</span>
        </template>
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <router-link to="/admin/doc">
              <a-button type="primary">
                文档管理
              </a-button>
            </router-link>
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
      title="Title"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      ok-text="我测,说藏话了"
      @ok="handleModalOk"
  >
    <!--弹出表单-->
    <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="封面">
        <a-input v-model:value="ebook.cover"/>
      </a-form-item>
      <a-form-item label="名称">
        <a-input v-model:value="ebook.name"/>
      </a-form-item>
      <a-form-item label="分类">
        <a-cascader
            v-model:value="categoryIds"
            :field-names="{label :'name',value: 'id',children: 'children' }"
            :options="level1"
        />
      </a-form-item>

      <a-form-item label="阅读数">
        <a-input v-model:value="ebook.viewCount"/>
      </a-form-item>
      <a-form-item label="点赞数">
        <a-input v-model:value="ebook.voteCount"/>
      </a-form-item>
      <a-form-item label="描述">
        <a-input v-model:value="ebook.description" type="textarea"/>
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
  name: 'AdminEbook',
  setup() {
    const param = ref();
    param.value = {};
    const ebooks = ref();//响应式数据 获取的书籍实时反馈到页面上
    const pagination = ref({
      current: 1,//当前页
      pageSize: 4,//分页条数
      total: 0
    });

    const loading = ref(false);

    const columns = [//页面的响应变量 不是数据的响应变量 代表就是这个表格里面有多少个数据 下面数据我们自己定义的
      {
        title: '封面',
        dataIndex: 'cover',
        slots: {customRender: 'cover'}//渲染      slots: 自定义渲染  title: 表头渲染  customRender: 值渲染
      },
      {
        title: '名称',
        dataIndex: 'name',
      },
      {
        title: '分类',
        slots: {customRender: 'category'}
      },
      {
        title: '阅读数',
        dataIndex: 'viewCount',
      },
      {
        title: '点赞数',
        dataIndex: 'voteCount'
      },
      {
        title: 'Action',
        key: 'action',
        slots: {customRender: 'action'}
      }
    ];
    /**
     * 数据查询
     **/
    const handleQuery = (params: any) => {
      loading.value = true;
      //如果不清空现有数据﹒则编辑保存重新加载数据后﹐再点编辑、则列表显示的还是编辑前的数据
      ebook.value =[];
      axios.get("/ebook/list", {
        params:{
          page:params.page,
          size:params.size,
          name:param.value.name
        }
      }).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success){
          ebooks.value = data.content.list;
        }else {
          message.error(data.message);
        }


        //重置分页按钮
        pagination.value.current = params.page;//点第二页的按钮的时候前端 不会刷新 还是第一页的地方 实际我们以及到第二页了
        pagination.value.total=data.content.total;
      });
    };
    /**
     * 表格点击页码时触发
     */
    const handleTableChange = (pagination: any) => {
      console.log("看看自带的分页参数都有啥：" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };

    /**表单*/
    /*
    * 数组·[100,101] 对应:前端开发/Vue
    * */
    const categoryIds = ref();
    const ebook=ref();
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const handleModalOk = () => {
      modalLoading.value = true;
      ebook.value.category1Id = categoryIds.value[0];
      ebook.value.category2Id = categoryIds.value[1];
      axios.post("/ebook/save",ebook.value).then((response) => {
        modalLoading.value = false;   //只要返回了，就去掉loging效果
        const data = response.data;  //commonResp
        if(data.success){
          modalVisible.value = false;
          //重新加载列表
          handleQuery({
            page:pagination.value.current,  //查询当前所在的页
            size:pagination.value.pageSize
          });
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
      ebook.value = Tool.copy(record);//把列表的值先复制出来，就不影响record
      categoryIds.value = [ebook.value.category1Id, ebook.value.category2Id]
    };
    /**
     * 添加
     */
    const add = () =>{
      modalVisible .value = true;
      ebook.value={};
    };


    /**
     * 删除
     */
    const handleDelete = ( id:number ) =>{

      axios.delete("/ebook/delete/"+id).then((response)=>{
        const data = response.data;  //data = commonResp
        if(data.success){
          //重新加载列表
          handleQuery({
            page:pagination.value.current,  //查询当前所在的页
            size:pagination.value.pageSize
          });
        }
      })
    };

    const level1 = ref(); //一级分类树，children属性就是二级分类
    let categorys: any;
    /**
     * 数据查询
     **/
    const handleQueryCategory = () => {
      loading.value = true;
      axios.get("/category/all").then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success){
          categorys = data.content;
          console.log("原始数据:",categorys);

          level1.value=[];
          level1.value = Tool.array2Tree(categorys,0);
          console.log("树形结构:",level1.value);

          //加载完分类后·再则载电子书·否则如果分类树加载很慢﹐则电子书渲染会报错
          handleQuery({
            page:1,
            size:pagination.value.pageSize
          });
        }else {
          message.error(data.message);
        }
      });
    };

    const getCategoryName =(cid:number) => {
      //console.log(cid)
      let result ="";
      categorys.forEach((item:any) => {
        if (item.id === cid){
          //result item.name; 注意这里的return不起作用
          result = item.name;
        }
      });
      return  result;
    };


    onMounted(() => {
      handleQueryCategory();


    });

    return {
      param,
      ebooks,//表格
      pagination,
      columns,
      loading,
      handleTableChange,
      handleQuery,
      getCategoryName,


      edit,
      add,
      handleDelete,

      ebook,
      modalVisible,
      modalLoading,
      handleModalOk,
      categoryIds,
      level1,

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