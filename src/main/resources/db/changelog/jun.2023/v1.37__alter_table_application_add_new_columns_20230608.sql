--liquibase formatted sql
--changeset ManhNV8:v1.37__alter_table_application_add_new_columns_20230608 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'business_unit_code';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'branch_code';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'branch_name';
ALTER TABLE application ADD business_unit_code varchar(20) NULL;
ALTER TABLE application CHANGE business_unit_code business_unit_code varchar(20) NULL AFTER risk_level;
ALTER TABLE application ADD branch_code varchar(20) NULL;
ALTER TABLE application CHANGE branch_code branch_code varchar(20) NULL AFTER region;
ALTER TABLE application ADD branch_name varchar(100) NULL;
ALTER TABLE application CHANGE branch_name branch_name varchar(100) NULL AFTER branch_code;