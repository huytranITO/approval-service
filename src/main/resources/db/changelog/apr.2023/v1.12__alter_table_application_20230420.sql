--liquibase formatted sql
--changeset ManhNV8:v1.10__alter_table_application_credit_20230419 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'assignee_full_name';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'loan_approval_full_name';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'assignee_phone_number';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'loan_approval_phone_number';
ALTER TABLE `application` CHANGE assignee_full_name loan_approval_full_name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Họ tên người phê duyệt';
ALTER TABLE `application` CHANGE assignee_phone_number loan_approval_phone_number varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'SĐT người phê duyệt';
