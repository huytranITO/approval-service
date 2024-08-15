--liquibase formatted sql
--changeset ManhNV8:v1.24__created_new_table_process_instance_20230521-001 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = 'process_instance'
CREATE TABLE `process_instance`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `process_instance_type` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Loại workflow (MANUAL | AUTO)',
  `process_instance_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Instance id workflow tổng',
  `process_definition_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Definition id workflow tổng',
  `process_business_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Business key (từ khi bắt đầu cho tới kết thúc workflow)',
  `process_key` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Process key workflow tổng',
  `workflow_version` int(0) NULL DEFAULT NULL COMMENT 'Phiên bản sử dụng (workflow tổng)',
  `sub_process_instance_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Instance id workflow con',
  `sub_process_definition_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Definition id workflow con',
  `sub_process_key` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Process key workflow con',
  `sub_workflow_version` int(0) NULL DEFAULT NULL COMMENT 'Phiên bản sử dụng (workflow con)',
  `created_at` datetime(0) NULL DEFAULT NULL,
  `updated_at` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

--liquibase formatted sql
--changeset ManhNV8:v1.24__created_new_table_process_instance_20230521-002 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'process_instance_id';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'process_definition_id';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'process_business_key';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'process_key';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'workflow_version';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'sub_process_instance_id';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'sub_process_definition_id';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'sub_process_key';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'user_authority_level';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'loan_approval_user';
ALTER TABLE `application`
MODIFY COLUMN `process_instance_id` bigint NULL DEFAULT NULL AFTER `customer_id`,
DROP COLUMN `process_definition_id`,
DROP COLUMN `process_business_key`,
DROP COLUMN `process_key`,
DROP COLUMN `workflow_version`,
DROP COLUMN `sub_process_instance_id`,
DROP COLUMN `sub_process_definition_id`,
DROP COLUMN `sub_process_key`,
DROP COLUMN `user_authority_level`,
CHANGE COLUMN `loan_approval_user` `proposal_approval_reception` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Cấp TQ tiếp nhận' AFTER `loan_approval_position`;;

SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE application ADD CONSTRAINT application_ibfk_2 FOREIGN KEY (process_instance_id) REFERENCES process_instance(id) ON DELETE RESTRICT ON UPDATE RESTRICT;
SET FOREIGN_KEY_CHECKS = 1;

--liquibase formatted sql
--changeset ManhNV8:v1.24__created_new_table_process_instance_20230521-003 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_history_approval' AND COLUMN_NAME = 'reception_level';
ALTER TABLE `application_history_approval`
ADD COLUMN `reception_level` varchar(20) NULL COMMENT 'Cấp Thẩm quyền tiếp nhận' AFTER `full_name`;