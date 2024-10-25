// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/view/HomePage.vue'
import LoginPage from '@/view/LoginPage.vue'
import RegisterPage from "@/view/RegisterPage.vue";
import ProfilePage from "@/view/ProfilePage.vue";
import ChatPage from "@/view/ChatPage.vue";
import createBot from "@/view/CreateBot.vue";
import marketPage from "@/view/MarketPage.vue";
import reviewPage from "@/view/reviewPage.vue";
import ChatHistory from "@/view/ChatHistory.vue";
import store from '@/store';

const routes = [
    { path: '/', component: HomePage },
    { path: '/login', component: LoginPage },
    { path: '/register', component: RegisterPage },
    { path: '/profile', component: ProfilePage, meta: { requiresAuth: true } },
    { path: '/chat', component: ChatPage, meta: { requiresAuth: true } },
    { path: '/createBot', component: createBot, meta: { requiresAuth: true } },
    { path: '/review/:botId', component: reviewPage, meta: { requiresAuth: true }},
    { path: '/market', component: marketPage, meta: { requiresAuth: true } },
    { path: '/chatHistory', component: ChatHistory, meta: { requiresAuth: true } }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!store.getters.isLoggedIn) {
            next({
                path: '/login',
                query: { redirect: to.fullPath }
            });
        } else {
            next();
        }
    } else {
        next();
    }
});

export default router