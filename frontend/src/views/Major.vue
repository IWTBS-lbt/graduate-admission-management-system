<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>📚 专业管理</span>
        <el-button type="primary" @click="openAddDialog">+ 添加专业</el-button>
      </div>
    </template>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-autocomplete
        v-model="searchKeyword"
        :fetch-suggestions="fetchSuggestions"
        placeholder="搜索专业代码或名称..."
        clearable
        style="width: 300px"
        @select="handleSearch"
        @clear="handleSearch"
        @keyup.enter="handleSearch"
      />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <el-table :data="majorList" border stripe>
      <el-table-column prop="majorCode" label="专业代码" width="120" />
      <el-table-column prop="majorName" label="专业名称" width="160" />
      <el-table-column prop="department" label="所属院系" width="150">
        <template #default="scope">
          <el-tag type="primary" size="small">{{ scope.row.department || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="planInside" label="计划内" width="80" />
      <el-table-column prop="planOutside" label="计划外" width="80" />
      <el-table-column prop="cutoffLine" label="录取线" width="90">
        <template #default="scope">
          <span v-if="scope.row.cutoffLine">{{ scope.row.cutoffLine }}</span>
          <span v-else style="color: #c0c4cc">未设置</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button type="primary" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row.majorCode)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'add' ? '添加专业' : '编辑专业'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="专业代码" prop="majorCode">
          <el-input v-model="form.majorCode" :disabled="dialogMode === 'edit'" />
        </el-form-item>
        <el-form-item label="专业名称" prop="majorName">
          <el-input v-model="form.majorName" />
        </el-form-item>
        <el-form-item label="所属院系">
          <el-select v-model="form.department" placeholder="请选择院系" filterable clearable>
            <el-option
              v-for="d in deptOptions"
              :key="d.id"
              :label="d.name"
              :value="d.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="计划内">
          <el-input-number v-model="form.planInside" :min="0" />
        </el-form-item>
        <el-form-item label="计划外">
          <el-input-number v-model="form.planOutside" :min="0" />
        </el-form-item>
        <el-form-item label="录取线">
          <el-input-number v-model="form.cutoffLine" :min="0" :max="1000" placeholder="未设置" />
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMajorList, addMajor, deleteMajor, updateMajor, majorSuggest, getDeptList } from '@/api'

const majorList = ref([])
const dialogVisible = ref(false)
const dialogMode = ref('add') // 'add' | 'edit'
const formRef = ref(null)
const form = ref({ majorCode: '', majorName: '', department: '', planInside: 0, planOutside: 0, cutoffLine: null })
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表单校验规则
const rules = {
  majorCode: [
    { required: true, message: '请输入专业代码', trigger: 'blur' }
  ],
  majorName: [
    { required: true, message: '请输入专业名称', trigger: 'blur' }
  ]
}

const openAddDialog = () => {
  dialogMode.value = 'add'
  form.value = { majorCode: '', majorName: '', department: '', planInside: 0, planOutside: 0, cutoffLine: null }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  dialogMode.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

const fetchData = async () => {
  try {
    const keyword = searchKeyword.value?.trim() || undefined
    const res = await getMajorList(keyword, currentPage.value, pageSize.value)
    // 后端分页返回 Page 对象
    majorList.value = res.data.records || res.data
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error(error.message || '获取专业列表失败')
  }
}

const deptOptions = ref([])

const fetchDeptOptions = async () => {
  try {
    const res = await getDeptList()
    deptOptions.value = res.data || []
  } catch { /* ignore */ }
}

const fetchSuggestions = async (queryString, cb) => {
  if (!queryString || queryString.trim().length === 0) {
    cb([])
    return
  }
  try {
    const res = await majorSuggest(queryString.trim())
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
  searchKeyword.value = ''
  currentPage.value = 1
  fetchData()
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  try {
    if (dialogMode.value === 'add') {
      await addMajor(form.value)
      ElMessage.success('添加成功')
    } else {
      await updateMajor(form.value)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(error.message || (dialogMode.value === 'add' ? '添加专业失败' : '修改专业失败'))
  }
}

const handleDelete = async (code) => {
  try {
    await ElMessageBox.confirm('确认删除该专业吗？')
    await deleteMajor(code)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    // 用户取消或API失败，API失败已在拦截器提示
  }
}

onMounted(() => {
  fetchData()
  fetchDeptOptions()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
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
