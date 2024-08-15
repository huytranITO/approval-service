--liquibase formatted sql
--changeset BaoNV2:v1.40_alter_table_application_credit_card_add_column_20220906 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card' AND COLUMN_NAME = 'contract_l';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card' AND COLUMN_NAME = 'issuing_contract';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card' AND COLUMN_NAME = 'contract_number';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card' AND COLUMN_NAME = 'created_date';
ALTER TABLE application_credit_card ADD contract_l varchar(100) COMMENT 'Mã HĐ mức Liability của KH';
ALTER TABLE application_credit_card ADD issuing_contract varchar(100) COMMENT 'Mã HĐ mức Issuing của KH';
ALTER TABLE application_credit_card ADD contract_number varchar(50) COMMENT 'Số thẻ';
ALTER TABLE application_credit_card ADD created_date DATE COMMENT 'Ngày phát hành';
