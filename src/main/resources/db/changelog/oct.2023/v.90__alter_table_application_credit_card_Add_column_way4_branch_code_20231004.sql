--liquibase formatted sql
--changeset BAONV2:v.90__alter_table_application_credit_card_Add_column_way4_branch_code_20231004 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card' AND COLUMN_NAME = 'way4_branch_code';
ALTER TABLE application_credit_card ADD way4_branch_code varchar(50) NULL COMMENT 'Mã chi nhánh way4';