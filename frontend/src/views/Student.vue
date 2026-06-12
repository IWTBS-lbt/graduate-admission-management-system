<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>👨‍🎓 考生档案</span>
        <el-button type="primary" @click="openAddDialog">+ 添加考生</el-button>
      </div>
    </template>

    <!-- 高级搜索栏 -->
    <div class="search-bar">
      <div class="search-row">
        <el-autocomplete
          v-model="filters.keyword"
          :fetch-suggestions="fetchSuggestions"
          placeholder="搜索考号或姓名..."
          clearable
          style="width: 220px"
          @select="handleSearch"
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        />
        <el-input v-model="filters.source" placeholder="考生来源" clearable style="width: 160px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-select v-model="filters.majorCode" placeholder="专业代码" clearable filterable style="width: 180px" @change="handleSearch" @clear="handleSearch">
          <el-option v-for="m in majorList" :key="m.majorCode" :label="m.majorCode + ' ' + m.majorName" :value="m.majorCode" />
        </el-select>
        <el-select v-model="filters.political" placeholder="政治面貌" clearable style="width: 130px" @change="handleSearch" @clear="handleSearch">
          <el-option label="共青团员" value="共青团员" />
          <el-option label="中共党员" value="中共党员" />
          <el-option label="群众" value="群众" />
          <el-option label="其他" value="其他" />
        </el-select>
      </div>
      <div class="search-row">
        <el-select v-model="filters.isFresh" placeholder="是否应届" clearable style="width: 130px" @change="handleSearch" @clear="handleSearch">
          <el-option label="应届" :value="1" />
          <el-option label="往届" :value="0" />
        </el-select>
        <el-select v-model="filters.education" placeholder="学历" clearable style="width: 140px" @change="handleSearch" @clear="handleSearch">
          <el-option label="本科毕业" value="本科毕业" />
          <el-option label="本科结业" value="本科结业" />
          <el-option label="高职高专" value="高职高专" />
          <el-option label="同等学力" value="同等学力" />
          <el-option label="硕士研究生" value="硕士研究生" />
          <el-option label="其他" value="其他" />
        </el-select>
        <el-select v-model="filters.type" placeholder="报考类别" clearable style="width: 130px" @change="handleSearch" @clear="handleSearch">
          <el-option label="全日制" value="全日制" />
          <el-option label="非全日制" value="非全日制" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button type="success" @click="handleExport">导出 CSV</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-table :data="studentList" border stripe style="width: 100%">
      <el-table-column prop="examId" label="考号" width="120" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="80" />
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="political" label="政治面貌" width="100" />
      <el-table-column prop="isFresh" label="是否应届" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.isFresh ? 'success' : 'info'" size="small">
            {{ scope.row.isFresh ? '应届' : '往届' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="education" label="学历" width="100" />
      <el-table-column prop="source" label="来源" width="150" />
      <el-table-column prop="majorCode" label="专业代码" width="120" />
      <el-table-column prop="type" label="报考类别" width="120" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button type="primary" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row.examId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-bar">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </div>

    <!-- 添加/编辑考生弹窗（合并） -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="考号" prop="examId">
          <el-input v-model="form.examId" :disabled="dialogMode === 'edit'" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender">
            <el-option label="男" value="男"/>
            <el-option label="女" value="女"/>
          </el-select>
        </el-form-item>
        <el-form-item label="年龄"><el-input-number v-model="form.age" :min="0" /></el-form-item>

        <el-form-item label="政治面貌">
          <el-select v-model="form.political" placeholder="请选择政治面貌">
            <el-option label="共青团员" value="共青团员" />
            <el-option label="中共党员" value="中共党员" />
            <el-option label="群众" value="群众" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="是否应届">
          <el-select v-model="form.isFresh" placeholder="请选择">
            <el-option label="应届" :value="1" />
            <el-option label="往届" :value="0" />
          </el-select>
        </el-form-item>

        <el-form-item label="学历">
          <el-select v-model="form.education" placeholder="请选择学历">
            <el-option label="本科毕业" value="本科毕业" />
            <el-option label="本科结业" value="本科结业" />
            <el-option label="高职高专" value="高职高专" />
            <el-option label="同等学力" value="同等学力" />
            <el-option label="硕士研究生" value="硕士研究生" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="来源"><el-input v-model="form.source" /></el-form-item>

        <el-form-item label="专业代码">
          <el-select v-model="form.majorCode" placeholder="请选择专业代码" filterable>
            <el-option
              v-for="major in majorList"
              :key="major.majorCode"
              :label="major.majorCode + ' - ' + major.majorName"
              :value="major.majorCode"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="报考类别">
          <el-select v-model="form.type" placeholder="请选择报考类别">
            <el-option label="全日制" value="全日制" />
            <el-option label="非全日制" value="非全日制" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStudentList, addStudent, deleteStudent, updateStudent, getMajorList, exportStudent, studentSuggest } from '@/api'

// 表单默认值工厂函数
const createDefaultForm = () => ({
  examId: '',
  name: '',
  gender: '男',
  age: 22,
  political: '',
  isFresh: 1,
  education: '',
  source: '',
  majorCode: '',
  type: ''
})

// 表单校验规则
const rules = {
  examId: [
    { required: true, message: '请输入考号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ]
}

const studentList = ref([])
const majorList = ref([])
const filters = ref({
  keyword: '',
  political: '',
  isFresh: null,
  education: '',
  source: '',
  majorCode: '',
  type: ''
})
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框（合并添加/编辑模式）
const dialogVisible = ref(false)
const dialogMode = ref('add') // 'add' | 'edit'
const dialogTitle = computed(() => dialogMode.value === 'add' ? '添加考生' : '修改考生信息')
const formRef = ref(null)
const form = ref(createDefaultForm())

// 打开添加弹窗
const openAddDialog = () => {
  dialogMode.value = 'add'
  form.value = createDefaultForm()
  dialogVisible.value = true
}

// 打开编辑弹窗
const openEditDialog = (row) => {
  dialogMode.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

// 构建搜索参数（过滤空值）
const buildSearchParams = () => {
  const params = { page: currentPage.value, pageSize: pageSize.value }
  const f = filters.value
  if (f.keyword?.trim()) params.keyword = f.keyword.trim()
  if (f.political?.trim()) params.political = f.political.trim()
  if (f.isFresh !== null && f.isFresh !== '') params.isFresh = f.isFresh
  if (f.education?.trim()) params.education = f.education.trim()
  if (f.source?.trim()) params.source = f.source.trim()
  if (f.majorCode?.trim()) params.majorCode = f.majorCode.trim()
  if (f.type?.trim()) params.type = f.type.trim()
  return params
}

// 获取考生列表
const fetchData = async () => {
  try {
    const params = buildSearchParams()
    const res = await getStudentList(params)
    studentList.value = res.data.records || res.data
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error(error.message || '获取考生列表失败')
  }
}

const fetchSuggestions = async (queryString, cb) => {
  if (!queryString || queryString.trim().length === 0) {
    cb([])
    return
  }
  try {
    const res = await studentSuggest(queryString.trim())
    cb(res.data || [])
  } catch {
    cb([])
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const handleReset = () => {
  filters.value = {
    keyword: '',
    political: '',
    isFresh: null,
    education: '',
    source: '',
    majorCode: '',
    type: ''
  }
  currentPage.value = 1
  fetchData()
}

const handleExport = () => {
  exportStudent()
}

// 获取专业列表（用于下拉选择，不带搜索条件）
const fetchMajorList = async () => {
  try {
    // 获取全部专业用于下拉选择（pageSize 设大值）
    const res = await getMajorList(undefined, 1, 1000)
    majorList.value = res.data.records || res.data
  } catch (error) {
    ElMessage.error(error.message || '获取专业列表失败')
  }
}

// 提交表单（添加或修改）
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch {
    // 校验不通过，不提交
    return
  }
  try {
    if (dialogMode.value === 'add') {
      await addStudent(form.value)
      ElMessage.success('添加成功')
    } else {
      await updateStudent(form.value)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    fetchData()
    form.value = createDefaultForm()
  } catch (error) {
    ElMessage.error(error.message || (dialogMode.value === 'add' ? '添加失败，请检查数据完整性' : '修改失败'))
  }
}

// 删除考生
const handleDelete = async (examId) => {
  try {
    await ElMessageBox.confirm('确认删除该考生吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteStudent(examId)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    // 用户取消或API失败，API失败已在拦截器提示
  }
}

onMounted(() => {
  fetchData()
  fetchMajorList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-bar {
  margin-bottom: 16px;
}
.search-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}
.pagination-bar {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
