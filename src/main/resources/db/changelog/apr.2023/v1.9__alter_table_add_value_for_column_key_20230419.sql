--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419-application_credit dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit ' AND COLUMN_NAME = 'document_code';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit ' AND COLUMN_NAME = 'credit_type';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit ' AND COLUMN_NAME = 'approve_result';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit ' AND COLUMN_NAME = 'credit_type_value';
ALTER TABLE application_credit MODIFY COLUMN document_code varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Mã văn bản';
ALTER TABLE application_credit MODIFY COLUMN credit_type varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Khoản cấp tín dụng: LOAN_UNSECURE | OVERDRAFT_UNSECURE | CARD_UNSECURE';
ALTER TABLE application_credit MODIFY COLUMN approve_result varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Ý kiến phê duyệt: APPROVED | REJECTED';
ALTER TABLE application_credit ADD credit_type_value varchar(100) NULL;


--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419-application_credit_loan dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_loan ' AND COLUMN_NAME = 'product_code';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_loan ' AND COLUMN_NAME = 'loan_purpose_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_loan ' AND COLUMN_NAME = 'credit_form_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_loan ' AND COLUMN_NAME = 'disburse_frequency_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_loan ' AND COLUMN_NAME = 'debt_pay_method_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_loan ' AND COLUMN_NAME = 'disburse_method_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_loan ' AND COLUMN_NAME = 'principal_pay_unit_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_loan ' AND COLUMN_NAME = 'interest_pay_unit_value';
ALTER TABLE application_credit_loan MODIFY COLUMN product_code varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Mã sản phẩm';
ALTER TABLE application_credit_loan ADD loan_purpose_value varchar(100) NULL;
ALTER TABLE application_credit_loan ADD credit_form_value varchar(100) NULL;
ALTER TABLE application_credit_loan ADD disburse_frequency_value varchar(100) NULL;
ALTER TABLE application_credit_loan ADD debt_pay_method_value varchar(100) NULL;
ALTER TABLE application_credit_loan ADD disburse_method_value varchar(100) NULL;
ALTER TABLE application_credit_loan ADD principal_pay_unit_value varchar(100) NULL;
ALTER TABLE application_credit_loan ADD interest_pay_unit_value varchar(100) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419-alter_table_application_credit_overdraft dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_overdraft ' AND COLUMN_NAME = 'interest_rate_code';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_overdraft ' AND COLUMN_NAME = 'loan_purpose_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_overdraft ' AND COLUMN_NAME = 'debt_pay_method_value';
ALTER TABLE application_credit_overdraft MODIFY COLUMN interest_rate_code varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Mã lãi suất';
ALTER TABLE application_credit_overdraft ADD loan_purpose_value varchar(100) NULL;
ALTER TABLE application_credit_overdraft ADD debt_pay_method_value varchar(100) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419_application_credit_card dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card ' AND COLUMN_NAME = 'card_policy_code';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card ' AND COLUMN_NAME = 'card_type_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card ' AND COLUMN_NAME = 'auto_deduct_rate_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card ' AND COLUMN_NAME = 'card_form_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card ' AND COLUMN_NAME = 'card_receive_addr_value';
ALTER TABLE application_credit_card MODIFY COLUMN card_policy_code varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Mã chính sách thẻ';
ALTER TABLE application_credit_card ADD card_type_value varchar(100) NULL;
ALTER TABLE application_credit_card ADD auto_deduct_rate_value varchar(100) NULL;
ALTER TABLE application_credit_card ADD card_form_value varchar(100) NULL;
ALTER TABLE application_credit_card ADD card_receive_addr_value varchar(100) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419-salary_income dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income ' AND COLUMN_NAME = 'city_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income ' AND COLUMN_NAME = 'district_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income ' AND COLUMN_NAME = 'ward_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income ' AND COLUMN_NAME = 'rank_type_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income ' AND COLUMN_NAME = 'kpi_rating_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income ' AND COLUMN_NAME = 'pay_type_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income ' AND COLUMN_NAME = 'labor_type_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income ' AND COLUMN_NAME = 'income_owner_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income ' AND COLUMN_NAME = 'income_type';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income ' AND COLUMN_NAME = 'income_type_value';
ALTER TABLE salary_income ADD city_value varchar(50) NULL;
ALTER TABLE salary_income ADD district_value varchar(50) NULL;
ALTER TABLE salary_income ADD ward_value varchar(50) NULL;
ALTER TABLE salary_income ADD rank_type_value varchar(100) NULL;
ALTER TABLE salary_income ADD kpi_rating_value varchar(100) NULL;
ALTER TABLE salary_income ADD pay_type_value varchar(100) NULL;
ALTER TABLE salary_income ADD labor_type_value varchar(100) NULL;
ALTER TABLE salary_income ADD income_owner_value varchar(100) NULL;
ALTER TABLE salary_income ADD income_type varchar(10) NULL;
ALTER TABLE salary_income ADD income_type_value varchar(100) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419-rental_income dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'rental_income ' AND COLUMN_NAME = 'asset_type_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'rental_income ' AND COLUMN_NAME = 'rental_purpose_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'rental_income ' AND COLUMN_NAME = 'income_owner_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'rental_income ' AND COLUMN_NAME = 'income_type';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'rental_income ' AND COLUMN_NAME = 'income_type_value';
ALTER TABLE rental_income ADD asset_type_value varchar(100) NULL;
ALTER TABLE rental_income ADD rental_purpose_value varchar(100) NULL;
ALTER TABLE rental_income ADD income_owner_value varchar(100) NULL;
ALTER TABLE rental_income ADD income_type varchar(10) NULL;
ALTER TABLE rental_income ADD income_type_value varchar(100) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419-individual_enterprise_income dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_enterprise_income ' AND COLUMN_NAME = 'business_place_ownership_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_enterprise_income ' AND COLUMN_NAME = 'evaluation_period_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_enterprise_income ' AND COLUMN_NAME = 'income_owner_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_enterprise_income ' AND COLUMN_NAME = 'city_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_enterprise_income ' AND COLUMN_NAME = 'district_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_enterprise_income ' AND COLUMN_NAME = 'ward_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_enterprise_income ' AND COLUMN_NAME = 'income_type';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_enterprise_income ' AND COLUMN_NAME = 'income_type_value';
ALTER TABLE individual_enterprise_income ADD business_place_ownership_value varchar(100) NULL;
ALTER TABLE individual_enterprise_income ADD evaluation_period_value varchar(100) NULL;
ALTER TABLE individual_enterprise_income ADD income_owner_value varchar(100) NULL;
ALTER TABLE individual_enterprise_income ADD city_value varchar(50) NULL;
ALTER TABLE individual_enterprise_income ADD district_value varchar(50) NULL;
ALTER TABLE individual_enterprise_income ADD ward_value varchar(50) NULL;
ALTER TABLE individual_enterprise_income ADD income_type varchar(10) NULL;
ALTER TABLE individual_enterprise_income ADD income_type_value varchar(100) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419-other_income dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'other_income ' AND COLUMN_NAME = 'income_detail_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'other_income ' AND COLUMN_NAME = 'income_owner_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'other_income ' AND COLUMN_NAME = 'income_type';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'other_income ' AND COLUMN_NAME = 'income_type_value';
ALTER TABLE other_income ADD income_detail_value varchar(100) NULL;
ALTER TABLE other_income ADD income_owner_value varchar(100) NULL;
ALTER TABLE other_income ADD income_type varchar(10) NULL;
ALTER TABLE other_income ADD income_type_value varchar(100) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419-application dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'process_flow_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'submission_purpose_value';
ALTER TABLE application ADD process_flow_value varchar(100) NULL;
ALTER TABLE application ADD submission_purpose_value varchar(100) NULL;
-- ALTER TABLE application ADD segment_value varchar(100) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419-application_appraisal_content dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_appraisal_content ' AND COLUMN_NAME = 'criteria_group_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_appraisal_content ' AND COLUMN_NAME = 'detail_value';
ALTER TABLE application_appraisal_content ADD criteria_group_value varchar(100) NULL;
ALTER TABLE application_appraisal_content ADD detail_value text NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419-application_credit_conditions dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_conditions ' AND COLUMN_NAME = 'group_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_conditions ' AND COLUMN_NAME = 'code_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_conditions ' AND COLUMN_NAME = 'time_of_control_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_conditions ' AND COLUMN_NAME = 'applicable_subject_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_conditions ' AND COLUMN_NAME = 'control_unit_value';
ALTER TABLE application_credit_conditions ADD group_value varchar(100) NULL;
ALTER TABLE application_credit_conditions ADD code_value varchar(100) NULL;
ALTER TABLE application_credit_conditions ADD time_of_control_value varchar(100) NULL;
ALTER TABLE application_credit_conditions ADD applicable_subject_value varchar(100) NULL;
ALTER TABLE application_credit_conditions ADD control_unit_value varchar(100) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419-application_field_information dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_field_information ' AND COLUMN_NAME = 'place_type_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_field_information ' AND COLUMN_NAME = 'relationship_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_field_information ' AND COLUMN_NAME = 'city_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_field_information ' AND COLUMN_NAME = 'district_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_field_information ' AND COLUMN_NAME = 'ward_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_field_information ' AND COLUMN_NAME = 'result_value';
ALTER TABLE application_field_information ADD place_type_value varchar(100) NULL;
ALTER TABLE application_field_information ADD relationship_value varchar(100) NULL;
ALTER TABLE application_field_information ADD city_value varchar(50) NULL;
ALTER TABLE application_field_information ADD district_value varchar(50) NULL;
ALTER TABLE application_field_information ADD ward_value varchar(50) NULL;
ALTER TABLE application_field_information ADD result_value varchar(100) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419-application_income dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_income ' AND COLUMN_NAME = 'income_recognition_method_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_income ' AND COLUMN_NAME = 'conversion_method_value';
ALTER TABLE application_income ADD income_recognition_method_value varchar(100) NULL;
ALTER TABLE application_income ADD conversion_method_value varchar(100) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419-application_limit_credit dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_limit_credit ' AND COLUMN_NAME = 'loan_limit_value';
ALTER TABLE application_limit_credit ADD loan_limit_value varchar(100) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419-customer_address dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer_address ' AND COLUMN_NAME = 'address_type_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer_address ' AND COLUMN_NAME = 'city_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer_address ' AND COLUMN_NAME = 'district_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer_address ' AND COLUMN_NAME = 'ward_value';
ALTER TABLE customer_address ADD address_type_value varchar(100) NULL;
ALTER TABLE customer_address ADD city_value varchar(50) NULL;
ALTER TABLE customer_address ADD district_value varchar(50) NULL;
ALTER TABLE customer_address ADD ward_value varchar(50) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419-customer_identity dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer_identity ' AND COLUMN_NAME = 'document_type_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer_identity ' AND COLUMN_NAME = 'issued_by_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer_identity ' AND COLUMN_NAME = 'issued_place_value';
ALTER TABLE customer_identity ADD document_type_value varchar(50) NULL;
ALTER TABLE customer_identity ADD issued_by_value varchar(50) NULL;
ALTER TABLE customer_identity ADD issued_place_value varchar(50) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419-customer_relation_ship dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer_relation_ship ' AND COLUMN_NAME = 'relationship_value';
ALTER TABLE customer_relation_ship ADD relationship_value varchar(100) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.9__alter_table_add_value_for_column_key_20230419-individual_customer dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_customer ' AND COLUMN_NAME = 'gender_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_customer ' AND COLUMN_NAME = 'martial_status_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_customer ' AND COLUMN_NAME = 'nation_value';
ALTER TABLE individual_customer ADD gender_value varchar(30) NULL;
ALTER TABLE individual_customer ADD martial_status_value varchar(50) NULL;
ALTER TABLE individual_customer ADD nation_value varchar(50) NULL;