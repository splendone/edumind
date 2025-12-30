-- AI增强型在线学习平台 数据库设计
-- 创建数据库
CREATE DATABASE IF NOT EXISTS ai_learning_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ai_learning_platform;

-- 1. 用户表
CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    `email` VARCHAR(100) UNIQUE COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `role` ENUM('STUDENT', 'TEACHER', 'ADMIN') DEFAULT 'STUDENT' COMMENT '角色',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`),
    INDEX `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 课程表
CREATE TABLE `course` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    `title` VARCHAR(200) NOT NULL COMMENT '课程标题',
    `subtitle` VARCHAR(300) COMMENT '课程副标题',
    `description` TEXT COMMENT '课程描述',
    `cover_image` VARCHAR(255) COMMENT '封面图片',
    `teacher_id` BIGINT NOT NULL COMMENT '授课教师ID',
    `category_id` BIGINT COMMENT '课程分类ID',
    `level` ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') DEFAULT 'BEGINNER' COMMENT '难度等级',
    `price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '课程价格',
    `total_hours` INT DEFAULT 0 COMMENT '总课时（分钟）',
    `student_count` INT DEFAULT 0 COMMENT '学习人数',
    `rating` DECIMAL(3,2) DEFAULT 0.00 COMMENT '课程评分',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-发布，0-草稿',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_teacher` (`teacher_id`),
    INDEX `idx_category` (`category_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 3. 课程分类表
CREATE TABLE `course_category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程分类表';

-- 4. 课程章节表
CREATE TABLE `course_chapter` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '章节ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `title` VARCHAR(200) NOT NULL COMMENT '章节标题',
    `description` TEXT COMMENT '章节描述',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父章节ID（0表示一级章节）',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_course` (`course_id`),
    INDEX `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程章节表';

-- 5. 课程视频表
CREATE TABLE `course_video` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '视频ID',
    `chapter_id` BIGINT NOT NULL COMMENT '章节ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `title` VARCHAR(200) NOT NULL COMMENT '视频标题',
    `video_url` VARCHAR(500) NOT NULL COMMENT '视频URL（阿里云VOD）',
    `video_id` VARCHAR(100) COMMENT '阿里云视频ID',
    `duration` INT DEFAULT 0 COMMENT '视频时长（秒）',
    `size` BIGINT DEFAULT 0 COMMENT '视频大小（字节）',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `allow_preview` TINYINT DEFAULT 0 COMMENT '是否允许试看',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_chapter` (`chapter_id`),
    INDEX `idx_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程视频表';

-- 6. 课程资料表
CREATE TABLE `course_material` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '资料ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `chapter_id` BIGINT COMMENT '章节ID',
    `title` VARCHAR(200) NOT NULL COMMENT '资料标题',
    `file_url` VARCHAR(500) NOT NULL COMMENT '文件URL',
    `file_name` VARCHAR(200) COMMENT '文件名',
    `file_size` BIGINT DEFAULT 0 COMMENT '文件大小（字节）',
    `file_type` VARCHAR(50) COMMENT '文件类型',
    `download_count` INT DEFAULT 0 COMMENT '下载次数',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_course` (`course_id`),
    INDEX `idx_chapter` (`chapter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程资料表';

-- 7. 学习记录表
CREATE TABLE `learning_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `video_id` BIGINT COMMENT '视频ID',
    `learn_time` INT DEFAULT 0 COMMENT '学习时长（秒）',
    `progress` DECIMAL(5,2) DEFAULT 0.00 COMMENT '学习进度（百分比）',
    `last_learn_position` INT DEFAULT 0 COMMENT '最后学习位置（秒）',
    `is_completed` TINYINT DEFAULT 0 COMMENT '是否完成',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_user_course_video` (`user_id`, `course_id`, `video_id`),
    INDEX `idx_user` (`user_id`),
    INDEX `idx_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习记录表';

-- 8. 用户课程关系表（选课表）
CREATE TABLE `user_course` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `enroll_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    `complete_time` DATETIME COMMENT '完成时间',
    `progress` DECIMAL(5,2) DEFAULT 0.00 COMMENT '总进度',
    `total_learn_time` INT DEFAULT 0 COMMENT '总学习时长（分钟）',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-学习中，2-已完成，0-已放弃',
    UNIQUE KEY `uk_user_course` (`user_id`, `course_id`),
    INDEX `idx_user` (`user_id`),
    INDEX `idx_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户课程关系表';

-- 9. 题库表
CREATE TABLE `question_bank` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '题目ID',
    `course_id` BIGINT COMMENT '关联课程ID',
    `chapter_id` BIGINT COMMENT '关联章节ID',
    `question_type` ENUM('SINGLE', 'MULTIPLE', 'JUDGE', 'FILL', 'ESSAY') NOT NULL COMMENT '题目类型',
    `question_content` TEXT NOT NULL COMMENT '题目内容',
    `options` JSON COMMENT '选项（JSON格式）',
    `correct_answer` TEXT COMMENT '正确答案',
    `analysis` TEXT COMMENT '答案解析',
    `difficulty` ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM' COMMENT '难度',
    `knowledge_points` VARCHAR(500) COMMENT '知识点标签',
    `usage_count` INT DEFAULT 0 COMMENT '使用次数',
    `correct_rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '正确率',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_course` (`course_id`),
    INDEX `idx_chapter` (`chapter_id`),
    INDEX `idx_type` (`question_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题库表';

-- 10. 试卷表
CREATE TABLE `exam_paper` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '试卷ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `title` VARCHAR(200) NOT NULL COMMENT '试卷标题',
    `description` TEXT COMMENT '试卷说明',
    `total_score` INT DEFAULT 100 COMMENT '总分',
    `pass_score` INT DEFAULT 60 COMMENT '及格分',
    `duration` INT DEFAULT 60 COMMENT '考试时长（分钟）',
    `question_count` INT DEFAULT 0 COMMENT '题目数量',
    `paper_type` ENUM('PRACTICE', 'EXAM', 'HOMEWORK') DEFAULT 'PRACTICE' COMMENT '试卷类型',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `created_by` BIGINT COMMENT '创建者ID',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';

-- 11. 试卷题目关系表
CREATE TABLE `exam_paper_question` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
    `paper_id` BIGINT NOT NULL COMMENT '试卷ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `score` INT DEFAULT 0 COMMENT '题目分值',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    INDEX `idx_paper` (`paper_id`),
    INDEX `idx_question` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷题目关系表';

-- 12. 考试记录表
CREATE TABLE `exam_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `paper_id` BIGINT NOT NULL COMMENT '试卷ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `start_time` DATETIME COMMENT '开始时间',
    `submit_time` DATETIME COMMENT '提交时间',
    `total_score` INT DEFAULT 0 COMMENT '总分',
    `user_score` DECIMAL(5,2) DEFAULT 0.00 COMMENT '得分',
    `correct_count` INT DEFAULT 0 COMMENT '正确题数',
    `status` ENUM('IN_PROGRESS', 'SUBMITTED', 'SCORED') DEFAULT 'IN_PROGRESS' COMMENT '状态',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user` (`user_id`),
    INDEX `idx_paper` (`paper_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试记录表';

-- 13. 答题详情表
CREATE TABLE `exam_answer` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '答题ID',
    `record_id` BIGINT NOT NULL COMMENT '考试记录ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `user_answer` TEXT COMMENT '用户答案',
    `is_correct` TINYINT COMMENT '是否正确：1-正确，0-错误',
    `score` DECIMAL(5,2) DEFAULT 0.00 COMMENT '得分',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_record` (`record_id`),
    INDEX `idx_question` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题详情表';

-- 14. 错题本表
CREATE TABLE `wrong_question` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '错题ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `course_id` BIGINT COMMENT '课程ID',
    `wrong_count` INT DEFAULT 1 COMMENT '错误次数',
    `is_mastered` TINYINT DEFAULT 0 COMMENT '是否已掌握',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_user_question` (`user_id`, `question_id`),
    INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错题本表';

-- 15. AI对话记录表
CREATE TABLE `ai_chat_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '对话ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `course_id` BIGINT COMMENT '关联课程ID',
    `session_id` VARCHAR(100) NOT NULL COMMENT '会话ID',
    `question` TEXT NOT NULL COMMENT '用户问题',
    `answer` TEXT COMMENT 'AI回答',
    `context` JSON COMMENT '上下文信息',
    `rating` TINYINT COMMENT '回答评分（1-5）',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user` (`user_id`),
    INDEX `idx_session` (`session_id`),
    INDEX `idx_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话记录表';

-- 16. 课程知识点表
CREATE TABLE `knowledge_point` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '知识点ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `chapter_id` BIGINT COMMENT '章节ID',
    `name` VARCHAR(200) NOT NULL COMMENT '知识点名称',
    `content` TEXT COMMENT '知识点内容',
    `embedding_vector` BLOB COMMENT '向量嵌入（用于RAG检索）',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_course` (`course_id`),
    INDEX `idx_chapter` (`chapter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程知识点表';

-- 17. 讨论区表
CREATE TABLE `discussion` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '讨论ID',
    `course_id` BIGINT COMMENT '课程ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '内容',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `reply_count` INT DEFAULT 0 COMMENT '回复数',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-删除',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_course` (`course_id`),
    INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='讨论区表';

-- 18. 讨论回复表
CREATE TABLE `discussion_reply` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '回复ID',
    `discussion_id` BIGINT NOT NULL COMMENT '讨论ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父回复ID',
    `content` TEXT NOT NULL COMMENT '回复内容',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `status` TINYINT DEFAULT 1 COMMENT '状态',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_discussion` (`discussion_id`),
    INDEX `idx_user` (`user_id`),
    INDEX `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='讨论回复表';

-- 19. 学习笔记表
CREATE TABLE `study_note` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '笔记ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `video_id` BIGINT COMMENT '视频ID',
    `title` VARCHAR(200) COMMENT '笔记标题',
    `content` TEXT NOT NULL COMMENT '笔记内容',
    `video_time` INT COMMENT '视频时间点（秒）',
    `is_public` TINYINT DEFAULT 0 COMMENT '是否公开',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user` (`user_id`),
    INDEX `idx_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习笔记表';

-- 20. 用户行为日志表
CREATE TABLE `user_behavior_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `action_type` VARCHAR(50) NOT NULL COMMENT '行为类型',
    `resource_type` VARCHAR(50) COMMENT '资源类型',
    `resource_id` BIGINT COMMENT '资源ID',
    `action_detail` JSON COMMENT '行为详情',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user` (`user_id`),
    INDEX `idx_action` (`action_type`),
    INDEX `idx_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为日志表';

-- 插入初始数据
-- 管理员账户（密码：admin123，需BCrypt加密）
INSERT INTO `user` (`username`, `password`, `email`, `nickname`, `role`) VALUES
('admin', '$2a$10$N.ZOn9G6/YLFixAopPYY3u2xdQJH4RJC4UqzHDKhvVKpE7JyVhKaS', 'admin@ailearning.com', '系统管理员', 'ADMIN'),
('teacher1', '$2a$10$N.ZOn9G6/YLFixAopPYY3u2xdQJH4RJC4UqzHDKhvVKpE7JyVhKaS', 'teacher@ailearning.com', '张教师', 'TEACHER'),
('student1', '$2a$10$N.ZOn9G6/YLFixAopPYY3u2xdQJH4RJC4UqzHDKhvVKpE7JyVhKaS', 'student@ailearning.com', '李同学', 'STUDENT');

-- 课程分类
INSERT INTO `course_category` (`name`, `parent_id`) VALUES
('编程开发', 0),
('人工智能', 0),
('数据科学', 0),
('前端开发', 1),
('后端开发', 1),
('机器学习', 2),
('深度学习', 2);
