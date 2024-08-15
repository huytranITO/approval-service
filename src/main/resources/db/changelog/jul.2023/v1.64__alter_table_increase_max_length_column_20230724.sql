--liquibase formatted sql
--changeset ManhNV8:v1.64__alter_table_increase_max_length_column_20230724-appraisal_content dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_appraisal_content' AND COLUMN_NAME = 'detail';
ALTER TABLE application_appraisal_content MODIFY COLUMN detail text NULL COMMENT 'Danh sách mã chi tiết. Cách nhau bằng dấu ","';

--liquibase formatted sql
--changeset ManhNV8:v1.64__alter_table_increase_max_length_column_20230724-individual_customer dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_customer' AND COLUMN_NAME = 'first_name';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_customer' AND COLUMN_NAME = 'last_name';
ALTER TABLE individual_customer MODIFY COLUMN first_name VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Tên';
ALTER TABLE individual_customer MODIFY COLUMN last_name VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Họ';

--liquibase formatted sql
--changeset ManhNV8:v1.64__alter_table_increase_max_length_column_20230724-salary_income dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income' AND COLUMN_NAME = 'income_owner_name';
ALTER TABLE `salary_income`
MODIFY COLUMN `income_owner_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Họ tên chủ nguồn thu' AFTER `income_owner_value`;

--liquibase formatted sql
--changeset ManhNV8:v1.64__alter_table_increase_max_length_column_20230724-rental_income dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'rental_income' AND COLUMN_NAME = 'income_owner_name';
ALTER TABLE `rental_income`
MODIFY COLUMN `income_owner_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Họ tên chủ nguồn thu' AFTER `income_owner_value`;

--liquibase formatted sql
--changeset ManhNV8:v1.64__alter_table_increase_max_length_column_20230724-individual_enterprise_income dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'rental_income' AND COLUMN_NAME = 'income_owner_name';
ALTER TABLE `individual_enterprise_income`
MODIFY COLUMN `income_owner_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Họ Tên chủ nguồn thu' AFTER `income_owner_value`;

--liquibase formatted sql
--changeset ManhNV8:v1.64__alter_table_increase_max_length_column_20230724-sub_credit_card dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'sub_credit_card' AND COLUMN_NAME = 'card_owner_name';
ALTER TABLE `sub_credit_card`
MODIFY COLUMN `card_owner_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên chủ thẻ' AFTER `application_credit_card_id`;

--liquibase formatted sql
--changeset ManhNV8:v1.64__alter_table_increase_max_length_column_20230724-other_income dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'other_income' AND COLUMN_NAME = 'income_owner_name';
ALTER TABLE other_income MODIFY COLUMN income_owner_name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Họ tên chủ nguồn thu';