--liquibase formatted sql
--changeset BaoNV2:1.92__alter_table_customer_modify_column_refCusId_20231004 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer' AND COLUMN_NAME = 'ref_cus_id';
ALTER TABLE customer MODIFY COLUMN ref_cus_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Mã KH của hồ sơ đẩy từ Kênh hồ sơ về BPM';
