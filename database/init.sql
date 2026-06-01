-- 专业字典表
CREATE TABLE IF NOT EXISTS major(
    major_code VARCHAR(32) PRIMARY KEY COMMENT '专业代码',
    major_name VARCHAR(64) NOT NULL COMMENT '专业名称',
    plan_inside INT DEFAULT 0 COMMENT '计划内招生数',
    plan_outside INT DEFAULT 0 COMMENT '计划外招生数'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专业字典';

-- 考生档案表
CREATE TABLE IF NOT EXISTS student(
    exam_id VARCHAR(32) PRIMARY KEY COMMENT '考号',
    name VARCHAR(32) NOT NULL COMMENT '姓名',
    gender VARCHAR(8) COMMENT '性别',
    age INT COMMENT '年龄',
    political VARCHAR(32) COMMENT '政治面貌',
    is_fresh TINYINT(1) COMMENT '是否应届 0-否 1-是',
    education VARCHAR(32) COMMENT '学历',
    source VARCHAR(64) COMMENT '考生来源',
    major_code VARCHAR(32) COMMENT '报考专业代码',
    type VARCHAR(32) COMMENT '报考类别',
    FOREIGN KEY (major_code) REFERENCES major(major_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考生档案';

-- 初试成绩表
CREATE TABLE IF NOT EXISTS first_score(
    exam_id VARCHAR(32) PRIMARY KEY COMMENT '考号',
    politics INT DEFAULT 0 COMMENT '政治成绩',
    english INT DEFAULT 0 COMMENT '外语成绩',
    professional_base INT DEFAULT 0 COMMENT '专业基础成绩',
    total INT GENERATED ALWAYS AS (politics + english + professional_base) STORED COMMENT '初试总分',
    FOREIGN KEY (exam_id) REFERENCES student(exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='初试成绩';

-- 复试成绩表
CREATE TABLE IF NOT EXISTS second_score(
    exam_id VARCHAR(32) PRIMARY KEY COMMENT '考号',
    professional INT DEFAULT 0 COMMENT '复试专业科目成绩',
    interview INT DEFAULT 0 COMMENT '面试成绩',
    computer_test INT DEFAULT 0 COMMENT '上机成绩',
    total INT GENERATED ALWAYS AS (professional + interview + computer_test) STORED COMMENT '复试总分',
    FOREIGN KEY (exam_id) REFERENCES student(exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='复试成绩';

-- 录取名单表
CREATE TABLE IF NOT EXISTS admission(
    exam_id VARCHAR(32) PRIMARY KEY COMMENT '考号',
    department VARCHAR(64) COMMENT '录取系别',
    first_total INT COMMENT '初试总分',
    second_total INT COMMENT '复试总分',
    is_admitted TINYINT(1) DEFAULT 0 COMMENT '是否录取 0-未录取 1-已录取',
    FOREIGN KEY (exam_id) REFERENCES student(exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='录取名单';