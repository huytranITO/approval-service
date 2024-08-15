--liquibase formatted sql
--changeset TRILM9:v1.88__alter_table_application_add_column_rmCommitStatus_20230926 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'rm_commit_status';
ALTER TABLE cic ADD error_code_sbv varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Thong tin loi cic khi goi sbv';