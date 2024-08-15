--liquibase formatted sql
--changeset BaoNV2:1.99__alter_table_application credit_modify_column_ldpCreditId_20231004 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit' AND COLUMN_NAME = 'ldp_credit_id';
ALTER TABLE application_credit MODIFY COLUMN ldp_credit_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'ID thông tin khoản vay từ Landing Page';
