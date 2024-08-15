--liquibase formatted sql
--changeset ManhNV8:v1.66__alter_table_application_add_new_column_reception_at_20230725 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'reception_at';
ALTER TABLE application ADD reception_at DATETIME NULL;
ALTER TABLE application CHANGE reception_at reception_at DATETIME NULL AFTER branch_name;
