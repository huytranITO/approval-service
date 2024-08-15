--liquibase formatted sql
--changeset TuanHA13:v1.15__create_table_application_checklist_20230421 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = 'application_checklist'
CREATE TABLE `application_checklist` (
                                     `id` bigint PRIMARY KEY AUTO_INCREMENT,
                                     `application_id` bigint DEFAULT NULL,
                                     `phase` varchar(10) DEFAULT NULL COMMENT 'phase RM|BM',
                                     `request_code` varchar(50) DEFAULT NULL COMMENT 'Mã hồ sơ dùng để liên kết với checklist',
                                     `created_by` varchar(50) DEFAULT NULL COMMENT 'User khởi tạo',
                                     `created_at` datetime DEFAULT NULL COMMENT 'Thời gian khởi tạo',
                                     `updated_by` varchar(50) DEFAULT NULL COMMENT 'User cập nhật',
                                     `updated_at` datetime DEFAULT NULL COMMENT 'Thời gian cập nhật'
);