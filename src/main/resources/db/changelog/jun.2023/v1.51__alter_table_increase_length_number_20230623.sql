--liquibase formatted sql
--changeset ManhNV8:v1.51__alter_table_increase_length_number_20230623-application_credit dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit' AND COLUMN_NAME = 'loan_amount';
ALTER TABLE application_credit MODIFY COLUMN loan_amount decimal(20,0) NULL COMMENT 'Số tiền vay/Hạn mức cấp/Hạn mức thẻ';

--liquibase formatted sql
--changeset ManhNV8:v1.51__alter_table_increase_length_number_20230623-application_credit_loan dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_loan' AND COLUMN_NAME = 'total_capital';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_loan' AND COLUMN_NAME = 'equity_capital';
ALTER TABLE application_credit_loan MODIFY COLUMN total_capital decimal(20,0) NULL COMMENT 'Tổng nhu cầu vốn';
ALTER TABLE application_credit_loan MODIFY COLUMN equity_capital decimal(20,0) NULL COMMENT 'Vốn tự có';

--liquibase formatted sql
--changeset ManhNV8:v1.51__alter_table_increase_length_number_20230623-application_income dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_income' AND COLUMN_NAME = 'recognized_income';
ALTER TABLE application_income MODIFY COLUMN recognized_income decimal(20,0) NULL COMMENT 'Thu nhập ghi nhận (tháng)';

--liquibase formatted sql
--changeset ManhNV8:v1.51__alter_table_increase_length_number_20230623-application_limit_credit dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_limit_credit' AND COLUMN_NAME = 'loan_product_collateral';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_limit_credit' AND COLUMN_NAME = 'other_loan_product_collateral';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_limit_credit' AND COLUMN_NAME = 'unsecure_product';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_limit_credit' AND COLUMN_NAME = 'other_unsecure_product';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_limit_credit' AND COLUMN_NAME = 'total';
ALTER TABLE application_limit_credit MODIFY COLUMN loan_product_collateral decimal(20,0) NULL COMMENT 'Khoản cấp tín dụng có TSBĐ - SP vay mua sản phẩm tại dự án hợp tác chiến lược';
ALTER TABLE application_limit_credit MODIFY COLUMN other_loan_product_collateral decimal(20,0) NULL COMMENT 'Khoản cấp tín dụng có TSBĐ - SP khác SP vay mua sản phẩm tại dự án hợp tác chiến lược';
ALTER TABLE application_limit_credit MODIFY COLUMN unsecure_product decimal(20,0) NULL COMMENT 'Khoản cấp tín dụng không TSBĐ - SP vay mua sản phẩm tại dự án hợp tác chiến lược';
ALTER TABLE application_limit_credit MODIFY COLUMN other_unsecure_product decimal(20,0) NULL COMMENT 'Khoản cấp tín dụng không TSBĐ - SP khác SP vay mua sản phẩm tại dự án hợp tác chiến lược';
ALTER TABLE application_limit_credit MODIFY COLUMN total decimal(20,0) NULL COMMENT 'Tổng cộng';

--liquibase formatted sql
--changeset ManhNV8:v1.51__alter_table_increase_length_number_20230623-application_repayment dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_repayment' AND COLUMN_NAME = 'total_repay';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_repayment' AND COLUMN_NAME = 'mue';
ALTER TABLE application_repayment MODIFY COLUMN total_repay decimal(20,0) NULL COMMENT 'Tổng nghĩa vụ trả nợ';
ALTER TABLE application_repayment MODIFY COLUMN mue decimal(20,0) NULL COMMENT 'MUE';

--liquibase formatted sql
--changeset ManhNV8:v1.51__alter_table_increase_length_number_20230623-cic dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'cic' AND COLUMN_NAME = 'total_loan_collateral';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'cic' AND COLUMN_NAME = 'total_unsecure_loan';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'cic' AND COLUMN_NAME = 'total_credit_card_limit';
ALTER TABLE cic MODIFY COLUMN total_loan_collateral decimal(20,0) NULL COMMENT 'Tổng dư nợ có TSĐB (trđ)';
ALTER TABLE cic MODIFY COLUMN total_unsecure_loan decimal(20,0) NULL COMMENT 'Tổng dư nợ không có TSĐB (trđ)';
ALTER TABLE cic MODIFY COLUMN total_credit_card_limit decimal(20,0) NULL COMMENT 'Tổng HM TTD (trđ)';

--liquibase formatted sql
--changeset ManhNV8:v1.51__alter_table_increase_length_number_20230623-rental_income dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'rental_income' AND COLUMN_NAME = 'rental_price';
ALTER TABLE rental_income MODIFY COLUMN rental_price decimal(20,0) NULL COMMENT 'Giá thuê';
