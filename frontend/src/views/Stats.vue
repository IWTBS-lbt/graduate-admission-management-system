<template>
  <div>
    <!-- 院系选择器 -->
    <el-card class="row-section">
      <div class="dept-selector">
        <span class="selector-label">📊 院系筛选：</span>
        <el-select v-model="selectedDept" placeholder="选择院系" @change="onDeptChange" style="width: 200px">
          <el-option label="全部院系" value="all" />
          <el-option v-for="d in deptList" :key="d" :label="d" :value="d" />
        </el-select>
      </div>
    </el-card>

    <!-- ====== 院系各科成绩对比表 ====== -->
    <el-card class="row-section">
      <template #header><span>📋 各院系成绩对比（政治/外语满分100，专业基础满分150）</span></template>
      <el-table :data="deptSubjectData" border stripe size="small">
        <el-table-column prop="department" label="院系" width="150" fixed />
        <el-table-column prop="totalCount" label="考生数" width="80" align="center" />
        <el-table-column label="政治均分" width="90" align="center">
          <template #default="s">{{ s.row.politicsAvg != null ? s.row.politicsAvg.toFixed(1) : '-' }}</template>
        </el-table-column>
        <el-table-column label="政治及格" width="110" align="center">
          <template #default="s">
            <span :class="passRateClass(s.row.politicsPass, s.row.politicsFail)">
              {{ s.row.politicsPass }}/{{ s.row.totalCount }} ({{ passRate(s.row.politicsPass, s.row.politicsFail) }})
            </span>
          </template>
        </el-table-column>
        <el-table-column label="外语均分" width="90" align="center">
          <template #default="s">{{ s.row.englishAvg != null ? s.row.englishAvg.toFixed(1) : '-' }}</template>
        </el-table-column>
        <el-table-column label="外语及格" width="110" align="center">
          <template #default="s">
            <span :class="passRateClass(s.row.englishPass, s.row.englishFail)">
              {{ s.row.englishPass }}/{{ s.row.totalCount }} ({{ passRate(s.row.englishPass, s.row.englishFail) }})
            </span>
          </template>
        </el-table-column>
        <el-table-column label="专业基础均分" width="110" align="center">
          <template #default="s">{{ s.row.professionalAvg != null ? s.row.professionalAvg.toFixed(1) : '-' }}</template>
        </el-table-column>
        <el-table-column label="专业基础及格" width="120" align="center">
          <template #default="s">
            <span :class="passRateClass(s.row.professionalPass, s.row.professionalFail)">
              {{ s.row.professionalPass }}/{{ s.row.totalCount }} ({{ passRate(s.row.professionalPass, s.row.professionalFail) }})
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- ====== 图表行1：院系各科平均分 + 分数段分布 ====== -->
    <el-row :gutter="20" class="row-section">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>📊 各院系平均分对比</span></template>
          <div id="chart-dept-bar" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>📈 {{ selectedDept === 'all' ? '全部' : selectedDept }} — 分数段分布</span></template>
          <div id="chart-segment-pie" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- ====== 图表行2：招生计划vs实际 + 录取专业分布 ====== -->
    <el-row :gutter="20" class="row-section">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>📋 招生计划 vs 实际录取</span></template>
          <div id="chart-plan-bar" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>🎓 录取专业分布</span></template>
          <div id="chart-major-pie" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- ====== 录取生源分析表格 ====== -->
    <el-card class="row-section">
      <template #header><span>📊 录取生源分析</span></template>
      <el-row :gutter="20">
        <el-col :span="8">
          <h3>🎂 年龄分布</h3>
          <el-table :data="ageData" size="small" border stripe>
            <el-table-column prop="key" label="年龄" />
            <el-table-column prop="count" label="人数" />
          </el-table>
        </el-col>
        <el-col :span="8">
          <h3>🏫 来源分布</h3>
          <el-table :data="sourceData" size="small" border stripe>
            <el-table-column prop="key" label="来源" />
            <el-table-column prop="count" label="人数" />
          </el-table>
        </el-col>
        <el-col :span="8">
          <h3>📚 专业分布</h3>
          <el-table :data="majorData" size="small" border stripe>
            <el-table-column prop="key" label="专业" />
            <el-table-column prop="count" label="人数" />
          </el-table>
        </el-col>
      </el-row>
    </el-card>

    <!-- ====== 计划招生 vs 实际录取表格 ====== -->
    <el-card class="row-section">
      <template #header><span>📈 计划招生 vs 实际录取</span></template>
      <el-table :data="planVsActualData" border stripe>
        <el-table-column prop="majorName" label="专业" />
        <el-table-column prop="planTotal" label="计划招生（总）" />
        <el-table-column prop="actual" label="实际录取" />
        <el-table-column label="完成率" width="180">
          <template #default="scope">
            <template v-if="scope.row.planTotal > 0">
              <el-progress
                :percentage="Math.round(scope.row.actual / scope.row.planTotal * 100)"
                :status="scope.row.actual >= scope.row.planTotal ? 'success' : 'warning'"
              />
            </template>
            <span v-else style="color: #c0c4cc">-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- ====== 分数段统计表格 ====== -->
    <el-card class="row-section">
      <template #header><span>📈 分数段统计（{{ selectedDept === 'all' ? '全部院系' : selectedDept }}，满分350）</span></template>
      <el-table :data="filteredSegmentData" border stripe>
        <el-table-column prop="segment" label="分数段" />
        <el-table-column prop="count" label="人数" />
        <el-table-column label="占比">
          <template #default="scope">
            {{ filteredSegmentTotal > 0 ? (scope.row.count / filteredSegmentTotal * 100).toFixed(1) : 0 }}%
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import * as echarts from 'echarts'
import {
  getAdmissionStats, getPlanVsActualStats, getScoreSegmentStats,
  getDeptSubjectStats, getDeptSegmentStats
} from '@/api'
import { admissionVersion } from '@/utils/eventBus'

const selectedDept = ref('all')
const deptList = ref([])
const deptSubjectData = ref([])
const deptSegmentData = ref([])
const planVsActualData = ref([])
const ageData = ref([])
const sourceData = ref([])
const majorData = ref([])
const segmentData = ref([])

// 院系过滤后的分数段数据
const filteredSegmentData = computed(() => {
  if (selectedDept.value === 'all') return segmentData.value
  return deptSegmentData.value.filter(d => d.department === selectedDept.value)
})
const filteredSegmentTotal = computed(() => {
  return filteredSegmentData.value.reduce((sum, s) => sum + (s.count || 0), 0)
})

// 各院系及格率辅助函数
const passRate = (pass, fail) => {
  const p = pass || 0; const f = fail || 0; const t = p + f
  if (t === 0) return '-'
  return (p / t * 100).toFixed(1) + '%'
}
const passRateClass = (pass, fail) => {
  const p = pass || 0; const f = fail || 0; const t = p + f
  if (t === 0) return ''
  return (p / t) >= 0.6 ? 'pass-good' : 'pass-bad'
}

// 图表实例
let chartInstances = {}

const initChartById = (id, option) => {
  setTimeout(() => {
    const dom = document.getElementById(id)
    if (!dom) return
    if (chartInstances[id]) chartInstances[id].dispose()
    const chart = echarts.init(dom)
    chart.setOption(option)
    chartInstances[id] = chart
  }, 200)
}

const renderCharts = () => {
  // 1. 各院系平均分分组柱状图
  if (deptSubjectData.value.length > 0) {
    const depts = deptSubjectData.value.map(d => d.department)
    initChartById('chart-dept-bar', {
      tooltip: { trigger: 'axis' },
      legend: { data: ['政治', '外语', '专业基础'], bottom: 0 },
      grid: { left: '3%', right: '4%', bottom: '12%', containLabel: true },
      xAxis: { type: 'category', data: depts, axisLabel: { rotate: 30 } },
      yAxis: { type: 'value', name: '平均分' },
      series: [
        { name: '政治', type: 'bar', data: deptSubjectData.value.map(d => d.politicsAvg ? +d.politicsAvg.toFixed(1) : 0), barWidth: '22%' },
        { name: '外语', type: 'bar', data: deptSubjectData.value.map(d => d.englishAvg ? +d.englishAvg.toFixed(1) : 0), barWidth: '22%' },
        { name: '专业基础', type: 'bar', data: deptSubjectData.value.map(d => d.professionalAvg ? +d.professionalAvg.toFixed(1) : 0), barWidth: '22%' }
      ]
    })
  }

  // 2. 分数段饼图（按院系过滤）
  let segData = []
  if (selectedDept.value === 'all') {
    segData = segmentData.value.map(s => ({ name: s.segment, value: s.count }))
  } else {
    segData = deptSegmentData.value
      .filter(d => d.department === selectedDept.value)
      .map(s => ({ name: s.segment, value: s.count }))
  }
  if (segData.length > 0) {
    initChartById('chart-segment-pie', {
      tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie', radius: ['40%', '70%'], center: ['50%', '45%'],
        label: { formatter: '{b}\n{d}%' },
        data: segData
      }]
    })
  }

  // 3. 计划 vs 实际柱状图（不变）
  if (planVsActualData.value.length > 0) {
    initChartById('chart-plan-bar', {
      tooltip: { trigger: 'axis' },
      legend: { data: ['计划招生', '实际录取'], bottom: 0 },
      grid: { left: '3%', right: '4%', bottom: '12%', containLabel: true },
      xAxis: { type: 'category', data: planVsActualData.value.map(d => d.majorName), axisLabel: { rotate: 30 } },
      yAxis: { type: 'value', name: '人数' },
      series: [
        { name: '计划招生', type: 'bar', data: planVsActualData.value.map(d => d.planTotal), barWidth: '35%' },
        { name: '实际录取', type: 'bar', data: planVsActualData.value.map(d => d.actual), barWidth: '35%' }
      ]
    })
  }

  // 4. 录取专业分布饼图（不变）
  if (majorData.value.length > 0) {
    initChartById('chart-major-pie', {
      tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
      legend: { type: 'scroll', bottom: 0 },
      series: [{
        type: 'pie', radius: '65%', center: ['50%', '45%'],
        data: majorData.value.map(d => ({ name: d.key, value: d.count }))
      }]
    })
  }
}

// 加载所有数据
const loadAll = async () => {
  try { segmentData.value = (await getScoreSegmentStats()).data } catch {}
  try { planVsActualData.value = (await getPlanVsActualStats()).data.list || [] } catch {}
  try {
    const res = await getAdmissionStats()
    ageData.value = res.data.age || []
    sourceData.value = res.data.source || []
    majorData.value = res.data.major || []
  } catch {}
  try {
    const res = await getDeptSubjectStats()
    deptSubjectData.value = res.data || []
    deptList.value = deptSubjectData.value.map(d => d.department)
  } catch {}
  try {
    const res = await getDeptSegmentStats()
    deptSegmentData.value = res.data || []
  } catch {}
  renderCharts()
}

const onDeptChange = () => {
  renderCharts()
}

onMounted(loadAll)

watch(admissionVersion, () => loadAll())

window.addEventListener('resize', () => {
  Object.values(chartInstances).forEach(c => c.resize())
})
</script>

<style scoped>
.row-section { margin-bottom: 20px; }
h3 { margin: 0 0 12px; font-size: 15px; color: #303133; }
.chart-box { width: 100%; height: 350px; }
.dept-selector {
  display: flex;
  align-items: center;
  gap: 10px;
}
.selector-label {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}
.pass-good { color: #67c23a; font-weight: 600; }
.pass-bad { color: #f56c6c; font-weight: 600; }
</style>
