--liquibase formatted sql
--changeset ManhNV8:v1.32__alter_table_application_add_new_column_20230603 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'previous_role';
ALTER TABLE application ADD previous_role varchar(20) NULL COMMENT 'Role xử lý trước đó';
ALTER TABLE application CHANGE previous_role previous_role varchar(20) NULL COMMENT 'Role xử lý trước đó' AFTER processing_role;