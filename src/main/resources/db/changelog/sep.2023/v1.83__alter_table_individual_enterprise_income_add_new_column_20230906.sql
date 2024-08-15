--liquibase formatted sql
--changeset ManhNV8:v1.83__alter_table_individual_enterprise_income_add_new_column_20230906 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_enterprise_income' AND COLUMN_NAME = 'business_experience';
ALTER TABLE individual_enterprise_income ADD business_experience INTEGER NULL COMMENT 'Kinh nghiệm kinh doanh (năm)';
ALTER TABLE individual_enterprise_income CHANGE business_experience business_experience INTEGER NULL COMMENT 'Kinh nghiệm kinh doanh (năm)' AFTER income_type_value;
