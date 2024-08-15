--liquibase formatted sql
--changeset ManhNV8:v1.44__alter_table_application_credit_card_increase_max_size_columns_20230613 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card' AND COLUMN_NAME = 'card_type';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card' AND COLUMN_NAME = 'way4_card_code';
ALTER TABLE application_credit_card MODIFY COLUMN card_type varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Loại thẻ';
ALTER TABLE application_credit_card MODIFY COLUMN way4_card_code varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Mã Way4 thẻ';