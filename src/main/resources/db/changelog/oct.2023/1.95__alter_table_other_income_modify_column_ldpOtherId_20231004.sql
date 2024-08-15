--liquibase formatted sql
--changeset BaoNV2:1.95__alter_table_other_income_modify_column_ldpOtherId_20231004 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'other_income' AND COLUMN_NAME = 'ldp_other_id';
ALTER TABLE other_income MODIFY COLUMN ldp_other_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'ID nguồn thu khác từ Landing Page';
