import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from "@/store";
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import * as Icons from '@ant-design/icons-vue';
import axios from "axios";


/**
 * axios拦截器
 * 去store把token拿出来 不为空就加入一个header增加这个token 打印日志
 */
axios.interceptors.request.use((config)=> {
    console.log('请求参数:', config);
    return config;
}, error => {
    return Promise.reject(error);
});
axios.interceptors.response.use((response)=> {
    console.log('返回参数', response);
    return response;
}, error => {
    console.log('返回错误:', error);
    return Promise.reject(error);
});


//axios的默认url地址，修改为自定义url
axios.defaults.baseURL = process.env.VUE_APP_SERVER;
const app=createApp(App);
app.use(store).use(router).use(Antd).mount('#app')

// 全局使用图标
const icons: any = Icons;
for (const i in icons) {
    app.component(i, icons[i]);
}

//测试多环节是否成功
console.log('环境', process.env.NODE_ENV);
console.log('地址', process.env.VUE_APP_SERVER);




