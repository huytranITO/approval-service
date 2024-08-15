--liquibase formatted sql
--changeset ManhNV8:v1.73__alter_table_application_add_new_columns_ldp_status_20230820 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'ldp_status';
ALTER TABLE application ADD ldp_status INT NULL COMMENT 'Trạng thái bổ sung hồ sơ từ khách hàng Landing Page
- 0 : Hoàn thành
- 1 : Chờ khách hàng bổ sung
- 2 : Chờ khách hàng xác nhận';
ALTER TABLE application CHANGE ldp_status ldp_status INT NULL COMMENT 'Trạng thái bổ sung hồ sơ từ khách hàng Landing Page
- 0 : Hoàn thành
- 1 : Chờ khách hàng bổ sung
- 2 : Chờ khách hàng xác nhận' AFTER sale_code;
ALTER TABLE application CHANGE generator_status generator_status varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '00' NULL AFTER reception_at;
ALTER TABLE application CHANGE processing_step_code processing_step_code varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Mã bước xử lý' AFTER status;
ALTER TABLE application CHANGE approval_document_no approval_document_no varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Số biên bản phê duyêt' AFTER generator_status;
ALTER TABLE application CHANGE sale_code sale_code varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'sale code' AFTER approval_document_no;