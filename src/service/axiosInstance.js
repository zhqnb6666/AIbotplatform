// src/axiosInstance.js
import axios from 'axios';

// 创建 axios 实例
const axiosInstance = axios.create({
    baseURL: '/api', // 使用代理服务器
    withCredentials: true, // 发送跨域请求时携带 cookie

    headers: {
        'Content-Type': 'application/json; charset=utf-8',
    }

});

// // 获取 CSRF 令牌并存储到 localStorage
// axiosInstance.get('/csrf-token').then(response => {
//     localStorage.setItem('csrfToken', response.data.token);
// }).catch(error => {
//     console.error('Error fetching CSRF token:', error);
// });
//
// // 请求拦截器，添加 CSRF 令牌到请求头
// axiosInstance.interceptors.request.use(config => {
//     const csrfToken = localStorage.getItem('csrfToken'); // 从 localStorage 获取 CSRF 令牌
//     if (csrfToken) {
//         config.headers['X-CSRF-Token'] = csrfToken; // 设置 CSRF 令牌到请求头
//     }
//     return config;
// }, error => {
//     return Promise.reject(error);
// });

export default axiosInstance;