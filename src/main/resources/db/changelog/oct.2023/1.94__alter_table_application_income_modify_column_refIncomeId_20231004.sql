--liquibase formatted sql
--changeset BaoNV2:1.94__alter_table_application_income_modify_column_refIncomeId_20231004 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_income' AND COLUMN_NAME = 'ldp_income_id';
ALTER TABLE application_income MODIFY COLUMN ldp_income_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'ID nguồn thu từ Landing Page';
