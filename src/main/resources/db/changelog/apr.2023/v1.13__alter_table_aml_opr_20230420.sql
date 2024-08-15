--liquibase formatted sql
--changeset ManhNV8:v1.13__alter_table_aml_opr_20230420 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'aml_opr ' AND COLUMN_NAME = 'start_date';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'aml_opr ' AND COLUMN_NAME = 'end_date';
ALTER TABLE aml_opr ADD start_date DATE NULL;
ALTER TABLE aml_opr ADD end_date DATE NULL;
