# 研究生招生信息管理系统 — 数据库分析文档

> 更新日期：2026-06-12  
> 数据库：MySQL 8.0 — `admission_db`

---

## 一、E-R 图

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
     ┌────────────────┐
     │  first_score   │     ┌────────────────┐     ┌────────────────┐
     ├────────────────┤     │  second_score  │     │   admission    │
     │ exam_id PK+FK  │     ├────────────────┤     ├────────────────┤
     │ politics       │     │ exam_id PK+FK  │     │ exam_id PK+FK  │
     │ english        │     │ professional   │     │ department     │
     │ professional_  │     │ interview      │     │ first_total    │
     │   base         │     │ computer_test  │     │ second_total   │
     │ total (计算列)  │     │ total (计算列)  │     │ is_admitted    │
     └────────────────┘     └────────────────┘     └────────────────┘
```

### 关系一览

| 关系 | 类型 | 外键 / 关联方式 |
|---|---|---|
| student → major | N:1 | `student.major_code` → `major.major_code` (FK) |
| student → first_score | 1:1 | `first_score.exam_id` → `student.exam_id` (FK) |
| student → second_score | 1:1 | `second_score.exam_id` → `student.exam_id` (FK) |
| student → admission | 1:1 | `admission.exam_id` → `student.exam_id` (FK) |
| major → department | N:1 | `major.department` 按名称匹配 `department.name`（非 FK） |

---

## 二、表结构

### 2.1 系统用户 `user`

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | INT | PK, AUTO_INCREMENT | 用户ID |
| username | VARCHAR(32) | NOT NULL, UNIQUE | 用户名 |
| password | VARCHAR(64) | NOT NULL | 密码（BCrypt 加密） |
| role | VARCHAR(16) | DEFAULT 'admin' | 角色 |

默认账号：`admin / 123456`

### 2.2 院系字典 `department`

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | INT | PK, AUTO_INCREMENT | 院系ID |
| name | VARCHAR(64) | NOT NULL, UNIQUE | 院系名称 |

初始 7 个院系：计算机学院、软件学院、人工智能学院、信息与通信学院、自动化学院、数学与统计学院、管理学院。

> **注意**：院系与专业的关联通过 `major.department` 字段按名称匹配实现，**不是数据库外键**。这种设计的优势是建表简单、院系名可直接作为专业的属性值；劣势是院系改名后需同步更新 `major` 表和 `admission` 表中的 `department` 字段。

### 2.3 专业字典 `major`

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| major_code | VARCHAR(32) | PK | 专业代码（如 `081200`） |
| major_name | VARCHAR(64) | NOT NULL | 专业名称 |
| department | VARCHAR(64) | — | 所属院系名称 |
| plan_inside | INT | DEFAULT 0 | 计划内招生数 |
| plan_outside | INT | DEFAULT 0 | 计划外招生数 |
| **cutoff_line** | INT | DEFAULT NULL | **录取分数线**（初试+复试总分 ≥ 此值录取） |

> `cutoff_line` 为 NULL 表示该专业尚未设置分数线，生成录取名单时该专业考生不会被录取。这要求管理员必须显式配置每个专业的分数线。

### 2.4 考生档案 `student`

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| exam_id | VARCHAR(32) | PK | 考号（主键） |
| name | VARCHAR(32) | NOT NULL | 姓名 |
| gender | VARCHAR(8) | — | 性别（男/女） |
| age | INT | — | 年龄 |
| political | VARCHAR(32) | — | 政治面貌（中共党员/共青团员/群众/其他） |
| is_fresh | TINYINT(1) | — | 是否应届（0=往届 / 1=应届） |
| education | VARCHAR(32) | — | 学历（本科毕业/本科结业/高职高专/同等学力/硕士研究生/其他） |
| source | VARCHAR(64) | — | 考生来源（毕业院校） |
| major_code | VARCHAR(32) | FK → major | 报考专业代码 |
| type | VARCHAR(32) | — | 报考类别（全日制/非全日制） |

**索引**：`PRIMARY KEY (exam_id)`、`INDEX (major_code)`

### 2.5 初试成绩 `first_score`

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| exam_id | VARCHAR(32) | PK, FK → student | 考号 |
| politics | INT | DEFAULT 0 | 政治（满分 100） |
| english | INT | DEFAULT 0 | 外语（满分 100） |
| professional_base | INT | DEFAULT 0 | 专业基础（满分 150） |
| total | INT | **GENERATED ALWAYS AS** `(politics + english + professional_base)` STORED | 初试总分（计算列，满分 350） |

> `total` 是 MySQL 计算列（STORED），由数据库自动维护。Java 实体中标记了 `insertStrategy = NEVER, updateStrategy = NEVER`，MyBatis-Plus 不会尝试写入此字段。

**及格标准**：政治 ≥ 60、外语 ≥ 60、专业基础 ≥ 90（满分 150 的 60%）。

### 2.6 复试成绩 `second_score`

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| exam_id | VARCHAR(32) | PK, FK → student | 考号 |
| professional | INT | DEFAULT 0 | 复试专业科目（满分 100） |
| interview | INT | DEFAULT 0 | 面试（满分 100） |
| computer_test | INT | DEFAULT 0 | 上机（满分 100） |
| total | INT | **GENERATED ALWAYS AS** `(professional + interview + computer_test)` STORED | 复试总分（计算列，满分 300） |

### 2.7 录取名单 `admission`

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| exam_id | VARCHAR(32) | PK, FK → student | 考号 |
| department | VARCHAR(64) | — | 录取系别（取自考生所属专业的院系） |
| first_total | INT | — | 初试总分（冗余存储） |
| second_total | INT | — | 复试总分（冗余存储） |
| is_admitted | TINYINT(1) | DEFAULT 0 | 是否录取（0=未录取 / 1=已录取） |

> 生成录取名单的逻辑：读取每个专业的 `cutoff_line`，考生 `first_total + second_total ≥ cutoff_line` 即录取。全员使用各专业独立分数线，不再有全局统一分数线。

---

## 三、数据库对象

| 对象类型 | 数量 | 说明 |
|---|---|---|
| 表 | **7** | user, department, major, student, first_score, second_score, admission |
| 视图 | **0** | 多表关联查询在 Java 层通过 MyBatis `@Select` 实现 |
| 触发器 | **0** | 业务逻辑在 Service 层处理 |
| 存储过程 | **0** | 未使用 |
| 外键 | **4** | student→major, first_score→student, second_score→student, admission→student |
| 计算列 | **2** | first_score.total, second_score.total |

### 为什么不使用视图和存储过程

- 视图：查询逻辑依赖 MyBatis-Plus 的 `@Select` + `LambdaQueryWrapper` 动态组装条件（如多字段搜索、分页），静态视图无法满足灵活性需求。
- 存储过程：录取名单生成等写操作使用 Spring `@Transactional` 保证原子性，配合 MyBatis-Plus `saveBatch` 批量写入，代码比存储过程更易维护。

---

## 四、存储与配置

| 配置项 | 值 | 说明 |
|---|---|---|
| 存储引擎 | **InnoDB** | 支持事务 (ACID)、行级锁、外键约束 |
| 字符集 | **utf8mb4** | 完整 Unicode 支持 |
| 排序规则 | utf8mb4_0900_ai_ci | MySQL 8.0 默认，Unicode 9.0，不区分大小写 |
| 数据库连接 | `jdbc:mysql://localhost:3306/admission_db` | — |
| ORM 框架 | MyBatis-Plus 3.5.3.1 | 注解驱动，无 XML Mapper |
| 命名策略 | 驼峰 ↔ 下划线自动转换 | `map-underscore-to-camel-case: true` |

---

## 五、业务数据流

```
考生报名 ──► student 表
                │
    ┌───────────┼───────────┐
    ▼           ▼           ▼
录入初试成绩   录入复试成绩   设置专业分数线
first_score   second_score  major.cutoff_line
(总分自动计算) (总分自动计算)     │
    │           │              │
    └─────┬─────┘              │
          ▼                    │
    综合总分 = first_total      │
            + second_total     │
          │                    │
          ▼                    ▼
    生成录取名单 ────────► 综合总分 ≥ cutoff_line → admission 表
          │
          ▼
    统计页面 ←─ 聚合 student + first_score + second_score + admission + major
```

---

## 六、Java 实体与 VO

| 数据库表 | Java 实体 | 相关 VO |
|---|---|---|
| user | `User.java` | — |
| department | `Department.java` | `DepartmentVO.java` |
| major | `Major.java` | — |
| student | `Student.java` | `AdmissionStatsVO.java` |
| first_score | `FirstScore.java` | `FirstScoreVO.java`, `SubjectStatsVO.java`, `ScoreSegmentVO.java`, `DeptSubjectVO.java`, `DeptSegmentVO.java` |
| second_score | `SecondScore.java` | — |
| admission | `Admission.java` | `AdmissionVO.java` |

> VO（View Object）用于封装多表关联查询结果或统计聚合结果，避免直接暴露实体。

---

## 七、当前数据概况

| 表 | 行数 | 说明 |
|---|---|---|
| user | 1 | admin |
| department | 7 | 七大院系 |
| major | 11 | 10 个标准专业 + 1 个网络工程 |
| student | 65 | 30 原始数据 + 9 CS + 25 补充 + 1? |
| first_score | 64 | 与 student 基本一一对应 |
| second_score | 64 | 同上 |
| admission | 62 | 生成录取名单后填充（取决于分数线设置） |

测试数据位于 `database/test-data.sql`（55 名考生，覆盖全部 7 个院系），执行 `init.sql` + `test-data.sql` 即可重建完整开发环境。

---

## 八、建表脚本

- 初始化（含 DDL）：`database/init.sql`
- 测试数据：`database/test-data.sql`
