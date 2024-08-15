--liquibase formatted sql
--changeset BAONV2:v1.87__alter_table_application_add_column_rmStatus_20230913 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'rm_status';
ALTER TABLE application ADD rm_status TINYINT(1) NULL DEFAULT 0 COMMENT 'Trạng thái RM tư vấn';