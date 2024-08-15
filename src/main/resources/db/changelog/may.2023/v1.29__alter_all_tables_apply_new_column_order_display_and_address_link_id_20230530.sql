--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_customer_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer' AND COLUMN_NAME = 'order_display';
ALTER TABLE `customer`
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự hiển thị' AFTER `customer_type`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_customer_address_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer_address' AND COLUMN_NAME = 'address_link_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer_address' AND COLUMN_NAME = 'order_display';
ALTER TABLE `customer_address`
ADD COLUMN `address_link_id` varchar(36) NULL COMMENT 'UUID link địa chỉ khách hàng với thông tin thực địa' AFTER `address_line`,
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự hiển thị' AFTER `address_link_id`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_application_field_information_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_field_information' AND COLUMN_NAME = 'address_link_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_field_information' AND COLUMN_NAME = 'order_display';
ALTER TABLE `application_field_information`
ADD COLUMN `address_link_id` varchar(36) NULL COMMENT 'UUID link địa chỉ thực địa với địa chỉ khách hàng/nguồn thu' AFTER `result`,
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự hiển thị' AFTER `address_link_id`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_application_appraisal_content_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_appraisal_content' AND COLUMN_NAME = 'order_display';
ALTER TABLE `application_appraisal_content`
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự hiển thị' AFTER `authorization`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_application_credit_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit' AND COLUMN_NAME = 'order_display';
ALTER TABLE `application_credit`
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự hiển thị' AFTER `currency`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_application_credit_ratings_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_ratings' AND COLUMN_NAME = 'order_display';
ALTER TABLE `application_credit_ratings`
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự hiển thị' AFTER `rating_result`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_application_credit_ratings_dtl_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_ratings_dtl' AND COLUMN_NAME = 'order_display';
ALTER TABLE `application_credit_ratings_dtl`
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự hiển thị' AFTER `status`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_application_income_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_income' AND COLUMN_NAME = 'order_display';
ALTER TABLE `application_income`
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự hiển thị' AFTER `explanation`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_application_limit_credit_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_limit_credit' AND COLUMN_NAME = 'order_display';
ALTER TABLE `application_limit_credit`
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự hiển thị' AFTER `total`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_application_phone_expertise_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_phone_expertise' AND COLUMN_NAME = 'order_display';
ALTER TABLE `application_phone_expertise`
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự hiển thị' AFTER `ext`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_cic_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'cic' AND COLUMN_NAME = 'order_display';
ALTER TABLE `cic`
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự hiển thị' AFTER `query_at`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_customer_identity_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer_identity' AND COLUMN_NAME = 'order_display';
ALTER TABLE `customer_identity`
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự hiển thị' AFTER `issued_place`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_individual_enterprise_income_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_enterprise_income' AND COLUMN_NAME = 'address_link_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_enterprise_income' AND COLUMN_NAME = 'order_display';
ALTER TABLE `individual_enterprise_income`
ADD COLUMN `address_link_id` varchar(36) NULL COMMENT 'UUID link địa chỉ với thông tin thực địa' AFTER `evaluate_result`,
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự ưu tiên' AFTER `address_link_id`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_other_income_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'other_income' AND COLUMN_NAME = 'order_display';
ALTER TABLE `other_income`
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự hiển thị' AFTER `explanation`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_rental_income_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'rental_income' AND COLUMN_NAME = 'order_display';
ALTER TABLE `rental_income`
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự hiển thị' AFTER `explanation`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_salary_income_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income' AND COLUMN_NAME = 'address_link_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income' AND COLUMN_NAME = 'order_display';
ALTER TABLE `salary_income`
ADD COLUMN `address_link_id` varchar(36) NULL COMMENT 'UUID link địa chỉ nguồn thu với thông tin thực địa' AFTER `explanation`,
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự hiển thị' AFTER `address_link_id`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_sub_credit_card_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'sub_credit_card' AND COLUMN_NAME = 'order_display';
ALTER TABLE `sub_credit_card`
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự hiển thị' AFTER `card_limit_amount`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_aml_opr_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'aml_opr' AND COLUMN_NAME = 'order_display';
ALTER TABLE `aml_opr`
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự hiển thị' AFTER `reason_in_list`;

--liquibase formatted sql
--changeset ManhNV8:v1.29__alter_table_application_credit_conditions_add_new_column_20230530 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_conditions' AND COLUMN_NAME = 'order_display';
ALTER TABLE `application_credit_conditions`
ADD COLUMN `order_display` int(0) NULL COMMENT 'Thứ tự ưu tiên' AFTER `time_control_disburse`;