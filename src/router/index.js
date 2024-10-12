// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/view/HomePage.vue'
import LoginPage from '@/view/LoginPage.vue'
import RegisterPage from "@/view/RegisterPage.vue";
import ProfilePage from "@/view/ProfilePage.vue";
import ChatPage from "@/view/ChatPage.vue";
const routes = [
    { path: '/', component: HomePage },
    { path: '/login', component: LoginPage },
    { path: '/register', component: RegisterPage },
    { path: '/profile', component: ProfilePage },
    { path: '/chat', component: ChatPage}
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router