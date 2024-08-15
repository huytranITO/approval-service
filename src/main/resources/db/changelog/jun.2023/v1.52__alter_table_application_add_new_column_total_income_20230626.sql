--liquibase formatted sql
--changeset ManhNV8:v1.52__alter_table_application_add_new_column_total_income_20230626-add_new_column_total_income dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'total_income';
ALTER TABLE application ADD total_income decimal(20,0) NULL COMMENT 'Tổng nguồn thu';
ALTER TABLE application CHANGE total_income total_income decimal(20,0) NULL COMMENT 'Tổng nguồn thu' AFTER ref_id;

--liquibase formatted sql
--changeset ManhNV8:v1.52__alter_table_application_add_new_column_total_income_20230626-increase_max_length_suggested_amount dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'suggested_amount';
ALTER TABLE application MODIFY COLUMN suggested_amount decimal(20,0) NULL COMMENT 'Số tiền đề xuất = SUM(hạn mức tại tab khoản vay)';