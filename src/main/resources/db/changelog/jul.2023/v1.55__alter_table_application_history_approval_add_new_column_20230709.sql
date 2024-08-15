--liquibase formatted sql
--changeset ManhNV8:v1.55__alter_table_application_history_approval_add_new_column_20230709 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_history_approval' AND COLUMN_NAME = 'from_user_role';
ALTER TABLE application_history_approval ADD from_user_role varchar(20) NULL COMMENT 'Role gán hồ sơ';
ALTER TABLE application_history_approval CHANGE from_user_role from_user_role varchar(20) NULL COMMENT 'Role gán hồ sơ' AFTER username;
