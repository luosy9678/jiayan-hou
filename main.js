import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { vantComponents } from './vant-components'

// 创建Vue应用
const app = createApp(App)

// 注册Vant组件
Object.entries(vantComponents).forEach(([name, component]) => {
  app.component(name, component)
})

// 使用路由
app.use(router)

// 挂载应用
app.mount('#app') 