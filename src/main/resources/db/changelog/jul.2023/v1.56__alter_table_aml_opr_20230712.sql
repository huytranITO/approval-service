--liquibase formatted sql
--changeset TuanHA13:v1.56__alter_table_aml_opr_20230712 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'aml_opr' AND COLUMN_NAME = 'start_date';
ALTER TABLE aml_opr MODIFY COLUMN start_date varchar(20) DEFAULT NULL;