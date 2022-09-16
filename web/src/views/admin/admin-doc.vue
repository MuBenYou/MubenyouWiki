<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '80px'  }"
    >
      <a-row>
        <a-col :span="8">
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
          <!--列,key id,数据doc,分页,等待框,分页执行方法-->
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

                  <a-button type="primary"  >
                    删除
                  </a-button>
                </a-popconfirm>

              </a-space>
            </template>
          </a-table>
        </a-col>
        <a-col :span="16">
          <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">

            <a-form-item label="名称">
              <a-input v-model:value="doc.name"/>
            </a-form-item>
            <a-form-item label="父文档">
              <a-tree-select
                  v-model:value="doc.parent"
                  style="width: 100%"
                  :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                  placeholder="请选择父文档"
                  tree-default-expand-all
                  :tree-data="treeSelectData"
                  :replaceFields="{title:'name', key:'id', value:'id'}"
              >
              </a-tree-select>
            </a-form-item>

            <a-form-item label="顺序">
              <a-input v-model:value="doc.sort"/>
            </a-form-item>
            <a-form-item label="内容">
              <div id="content"></div>
            </a-form-item>


          </a-form>
        </a-col>
      </a-row>


    </a-layout-content>

  </a-layout>
  <!--  <a-modal-->
  <!--      title="文档表单"-->
  <!--      v-model:visible="modalVisible"-->
  <!--      :confirm-loading="modalLoading"-->
  <!--      ok-text="我测,说藏话了"-->
  <!--      @ok="handleModalOk"-->
  <!--  >-->
  <!--    &lt;!&ndash;弹出表单&ndash;&gt;-->
  <!--    -->
  <!--  </a-modal>-->

</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';//写上onMounted VUE3.0 setup集成了 导入ref 做响应式数据
import axios from 'axios';
import {message} from "ant-design-vue";
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";
// import { Modal } from 'ant-design-vue';

import E from 'wangeditor';


export default defineComponent({
  name: 'AdminDoc',
  setup() {
    //这个是路由内置的一个变量,我们可以按alt+enter导入
    const route = useRoute();
    console.log("路由:",route);
    console.log("route.path: ", route.path);             //path路径不含参数
    console.log("route.query: ", route.query);           //query参数
    console.log("route.param: ", route.params);         //params参数
    console.log("route.fullPath: ", route.fullPath);     //fullPath全路劲，包含路劲path和参数query，params
    console.log("route.name: ", route.name);              //index.ts中的name
    console.log("route.meta: ", route.meta);              //自定义的扩展信息
    const param = ref();
    param.value = {};
    const docs = ref();//响应式数据 获取的书籍实时反馈到页面上


    const loading = ref(false);
    // const showConfirm = () => {
    //   Modal.confirm({
    //     title: '你确定要删除吗?',
    //     icon: createVNode(ExclamationCircleOutlined),
    //     content: createVNode('div', { style: 'color:red;' }, 'Some descriptions'),
    //     onOk() {
    //       console.log('OK');
    //     },
    //     onCancel() {
    //       console.log('Cancel');
    //     },
    //     class: 'test',
    //   });
    // };

    const columns = [//页面的响应变量 不是数据的响应变量 代表就是这个表格里面有多少个数据 下面数据我们自己定义的

      {
        title: '名称',
        dataIndex: 'name',
      },
      {
        title: '父文档',
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
    const level1 = ref(); //一级文档树，children属性就是二级文档

    /**
     * 数据查询
     **/
    const handleQuery = () => {
      loading.value = true;
      //如果不清空现有数据，刘编辑保存重新加载数据后，再点编辑
      level1.value=[];
      axios.get("/doc/all").then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success){
          docs.value = data.content;
          console.log("原始数据:",docs.value);

          level1.value=[];
          level1.value = Tool.array2Tree(docs.value,0);
          console.log("树形结构:",level1);
        }else {
          message.error(data.message);
        }
      });
    };


    /**表单*/
        //因为树选择组件的属性状态，会随当前编辑的节点而变化,所以单独声明一个响应式变量,而不用level1
    const treeSelectData =ref();
    treeSelectData.value=[];
    const doc=ref({});
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const  editor = new E('#content');

    
    const handleModalOk = () => {
      modalLoading.value = true;
      axios.post("/doc/save",doc.value).then((response) => {
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
    /*将某节点及其子孙节点全部置为disabled*/
    const setDisable = (treeSelectData:any, id:any)=>{
      // console.log(treeSelectData, id);
      //遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++){
        const  node = treeSelectData[i];
        if (node.id === id) {
          //如果当前节点就是目标节点
          console.log("disabled", node);
          //将目标节点设置为disabled
          node.disabled = true;

          //遍历所有子节点，将所有子节点全部都加上disabled
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              setDisable(children, children[j].id)
            }
          }
        }else {
          //如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
            if (Tool.isNotEmpty(children)){
              setDisable(children,id);
            }
          }
        }
    };

    const ids:Array<string> = [];
    /*查找整根树枝*/
    const getDeleteIds = (treeSelectData:any, id:any)=>{
      // console.log(treeSelectData, id);
      //遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++){
        const  node = treeSelectData[i];
        if (node.id === id) {
          //如果当前节点就是目标节点
          console.log("delete", node);
          //将目标ID放又结果集ids
          // node.disabled = true;
          ids.push(id);

          //遍历所有子节点
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              getDeleteIds(children, children[j].id)
            }
          }
        }else {
          //如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if (Tool.isNotEmpty(children)){
            getDeleteIds(children,id);
          }
        }
      }
    };


    /**
     * 编辑
     */
    const edit = ( record:any ) =>{

      modalVisible .value = true;
      doc.value = Tool.copy(record);//把列表的值先复制出来，就不影响record
      //不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
      treeSelectData.value =Tool.copy(level1.value);
      setDisable(treeSelectData.value,record.id);

      //为选择树添加一个“无”
      treeSelectData.value.unshift({id: 0,name:'无'});
      setTimeout(function (){
        editor.create();
      },100);

    };
    /**
     * 添加
     */
    const add = () =>{
      modalVisible .value = true;
      doc.value={
        ebookId: route.query.ebookId
      };
      treeSelectData.value = Tool.copy(level1.value);
      //为选择树添加一个“无”
      treeSelectData.value.unshift({id: 0,name:'无'});
      setTimeout(function (){
        editor.create();
      },100);

    };


    /**
     * 删除
     */
    const handleDelete = ( id:number ) =>{
      getDeleteIds(level1.value,id);
      axios.delete("/doc/delete/"+ids.join(",")).then((response)=>{
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
      //docs,//表格
      level1,
      columns,
      loading,
      handleQuery,


      edit,
      add,
      handleDelete,
      treeSelectData,
      // showConfirm,

      doc,
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