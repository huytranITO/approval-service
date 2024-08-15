--liquibase formatted sql
--changeset ManhNV8:v1.54__alter_table_application_add_new_column_processing_step_code_20230705 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'processing_step_code';
ALTER TABLE application ADD processing_step_code varchar(10) NULL COMMENT 'Mã bước xử lý';