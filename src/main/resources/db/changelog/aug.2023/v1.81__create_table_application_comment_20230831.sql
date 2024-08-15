--liquibase formatted sql
--changeset TRILM9:v1.81__create_table_application_comment_20230831.sql dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_comment';
CREATE TABLE `application_comment` (
                                       `application_id` bigint NOT NULL COMMENT 'PK, ID của hồ sơ đề nghị vay vốn',
                                       `bpm_id` varchar(36) COMMENT 'Mã hồ sơ tự sinh của BPM',
                                       `ref_id` varchar(36) COMMENT 'Mã hồ sơ của LDP',
                                       `application_comment_data` blob COMMENT 'Thông tin comment của application',
                                       `created_at` datetime DEFAULT NULL COMMENT 'Ngày tạo comment',
                                       `created_by` varchar(50) DEFAULT NULL COMMENT 'Người tạo comment',
                                       `updated_at` datetime DEFAULT NULL COMMENT 'Ngày cập nhật',
                                       `updated_by` varchar(50) DEFAULT NULL COMMENT 'Người cập nhật',
                                       PRIMARY KEY (`application_id`),
                                       CONSTRAINT `application_comment_application_fk` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`)
);