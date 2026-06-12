import { ref } from 'vue'

// 录取名单版本号：每次生成录取名单时 +1，统计页面监听此值自动刷新
export const admissionVersion = ref(0)

// 生成录取名单后调用，通知统计页面刷新
export const notifyAdmissionGenerated = () => {
  admissionVersion.value++
}
