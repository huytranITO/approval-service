--liquibase formatted sql
--changeset ManhNV8:v1.82__alter_table_aml_opr_change_data_type_20230905 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'aml_opr' AND COLUMN_NAME = 'end_date';
ALTER TABLE aml_opr MODIFY COLUMN end_date VARCHAR(20) NULL;
