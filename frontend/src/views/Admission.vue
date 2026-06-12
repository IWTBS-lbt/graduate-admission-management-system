<template>
  <div>
    <!-- 分数线配置（按院系-专业设置独立分数线） -->
    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <span>📋 分数线配置</span>
          <div class="header-actions">
            <el-button type="primary" :loading="savingCutoffs" @click="handleSaveCutoffs">
              保存分数线
            </el-button>
            <el-button type="success" :loading="generating" @click="handleGenerate">
              生成录取名单
            </el-button>
          </div>
        </div>
      </template>
      <div class="cutoff-hint">
        💡 为每个专业设置独立的录取分数线（初试总分+复试总分），未设置的分数线不会录取。设置完分数线后需先「保存分数线」再「生成录取名单」。
      </div>
      <el-collapse v-model="activeDepts" class="dept-collapse">
        <el-collapse-item
          v-for="dept in deptWithMajors"
          :key="dept.id"
          :name="dept.id"
        >
          <template #title>
            <span class="dept-title">{{ dept.name }}</span>
            <el-tag size="small" type="info" class="dept-tag">{{ dept.majorCount }} 个专业</el-tag>
          </template>
          <el-table :data="dept.majors" size="small" border>
            <el-table-column prop="majorCode" label="专业代码" width="140" />
            <el-table-column prop="majorName" label="专业名称" width="180" />
            <el-table-column prop="planInside" label="计划内" width="80" />
            <el-table-column prop="planOutside" label="计划外" width="80" />
            <el-table-column label="录取分数线" min-width="200">
              <template #default="scope">
                <el-input-number
                  v-model="scope.row.cutoffLine"
                  :min="0"
                  :max="1000"
                  placeholder="未设置"
                  size="small"
                  controls-position="right"
                />
                <span v-if="!scope.row.cutoffLine" class="unset-hint">未设置则不录取</span>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
      <el-empty v-if="deptWithMajors.length === 0" description="暂无院系专业数据" />
    </el-card>

    <!-- 录取名单表格 -->
    <el-card class="section-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>📄 录取名单（共 {{ total }} 条）</span>
          <el-button type="primary" @click="handleExport">导出 CSV</el-button>
        </div>
      </template>

      <el-table :data="admissionList" border stripe v-loading="loading" :span-method="spanMethod">
        <el-table-column prop="department" label="录取系别" width="140" />
        <el-table-column prop="majorName" label="报考专业" width="160" />
        <el-table-column prop="examId" label="考号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="firstTotal" label="初试总分" width="100" />
        <el-table-column prop="secondTotal" label="复试总分" width="100" />
        <el-table-column prop="totalScore" label="综合总分" width="110">
          <template #default="scope">
            <el-tag type="warning">{{ scope.row.totalScore }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isAdmitted" label="录取状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isAdmitted ? 'success' : 'info'">
              {{ scope.row.isAdmitted ? '已录取' : '未录取' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { generateAdmission, getAdmissionDetail, exportAdmission, getDeptWithMajors, updateMajorCutoffs } from '@/api'
import { notifyAdmissionGenerated } from '@/utils/eventBus'

const deptWithMajors = ref([])
const activeDepts = ref([])
const savingCutoffs = ref(false)
const admissionList = ref([])
const loading = ref(false)
const generating = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 加载院系-专业树（用于分数线配置）
const fetchCutoffs = async () => {
  try {
    const res = await getDeptWithMajors()
    deptWithMajors.value = res.data || []
    // 默认全部展开
    activeDepts.value = deptWithMajors.value.map(d => d.id)
  } catch (error) {
    console.error('加载院系专业数据失败:', error)
  }
}

// 保存所有专业分数线
const handleSaveCutoffs = async () => {
  savingCutoffs.value = true
  try {
    const cutoffData = []
    for (const dept of deptWithMajors.value) {
      for (const m of dept.majors) {
        cutoffData.push({
          majorCode: m.majorCode,
          cutoffLine: m.cutoffLine
        })
      }
    }
    await updateMajorCutoffs(cutoffData)
    ElMessage.success('分数线保存成功')
  } catch (error) {
    ElMessage.error('保存失败: ' + (error.message || ''))
  } finally {
    savingCutoffs.value = false
  }
}

// 计算院系列合并行数
const deptSpans = computed(() => {
  const spans = []
  let i = 0
  const list = admissionList.value
  while (i < list.length) {
    const dept = list[i].department
    let j = i + 1
    while (j < list.length && list[j].department === dept) j++
    const count = j - i
    for (let k = 0; k < count; k++) spans.push(k === 0 ? count : 0)
    i = j
  }
  return spans
})

// 计算专业列合并行数（在同一院系内合并相同专业）
const majorSpans = computed(() => {
  const spans = []
  let i = 0
  const list = admissionList.value
  while (i < list.length) {
    const dept = list[i].department
    const major = list[i].majorName
    let j = i + 1
    while (j < list.length && list[j].department === dept && list[j].majorName === major) j++
    const count = j - i
    for (let k = 0; k < count; k++) spans.push(k === 0 ? count : 0)
    i = j
  }
  return spans
})

const spanMethod = ({ rowIndex, columnIndex }) => {
  // 录取系别列（第0列）
  if (columnIndex === 0) {
    const s = deptSpans.value[rowIndex]
    return s ? { rowspan: s, colspan: 1 } : { rowspan: 0, colspan: 0 }
  }
  // 报考专业列（第1列）
  if (columnIndex === 1) {
    const s = majorSpans.value[rowIndex]
    return s ? { rowspan: s, colspan: 1 } : { rowspan: 0, colspan: 0 }
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAdmissionDetail(currentPage.value, pageSize.value)
    admissionList.value = res.data.records || res.data
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取录取名单失败:', error)
  } finally {
    loading.value = false
  }
}

const handleGenerate = async () => {
  generating.value = true
  try {
    await generateAdmission()
    ElMessage.success('录取名单生成成功')
    notifyAdmissionGenerated()
    currentPage.value = 1
    await fetchData()
  } catch (error) {
    ElMessage.error(error.message || '生成录取名单失败')
  } finally {
    generating.value = false
  }
}

const handleExport = () => {
  exportAdmission()
}

onMounted(() => {
  fetchCutoffs()
  fetchData()
})
</script>

<style scoped>
.section-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.cutoff-hint {
  color: #909399;
  font-size: 13px;
  margin-bottom: 12px;
  line-height: 1.6;
}

.dept-collapse {
  --el-collapse-header-height: 44px;
}

.dept-title {
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.dept-tag {
  margin-left: 10px;
}

.unset-hint {
  margin-left: 8px;
  color: #c0c4cc;
  font-size: 12px;
}

.pagination-bar {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
