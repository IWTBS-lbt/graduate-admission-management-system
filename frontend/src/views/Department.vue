<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>🏛️ 院系管理</span>
        <el-button type="primary" @click="openAddDialog">+ 添加院系</el-button>
      </div>
    </template>

    <el-table :data="deptList" border stripe row-key="id">
      <el-table-column type="expand">
        <template #default="scope">
          <div class="expand-content" v-if="scope.row.majors && scope.row.majors.length > 0">
            <el-table :data="scope.row.majors" size="small" border>
              <el-table-column prop="majorCode" label="专业代码" width="140" />
              <el-table-column prop="majorName" label="专业名称" width="180" />
              <el-table-column prop="planInside" label="计划内" width="80" />
              <el-table-column prop="planOutside" label="计划外" width="80" />
            </el-table>
          </div>
          <div v-else class="empty-majors">暂无专业</div>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="院系名称" min-width="200" />
      <el-table-column prop="majorCount" label="专业数" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.majorCount > 0 ? 'success' : 'info'" size="small">
            {{ scope.row.majorCount }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button type="primary" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'add' ? '添加院系' : '编辑院系'" width="400px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="院系名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入院系名称" />
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
import { getDeptWithMajors, addDept, updateDept, deleteDept } from '@/api'

const deptList = ref([])
const dialogVisible = ref(false)
const dialogMode = ref('add')
const formRef = ref(null)
const form = ref({ id: null, name: '' })

const rules = {
  name: [{ required: true, message: '请输入院系名称', trigger: 'blur' }]
}

const fetchData = async () => {
  try {
    const res = await getDeptWithMajors()
    deptList.value = res.data
  } catch (error) {
    ElMessage.error(error.message || '获取院系列表失败')
  }
}

const openAddDialog = () => {
  dialogMode.value = 'add'
  form.value = { id: null, name: '' }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  dialogMode.value = 'edit'
  form.value = { id: row.id, name: row.name }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try { await formRef.value.validate() } catch { return }
  try {
    if (dialogMode.value === 'add') {
      await addDept(form.value)
      ElMessage.success('添加成功')
    } else {
      await updateDept(form.value)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该院系吗？')
    await deleteDept(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch { /* 取消或失败 */ }
}

onMounted(fetchData)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.expand-content {
  padding: 10px 20px;
}
.empty-majors {
  padding: 10px 20px;
  color: #909399;
  font-size: 13px;
}
</style>
