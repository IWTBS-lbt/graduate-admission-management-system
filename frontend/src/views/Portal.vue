<template>
  <div class="portal-container">
    <!-- 顶部导航 -->
    <div class="portal-header">
      <span class="logo">🎓 研究生招生信息服务平台</span>
    </div>

    <div class="portal-body">
      <el-tabs v-model="activeTab" type="border-card" class="portal-tabs">
        <!-- 考生报名 -->
        <el-tab-pane label="📝 考生报名" name="apply">
          <div v-if="!applySubmitted">
            <div class="form-title">
              <h2>考生报名登记表</h2>
              <p>请如实填写以下信息，提交后将作为您的考生档案</p>
            </div>

            <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" size="large">
              <el-divider content-position="left">基本信息</el-divider>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="考号" prop="examId">
                    <el-input v-model="form.examId" placeholder="请输入准考证号" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="姓名" prop="name">
                    <el-input v-model="form.name" placeholder="请输入姓名" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="性别">
                    <el-select v-model="form.gender">
                      <el-option label="男" value="男" /><el-option label="女" value="女" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="年龄">
                    <el-input-number v-model="form.age" :min="18" :max="60" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="政治面貌">
                    <el-select v-model="form.political" placeholder="请选择">
                      <el-option label="共青团员" value="共青团员" />
                      <el-option label="中共党员" value="中共党员" />
                      <el-option label="群众" value="群众" />
                      <el-option label="其他" value="其他" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="是否应届">
                    <el-select v-model="form.isFresh">
                      <el-option label="应届" :value="1" /><el-option label="往届" :value="0" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="学历">
                    <el-select v-model="form.education">
                      <el-option label="本科毕业" value="本科毕业" />
                      <el-option label="本科结业" value="本科结业" />
                      <el-option label="高职高专" value="高职高专" />
                      <el-option label="同等学力" value="同等学力" />
                      <el-option label="硕士研究生" value="硕士研究生" />
                      <el-option label="其他" value="其他" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="毕业院校">
                    <el-input v-model="form.source" placeholder="请输入毕业院校" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-divider content-position="left">报考信息</el-divider>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="报考专业" prop="majorCode">
                    <el-select v-model="form.majorCode" placeholder="请选择报考专业" filterable>
                      <el-option v-for="m in majorList" :key="m.majorCode"
                        :label="m.majorCode + ' - ' + m.majorName" :value="m.majorCode" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="报考类别">
                    <el-select v-model="form.type">
                      <el-option label="全日制" value="全日制" />
                      <el-option label="非全日制" value="非全日制" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-divider />
              <el-form-item>
                <el-button type="primary" size="large" :loading="applyLoading" @click="handleSubmit" style="width:200px">提交报名</el-button>
                <el-button size="large" @click="handleReset">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 报名成功 -->
          <div v-else class="success-box">
            <el-icon size="80" color="#67C23A"><CircleCheckFilled /></el-icon>
            <h2>报名成功！</h2>
            <p>考号：<b>{{ submittedExamId }}</b></p>
            <p class="tip">请牢记您的考号，后续查分和录取查询需要使用</p>
            <el-button type="primary" @click="handleNewApply">继续报名</el-button>
          </div>
        </el-tab-pane>

        <!-- 成绩查询 -->
        <el-tab-pane label="🔍 成绩与录取查询" name="inquiry">
          <div class="query-section">
            <div class="query-input-row">
              <el-input v-model="examId" placeholder="请输入准考证号" size="large" style="width:260px"
                @keyup.enter="handleInquiry" clearable />
              <el-button type="primary" size="large" :loading="inquiryLoading" @click="handleInquiry">查询</el-button>
            </div>

            <div v-if="inquiryResult" class="result-area">
              <el-card shadow="hover" v-if="inquiryResult.name && inquiryResult.name !== '考生信息未录入'">
                <el-descriptions :column="3" border title="👤 考生信息">
                  <el-descriptions-item label="考号">{{ inquiryResult.examId }}</el-descriptions-item>
                  <el-descriptions-item label="姓名">{{ inquiryResult.name }}</el-descriptions-item>
                  <el-descriptions-item label="报考专业">{{ inquiryResult.majorName || '-' }}</el-descriptions-item>
                </el-descriptions>

                <el-divider />
                <h4>📝 初试成绩 <el-tag v-if="inquiryResult.hasFirstScore" type="success" size="small">已公布</el-tag><el-tag v-else type="info" size="small">未录入</el-tag></h4>
                <el-descriptions v-if="inquiryResult.hasFirstScore" :column="4" border style="margin-top:8px">
                  <el-descriptions-item label="政治">{{ inquiryResult.politics }}</el-descriptions-item>
                  <el-descriptions-item label="英语">{{ inquiryResult.english }}</el-descriptions-item>
                  <el-descriptions-item label="专业基础">{{ inquiryResult.professionalBase }}</el-descriptions-item>
                  <el-descriptions-item label="初试总分"><b style="color:#409EFF;font-size:16px">{{ inquiryResult.firstTotal }}</b></el-descriptions-item>
                </el-descriptions>

                <el-divider />
                <h4>🎤 复试成绩 <el-tag v-if="inquiryResult.hasSecondScore" type="success" size="small">已公布</el-tag><el-tag v-else-if="inquiryResult.hasFirstScore" type="warning" size="small">待复试</el-tag><el-tag v-else type="info" size="small">未开始</el-tag></h4>
                <el-descriptions v-if="inquiryResult.hasSecondScore" :column="4" border style="margin-top:8px">
                  <el-descriptions-item label="专业科目">{{ inquiryResult.professional }}</el-descriptions-item>
                  <el-descriptions-item label="面试">{{ inquiryResult.interview }}</el-descriptions-item>
                  <el-descriptions-item label="上机">{{ inquiryResult.computerTest }}</el-descriptions-item>
                  <el-descriptions-item label="复试总分"><b style="color:#409EFF;font-size:16px">{{ inquiryResult.secondTotal }}</b></el-descriptions-item>
                </el-descriptions>

                <el-divider />
                <h4>🏆 录取结果</h4>
                <div v-if="inquiryResult.hasAdmission" :class="inquiryResult.isAdmitted === 1 ? 'admitted-box' : 'rejected-box'">
                  <template v-if="inquiryResult.isAdmitted === 1">
                    <el-icon size="50" color="#67C23A"><CircleCheckFilled /></el-icon>
                    <h3 style="color:#67C23A">🎉 恭喜录取！</h3>
                    <el-descriptions :column="3" border style="margin-top:12px">
                      <el-descriptions-item label="初试总分">{{ inquiryResult.firstTotal }}</el-descriptions-item>
                      <el-descriptions-item label="复试总分">{{ inquiryResult.secondTotal }}</el-descriptions-item>
                      <el-descriptions-item label="综合总分"><b style="color:#E6A23C;font-size:18px">{{ inquiryResult.combinedTotal }}</b></el-descriptions-item>
                      <el-descriptions-item label="录取系别">{{ inquiryResult.admitDepartment }}</el-descriptions-item>
                    </el-descriptions>
                  </template>
                  <template v-else>
                    <el-icon size="50" color="#F56C6C"><CircleCloseFilled /></el-icon>
                    <h3 style="color:#F56C6C">未达录取线</h3>
                    <p style="color:#909399">综合总分：{{ inquiryResult.combinedTotal }}</p>
                  </template>
                </div>
                <p v-else style="color:#909399;text-align:center;padding:20px">录取工作尚未开始，请耐心等待</p>
              </el-card>

              <el-card v-else-if="notFound" shadow="hover" style="text-align:center">
                <el-icon size="60" color="#F56C6C"><WarningFilled /></el-icon>
                <h3 style="color:#F56C6C;margin-top:12px">未查到该考生信息</h3>
                <p style="color:#909399">请检查考号是否正确</p>
              </el-card>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api'
import { getMajorList, addStudent } from '@/api'

const activeTab = ref('apply')

// ====== 报名模块 ======
const formRef = ref(null)
const applyLoading = ref(false)
const applySubmitted = ref(false)
const submittedExamId = ref('')
const majorList = ref([])

const form = ref({
  examId: '', name: '', gender: '男', age: 22,
  political: '', isFresh: 1, education: '本科毕业',
  source: '', majorCode: '', type: '全日制'
})

const rules = {
  examId: [{ required: true, message: '请输入考号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  majorCode: [{ required: true, message: '请选择报考专业', trigger: 'change' }]
}

const handleSubmit = async () => {
  try { await formRef.value.validate() } catch { return }
  applyLoading.value = true
  try {
    await addStudent(form.value)
    submittedExamId.value = form.value.examId
    applySubmitted.value = true
  } catch (error) {
    ElMessage.error(error.message || '报名失败，考号可能已被使用')
  } finally { applyLoading.value = false }
}

const handleReset = () => {
  formRef.value?.resetFields()
  form.value = { examId: '', name: '', gender: '男', age: 22, political: '', isFresh: 1, education: '本科毕业', source: '', majorCode: '', type: '全日制' }
}

const handleNewApply = () => {
  handleReset()
  applySubmitted.value = false
}

const fetchMajors = async () => {
  try { const res = await getMajorList(undefined, 1, 100); majorList.value = res.data.records || res.data } catch {}
}

// ====== 查分模块 ======
const examId = ref('')
const inquiryLoading = ref(false)
const inquiryResult = ref(null)
const notFound = ref(false)

const handleInquiry = async () => {
  if (!examId.value.trim()) { ElMessage.warning('请输入考号'); return }
  inquiryLoading.value = true
  inquiryResult.value = null
  notFound.value = false
  try {
    const res = await request.get(`/inquiry/${examId.value.trim()}`)
    const data = res.data
    if (data.name === '考生信息未录入') { notFound.value = true } else { inquiryResult.value = data }
  } catch { ElMessage.error('查询失败') }
  finally { inquiryLoading.value = false }
}

onMounted(fetchMajors)
</script>

<style scoped>
.portal-container { min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.portal-header { background: rgba(255,255,255,0.95); padding: 14px 40px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.logo { font-size: 20px; font-weight: bold; color: #303133; }
.portal-body { max-width: 960px; margin: 30px auto; padding: 0 20px 40px; }
.portal-tabs { border-radius: 12px; overflow: hidden; }

.form-title { text-align: center; margin-bottom: 20px; }
.form-title h2 { margin: 0 0 8px; color: #303133; }
.form-title p { color: #909399; font-size: 14px; margin: 0; }

.el-select { width: 100%; }

.success-box { text-align: center; padding: 40px 0; }
.success-box h2 { color: #67C23A; margin: 16px 0; }
.success-box p { color: #606266; margin: 8px 0; }
.success-box .tip { color: #E6A23C; font-size: 13px; margin-bottom: 20px; }
.success-box b { color: #409EFF; font-size: 18px; }

.query-section { padding: 10px 0; }
.query-input-row { display: flex; justify-content: center; gap: 12px; margin-bottom: 24px; }
.result-area { margin-top: 16px; }

h4 { margin: 8px 0; font-size: 15px; }
h4 .el-tag { margin-left: 8px; }

.admitted-box { text-align: center; padding: 20px; border: 2px solid #67C23A; border-radius: 8px; }
.admitted-box h3 { margin: 12px 0; }
.rejected-box { text-align: center; padding: 20px; border: 2px solid #F56C6C; border-radius: 8px; }
.rejected-box h3 { margin: 12px 0; }
</style>
