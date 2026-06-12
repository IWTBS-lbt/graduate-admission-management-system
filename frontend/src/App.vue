<template>
  <div id="app">
    <template v-if="isLoggedIn">
      <el-container style="height:100vh">
        <!-- 左侧菜单 -->
        <el-aside width="220px" class="sidebar">
          <div class="logo">🎓 招生管理系统</div>
          <el-menu
            :default-active="$route.path"
            router
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409EFF"
          >
            <el-menu-item index="/stats">
              <el-icon><DataAnalysis /></el-icon>
              <span>数据统计</span>
            </el-menu-item>
            <el-menu-item index="/department">
              <el-icon><OfficeBuilding /></el-icon>
              <span>院系管理</span>
            </el-menu-item>
            <el-menu-item index="/major">
              <el-icon><Collection /></el-icon>
              <span>专业管理</span>
            </el-menu-item>
            <el-menu-item index="/student">
              <el-icon><User /></el-icon>
              <span>考生档案</span>
            </el-menu-item>
            <el-menu-item index="/first-score">
              <el-icon><Edit /></el-icon>
              <span>初试成绩</span>
            </el-menu-item>
            <el-menu-item index="/second-score">
              <el-icon><ChatLineSquare /></el-icon>
              <span>复试成绩</span>
            </el-menu-item>
            <el-menu-item index="/admission">
              <el-icon><Finished /></el-icon>
              <span>录取管理</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- 右侧 -->
        <el-container>
          <el-header class="top-header">
            <span class="user-info">
              <el-icon><UserFilled /></el-icon>
              {{ userName }}
            </span>
            <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
          </el-header>
          <el-main class="main-area">
            <router-view />
          </el-main>
        </el-container>
      </el-container>
    </template>

    <!-- 未登录时显示登录页/报名页 -->
    <template v-else>
      <router-view />
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUser, removeUser, isAuthenticated } from '@/utils/auth'

const router = useRouter()
const isLoggedIn = computed(() => isAuthenticated())
const userName = computed(() => {
  const user = getUser()
  return user ? user.username : ''
})

const handleLogout = () => {
  removeUser()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif; }
#app { height: 100vh; }

.sidebar { background-color: #304156; overflow-y: auto; }
.sidebar .logo {
  height: 60px; display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 18px; font-weight: bold;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.sidebar .el-menu { border-right: none; }

.top-header {
  background: #fff; display: flex; align-items: center; justify-content: flex-end;
  gap: 16px; box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
.user-info { display: flex; align-items: center; gap: 6px; color: #606266; font-size: 14px; }

.main-area { background: #f0f2f5; }

.el-card { border-radius: 8px; }
.search-bar { display: flex; align-items: center; gap: 10px; margin-bottom: 16px; }
.pagination-bar { display: flex; justify-content: flex-end; margin-top: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
