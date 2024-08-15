--liquibase formatted sql
--changeset ManhNV8:v1.10__alter_table_application_credit_20230419-001 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit ' AND COLUMN_NAME = 'approve_result_value';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit ' AND COLUMN_NAME = 'document_code';
ALTER TABLE application_credit ADD approve_result_value varchar(100) NULL;
ALTER TABLE application_credit MODIFY COLUMN document_code varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Mã văn bản';

--liquibase formatted sql
--changeset ManhNV8:v1.10__alter_table_application_credit_20230419-002 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'regulatory_code';
ALTER TABLE application MODIFY COLUMN regulatory_code text NULL COMMENT 'Mã quy định = nhiều mã văn bản, cách nhau bởi dấu ";"';
