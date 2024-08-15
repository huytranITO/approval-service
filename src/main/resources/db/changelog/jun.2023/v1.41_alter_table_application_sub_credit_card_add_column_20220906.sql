--liquibase formatted sql
--changeset BaoNV2:v1.41_alter_table_application_sub_credit_card_add_column_20220906 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'sub_credit_card' AND COLUMN_NAME = 'contract_number';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'sub_credit_card' AND COLUMN_NAME = 'created_date';
ALTER TABLE sub_credit_card ADD contract_number varchar(50) COMMENT 'Số thẻ';
ALTER TABLE sub_credit_card ADD created_date DATE COMMENT 'Ngày phát hành';
