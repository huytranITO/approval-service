--liquibase formatted sql
--changeset BAONV2:v_1.65_alter_table_application_add_column_20230725 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'sale_code';
ALTER TABLE application ADD sale_code varchar(1000) NULL COMMENT 'sale code';
