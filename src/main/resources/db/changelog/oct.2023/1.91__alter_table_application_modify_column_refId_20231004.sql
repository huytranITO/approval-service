--liquibase formatted sql
--changeset BaoNV2:1.91__alter_table_application_modify_column_refId_20231004 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'ref_id';
ALTER TABLE application MODIFY COLUMN ref_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Mã hồ sơ đẩy từ Kênh hồ sơ về BPM';
