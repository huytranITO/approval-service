--liquibase formatted sql
--changeset KhanhNQ16:v1.53__alter_table_application_add_new_columns_20230704 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'generator_status';
ALTER TABLE application ADD generator_status varchar(100) NULL;