--liquibase formatted sql
--changeset TuVM2:v1.42_alter_table_application_credit_loan_add_column_20230614.sql dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_loan' AND COLUMN_NAME = 'acf_no';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_loan' AND COLUMN_NAME = 'account_no';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_loan' AND COLUMN_NAME = 'status';
ALTER TABLE application_credit_loan ADD acf_no varchar(100) COMMENT 'Số acf';
ALTER TABLE application_credit_loan ADD account_no varchar(100) COMMENT 'Số tài khoản';
ALTER TABLE application_credit_loan ADD status varchar(50) COMMENT 'Trạng thái từ bpm vận hành';

