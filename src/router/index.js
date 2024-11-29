// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store';


const routes = [
    { path: '/', component: () => import('@/view/HomePage.vue') },
    { path: '/login', component: () => import('@/view/LoginPage.vue') },
    { path: '/register', component: () => import('@/view/RegisterPage.vue') },
    { path: '/profile', component: () => import('@/view/ProfilePage.vue'), meta: { requiresAuth: true } },
    { path: '/chat', component: () => import('@/view/ChatPage.vue'), meta: { requiresAuth: true } },
    { path: '/createBot', component: () => import('@/view/CreateBot.vue'), meta: { requiresAuth: true } },
    { path: '/review/:botId', component: () => import('@/view/reviewPage.vue'), meta: { requiresAuth: true } },
    { path: '/market', component: () => import('@/view/MarketPage.vue'), meta: { requiresAuth: true } },
    { path: '/chatHistory', component: () => import('@/view/ChatHistory.vue'), meta: { requiresAuth: true } },
    { path: '/setting', component: () => import('@/view/SettingPage.vue'), meta: { requiresAuth: true } },
    { path: '/reset-password', component: () => import('@/view/ResetPasswordPage.vue') },
    { path: '/official-bot-edit', component:  () => import('@/view/OfficialBotPage.vue'), meta: { requiresAuth: true } },
    { path: '/popularity', component: () => import('@/view/PopularityPage.vue')},
    { path: '/view-profile', component: () => import('@/view/ViewProfile.vue'), meta: { requiresAuth: true } },
    { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('@/view/NotFound.vue') }
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
        } else if (to.path === '/official-bot-edit' && store.getters.personalProfile.role !== 'ADMIN'){
            next({ path: '/404' });
        } else {
            next();
        }
    } else {
        next();
    }
});

export default router