--liquibase formatted sql
--changeset KhanhNQ15:v1.51__alter_table_application_credit_conditions_add_column_credit_condition_id_20230621 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_conditions' AND COLUMN_NAME = 'credit_condition_id';
ALTER TABLE application_credit_conditions ADD credit_condition_id bigint NULL COMMENT 'Điều kiện tín dụng';
