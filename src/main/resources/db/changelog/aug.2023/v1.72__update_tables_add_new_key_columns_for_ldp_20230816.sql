--liquibase formatted sql
--changeset ManhNV8:v1.72__update_tables_add_new_key_columns_for_ldp_20230816 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer_identity' AND COLUMN_NAME = 'ldp_identity_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer_address' AND COLUMN_NAME = 'ldp_address_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit' AND COLUMN_NAME = 'ldp_credit_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_loan' AND COLUMN_NAME = 'ldp_loan_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_overdraft' AND COLUMN_NAME = 'ldp_overdraft_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card' AND COLUMN_NAME = 'ldp_card_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_income' AND COLUMN_NAME = 'ldp_income_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income' AND COLUMN_NAME = 'ldp_salary_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'rental_income' AND COLUMN_NAME = 'ldp_rental_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_enterprise_income' AND COLUMN_NAME = 'ldp_business_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'other_income' AND COLUMN_NAME = 'ldp_other_id';
ALTER TABLE customer_identity ADD ldp_identity_id varchar(36) NULL COMMENT 'ID thông tin định danh từ Landing Page';
ALTER TABLE customer_identity CHANGE ldp_identity_id ldp_identity_id varchar(36) NULL COMMENT 'ID thông tin định danh từ Landing Page' AFTER ref_identity_id;

ALTER TABLE customer_address ADD ldp_address_id varchar(36) NULL;
ALTER TABLE customer_address CHANGE ldp_address_id ldp_address_id varchar(36) NULL AFTER ref_address_id;

ALTER TABLE application_credit ADD ldp_credit_id varchar(36) NULL COMMENT 'ID thông tin khoản vay từ Landing Page';
ALTER TABLE application_credit CHANGE ldp_credit_id ldp_credit_id varchar(36) NULL COMMENT 'ID thông tin khoản vay từ Landing Page' AFTER id;

ALTER TABLE application_credit_loan ADD ldp_loan_id varchar(36) NULL COMMENT 'ID thông tin khoản vay từ Landing Page';
ALTER TABLE application_credit_loan CHANGE ldp_loan_id ldp_loan_id varchar(36) NULL COMMENT 'ID thông tin khoản vay từ Landing Page' AFTER id;

ALTER TABLE application_credit_overdraft ADD ldp_overdraft_id varchar(36) NULL COMMENT 'ID thông tin khoản vay từ Landing Page';
ALTER TABLE application_credit_overdraft CHANGE ldp_overdraft_id ldp_overdraft_id varchar(36) NULL COMMENT 'ID thông tin khoản vay từ Landing Page' AFTER id;

ALTER TABLE application_credit_card ADD ldp_card_id varchar(36) NULL COMMENT 'ID thông tin khoản vay từ Landing Page';
ALTER TABLE application_credit_card CHANGE ldp_card_id ldp_card_id varchar(36) NULL COMMENT 'ID thông tin khoản vay từ Landing Page' AFTER id;

ALTER TABLE application_income ADD ldp_income_id varchar(36) NULL COMMENT 'ID nguồn thu từ Landing Page';
ALTER TABLE application_income CHANGE ldp_income_id ldp_income_id varchar(36) NULL COMMENT 'ID nguồn thu từ Landing Page' AFTER id;

ALTER TABLE salary_income ADD ldp_salary_id varchar(36) NULL COMMENT 'ID nguồn thu lương từ Landing Page';
ALTER TABLE salary_income CHANGE ldp_salary_id ldp_salary_id varchar(36) NULL COMMENT 'ID nguồn thu lương từ Landing Page' AFTER id;

ALTER TABLE rental_income ADD ldp_rental_id varchar(36) NULL COMMENT 'ID nguồn thu cho thuê từ Landing Page';
ALTER TABLE rental_income CHANGE ldp_rental_id ldp_rental_id varchar(36) NULL COMMENT 'ID nguồn thu cho thuê từ Landing Page' AFTER id;

ALTER TABLE individual_enterprise_income ADD ldp_business_id varchar(36) NULL COMMENT 'ID nguồn thu cá nhân kinh doanh/doanh nghiệp từ Landing Page';
ALTER TABLE individual_enterprise_income CHANGE ldp_business_id ldp_business_id varchar(36) NULL COMMENT 'ID nguồn thu cá nhân kinh doanh/doanh nghiệp từ Landing Page' AFTER id;

ALTER TABLE other_income ADD ldp_other_id VARCHAR(36) NULL COMMENT 'ID nguồn thu khác từ Landing Page';
ALTER TABLE other_income CHANGE ldp_other_id ldp_other_id VARCHAR(36) NULL COMMENT 'ID nguồn thu khác từ Landing Page' AFTER id;
