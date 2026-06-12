<template>
  <el-card>
    <template #header><span>🎤 复试成绩管理</span></template>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <h3>录入复试成绩</h3>
          <el-form :model="scoreForm" :rules="scoreRules" ref="scoreFormRef" label-width="80px">
            <el-form-item label="考号" prop="examId">
              <el-autocomplete
                v-model="scoreForm.examId"
                :fetch-suggestions="fetchStudentSuggestions"
                placeholder="输入考号，自动提示已有考生"
              />
            </el-form-item>
            <el-form-item label="专业">
              <el-input-number v-model="scoreForm.professional" :min="0" :max="100" />
            </el-form-item>
            <el-form-item label="面试">
              <el-input-number v-model="scoreForm.interview" :min="0" :max="100" />
            </el-form-item>
            <el-form-item label="上机">
              <el-input-number v-model="scoreForm.computerTest" :min="0" :max="100" />
            </el-form-item>
            <el-button type="primary" @click="handleSaveScore">保存成绩</el-button>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <h3>生成录取名单</h3>
          <el-form :inline="true">
            <el-form-item label="总分线">
              <el-input-number v-model="totalScoreLine" :min="0" :max="1000" />
            </el-form-item>
            <el-button type="success" @click="handleGenerate">生成录取名单</el-button>
          </el-form>
          <el-table :data="admissionList" size="small" border stripe>
            <el-table-column prop="examId" label="考号" />
            <el-table-column prop="isAdmitted" label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.isAdmitted ? 'success' : 'info'">
                  {{ scope.row.isAdmitted ? '已录取' : '未录取' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-col :span="24" style="margin-top: 20px;">
      <el-card shadow="hover">
        <h3>📋 所有复试成绩</h3>

        <!-- 搜索栏 -->
        <div class="search-bar">
          <el-autocomplete
            v-model="searchKeyword"
            :fetch-suggestions="fetchScoreSuggestions"
            placeholder="搜索考号或姓名..."
            clearable
            style="width: 300px"
            @select="handleSearch"
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="exportSecondScore">导出 CSV</el-button>
        </div>

        <el-table :data="secondScoreList" size="small" border stripe>
          <el-table-column prop="examId" label="考号" />
          <el-table-column prop="professional" label="专业成绩" />
          <el-table-column prop="interview" label="面试成绩" />
          <el-table-column prop="computerTest" label="上机成绩" />
          <el-table-column prop="total" label="复试总分" />
        </el-table>

        <!-- 分页 -->
        <div class="pagination-bar">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[5, 10, 20, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="fetchSecondScoreList"
            @current-change="fetchSecondScoreList"
          />
        </div>
      </el-card>
    </el-col>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { saveSecondScore, generateAdmission, getAdmissionList, listSecondScore, exportSecondScore, studentSuggest, secondScoreSuggest } from '@/api'

const scoreFormRef = ref(null)
const scoreForm = ref({ examId: '', professional: 0, interview: 0, computerTest: 0 })
const scoreRules = {
  examId: [{ required: true, message: '请输入考生考号', trigger: 'blur' }]
}
const totalScoreLine = ref(360)
const admissionList = ref([])
const secondScoreList = ref([])
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const handleSaveScore = async () => {
  try {
    await scoreFormRef.value.validate()
  } catch {
    return
  }
  try {
    await saveSecondScore(scoreForm.value)
    ElMessage.success('复试成绩保存成功')
    fetchSecondScoreList()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  }
}

const handleGenerate = async () => {
  try {
    const res = await generateAdmission(totalScoreLine.value)
    ElMessage.success('录取名单生成成功')
    admissionList.value = res.data
  } catch (error) {
    ElMessage.error(error.message || '生成录取名单失败')
  }
}

const fetchAdmissionList = async () => {
  try {
    const res = await getAdmissionList()
    admissionList.value = res.data.records || res.data
  } catch (error) {
    // API 错误已在拦截器中提示，此处静默处理
    console.error('获取录取名单失败:', error)
  }
}

const fetchSecondScoreList = async () => {
  try {
    const keyword = searchKeyword.value?.trim() || undefined
    const res = await listSecondScore(keyword, currentPage.value, pageSize.value)
    secondScoreList.value = res.data.records || res.data
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取复试成绩列表失败:', error)
  }
}

const fetchScoreSuggestions = async (queryString, cb) => {
  if (!queryString || queryString.trim().length === 0) { cb([]); return }
  try {
    const res = await secondScoreSuggest(queryString.trim())
    cb(res.data || [])
  } catch { cb([]) }
}

const fetchStudentSuggestions = async (queryString, cb) => {
  if (!queryString || queryString.trim().length === 0) { cb([]); return }
  try {
    const res = await studentSuggest(queryString.trim())
    cb(res.data || [])
  } catch { cb([]) }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchSecondScoreList()
}

const handleReset = () => {
  searchKeyword.value = ''
  currentPage.value = 1
  fetchSecondScoreList()
}

onMounted(() => {
  fetchAdmissionList()
  fetchSecondScoreList()
})
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}
.pagination-bar {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
