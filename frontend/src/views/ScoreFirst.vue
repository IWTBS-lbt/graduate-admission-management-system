<template>
  <el-card>
    <template #header><span>📝 初试成绩管理</span></template>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <h3>录入成绩</h3>
          <el-form :model="scoreForm" :rules="scoreRules" ref="scoreFormRef" label-width="80px">
            <el-form-item label="考号" prop="examId">
              <el-autocomplete
                v-model="scoreForm.examId"
                :fetch-suggestions="fetchStudentSuggestions"
                placeholder="输入考号，自动提示已有考生"
              />
            </el-form-item>
            <el-form-item label="政治">
              <el-input-number v-model="scoreForm.politics" :min="0" :max="100" />
            </el-form-item>
            <el-form-item label="英语">
              <el-input-number v-model="scoreForm.english" :min="0" :max="100" />
            </el-form-item>
            <el-form-item label="专业基础">
              <el-input-number v-model="scoreForm.professionalBase" :min="0" :max="150" />
            </el-form-item>
            <el-button type="primary" @click="handleSaveScore">保存成绩</el-button>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <h3>筛选复试名单</h3>
          <el-form :inline="true">
            <el-form-item label="政治线">
              <el-input-number v-model="line.politics" :min="0" :max="100" />
            </el-form-item>
            <el-form-item label="英语线">
              <el-input-number v-model="line.english" :min="0" :max="100" />
            </el-form-item>
            <el-form-item label="专业基础线">
              <el-input-number v-model="line.professionalBase" :min="0" :max="150" />
            </el-form-item>
            <el-form-item label="总分线">
              <el-input-number v-model="line.total" :min="0" :max="500" />
            </el-form-item>
            <el-button type="primary" @click="handleCheck">筛选</el-button>
          </el-form>
          <el-table :data="eligibleList" size="small" border stripe>
            <el-table-column prop="examId" label="考号" />
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="total" label="总分" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-col :span="24" style="margin-top: 20px;">
      <el-card shadow="hover">
        <h3>📋 所有初试成绩</h3>

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
          <el-button type="success" @click="exportFirstScore">导出 CSV</el-button>
        </div>

        <el-table :data="scoreList" size="small" border stripe>
          <el-table-column prop="examId" label="考号" />
          <el-table-column prop="politics" label="政治" />
          <el-table-column prop="english" label="英语" />
          <el-table-column prop="professionalBase" label="专业基础" />
          <el-table-column prop="total" label="总分" />
        </el-table>

        <!-- 分页 -->
        <div class="pagination-bar">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[5, 10, 20, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="fetchAllScores"
            @current-change="fetchAllScores"
          />
        </div>
      </el-card>
    </el-col>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { saveFirstScore, checkEligible, listFirstScore, exportFirstScore, studentSuggest, firstScoreSuggest } from '@/api'

const scoreFormRef = ref(null)
const scoreForm = ref({ examId: '', politics: 0, english: 0, professionalBase: 0 })
const scoreRules = {
  examId: [{ required: true, message: '请输入考生考号', trigger: 'blur' }]
}
const line = ref({ politics: 60, english: 60, professionalBase: 90, total: 250 })
const eligibleList = ref([])
const scoreList = ref([])
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchAllScores = async () => {
  try {
    const keyword = searchKeyword.value?.trim() || undefined
    const res = await listFirstScore(keyword, currentPage.value, pageSize.value)
    scoreList.value = res.data.records || res.data
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取初试成绩列表失败:', error)
  }
}

// 搜索框自动补全（初试成绩中的考号）
const fetchScoreSuggestions = async (queryString, cb) => {
  if (!queryString || queryString.trim().length === 0) { cb([]); return }
  try {
    const res = await firstScoreSuggest(queryString.trim())
    cb(res.data || [])
  } catch { cb([]) }
}

// 录入表单自动补全（所有考生考号）
const fetchStudentSuggestions = async (queryString, cb) => {
  if (!queryString || queryString.trim().length === 0) { cb([]); return }
  try {
    const res = await studentSuggest(queryString.trim())
    cb(res.data || [])
  } catch { cb([]) }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchAllScores()
}

const handleReset = () => {
  searchKeyword.value = ''
  currentPage.value = 1
  fetchAllScores()
}

const handleSaveScore = async () => {
  // 表单验证
  try {
    await scoreFormRef.value.validate()
  } catch {
    return
  }

  const data = {
    examId: scoreForm.value.examId,
    politics: Number(scoreForm.value.politics),
    english: Number(scoreForm.value.english),
    professionalBase: Number(scoreForm.value.professionalBase)
  }

  try {
    await saveFirstScore(data)
    ElMessage.success('成绩保存成功！')
    await fetchAllScores()
    handleCheck()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  }
}

const handleCheck = async () => {
  try {
    const params = {
      politicsLine: line.value.politics,
      englishLine: line.value.english,
      professionalBaseLine: line.value.professionalBase,
      totalScoreLine: line.value.total
    }
    const res = await checkEligible(params)
    eligibleList.value = res.data
    if (res.data.length === 0) {
      ElMessage.warning('没有符合条件的考生，请检查分数线或录入数据')
    }
  } catch (error) {
    ElMessage.error(error.message || '筛选失败')
  }
}

onMounted(() => {
  fetchAllScores()
  handleCheck()
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
