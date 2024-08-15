--liquibase formatted sql
--changeset BaoNV2:1.97__alter_table_salary_income_modify_column_ldpSalaryId_20231004 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income' AND COLUMN_NAME = 'ldp_salary_id';
ALTER TABLE salary_income MODIFY COLUMN ldp_salary_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'ID nguồn thu lương từ Landing Page';
