--liquibase formatted sql
--changeset ManhNV8:v1.63__alter_table_apply_new_columns_and_increase_max_length_20230721-application_history_approval dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_history_approval' AND COLUMN_NAME = 'note';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_history_approval' AND COLUMN_NAME = 'status';
ALTER TABLE application_history_approval ADD note varchar(1000) NULL COMMENT 'Ghi chú';
ALTER TABLE application_history_approval CHANGE note note varchar(1000) NULL COMMENT 'Ghi chú' AFTER reason;
ALTER TABLE application_history_approval ADD status varchar(20) NULL COMMENT 'Trạng thái hồ sơ';
ALTER TABLE application_history_approval CHANGE status status varchar(20) NULL COMMENT 'Trạng thái hồ sơ' AFTER note;