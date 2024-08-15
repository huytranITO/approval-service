--liquibase formatted sql
--changeset BaoNV2:1.98__alter_table_individual_enterprise_income_modify_column_ldpBusinessId_20231004 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_enterprise_income' AND COLUMN_NAME = 'ldp_business_id';
ALTER TABLE individual_enterprise_income MODIFY COLUMN ldp_business_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'ID nguồn thu cá nhân kinh doanh/doanh nghiệp từ Landing Page';
