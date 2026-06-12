import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as Icons from '@element-plus/icons-vue'
import router from './router'
import App from './App.vue'

const app = createApp(App)

// 按需注册项目实际使用的图标（避免全量注册 ~200 个图标增大打包体积）
const iconList = [
  'User', 'Lock', 'DataAnalysis', 'Collection', 'Edit',
  'ChatLineSquare', 'Finished', 'Fold', 'Expand', 'UserFilled', 'OfficeBuilding', 'CircleCheckFilled', 'CircleCloseFilled', 'WarningFilled'
]
iconList.forEach(name => {
  if (Icons[name]) app.component(name, Icons[name])
})

app.use(ElementPlus, { locale: zhCn })
app.use(router)
app.mount('#app')
