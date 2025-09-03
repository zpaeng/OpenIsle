-- 2025-09-02
-- 本地化开发，初始化脚本
-- 抽奖的时候奖品图片是必须的，把相关代码注释掉即可跳过check

-- 清空users表
DELETE FROM `users`;
-- 插入用户，两个普通用户，一个管理员
INSERT INTO `users` (`id`, `approved`, `avatar`, `created_at`, `display_medal`, `email`, `experience`, `introduction`, `password`, `password_reset_code`, `point`, `register_reason`, `role`, `username`, `verification_code`, `verified`) VALUES
	(1, b'1', '', '2025-09-01 16:08:17.426430', 'PIONEER', 'adminmail@openisle.com', 70, NULL, '$2a$10$m.lLbT3wFtnzFMi7JqN17ecv/dzH704WzU1f/xvQ0nVz4XxTXPT0K', NULL, 110, '测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试', 'ADMIN', '管理员', NULL, b'1'),
	(2, b'1', '', '2025-09-03 16:08:17.426430', 'PIONEER', 'usermail2@openisle.com', 70, NULL, '$2a$10$m.lLbT3wFtnzFMi7JqN17ecv/dzH704WzU1f/xvQ0nVz4XxTXPT0K', NULL, 110, '测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试', 'USER', '普通用户2', NULL, b'1'),
	(3, b'1', '', '2025-09-02 17:21:21.617666', 'PIONEER', 'usermail1@openisle.com', 40, NULL, '$2a$10$m.lLbT3wFtnzFMi7JqN17ecv/dzH704WzU1f/xvQ0nVz4XxTXPT0K', NULL, 40, '测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试', 'USER', '普通用户1', NULL, b'1');

-- 清空tags表
DELETE FROM `tags`;
-- 插入标签，三个测试用标签
INSERT INTO `tags` (`id`, `approved`, `created_at`, `description`, `icon`, `name`, `small_icon`, `creator_id`) VALUES
	(1, b'1', '2025-09-02 10:51:56.000000', '测试用标签1', NULL, '测试用标签1', NULL, NULL),
	(2, b'1', '2025-09-02 10:51:56.000000', '测试用标签2', NULL, '测试用标签2', NULL, NULL),
	(3, b'1', '2025-09-02 10:51:56.000000', '测试用标签3', NULL, '测试用标签3', NULL, NULL);

-- 清空categories表
DELETE FROM `categories`;
-- 插入分类，三个测试用分类
INSERT INTO `categories` (`id`, `description`, `icon`, `name`, `small_icon`) VALUES
	(1, '测试用分类1', '1', '测试用分类1', NULL),
	(2, '测试用分类2', '2', '测试用分类2', NULL),
	(3, '测试用分类3', '3', '测试用分类3', NULL);