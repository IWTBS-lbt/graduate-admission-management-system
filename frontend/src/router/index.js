import { createRouter, createWebHistory } from 'vue-router'
import { isAuthenticated } from '@/utils/auth'

const routes = [
  { path: '/', redirect: '/stats' },
  { path: '/login', component: () => import('../views/Login.vue'), meta: { noAuth: true } },
  { path: '/portal', component: () => import('../views/Portal.vue'), meta: { noAuth: true } },
  { path: '/stats', component: () => import('../views/Stats.vue') },
  { path: '/department', component: () => import('../views/Department.vue') },
  { path: '/major', component: () => import('../views/Major.vue') },
  { path: '/student', component: () => import('../views/Student.vue') },
  { path: '/first-score', component: () => import('../views/ScoreFirst.vue') },
  { path: '/second-score', component: () => import('../views/ScoreSecond.vue') },
  { path: '/admission', component: () => import('../views/Admission.vue') },
  // 404 通配路由：未匹配路径重定向到首页
  { path: '/:pathMatch(.*)*', redirect: '/stats' },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  // /portal 考生端页面，任何人都能访问，不做拦截
  if (to.path === '/portal') {
    return next()
  }
  // /login 登录页：已登录用户重定向到首页
  if (to.path === '/login') {
    if (isAuthenticated()) {
      return next('/stats')
    }
    return next()
  }
  // 管理端页面：未登录重定向到登录页
  if (!isAuthenticated()) {
    return next('/login')
  }
  next()
})

export default router
