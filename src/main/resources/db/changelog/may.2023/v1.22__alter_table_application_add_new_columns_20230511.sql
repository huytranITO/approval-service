--liquibase formatted sql
--changeset ManhNV8:v1.22__alter_table_application_add_new_columns_20230511 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'sub_process_instance_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'sub_process_definition_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'sub_process_key';
ALTER TABLE application ADD sub_process_instance_id varchar(36) NULL;
ALTER TABLE application ADD sub_process_definition_id varchar(100) NULL;
ALTER TABLE application ADD sub_process_key varchar(45) NULL;
