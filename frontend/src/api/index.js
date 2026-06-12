import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken } from '@/utils/auth'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 5000,
})

// 请求拦截器：附加 JWT 认证 Token
request.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器：统一处理 Result 格式
request.interceptors.response.use(
  response => {
    const res = response.data
    // 后端返回的 Result 对象，code !== 200 视为业务失败
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  error => {
    console.error('请求错误:', error)
    ElMessage.error('网络请求失败，请检查服务是否启动')
    return Promise.reject(error)
  }
)

// ========== 登录模块 ==========
export const loginUser = (data) => request.post('/user/login', data)

// ========== 统计模块 ==========
export const getAdmissionStats = () => request.get('/stats/admission')
export const getSubjectStats = () => request.get('/stats/subject')
export const getScoreSegmentStats = () => request.get('/stats/segment')
export const getPlanVsActualStats = () => request.get('/stats/plan-vs-actual')
export const getDeptSubjectStats = () => request.get('/stats/dept-subject')
export const getDeptSegmentStats = () => request.get('/stats/dept-segment')

// ========== 专业模块 ==========
export const getMajorList = (keyword, page = 1, pageSize = 10) => request.get('/major/list', { params: { keyword, page, pageSize } })
export const majorSuggest = (keyword) => request.get('/major/suggest', { params: { keyword } })
export const addMajor = (data) => request.post('/major/add', data)
export const updateMajor = (data) => request.put('/major/update', data)
export const deleteMajor = (majorCode) => request.delete(`/major/delete/${majorCode}`)
export const updateMajorCutoffs = (data) => request.put('/major/batch-cutoff', data)

// ========== 考生模块 ==========
export const getStudentList = (params = {}) => request.get('/student/list', { params })
export const studentSuggest = (keyword) => request.get('/student/suggest', { params: { keyword } })
export const addStudent = (data) => request.post('/student/add', data)
export const deleteStudent = (examId) => request.delete(`/student/delete/${examId}`)
export const updateStudent = (data) => request.put('/student/update', data)

// ========== 初试成绩模块 ==========
export const saveFirstScore = (data) => request.post('/first_score/save', data)
export const checkEligible = (params) => request.get('/first_score/check', { params })
export const listFirstScore = (keyword, page = 1, pageSize = 10) => request.get('/first_score/list', { params: { keyword, page, pageSize } })
export const firstScoreSuggest = (keyword) => request.get('/first_score/suggest', { params: { keyword } })

// ========== 复试成绩模块 ==========
export const saveSecondScore = (data) => request.post('/second_score/save', data)
export const listSecondScore = (keyword, page = 1, pageSize = 10) => request.get('/second_score/list', { params: { keyword, page, pageSize } })
export const secondScoreSuggest = (keyword) => request.get('/second_score/suggest', { params: { keyword } })

// ========== 录取模块 ==========
export const generateAdmission = () => request.post('/admission/generate')
export const getAdmissionList = (page = 1, pageSize = 10) => request.get('/admission/list', { params: { page, pageSize } })
export const getAdmissionDetail = (page = 1, pageSize = 10) => request.get('/admission/detail', { params: { page, pageSize } })

// ========== 院系模块 ==========
export const getDeptList = () => request.get('/department/list')
export const getDeptWithMajors = () => request.get('/department/with-majors')
export const addDept = (data) => request.post('/department/add', data)
export const updateDept = (data) => request.put('/department/update', data)
export const deleteDept = (id) => request.delete(`/department/delete/${id}`)

// ========== 导出模块（直接打开下载链接） ==========
const BASE_URL = import.meta.env.VITE_API_BASE_URL || ''
export const exportAdmission = () => window.open(BASE_URL + '/admission/export', '_blank')
export const exportStudent = () => window.open(BASE_URL + '/student/export', '_blank')
export const exportFirstScore = () => window.open(BASE_URL + '/first_score/export', '_blank')
export const exportSecondScore = () => window.open(BASE_URL + '/second_score/export', '_blank')

export default request
