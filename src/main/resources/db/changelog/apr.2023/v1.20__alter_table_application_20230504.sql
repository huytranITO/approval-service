--liquibase formatted sql
--changeset ManhNV8:v1.20__alter_table_application_20230504_001 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'region_code';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'area_code';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'distribution_form_code';
ALTER TABLE `application` ADD `region_code` VARCHAR(10) NULL;
ALTER TABLE `application` ADD `area_code` VARCHAR(10) NULL;
ALTER TABLE `application` ADD `distribution_form_code` VARCHAR(10) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.20__alter_table_application_20230504_002 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'process_key';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'workflow_version';
ALTER TABLE `application` ADD `process_key` VARCHAR(45) NULL;
ALTER TABLE `application` ADD `workflow_version` INT NULL;