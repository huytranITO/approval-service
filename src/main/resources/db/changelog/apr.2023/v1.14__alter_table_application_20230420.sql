--liquibase formatted sql
--changeset TuanHA13:v1.14__alter_table_application_20230420-001 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'area';
ALTER TABLE application ADD area varchar(255) DEFAULT NULL COMMENT 'Vùng'