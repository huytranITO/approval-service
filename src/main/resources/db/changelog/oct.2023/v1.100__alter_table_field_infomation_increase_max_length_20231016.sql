--liquibase formatted sql
--changeset ManhNV:v1.100__alter_table_field_infomation_increase_max_length_20231016 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_field_information' AND COLUMN_NAME = 'address_link_id';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer_address' AND COLUMN_NAME = 'address_link_id';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income' AND COLUMN_NAME = 'address_link_id';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_enterprise_income' AND COLUMN_NAME = 'address_link_id';
ALTER TABLE application_field_information MODIFY COLUMN address_link_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'UUID link địa chỉ thực địa với địa chỉ khách hàng/nguồn thu';
ALTER TABLE customer_address MODIFY COLUMN address_link_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'UUID link địa chỉ khách hàng với thông tin thực địa';
ALTER TABLE salary_income MODIFY COLUMN address_link_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'UUID link địa chỉ nguồn thu với thông tin thực địa';
ALTER TABLE individual_enterprise_income MODIFY COLUMN address_link_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'UUID link địa chỉ với thông tin thực địa';
