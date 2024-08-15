--liquibase formatted sql
--changeset ManhNV8:v1.30__alter_table_application_adding_new_column_20230531 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'give_back_role';
ALTER TABLE `application`
ADD COLUMN `give_back_role` varchar(20) NULL DEFAULT '' COMMENT 'Role trả hồ sơ về RM' AFTER `segment`;