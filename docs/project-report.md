# 研究生招生信息管理系统 — 项目报告

> 版本：1.0  
> 日期：2026-06-12

---

## 目录

1. [项目概述](#一项目概述)
2. [技术栈](#二技术栈)
3. [系统架构](#三系统架构)
4. [功能模块详解](#四功能模块详解)
   - [4.1 登录认证](#41-登录认证)
   - [4.2 院系管理](#42-院系管理)
   - [4.3 专业管理](#43-专业管理)
   - [4.4 考生档案管理](#44-考生档案管理)
   - [4.5 初试成绩管理](#45-初试成绩管理)
   - [4.6 复试成绩管理](#46-复试成绩管理)
   - [4.7 录取管理](#47-录取管理)
   - [4.8 数据统计](#48-数据统计)
   - [4.9 考生门户](#49-考生门户)
5. [数据库设计](#五数据库设计)
6. [API 接口文档](#六api-接口文档)
7. [前端路由与页面](#七前端路由与页面)
8. [关键设计决策](#八关键设计决策)
9. [项目文件结构](#九项目文件结构)
10. [部署与运行](#十部署与运行)

---

## 一、项目概述

**研究生招生信息管理系统** 是一个基于 Spring Boot + Vue 3 的全栈 Web 应用，管理研究生招生从考生报名、初试成绩录入、复试筛选、复试成绩录入到最终录取的完整业务流程。

### 核心业务流程

```
考生报名 ──► 初试成绩录入 ──► 按分数线筛选复试名单 ──► 复试成绩录入
                                                              │
                                                              ▼
                                           综合总分 = 初试总分 + 复试总分
                                                              │
                                          ┌───────────────────┘
                                          ▼
                                    各专业独立分数线判断 ──► 生成录取名单
                                          │
                                          ▼
                                    统计图表自动刷新
```

### 系统角色

| 角色 | 功能 |
|---|---|
| 管理员（admin） | 全部管理功能：院系/专业/考生/成绩/录取 CRUD、数据统计、CSV 导出 |
| 考生（公开访问） | 通过考生门户自助报名、查询个人成绩与录取状态 |

---

## 二、技术栈

### 后端

| 技术 | 版本 | 用途 |
|---|---|---|
| Spring Boot | 2.7.15 | 应用框架 |
| Java | 11 | 开发语言 |
| MyBatis-Plus | 3.5.3.1 | ORM（注解驱动，无 XML） |
| MySQL Connector-J | 8.0.33 | 数据库驱动 |
| JJWT | 0.11.5 | JWT Token 生成与验证 |
| Spring Security Crypto | — | BCrypt 密码加密 |
| Lombok | 1.18.24 | 减少样板代码 |
| Maven | — | 项目构建 |

### 前端

| 技术 | 版本 | 用途 |
|---|---|---|
| Vue | 3.5.34 | 前端框架（Composition API） |
| Vite | 8.0.12 | 构建工具 |
| Vue Router | 4.6.4 | 前端路由 |
| Element Plus | 2.14.1 | UI 组件库（表格、表单、弹窗等） |
| ECharts | 6.1.0 | 数据可视化图表 |
| Axios | 1.17.0 | HTTP 请求库 |

### 数据库

| 配置项 | 值 |
|---|---|
| 数据库 | MySQL 8.0 — `admission_db` |
| 存储引擎 | InnoDB |
| 字符集 | utf8mb4 |

---

## 三、系统架构

```
┌─────────────────────────────────────────────────────────┐
│                     浏览器 (Browser)                     │
├─────────────────────────────────────────────────────────┤
│  Vue 3 SPA                                              │
│  ├── Element Plus (UI 组件)                              │
│  ├── ECharts (图表)                                      │
│  ├── Vue Router (路由 + 导航守卫)                         │
│  ├── Axios (HTTP 拦截器：JWT Token 注入 + 错误处理)        │
│  └── EventBus (跨组件通信：录取名单生成 → 统计刷新)         │
└────────────┬────────────────────────────────────────────┘
             │  HTTP (REST API)
             │  Content-Type: application/json
             │  Authorization: Bearer <JWT>
┌────────────┴────────────────────────────────────────────┐
│  Spring Boot (端口 8080)                                 │
│  ├── AuthInterceptor (JWT 验证，/portal 和 /login 放行)    │
│  ├── Controller 层 (9 个控制器，33 个端点)                 │
│  ├── Service 层 (业务逻辑 + @Transactional 事务)           │
│  ├── Mapper 层 (MyBatis-Plus BaseMapper + @Select 注解)   │
│  └── VO/DTO 层 (视图对象，封装多表 JOIN 结果)              │
└────────────┬────────────────────────────────────────────┘
             │  JDBC
┌────────────┴────────────────────────────────────────────┐
│  MySQL 8.0 — admission_db                               │
│  ├── 7 张业务表 (InnoDB, utf8mb4)                        │
│  ├── 4 个外键约束                                        │
│  ├── 2 个计算列 (GENERATED ALWAYS AS STORED)             │
│  └── 0 视图 / 0 触发器 / 0 存储过程（逻辑在 Java 层）      │
└─────────────────────────────────────────────────────────┘
```

### 项目内部架构（Java 包结构）

| 包 | 文件数 | 职责 |
|---|---|---|
| `controller` | 9 | REST 接口层，接收请求、参数校验、调用 Service、返回 Result |
| `service` + `impl` | 8+8 | 业务逻辑层，事务管理 |
| `mapper` | 7 | 数据访问层，MyBatis-Plus BaseMapper + 自定义 SQL |
| `entity` | 7 | 数据库实体（一一映射表） |
| `vo` | 9 | 视图对象（多表 JOIN 结果、统计聚合结果） |
| `dto` | 1 | 数据传输对象（登录请求） |
| `config` | 4 | CORS 跨域、MyBatis-Plus 分页、JWT 工具、全局异常处理 |
| `interceptor` | 1 | JWT 认证拦截器 |
| `utils` | 1 | CSV 文件导出工具 |
| `common` | 1 | 统一响应格式 `Result` |

---

## 四、功能模块详解

### 4.1 登录认证

**页面**：`/login`  
**后端**：`POST /user/login`

#### 功能描述
- 管理员通过用户名密码登录系统
- 登录成功后返回 JWT Token（24 小时有效），存入 localStorage
- 前端路由守卫：未登录用户跳转到登录页
- `/portal`（考生门户）和 `/login` 无需认证

#### 认证流程
```
用户输入用户名密码
  → POST /user/login
  → UserServiceImpl：BCrypt 验证密码（首次明文密码自动升级为 BCrypt）
  → JwtUtils.generateToken(username, role)
  → 返回 { token, username, role }
  → 前端存入 localStorage
  → Axios 拦截器：每次请求自动附加 Authorization: Bearer <token>
```

#### 技术细节
- JWT 密钥：`admission-system-jwt-secret-key-2026`
- 加密算法：HS256
- Token 结构：`{ sub: username, role: role, exp: 24h }`
- 密码存储：BCrypt（Spring Security Crypto）

---

### 4.2 院系管理

**页面**：`/department`  
**后端**：`/department/*`（5 个端点）

#### 功能描述
- 7 大预设院系：计算机学院、软件学院、人工智能学院、信息与通信学院、自动化学院、数学与统计学院、管理学院
- **展开行功能**：点击院系行左侧箭头，展开显示该院系下所有专业列表（专业代码、名称、计划内、计划外）
- 增删改查院系名称

#### 技术实现
- **前端**：`el-table` 的 `type="expand"` 展开行，嵌套子表格展示专业
- **后端**：`GET /department/with-majors` 返回 `DepartmentVO` 列表，每个 VO 包含 `List<Major>` 子列表
- **关联方式**：`Department.name` ↔ `Major.department` 按名称匹配（非外键）

#### DepartmentVO 结构
```java
{
  id: 1,
  name: "计算机学院",
  majorCount: 3,
  majors: [
    { majorCode: "081200", majorName: "计算机科学与技术", planInside: 15, planOutside: 5 },
    { majorCode: "083900", majorName: "网络空间安全", planInside: 8, planOutside: 2 },
    ...
  ]
}
```

---

### 4.3 专业管理

**页面**：`/major`  
**后端**：`/major/*`（6 个端点）

#### 功能描述
- 专业代码（主键）、专业名称、所属院系（下拉选择）、计划内/外招生数、**录取分数线**
- 搜索自动补全：输入时调用 suggest API 实时提示（匹配专业代码/名称/院系）
- 批量保存各专业录取分数线（`PUT /major/batch-cutoff`）
- 删除保护：该专业下有考生时禁止删除

#### 表格列
| 专业代码 | 专业名称 | 所属院系 | 计划内 | 计划外 | 录取线 | 操作 |
|---|---|---|---|---|---|---|
| 081200 | 计算机科学与技术 | 计算机学院 | 15 | 5 | 380 | 编辑 删除 |

#### 录取分数线（cutoff_line）
- 类型：`INT DEFAULT NULL`
- NULL 含义：该专业尚未设置分数线，生成录取时该专业考生不会被录取
- 设置方式：专业管理页单个设置，或录取管理页批量设置
- 这是本项目区别于"全局统一分数线"的关键设计

---

### 4.4 考生档案管理

**页面**：`/student`  
**后端**：`/student/*`（6 个端点）

#### 功能描述
管理所有报考考生的基本信息档案。

#### 表单字段
| 字段 | 类型 | 说明 |
|---|---|---|
| 考号 | VARCHAR(32) PK | 唯一标识 |
| 姓名 | VARCHAR(32) | 必填 |
| 性别 | 下拉（男/女） | — |
| 年龄 | 数字 | — |
| 政治面貌 | 下拉（中共党员/共青团员/群众/其他） | — |
| 是否应届 | 下拉（应届=1/往届=0） | — |
| 学历 | 下拉（本科毕业/本科结业/高职高专/同等学力/硕士研究生/其他） | — |
| 考生来源 | 文本框 | 毕业院校名称 |
| 报考专业 | 下拉（从 major 表加载） | FK → major |
| 报考类别 | 下拉（全日制/非全日制） | — |

#### 高级搜索（7 维度组合）

搜索栏分两行布局：

```
[考号/姓名 自动补全] [考生来源 输入框] [专业代码 下拉] [政治面貌 下拉]
[是否应届 下拉] [学历 下拉] [报考类别 下拉] [搜索] [重置] [导出CSV]
```

- 考号/姓名 → 模糊匹配（OR）
- 其余字段 → 精确匹配
- 所有条件可自由组合（AND 逻辑）
- 下拉选择后**立即触发搜索**
- 重置一键清空 7 个筛选条件

#### 后端查询逻辑
```java
// StudentServiceImpl.search()
LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
// keyword: 考号 OR 姓名模糊匹配
if (keyword != null) wrapper.and(w -> w.like(examId, keyword).or().like(name, keyword));
// 其余字段精确匹配
if (political != null) wrapper.eq(political, value);
if (isFresh != null) wrapper.eq(isFresh, value);
if (education != null) wrapper.eq(education, value);
if (source != null) wrapper.like(source, value);     // 来源为模糊
if (majorCode != null) wrapper.eq(majorCode, value);
if (type != null) wrapper.eq(type, value);
```

#### CSV 导出
- 导出全部考生数据（考号、姓名、性别、年龄、政治面貌、是否应届、学历、来源、专业代码、报考类别）
- UTF-8 BOM 头确保 Excel 正确显示中文

---

### 4.5 初试成绩管理

**页面**：`/first-score`  
**后端**：`/first_score/*`（5 个端点）

#### 功能描述
录入和管理考生的初试（全国统考）成绩。

#### 成绩字段

| 科目 | 满分 | 及格线 | 字段 |
|---|---|---|---|
| 政治 | 100 | ≥ 60 | `politics` |
| 外语 | 100 | ≥ 60 | `english` |
| 专业基础 | **150** | **≥ 90**（60%） | `professional_base` |
| **总分** | **350** | — | `total`（计算列） |

#### 核心功能

**成绩录入**：
- 输入考号自动提示已有考生（调用 `studentSuggest` API）
- 支持新增和修改（`saveOrUpdate`）

**复试名单筛选**：
- 设置政治线、外语线、专业基础线、总分线
- 查询满足全部条件的考生（四科都达标才算通过）
- SQL：`WHERE politics >= ? AND english >= ? AND professional_base >= ? AND total >= ?`

**成绩列表**：
- 搜索：支持按考号或**考生姓名**搜索
  - 后端先按姓名查 `student` 表获取考号列表，再在 `first_score` 表中查询
- CSV 导出

#### 计算列
```sql
total INT GENERATED ALWAYS AS (politics + english + professional_base) STORED
```
数据库自动维护，Java 实体标记 `insertStrategy = NEVER, updateStrategy = NEVER`。

---

### 4.6 复试成绩管理

**页面**：`/second-score`  
**后端**：`/second_score/*`（4 个端点）

#### 功能描述
录入和管理通过初试筛选的考生的复试成绩。

#### 成绩字段

| 科目 | 满分 | 字段 |
|---|---|---|
| 复试专业科目 | 100 | `professional` |
| 面试 | 100 | `interview` |
| 上机 | 100 | `computer_test` |
| **总分** | **300** | `total`（计算列） |

#### 核心功能
- 成绩录入：输入考号自动提示已有考生
- 按考号或姓名搜索
- CSV 导出
- 总分计算列（与初试相同的机制）

---

### 4.7 录取管理

**页面**：`/admission`  
**后端**：`/admission/*`（4 个端点）

#### 功能描述
这是系统的核心模块，管理从分数线配置到录取名单生成的全流程。

#### 页面布局

**上半部分 — 分数线配置**：
```
┌─────────────────────────────────────────────┐
│ 📋 分数线配置            [保存分数线] [生成录取名单] │
├─────────────────────────────────────────────┤
│ 💡 为每个专业设置独立的录取分数线...              │
├─────────────────────────────────────────────┤
│ ▼ 计算机学院 (3 个专业)                       │
│   ┌──────────┬──────────┬──────┬──────┬──────────┐ │
│   │ 专业代码  │ 专业名称  │ 计划内│ 计划外│ 录取分数线 │ │
│   ├──────────┼──────────┼──────┼──────┼──────────┤ │
│   │ 081200   │ 计算机科学 │  15  │   5  │ 380  ±   │ │
│   │ 083900   │ 网络空间安全│   8  │   2  │ 360  ±   │ │
│   │ CS03     │ 网络工程   │   8  │   2  │ 360  ±   │ │
│   └──────────┴──────────┴──────┴──────┴──────────┘ │
│ ▶ 软件学院 (1 个专业)                          │
│ ▶ 人工智能学院 (1 个专业)                        │
│ ...                                          │
└─────────────────────────────────────────────┘
```

**下半部分 — 录取名单表格**：
- 按**院系 → 专业 → 综合总分降序**排列
- 院系列和专业列做单元格合并（`span-method`）
- 列：录取系别 | 报考专业 | 考号 | 姓名 | 初试总分 | 复试总分 | 综合总分 | 录取状态

#### 录取生成逻辑

```java
// AdmissionServiceImpl.generateAdmissionList()
1. DELETE FROM admission  // 清空旧名单
2. 加载所有 first_score
3. 批量加载 second_score，构建 examId → score 映射
4. 批量加载 student，构建 examId → majorCode 映射
5. 批量加载 major，构建两个 Map：
   - majorCode → cutoffLine  (分数线)
   - majorCode → majorName   (院系名称)
6. 遍历每位考生：
   - 查找该考生专业的 cutoffLine
   - 如果 cutoffLine == null → 跳过（该专业未设置分数线）
   - 综合总分 = first_total + second_total
   - 如果 综合总分 ≥ cutoffLine → 录取，写入 admission 表
7. batchSave(admissionList)  // 批量插入
```

#### 关键设计
- **分数线与生成分离**：先保存分数线配置到 `major` 表，再点击生成按钮
- **每专业独立分数线**：不同专业可以有不同的录取标准（如计算机 380、数学 340）
- **NULL = 不录取**：未设置分数线的专业自动跳过，避免误录
- **事件通知**：生成成功后通过 EventBus 通知统计页面自动刷新

---

### 4.8 数据统计

**页面**：`/stats`  
**后端**：`/stats/*`（6 个端点）

#### 功能描述
提供多维度数据可视化，支持按院系过滤。

#### 页面布局

```
┌─────────────────────────────────────────────┐
│ 📊 院系筛选：[全部院系 ▾]                      │  ← 院系选择器
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│ 📋 各院系成绩对比（表格）                       │  ← 7 个院系横向对比
│ 院系 │人数│政治均分│政治及格│外语均分│外语及格│专业均分│专业及格│
│ 计算机│13 │ 76.4  │16/17 94%│ 72.1  │16/17 94%│126.4  │17/17 100%│
│ 软件  │ 9 │ 71.3  │ 8/9 89%│ 69.2  │ 7/9 78%│113.8  │ 8/9 89%│
│ ...   │                                              │
└─────────────────────────────────────────────┘

┌──────────────────┬──────────────────────────┐
│ 📊 各院系平均分对比 │ 📈 分数段分布（饼图，可过滤）  │
│ (分组柱状图)       │                          │
└──────────────────┴──────────────────────────┘

┌──────────────────┬──────────────────────────┐
│ 📋 计划vs实际录取  │ 🎓 录取专业分布（饼图）      │
│ (对比柱状图)       │                          │
└──────────────────┴──────────────────────────┘

📊 录取生源分析：年龄分布 | 来源分布 | 专业分布（三栏表格）
📈 计划招生 vs 实际录取（进度条表格）
📈 分数段统计（0-200/200-250/250-300/300-350，院系可过滤）
```

#### 统计查询实现

**院系成绩统计** — `GET /stats/dept-subject`：
```sql
SELECT 
    m.department,
    COUNT(*) AS totalCount,
    ROUND(AVG(f.politics), 2) AS politicsAvg,
    SUM(CASE WHEN f.politics >= 60 THEN 1 ELSE 0 END) AS politicsPass,
    SUM(CASE WHEN f.politics < 60 THEN 1 ELSE 0 END) AS politicsFail,
    -- 外语、专业基础同理...
FROM first_score f
INNER JOIN student s ON f.exam_id = s.exam_id
LEFT JOIN major m ON s.major_code = m.major_code
GROUP BY m.department
```

**院系分数段** — `GET /stats/dept-segment`：
```sql
SELECT 
    m.department,
    CASE WHEN f.total < 200 THEN '0-200'
         WHEN f.total < 250 THEN '200-250'
         WHEN f.total < 300 THEN '250-300'
         ELSE '300-350' END AS segment,
    COUNT(*) AS count
FROM first_score f
INNER JOIN student s ON f.exam_id = s.exam_id
LEFT JOIN major m ON s.major_code = m.major_code
GROUP BY m.department, segment
```

#### 及格标准

| 科目 | 满分 | 及格线 | 理由 |
|---|---|---|---|
| 政治 | 100 | 60 | 60% = 60 分 |
| 外语 | 100 | 60 | 60% = 60 分 |
| 专业基础 | 150 | **90** | 60% = 90 分（非全局 60 分） |

#### 图表说明

| 图表 | 类型 | 说明 |
|---|---|---|
| 各院系平均分对比 | **分组柱状图** | X 轴=院系，每组 3 根柱子（政治/外语/专业基础） |
| 分数段分布 | **饼图** | 四段（0-200/200-250/250-300/300-350），按院系选择器过滤 |
| 计划 vs 实际 | **对比柱状图** | 每个专业两根柱子（计划数/实际录取数） |
| 录取专业分布 | **饼图** | 各专业录取人数占比 |

#### 自动刷新机制

```
Admission.vue 生成录取名单
  → notifyAdmissionGenerated()
  → EventBus: admissionVersion++
  → Stats.vue watch(admissionVersion)
  → loadAll() 重新拉取全部统计数据
  → 图表重新渲染
```

---

### 4.9 考生门户

**页面**：`/portal`（公开访问，无需登录）  
**后端**：`/inquiry/{examId}`

#### 功能描述
提供给考生的独立自助服务平台，包含两个功能标签页。

#### Tab 1：自助报名
- 填写考生档案表单（与管理员端的考生添加表单相同）
- 提交后数据写入 `student` 表
- 调用 `POST /student/add`

#### Tab 2：成绩查询
- 输入考号
- 调用 `GET /inquiry/{examId}`（公开接口，无 JWT 验证）
- 返回聚合数据：

```java
// InquiryVO 结构
{
  examId: "20250001",          // 考号
  name: "张伟",                // 姓名
  majorCode: "081200",         // 报考专业代码
  majorName: "计算机科学与技术", // 专业名称
  hasFirstScore: true,         // 是否有初试成绩
  hasSecondScore: true,        // 是否有复试成绩
  politics: 78,                // 政治
  english: 82,                 // 外语
  professionalBase: 135,       // 专业基础
  firstTotal: 295,             // 初试总分
  professional: 82,            // 复试专业科目
  interview: 85,               // 面试
  computerTest: 78,            // 上机
  secondTotal: 245,            // 复试总分
  combinedTotal: 540,          // 综合总分
  isAdmitted: true,            // 是否录取
  department: "计算机学院"      // 录取院系
}
```

#### 查询逻辑
```java
// InquiryController
1. 从 student 表查考生信息
2. 从 first_score 表查初试成绩（可能不存在 → hasFirstScore=false）
3. 从 second_score 表查复试成绩（可能不存在 → hasSecondScore=false）
4. 从 admission 表查录取状态（可能不存在 → isAdmitted=null）
5. 拼接 major 表获取专业名称
6. 组装 InquiryVO 返回
```

---

## 五、数据库设计

### 5.1 E-R 图

```
┌──────────┐       ┌──────────────┐       ┌──────────────┐
│   user   │       │  department  │       │    major     │
├──────────┤       ├──────────────┤       ├──────────────┤
│ id    PK │       │ id    PK     │       │ major_code PK│
│ username │       │ name  UNIQUE │◄──────│ major_name   │
│ password │       └──────────────┘       │ department   │
│ role     │              按名称匹配      │ plan_inside  │
└──────────┘              (非正式FK)     │ plan_outside │
                              ▲          │ cutoff_line  │
                              │          └──────┬───────┘
                              │                 │
                              │           N:1   │ FK
                              │  ┌──────────────┘
                              │  │
                         ┌────┴──┴─────┐
                         │   student   │
                         ├─────────────┤
                         │ exam_id  PK │──┬── 1:1 ──► first_score
                         │ name        │  │           (exam_id PK+FK)
                         │ gender      │  │
                         │ age         │  ├── 1:1 ──► second_score
                         │ political   │  │           (exam_id PK+FK)
                         │ is_fresh    │  │
                         │ education   │  ├── 1:1 ──► admission
                         │ source      │  │           (exam_id PK+FK)
                         │ major_code FK│ │
                         │ type        │  │
                         └─────────────┘  │
                                          │
              ┌───────────────────────────┘
              ▼
     ┌────────────────┐     ┌────────────────┐     ┌────────────────┐
     │  first_score   │     │  second_score  │     │   admission    │
     ├────────────────┤     ├────────────────┤     ├────────────────┤
     │ exam_id PK+FK  │     │ exam_id PK+FK  │     │ exam_id PK+FK  │
     │ politics (100) │     │ professional   │     │ department     │
     │ english (100)  │     │ interview      │     │ first_total    │
     │ professional_  │     │ computer_test  │     │ second_total   │
     │   base (150)   │     │ total (计算列)  │     │ is_admitted    │
     │ total (计算列)  │     └────────────────┘     └────────────────┘
     └────────────────┘
```

### 5.2 7 张表完整结构

#### `user` — 系统用户

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | INT | PK, AUTO_INCREMENT | 用户ID |
| username | VARCHAR(32) | NOT NULL, UNIQUE | 用户名 |
| password | VARCHAR(64) | NOT NULL | 密码（BCrypt） |
| role | VARCHAR(16) | DEFAULT 'admin' | 角色 |

#### `department` — 院系字典

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | INT | PK, AUTO_INCREMENT | 院系ID |
| name | VARCHAR(64) | NOT NULL, UNIQUE | 院系名称 |

#### `major` — 专业字典

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| major_code | VARCHAR(32) | PK | 专业代码 |
| major_name | VARCHAR(64) | NOT NULL | 专业名称 |
| department | VARCHAR(64) | — | 所属院系（按名称） |
| plan_inside | INT | DEFAULT 0 | 计划内招生数 |
| plan_outside | INT | DEFAULT 0 | 计划外招生数 |
| **cutoff_line** | INT | DEFAULT NULL | **录取分数线** |

#### `student` — 考生档案

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| exam_id | VARCHAR(32) | PK | 考号 |
| name | VARCHAR(32) | NOT NULL | 姓名 |
| gender | VARCHAR(8) | — | 性别 |
| age | INT | — | 年龄 |
| political | VARCHAR(32) | — | 政治面貌 |
| is_fresh | TINYINT(1) | — | 是否应届 |
| education | VARCHAR(32) | — | 学历 |
| source | VARCHAR(64) | — | 考生来源（毕业院校） |
| major_code | VARCHAR(32) | FK → major | 报考专业代码 |
| type | VARCHAR(32) | — | 报考类别 |

**索引**：`PRIMARY (exam_id)`, `INDEX (major_code)`

#### `first_score` — 初试成绩

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| exam_id | VARCHAR(32) | PK, FK | 考号 |
| politics | INT | DEFAULT 0 | 政治（满分 100） |
| english | INT | DEFAULT 0 | 外语（满分 100） |
| professional_base | INT | DEFAULT 0 | 专业基础（满分 150） |
| total | INT | **计算列** | politics+english+professional_base |

#### `second_score` — 复试成绩

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| exam_id | VARCHAR(32) | PK, FK | 考号 |
| professional | INT | DEFAULT 0 | 专业科目（满分 100） |
| interview | INT | DEFAULT 0 | 面试（满分 100） |
| computer_test | INT | DEFAULT 0 | 上机（满分 100） |
| total | INT | **计算列** | professional+interview+computer_test |

#### `admission` — 录取名单

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| exam_id | VARCHAR(32) | PK, FK | 考号 |
| department | VARCHAR(64) | — | 录取系别（冗余） |
| first_total | INT | — | 初试总分（冗余） |
| second_total | INT | — | 复试总分（冗余） |
| is_admitted | TINYINT(1) | DEFAULT 0 | 0=未录取 / 1=已录取 |

### 5.3 外键约束

| 子表 | 外键列 | 父表 | 说明 |
|---|---|---|---|
| student | major_code | major(major_code) | 考生必须报考已有专业 |
| first_score | exam_id | student(exam_id) | 成绩必须对应已有考生 |
| second_score | exam_id | student(exam_id) | 同上 |
| admission | exam_id | student(exam_id) | 录取必须对应已有考生 |

### 5.4 数据库对象

| 类型 | 数量 | 说明 |
|---|---|---|
| 表 | 7 | — |
| 视图 | 0 | 多表查询在 Java 层实现 |
| 触发器 | 0 | 业务逻辑在 Service 层处理 |
| 存储过程 | 0 | 事务由 Spring @Transactional 管理 |
| 计算列 | 2 | first_score.total, second_score.total |

### 5.5 当前数据量

| 表 | 行数 |
|---|---|
| user | 1 |
| department | 7 |
| major | 11 |
| student | 65 |
| first_score | 64 |
| second_score | 64 |
| admission | 62 |

---

## 六、API 接口文档

### 6.1 通用规范

**Base URL**：`http://localhost:8080`

**统一响应格式**：
```json
{
  "code": 200,       // 200=成功, 其他=失败
  "msg": "success",  // 提示信息
  "data": {}         // 响应数据
}
```

**认证**：除 `/user/login` 和 `/inquiry/{examId}` 外，所有接口需携带 `Authorization: Bearer <token>` 请求头。

---

### 6.2 接口列表

#### 用户模块

| 方法 | 路径 | 参数 | 返回 |
|---|---|---|---|
| POST | `/user/login` | Body: `{ username, password }` | `{ token, username, role }` |

#### 统计模块

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/stats/subject` | 全局各科统计（政治/外语/专业基础） |
| GET | `/stats/segment` | 全局分数段分布 |
| GET | `/stats/admission` | 录取生源分析（年龄/来源/专业分布） |
| GET | `/stats/plan-vs-actual` | 计划 vs 实际录取 |
| GET | `/stats/dept-subject` | **院系各科成绩对比** |
| GET | `/stats/dept-segment` | **院系分数段分布** |

#### 院系模块

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/department/list` | 院系列表 |
| GET | `/department/with-majors` | 院系+下属专业（展开行用） |
| POST | `/department/add` | 添加院系 `{ name }` |
| PUT | `/department/update` | 修改院系 `{ id, name }` |
| DELETE | `/department/delete/{id}` | 删除院系 |

#### 专业模块

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/major/list?keyword=&page=&pageSize=` | 分页列表 |
| GET | `/major/suggest?keyword=` | 搜索自动补全 |
| POST | `/major/add` | 添加专业 |
| PUT | `/major/update` | 修改专业 |
| DELETE | `/major/delete/{majorCode}` | 删除专业（有考生则拒绝） |
| PUT | `/major/batch-cutoff` | **批量保存分数线** `[{ majorCode, cutoffLine }]` |

#### 考生模块

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/student/list?keyword=&political=&isFresh=&education=&source=&majorCode=&type=&page=&pageSize=` | **7 维度组合搜索** |
| GET | `/student/suggest?keyword=` | 考号/姓名自动补全 |
| POST | `/student/add` | 添加考生 |
| PUT | `/student/update` | 修改考生 |
| DELETE | `/student/delete/{examId}` | 删除考生 |
| GET | `/student/export` | **CSV 导出**（直接下载） |

#### 初试成绩模块

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/first_score/save` | 录入/修改成绩 |
| GET | `/first_score/check?politicsLine=&englishLine=&professionalBaseLine=&totalScoreLine=` | 按分数线筛选复试名单 |
| GET | `/first_score/list?keyword=&page=&pageSize=` | 列表（考号/姓名搜索） |
| GET | `/first_score/suggest?keyword=` | 自动补全（含姓名） |
| GET | `/first_score/export` | CSV 导出 |

#### 复试成绩模块

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/second_score/save` | 录入/修改成绩 |
| GET | `/second_score/list?keyword=&page=&pageSize=` | 列表（考号/姓名搜索） |
| GET | `/second_score/suggest?keyword=` | 自动补全（含姓名） |
| GET | `/second_score/export` | CSV 导出 |

#### 录取模块

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/admission/generate` | **生成录取名单**（按各专业 cutoff_line） |
| GET | `/admission/list?page=&pageSize=` | 录取名单列表（基本） |
| GET | `/admission/detail?page=&pageSize=` | 录取名单详情（含姓名/专业名，按院系→专业→分数排序） |
| GET | `/admission/export` | **CSV 导出**（按院系→专业→分数排序） |

#### 公开查询模块

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/inquiry/{examId}` | 考生查询个人成绩+录取状态（无需登录） |

---

## 七、前端路由与页面

### 7.1 路由表

| 路径 | 组件 | 权限 | 说明 |
|---|---|---|---|
| `/` | 重定向 | — | → `/stats` |
| `/login` | `Login.vue` | 公开 | 管理员登录 |
| `/portal` | `Portal.vue` | 公开 | 考生门户（报名+查分） |
| `/stats` | `Stats.vue` | 需登录 | 数据统计仪表盘 |
| `/department` | `Department.vue` | 需登录 | 院系管理 |
| `/major` | `Major.vue` | 需登录 | 专业管理 |
| `/student` | `Student.vue` | 需登录 | 考生档案 |
| `/first-score` | `ScoreFirst.vue` | 需登录 | 初试成绩 |
| `/second-score` | `ScoreSecond.vue` | 需登录 | 复试成绩 |
| `/admission` | `Admission.vue` | 需登录 | 录取管理 |
| `/:pathMatch(.*)*` | 重定向 | — | 404 → `/stats` |

### 7.2 路由守卫逻辑

```javascript
router.beforeEach((to, from, next) => {
  // 公开页面：直接放行
  if (to.path === '/portal' || to.path === '/login') {
    if (to.path === '/login' && getToken()) {
      next('/stats')  // 已登录则跳转主页
    } else {
      next()
    }
  }
  // 需认证页面：检查 Token
  else if (getToken() || getUserInfo()) {
    next()
  }
  // 未登录：跳转登录页
  else {
    next('/login')
  }
})
```

### 7.3 页面布局

**管理端**（需登录）：
```
┌──────────┬──────────────────────────────┐
│ 侧边栏    │  顶栏：用户名 + 退出按钮        │
│          ├──────────────────────────────┤
│ 📊 数据统计│                              │
│ 🏛 院系管理│        页面内容区               │
│ 📚 专业管理│                              │
│ 👨‍🎓 考生档案│                              │
│ 📝 初试成绩│                              │
│ 📋 复试成绩│                              │
│ 🎓 录取管理│                              │
└──────────┴──────────────────────────────┘
```

**考生门户**（公开，无侧边栏）：
```
┌──────────────────────────────────────────┐
│ 🎓 研究生招生考生服务门户                    │
├──────────────────────────────────────────┤
│ [自助报名] [成绩查询]                       │
├──────────────────────────────────────────┤
│         对应标签页内容                      │
└──────────────────────────────────────────┘
```

---

## 八、关键设计决策

### 8.1 为什么分数线存在 major 表而非 admission 表？

major 表是一对一关系（每个专业一条分数线），admission 表是多对一（多个考生对应一个专业）。将 cutoff_line 放在 major 表意味着分数线是专业的属性，修改一次即可影响该专业下所有考生的录取判断。

### 8.2 为什么录取生成逻辑在 Service 层而非存储过程？

- Spring `@Transactional` 提供原子性（全部成功或全部回滚）
- MyBatis-Plus `saveBatch` 提供批量插入效率
- Java 代码比存储过程更易调试、版本控制和单元测试
- 生成逻辑涉及多表关联 + 业务判断（NULL 跳过），Java 代码表达力更强

### 8.3 为什么不使用数据库视图？

统计查询依赖动态 WHERE 条件（院系过滤、分页、关键字搜索），静态视图无法满足灵活性。MyBatis-Plus 的 `@Select` + `LambdaQueryWrapper` 组合可以动态构建 SQL。

### 8.4 为什么院系-专业关联按名称而非外键？

- 简化建表（无需先创建 department 再创建 major）
- 院系名称变更时，只需同步更新 major.department 字段
- 缺点：无强引用完整性保障（存在脏数据风险）→ 通过应用层代码保证一致性

### 8.5 为什么初试复试总分使用 MySQL 计算列？

- 保证数据一致性（总分永远等于各科之和）
- 减少 Java 代码中的计算逻辑
- 数据库自动维护，无需应用层干预
- Java 实体标记 NEVER 插入/更新，MyBatis-Plus 跳过该字段

### 8.6 为什么 CSV 导出使用直接 URL 跳转而非 Axios？

导出文件需要触发浏览器下载行为。`window.open('/admission/export', '_blank')` 方式简单可靠，后端设置 `Content-Disposition: attachment` + `Content-Type: text/csv` 即可触发下载。UTF-8 BOM 头确保 Excel 正确显示中文。

### 8.7 为什么统计页使用 EventBus 而非轮询？

EventBus（`admissionVersion` ref）实现了发布-订阅模式：录取生成后立即通知统计页刷新，无需定时轮询浪费资源。基于 Vue 响应式系统，零依赖。

---

## 九、项目文件结构

```
admission-system/
│
├── backend/admission-system/
│   ├── pom.xml                              ← Maven 配置
│   └── src/main/
│       ├── java/com/admission/
│       │   ├── AdmissionSystemApplication.java  ← Spring Boot 启动类
│       │   ├── common/
│       │   │   └── Result.java              ← 统一响应格式 { code, msg, data }
│       │   ├── config/
│       │   │   ├── WebConfig.java           ← CORS 跨域 + BCrypt Bean
│       │   │   ├── MyBatisPlusConfig.java   ← 分页插件配置
│       │   │   ├── JwtUtils.java            ← JWT Token 工具（生成/解析/验证）
│       │   │   └── GlobalExceptionHandler.java ← 全局异常处理
│       │   ├── controller/
│       │   │   ├── UserController.java      ← POST /user/login
│       │   │   ├── StatsController.java     ← GET /stats/*
│       │   │   ├── MajorController.java     ← CRUD + batch-cutoff
│       │   │   ├── StudentController.java   ← CRUD + export + multi-filter
│       │   │   ├── FirstScoreController.java← save + check + list + export
│       │   │   ├── SecondScoreController.java← save + list + export
│       │   │   ├── AdmissionController.java ← generate + detail + export
│       │   │   ├── DepartmentController.java← CRUD + with-majors
│       │   │   └── InquiryController.java   ← GET /inquiry/{examId}
│       │   ├── dto/
│       │   │   └── LoginDTO.java            ← { username, password }
│       │   ├── entity/
│       │   │   ├── User.java                ← user 表
│       │   │   ├── Department.java          ← department 表
│       │   │   ├── Major.java               ← major 表（含 cutoffLine）
│       │   │   ├── Student.java             ← student 表
│       │   │   ├── FirstScore.java          ← first_score 表（total 计算列）
│       │   │   ├── SecondScore.java         ← second_score 表（total 计算列）
│       │   │   └── Admission.java           ← admission 表
│       │   ├── interceptor/
│       │   │   └── AuthInterceptor.java     ← JWT 认证拦截器
│       │   ├── mapper/
│       │   │   ├── UserMapper.java
│       │   │   ├── MajorMapper.java
│       │   │   ├── StudentMapper.java       ← + 生源分析 SQL
│       │   │   ├── FirstScoreMapper.java    ← + 统计 SQL（5 条）
│       │   │   ├── SecondScoreMapper.java
│       │   │   ├── AdmissionMapper.java     ← + 详情列表 JOIN SQL
│       │   │   └── DepartmentMapper.java
│       │   ├── service/
│       │   │   ├── UserService.java         ← login
│       │   │   ├── StatsService.java        ← 6 个统计方法
│       │   │   ├── MajorService.java        ← removeMajorById, searchByKeyword
│       │   │   ├── StudentService.java      ← search (多条件)
│       │   │   ├── FirstScoreService.java   ← saveOrUpdate, getEligibleList, searchByKeyword
│       │   │   ├── SecondScoreService.java  ← saveOrUpdate, searchByKeyword
│       │   │   ├── AdmissionService.java    ← generateAdmissionList, getDetailList
│       │   │   └── DepartmentService.java   ← listWithMajors
│       │   ├── service/impl/                ← (8 个实现类)
│       │   ├── utils/
│       │   │   └── CsvUtils.java            ← UTF-8 BOM CSV 写入
│       │   └── vo/
│       │       ├── AdmissionVO.java         ← 录取详情（多表 JOIN）
│       │       ├── AdmissionStatsVO.java    ← 生源分布
│       │       ├── SubjectStatsVO.java      ← 单科统计
│       │       ├── ScoreSegmentVO.java      ← 分数段
│       │       ├── DeptSubjectVO.java       ← 院系成绩统计
│       │       ├── DeptSegmentVO.java       ← 院系分数段
│       │       ├── FirstScoreVO.java        ← 复试筛选名单
│       │       ├── DepartmentVO.java        ← 院系+专业列表
│       │       └── InquiryVO.java           ← 考生查询结果
│       └── resources/
│           └── application.properties       ← 数据库/JWT/MyBatis 配置
│
├── frontend/
│   ├── package.json                         ← 依赖配置
│   ├── vite.config.js                       ← Vite 构建配置
│   ├── index.html                           ← HTML 入口
│   └── src/
│       ├── main.js                          ← Vue 入口（注册 Element Plus + Router）
│       ├── App.vue                          ← 根布局（侧边栏 + 顶栏 + 路由出口）
│       ├── style.css                        ← 全局样式
│       ├── router/
│       │   └── index.js                     ← 10 条路由 + 导航守卫
│       ├── api/
│       │   └── index.js                     ← Axios 实例 + 拦截器 + 32 个 API 函数
│       ├── utils/
│       │   ├── auth.js                      ← Token/User 存取
│       │   └── eventBus.js                  ← 跨组件事件总线
│       └── views/
│           ├── Login.vue                    ← 登录页
│           ├── Portal.vue                   ← 考生门户（报名+查分）
│           ├── Stats.vue                    ← 数据统计（6 图表 + 5 表格）
│           ├── Department.vue               ← 院系管理（展开行）
│           ├── Major.vue                    ← 专业管理（含录取线列）
│           ├── Student.vue                  ← 考生档案（7 维度搜索）
│           ├── ScoreFirst.vue               ← 初试成绩（复试筛选）
│           ├── ScoreSecond.vue              ← 复试成绩
│           └── Admission.vue                ← 录取管理（分数线配置+名单）
│
├── database/
│   ├── init.sql                             ← 建表 DDL + 初始院系 + admin 账号
│   └── test-data.sql                        ← 55 名考生测试数据
│
└── docs/
    ├── database-analysis.md                 ← 数据库分析文档
    └── project-report.md                    ← 本文档
```

---

## 十、部署与运行

### 环境要求

| 组件 | 要求 |
|---|---|
| JDK | 11+ |
| Maven | 3.6+ |
| Node.js | 18+ |
| MySQL | 8.0+ |

### 数据库初始化

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS admission_db DEFAULT CHARSET utf8mb4;"

# 2. 执行建表脚本
mysql -u root -p admission_db < database/init.sql

# 3. 导入测试数据（可选）
mysql -u root -p admission_db < database/test-data.sql
```

### 后端启动

```bash
cd backend/admission-system
mvn clean package -DskipTests
java -jar target/admission-system-*.jar
# 或使用 Maven 插件
mvn spring-boot:run
```

服务运行在 `http://localhost:8080`。

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

开发服务器运行在 `http://localhost:5173`。

### 登录信息

- 用户名：`admin`
- 密码：`123456`
- 考生门户：`http://localhost:5173/portal`（无需登录）

---

> **文档版本**：1.0 | **生成日期**：2026-06-12 | **总计约 56 个 Java 源文件 + 10 个 Vue 组件 + 7 张数据表**
