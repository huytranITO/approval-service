--liquibase formatted sql
--changeset ManhNV8:v1.57__alter_table_application_add_new_column_approval_document_no_20230712 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'approval_document_no';
ALTER TABLE application ADD approval_document_no varchar(100) NULL COMMENT 'Số biên bản phê duyêt';