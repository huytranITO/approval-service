--liquibase formatted sql
--changeset ManhNV8:v1.45__alter_table_application_draft_add_new_columns_20230613 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_draft' AND COLUMN_NAME = 'processing_role';
ALTER TABLE application_draft ADD processing_role varchar(20) NULL;
ALTER TABLE application_draft CHANGE processing_role processing_role varchar(20) NULL AFTER tab_code;