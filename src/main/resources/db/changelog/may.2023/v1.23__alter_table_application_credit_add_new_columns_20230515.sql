--liquibase formatted sql
--changeset ManhNV8:v1.22__alter_table_application_add_new_columns_20230511-001 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit' AND COLUMN_NAME = 'guarantee_form';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit' AND COLUMN_NAME = 'guarantee_form_value';
ALTER TABLE application_credit ADD guarantee_form varchar(10) NULL;
ALTER TABLE application_credit ADD guarantee_form_value varchar(100) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.22__alter_table_application_add_new_columns_20230511-002 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'application_authority_level';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'priority_authority';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'user_authority_level';
ALTER TABLE application ADD application_authority_level INT NULL COMMENT 'Cấp độ ưu tiên thẩm quyền khoản vay';
ALTER TABLE application ADD priority_authority varchar(50) NULL COMMENT 'Cấp thẩm quyền khoản vay';
ALTER TABLE application MODIFY COLUMN partner_code varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Mã đối tác';
ALTER TABLE application MODIFY COLUMN process_definition_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'definition id workflow tổng';
ALTER TABLE application MODIFY COLUMN process_business_key varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'business key workflow tổng';
ALTER TABLE application MODIFY COLUMN region_code varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Mã miền';
ALTER TABLE application MODIFY COLUMN area_code varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Mã vùng';
ALTER TABLE application MODIFY COLUMN process_key varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Key workflow tổng';
ALTER TABLE application MODIFY COLUMN workflow_version int NULL COMMENT 'Phiên bản workflow tổng';
ALTER TABLE application MODIFY COLUMN process_instance_id varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'process instance id workflow tổng';
ALTER TABLE application MODIFY COLUMN sub_process_instance_id varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'process instance id workflow con';
ALTER TABLE application MODIFY COLUMN sub_process_definition_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'definition id workflow con';
ALTER TABLE application MODIFY COLUMN sub_process_key varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Key workflow con';
ALTER TABLE application MODIFY COLUMN proposal_approval_position varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Cấp phê duyệt đề xuất (tên thẩm quyền đề xuất)';
ALTER TABLE application MODIFY COLUMN loan_approval_position varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Cấp phê duyệt khoản vay (tên thẩm quyền khoản vay)';
ALTER TABLE application ADD user_authority_level INT NULL COMMENT 'Cấp độ ưu tiên thẩm quyền của user hiện tại (user đang thao tác hồ sơ)';

