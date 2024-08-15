--liquibase formatted sql
--changeset ManhNV8:v1.19__alter_table_application_20230427 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'process_definition_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'process_business_key';
ALTER TABLE `application` ADD `process_definition_id` varchar(100) NULL;
ALTER TABLE `application` ADD `process_business_key` varchar(100) NULL;